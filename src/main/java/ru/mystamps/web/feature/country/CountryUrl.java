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
package ru.mystamps.web.feature.country;

import java.util.Map;

/**
 * Country-related URLs.
 *
 * Should be used everywhere instead of hard-coded paths.
 *
 * @author Slava Semushin
 */
@SuppressWarnings("PMD.CommentDefaultAccessModifier")
public final class CountryUrl {
	
	public static final String SUGGEST_SERIES_COUNTRY = "/suggest/series_country";
	public static final String ADD_COUNTRY_PAGE       = "/country/add";
	static final String GET_COUNTRIES_PAGE            = "/countries";
	
	private CountryUrl() {
	}
	
	public static void exposeUrlsToView(Map<String, String> urls) {
		urls.put("ADD_COUNTRY_PAGE", ADD_COUNTRY_PAGE);
		urls.put("GET_COUNTRIES_PAGE", GET_COUNTRIES_PAGE);
		urls.put("SUGGEST_SERIES_COUNTRY", SUGGEST_SERIES_COUNTRY);
	}
	
}
