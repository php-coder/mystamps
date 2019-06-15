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
package ru.mystamps.web.feature.series.importing;

import java.util.Map;

/**
 * URLs related to the series import.
 *
 * Should be used everywhere instead of hard-coded paths.
 *
 * @author Slava Semushin
 */
@SuppressWarnings("PMD.CommentDefaultAccessModifier")
public final class SeriesImportUrl {
	
	public static final String REQUEST_IMPORT_SERIES_PAGE = "/series/import/request";
	public static final String REQUEST_IMPORT_PAGE        = "/series/import/request/{id}";
	static final String LIST_IMPORT_REQUESTS_PAGE         = "/series/import/requests";
	
	private SeriesImportUrl() {
	}
	
	public static void exposeUrlsToView(Map<String, String> urls) {
		urls.put("LIST_IMPORT_REQUESTS_PAGE", LIST_IMPORT_REQUESTS_PAGE);
		urls.put("REQUEST_IMPORT_PAGE", REQUEST_IMPORT_PAGE);
		urls.put("REQUEST_IMPORT_SERIES_PAGE", REQUEST_IMPORT_SERIES_PAGE);
	}
	
}
