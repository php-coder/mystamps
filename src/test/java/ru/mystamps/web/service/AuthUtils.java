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
package ru.mystamps.web.service;

import java.util.Collections;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

final class AuthUtils {
	
	private AuthUtils() {
	}
	
	static void authenticateAsAnonymous() {
		final Authentication authentication = new AnonymousAuthenticationToken(
			"anonymous",
			"anonymous",
			Collections.singletonList(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))
		);
		
		authenticate(authentication);
	}
	
	static void authenticateAsUser(final String login, final String password) {
		final Authentication authentication =
			new UsernamePasswordAuthenticationToken(login, password);
		
		authenticate(authentication);
	}
	
	private static void authenticate(final Authentication authentication) {
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
	
}
