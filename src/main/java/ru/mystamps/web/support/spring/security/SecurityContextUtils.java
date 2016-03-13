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

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;

import ru.mystamps.web.entity.User;

public final class SecurityContextUtils {
	
	private SecurityContextUtils() {
	}
	
	public static boolean hasAuthority(HttpServletRequest request, String authority) {
		return new SecurityContextHolderAwareRequestWrapper(request, null).isUserInRole(authority);
	}
	
	public static User getUser() {
		return Optional
			.ofNullable(SecurityContextHolder.getContext().getAuthentication())
			.map(Authentication::getPrincipal)
			.filter(CustomUserDetails.class::isInstance)
			.map(CustomUserDetails.class::cast)
			.map(CustomUserDetails::getUser)
			.orElse(null);
	}
	
	/**
	 * @author Sergey Chechenev
	 * @author Slava Semushin
	 */
	public static Integer getUserId() {
		return Optional.ofNullable(getUser())
			.map(User::getId)
			.orElse(null);
	}
	
}
