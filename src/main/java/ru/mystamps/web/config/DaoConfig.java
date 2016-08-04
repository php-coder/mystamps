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
package ru.mystamps.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

// CheckStyle: ignore AvoidStarImportCheck for next 2 lines
import ru.mystamps.web.dao.*; // NOPMD: UnusedImports
import ru.mystamps.web.dao.impl.*; // NOPMD: UnusedImports

@Configuration
public class DaoConfig {
	
	@Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;
	
	@Bean
	public CategoryDao getCategoryDao() {
		return new JdbcCategoryDao(jdbcTemplate);
	}
	
	@Bean
	public CountryDao getCountryDao() {
		return new JdbcCountryDao(jdbcTemplate);
	}
	
	@Bean
	public CollectionDao getCollectionDao() {
		return new JdbcCollectionDao(jdbcTemplate);
	}
	
	@Bean
	public StampsCatalogDao getGibbonsCatalogDao() {
		return new JdbcGibbonsCatalogDao(jdbcTemplate);
	}
	
	@Bean
	public ImageDao getImageDao() {
		return new JdbcImageDao(jdbcTemplate);
	}
	
	@Bean
	public ImageDataDao getImageDataDao() {
		return new JdbcImageDataDao(jdbcTemplate);
	}
	
	@Bean
	public StampsCatalogDao getMichelCatalogDao() {
		return new JdbcMichelCatalogDao(jdbcTemplate);
	}
	
	@Bean
	public StampsCatalogDao getScottCatalogDao() {
		return new JdbcScottCatalogDao(jdbcTemplate);
	}
	
	@Bean
	public SeriesDao getSeriesDao() {
		return new JdbcSeriesDao(jdbcTemplate);
	}
	
	@Bean
	public UserDao getUserDao() {
		return new JdbcUserDao(jdbcTemplate);
	}
	
	@Bean
	public UsersActivationDao getUsersActivationDao() {
		return new JdbcUsersActivationDao(jdbcTemplate);
	}
	
	@Bean
	public SuspiciousActivityDao getSuspiciousActivityDao() {
		return new JdbcSuspiciousActivityDao(jdbcTemplate);
	}
	
	@Bean
	public StampsCatalogDao getYvertCatalogDao() {
		return new JdbcYvertCatalogDao(jdbcTemplate);
	}
	
}
