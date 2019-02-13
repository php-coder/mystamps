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
package ru.mystamps.web.support.beanvalidation;

import ru.mystamps.web.validation.ValidationRules;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class CatalogNumbersValidator implements ConstraintValidator<CatalogNumbers, String> {
	
	private static final Pattern CATALOG_NUMBERS =
		Pattern.compile(ValidationRules.CATALOG_NUMBERS_REGEXP);
	
	private static final Pattern CATALOG_NUMBERS_WITH_LETTERS =
		Pattern.compile(ValidationRules.CATALOG_NUMBERS_AND_LETTERS_REGEXP);
	
	private boolean allowLetters;
	
	@Override
	public void initialize(CatalogNumbers catalogNumbers) {
		allowLetters = catalogNumbers.allowLetters();
	}
	
	@Override
	public boolean isValid(String catalogNumbers, ConstraintValidatorContext ctx) {
		if (catalogNumbers == null) {
			return true;
		}
		
		if (!allowLetters) {
			return CATALOG_NUMBERS.matcher(catalogNumbers).matches();
		}
		
		boolean matches = CATALOG_NUMBERS_WITH_LETTERS.matcher(catalogNumbers).matches();
		if (matches) {
			return true;
		}
		
		ConstraintViolationUtils.recreate(
			ctx,
			"{ru.mystamps.web.support.beanvalidation.CatalogNumbers.Alnum.message}"
		);
		
		return false;
	}
	
}
