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
package ru.mystamps.web.feature.category;

/**
 * Category-related URLs.
 *
 * Should be used everywhere instead of hard-coded paths.
 *
 * @author Slava Semushin
 */
@SuppressWarnings("PMD.CommentDefaultAccessModifier")
public final class CategoryUrl {
	
	public static final String ADD_CATEGORY_PAGE   = "/category/add";
	public static final String GET_CATEGORIES_PAGE = "/categories";
	public static final String INFO_CATEGORY_PAGE  = "/category/{slug}";
	
	// For backward compatibility
	static final String LIST_CATEGORIES_PAGE       = "/category/list";
	static final String INFO_CATEGORY_BY_ID_PAGE   = "/category/{id}/{slug}";
	
	private CategoryUrl() {
	}
	
}
