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
package ru.mystamps.web;

import ru.mystamps.web.feature.account.AccountUrl;
import ru.mystamps.web.feature.category.CategoryUrl;
import ru.mystamps.web.feature.collection.CollectionUrl;
import ru.mystamps.web.feature.country.CountryUrl;
import ru.mystamps.web.feature.image.ImageUrl;
import ru.mystamps.web.feature.participant.ParticipantUrl;
import ru.mystamps.web.feature.series.SeriesUrl;
import ru.mystamps.web.feature.series.importing.SeriesImportUrl;
import ru.mystamps.web.feature.series.importing.sale.SeriesSalesImportUrl;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds path to site and all URLs.
 *
 * Should be used anywhere instead of hard-coded paths.
 *
 * @author Slava Semushin
 */
public final class Url {
	public static final String PUBLIC_URL            = "https://my-stamps.ru";
	public static final String STATIC_RESOURCES_URL  = "https://stamps.filezz.ru";
	
	// see also robotframework-maven-plugin configuration at pom.xml
	public static final String SITE                  = "http://127.0.0.1:8080";
	
	public static final String INDEX_PAGE            = "/";
	public static final String ROBOTS_TXT            = "/robots.txt";
	public static final String SITEMAP_XML           = "/sitemap.xml";

	public static final String DAILY_STATISTICS      = "/report/daily";
	public static final String SITE_EVENTS_PAGE      = "/site/events";
	
	public static final String FORBIDDEN_PAGE        = "/error/403";
	public static final String NOT_FOUND_PAGE        = "/error/404";
	public static final String INTERNAL_ERROR_PAGE   = "/error/500";
	
	// MUST be updated when any of our resources were modified
	public static final String RESOURCES_VERSION      = "v0.3.13";
	
	// CheckStyle: ignore LineLength for next 7 lines
	public static final String MAIN_CSS               = "/static/" + RESOURCES_VERSION + "/styles/main.min.css";
	public static final String CATALOG_UTILS_JS       = "/public/js/" + RESOURCES_VERSION + "/CatalogUtils.min.js";    // NOPMD: AvoidDuplicateLiterals
	public static final String DATE_UTILS_JS          = "/public/js/" + RESOURCES_VERSION + "/DateUtils.min.js";       // NOPMD: AvoidDuplicateLiterals
	public static final String PARTICIPANT_ADD_JS     = "/public/js/" + RESOURCES_VERSION + "/participant/add.min.js"; // NOPMD: AvoidDuplicateLiterals
	public static final String SERIES_ADD_JS          = "/public/js/" + RESOURCES_VERSION + "/series/add.min.js";      // NOPMD: AvoidDuplicateLiterals
	public static final String SERIES_INFO_JS         = "/public/js/" + RESOURCES_VERSION + "/series/info.min.js";     // NOPMD: AvoidDuplicateLiterals
	public static final String COLLECTION_INFO_JS     = "/public/js/" + RESOURCES_VERSION + "/collection/info.min.js"; // NOPMD: AvoidDuplicateLiterals
	public static final String FAVICON_ICO            = "/favicon.ico";
	
	// CheckStyle: ignore LineLength for next 4 lines
	public static final String BOOTSTRAP_CSS          = "/public/bootstrap/3.4.1/css/bootstrap.min.css";
	public static final String BOOTSTRAP_JS           = "/public/bootstrap/3.4.1/js/bootstrap.min.js";
	public static final String JQUERY_JS              = "/public/jquery/1.9.1/jquery.min.js";
	public static final String BOOTSTRAP_LANGUAGE     = "https://cdn.jsdelivr.net/gh/usrz/bootstrap-languages@3ac2a3d2b27ac43a471cd99e79d378a03b2c6b5f/languages.min.css";
	
	// CheckStyle: ignore LineLength for next 3 lines
	// FIXME: use minimal version of CSS file when it will be available (https://github.com/webjars/selectize.js/issues/3)
	public static final String SELECTIZE_CSS          = "/public/selectize/0.12.5/css/selectize.bootstrap3.css";
	public static final String SELECTIZE_JS           = "/public/selectize/0.12.5/js/standalone/selectize.min.js";
	
	// see also pom.xml and ru.mystamps.web.config.MvcConfig#addResourceHandlers()
	// CheckStyle: ignore LineLength for next 5 lines
	public static final String BOOTSTRAP_CSS_CDN     = "https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css";
	public static final String BOOTSTRAP_JS_CDN      = "https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js";
	public static final String JQUERY_JS_CDN         = "https://yandex.st/jquery/1.9.1/jquery.min.js";
	public static final String SELECTIZE_CSS_CDN     = "https://cdnjs.cloudflare.com/ajax/libs/selectize.js/0.12.4/css/selectize.bootstrap3.min.css";
	public static final String SELECTIZE_JS_CDN      = "https://cdnjs.cloudflare.com/ajax/libs/selectize.js/0.12.4/js/standalone/selectize.min.js";
	
	// see also ru.mystamps.web.support.togglz.TogglzConfig#getTogglzConsole()
	public static final String TOGGLZ_CONSOLE_PAGE    = "/togglz";
	
	// see also src/main/resources/application-test.properties
	public static final String H2_CONSOLE_PAGE       = "/console";
	
	private Url() {
	}
	
	public static Map<String, String> asMap(boolean production) {
		boolean serveContentFromSingleHost = !production;
		
		// Not all URLs are listed here but only those that are being used on views
		Map<String, String> map = new HashMap<>();
		AccountUrl.exposeUrlsToView(map);
		CategoryUrl.exposeUrlsToView(map);
		CountryUrl.exposeUrlsToView(map);
		CollectionUrl.exposeUrlsToView(map);
		ParticipantUrl.exposeUrlsToView(map);
		SeriesUrl.exposeUrlsToView(map);
		SeriesImportUrl.exposeUrlsToView(map);
		SeriesSalesImportUrl.exposeUrlsToView(map);
		
		map.put("BOOTSTRAP_LANGUAGE", BOOTSTRAP_LANGUAGE);
		map.put("DAILY_STATISTICS", DAILY_STATISTICS);
		map.put("PUBLIC_URL", production ? PUBLIC_URL : SITE);
		map.put("SITE_EVENTS_PAGE", SITE_EVENTS_PAGE);
		
		if (serveContentFromSingleHost) {
			ImageUrl.exposeResourcesToView(map);
			
			map.put("BOOTSTRAP_CSS", BOOTSTRAP_CSS);
			map.put("BOOTSTRAP_JS", BOOTSTRAP_JS);
			map.put("CATALOG_UTILS_JS", CATALOG_UTILS_JS);
			map.put("COLLECTION_INFO_JS", COLLECTION_INFO_JS);
			map.put("DATE_UTILS_JS", DATE_UTILS_JS);
			map.put("FAVICON_ICO", FAVICON_ICO);
			map.put("JQUERY_JS", JQUERY_JS);
			map.put("MAIN_CSS", MAIN_CSS);
			map.put("PARTICIPANT_ADD_JS", PARTICIPANT_ADD_JS);
			map.put("SELECTIZE_CSS", SELECTIZE_CSS);
			map.put("SELECTIZE_JS", SELECTIZE_JS);
			map.put("SERIES_ADD_JS", SERIES_ADD_JS);
			map.put("SERIES_INFO_JS", SERIES_INFO_JS);
		} else {
			// This is a simplest decorator around Map that modifies inserted URLs by prepending
			// a host for static resources to them.
			//
			// I don't want to use ForwardingMap (Guava) or TransformedMap (commons-collections)
			// as we don't have them in dependencies and I don't want to add them either just for
			// a few lines of code.
			//
			// NOTE: this implementation won't work as expected when a caller uses putAll(),
			// putIfAbsent() or modifies a map by other ways.
			Map<String, String> resourcesMap = new HashMap<String, String>(map) {
				@Override
				public String put(String key, String value) {
					// Use a separate domain for our own resources
					return map.put(STATIC_RESOURCES_URL + key, value);
				}
			};
			
			ImageUrl.exposeResourcesToView(resourcesMap);
			
			map.put("CATALOG_UTILS_JS", STATIC_RESOURCES_URL + CATALOG_UTILS_JS);
			map.put("COLLECTION_INFO_JS", STATIC_RESOURCES_URL + COLLECTION_INFO_JS);
			map.put("DATE_UTILS_JS", STATIC_RESOURCES_URL + DATE_UTILS_JS);
			map.put("FAVICON_ICO", STATIC_RESOURCES_URL + FAVICON_ICO);
			map.put("MAIN_CSS", STATIC_RESOURCES_URL + MAIN_CSS);
			map.put("PARTICIPANT_ADD_JS", STATIC_RESOURCES_URL + PARTICIPANT_ADD_JS);
			map.put("SERIES_ADD_JS", STATIC_RESOURCES_URL + SERIES_ADD_JS);
			map.put("SERIES_INFO_JS", STATIC_RESOURCES_URL + SERIES_INFO_JS);
			
			// Use CDN for the external resources like libraries
			map.put("BOOTSTRAP_CSS", BOOTSTRAP_CSS_CDN);
			map.put("BOOTSTRAP_JS", BOOTSTRAP_JS_CDN);
			map.put("JQUERY_JS", JQUERY_JS_CDN);
			map.put("SELECTIZE_CSS", SELECTIZE_CSS_CDN);
			map.put("SELECTIZE_JS", SELECTIZE_JS_CDN);
		}
		
		return map;
	}
	
}
