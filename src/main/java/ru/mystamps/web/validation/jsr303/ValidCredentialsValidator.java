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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

package ru.mystamps.web.validation.jsr303;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.lang.reflect.InvocationTargetException;

import javax.inject.Inject;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.beanutils.BeanUtils;

import ru.mystamps.web.service.UserService;

public class ValidCredentialsValidator implements ConstraintValidator<ValidCredentials, Object> {
	
	@Inject
	private UserService userService;
	
	@Override
	public void initialize(final ValidCredentials annotation) {
	}
	
	@Override
	public boolean isValid(final Object value, final ConstraintValidatorContext ctx) {
		
		if (value == null) {
			return true;
		}
		
		try {
			final String login    = BeanUtils.getProperty(value, "login");
			final String password = BeanUtils.getProperty(value, "password");
			
			if (isNullOrEmpty(login)) {
				return true;
			}
			
			if (isNullOrEmpty(password)) {
				return true;
			}
			
			// TODO: check fields only when both fields are equals
			
			if (userService.findByLoginAndPassword(login, password) == null) {
				return false;
			}
		
		} catch (final NoSuchMethodException ex) {
			ConstraintViolationUtils.recreate(ctx, "error.internal");
			return false;
		
		} catch (final InvocationTargetException ex) {
			ConstraintViolationUtils.recreate(ctx, "error.internal");
			return false;
		
		} catch (final IllegalAccessException ex) {
			ConstraintViolationUtils.recreate(ctx, "error.internal");
			return false;
		}
		
		return true;
	}
	
}
