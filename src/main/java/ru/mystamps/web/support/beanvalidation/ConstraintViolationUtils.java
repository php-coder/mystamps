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

import javax.validation.ConstraintValidatorContext;

public final class ConstraintViolationUtils {
	
	private ConstraintViolationUtils() {
	}
	
	/**
	 * Bind error {@code msgTemplate} to field named {@code fieldName}.
	 */
	public static void recreate(
		ConstraintValidatorContext ctx,
		String fieldName,
		String msgTemplate) {
		
		ctx.disableDefaultConstraintViolation();
		ctx.buildConstraintViolationWithTemplate(msgTemplate)
			.addPropertyNode(fieldName)
			.addConstraintViolation();
	}
	
	/**
	 * Bind error {@code msgTemplate} to whole form.
	 */
	public static void recreate(ConstraintValidatorContext ctx, String msgTemplate) {
		ctx.disableDefaultConstraintViolation();
		ctx.buildConstraintViolationWithTemplate(msgTemplate)
			.addConstraintViolation();
	}
	
}
