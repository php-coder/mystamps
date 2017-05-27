/*
 * Copyright (C) 2009-2017 Slava Semushin <slava.semushin@gmail.com>
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

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@SuppressWarnings("checkstyle:linelength")
public final class Authority {
	public static final GrantedAuthority ADD_COMMENTS_TO_SERIES = new SimpleGrantedAuthority(StringAuthority.ADD_COMMENTS_TO_SERIES);
	public static final GrantedAuthority ADD_IMAGES_TO_SERIES   = new SimpleGrantedAuthority(StringAuthority.ADD_IMAGES_TO_SERIES);
	public static final GrantedAuthority ADD_PARTICIPANT        = new SimpleGrantedAuthority(StringAuthority.ADD_PARTICIPANT);
	public static final GrantedAuthority ADD_SERIES_SALES       = new SimpleGrantedAuthority(StringAuthority.ADD_SERIES_SALES);
	public static final GrantedAuthority MANAGE_TOGGLZ          = new SimpleGrantedAuthority(StringAuthority.MANAGE_TOGGLZ);
	public static final GrantedAuthority CREATE_CATEGORY        = new SimpleGrantedAuthority(StringAuthority.CREATE_CATEGORY);
	public static final GrantedAuthority CREATE_COUNTRY         = new SimpleGrantedAuthority(StringAuthority.CREATE_COUNTRY);
	public static final GrantedAuthority CREATE_SERIES          = new SimpleGrantedAuthority(StringAuthority.CREATE_SERIES);
	public static final GrantedAuthority UPDATE_COLLECTION      = new SimpleGrantedAuthority(StringAuthority.UPDATE_COLLECTION);
	public static final GrantedAuthority VIEW_SITE_EVENTS       = new SimpleGrantedAuthority(StringAuthority.VIEW_SITE_EVENTS);
	public static final GrantedAuthority VIEW_SERIES_SALES      = new SimpleGrantedAuthority(StringAuthority.VIEW_SERIES_SALES);
	
	private Authority() {
	}
	
}
