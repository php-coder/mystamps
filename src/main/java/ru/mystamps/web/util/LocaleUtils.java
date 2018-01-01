/*
 * Copyright (C) 2009-2018 Slava Semushin <slava.semushin@gmail.com>
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

import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;

public final class LocaleUtils {
	
	public static final Locale RUSSIAN = new Locale("ru", "RU");
	
	private LocaleUtils() {
	}
	
	public static String getLanguageOrNull(Locale locale) {
		return getLanguageOrDefault(locale, null);
	}
	
	public static String getLanguageOrDefault(Locale locale, String defaultValue) {
		if (locale == null) {
			return defaultValue;
		}
		
		return locale.getLanguage();
	}
	
	// Our version of LocaleContextHolder.getLocale() that
	// doesn't fallback to the system default locale and
	// returns string representation of locale.
	public static String getCurrentLanguageOrNull() {
		LocaleContext localeContext = LocaleContextHolder.getLocaleContext();
		if (localeContext == null) {
			return null;
		}
		
		return getLanguageOrNull(localeContext.getLocale());
	}
	
}
