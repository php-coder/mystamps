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
 * Site-related URLs.
 *
 * Should be used everywhere instead of hard-coded paths.
 *
 * @author Slava Semushin
 */
@SuppressWarnings("PMD.CommentDefaultAccessModifier")
public final class SiteUrl {
	
	public static final String PUBLIC_URL          = "https://my-stamps.ru";
	
	// see also robotframework-maven-plugin configuration at pom.xml
	public static final String SITE                = "http://127.0.0.1:8080";
	
	public static final String INDEX_PAGE          = "/";
	
	public static final String SITE_EVENTS_PAGE    = "/site/events";
	public static final String CSP_REPORTS_HANDLER = "/site/csp/reports";
	
	public static final String FORBIDDEN_PAGE      = "/error/403";
	public static final String NOT_FOUND_PAGE      = "/error/404";
	public static final String INTERNAL_ERROR_PAGE = "/error/500";
	
	static final String ROBOTS_TXT                 = "/robots.txt";
	static final String SITEMAP_XML                = "/sitemap.xml";
	
	private SiteUrl() {
	}
	
	public static void exposeUrlsToView(Map<String, String> urls) {
		urls.put("SITE_EVENTS_PAGE", SITE_EVENTS_PAGE);
	}
	
}
