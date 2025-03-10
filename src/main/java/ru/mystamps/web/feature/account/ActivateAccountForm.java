/*
 * Copyright (C) 2009-2025 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.feature.account;

import lombok.Getter;
import lombok.Setter;
import ru.mystamps.web.support.beanvalidation.FieldsMatch;
import ru.mystamps.web.support.beanvalidation.FieldsMismatch;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static ru.mystamps.web.feature.account.AccountValidation.ACT_KEY_REGEXP;
import static ru.mystamps.web.feature.account.AccountValidation.LOGIN_MAX_LENGTH;
import static ru.mystamps.web.feature.account.AccountValidation.LOGIN_MIN_LENGTH;
import static ru.mystamps.web.feature.account.AccountValidation.LOGIN_REGEXP;
import static ru.mystamps.web.feature.account.AccountValidation.NAME_MAX_LENGTH;
import static ru.mystamps.web.feature.account.AccountValidation.NAME_NO_HYPHEN_REGEXP;
import static ru.mystamps.web.feature.account.AccountValidation.NAME_REGEXP;
import static ru.mystamps.web.feature.account.AccountValidation.PASSWORD_MAX_LENGTH;
import static ru.mystamps.web.feature.account.AccountValidation.PASSWORD_MIN_LENGTH;

@Getter
@Setter
@FieldsMismatch(
	first = "login",
	second = "password",
	message = "{password.login.match}",
	groups = ActivateAccountForm.FormChecks.class
)
@FieldsMatch(
	first = "password",
	second = "passwordConfirmation",
	message = "{password.mismatch}",
	groups = ActivateAccountForm.FormChecks.class
)
public class ActivateAccountForm implements ActivateAccountDto {
	
	@NotEmpty(groups = Login1Checks.class)
	@Size(min = LOGIN_MIN_LENGTH, message = "{value.too-short}", groups = Login2Checks.class)
	@Size(max = LOGIN_MAX_LENGTH, message = "{value.too-long}", groups = Login2Checks.class)
	@Pattern(regexp = LOGIN_REGEXP, message = "{login.invalid}", groups = Login3Checks.class)
	@Pattern(
		regexp = AccountValidation.LOGIN_NO_REPEATING_CHARS_REGEXP,
		message = "{login.repetition_chars}",
		groups = Login4Checks.class
	)
	@UniqueLogin(groups = Login5Checks.class)
	private String login;
	
	@Size(max = NAME_MAX_LENGTH, message = "{value.too-long}", groups = Name1Checks.class)
	@Pattern(regexp = NAME_REGEXP, message = "{name.invalid}", groups = Name2Checks.class)
	@Pattern(regexp = NAME_NO_HYPHEN_REGEXP, message = "{value.hyphen}", groups = Name3Checks.class)
	private String name;
	
	@NotEmpty(groups = Password1Checks.class)
	@Size(min = PASSWORD_MIN_LENGTH, message = "{value.too-short}", groups = Password2Checks.class)
	@Size(max = PASSWORD_MAX_LENGTH, message = "{value.too-long}", groups = Password2Checks.class)
	private String password;
	
	@NotEmpty(groups = PasswordConfirmation1Checks.class)
	private String passwordConfirmation;
	
	@NotEmpty(groups = ActKey1Checks.class)
	@Size(
		min = AccountValidation.ACT_KEY_LENGTH,
		max = AccountValidation.ACT_KEY_LENGTH,
		message = "{value.invalid-length}",
		groups = ActKey2Checks.class
	)
	@Pattern(regexp = ACT_KEY_REGEXP, message = "{key.invalid}", groups = ActKey3Checks.class)
	@ExistingActivationKey(groups = ActKey4Checks.class)
	private String activationKey;
	
	
	@GroupSequence({
		Login1Checks.class,
		Login2Checks.class,
		Login3Checks.class,
		Login4Checks.class,
		Login5Checks.class
	})
	public interface LoginChecks {
	}
	
	public interface Login1Checks {
	}
	
	public interface Login2Checks {
	}
	
	public interface Login3Checks {
	}
	
	public interface Login4Checks {
	}
	
	public interface Login5Checks {
	}
	
	@GroupSequence({
		Name1Checks.class,
		Name2Checks.class,
		Name3Checks.class
	})
	public interface NameChecks {
	}
	
	public interface Name1Checks {
	}
	
	public interface Name2Checks {
	}
	
	public interface Name3Checks {
	}
	
	@GroupSequence({
		Password1Checks.class,
		Password2Checks.class
	})
	public interface PasswordChecks {
	}
	
	public interface Password1Checks {
	}
	
	public interface Password2Checks {
	}
	
	@GroupSequence({
		PasswordConfirmation1Checks.class
	})
	public interface PasswordConfirmationChecks {
	}
	
	public interface PasswordConfirmation1Checks {
	}
	
	@GroupSequence({
		ActKey1Checks.class,
		ActKey2Checks.class,
		ActKey3Checks.class,
		ActKey4Checks.class
	})
	public interface ActKeyChecks {
	}
	
	public interface ActKey1Checks {
	}
	
	public interface ActKey2Checks {
	}
	
	public interface ActKey3Checks {
	}
	
	public interface ActKey4Checks {
	}
	
	public interface FormChecks {
	}
	
}
