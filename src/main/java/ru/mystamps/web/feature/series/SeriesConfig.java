/*
 * Copyright (C) 2009-2019 Slava Semushin <slava.semushin@gmail.com>
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
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import ru.mystamps.web.feature.category.CategoryService;
import ru.mystamps.web.feature.collection.CollectionService;
import ru.mystamps.web.feature.country.CountryService;
import ru.mystamps.web.feature.image.ImageService;
import ru.mystamps.web.feature.participant.ParticipantService;
import ru.mystamps.web.feature.series.importing.SeriesImportService;
import ru.mystamps.web.feature.series.sale.SeriesSalesService;

/**
 * Spring configuration that is required for using series in an application.
 *
 * The beans are grouped into two classes to make possible to register a controller
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
		
	}
	
	public static class Services {
		
		private final ImageService imageService;
		private final StampsCatalogService michelCatalogService;
		private final StampsCatalogService scottCatalogService;
		private final StampsCatalogService yvertCatalogService;
		private final StampsCatalogService gibbonsCatalogService;
		private final StampsCatalogService solovyovCatalogService;
		private final StampsCatalogService zagorskiCatalogService;
		private final NamedParameterJdbcTemplate jdbcTemplate;
		
		@SuppressWarnings("checkstyle:parameternumber")
		public Services(
			ImageService imageService,
			@Lazy @Qualifier("michelCatalog") StampsCatalogService michelCatalogService,
			@Lazy @Qualifier("scottCatalog") StampsCatalogService scottCatalogService,
			@Lazy @Qualifier("yvertCatalog") StampsCatalogService yvertCatalogService,
			@Lazy @Qualifier("gibbonsCatalog") StampsCatalogService gibbonsCatalogService,
			@Lazy @Qualifier("solovyovCatalog") StampsCatalogService solovyovCatalogService,
			@Lazy @Qualifier("zagorskiCatalog") StampsCatalogService zagorskiCatalogService,
			NamedParameterJdbcTemplate jdbcTemplate
		) {
			this.imageService = imageService;
			this.michelCatalogService = michelCatalogService;
			this.scottCatalogService = scottCatalogService;
			this.yvertCatalogService = yvertCatalogService;
			this.gibbonsCatalogService = gibbonsCatalogService;
			this.solovyovCatalogService = solovyovCatalogService;
			this.zagorskiCatalogService = zagorskiCatalogService;
			this.jdbcTemplate = jdbcTemplate;
		}
		
		@Bean
		public SeriesService seriesService(SeriesDao seriesDao) {
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
		public SeriesDao seriesDao() {
			return new JdbcSeriesDao(jdbcTemplate);
		}
		
	}
	
}
