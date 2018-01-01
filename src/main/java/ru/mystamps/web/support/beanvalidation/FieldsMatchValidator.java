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

import java.util.Objects;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.PropertyAccessor;

public class FieldsMatchValidator implements ConstraintValidator<FieldsMatch, Object> {
	
	private String firstFieldName;
	private String secondFieldName;
	
	@Override
	public void initialize(FieldsMatch annotation) {
		firstFieldName  = annotation.first();
		secondFieldName = annotation.second();
	}
	
	@Override
	public boolean isValid(Object value, ConstraintValidatorContext ctx) {
		
		if (value == null) {
			return true;
		}
		
		PropertyAccessor bean = new BeanWrapperImpl(value);
		
		Object firstField      = bean.getPropertyValue(firstFieldName);
		String firstFieldValue = Objects.toString(firstField, "");
		if (StringUtils.isEmpty(firstFieldValue)) {
			return true;
		}
		
		Object secondField      = bean.getPropertyValue(secondFieldName);
		String secondFieldValue = Objects.toString(secondField, "");
		if (StringUtils.isEmpty(secondFieldValue)) {
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
		
		return true;
	}
	
}

