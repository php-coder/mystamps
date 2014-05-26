/*
 * Copyright (C) 2009-2014 Slava Semushin <slava.semushin@gmail.com>
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

import java.lang.reflect.InvocationTargetException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;

public class FieldsMismatchValidator implements ConstraintValidator<FieldsMismatch, Object> {
	
	private String firstFieldName;
	private String secondFieldName;
	
	@Override
	public void initialize(FieldsMismatch annotation) {
		firstFieldName  = annotation.first();
		secondFieldName = annotation.second();
	}
	
	@Override
	public boolean isValid(Object value, ConstraintValidatorContext ctx) {
		
		if (value == null) {
			return true;
		}
		
		try {
			String firstFieldValue  = BeanUtils.getProperty(value, firstFieldName);
			if (StringUtils.isEmpty(firstFieldValue)) {
				return true;
			}
			
			String secondFieldValue = BeanUtils.getProperty(value, secondFieldName);
			if (StringUtils.isEmpty(secondFieldValue)) {
				return true;
			}
			
			// TODO: check fields only when both fields are equals
			
			if (firstFieldValue.equals(secondFieldValue)) {
				// bind error message to 2nd field
				ConstraintViolationUtils.recreate(
					ctx,
					secondFieldName,
					ctx.getDefaultConstraintMessageTemplate()
				);
				return false;
			}
		
		} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
			throw new RuntimeException(ex); // NOPMD
		}
		
		return true;
	}
	
}

