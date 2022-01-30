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
package ru.mystamps.web.feature.series.importing;

import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import ru.mystamps.web.feature.participant.ParticipantService;
import ru.mystamps.web.feature.series.SeriesController;
import ru.mystamps.web.feature.series.SeriesService;
import ru.mystamps.web.feature.series.importing.sale.SeriesSalesImportService;
import ru.mystamps.web.feature.series.sale.SeriesSalesService;

import java.util.Collections;

/**
 * Spring configuration that is required for importing series in an application.
 *
 * The beans are grouped into two classes to make possible to register a controller
 * and the services in the separated application contexts.
 */
@Configuration
public class SeriesImportConfig {
	
	@RequiredArgsConstructor
	public static class Controllers {
		
		private final ParticipantService participantService;
		private final SeriesImportService seriesImportService;
		private final SeriesSalesImportService seriesSalesImportService;
		private final SeriesController seriesController;
		private final ApplicationEventPublisher eventPublisher;
		
		@Bean
		public SeriesImportController seriesImportController() {
			return new SeriesImportController(
				seriesImportService,
				seriesSalesImportService,
				seriesController,
				participantService,
				eventPublisher
			);
		}
		
		// CheckStyle: ignore LineLength for next 2 lines
		@Bean
		public FilterRegistrationBean<ImportSeriesFormTrimmerFilter> importSeriesFormTrimmerFilter() {
			FilterRegistrationBean<ImportSeriesFormTrimmerFilter> bean =
				new FilterRegistrationBean<>(new ImportSeriesFormTrimmerFilter());
			
			String pattern = SeriesImportUrl.REQUEST_IMPORT_PAGE.replace("{id}", "*");
			bean.setUrlPatterns(Collections.singletonList(pattern));
			
			// register with an order greater than UserMdcLoggingFilter to get a lowest precedence
			// and being executed after it
			// CheckStyle: ignore MagicNumber for next 1 line
			bean.setOrder(Ordered.LOWEST_PRECEDENCE - 50);
			
			return bean;
		}
		
	}
	
	@RequiredArgsConstructor
	@PropertySource("classpath:sql/series_import_request_dao_queries.properties")
	public static class Services {
		
		private final NamedParameterJdbcTemplate jdbcTemplate;
		private final ParticipantService participantService;
		private final SeriesService seriesService;
		private final SeriesSalesService seriesSalesService;
		private final ApplicationEventPublisher eventPublisher;
		private final Environment env;
		
		@Bean
		public SeriesImportService seriesImportService(
			SeriesImportDao seriesImportDao,
			@Lazy SeriesSalesImportService seriesSalesImportService) {
			
			return new SeriesImportServiceImpl(
				LoggerFactory.getLogger(SeriesImportServiceImpl.class),
				seriesImportDao,
				seriesService,
				seriesSalesService,
				seriesSalesImportService,
				participantService,
				eventPublisher
			);
		}
		
		@Bean
		public SeriesImportDao seriesImportDao() {
			return new JdbcSeriesImportDao(env, jdbcTemplate);
		}
		
	}
	
}
