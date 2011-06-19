/*
 * Copyright (C) 2009-2011 Slava Semushin <slava.semushin@gmail.com>
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

package ru.mystamps.web.validation;

import org.apache.commons.validator.EmailValidator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.ValidationUtils;

import ru.mystamps.web.model.RegisterAccountForm;

import static ru.mystamps.web.validation.ValidationRules.EMAIL_MAX_LENGTH;

@Component
public class RegisterAccountValidator implements Validator {
	
	@Override
	public boolean supports(final Class clazz) {
		return RegisterAccountForm.class.isAssignableFrom(clazz);
	}
	
	@Override
	public void validate(final Object target, final Errors errors) {
		final RegisterAccountForm form = (RegisterAccountForm)target;
		
		validateEmail(form, errors);
	}
	
	private static void validateEmail(final RegisterAccountForm form, final Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "value.required");
		if (errors.hasFieldErrors("email")) {
			return;
		}
		
		final String email = form.getEmail();
		
		if (email.length() > EMAIL_MAX_LENGTH.intValue()) {
			errors.rejectValue(
				"email",
				"value.too-long",
				new Object[]{EMAIL_MAX_LENGTH},
				"XXX");
			return;
		}
		
		// TODO: Deprecated. Use the new EmailValidator in the
		// routines package. (Or move to using Hibernate validators.)
		EmailValidator validator = EmailValidator.getInstance();
		if (!validator.isValid(email)) {
			errors.rejectValue("email", "email.invalid");
		}
		
	}
	
}

