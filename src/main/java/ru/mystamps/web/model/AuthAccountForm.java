/*
 * Copyright (C) 2009-2012 Slava Semushin <slava.semushin@gmail.com>
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

package ru.mystamps.web.model;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.validation.GroupSequence;

import org.hibernate.validator.constraints.NotEmpty;

import ru.mystamps.web.validation.jsr303.ValidCredentials;

import lombok.Getter;
import lombok.Setter;

import static ru.mystamps.web.validation.ValidationRules.LOGIN_MAX_LENGTH;
import static ru.mystamps.web.validation.ValidationRules.LOGIN_MIN_LENGTH;
import static ru.mystamps.web.validation.ValidationRules.LOGIN_REGEXP;
import static ru.mystamps.web.validation.ValidationRules.PASSWORD_MIN_LENGTH;
import static ru.mystamps.web.validation.ValidationRules.PASSWORD_REGEXP;

@Getter
@Setter
@ValidCredentials(groups = AuthAccountForm.FormChecks.class)
public class AuthAccountForm {
	
	@NotEmpty(groups = Login1Checks.class)
	@Size.List({
		@Size(min = LOGIN_MIN_LENGTH, message = "{value.too-short}", groups = Login2Checks.class),
		@Size(max = LOGIN_MAX_LENGTH, message = "{value.too-long}", groups = Login2Checks.class)
	})
	@Pattern(regexp = LOGIN_REGEXP, message = "{login.invalid}", groups = Login3Checks.class)
	private String login;
	
	@NotEmpty(groups = Password1Checks.class)
	@Size(min = PASSWORD_MIN_LENGTH, message = "{value.too-short}", groups = Password2Checks.class)
	@Pattern(
		regexp = PASSWORD_REGEXP,
		message = "{password.invalid}",
		groups = Password3Checks.class
	)
	private String password;
	
	
	@GroupSequence({
		Login1Checks.class,
		Login2Checks.class,
		Login3Checks.class
	})
	public interface LoginChecks {
	}
	
	public interface Login1Checks {
	}
	
	public interface Login2Checks {
	}
	
	public interface Login3Checks {
	}
	
	@GroupSequence({
		Password1Checks.class,
		Password2Checks.class,
		Password3Checks.class
	})
	public interface PasswordChecks {
	}
	
	public interface Password1Checks {
	}
	
	public interface Password2Checks {
	}
	
	public interface Password3Checks {
	}
	
	public interface FormChecks {
	}
	
}
