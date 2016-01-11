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

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import ru.mystamps.web.service.dto.AddSeriesDto;

/**
 * @author Sergey Chechenev
 */
public class ReleaseDateIsNotInFutureValidator
		implements ConstraintValidator<ReleaseDateIsNotInFuture, AddSeriesDto> {
	
	@Override
	public void initialize(ReleaseDateIsNotInFuture annotation) {
		// Intentionally empty: nothing to initialize
	}
	
	@Override
	public boolean isValid(AddSeriesDto dto, ConstraintValidatorContext context) {
		if (dto == null) {
			return true;
		}
		
		if (yearInFuture(dto)) {
			rejectField(context, "year");
			return false;
		}
		
		if (yearAndMonthInFuture(dto)) {
			rejectField(context, "month");
			return false;
		}
		
		if (dateInFuture(dto)) {
			rejectField(context, "day");
			return false;
		}
		
		return true;
	}
	
	private static void rejectField(ConstraintValidatorContext context, String fieldName) {
		ConstraintViolationUtils.recreate(
			context,
			fieldName,
			context.getDefaultConstraintMessageTemplate());
	}
	
	private static boolean yearInFuture(AddSeriesDto dto) {
		if (dto.getYear() == null) {
			return false;
		}
		
		return Year.of(dto.getYear())
			.isAfter(Year.now());
	}
	
	private static boolean yearAndMonthInFuture(AddSeriesDto dto) {
		if (dto.getMonth() == null) {
			return false;
		}
		
		return YearMonth.of(dto.getYear(), dto.getMonth())
			.isAfter(YearMonth.now());
	}
	
	private static boolean dateInFuture(AddSeriesDto dto) {
		if (dto.getDay() == null) {
			return false;
		}
		
		return LocalDate.of(dto.getYear(), dto.getMonth(), dto.getDay())
			.isAfter(LocalDate.now());
	}
	
}
