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

import ru.mystamps.web.feature.collection.AddToCollectionDto;
import ru.mystamps.web.service.SeriesService;

/**
 * Implementation of the {@link MaxNumberOfStamps} validator.
 *
 * Retrieves quantity of stamps in a series by its id and compares it against requested number of
 * stamps. Marks the field {@code numberOfStamps} as having an error in case requested number of
 * stamps exceeds quantity of stamps in the series.
 */
@RequiredArgsConstructor
public class MaxNumberOfStampsValidator
	implements ConstraintValidator<MaxNumberOfStamps, AddToCollectionDto> {
	
	private final SeriesService seriesService;
	
	@Override
	public void initialize(MaxNumberOfStamps annotation) {
		// Intentionally empty: nothing to initialize
	}
	
	@Override
	public boolean isValid(AddToCollectionDto dto, ConstraintValidatorContext ctx) {
		
		if (dto == null) {
			return true;
		}
		
		Integer numberOfStamps = dto.getNumberOfStamps();
		if (numberOfStamps == null) {
			return true;
		}
		
		Integer seriesId = dto.getSeriesId();
		
		Integer quantity = seriesService.findQuantityById(seriesId);
		if (quantity != null && numberOfStamps <= quantity) {
			return true;
		}
		
		ConstraintViolationUtils.recreate(
			ctx,
			"numberOfStamps",
			ctx.getDefaultConstraintMessageTemplate()
		);
		
		return false;
	}
	
}

