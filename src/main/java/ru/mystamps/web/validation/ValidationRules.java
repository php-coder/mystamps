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

public final class ValidationRules {
	
	public static final Integer LOGIN_MIN_LENGTH = 2;
	public static final Integer LOGIN_MAX_LENGTH = 15;
	public static final String LOGIN_REGEXP = "[-_a-zA-Z0-9]+";
	
	public static final Integer NAME_MAX_LENGTH = 100;
	public static final String NAME_REGEXP1 = "[- \\p{Alpha}]+";
	public static final String NAME_REGEXP2 = "[ \\p{Alpha}]([- \\p{Alpha}]+[ \\p{Alpha}])*";
	
	public static final Integer PASSWORD_MIN_LENGTH = 4;
	public static final String PASSWORD_REGEXP = "[-_a-zA-Z0-9]+";
	
	public static final Integer EMAIL_MAX_LENGTH = 255;
	
	public static final Integer ACT_KEY_LENGTH = 10;
	public static final String ACT_KEY_REGEXP = "[0-9a-z]+";
	
	private ValidationRules() {
	}
	
}

