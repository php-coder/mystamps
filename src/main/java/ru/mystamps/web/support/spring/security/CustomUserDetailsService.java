/*
 * Copyright (C) 2009-2013 Slava Semushin <slava.semushin@gmail.com>
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
import java.util.Collections;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import org.apache.commons.lang3.Validate;

import ru.mystamps.web.entity.User;
import ru.mystamps.web.service.UserService;

/**
 * Implementation of Spring's {@link UserDetailsService} which uses our DAO to load user.
 */
public class CustomUserDetailsService implements UserDetailsService {
	
	private static final Logger LOG = LoggerFactory.getLogger(CustomUserDetailsService.class);
	
	private final UserService userService;
	
	@Inject
	public CustomUserDetailsService(UserService userService) {
		this.userService = userService;
	}
	
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
		
		return new CustomUserDetails(user, getAuthorities());
	}
	
	private static Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
	}
	
}
