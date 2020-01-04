/*
 * Copyright (C) 2009-2020 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.feature.series;

import java.util.Map;

/**
 * Series-related URLs.
 *
 * Should be used everywhere instead of hard-coded paths.
 *
 * @author Slava Semushin
 */
@SuppressWarnings("PMD.CommentDefaultAccessModifier")
public final class SeriesUrl {
	
	public static final String ADD_SERIES_PAGE          = "/series/add";
	public static final String ADD_SERIES_ASK_PAGE      = "/series/{id}/ask";
	public static final String INFO_SERIES_PAGE         = "/series/{id}";
	public static final String INFO_CATEGORY_PAGE       = "/category/{slug}";
	public static final String INFO_COUNTRY_PAGE        = "/country/{slug}";
	public static final String ADD_IMAGE_SERIES_PAGE    = "/series/{id}/image";
	public static final String SERIES_INFO_PAGE_REGEXP  = "/series/(\\d+|\\d+/(ask|image))";
	static final String SEARCH_SERIES_BY_CATALOG        = "/series/search/by_catalog";
	
	private SeriesUrl() {
	}
	
	public static void exposeUrlsToView(Map<String, String> urls) {
		urls.put("ADD_IMAGE_SERIES_PAGE", ADD_IMAGE_SERIES_PAGE);
		urls.put("ADD_SERIES_ASK_PAGE", ADD_SERIES_ASK_PAGE);
		urls.put("ADD_SERIES_PAGE", ADD_SERIES_PAGE);
		urls.put("INFO_CATEGORY_PAGE", INFO_CATEGORY_PAGE);
		urls.put("INFO_COUNTRY_PAGE", INFO_COUNTRY_PAGE);
		urls.put("INFO_SERIES_PAGE", INFO_SERIES_PAGE);
		urls.put("SEARCH_SERIES_BY_CATALOG", SEARCH_SERIES_BY_CATALOG);
	}
	
}
