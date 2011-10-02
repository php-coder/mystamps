/*
 * Copyright (C) 2009-2011 Slava Semushin <slava.semushin@gmail.com>
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

package ru.mystamps.web;

/**
 * Site map (holds path to site and all URLs).
 *
 * Should be statically imported and used anywhere instead of hard-coded paths.
 *
 * @author Slava Semushin <slava.semushin@gmail.com>
 * @since 2010-10-24
 */
public final class SiteMap {
	
	// defined at pom.xml (and used by functional tests only)
	public static final String SITE_URL                  = "http://127.0.0.1:8081";
	
	// defined at ru.mystamps.web.config.MvcConfig
	public static final String INDEX_PAGE_URL            = "/";
	public static final String MAINTENANCE_PAGE_URL      = "/site/maintenance";
	public static final String SUCCESSFUL_REGISTRATION_PAGE_URL = "/successful/registration";
	public static final String SUCCESSFUL_ACTIVATION_PAGE_URL = "/successful/activation";
	
	public static final String REGISTRATION_PAGE_URL     = "/account/register";
	public static final String AUTHENTICATION_PAGE_URL   = "/account/auth";
	public static final String ACTIVATE_ACCOUNT_PAGE_URL = "/account/activate";
	
	public static final String
		ACTIVATE_ACCOUNT_PAGE_WITH_KEY_URL = "/account/activate/key/{key}";
	
	public static final String LOGOUT_PAGE_URL           = "/account/logout";
	
	public static final String ADD_STAMPS_PAGE_URL       = "/stamps/add";
	
	public static final String ADD_COUNTRY_PAGE_URL      = "/country/add";
	public static final String INFO_COUNTRY_PAGE_URL     = "/country/{id}";
	
	// defined at ru.mystamps.web.config.MvcConfig
	public static final String RESTORE_PASSWORD_PAGE_URL = "/password/restore";
	
	// see also error-page definition at src/main/webapp/WEB-INF/web.xml
	public static final String NOT_FOUND_PAGE_URL        = "/error/404";
	
	private SiteMap() {
	}
	
}
