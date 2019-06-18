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
package ru.mystamps.web.feature.image;

import java.util.Map;

/**
 * Image-related URLs.
 *
 * Should be used everywhere instead of hard-coded paths.
 *
 * @author Slava Semushin
 */
@SuppressWarnings("PMD.CommentDefaultAccessModifier")
public final class ImageUrl {
	
	static final String GET_IMAGE_PAGE         = "/image/{id}";
	static final String GET_IMAGE_PREVIEW_PAGE = "/image/preview/{id}";
	
	private ImageUrl() {
	}
	
	public static void exposeResourcesToView(Map<String, String> resources, String host) {
		put(resources, host, "GET_IMAGE_PAGE", GET_IMAGE_PAGE);
		put(resources, host, "GET_IMAGE_PREVIEW_PAGE", GET_IMAGE_PREVIEW_PAGE);
	}
	
	private static void put(Map<String, String> map, String valuePrefix, String key, String value) {
		if (valuePrefix == null) {
			map.put(key, value);
			return;
		}
		
		map.put(key, valuePrefix + value);
	}
	
}
