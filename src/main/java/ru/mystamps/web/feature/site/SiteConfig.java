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
package ru.mystamps.web.feature.site;

import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import ru.mystamps.web.feature.account.UserService;
import ru.mystamps.web.feature.account.UsersActivationService;
import ru.mystamps.web.feature.category.CategoryService;
import ru.mystamps.web.feature.collection.CollectionService;
import ru.mystamps.web.feature.country.CountryService;
import ru.mystamps.web.feature.report.ReportService;
import ru.mystamps.web.feature.series.SeriesService;
import ru.mystamps.web.support.mailgun.ApiMailgunEmailSendingStrategy;
import ru.mystamps.web.support.mailgun.MailgunEmailSendingStrategy;

import java.util.Locale;

/**
 * Spring configuration that is required for site to be working.
 *
 * The beans are grouped into two classes to make possible to register a controller
 * and the services in the separated application contexts.
 */
@Configuration
public class SiteConfig {
	
	@RequiredArgsConstructor
	public static class Controllers {
		
		private final CategoryService categoryService;
		private final CountryService countryService;
		private final CollectionService collectionService;
		private final SeriesService seriesService;
		private final SiteService siteService;
		private final SuspiciousActivityService suspiciousActivityService;
		
		@Bean
		public ErrorController errorController() {
			return new ErrorController(siteService);
		}
		
		@Bean
		public RobotsTxtController robotsTxtController() {
			return new RobotsTxtController();
		}
		
		@Bean
		public SiteController siteController() {
			return new SiteController(
				categoryService,
				collectionService,
				countryService,
				seriesService,
				suspiciousActivityService
			);
		}
		
		@Bean
		public SitemapController sitemapController() {
			return new SitemapController(seriesService);
		}
		
		@Bean
		public CspController cspController() {
			return new CspController();
		}

	}
	
	@RequiredArgsConstructor
	public static class Services {

		private final CategoryService categoryService;
		private final CountryService countryService;
		private final CollectionService collectionService;
		private final UserService userService;
		private final UsersActivationService usersActivationService;
		private final ReportService reportService;
		private final SeriesService seriesService;
		private final Environment env;
		private final NamedParameterJdbcTemplate jdbcTemplate;
		private final MessageSource messageSource;
		private final RestTemplateBuilder restTemplateBuilder;
		
		@Bean
		public CronService cronService(
			MailService mailService,
			SuspiciousActivityService suspiciousActivityService) {
			
			return new CronServiceImpl(
				LoggerFactory.getLogger(CronServiceImpl.class),
				categoryService,
				countryService,
				collectionService,
				seriesService,
				suspiciousActivityService,
				userService,
				usersActivationService,
				mailService
			);
		}
		
		@Bean
		public MailService mailService() {
			boolean isProductionEnvironment = env.acceptsProfiles("prod");
			boolean enableTestMode = !isProductionEnvironment;
			
			String user = "api";
			String password = env.getRequiredProperty("mailgun.password");
			String endpoint = env.getRequiredProperty("mailgun.endpoint");
			
			MailgunEmailSendingStrategy mailStrategy = new ApiMailgunEmailSendingStrategy(
				restTemplateBuilder,
				endpoint,
				user,
				password
			);
			
			return new MailServiceImpl(
				reportService,
				mailStrategy,
				messageSource,
				env.getProperty("app.mail.admin.email", "root@localhost"),
				new Locale(env.getProperty("app.mail.admin.lang", "en")),
				env.getRequiredProperty("app.mail.robot.email"),
				enableTestMode
			);
		}
		
		@Bean
		public SiteService siteService(SuspiciousActivityDao suspiciousActivityDao) {
			return new SiteServiceImpl(
				LoggerFactory.getLogger(SiteServiceImpl.class),
				suspiciousActivityDao
			);
		}
		
		@Bean
		public SuspiciousActivityService suspiciousActivityService(
			SuspiciousActivityDao suspiciousActivityDao) {
			
			return new SuspiciousActivityServiceImpl(suspiciousActivityDao);
		}
		
		@Bean
		public SuspiciousActivityDao suspiciousActivityDao() {
			return new JdbcSuspiciousActivityDao(jdbcTemplate);
		}
		
	}
	
}
