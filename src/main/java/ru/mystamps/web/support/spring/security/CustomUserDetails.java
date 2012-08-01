/*
 * Copyright (C) 2012 Slava Semushin <slava.semushin@gmail.com>
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

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

import ru.mystamps.web.entity.User;

public class CustomUserDetails extends org.springframework.security.core.userdetails.User {
	private final User user;
	
	public CustomUserDetails(
		final User user,
		final Collection<? extends GrantedAuthority> authorities) {
		
		super(user.getLogin(), user.getHash(), authorities);
		this.user = user;
	}
	
	// used at least by NotFoundErrorController to getting and logging current user id
	public Integer getId() {
		return user.getId();
	}
	
	// used for showing user name at interface
	public String getName() {
		return user.getName();
	}
	
	// used during authentication by password-encoder with salt-source
	public String getSalt() {
		return user.getSalt();
	}
	
}
