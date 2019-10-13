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
package ru.mystamps.web.feature.collection;

import java.util.Map;

/**
 * Collection-related URLs.
 *
 * Should be used everywhere instead of hard-coded paths.
 *
 * @author Slava Semushin
 */
public final class CollectionUrl {
	
	public static final String INFO_COLLECTION_PAGE       = "/collection/{slug}";
	public static final String ESTIMATION_COLLECTION_PAGE = "/collection/{slug}/estimation";
	
	private CollectionUrl() {
	}
	
	public static void exposeUrlsToView(Map<String, String> urls) {
		urls.put("ESTIMATION_COLLECTION_PAGE", ESTIMATION_COLLECTION_PAGE);
		urls.put("INFO_COLLECTION_PAGE", INFO_COLLECTION_PAGE);
	}
	
}
