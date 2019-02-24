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
	
	public static final String REGISTRATION_PAGE     = "/account/register";
	public static final String AUTHENTICATION_PAGE   = "/account/auth";
	public static final String LOGIN_PAGE            = "/account/login";
	public static final String LOGOUT_PAGE           = "/account/logout";
	public static final String ACTIVATE_ACCOUNT_PAGE = "/account/activate";
	
	public static final String ADD_SERIES_PAGE          = "/series/add";
	public static final String ADD_SERIES_ASK_PAGE      = "/series/{id}/ask";
	public static final String INFO_SERIES_PAGE         = "/series/{id}";
	public static final String ADD_IMAGE_SERIES_PAGE    = "/series/{id}/image";
	public static final String SEARCH_SERIES_BY_CATALOG = "/series/search/by_catalog";
	
	public static final String REQUEST_IMPORT_SERIES_PAGE = "/series/import/request";
	public static final String REQUEST_IMPORT_PAGE        = "/series/import/request/{id}";
	public static final String LIST_IMPORT_REQUESTS_PAGE  = "/series/import/requests";
	
	public static final String SUGGEST_SERIES_CATEGORY    = "/suggest/series_category";
	public static final String SUGGEST_SERIES_COUNTRY     = "/suggest/series_country";
	
	public static final String ADD_CATEGORY_PAGE     = "/category/add";
	public static final String GET_CATEGORIES_PAGE   = "/categories";
	public static final String INFO_CATEGORY_PAGE    = "/category/{slug}";
	
	public static final String ADD_COUNTRY_PAGE      = "/country/add";
	public static final String GET_COUNTRIES_PAGE    = "/countries";
	public static final String INFO_COUNTRY_PAGE     = "/country/{slug}";
	
	public static final String INFO_COLLECTION_PAGE       = "/collection/{slug}";
	public static final String ESTIMATION_COLLECTION_PAGE = "/collection/{slug}/estimation";
	
	public static final String GET_IMAGE_PAGE         = "/image/{id}";
	public static final String GET_IMAGE_PREVIEW_PAGE = "/image/preview/{id}";
	
	public static final String ADD_PARTICIPANT_PAGE  = "/participant/add";
	
	public static final String FORBIDDEN_PAGE        = "/error/403";
	public static final String NOT_FOUND_PAGE        = "/error/404";
	public static final String INTERNAL_ERROR_PAGE   = "/error/500";
	
	// For backward compatibility
	public static final String ACTIVATE_ACCOUNT_PAGE_WITH_KEY = "/account/activate/key/{key}";
	public static final String LIST_CATEGORIES_PAGE           = "/category/list";
	public static final String LIST_COUNTRIES_PAGE            = "/country/list";
	public static final String INFO_CATEGORY_BY_ID_PAGE       = "/category/{id}/{slug}";
	public static final String INFO_COUNTRY_BY_ID_PAGE        = "/country/{id}/{slug}";
	public static final String INFO_COLLECTION_BY_ID_PAGE     = "/collection/{id}/{slug}";
	public static final String ADD_SERIES_WITH_CATEGORY_PAGE  = "/series/add/category/{slug}";
	public static final String ADD_SERIES_WITH_COUNTRY_PAGE   = "/series/add/country/{slug}";
	
	// MUST be updated when any of our resources were modified
	public static final String RESOURCES_VERSION      = "v0.3.12";
	
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
	public static final String BOOTSTRAP_LANGUAGE     = "https://cdn.rawgit.com/usrz/bootstrap-languages/3ac2a3d2b27ac43a471cd99e79d378a03b2c6b5f/languages.min.css";
	
	// CheckStyle: ignore LineLength for next 3 lines
	// FIXME: use minimal version of CSS file when it will be available (https://github.com/webjars/selectize.js/issues/3)
	public static final String SELECTIZE_CSS          = "/public/selectize/0.12.4/css/selectize.bootstrap3.css";
	public static final String SELECTIZE_JS           = "/public/selectize/0.12.4/js/standalone/selectize.min.js";
	
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
		// Constants sorted in an ascending order.
		Map<String, String> map = new HashMap<>();
		map.put("ACTIVATE_ACCOUNT_PAGE", ACTIVATE_ACCOUNT_PAGE);
		map.put("ADD_CATEGORY_PAGE", ADD_CATEGORY_PAGE);
		map.put("ADD_COUNTRY_PAGE", ADD_COUNTRY_PAGE);
		map.put("ADD_IMAGE_SERIES_PAGE", ADD_IMAGE_SERIES_PAGE);
		map.put("ADD_PARTICIPANT_PAGE", ADD_PARTICIPANT_PAGE);
		map.put("ADD_SERIES_ASK_PAGE", ADD_SERIES_ASK_PAGE);
		map.put("ADD_SERIES_PAGE", ADD_SERIES_PAGE);
		map.put("AUTHENTICATION_PAGE", AUTHENTICATION_PAGE);
		map.put("BOOTSTRAP_LANGUAGE", BOOTSTRAP_LANGUAGE);
		map.put("DAILY_STATISTICS", DAILY_STATISTICS);
		map.put("ESTIMATION_COLLECTION_PAGE", ESTIMATION_COLLECTION_PAGE);
		map.put("GET_CATEGORIES_PAGE", GET_CATEGORIES_PAGE);
		map.put("GET_COUNTRIES_PAGE", GET_COUNTRIES_PAGE);
		map.put("INFO_CATEGORY_PAGE", INFO_CATEGORY_PAGE);
		map.put("INFO_COLLECTION_PAGE", INFO_COLLECTION_PAGE);
		map.put("INFO_COUNTRY_PAGE", INFO_COUNTRY_PAGE);
		map.put("INFO_SERIES_PAGE", INFO_SERIES_PAGE);
		map.put("LIST_IMPORT_REQUESTS_PAGE", LIST_IMPORT_REQUESTS_PAGE);
		map.put("LOGIN_PAGE", LOGIN_PAGE);
		map.put("LOGOUT_PAGE", LOGOUT_PAGE);
		map.put("PUBLIC_URL", production ? PUBLIC_URL : SITE);
		map.put("REGISTRATION_PAGE", REGISTRATION_PAGE);
		map.put("REQUEST_IMPORT_PAGE", REQUEST_IMPORT_PAGE);
		map.put("REQUEST_IMPORT_SERIES_PAGE", REQUEST_IMPORT_SERIES_PAGE);
		map.put("SEARCH_SERIES_BY_CATALOG", SEARCH_SERIES_BY_CATALOG);
		map.put("SITE_EVENTS_PAGE", SITE_EVENTS_PAGE);
		map.put("SUGGEST_SERIES_COUNTRY", SUGGEST_SERIES_COUNTRY);
		map.put("SUGGEST_SERIES_CATEGORY", SUGGEST_SERIES_CATEGORY);
		
		if (serveContentFromSingleHost) {
			// Constants sorted in an ascending order.
			map.put("BOOTSTRAP_CSS", BOOTSTRAP_CSS);
			map.put("BOOTSTRAP_JS", BOOTSTRAP_JS);
			map.put("CATALOG_UTILS_JS", CATALOG_UTILS_JS);
			map.put("COLLECTION_INFO_JS", COLLECTION_INFO_JS);
			map.put("DATE_UTILS_JS", DATE_UTILS_JS);
			map.put("FAVICON_ICO", FAVICON_ICO);
			map.put("GET_IMAGE_PAGE", GET_IMAGE_PAGE);
			map.put("GET_IMAGE_PREVIEW_PAGE", GET_IMAGE_PREVIEW_PAGE);
			map.put("JQUERY_JS", JQUERY_JS);
			map.put("MAIN_CSS", MAIN_CSS);
			map.put("PARTICIPANT_ADD_JS", PARTICIPANT_ADD_JS);
			map.put("SELECTIZE_CSS", SELECTIZE_CSS);
			map.put("SELECTIZE_JS", SELECTIZE_JS);
			map.put("SERIES_ADD_JS", SERIES_ADD_JS);
			map.put("SERIES_INFO_JS", SERIES_INFO_JS);
		} else {
			// Use a separate domain for our own resources
			// Constants sorted in an ascending order.
			map.put("CATALOG_UTILS_JS", STATIC_RESOURCES_URL + CATALOG_UTILS_JS);
			map.put("COLLECTION_INFO_JS", STATIC_RESOURCES_URL + COLLECTION_INFO_JS);
			map.put("DATE_UTILS_JS", STATIC_RESOURCES_URL + DATE_UTILS_JS);
			map.put("FAVICON_ICO", STATIC_RESOURCES_URL + FAVICON_ICO);
			map.put("GET_IMAGE_PAGE", STATIC_RESOURCES_URL + GET_IMAGE_PAGE);
			map.put("GET_IMAGE_PREVIEW_PAGE", STATIC_RESOURCES_URL + GET_IMAGE_PREVIEW_PAGE);
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
