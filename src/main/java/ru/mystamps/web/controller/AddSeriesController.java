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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

package ru.mystamps.web.controller;

import javax.inject.Inject;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.mystamps.web.model.AddSeriesForm;
import ru.mystamps.web.entity.Country;
import ru.mystamps.web.service.CountryService;

import static ru.mystamps.web.SiteMap.ADD_SERIES_PAGE_URL;

@Controller
@RequestMapping(ADD_SERIES_PAGE_URL)
public class AddSeriesController {
	
	private static final Integer DAYS_IN_MONTH  = 31;
	private static final Integer MONTHS_IN_YEAR = 12;
	private static final Integer SINCE_YEAR     = 1840;
	private static final Integer CURRENT_YEAR   = new GregorianCalendar().get(Calendar.YEAR);
	
	private static final Map<Integer, Integer> DAYS;
	private static final Map<Integer, Integer> MONTHS;
	private static final Map<Integer, Integer> YEARS;
	
	private final CountryService countryService;
	
	static {
		
		DAYS = new LinkedHashMap<Integer, Integer>();
		for (Integer i = 1; i <= DAYS_IN_MONTH; i++) {
			DAYS.put(i, i);
		}
		
		MONTHS = new LinkedHashMap<Integer, Integer>();
		for (Integer i = 1; i <= MONTHS_IN_YEAR; i++) {
			MONTHS.put(i, i);
		}
		
		YEARS = new LinkedHashMap<Integer, Integer>();
		for (Integer i = CURRENT_YEAR; i >= SINCE_YEAR; i--) {
			YEARS.put(i, i);
		}
	}
	
	@Inject
	AddSeriesController(final CountryService countryService) {
		this.countryService = countryService;
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public void showForm(final Model model) {
		model.addAttribute("days", DAYS);
		model.addAttribute("months", MONTHS);
		model.addAttribute("years", YEARS);
		
		final Map<Integer, String> countries = new LinkedHashMap<Integer, String>();
		for (final Country country : countryService.findAll()) {
			countries.put(country.getId(), country.getName());
		}
		model.addAttribute("countries", countries);
		
		model.addAttribute(new AddSeriesForm());
	}
	
}

