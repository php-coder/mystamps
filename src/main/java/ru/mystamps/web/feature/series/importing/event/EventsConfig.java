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
package ru.mystamps.web.feature.series.importing.event;

import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import ru.mystamps.web.config.ServicesConfig;
import ru.mystamps.web.feature.category.CategoryService;
import ru.mystamps.web.feature.country.CountryService;
import ru.mystamps.web.feature.participant.ParticipantService;
import ru.mystamps.web.feature.series.importing.SeriesImportService;
import ru.mystamps.web.feature.series.importing.SeriesInfoExtractorService;
import ru.mystamps.web.feature.series.importing.SeriesInfoExtractorServiceImpl;
import ru.mystamps.web.feature.series.importing.TimedSeriesInfoExtractorService;
import ru.mystamps.web.feature.series.importing.extractor.JdbcSiteParserDao;
import ru.mystamps.web.feature.series.importing.extractor.SiteParserDao;
import ru.mystamps.web.feature.series.importing.extractor.SiteParserService;
import ru.mystamps.web.feature.series.importing.extractor.SiteParserServiceImpl;

@Configuration
@RequiredArgsConstructor
public class EventsConfig {
	
	private final CategoryService categoryService;
	private final CountryService countryService;
	private final ParticipantService participantService;
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
	public SeriesInfoExtractorService seriesInfoExtractorService() {
		return new TimedSeriesInfoExtractorService(
			LoggerFactory.getLogger(TimedSeriesInfoExtractorService.class),
			new SeriesInfoExtractorServiceImpl(
				LoggerFactory.getLogger(SeriesInfoExtractorServiceImpl.class),
				categoryService,
				countryService,
				participantService
			)
		);
	}
	
	@Bean
	public SiteParserDao siteParserDao() {
		return new JdbcSiteParserDao(jdbcTemplate);
	}
	
	@Bean
	public ApplicationListener<ImportRequestCreated> importRequestCreatedEventListener() {
		return new ImportRequestCreatedEventListener(
			servicesConfig.getSeriesDownloaderService(),
			seriesImportService,
			eventPublisher
		);
	}
	
	@Bean
	public ApplicationListener<DownloadingSucceeded> downloadingSucceededEventListener(
		SiteParserService siteParserService,
		SeriesInfoExtractorService extractorService
		) {
		
		return new DownloadingSucceededEventListener(
			seriesImportService,
			siteParserService,
			extractorService,
			eventPublisher
		);
	}
	
	@Bean
	public ApplicationListener<ParsingFailed> parsingFailedEventListener() {
		return new ParsingFailedEventListener(seriesImportService);
	}
	
}
