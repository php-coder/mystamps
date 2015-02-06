/*
 * Copyright (C) 2009-2015 Slava Semushin <slava.semushin@gmail.com>
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

import java.util.Locale;

import javax.validation.Valid;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.validation.BindingResult;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.Url;
import ru.mystamps.web.entity.Country;
import ru.mystamps.web.entity.User;
import ru.mystamps.web.model.AddCountryForm;
import ru.mystamps.web.service.CountryService;
import ru.mystamps.web.service.SeriesService;
import ru.mystamps.web.service.dto.UrlEntityDto;
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
	public String processInput(@Valid AddCountryForm form, BindingResult result, User currentUser) {
		
		if (result.hasErrors()) {
			return null;
		}
		
		UrlEntityDto countryUrl = countryService.add(form, currentUser);
		
		String dstUrl = UriComponentsBuilder.fromUriString(Url.INFO_COUNTRY_PAGE)
			.buildAndExpand(countryUrl.getId(), countryUrl.getSlug())
			.toString();
		
		return "redirect:" + dstUrl;
	}
	
	@RequestMapping(Url.INFO_COUNTRY_PAGE)
	public String showInfo(@PathVariable("id") Country country, Model model, Locale userLocale) {
		
		if (country == null) {
			throw new NotFoundException();
		}
		
		model.addAttribute("country", country);
		
		String lang = LocaleUtils.getLanguageOrNull(userLocale);
		model.addAttribute("seriesOfCountry", seriesService.findBy(country, lang));
		
		return "country/info";
	}
	
}

