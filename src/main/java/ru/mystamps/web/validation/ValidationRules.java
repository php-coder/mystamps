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
package ru.mystamps.web.validation;

import ru.mystamps.web.Db;

public final class ValidationRules {
	
	public static final int LOGIN_MIN_LENGTH = 2;
	public static final int LOGIN_MAX_LENGTH = Db.User.LOGIN_LENGTH;
	public static final String LOGIN_REGEXP = "[-_\\.a-zA-Z0-9]+";
	
	public static final int NAME_MAX_LENGTH = Db.User.NAME_LENGTH;
	public static final String NAME_REGEXP = "[- \\p{L}]+";
	public static final String NAME_NO_HYPHEN_REGEXP = "[ \\p{L}]([- \\p{L}]+[ \\p{L}])*";
	
	public static final int PASSWORD_MIN_LENGTH = 4;
	
	public static final int EMAIL_MAX_LENGTH = Db.UsersActivation.EMAIL_LENGTH;
	
	public static final int ACT_KEY_LENGTH = Db.UsersActivation.ACTIVATION_KEY_LENGTH;
	public static final String ACT_KEY_REGEXP = "[0-9a-z]+";
	
	public static final int CATEGORY_NAME_MIN_LENGTH = 3;
	public static final int CATEGORY_NAME_MAX_LENGTH = Db.Category.NAME_LENGTH;
	public static final String CATEGORY_NAME_EN_REGEXP = "[- a-zA-Z]+";
	public static final String CATEGORY_NAME_RU_REGEXP = "[- а-яёА-ЯЁ]+";
	public static final String CATEGORY_NAME_NO_HYPHEN_REGEXP = "[ \\p{L}]([- \\p{L}]+[ \\p{L}])*";
	
	public static final int COUNTRY_NAME_MIN_LENGTH = 3;
	public static final int COUNTRY_NAME_MAX_LENGTH = Db.Country.NAME_LENGTH;
	public static final String COUNTRY_NAME_EN_REGEXP = "[- a-zA-Z]+";
	public static final String COUNTRY_NAME_RU_REGEXP = "[- а-яёА-ЯЁ]+";
	public static final String COUNTRY_NAME_NO_HYPHEN_REGEXP = "[ \\p{L}]([- \\p{L}]+[ \\p{L}])*";
	
	public static final int MIN_STAMPS_IN_SERIES = 1;
	public static final int MAX_STAMPS_IN_SERIES = 50;
	public static final int MIN_RELEASE_YEAR     = 1840;
	public static final int MAX_SERIES_COMMENT_LENGTH = Db.Series.COMMENT_LENGTH;
	public static final String CATALOG_NUMBERS_REGEXP = "[1-9][0-9]{0,3}(,[1-9][0-9]{0,3})*";
	
	/** Maximum uploading image size in kilobytes. */
	public static final long MAX_IMAGE_SIZE = 500;
	
	public static final int MAX_DAYS_IN_MONTH = 31;
	public static final int MAX_MONTHS_IN_YEAR = 12;
	
	private ValidationRules() {
	}
	
}

