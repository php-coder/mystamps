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
package ru.mystamps.web.feature.category;

import java.util.Map;

/**
 * Category-related URLs.
 *
 * Should be used everywhere instead of hard-coded paths.
 *
 * @author Slava Semushin
 */
@SuppressWarnings("PMD.CommentDefaultAccessModifier")
public final class CategoryUrl {
	
	public static final String SUGGEST_SERIES_CATEGORY = "/suggest/series_category";
	public static final String ADD_CATEGORY_PAGE       = "/category/add";
	static final String GET_CATEGORIES_PAGE            = "/categories";
	
	private CategoryUrl() {
	}
	
	public static void exposeUrlsToView(Map<String, String> urls) {
		urls.put("ADD_CATEGORY_PAGE", ADD_CATEGORY_PAGE);
		urls.put("GET_CATEGORIES_PAGE", GET_CATEGORIES_PAGE);
		urls.put("SUGGEST_SERIES_CATEGORY", SUGGEST_SERIES_CATEGORY);
	}
	
}
