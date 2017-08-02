/*
 * Copyright (C) 2009-2017 Slava Semushin <slava.semushin@gmail.com>
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

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Benjamin Yue
 */
public class DenyValuesValidator
	implements ConstraintValidator<DenyValues, String> {
	
	private String[] forbiddenValues;
	
	@Override
	public void initialize(DenyValues annotation) {
		forbiddenValues = annotation.value();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext ctx) {
		if (value == null) {
			return true;
		}

		for (String forbiddenValue : forbiddenValues) {
			if (value.equals(forbiddenValue)) {
				return false;
			}
		}
		
		return true;
	}

}
