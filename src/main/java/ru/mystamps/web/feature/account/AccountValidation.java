/*
 * Copyright (C) 2009-2020 Slava Semushin <slava.semushin@gmail.com>
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

import ru.mystamps.web.feature.account.AccountDb.User;
import ru.mystamps.web.feature.account.AccountDb.UsersActivation;

@SuppressWarnings("PMD.CommentDefaultAccessModifier")
public final class AccountValidation {
	
	public static final int LOGIN_MIN_LENGTH = 2;
	public static final int LOGIN_MAX_LENGTH = User.LOGIN_LENGTH;
	static final String LOGIN_REGEXP = "[-_\\.a-zA-Z0-9]+";
	@SuppressWarnings("PMD.LongVariable")
	static final String LOGIN_NO_REPEATING_CHARS_REGEXP = "(?!.+[-_.]{2,}).+";
	
	static final int NAME_MAX_LENGTH = User.NAME_LENGTH;
	static final String NAME_REGEXP = "[- \\p{L}]+";
	static final String NAME_NO_HYPHEN_REGEXP = "[ \\p{L}]([- \\p{L}]+[ \\p{L}])*";
	
	static final int PASSWORD_MIN_LENGTH = 4;
	// We limit max length because bcrypt has a maximum password length.
	// See also: http://www.mscharhag.com/software-development/bcrypt-maximum-password-length
	static final int PASSWORD_MAX_LENGTH = 72;
	
	static final int EMAIL_MAX_LENGTH = UsersActivation.EMAIL_LENGTH;
	// Require 2+ level domains in e-mails.
	static final String EMAIL_2ND_LEVEL_DOMAIN_REGEXP = ".+@.+\\..+";
	
	static final int ACT_KEY_LENGTH = UsersActivation.ACTIVATION_KEY_LENGTH;
	static final String ACT_KEY_REGEXP = "[0-9a-z]+";
	
	private AccountValidation() {
	}
	
}

