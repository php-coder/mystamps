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
package ru.mystamps.web.validation.jsr303;

import java.util.Set;

import javax.inject.Inject;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import ru.mystamps.web.entity.GibbonsCatalog;
import ru.mystamps.web.service.SeriesService;
import ru.mystamps.web.util.CatalogUtils;

public class UniqueGibbonsNumbersValidator
	implements ConstraintValidator<UniqueGibbonsNumbers, String> {
	
	@Inject
	private SeriesService seriesService;
	
	@Override
	public void initialize(UniqueGibbonsNumbers annotation) {
		// Intentionally empty: nothing to initialize
	}
	
	@Override
	public boolean isValid(String value, ConstraintValidatorContext ctx) {
		
		if (value == null) {
			return true;
		}
		
		Set<GibbonsCatalog> gibbonsNumbers = CatalogUtils.fromString(value, GibbonsCatalog.class);
		for (GibbonsCatalog gibbonsNumber : gibbonsNumbers) {
			boolean numberExists = seriesService.countByGibbonsNumber(gibbonsNumber.getCode()) > 0;
			if (numberExists) {
				return false;
			}
		}
		
		return true;
	}
	
}
