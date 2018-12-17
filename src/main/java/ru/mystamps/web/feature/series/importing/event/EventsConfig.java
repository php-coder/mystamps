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
package ru.mystamps.web.feature.series.importing.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.config.ServicesConfig;
import ru.mystamps.web.feature.series.importing.SeriesImportService;
import ru.mystamps.web.feature.series.importing.extractor.JdbcSiteParserDao;
import ru.mystamps.web.feature.series.importing.extractor.SiteParserDao;
import ru.mystamps.web.feature.series.importing.extractor.SiteParserService;
import ru.mystamps.web.feature.series.importing.extractor.SiteParserServiceImpl;

@Configuration
@RequiredArgsConstructor
public class EventsConfig {
	
	private static final Logger LOG = LoggerFactory.getLogger(EventsConfig.class);
	
	private final SeriesImportService seriesImportService;
	private final ServicesConfig servicesConfig;
	private final ApplicationEventPublisher eventPublisher;
	private final NamedParameterJdbcTemplate jdbcTemplate;
	
	@Bean
	public SiteParserService siteParserService(SiteParserDao siteParserDao) {
		return new SiteParserServiceImpl(
			LoggerFactory.getLogger(SiteParserServiceImpl.class),
			siteParserDao
		);
	}
	
	@Bean
	public SiteParserDao siteParserDao() {
		return new JdbcSiteParserDao(jdbcTemplate);
	}
	
	@Bean
	public ApplicationListener<ImportRequestCreated> getImportRequestCreatedEventListener() {
		return new ImportRequestCreatedEventListener(
			LoggerFactory.getLogger(ImportRequestCreatedEventListener.class),
			servicesConfig.getSeriesDownloaderService(),
			seriesImportService,
			eventPublisher
		);
	}
	
	// This bean has logic that access database. To ensure that all migrations have been applied
	// we need this dependency. This annotation shouldn't be needed in Spring Boot 2.
	@DependsOn("liquibase")
	@Bean
	public ApplicationListener<DownloadingSucceeded> getDownloadingSucceededEventListener(
		SiteParserService siteParserService
		) {
		
		return new DownloadingSucceededEventListener(
			LoggerFactory.getLogger(DownloadingSucceededEventListener.class),
			seriesImportService,
			siteParserService,
			eventPublisher
		);
	}
	
	@Bean
	public ApplicationListener<ParsingFailed> getParsingFailedEventListener() {
		return new ParsingFailedEventListener(
			LoggerFactory.getLogger(ParsingFailedEventListener.class),
			seriesImportService
		);
	}
	
}
