/*
 * Copyright (C) 2009-2022 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.feature.series;

import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import ru.mystamps.web.feature.category.CategoryService;
import ru.mystamps.web.feature.collection.CollectionService;
import ru.mystamps.web.feature.country.CountryService;
import ru.mystamps.web.feature.image.ImageService;
import ru.mystamps.web.feature.participant.ParticipantService;
import ru.mystamps.web.feature.series.importing.SeriesImportService;
import ru.mystamps.web.feature.series.sale.SeriesSalesService;

import java.util.Map;

/**
 * Spring configuration that is required for using series in an application.
 *
 * The beans are grouped into separate classes to make possible to register a controller
 * and the services in the separated application contexts.
 */
@Configuration
public class SeriesConfig {
	
	@RequiredArgsConstructor
	public static class Controllers {
		
		private final CategoryService categoryService;
		private final CollectionService collectionService;
		private final CountryService countryService;
		private final SeriesService seriesService;
		private final SeriesImageService seriesImageService;
		private final SeriesImportService seriesImportService;
		private final SeriesSalesService seriesSalesService;
		private final ParticipantService participantService;
		
		@Bean
		public SeriesController seriesController() {
			return new SeriesController(
				categoryService,
				collectionService,
				countryService,
				seriesService,
				seriesImportService,
				seriesSalesService,
				participantService
			);
		}
		
		@Bean
		public RestSeriesController restSeriesController() {
			return new RestSeriesController(seriesService, seriesImageService);
		}
		
	}
	
	@Import(Daos.class)
	@RequiredArgsConstructor
	public static class Services {
		
		private final SeriesImageDao seriesImageDao;
		private final ImageService imageService;
		private final Map<String, StampsCatalogDao> stampsCatalogDaos;
		
		@Bean
		public SeriesService seriesService(
			SeriesDao seriesDao,
			@Qualifier("michelCatalog") StampsCatalogService michelCatalogService,
			@Qualifier("scottCatalog") StampsCatalogService scottCatalogService,
			@Qualifier("yvertCatalog") StampsCatalogService yvertCatalogService,
			@Qualifier("gibbonsCatalog") StampsCatalogService gibbonsCatalogService,
			@Qualifier("solovyovCatalog") StampsCatalogService solovyovCatalogService,
			@Qualifier("zagorskiCatalog") StampsCatalogService zagorskiCatalogService) {
			
			return new SeriesServiceImpl(
				LoggerFactory.getLogger(SeriesServiceImpl.class),
				seriesDao,
				imageService,
				michelCatalogService,
				scottCatalogService,
				yvertCatalogService,
				gibbonsCatalogService,
				solovyovCatalogService,
				zagorskiCatalogService
			);
		}

		@Bean
		public SeriesImageService seriesImageService() {
			return new SeriesImageServiceImpl(
				LoggerFactory.getLogger(SeriesImageServiceImpl.class),
				seriesImageDao
			);
		}
		
		@Bean(name = "michelCatalog")
		public StampsCatalogService michelCatalogService() {
			return new StampsCatalogServiceImpl(
				LoggerFactory.getLogger(StampsCatalogServiceImpl.class),
				"Michel",
				stampsCatalogDaos.get("michelCatalogDao")
			);
		}
		
		@Bean(name = "scottCatalog")
		public StampsCatalogService scottCatalogService() {
			return new StampsCatalogServiceImpl(
				LoggerFactory.getLogger(StampsCatalogServiceImpl.class),
				"Scott",
				stampsCatalogDaos.get("scottCatalogDao")
			);
		}
		
		@Bean(name = "yvertCatalog")
		public StampsCatalogService yvertCatalogService() {
			return new StampsCatalogServiceImpl(
				LoggerFactory.getLogger(StampsCatalogServiceImpl.class),
				"Yvert",
				stampsCatalogDaos.get("yvertCatalogDao")
			);
		}
		
		@Bean(name = "gibbonsCatalog")
		public StampsCatalogService gibbonsCatalogService() {
			return new StampsCatalogServiceImpl(
				LoggerFactory.getLogger(StampsCatalogServiceImpl.class),
				"Gibbons",
				stampsCatalogDaos.get("gibbonsCatalogDao")
			);
		}
		
		@Bean(name = "solovyovCatalog")
		public StampsCatalogService solovyovCatalogService() {
			return new StampsCatalogServiceImpl(
				LoggerFactory.getLogger(StampsCatalogServiceImpl.class),
				"Solovyov",
				stampsCatalogDaos.get("solovyovCatalogDao")
			);
		}
		
		@Bean(name = "zagorskiCatalog")
		public StampsCatalogService zagorskiCatalogService() {
			return new StampsCatalogServiceImpl(
				LoggerFactory.getLogger(StampsCatalogServiceImpl.class),
				"Zagorski",
				stampsCatalogDaos.get("zagorskiCatalogDao")
			);
		}
		
	}
	
	@RequiredArgsConstructor
	@PropertySource({
		"classpath:sql/series_dao_queries.properties",
		"classpath:sql/series_image_dao_queries.properties",
		"classpath:/sql/stamps_catalog_dao_queries.properties"
	})
	/* default */ static class Daos {
		
		private final NamedParameterJdbcTemplate jdbcTemplate;
		private final Environment env;
		
		@Bean
		public SeriesDao seriesDao() {
			return new JdbcSeriesDao(env, jdbcTemplate);
		}
		
		@Bean
		public SeriesImageDao seriesImageDao() {
			return new JdbcSeriesImageDao(env, jdbcTemplate);
		}
		
		@Bean
		public StampsCatalogDao michelCatalogDao() {
			return new JdbcStampsCatalogDao(
				jdbcTemplate,
				env.getRequiredProperty("michel.create"),
				env.getRequiredProperty("series_michel.add"),
				env.getRequiredProperty("series_michel.find_by_series_id"),
				env.getRequiredProperty("series_michel.find_series_ids_by_number")
			);
		}
		
		@Bean
		public StampsCatalogDao scottCatalogDao() {
			return new JdbcStampsCatalogDao(
				jdbcTemplate,
				env.getRequiredProperty("scott.create"),
				env.getRequiredProperty("series_scott.add"),
				env.getRequiredProperty("series_scott.find_by_series_id"),
				env.getRequiredProperty("series_scott.find_series_ids_by_number")
			);
		}
		
		@Bean
		public StampsCatalogDao yvertCatalogDao() {
			return new JdbcStampsCatalogDao(
				jdbcTemplate,
				env.getRequiredProperty("yvert.create"),
				env.getRequiredProperty("series_yvert.add"),
				env.getRequiredProperty("series_yvert.find_by_series_id"),
				env.getRequiredProperty("series_yvert.find_series_ids_by_number")
			);
		}
		
		@Bean
		public StampsCatalogDao gibbonsCatalogDao() {
			return new JdbcStampsCatalogDao(
				jdbcTemplate,
				env.getRequiredProperty("gibbons.create"),
				env.getRequiredProperty("series_gibbons.add"),
				env.getRequiredProperty("series_gibbons.find_by_series_id"),
				env.getRequiredProperty("series_gibbons.find_series_ids_by_number")
			);
		}
		
		@Bean
		public StampsCatalogDao solovyovCatalogDao() {
			return new JdbcStampsCatalogDao(
				jdbcTemplate,
				env.getRequiredProperty("solovyov.create"),
				env.getRequiredProperty("series_solovyov.add"),
				env.getRequiredProperty("series_solovyov.find_by_series_id"),
				env.getRequiredProperty("series_solovyov.find_series_ids_by_number")
			);
		}
		
		@Bean
		public StampsCatalogDao zagorskiCatalogDao() {
			return new JdbcStampsCatalogDao(
				jdbcTemplate,
				env.getRequiredProperty("zagorski.create"),
				env.getRequiredProperty("series_zagorski.add"),
				env.getRequiredProperty("series_zagorski.find_by_series_id"),
				env.getRequiredProperty("series_zagorski.find_series_ids_by_number")
			);
		}
		
	}
	
}
