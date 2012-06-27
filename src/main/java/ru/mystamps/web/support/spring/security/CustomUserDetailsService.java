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

import static com.google.common.base.Preconditions.checkArgument;

import ru.mystamps.web.entity.User;
import ru.mystamps.web.service.UserService;

/**
 * Implementation of Spring's {@link UserDetailsService} which uses our DAO to load user.
 */
public class CustomUserDetailsService implements UserDetailsService {
	
	private final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);
	
	@Inject
	private UserService userService;
	
	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(final String login) {
		checkArgument(login != null, "Login should be non null");
		
		log.debug("Find user by login '{}'", login);
		
		final User user = userService.findByLogin(login);
		if (user == null) {
			log.debug("User '{}' not found", login);
			throw new UsernameNotFoundException("User not found");
		}
		
		log.debug("User '{}' found", login);
		
		return new CustomUserDetails(user, getAuthorities());
	}
	
	private static Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
	}
	
}
