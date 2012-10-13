/*
 * Copyright (C) 2009-2012 Slava Semushin <slava.semushin@gmail.com>
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

import org.apache.commons.lang3.Validate;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import ru.mystamps.web.entity.User;
import ru.mystamps.web.service.AuthService;

@Service
public class SpringSecurityAuthService implements AuthService {
	
	public User getCurrentUser() {
		final SecurityContext ctx = SecurityContextHolder.getContext();
		Validate.validState(ctx != null, "Security context must be non null");
		
		final Authentication auth = ctx.getAuthentication();
		if (auth == null) {
			return null;
		}
		
		final Object principal = auth.getPrincipal();
		if (principal == null) {
			return null;
		}
		
		Validate.validState(
			principal instanceof CustomUserDetails,
			"Principal must be CustomUserDetails type"
		);
		
		final CustomUserDetails userDetails = (CustomUserDetails)principal;
		
		return userDetails.getUser();
	}
	
}

