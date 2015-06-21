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
package ru.mystamps.web;

import java.util.HashMap;
import java.util.Map;

import ru.mystamps.web.service.ImageService;

/**
 * Holds path to site and all URLs.
 *
 * Should be used anywhere instead of hard-coded paths.
 *
 * @author Slava Semushin
 */
public final class Url {
	public static final String PUBLIC_URL            = "http://my-stamps.ru";
	
	// defined at pom.xml (and used by functional tests only)
	public static final String SITE                  = "http://127.0.0.1:8080";
	
	public static final String INDEX_PAGE            = "/";
	public static final String ROBOTS_TXT            = "/robots.txt";
	public static final String SITEMAP_XML           = "/sitemap.xml";
	
	public static final String REGISTRATION_PAGE     = "/account/register";
	
	public static final String AUTHENTICATION_PAGE   = "/account/auth";
	public static final String LOGIN_PAGE            = "/account/login";
	public static final String LOGOUT_PAGE           = "/account/logout";
	
	public static final String ACTIVATE_ACCOUNT_PAGE = "/account/activate";
	public static final String ACTIVATE_ACCOUNT_PAGE_WITH_KEY = "/account/activate/key/{key}";
	
	public static final String ADD_SERIES_PAGE       = "/series/add";
	public static final String INFO_SERIES_PAGE      = "/series/{id}";
	
	public static final String ADD_CATEGORY_PAGE     = "/category/add";
	public static final String INFO_CATEGORY_PAGE    = "/category/{id}/{slug}";
	
	public static final String ADD_COUNTRY_PAGE      = "/country/add";
	public static final String INFO_COUNTRY_PAGE     = "/country/{id}/{slug}";
	
	public static final String INFO_COLLECTION_PAGE  = "/collection/{id}/{slug}";
	
	public static final String GET_IMAGE_PAGE        = ImageService.GET_IMAGE_PAGE;
	
	// see also error-page definitions at src/main/webapp/WEB-INF/web.xml
	public static final String UNAUTHORIZED_PAGE     = "/error/401";
	public static final String FORBIDDEN_PAGE        = "/error/403";
	public static final String NOT_FOUND_PAGE        = "/error/404";
	public static final String INTERNAL_ERROR_PAGE   = "/error/500";
	
	// resources
	public static final String FAVICON_ICO            = "/favicon.ico";
	public static final String MAIN_CSS               = "/static/styles/main.css";
	public static final String BOOTSTRAP_CSS          = "/public/bootstrap/css/bootstrap.min.css";
	public static final String BOOTSTRAP_RESPONSIVE_CSS = "/public/bootstrap/css/bootstrap-responsive.min.css";
	public static final String BOOTSTRAP_JS           = "/public/bootstrap/js/bootstrap.min.js";
	public static final String JQUERY_JS              = "/public/jquery/jquery.min.js";
	public static final String CATALOG_UTILS_JS       = "/public/js/CatalogUtils.js";
	
	public static final String TOGGLZ_CONSOLE_PAGE    = "/togglz";
	
	private Url() {
	}
	
	public static Map<String, String> asMap() {
		// There is not all urls but only those which used on views
		Map<String, String> map = new HashMap<>();
		map.put("PUBLIC_URL", PUBLIC_URL);
		map.put("AUTHENTICATION_PAGE", AUTHENTICATION_PAGE);
		map.put("LOGIN_PAGE", LOGIN_PAGE);
		map.put("LOGOUT_PAGE", LOGOUT_PAGE);
		map.put("ACTIVATE_ACCOUNT_PAGE", ACTIVATE_ACCOUNT_PAGE);
		map.put("REGISTRATION_PAGE", REGISTRATION_PAGE);
		map.put("ADD_SERIES_PAGE", ADD_SERIES_PAGE);
		map.put("INFO_SERIES_PAGE", INFO_SERIES_PAGE);
		map.put("ADD_CATEGORY_PAGE", ADD_CATEGORY_PAGE);
		map.put("INFO_CATEGORY_PAGE", INFO_CATEGORY_PAGE);
		map.put("ADD_COUNTRY_PAGE", ADD_COUNTRY_PAGE);
		map.put("INFO_COUNTRY_PAGE", INFO_COUNTRY_PAGE);
		map.put("INFO_COLLECTION_PAGE", INFO_COLLECTION_PAGE);
		map.put("FAVICON_ICO", FAVICON_ICO);
		map.put("MAIN_CSS", MAIN_CSS);
		map.put("BOOTSTRAP_CSS", BOOTSTRAP_CSS);
		map.put("BOOTSTRAP_RESPONSIVE_CSS", BOOTSTRAP_RESPONSIVE_CSS);
		map.put("BOOTSTRAP_JS", BOOTSTRAP_JS);
		map.put("JQUERY_JS", JQUERY_JS);
		map.put("CATALOG_UTILS_JS", CATALOG_UTILS_JS);
		return map;
	}
	
}
