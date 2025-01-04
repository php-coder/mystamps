/*
 * Copyright (C) 2009-2025 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.support.spring.security;

public final class HasAuthority {
	// Constants sorted in an ascending order.
	public static final String ADD_COMMENTS_TO_SERIES = "hasAuthority('" + StringAuthority.ADD_COMMENTS_TO_SERIES + "')";
	public static final String ADD_PARTICIPANT = "hasAuthority('" + StringAuthority.ADD_PARTICIPANT + "')";
	public static final String ADD_SERIES_PRICE_AND_COLLECTION_OWNER_OR_VIEW_ANY_ESTIMATION =
		"("
			+ "hasAuthority('" + StringAuthority.ADD_SERIES_PRICE + "') "
			+ "and "
			+ "principal?.userCollectionSlug == #slug"
		+ ") "
		+ "or "
		+ "hasAuthority('" + StringAuthority.VIEW_ANY_ESTIMATION + "')";
	public static final String ADD_SERIES_SALES = "hasAuthority('" + StringAuthority.ADD_SERIES_SALES + "')";
	public static final String CREATE_CATEGORY = "hasAuthority('" + StringAuthority.CREATE_CATEGORY + "')";
	public static final String CREATE_COUNTRY = "hasAuthority('" + StringAuthority.CREATE_COUNTRY + "')";
	public static final String CREATE_SERIES = "hasAuthority('" + StringAuthority.CREATE_SERIES + "')";
	public static final String DOWNLOAD_IMAGE = "hasAuthority('" + StringAuthority.DOWNLOAD_IMAGE + "')";
	public static final String HIDE_IMAGE = "hasAuthority('" + StringAuthority.HIDE_IMAGE + "')";
	public static final String IMPORT_SERIES = "hasAuthority('" + StringAuthority.IMPORT_SERIES + "')";
	public static final String MARK_SIMILAR_SERIES = "hasAuthority('" + StringAuthority.MARK_SIMILAR_SERIES + "')";
	public static final String REPLACE_IMAGE = "hasAuthority('" + StringAuthority.REPLACE_IMAGE + "')";
	public static final String UPDATE_COLLECTION = "hasAuthority('" + StringAuthority.UPDATE_COLLECTION + "')";
	public static final String VIEW_DAILY_STATS = "hasAuthority('" + StringAuthority.VIEW_DAILY_STATS + "')";
	public static final String VIEW_SERIES_SALES = "hasAuthority('" + StringAuthority.VIEW_SERIES_SALES + "')";
	public static final String VIEW_SITE_EVENTS = "hasAuthority('" + StringAuthority.VIEW_SITE_EVENTS + "')";
	
	private HasAuthority() {
	}
	
}
