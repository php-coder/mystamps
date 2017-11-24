/*
 * Copyright (C) 2009-2017 Slava Semushin <slava.semushin@gmail.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package ru.mystamps.web.controller;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.Url;
import ru.mystamps.web.controller.converter.annotation.CurrentUser;
import ru.mystamps.web.controller.dto.FirstLevelCategoryDto;
import ru.mystamps.web.controller.dto.ImportSeriesForm;
import ru.mystamps.web.controller.dto.RequestImportForm;
import ru.mystamps.web.controller.event.ImportRequestCreated;
import ru.mystamps.web.dao.dto.CategoryDto;
import ru.mystamps.web.dao.dto.ImportRequestDto;
import ru.mystamps.web.dao.dto.LinkEntityDto;
import ru.mystamps.web.dao.dto.ParsedDataDto;
import ru.mystamps.web.service.CategoryService;
import ru.mystamps.web.service.CountryService;
import ru.mystamps.web.service.SeriesImportService;
import ru.mystamps.web.service.SeriesService;
import ru.mystamps.web.support.thymeleaf.GroupByParent;
import ru.mystamps.web.util.LocaleUtils;

import static ru.mystamps.web.controller.ControllerUtils.redirectTo;

@Controller
@RequiredArgsConstructor
public class SeriesImportController {
	
	private final CategoryService categoryService;
	private final CountryService countryService;
	private final SeriesService seriesService;
	private final SeriesImportService seriesImportService;
	private final ApplicationEventPublisher eventPublisher;
	
	@InitBinder("requestImportForm")
	protected void initRequestImportForm(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, "url", new StringTrimmerEditor(true));
	}
	
	@GetMapping(Url.REQUEST_IMPORT_SERIES_PAGE)
	public void showRequestImportForm(Model model) {
		RequestImportForm requestImportForm = new RequestImportForm();
		model.addAttribute("requestImportForm", requestImportForm);
	}
	
	@PostMapping(Url.REQUEST_IMPORT_SERIES_PAGE)
	public String processRequestImportForm(
		@Valid RequestImportForm form,
		BindingResult result,
		@CurrentUser Integer currentUserId) {
		
		if (result.hasErrors()) {
			return null;
		}
		
		Integer requestId = seriesImportService.add(form, currentUserId);
		
		ImportRequestCreated requestCreated =
			new ImportRequestCreated(this, requestId, form.getUrl());
		eventPublisher.publishEvent(requestCreated);
		
		return redirectTo(Url.REQUEST_IMPORT_PAGE, requestId);
	}
	
	@GetMapping(Url.REQUEST_IMPORT_PAGE)
	public String showRequestAndImportSeriesForm(
		@PathVariable("id") Integer requestId,
		Model model,
		Locale userLocale,
		HttpServletResponse response)
		throws IOException {
		
		if (requestId == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		
		ImportRequestDto request = seriesImportService.findById(requestId);
		if (request == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		
		model.addAttribute("request", request);

		String lang = LocaleUtils.getLanguageOrNull(userLocale);
		ParsedDataDto parsedData = seriesImportService.getParsedData(requestId, lang);
		
		ImportSeriesForm form = new ImportSeriesForm();
		form.setPerforated(Boolean.TRUE);
		
		boolean hasParsedData = parsedData != null;
		if (hasParsedData) {
			if (parsedData.getCategory() != null) {
				form.setCategory(parsedData.getCategory());
			}
			if (parsedData.getCountry() != null) {
				form.setCountry(parsedData.getCountry());
			}
			if (parsedData.getImageUrl() != null) {
				form.setImageUrl(parsedData.getImageUrl());
			}
			if (parsedData.getIssueYear() != null) {
				form.setYear(parsedData.getIssueYear());
			}
		}
		
		model.addAttribute("importSeriesForm", form);
		model.addAttribute("showForm", hasParsedData);
		
		// @todo #709 SeriesImportController.showRequestAndImportSeriesForm():
		//  extract a method for adding shared attributes to the model
		List<CategoryDto> categories =
			categoryService.findCategoriesWithParents(lang);
		List<FirstLevelCategoryDto> groupedCategories =
			GroupByParent.transformCategories(categories);
		model.addAttribute("categories", groupedCategories);
		
		List<LinkEntityDto> countries = countryService.findAllAsLinkEntities(lang);
		model.addAttribute("countries", countries);
		
		model.addAttribute("years", SeriesController.YEARS);
		
		return "series/import/info";
	}
	
	@PostMapping(Url.REQUEST_IMPORT_PAGE)
	public String processImportSeriesForm(
		@PathVariable("id") Integer requestId,
		Model model,
		@Valid ImportSeriesForm form,
		BindingResult result,
		@CurrentUser Integer currentUserId,
		Locale userLocale,
		HttpServletResponse response)
		throws IOException {
		
		if (requestId == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		
		ImportRequestDto request = seriesImportService.findById(requestId);
		if (request == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		
		// @todo #709 SeriesImportController.processImportSeriesForm():
		//  handle errors from interceptor
		
		model.addAttribute("request", request);
		
		String lang = LocaleUtils.getLanguageOrNull(userLocale);
		
		ParsedDataDto parsedData = seriesImportService.getParsedData(requestId, lang);
		boolean hasParsedData = parsedData != null;
		model.addAttribute("showForm", hasParsedData);
		
		List<CategoryDto> categories =
			categoryService.findCategoriesWithParents(lang);
		List<FirstLevelCategoryDto> groupedCategories =
			GroupByParent.transformCategories(categories);
		model.addAttribute("categories", groupedCategories);
		
		List<LinkEntityDto> countries = countryService.findAllAsLinkEntities(lang);
		model.addAttribute("countries", countries);
		
		model.addAttribute("years", SeriesController.YEARS);

		if (result.hasErrors()) {
			return "series/import/info";
		}
		
		Integer seriesId = seriesService.add(form, currentUserId, false);
		
		return redirectTo(Url.INFO_SERIES_PAGE, seriesId);
	}
	
}

