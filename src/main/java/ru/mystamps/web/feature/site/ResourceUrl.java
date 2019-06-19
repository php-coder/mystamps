/*
 * Copyright (C) 2009-2019 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.feature.site;

import java.util.Map;

/**
 * Static resources URLs.
 *
 * Should be used everywhere instead of hard-coded paths.
 *
 * @author Slava Semushin
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class ResourceUrl {
	
	public static final String STATIC_RESOURCES_URL = "https://stamps.filezz.ru";
	
	// MUST be updated when any of our resources were modified
	public static final String RESOURCES_VERSION = "v0.3.13";

	// CheckStyle: ignore LineLength for next 8 lines
	private static final String CATALOG_UTILS_JS   = "/public/js/" + RESOURCES_VERSION + "/CatalogUtils.min.js";
	private static final String COLLECTION_INFO_JS = "/public/js/" + RESOURCES_VERSION + "/collection/info.min.js";
	private static final String DATE_UTILS_JS      = "/public/js/" + RESOURCES_VERSION + "/DateUtils.min.js";
	private static final String MAIN_CSS           = "/static/"    + RESOURCES_VERSION + "/styles/main.min.css";
	private static final String PARTICIPANT_ADD_JS = "/public/js/" + RESOURCES_VERSION + "/participant/add.min.js";
	private static final String SERIES_ADD_JS      = "/public/js/" + RESOURCES_VERSION + "/series/add.min.js";
	private static final String SERIES_INFO_JS     = "/public/js/" + RESOURCES_VERSION + "/series/info.min.js";
	private static final String BOOTSTRAP_LANGUAGE = "https://cdn.jsdelivr.net/gh/usrz/bootstrap-languages@3ac2a3d2b27ac43a471cd99e79d378a03b2c6b5f/languages.min.css";
	private static final String FAVICON_ICO        = "/favicon.ico";
	
	private ResourceUrl() {
	}
	
	public static void exposeUrlsToView(Map<String, String> urls) {
		urls.put("BOOTSTRAP_LANGUAGE", BOOTSTRAP_LANGUAGE);
	}
	
	public static void exposeResourcesToView(Map<String, String> resources, String host) {
		put(resources, host, "CATALOG_UTILS_JS", CATALOG_UTILS_JS);
		put(resources, host, "COLLECTION_INFO_JS", COLLECTION_INFO_JS);
		put(resources, host, "DATE_UTILS_JS", DATE_UTILS_JS);
		put(resources, host, "FAVICON_ICO", FAVICON_ICO);
		put(resources, host, "MAIN_CSS", MAIN_CSS);
		put(resources, host, "PARTICIPANT_ADD_JS", PARTICIPANT_ADD_JS);
		put(resources, host, "SERIES_ADD_JS", SERIES_ADD_JS);
		put(resources, host, "SERIES_INFO_JS", SERIES_INFO_JS);
	}
	
	private static void put(Map<String, String> map, String valuePrefix, String key, String value) {
		if (valuePrefix == null) {
			map.put(key, value);
			return;
		}

		map.put(key, valuePrefix + value);
	}
	
}
