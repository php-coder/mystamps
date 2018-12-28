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
package ru.mystamps.web.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public final class TranslationUtils {
	
	private static final String [] PROPERTIES_FILE_NAMES = new String[] {
		"ru/mystamps/i18n/Messages.properties",
		"ru/mystamps/i18n/ValidationMessages.properties",
		"ru/mystamps/i18n/SpringSecurityMessages.properties"
	};
	
	private static final ResourceBundle [] BUNDLES;
	
	static {
		BUNDLES = new ResourceBundle[PROPERTIES_FILE_NAMES.length];
		int i = 0;
		for (String propertiesFileName : PROPERTIES_FILE_NAMES) {
			BUNDLES[i++] = getResourceBundleForFile(propertiesFileName);
		}
	}
	
	private TranslationUtils() {
	}
	
	private static ResourceBundle getResourceBundleForFile(String filename) {
		File file;
		try {
			file = new File(
				TranslationUtils.class.getClassLoader().getResource(filename).toURI()
			);
		} catch (URISyntaxException ex) {
			throw new RuntimeException(ex);
		}
		try (FileInputStream stream = new FileInputStream(file)) {
			return new PropertyResourceBundle(stream);
		
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public static String tr(String key) {
		String msg = "";
		
		for (ResourceBundle bundle : BUNDLES) {
			if (bundle.containsKey(key)) {
				return bundle.getString(key);
			}
		}
		
		return msg;
	}
	
	public static String stripHtmlTags(String msg) {
		return msg.replaceAll("\\<.*?>", "");
	}
	
	public static String tr(String key, Object... args) {
		String messageFormat = tr(key).replaceAll("\\{[^\\}]+\\}", "{0}");
		return MessageFormat.format(messageFormat, args);
	}
	
}
