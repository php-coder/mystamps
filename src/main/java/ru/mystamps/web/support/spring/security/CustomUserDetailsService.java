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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.Validate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.entity.User;
import ru.mystamps.web.service.UserService;

/**
 * Implementation of Spring's {@link UserDetailsService} which uses our DAO to load user.
 */
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
	
	private static final Logger LOG = LoggerFactory.getLogger(CustomUserDetailsService.class);
	
	private final UserService userService;
	
	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String login) {
		Validate.isTrue(login != null, "Login should be non null");
		
		LOG.debug("Find user by login '{}'", login);
		
		User user = userService.findByLogin(login);
		if (user == null) {
			LOG.debug("User '{}' not found", login);
			throw new UsernameNotFoundException("User not found");
		}
		
		LOG.debug("User '{}' found", login);
		
		return new CustomUserDetails(user, getAuthorities(user));
	}
	
	private static Collection<? extends GrantedAuthority> getAuthorities(User user) {
		List<SimpleGrantedAuthority> authorities = new LinkedList<>();
		authorities.add(new SimpleGrantedAuthority("CREATE_SERIES"));
		authorities.add(new SimpleGrantedAuthority("UPDATE_COLLECTION"));
		authorities.add(new SimpleGrantedAuthority("ADD_IMAGES_TO_SERIES"));
		
		if (user.isAdmin()) {
			authorities.add(new SimpleGrantedAuthority("CREATE_CATEGORY"));
			authorities.add(new SimpleGrantedAuthority("CREATE_COUNTRY"));
			authorities.add(new SimpleGrantedAuthority("ADD_COMMENTS_TO_SERIES"));
			authorities.add(new SimpleGrantedAuthority("VIEW_SITE_EVENTS"));
			
			// gives access to Togglz web console
			authorities.add(new SimpleGrantedAuthority("CHANGE_FEATURES"));
		}
		
		return authorities;
	}
	
}
