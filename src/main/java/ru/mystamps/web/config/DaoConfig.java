/*
 * Copyright (C) 2009-2018 Slava Semushin <slava.semushin@gmail.com>
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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import lombok.RequiredArgsConstructor;

// CheckStyle: ignore AvoidStarImportCheck for next 2 lines
import ru.mystamps.web.dao.*; // NOPMD: UnusedImports
import ru.mystamps.web.dao.impl.*; // NOPMD: UnusedImports
import ru.mystamps.web.feature.category.JdbcCategoryDao;

@Configuration
@PropertySource("classpath:/sql/stamps_catalog_dao_queries.properties")
@RequiredArgsConstructor
public class DaoConfig {
	
	private final NamedParameterJdbcTemplate jdbcTemplate;
	private final Environment env;
	
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
		return new JdbcStampsCatalogDao(
			jdbcTemplate,
			env.getRequiredProperty("gibbons.create"),
			env.getRequiredProperty("series_gibbons.add"),
			env.getRequiredProperty("series_gibbons.find_by_series_id"),
			env.getRequiredProperty("series_gibbons.find_series_ids_by_number")
		);
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
		return new JdbcStampsCatalogDao(
			jdbcTemplate,
			env.getRequiredProperty("michel.create"),
			env.getRequiredProperty("series_michel.add"),
			env.getRequiredProperty("series_michel.find_by_series_id"),
			env.getRequiredProperty("series_michel.find_series_ids_by_number")
		);
	}
	
	@Bean
	public StampsCatalogDao getScottCatalogDao() {
		return new JdbcStampsCatalogDao(
			jdbcTemplate,
			env.getRequiredProperty("scott.create"),
			env.getRequiredProperty("series_scott.add"),
			env.getRequiredProperty("series_scott.find_by_series_id"),
			env.getRequiredProperty("series_scott.find_series_ids_by_number")
		);
	}
	
	@Bean
	public SeriesDao getSeriesDao() {
		return new JdbcSeriesDao(jdbcTemplate);
	}
	
	@Bean
	public SeriesImportDao getSeriesImportDao() {
		return new JdbcSeriesImportDao(jdbcTemplate);
	}
	
	@Bean
	public SeriesSalesImportDao getSeriesSalesImportDao() {
		return new JdbcSeriesSalesImportDao(jdbcTemplate);
	}
	
	@Bean
	public SeriesSalesDao getSeriesSalesDao() {
		return new JdbcSeriesSalesDao(jdbcTemplate);
	}
	
	@Bean
	public StampsCatalogDao getSolovyovCatalogDao() {
		return new JdbcStampsCatalogDao(
			jdbcTemplate,
			env.getRequiredProperty("solovyov.create"),
			env.getRequiredProperty("series_solovyov.add"),
			env.getRequiredProperty("series_solovyov.find_by_series_id"),
			env.getRequiredProperty("series_solovyov.find_series_ids_by_number")
		);
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
	public TransactionParticipantDao getTransactionParticipantDao() {
		return new JdbcTransactionParticipantDao(jdbcTemplate);
	}
	
	@Bean
	public StampsCatalogDao getYvertCatalogDao() {
		return new JdbcStampsCatalogDao(
			jdbcTemplate,
			env.getRequiredProperty("yvert.create"),
			env.getRequiredProperty("series_yvert.add"),
			env.getRequiredProperty("series_yvert.find_by_series_id"),
			env.getRequiredProperty("series_yvert.find_series_ids_by_number")
		);
	}
	
	@Bean
	public StampsCatalogDao getZagorskiCatalogDao() {
		return new JdbcStampsCatalogDao(
			jdbcTemplate,
			env.getRequiredProperty("zagorski.create"),
			env.getRequiredProperty("series_zagorski.add"),
			env.getRequiredProperty("series_zagorski.find_by_series_id"),
			env.getRequiredProperty("series_zagorski.find_series_ids_by_number")
		);
	}
	
}
