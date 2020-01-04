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
package ru.mystamps.web.feature.country;

import ru.mystamps.web.feature.country.CountryDb.Country;

@SuppressWarnings("PMD.CommentDefaultAccessModifier")
public final class CountryValidation {
	
	public static final int NAME_MIN_LENGTH = 3;
	public static final int NAME_MAX_LENGTH = Country.NAME_LENGTH;
	public static final String NAME_EN_REGEXP = "[- a-zA-Z]+";
	public static final String NAME_RU_REGEXP = "[- а-яёА-ЯЁ]+";
	static final String NAME_NO_HYPHEN_REGEXP = "[ \\p{L}]([- \\p{L}]+[ \\p{L}])*";
	@SuppressWarnings("PMD.LongVariable")
	static final String NAME_NO_REPEATING_HYPHENS_REGEXP = "(?!.+[-]{2,}).+";
	
	private CountryValidation() {
	}
	
}

