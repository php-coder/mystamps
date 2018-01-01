/*
 * Copyright (C) 2009-2018 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.support.beanvalidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.service.CountryService;
import ru.mystamps.web.support.beanvalidation.UniqueCountryName.Lang;

@RequiredArgsConstructor
public class UniqueCountryNameValidator implements ConstraintValidator<UniqueCountryName, String> {
	
	private final CountryService countryService;

	private Lang lang;
	
	@Override
	public void initialize(UniqueCountryName annotation) {
		lang = annotation.lang();
	}
	
	@Override
	public boolean isValid(String value, ConstraintValidatorContext ctx) {
		
		if (value == null) {
			return true;
		}
		
		if (lang == Lang.EN && countryService.countByName(value) > 0) {
			return false;
		
		} else if (lang == Lang.RU && countryService.countByNameRu(value) > 0) {
			return false;
		}
		
		return true;
	}
	
}
