/*
 * Copyright (C) 2009-2016 Slava Semushin <slava.semushin@gmail.com>
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

@SuppressWarnings({ "checkstyle:linelength", "PMD.AvoidDuplicateLiterals" })
public final class HasAuthority {
	public static final String CREATE_SERIES = "hasAuthority('" + StringAuthority.CREATE_SERIES + "')";
	public static final String CREATE_CATEGORY = "hasAuthority('" + StringAuthority.CREATE_CATEGORY + "')";
	public static final String CREATE_COUNTRY = "hasAuthority('" + StringAuthority.CREATE_COUNTRY + "')";
	public static final String UPDATE_COLLECTION = "hasAuthority('" + StringAuthority.UPDATE_COLLECTION + "')";
	public static final String VIEW_SITE_EVENTS = "hasAuthority('" + StringAuthority.VIEW_SITE_EVENTS + "')";
	public static final String VIEW_SERIES_SALES = "hasAuthority('" + StringAuthority.VIEW_SERIES_SALES + "')";
	
	private HasAuthority() {
	}
	
}
