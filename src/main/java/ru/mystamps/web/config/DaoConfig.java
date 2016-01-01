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
	public JdbcCategoryDao getJdbcCategoryDao() {
		return new JdbcCategoryDaoImpl(jdbcTemplate);
	}
	
	@Bean
	public JdbcCountryDao getJdbcCountryDao() {
		return new JdbcCountryDaoImpl(jdbcTemplate);
	}
	
	@Bean
	public JdbcCollectionDao getJdbcCollectionDao() {
		return new JdbcCollectionDaoImpl(jdbcTemplate);
	}
	
	@Bean
	public GibbonsCatalogDao getGibbonsCatalogDao() {
		return new JdbcGibbonsCatalogDaoImpl(jdbcTemplate);
	}
	
	@Bean
	public JdbcImageDao getJdbcImageDao() {
		return new JdbcImageDaoImpl(jdbcTemplate);
	}
	
	@Bean
	public MichelCatalogDao getMichelCatalogDao() {
		return new JdbcMichelCatalogDaoImpl(jdbcTemplate);
	}
	
	@Bean
	public ScottCatalogDao getScottCatalogDao() {
		return new JdbcScottCatalogDaoImpl(jdbcTemplate);
	}
	
	@Bean
	public JdbcSeriesDao getSeriesDao() {
		return new JdbcSeriesDaoImpl(jdbcTemplate);
	}
	
	@Bean
	public JdbcUserDao getJdbcUserDao() {
		return new JdbcUserDaoImpl(jdbcTemplate);
	}
	
	@Bean
	public JdbcUsersActivationDao getJdbcUsersActivationDao() {
		return new JdbcUsersActivationDaoImpl(jdbcTemplate);
	}
	
	@Bean
	public SuspiciousActivityDao getSuspiciousActivityDao() {
		return new JdbcSuspiciousActivityDao(jdbcTemplate);
	}
	
	@Bean
	public YvertCatalogDao getYvertCatalogDao() {
		return new JdbcYvertCatalogDaoImpl(jdbcTemplate);
	}
	
}
