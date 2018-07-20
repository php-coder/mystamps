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
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import lombok.RequiredArgsConstructor;

// CheckStyle: ignore AvoidStarImportCheck for next 1 line
import ru.mystamps.web.controller.*; // NOPMD: UnusedImports
import ru.mystamps.web.feature.category.CategoryConfig;
import ru.mystamps.web.feature.category.CategoryService;
import ru.mystamps.web.feature.country.CountryConfig;
import ru.mystamps.web.feature.country.CountryService;

@Configuration
@RequiredArgsConstructor
@Import({
	CategoryConfig.Controllers.class,
	CountryConfig.Controllers.class
})
public class ControllersConfig {
	
	private final ServicesConfig servicesConfig;
	private final MessageSource messageSource;
	private final ApplicationEventPublisher eventPublisher;
	private final CategoryService categoryService;
	private final CountryService countryService;
	
	@Bean
	public AccountController getAccountController() {
		return new AccountController(
			servicesConfig.getUserService(),
			servicesConfig.getUsersActivationService()
		);
	}
	
	@Bean
	public CollectionController getCollectionController() {
		return new CollectionController(
			categoryService,
			servicesConfig.getCollectionService(),
			countryService,
			servicesConfig.getSeriesService(),
			messageSource
		);
	}
	
	@Bean
	public ImageController getImageController() {
		return new ImageController(servicesConfig.getImageService());
	}
	
	@Bean
	public ErrorController getErrorController() {
		return new ErrorController(servicesConfig.getSiteService());
	}
	
	@Bean
	public ParticipantController getParticipantController() {
		return new ParticipantController(servicesConfig.getTransactionParticipantService());
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
	public SeriesController getSeriesController() {
		return new SeriesController(
			categoryService,
			servicesConfig.getCollectionService(),
			countryService,
			servicesConfig.getSeriesService(),
			servicesConfig.getSeriesImportService(),
			servicesConfig.getSeriesSalesService(),
			servicesConfig.getTransactionParticipantService()
		);
	}
	
	@Bean
	public SeriesImportController getSeriesImportController() {
		return new SeriesImportController(
			servicesConfig.getSeriesImportService(),
			servicesConfig.getSeriesSalesService(),
			servicesConfig.getSeriesSalesImportService(),
			getSeriesController(),
			servicesConfig.getTransactionParticipantService(),
			eventPublisher
		);
	}
	
	@Bean
	public SiteController getSiteController() {
		return new SiteController(
			categoryService,
			servicesConfig.getCollectionService(),
			countryService,
			servicesConfig.getSeriesService(),
			servicesConfig.getSuspiciousActivityService()
		);
	}
	
	@Bean
	public SitemapController getSitemapController() {
		return new SitemapController(servicesConfig.getSeriesService());
	}

	@Bean
	public SuggestionController getSuggestionController() {
		return new SuggestionController(countryService);
	}

	@Bean
	@Profile({ "test", "travis" })
	public TestController getTestController() {
		return new TestController();
	}
	
}
