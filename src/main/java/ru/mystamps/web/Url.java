/*
 * Copyright (C) 2009-2016 Slava Semushin <slava.semushin@gmail.com>
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
	public static final String STATIC_RESOURCES_URL  = PUBLIC_URL;
	
	// defined at pom.xml (and used by functional tests only)
	public static final String SITE                  = "http://127.0.0.1:8080";
	
	public static final String INDEX_PAGE            = "/";
	public static final String ROBOTS_TXT            = "/robots.txt";
	public static final String SITEMAP_XML           = "/sitemap.xml";
	
	public static final String SITE_EVENTS_PAGE      = "/site/events";
	
	public static final String REGISTRATION_PAGE     = "/account/register";
	
	public static final String AUTHENTICATION_PAGE   = "/account/auth";
	public static final String LOGIN_PAGE            = "/account/login";
	public static final String LOGOUT_PAGE           = "/account/logout";
	
	public static final String ACTIVATE_ACCOUNT_PAGE = "/account/activate";
	public static final String ACTIVATE_ACCOUNT_PAGE_WITH_KEY = "/account/activate/key/{key}";
	
	// CheckStyle: ignore LineLength for next 3 lines
	public static final String ADD_SERIES_PAGE               = "/series/add";
	public static final String ADD_SERIES_WITH_CATEGORY_PAGE = "/series/add/category/{id}";
	public static final String ADD_SERIES_WITH_COUNTRY_PAGE  = "/series/add/country/{id}";
	public static final String INFO_SERIES_PAGE              = "/series/{id}";
	public static final String ADD_IMAGE_SERIES_PAGE         = "/series/{id}/image";
	public static final String SEARCH_SERIES_BY_CATALOG      = "/series/search/by_catalog";
	
	public static final String ADD_CATEGORY_PAGE     = "/category/add";
	public static final String INFO_CATEGORY_PAGE    = "/category/{id}/{slug}";
	public static final String LIST_CATEGORIES_PAGE  = "/category/list";
	
	public static final String ADD_COUNTRY_PAGE      = "/country/add";
	public static final String LIST_COUNTRIES_PAGE   = "/country/list";
	public static final String INFO_COUNTRY_PAGE     = "/country/{slug}";
	// For backward compatibility
	public static final String INFO_COUNTRY_BY_ID_PAGE = "/country/{id}/{slug}";
	
	public static final String INFO_COLLECTION_PAGE  = "/collection/{id}/{slug}";
	
	public static final String GET_IMAGE_PAGE        = "/image/{id}";
	
	public static final String UNAUTHORIZED_PAGE     = "/error/401";
	public static final String FORBIDDEN_PAGE        = "/error/403";
	public static final String NOT_FOUND_PAGE        = "/error/404";
	public static final String INTERNAL_ERROR_PAGE   = "/error/500";
	
	// resources
	public static final String FAVICON_ICO            = "/favicon.ico";
	public static final String MAIN_CSS               = "/static/styles/main.min.css";
	public static final String CATALOG_UTILS_JS       = "/public/js/CatalogUtils.min.js";
	public static final String SERIES_ADD_JS          = "/public/js/series/add.min.js";
	public static final String COLLECTION_INFO_JS     = "/public/js/collection/info.min.js";
	
	public static final String BOOTSTRAP_CSS          = "/public/bootstrap/css/bootstrap.min.css";
	public static final String BOOTSTRAP_JS           = "/public/bootstrap/js/bootstrap.min.js";
	public static final String JQUERY_JS              = "/public/jquery/jquery.min.js";
	// CheckStyle: ignore LineLength for next 1 lines
	public static final String BOOTSTRAP_LANGUAGE     = "https://cdn.rawgit.com/usrz/bootstrap-languages/3ac2a3d2b27ac43a471cd99e79d378a03b2c6b5f/languages.min.css";
	
	// CheckStyle: ignore LineLength for next 3 lines
	// TODO: use minimal version of CSS file when it will be available (https://github.com/webjars/selectize.js/issues/3)
	public static final String SELECTIZE_CSS          = "/public/selectize/css/selectize.bootstrap3.css";
	public static final String SELECTIZE_JS           = "/public/selectize/js/standalone/selectize.min.js";
	
	// see also pom.xml and ru.mystamps.web.config.MvcConfig#addResourceHandlers()
	// CheckStyle: ignore LineLength for next 5 lines
	public static final String BOOTSTRAP_CSS_CDN     = "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css";
	public static final String BOOTSTRAP_JS_CDN      = "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js";
	public static final String JQUERY_JS_CDN         = "https://yandex.st/jquery/1.9.1/jquery.min.js";
	public static final String SELECTIZE_CSS_CDN     = "https://cdnjs.cloudflare.com/ajax/libs/selectize.js/0.12.1/css/selectize.bootstrap3.min.css";
	public static final String SELECTIZE_JS_CDN      = "https://cdnjs.cloudflare.com/ajax/libs/selectize.js/0.12.1/js/standalone/selectize.min.js";
	
	// see also ru.mystamps.web.support.togglz.TogglzConfig#getTogglzConsole()
	public static final String TOGGLZ_CONSOLE_PAGE    = "/togglz";
	
	private Url() {
	}
	
	public static Map<String, String> asMap(boolean serveContentFromSingleHost) {
		// There is not all urls but only those which used on views
		Map<String, String> map = new HashMap<>();
		map.put("PUBLIC_URL", PUBLIC_URL);
		map.put("AUTHENTICATION_PAGE", AUTHENTICATION_PAGE);
		map.put("LOGIN_PAGE", LOGIN_PAGE);
		map.put("LOGOUT_PAGE", LOGOUT_PAGE);
		map.put("ACTIVATE_ACCOUNT_PAGE", ACTIVATE_ACCOUNT_PAGE);
		map.put("REGISTRATION_PAGE", REGISTRATION_PAGE);
		map.put("ADD_SERIES_PAGE", ADD_SERIES_PAGE);
		map.put("ADD_SERIES_WITH_CATEGORY_PAGE", ADD_SERIES_WITH_CATEGORY_PAGE);
		map.put("ADD_SERIES_WITH_COUNTRY_PAGE", ADD_SERIES_WITH_COUNTRY_PAGE);
		map.put("INFO_SERIES_PAGE", INFO_SERIES_PAGE);
		map.put("ADD_IMAGE_SERIES_PAGE", ADD_IMAGE_SERIES_PAGE);
		map.put("SEARCH_SERIES_BY_CATALOG", SEARCH_SERIES_BY_CATALOG);
		map.put("ADD_CATEGORY_PAGE", ADD_CATEGORY_PAGE);
		map.put("INFO_CATEGORY_PAGE", INFO_CATEGORY_PAGE);
		map.put("LIST_CATEGORIES_PAGE", LIST_CATEGORIES_PAGE);
		map.put("ADD_COUNTRY_PAGE", ADD_COUNTRY_PAGE);
		map.put("INFO_COUNTRY_PAGE", INFO_COUNTRY_PAGE);
		map.put("LIST_COUNTRIES_PAGE", LIST_COUNTRIES_PAGE);
		map.put("INFO_COLLECTION_PAGE", INFO_COLLECTION_PAGE);
		map.put("SITE_EVENTS_PAGE", SITE_EVENTS_PAGE);
		map.put("BOOTSTRAP_LANGUAGE", BOOTSTRAP_LANGUAGE);
		
		if (serveContentFromSingleHost) {
			map.put("BOOTSTRAP_CSS", BOOTSTRAP_CSS);
			map.put("BOOTSTRAP_JS", BOOTSTRAP_JS);
			map.put("JQUERY_JS", JQUERY_JS);
			map.put("SELECTIZE_CSS", SELECTIZE_CSS);
			map.put("SELECTIZE_JS", SELECTIZE_JS);
			map.put("GET_IMAGE_PAGE", GET_IMAGE_PAGE);
			map.put("FAVICON_ICO", FAVICON_ICO);
			map.put("MAIN_CSS", MAIN_CSS);
			map.put("CATALOG_UTILS_JS", CATALOG_UTILS_JS);
			map.put("SERIES_ADD_JS", SERIES_ADD_JS);
			map.put("COLLECTION_INFO_JS", COLLECTION_INFO_JS);
		} else {
			// Use separate domain for our own resources
			map.put("GET_IMAGE_PAGE", STATIC_RESOURCES_URL + GET_IMAGE_PAGE);
			map.put("FAVICON_ICO", STATIC_RESOURCES_URL + FAVICON_ICO);
			map.put("MAIN_CSS", STATIC_RESOURCES_URL + MAIN_CSS);
			map.put("CATALOG_UTILS_JS", STATIC_RESOURCES_URL + CATALOG_UTILS_JS);
			map.put("SERIES_ADD_JS", STATIC_RESOURCES_URL + SERIES_ADD_JS);
			map.put("COLLECTION_INFO_JS", STATIC_RESOURCES_URL + COLLECTION_INFO_JS);
			
			// Use CDN for external resources like frameworks
			map.put("BOOTSTRAP_CSS", BOOTSTRAP_CSS_CDN);
			map.put("BOOTSTRAP_JS", BOOTSTRAP_JS_CDN);
			map.put("JQUERY_JS", JQUERY_JS_CDN);
			map.put("SELECTIZE_CSS", SELECTIZE_CSS_CDN);
			map.put("SELECTIZE_JS", SELECTIZE_JS_CDN);
		}
		
		return map;
	}
	
}
