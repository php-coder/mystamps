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
import javax.validation.Valid;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.validation.BindingResult;

import ru.mystamps.web.model.AddCountryForm;
import ru.mystamps.web.service.CountryService;
import ru.mystamps.web.validation.AddCountryValidator;

import static ru.mystamps.web.SiteMap.ADD_COUNTRY_PAGE_URL;
import static ru.mystamps.web.SiteMap.INFO_COUNTRY_PAGE_URL;

@Controller
@RequestMapping(ADD_COUNTRY_PAGE_URL)
public class AddCountryController {
	
	private final AddCountryValidator addCountryValidator;
	private final CountryService countryService;
	
	@Inject
	AddCountryController(
			final AddCountryValidator addCountryValidator,
			final CountryService countryService) {
		this.addCountryValidator = addCountryValidator;
		this.countryService = countryService;
	}
	
	@InitBinder
	protected void initBinder(final WebDataBinder binder) {
		binder.setValidator(addCountryValidator);
		binder.registerCustomEditor(String.class, "country", new StringTrimmerEditor(false));
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public AddCountryForm showForm() {
		return new AddCountryForm();
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String processInput(
			@Valid final AddCountryForm form,
			final BindingResult result) {
		
		if (result.hasErrors()) {
			return null;
		}
		
		final Integer countryId = countryService.add(form.getCountry());
		
		return "redirect:" + INFO_COUNTRY_PAGE_URL.replace("{id}", countryId.toString());
	}
	
}

