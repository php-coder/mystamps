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
package ru.mystamps.web.config;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ru.mystamps.web.dao.JdbcCategoryDao;
import ru.mystamps.web.dao.JdbcCollectionDao;
import ru.mystamps.web.dao.JdbcCountryDao;
import ru.mystamps.web.dao.JdbcUserDao;
import ru.mystamps.web.dao.JdbcSeriesDao;
import ru.mystamps.web.dao.JdbcUsersActivationDao;
import ru.mystamps.web.dao.SuspiciousActivityDao;
import ru.mystamps.web.dao.impl.JdbcCategoryDaoImpl;
import ru.mystamps.web.dao.impl.JdbcCollectionDaoImpl;
import ru.mystamps.web.dao.impl.JdbcCountryDaoImpl;
import ru.mystamps.web.dao.impl.JdbcSuspiciousActivityDao;
import ru.mystamps.web.dao.impl.JdbcUserDaoImpl;
import ru.mystamps.web.dao.impl.JdbcSeriesDaoImpl;
import ru.mystamps.web.dao.impl.JdbcUsersActivationDaoImpl;

@Configuration
public class DaoConfig {
	
	@Inject
	private DataSource dataSource;
	
	@Bean
	public JdbcCategoryDao getJdbcCategoryDao() {
		return new JdbcCategoryDaoImpl(dataSource);
	}
	
	@Bean
	public JdbcCountryDao getJdbcCountryDao() {
		return new JdbcCountryDaoImpl(dataSource);
	}
	
	@Bean
	public JdbcCollectionDao getJdbcCollectionDao() {
		return new JdbcCollectionDaoImpl(dataSource);
	}
	
	@Bean
	public JdbcSeriesDao getJdbcSeriesDao() {
		return new JdbcSeriesDaoImpl(dataSource);
	}
	
	@Bean
	public JdbcUserDao getJdbcUserDao() {
		return new JdbcUserDaoImpl(dataSource);
	}
	
	@Bean
	public JdbcUsersActivationDao getJdbcUsersActivationDao() {
		return new JdbcUsersActivationDaoImpl(dataSource);
	}
	
	@Bean
	public SuspiciousActivityDao getSuspiciousActivityDao() {
		return new JdbcSuspiciousActivityDao(dataSource);
	}
	
}
