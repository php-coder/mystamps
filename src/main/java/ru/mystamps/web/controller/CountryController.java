/*
 * Copyright (C) 2009-2016 Slava Semushin <slava.semushin@gmail.com>
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.Url;
import ru.mystamps.web.controller.converter.annotation.Country;
import ru.mystamps.web.controller.converter.annotation.CurrentUser;
import ru.mystamps.web.dao.dto.LinkEntityDto;
import ru.mystamps.web.dao.dto.SeriesInfoDto;
import ru.mystamps.web.dao.dto.UrlEntityDto;
import ru.mystamps.web.model.AddCountryForm;
import ru.mystamps.web.service.CountryService;
import ru.mystamps.web.service.SeriesService;
import ru.mystamps.web.util.LocaleUtils;

@Controller
@RequiredArgsConstructor
public class CountryController {
	
	private final CountryService countryService;
	private final SeriesService seriesService;
	
	@InitBinder("addCountryForm")
	protected void initBinder(WebDataBinder binder) {
		StringTrimmerEditor editor = new StringTrimmerEditor(false);
		binder.registerCustomEditor(String.class, "name", editor);
		binder.registerCustomEditor(String.class, "nameRu", editor);
	}
	
	@RequestMapping(Url.ADD_COUNTRY_PAGE)
	public AddCountryForm showForm() {
		return new AddCountryForm();
	}
	
	@RequestMapping(value = Url.ADD_COUNTRY_PAGE, method = RequestMethod.POST)
	public String processInput(
		@Valid AddCountryForm form,
		BindingResult result,
		@CurrentUser Integer currentUserId,
		RedirectAttributes redirectAttributes) {
		
		if (result.hasErrors()) {
			return null;
		}
		
		UrlEntityDto countryUrl = countryService.add(form, currentUserId);
		
		String dstUrl = UriComponentsBuilder.fromUriString(Url.INFO_COUNTRY_PAGE)
			.buildAndExpand(countryUrl.getSlug())
			.toString();
		
		redirectAttributes.addFlashAttribute("justAddedCountry", true);
		
		return "redirect:" + dstUrl;
	}
	
	@RequestMapping(Url.INFO_COUNTRY_PAGE)
	public String showInfoBySlug(
		@Country @PathVariable("slug") LinkEntityDto country,
		Model model,
		Locale userLocale,
		HttpServletResponse response)
		throws IOException {
		
		if (country == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		
		String slug = country.getSlug();
		String name = country.getName();
		
		String lang = LocaleUtils.getLanguageOrNull(userLocale);
		List<SeriesInfoDto> series = seriesService.findByCountrySlug(slug, lang);
		
		model.addAttribute("countrySlug", slug);
		model.addAttribute("countryName", name);
		model.addAttribute("seriesOfCountry", series);
		
		return "country/info";
	}
	
	/**
	 * @author Aleksander Parkhomenko
	 */
	@RequestMapping(Url.INFO_COUNTRY_BY_ID_PAGE)
	public View showInfoById(
		@Country @PathVariable("slug") LinkEntityDto country,
		HttpServletResponse response)
		throws IOException {
		
		if (country == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		
		RedirectView view = new RedirectView();
		view.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
		view.setUrl(Url.INFO_COUNTRY_PAGE);
		
		return view;
	}
	
	@RequestMapping(Url.LIST_COUNTRIES_PAGE)
	public void list(Model model, Locale userLocale) {
		String lang = LocaleUtils.getLanguageOrNull(userLocale);
		List<LinkEntityDto> countries = countryService.findAllAsLinkEntities(lang);
		
		model.addAttribute("countries", countries);
	}
	
}

