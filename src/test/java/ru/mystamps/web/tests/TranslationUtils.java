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

package ru.mystamps.web.tests;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public final class TranslationUtils {
	
	private static final String [] BUNDLE_CLASS_NAMES = new String[] {
		"ru.mystamps.i18n.Messages",
		"ru.mystamps.i18n.ValidationMessages",
		"ru.mystamps.i18n.SpringSecurityMessages"
	};
	
	private static final Locale DEFAULT_BUNDLE_LOCALE =
		Locale.ENGLISH;
	
	private static final ResourceBundle [] BUNDLES;
	
	static {
		BUNDLES = new ResourceBundle[BUNDLE_CLASS_NAMES.length];
		int i = 0;
		for (String bundleClassName : BUNDLE_CLASS_NAMES) {
			BUNDLES[i++] = PropertyResourceBundle.getBundle(
				bundleClassName,
				DEFAULT_BUNDLE_LOCALE
			);
		}
	}
	
	private TranslationUtils() {
	}
	
	public static String tr(final String key) {
		String msg = "";
		
		for (ResourceBundle bundle : BUNDLES) {
			if (bundle.containsKey(key)) {
				return bundle.getString(key);
			}
		}
		
		return msg;
	}
	
	// TODO: add simple unit tests (#93)
	public static String stripHtmlTags(final String msg) {
		return msg.replaceAll("\\<.*?>", "");
	}
	
	public static String tr(final String key, final Object... args) {
		// TODO: replace this hack to something less ugly
		final String messageFormat = tr(key).replaceAll("\\{[^\\}]+\\}", "{0}");
		return MessageFormat.format(messageFormat, args);
	}
	
}
