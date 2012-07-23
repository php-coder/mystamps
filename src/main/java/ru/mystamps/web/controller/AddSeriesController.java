/*
 * Copyright (C) 2009-2012 Slava Semushin <slava.semushin@gmail.com>
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

import javax.inject.Inject;
import javax.validation.Valid;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.WebDataBinder;

import ru.mystamps.web.Url;
import ru.mystamps.web.model.AddSeriesForm;
import ru.mystamps.web.entity.Country;
import ru.mystamps.web.entity.Series;
import ru.mystamps.web.service.CountryService;
import ru.mystamps.web.service.SeriesService;


@Controller
@RequestMapping(Url.ADD_SERIES_PAGE)
public class AddSeriesController {
	
	private static final Integer SINCE_YEAR     = 1840;
	private static final Integer CURRENT_YEAR   = new GregorianCalendar().get(Calendar.YEAR);
	
	private static final Map<Integer, Integer> YEARS;
	
	private final CountryService countryService;
	private final SeriesService seriesService;
	
	static {
		YEARS = new LinkedHashMap<Integer, Integer>();
		for (Integer i = CURRENT_YEAR; i >= SINCE_YEAR; i--) {
			YEARS.put(i, i);
		}
	}
	
	@Inject
	AddSeriesController(final CountryService countryService, final SeriesService seriesService) {
		this.countryService = countryService;
		this.seriesService = seriesService;
	}
	
	@InitBinder
	protected void initBinder(final WebDataBinder binder) {
		final StringTrimmerEditor editor = new StringTrimmerEditor(" ", true);
		binder.registerCustomEditor(String.class, "michelNumbers", editor);
		binder.registerCustomEditor(String.class, "scottNumbers", editor);
		binder.registerCustomEditor(String.class, "yvertNumbers", editor);
		binder.registerCustomEditor(String.class, "gibbonsNumbers", editor);
		binder.registerCustomEditor(String.class, "comment", new StringTrimmerEditor(true));
	}
	
	@ModelAttribute("years")
	public Map<Integer, Integer> getYears() {
		return YEARS;
	}
	
	@ModelAttribute("countries")
	public Map<Integer, String> getCountries() {
		final Map<Integer, String> countries = new LinkedHashMap<Integer, String>();
		
		for (final Country country : countryService.findAll()) {
			countries.put(country.getId(), country.getName());
		}
		
		return countries;
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public AddSeriesForm showForm() {
		
		final AddSeriesForm addSeriesForm = new AddSeriesForm();
		addSeriesForm.setPerforated(true);
		
		return addSeriesForm;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String processInput(@Valid final AddSeriesForm form, final BindingResult result) {
		
		if (result.hasErrors()) {
			return null;
		}
		
		final Series series = seriesService.add(form);
		
		return "redirect:" + Url.INFO_SERIES_PAGE.replace("{id}", series.getId().toString());
	}
	
}

