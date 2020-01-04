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
package ru.mystamps.web.feature.account;

import java.util.Map;

/**
 * Account-related URLs.
 *
 * Should be used everywhere instead of hard-coded paths.
 *
 * @author Slava Semushin
 */
@SuppressWarnings("PMD.CommentDefaultAccessModifier")
public final class AccountUrl {
	
	public static final String REGISTRATION_PAGE     = "/account/register";
	public static final String AUTHENTICATION_PAGE   = "/account/auth";
	public static final String LOGIN_PAGE            = "/account/login";
	public static final String LOGOUT_PAGE           = "/account/logout";
	public static final String ACTIVATE_ACCOUNT_PAGE = "/account/activate";
	
	private AccountUrl() {
	}
	
	public static void exposeUrlsToView(Map<String, String> urls) {
		urls.put("ACTIVATE_ACCOUNT_PAGE", ACTIVATE_ACCOUNT_PAGE);
		urls.put("AUTHENTICATION_PAGE", AUTHENTICATION_PAGE);
		urls.put("LOGIN_PAGE", LOGIN_PAGE);
		urls.put("LOGOUT_PAGE", LOGOUT_PAGE);
		urls.put("REGISTRATION_PAGE", REGISTRATION_PAGE);
	}
	
}
