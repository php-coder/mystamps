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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

package ru.mystamps.web;

/**
 * Holds path to site and all URLs.
 *
 * Should be used anywhere instead of hard-coded paths.
 *
 * @author Slava Semushin <slava.semushin@gmail.com>
 */
public final class Url {
	
	// defined at pom.xml (and used by functional tests only)
	public static final String SITE                  = "http://127.0.0.1:8081";
	
	public static final String INDEX_PAGE            = "/";
	public static final String SUCCESSFUL_REGISTRATION_PAGE = "/successful/registration";
	public static final String SUCCESSFUL_ACTIVATION_PAGE = "/successful/activation";
	
	public static final String REGISTRATION_PAGE     = "/account/register";
	
	// defined at src/main/resources/spring/security.xml
	public static final String AUTHENTICATION_PAGE   = "/account/auth";
	public static final String LOGIN_PAGE            = "/account/login";
	public static final String LOGOUT_PAGE           = "/account/logout";
	
	public static final String ACTIVATE_ACCOUNT_PAGE = "/account/activate";
	public static final String ACTIVATE_ACCOUNT_PAGE_WITH_KEY = "/account/activate/key/{key}";
	
	public static final String ADD_SERIES_PAGE       = "/series/add";
	public static final String INFO_SERIES_PAGE      = "/series/{id}";
	
	public static final String ADD_COUNTRY_PAGE      = "/country/add";
	public static final String INFO_COUNTRY_PAGE     = "/country/{id}";
	
	public static final String GET_IMAGE_PAGE        = "/image/{id}";
	
	// see also error-page definition at src/env/{dev,test}/WEB-INF/web.xml
	public static final String NOT_FOUND_PAGE        = "/error/404";
	
	private Url() {
	}
	
}
