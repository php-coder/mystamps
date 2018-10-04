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

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import lombok.RequiredArgsConstructor;

// CheckStyle: ignore AvoidStarImportCheck for next 1 line
import ru.mystamps.web.controller.*; // NOPMD: UnusedImports
import ru.mystamps.web.feature.account.AccountController;
import ru.mystamps.web.feature.category.CategoryConfig;
import ru.mystamps.web.feature.category.CategoryService;
import ru.mystamps.web.feature.collection.CollectionConfig;
import ru.mystamps.web.feature.collection.CollectionService;
import ru.mystamps.web.feature.country.CountryConfig;
import ru.mystamps.web.feature.country.CountryService;
import ru.mystamps.web.feature.image.ImageConfig;
import ru.mystamps.web.feature.participant.ParticipantConfig;
import ru.mystamps.web.feature.participant.ParticipantService;
import ru.mystamps.web.feature.series.SeriesConfig;
import ru.mystamps.web.feature.series.SeriesController;
import ru.mystamps.web.feature.series.SeriesService;

@Configuration
@RequiredArgsConstructor
@Import({
	CategoryConfig.Controllers.class,
	CollectionConfig.Controllers.class,
	CountryConfig.Controllers.class,
	ImageConfig.Controllers.class,
	ParticipantConfig.Controllers.class,
	SeriesConfig.Controllers.class
})
public class ControllersConfig {
	
	private final ServicesConfig servicesConfig;
	private final ApplicationEventPublisher eventPublisher;
	private final CategoryService categoryService;
	private final CollectionService collectionService;
	private final CountryService countryService;
	private final ParticipantService participantService;
	private final SeriesService seriesService;
	private final SeriesController seriesController;
	
	@Bean
	public AccountController getAccountController() {
		return new AccountController(
			servicesConfig.getUserService(),
			servicesConfig.getUsersActivationService()
		);
	}
	
	@Bean
	public ErrorController getErrorController() {
		return new ErrorController(servicesConfig.getSiteService());
	}
	
	@Bean
	public RobotsTxtController getRobotsTxtController() {
		return new RobotsTxtController();
	}

	@Bean
	public ReportController getReportController() {
		return new ReportController(
			servicesConfig.getReportService(),
			servicesConfig.getCronService()
		);
	}
	
	@Bean
	public SeriesImportController getSeriesImportController() {
		return new SeriesImportController(
			servicesConfig.getSeriesImportService(),
			servicesConfig.getSeriesSalesImportService(),
			seriesController,
			participantService,
			eventPublisher
		);
	}
	
	@Bean
	public SiteController getSiteController() {
		return new SiteController(
			categoryService,
			collectionService,
			countryService,
			seriesService,
			servicesConfig.getSuspiciousActivityService()
		);
	}
	
	@Bean
	public SitemapController getSitemapController() {
		return new SitemapController(seriesService);
	}
	
}
