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
package ru.mystamps.web.config;

import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import ru.mystamps.web.feature.account.AccountConfig;
import ru.mystamps.web.feature.account.UserService;
import ru.mystamps.web.feature.account.UsersActivationService;
import ru.mystamps.web.feature.category.CategoryConfig;
import ru.mystamps.web.feature.category.CategoryService;
import ru.mystamps.web.feature.collection.CollectionConfig;
import ru.mystamps.web.feature.collection.CollectionService;
import ru.mystamps.web.feature.country.CountryConfig;
import ru.mystamps.web.feature.country.CountryService;
import ru.mystamps.web.feature.image.ImageConfig;
import ru.mystamps.web.feature.participant.ParticipantConfig;
import ru.mystamps.web.feature.participant.ParticipantService;
import ru.mystamps.web.feature.series.DownloaderService;
import ru.mystamps.web.feature.series.HttpURLConnectionDownloaderService;
import ru.mystamps.web.feature.series.SeriesConfig;
import ru.mystamps.web.feature.series.SeriesService;
import ru.mystamps.web.feature.series.TimedDownloaderService;
import ru.mystamps.web.feature.series.importing.SeriesImportConfig;
import ru.mystamps.web.feature.series.importing.sale.SeriesSalesImportService;
import ru.mystamps.web.feature.series.importing.sale.SeriesSalesImportServiceImpl;
import ru.mystamps.web.feature.series.sale.SeriesSalesConfig;
import ru.mystamps.web.feature.series.sale.SeriesSalesService;
import ru.mystamps.web.service.CronService;
import ru.mystamps.web.service.CronServiceImpl;
import ru.mystamps.web.service.MailService;
import ru.mystamps.web.service.MailServiceImpl;
import ru.mystamps.web.service.ReportService;
import ru.mystamps.web.service.ReportServiceImpl;
import ru.mystamps.web.service.SiteService;
import ru.mystamps.web.service.SiteServiceImpl;
import ru.mystamps.web.service.StampsCatalogService;
import ru.mystamps.web.service.StampsCatalogServiceImpl;
import ru.mystamps.web.service.SuspiciousActivityService;
import ru.mystamps.web.service.SuspiciousActivityServiceImpl;

import java.util.Locale;

@Configuration
@Import({
	AccountConfig.Services.class,
	CategoryConfig.Services.class,
	CollectionConfig.Services.class,
	CountryConfig.Services.class,
	ImageConfig.Services.class,
	ParticipantConfig.Services.class,
	SeriesConfig.Services.class,
	SeriesImportConfig.Services.class,
	SeriesSalesConfig.Services.class
})
@RequiredArgsConstructor
@SuppressWarnings("PMD.CouplingBetweenObjects")
public class ServicesConfig {
	
	private final DaoConfig daoConfig;
	private final JavaMailSender mailSender;
	private final Environment env;
	private final MessageSource messageSource;
	private final ApplicationEventPublisher eventPublisher;
	private final CategoryService categoryService;
	private final CollectionService collectionService;
	private final CountryService countryService;
	private final ParticipantService participantService;
	private final SeriesService seriesService;
	private final SeriesSalesService seriesSalesService;
	private final UserService userService;
	
	@Lazy
	private final UsersActivationService usersActivationService;
	
	@Bean
	public SuspiciousActivityService getSuspiciousActivityService() {
		return new SuspiciousActivityServiceImpl(daoConfig.getSuspiciousActivityDao());
	}

	@Bean
	public CronService getCronService() {
		return new CronServiceImpl(
			LoggerFactory.getLogger(CronServiceImpl.class),
			categoryService,
			countryService,
			collectionService,
			seriesService,
			getSuspiciousActivityService(),
			userService,
			usersActivationService,
			getMailService()
		);
	}
	
	@Bean
	public DownloaderService getImageDownloaderService() {
		return new TimedDownloaderService(
			LoggerFactory.getLogger(TimedDownloaderService.class),
			new HttpURLConnectionDownloaderService(
				new String[]{"image/jpeg", "image/png"},
				env.getRequiredProperty("app.downloader.timeout", Integer.class)
			)
		);
	}
	
	@Bean
	public DownloaderService getSeriesDownloaderService() {
		return new TimedDownloaderService(
			LoggerFactory.getLogger(TimedDownloaderService.class),
			new HttpURLConnectionDownloaderService(
				new String[]{"text/html", "image/jpeg", "image/png"},
				env.getRequiredProperty("app.downloader.timeout", Integer.class)
			)
		);
	}
	
	@Bean
	public MailService getMailService() {
		boolean isProductionEnvironment = env.acceptsProfiles("prod");
		boolean enableTestMode = !isProductionEnvironment;
		
		return new MailServiceImpl(
			getReportService(),
			mailSender,
			messageSource,
			env.getProperty("app.mail.admin.email", "root@localhost"),
			new Locale(env.getProperty("app.mail.admin.lang", "en")),
			env.getRequiredProperty("app.mail.robot.email"),
			enableTestMode
		);
	}
	
	@Bean
	public ReportService getReportService() {
		return new ReportServiceImpl(
			messageSource,
			new Locale(env.getProperty("app.mail.admin.lang", "en"))
		);
	}
	
	@Bean
	public SeriesSalesImportService getSeriesSalesImportService() {
		return new SeriesSalesImportServiceImpl(
			LoggerFactory.getLogger(SeriesSalesImportServiceImpl.class),
			daoConfig.getSeriesSalesImportDao()
		);
	}
	
	@Bean
	public SiteService getSiteService() {
		return new SiteServiceImpl(
			LoggerFactory.getLogger(SiteServiceImpl.class),
			daoConfig.getSuspiciousActivityDao()
		);
	}
	
	@Bean(name = "michelCatalog")
	public StampsCatalogService getMichelCatalogService() {
		return new StampsCatalogServiceImpl(
			LoggerFactory.getLogger(StampsCatalogServiceImpl.class),
			"Michel",
			daoConfig.getMichelCatalogDao()
		);
	}
	
	@Bean(name = "scottCatalog")
	public StampsCatalogService getScottCatalogService() {
		return new StampsCatalogServiceImpl(
			LoggerFactory.getLogger(StampsCatalogServiceImpl.class),
			"Scott",
			daoConfig.getScottCatalogDao()
		);
	}
	
	@Bean(name = "yvertCatalog")
	public StampsCatalogService getYvertCatalogService() {
		return new StampsCatalogServiceImpl(
			LoggerFactory.getLogger(StampsCatalogServiceImpl.class),
			"Yvert",
			daoConfig.getYvertCatalogDao()
		);
	}
	
	@Bean(name = "gibbonsCatalog")
	public StampsCatalogService getGibbonsCatalogService() {
		return new StampsCatalogServiceImpl(
			LoggerFactory.getLogger(StampsCatalogServiceImpl.class),
			"Gibbons",
			daoConfig.getGibbonsCatalogDao()
		);
	}
	
	@Bean(name = "solovyovCatalog")
	public StampsCatalogService getSolovyovCatalogService() {
		return new StampsCatalogServiceImpl(
			LoggerFactory.getLogger(StampsCatalogServiceImpl.class),
			"Solovyov",
			daoConfig.getSolovyovCatalogDao()
		);
	}
	
	@Bean(name = "zagorskiCatalog")
	public StampsCatalogService getZagorskiCatalogService() {
		return new StampsCatalogServiceImpl(
			LoggerFactory.getLogger(StampsCatalogServiceImpl.class),
			"Zagorski",
			daoConfig.getZagorskiCatalogDao()
		);
	}
	
}
