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

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.validation.BindingResult;

import ru.mystamps.web.Url;
import ru.mystamps.web.entity.Country;
import ru.mystamps.web.model.AddCountryForm;
import ru.mystamps.web.service.CountryService;

@Controller
public class CountryController {
	
	private final CountryService countryService;
	
	@Inject
	CountryController(final CountryService countryService) {
		this.countryService = countryService;
	}
	
	@InitBinder("addCountryForm")
	protected void initBinder(final WebDataBinder binder) {
		binder.registerCustomEditor(String.class, "name", new StringTrimmerEditor(false));
	}
	
	@RequestMapping(value = Url.ADD_COUNTRY_PAGE, method = RequestMethod.GET)
	public AddCountryForm showForm() {
		return new AddCountryForm();
	}
	
	@RequestMapping(value = Url.ADD_COUNTRY_PAGE, method = RequestMethod.POST)
	public String processInput(@Valid final AddCountryForm form, final BindingResult result) {
		
		if (result.hasErrors()) {
			return null;
		}
		
		final Country country = countryService.add(form.getName());
		
		return "redirect:" + Url.INFO_COUNTRY_PAGE.replace("{id}", country.getId().toString());
	}
	
	@RequestMapping(value = Url.INFO_COUNTRY_PAGE, method = RequestMethod.GET)
	public String showInfo(
		@PathVariable("id") final Integer id,
		final Model model,
		final HttpServletResponse response) throws IOException {
		
		final Country country = countryService.findById(id);
		if (country == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		
		model.addAttribute("country", country);
		return "country/info";
	}
	
}

