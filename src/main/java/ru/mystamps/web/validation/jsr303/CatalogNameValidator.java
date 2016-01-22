/*
 * Copyright (C) 2009-2016 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.validation.jsr303;

import ru.mystamps.web.model.SearchSeriesForm;
import ru.mystamps.web.service.dto.AddSeriesDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.Arrays;

/**
 * @author Sergey Chechenev
 */
public class CatalogNameValidator
	implements ConstraintValidator<CatalogName, String> {
	
	@Override
	public void initialize(CatalogName annotation) {
		// Intentionally empty: nothing to initialize
	}
	
	@Override
	public boolean isValid(String name, ConstraintValidatorContext context) {
		if (name == null) {
			return true;
		}
		
		return Arrays.<String>asList("michel", "scott", "yvert", "gibbons").contains(name.toLowerCase());
	}
	
}
