/*
 * Copyright (C) 2009-2017 Slava Semushin <slava.semushin@gmail.com>
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

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import lombok.RequiredArgsConstructor;

// CheckStyle: ignore AvoidStarImportCheck for next 1 line
import ru.mystamps.web.controller.*; // NOPMD: UnusedImports

@Configuration
@RequiredArgsConstructor
public class ControllersConfig {
	
	private final ServicesConfig servicesConfig;
	private final MessageSource messageSource;
	
	@Bean
	public AccountController getAccountController() {
		return new AccountController(
			servicesConfig.getUserService(),
			servicesConfig.getUsersActivationService()
		);
	}
	
	@Bean
	public CategoryController getCategoryController() {
		return new CategoryController(
			servicesConfig.getCategoryService(),
			servicesConfig.getSeriesService()
		);
	}
	
	@Bean
	public CountryController getCountryController() {
		return new CountryController(
			servicesConfig.getCountryService(),
			servicesConfig.getSeriesService()
		);
	}
	
	@Bean
	public CollectionController getCollectionController() {
		return new CollectionController(
			servicesConfig.getCategoryService(),
			servicesConfig.getCollectionService(),
			servicesConfig.getCountryService(),
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
			servicesConfig.getCategoryService(),
			servicesConfig.getCollectionService(),
			servicesConfig.getCountryService(),
			servicesConfig.getSeriesService(),
			servicesConfig.getSeriesSalesService(),
			servicesConfig.getTransactionParticipantService()
		);
	}
	
	@Bean
	public SiteController getSiteController() {
		return new SiteController(
			servicesConfig.getCategoryService(),
			servicesConfig.getCollectionService(),
			servicesConfig.getCountryService(),
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
		return new SuggestionController(
			servicesConfig.getCountryService()
		);
	}

	@Bean
	@Profile({ "test", "travis" })
	public TestController getTestController() {
		return new TestController();
	}
	
}
