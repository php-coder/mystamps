/*
 * Copyright (C) 2009-2014 Slava Semushin <slava.semushin@gmail.com>
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.groups.Default;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.Url;
import ru.mystamps.web.entity.User;
import ru.mystamps.web.model.AddSeriesForm;
import ru.mystamps.web.model.AddSeriesForm.ScottCatalogChecks;
import ru.mystamps.web.model.AddSeriesForm.ImageChecks;
import ru.mystamps.web.model.AddSeriesForm.GibbonsCatalogChecks;
import ru.mystamps.web.model.AddSeriesForm.MichelCatalogChecks;
import ru.mystamps.web.model.AddSeriesForm.YvertCatalogChecks;
import ru.mystamps.web.entity.Series;
import ru.mystamps.web.service.CategoryService;
import ru.mystamps.web.service.CountryService;
import ru.mystamps.web.service.SeriesService;
import ru.mystamps.web.service.dto.EntityInfoDto;
import ru.mystamps.web.support.spring.security.SecurityContextUtils;
import ru.mystamps.web.util.CatalogUtils;
import ru.mystamps.web.util.LocaleUtils;
import ru.mystamps.web.validation.ValidationRules;

@Controller
@RequiredArgsConstructor
public class SeriesController {
	
	private static final Integer SINCE_YEAR     = 1840;
	private static final Integer CURRENT_YEAR   = new GregorianCalendar().get(Calendar.YEAR);
	
	private static final Map<Integer, Integer> DAYS;
	private static final Map<Integer, Integer> MONTHS;
	private static final Map<Integer, Integer> YEARS;
	
	private final CategoryService categoryService;
	private final CountryService countryService;
	private final SeriesService seriesService;
	
	static {
		DAYS = new LinkedHashMap<>(ValidationRules.MAX_DAYS_IN_MONTH);
		for (int i = 1; i <= ValidationRules.MAX_DAYS_IN_MONTH; i++) {
			Integer day = Integer.valueOf(i);
			DAYS.put(day, day);
		}
		
		MONTHS = new LinkedHashMap<>(ValidationRules.MAX_MONTHS_IN_YEAR);
		for (int i = 1; i <= ValidationRules.MAX_MONTHS_IN_YEAR; i++) {
			Integer month = Integer.valueOf(i);
			MONTHS.put(month, month);
		}
		
		YEARS = new LinkedHashMap<>();
		for (Integer i = CURRENT_YEAR; i >= SINCE_YEAR; i--) {
			YEARS.put(i, i);
		}
	}
	
	@InitBinder("addSeriesForm")
	protected void initBinder(WebDataBinder binder) {
		StringTrimmerEditor editor = new StringTrimmerEditor(" ", true);
		binder.registerCustomEditor(String.class, "michelNumbers", editor);
		binder.registerCustomEditor(String.class, "scottNumbers", editor);
		binder.registerCustomEditor(String.class, "yvertNumbers", editor);
		binder.registerCustomEditor(String.class, "gibbonsNumbers", editor);
		binder.registerCustomEditor(String.class, "comment", new StringTrimmerEditor(true));
	}
	
	@ModelAttribute("days")
	public Map<Integer, Integer> getDays() {
		return DAYS;
	}
	
	@ModelAttribute("months")
	public Map<Integer, Integer> getMonths() {
		return MONTHS;
	}
	
	@ModelAttribute("years")
	public Map<Integer, Integer> getYears() {
		return YEARS;
	}
	
	@ModelAttribute("categories")
	public Iterable<EntityInfoDto> getCategories() {
		return categoryService.findAll();
	}
	
	@ModelAttribute("countries")
	public Iterable<EntityInfoDto> getCountries(Locale userLocale) {
		String lang = LocaleUtils.getLanguageOrNull(userLocale);
		return countryService.findAll(lang);
	}
	
	@RequestMapping(value = Url.ADD_SERIES_PAGE, method = RequestMethod.GET)
	public AddSeriesForm showForm() {
		
		AddSeriesForm addSeriesForm = new AddSeriesForm();
		addSeriesForm.setPerforated(true);
		
		return addSeriesForm;
	}
	
	@RequestMapping(value = Url.ADD_SERIES_PAGE, method = RequestMethod.POST)
	public String processInput(
		@Validated({
			Default.class, MichelCatalogChecks.class, ScottCatalogChecks.class,
			YvertCatalogChecks.class, GibbonsCatalogChecks.class, ImageChecks.class
		}) AddSeriesForm form,
		BindingResult result,
		HttpServletRequest request,
		User currentUser) {
		
		if (result.hasErrors()) {
			// don't try to re-display file upload field
			form.setImage(null);
			return null;
		}
		
		boolean userCanAddComments =
			SecurityContextUtils.hasAuthority(request, "ADD_COMMENTS_TO_SERIES");
		Series series = seriesService.add(form, currentUser, userCanAddComments);
		
		String dstUrl = UriComponentsBuilder.fromUriString(Url.INFO_SERIES_PAGE)
			.buildAndExpand(series.getId())
			.toString();
		
		return "redirect:" + dstUrl;
	}
	
	@RequestMapping(value = Url.INFO_SERIES_PAGE, method = RequestMethod.GET)
	public String showInfo(@PathVariable("id") Series series, Model model) {
		
		if (series == null) {
			throw new NotFoundException();
		}
		
		model.addAttribute("series", series);
		model.addAttribute("michelNumbers", CatalogUtils.toShortForm(series.getMichel()));
		model.addAttribute("scottNumbers", CatalogUtils.toShortForm(series.getScott()));
		model.addAttribute("yvertNumbers", CatalogUtils.toShortForm(series.getYvert()));
		model.addAttribute("gibbonsNumbers", CatalogUtils.toShortForm(series.getGibbons()));
		
		return "series/info";
	}
	
}

