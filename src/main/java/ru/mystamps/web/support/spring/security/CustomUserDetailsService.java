/*
 * Copyright (C) 2009-2025 Slava Semushin <slava.semushin@gmail.com>
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

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import ru.mystamps.web.feature.account.UserDetails;
import ru.mystamps.web.feature.account.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Implementation of Spring's {@link UserDetailsService} which uses our DAO to load user.
 */
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
	
	private static final Logger LOG = LoggerFactory.getLogger(CustomUserDetailsService.class);
	
	private final UserService userService;
	
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
		// Constants sorted in an ascending order.
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(Authority.ADD_COMMENTS_TO_SERIES);
		authorities.add(Authority.CREATE_CATEGORY);
		authorities.add(Authority.CREATE_COUNTRY);
		authorities.add(Authority.CREATE_SERIES);
		authorities.add(Authority.UPDATE_COLLECTION);
		
		if (userDetails.isAdmin()) {
			// Constants sorted in an ascending order.
			authorities.add(Authority.ADD_IMAGES_TO_SERIES);
			authorities.add(Authority.ADD_PARTICIPANT);
			authorities.add(Authority.ADD_SERIES_PRICE);
			authorities.add(Authority.ADD_SERIES_SALES);
			authorities.add(Authority.DOWNLOAD_IMAGE);
			authorities.add(Authority.HIDE_IMAGE);
			authorities.add(Authority.IMPORT_SERIES);
			authorities.add(Authority.IMPORT_SERIES_SALES);
			authorities.add(Authority.MANAGE_TOGGLZ);
			authorities.add(Authority.MARK_SIMILAR_SERIES);
			authorities.add(Authority.REPLACE_IMAGE);
			authorities.add(Authority.VIEW_ANY_ESTIMATION);
			authorities.add(Authority.VIEW_DAILY_STATS);
			authorities.add(Authority.VIEW_HIDDEN_IMAGES);
			authorities.add(Authority.VIEW_SERIES_SALES);
			authorities.add(Authority.VIEW_SITE_EVENTS);
		
		} else if (userDetails.isPaidUser()) {
			authorities.add(Authority.ADD_SERIES_PRICE);
		}
		
		return authorities;
	}
	
}
