/*
 * Copyright (C) 2009-2015 Slava Semushin <slava.semushin@gmail.com>
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

import org.apache.commons.io.IOUtils;

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
		FileInputStream stream = null;
		try {
			File file = new File(
				TranslationUtils.class.getClassLoader().getResource(filename).toURI()
			);
			stream = new FileInputStream(file);
			return new PropertyResourceBundle(stream);
		
		} catch (IOException | URISyntaxException ex) {
			throw new RuntimeException(ex);
		
		} finally {
			IOUtils.closeQuietly(stream);
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
	
	// TODO: add simple unit tests (#93)
	public static String stripHtmlTags(String msg) {
		return msg.replaceAll("\\<.*?>", "");
	}
	
	public static String tr(String key, Object... args) {
		// TODO: replace this hack to something less ugly
		String messageFormat = tr(key).replaceAll("\\{[^\\}]+\\}", "{0}");
		return MessageFormat.format(messageFormat, args);
	}
	
}
