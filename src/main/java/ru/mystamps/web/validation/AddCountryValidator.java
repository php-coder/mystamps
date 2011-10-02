/*
 * Copyright (C) 2009-2011 Slava Semushin <slava.semushin@gmail.com>
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

package ru.mystamps.web.validation;

import javax.inject.Inject;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.ValidationUtils;

import ru.mystamps.web.model.AddCountryForm;
import ru.mystamps.web.service.CountryService;

import static ru.mystamps.web.validation.ValidationRules.COUNTRY_NAME_MAX_LENGTH;
import static ru.mystamps.web.validation.ValidationRules.COUNTRY_NAME_MIN_LENGTH;
import static ru.mystamps.web.validation.ValidationRules.COUNTRY_NAME_REGEXP1;
import static ru.mystamps.web.validation.ValidationRules.COUNTRY_NAME_REGEXP2;

@Component
public class AddCountryValidator implements Validator {
	
	private final CountryService countryService;
	
	@Inject
	AddCountryValidator(final CountryService countryService) {
		this.countryService = countryService;
	}
	
	
	@Override
	public boolean supports(final Class clazz) {
		return AddCountryForm.class.isAssignableFrom(clazz);
	}
	
	@Override
	public void validate(final Object target, final Errors errors) {
		final AddCountryForm form = (AddCountryForm)target;
		
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "country", "value.required");
		if (errors.hasFieldErrors("country")) {
			return;
		}
		
		final String name = form.getCountry();
		if (name.length() < COUNTRY_NAME_MIN_LENGTH.intValue()) {
			errors.rejectValue(
				"country",
				"value.too-short",
				new Object[]{COUNTRY_NAME_MIN_LENGTH},
				"XXX");
			return;
		}
		
		if (name.length() > COUNTRY_NAME_MAX_LENGTH.intValue()) {
			errors.rejectValue(
				"country",
				"value.too-long",
				new Object[]{COUNTRY_NAME_MAX_LENGTH},
				"XXX");
			return;
		}
		
		if (!name.matches(COUNTRY_NAME_REGEXP1)) {
			errors.rejectValue("country", "country-name.invalid");
			return;
		}
		
		if (!name.matches(COUNTRY_NAME_REGEXP2)) {
			errors.rejectValue("country", "country-name.hyphen");
			return;
		}
		
		try {
			if (countryService.findByName(name) != null) {
				errors.rejectValue("country", "country-name.exists");
			}
		} catch (final Exception ex) {
			errors.rejectValue("country", "error.internal");
		}
		
	}
	
}

