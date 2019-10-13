/*
 * Copyright (C) 2009-2019 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.feature.country;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.mystamps.web.common.LinkEntityDto;
import ru.mystamps.web.common.LocaleUtils;
import ru.mystamps.web.feature.series.SeriesUrl;
import ru.mystamps.web.support.spring.mvc.ReplaceRepeatingSpacesEditor;
import ru.mystamps.web.support.spring.security.CurrentUser;

import javax.validation.Valid;
import java.util.List;
import java.util.Locale;

import static ru.mystamps.web.common.ControllerUtils.redirectTo;

@Controller
@RequiredArgsConstructor
public class CountryController {
	
	private final CountryService countryService;
	
	@InitBinder("addCountryForm")
	protected void initBinder(WebDataBinder binder) {
		// We can't use StringTrimmerEditor here because "only one single registered custom
		// editor per property path is supported".
		ReplaceRepeatingSpacesEditor editor = new ReplaceRepeatingSpacesEditor(true);
		binder.registerCustomEditor(String.class, "name", editor);
		binder.registerCustomEditor(String.class, "nameRu", editor);
	}
	
	@GetMapping(CountryUrl.ADD_COUNTRY_PAGE)
	public AddCountryForm showForm() {
		return new AddCountryForm();
	}
	
	@PostMapping(CountryUrl.ADD_COUNTRY_PAGE)
	public String processInput(
		@Valid AddCountryForm form,
		BindingResult result,
		@CurrentUser Integer currentUserId,
		RedirectAttributes redirectAttributes) {
		
		if (result.hasErrors()) {
			return null;
		}
		
		String slug = countryService.add(form, currentUserId);
		
		redirectAttributes.addFlashAttribute("justAddedCountry", true);
		
		return redirectTo(SeriesUrl.INFO_COUNTRY_PAGE, slug);
	}
	
	@GetMapping(CountryUrl.GET_COUNTRIES_PAGE)
	public String showCountries(Model model, Locale userLocale) {
		String lang = LocaleUtils.getLanguageOrNull(userLocale);
		List<LinkEntityDto> countries = countryService.findAllAsLinkEntities(lang);
		
		model.addAttribute("countries", countries);
		
		return "country/list";
	}
	
}

