/*
 * Copyright (C) 2009-2025 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.feature.category;

import lombok.RequiredArgsConstructor;
import ru.mystamps.web.feature.category.UniqueCategoryName.Lang;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
public class UniqueCategoryNameValidator
	implements ConstraintValidator<UniqueCategoryName, String> {
	
	private final CategoryService categoryService;
	
	private Lang lang;
	
	@Override
	public void initialize(UniqueCategoryName annotation) {
		lang = annotation.lang();
	}
	
	@Override
	public boolean isValid(String value, ConstraintValidatorContext ctx) {
		
		if (value == null) {
			return true;
		}
		
		if (lang == Lang.EN && categoryService.countByName(value) > 0) {
			return false;
		}
		
		if (lang == Lang.RU && categoryService.countByNameRu(value) > 0) {
			return false;
		}
		
		return true;
	}
	
}
