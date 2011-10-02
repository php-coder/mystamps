/*
 * Copyright (C) 2011 Slava Semushin <slava.semushin@gmail.com>
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

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static ru.mystamps.web.SiteMap.INFO_COUNTRY_PAGE_URL;

import ru.mystamps.web.service.CountryService;

@Controller
@RequestMapping(INFO_COUNTRY_PAGE_URL)
public class InfoCountryController {
	
	private final CountryService countryService;
	
	@Inject
	InfoCountryController(final CountryService countryService) {
		this.countryService = countryService;
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public String showInfo(@PathVariable("id") final Integer id, final Model model) {
		model.addAttribute("country", countryService.findById(id));
		return "country/info";
	}
	
}

