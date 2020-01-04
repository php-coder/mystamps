/*
 * Copyright (C) 2009-2020 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.feature.series.importing.sale;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import ru.mystamps.web.feature.series.DownloaderService;
import ru.mystamps.web.feature.series.importing.SeriesInfoExtractorService;
import ru.mystamps.web.feature.series.importing.extractor.SiteParserService;

/**
 * Spring configuration that is required for importing series sale in an application.
 *
 *  The beans are grouped into two classes to make possible to register a controller
 *  and the services in the separated application contexts.
 */
@Configuration
public class SeriesSalesImportConfig {
	
	@RequiredArgsConstructor
	public static class Controllers {
		
		private final SeriesSalesImportService seriesSalesImportService;
		
		@Bean
		public SeriesSaleImportController seriesSaleImportController() {
			return new SeriesSaleImportController(seriesSalesImportService);
		}
		
	}
	
	@RequiredArgsConstructor
	public static class Services {
		
		private final NamedParameterJdbcTemplate jdbcTemplate;
		private final SiteParserService siteParserService;
		private final SeriesInfoExtractorService extractorService;
		
		@Bean
		public SeriesSalesImportService seriesSalesImportService(
			SeriesSalesImportDao seriesSalesImportDao,
			@Qualifier("seriesDownloaderService") DownloaderService seriesDownloaderService) {
			
			return new SeriesSalesImportServiceImpl(
				seriesSalesImportDao,
				seriesDownloaderService,
				siteParserService,
				extractorService
			);
		}
		
		@Bean
		public SeriesSalesImportDao seriesSalesImportDao() {
			return new JdbcSeriesSalesImportDao(jdbcTemplate);
		}
		
	}
	
}
