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
package ru.mystamps.web.support.spring.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import ru.mystamps.web.feature.account.UserDetails;

import java.util.Collection;

@Getter
public class CustomUserDetails extends org.springframework.security.core.userdetails.User {
	
	private final Integer userId;
	
	// used in controllers for getting info about current user
	private final String userName;
	private final String userCollectionSlug;
	
	public CustomUserDetails(
		UserDetails userDetails,
		Collection<? extends GrantedAuthority> authorities) {
		
		super(userDetails.getLogin(), userDetails.getHash(), authorities);
		this.userId = userDetails.getId();
		this.userName = userDetails.getName();
		this.userCollectionSlug = userDetails.getCollectionSlug();
	}
	
}
