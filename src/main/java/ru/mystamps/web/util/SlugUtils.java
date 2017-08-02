/*
 * Copyright (C) 2009-2017 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.util;

import java.util.Locale;

import org.apache.commons.lang3.Validate;

public final class SlugUtils {
	
	private SlugUtils() {
	}
	
	public static String slugify(String text) {
		Validate.isTrue(text != null, "Text must be non null");
		
		return text.toLowerCase(Locale.ENGLISH)
			// replace all characters except letters and digits to hyphen
			.replaceAll("[^\\p{Alnum}]", "-")
			// replace multiple hyphens by one
			.replaceAll("-{2,}", "-")
			// remove leading hyphen
			.replaceAll("^-", "")
			// remove ending hyphen
			.replaceAll("-$", "");
	}
	
}
