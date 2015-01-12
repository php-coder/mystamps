/*
 * Copyright (C) 2009-2015 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.support.togglz;

import javax.sql.DataSource;

import org.togglz.core.Feature;
import org.togglz.core.manager.TogglzConfig;
import org.togglz.core.repository.StateRepository;
import org.togglz.core.repository.cache.CachingStateRepository;
import org.togglz.core.repository.jdbc.JDBCStateRepository;
import org.togglz.core.user.UserProvider;
import org.togglz.spring.security.SpringSecurityUserProvider;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FeatureConfig implements TogglzConfig {
	
	private final DataSource dataSource;
	
	@Override
	public Class<? extends Feature> getFeatureClass() {
		return Features.class;
	}
	
	@Override
	public StateRepository getStateRepository() {
		return new CachingStateRepository(new JDBCStateRepository(dataSource));
	}
	
	@Override
	public UserProvider getUserProvider() {
		return new SpringSecurityUserProvider("CHANGE_FEATURES");
	}
	
}
