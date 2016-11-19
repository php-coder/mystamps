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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.dao.dto.UserDetails;
import ru.mystamps.web.service.UserService;

/**
 * Implementation of Spring's {@link UserDetailsService} which uses our DAO to load user.
 */
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
	
	private static final Logger LOG = LoggerFactory.getLogger(CustomUserDetailsService.class);
	
	private final UserService userService;
	
	// CheckStyle: ignore LineLength for next 3 lines
	@Override
	@Transactional(readOnly = true)
	public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String login) {
		Validate.isTrue(login != null, "Login must be non null");
		
		LOG.debug("Find user by login '{}'", login);
		
		UserDetails userDetails = userService.findUserDetailsByLogin(login);
		if (userDetails == null) {
			LOG.debug("User '{}' not found", login);
			throw new UsernameNotFoundException("User not found");
		}
		
		LOG.debug("User '{}' found", login);
		
		return new CustomUserDetails(userDetails, getAuthorities(userDetails));
	}
	
	private static Collection<? extends GrantedAuthority> getAuthorities(UserDetails userDetails) {
		List<GrantedAuthority> authorities = new LinkedList<>();
		authorities.add(Authority.CREATE_CATEGORY);
		authorities.add(Authority.CREATE_COUNTRY);
		authorities.add(Authority.CREATE_SERIES);
		authorities.add(Authority.UPDATE_COLLECTION);
		
		if (userDetails.isAdmin()) {
			authorities.add(Authority.ADD_COMMENTS_TO_SERIES);
			authorities.add(Authority.ADD_IMAGES_TO_SERIES);
			authorities.add(Authority.VIEW_SITE_EVENTS);
			authorities.add(Authority.ADD_SERIES_SALES);
			authorities.add(Authority.VIEW_SERIES_SALES);
			authorities.add(Authority.MANAGE_TOGGLZ);
			authorities.add(Authority.VIEW_DAILY_STATS);
		}
		
		return authorities;
	}
	
}
