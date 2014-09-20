/*
 * Copyright (C) 2009-2014 Slava Semushin <slava.semushin@gmail.com>
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

import ru.mystamps.web.entity.LocalizedEntity;

public final class LocaleUtils {
	
	private LocaleUtils() {
	}
	
	@SuppressWarnings("checkstyle:avoidinlineconditionals")
	public static String getLanguageOrNull(Locale locale) {
		return locale != null ? locale.getLanguage() : null;
	}
	
	public static String getLanguageOrDefault(Locale locale, String defaultValue) {
		if (locale == null) {
			return defaultValue;
		}
		
		return locale.getLanguage();
	}
	
	public static String getLocalizedName(Locale locale, LocalizedEntity entity) {
		Validate.isTrue(entity != null, "LocalizedEntity must be non null");
		
		String lang = getLanguageOrNull(locale);
		if ("ru".equals(lang)) {
			return entity.getNameRu();
		}
		
		return entity.getName();
	}
	
}
