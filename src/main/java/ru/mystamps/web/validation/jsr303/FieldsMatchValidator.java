/*
 * Copyright (C) 2012 Slava Semushin <slava.semushin@gmail.com>
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

import static com.google.common.base.Strings.isNullOrEmpty;

import java.lang.reflect.InvocationTargetException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.beanutils.BeanUtils;

public class FieldsMatchValidator implements ConstraintValidator<FieldsMatch, Object> {
	
	private String firstFieldName;
	private String secondFieldName;
	
	@Override
	public void initialize(final FieldsMatch annotation) {
		firstFieldName  = annotation.first();
		secondFieldName = annotation.second();
	}
	
	@Override
	public boolean isValid(final Object value, final ConstraintValidatorContext ctx) {
		
		if (value == null) {
			return true;
		}
		
		try {
			final String firstFieldValue  = BeanUtils.getProperty(value, firstFieldName);
			final String secondFieldValue = BeanUtils.getProperty(value, secondFieldName);
			
			if (isNullOrEmpty(firstFieldValue)) {
				return true;
			}
			
			if (isNullOrEmpty(secondFieldValue)) {
				return true;
			}
			
			// TODO: check fields only when both fields are equals
			
			if (!firstFieldValue.equals(secondFieldValue)) {
				// bind error message to 2nd field
				ConstraintViolationUtils.recreate(
					ctx,
					secondFieldName,
					ctx.getDefaultConstraintMessageTemplate()
				);
				return false;
			}
		
		} catch (final NoSuchMethodException ex) {
			ConstraintViolationUtils.recreate(ctx, secondFieldName, "error.internal");
			return false;
		
		} catch (final InvocationTargetException ex) {
			ConstraintViolationUtils.recreate(ctx, secondFieldName, "error.internal");
			return false;
		
		} catch (final IllegalAccessException ex) {
			ConstraintViolationUtils.recreate(ctx, secondFieldName, "error.internal");
			return false;
		}
		
		return true;
	}
	
}

