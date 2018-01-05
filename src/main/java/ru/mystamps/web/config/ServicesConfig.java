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

import java.util.Locale;

import org.slf4j.LoggerFactory;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;

import lombok.RequiredArgsConstructor;

// CheckStyle: ignore AvoidStarImportCheck for next 1 line
import ru.mystamps.web.service.*; // NOPMD: UnusedImports
import ru.mystamps.web.support.spring.security.SecurityConfig;

@Configuration
@RequiredArgsConstructor
@SuppressWarnings("PMD.CouplingBetweenObjects")
public class ServicesConfig {
	
	private final DaoConfig daoConfig;
	private final SecurityConfig securityConfig;
	private final StrategiesConfig strategiesConfig;
	private final JavaMailSender mailSender;
	private final Environment env;
	private final MessageSource messageSource;
	private final ApplicationEventPublisher eventPublisher;
	
	@Bean
	public SuspiciousActivityService getSuspiciousActivityService() {
		return new SuspiciousActivityServiceImpl(daoConfig.getSuspiciousActivityDao());
	}

	@Bean
	public CountryService getCountryService() {
		return new CountryServiceImpl(
			LoggerFactory.getLogger(CountryServiceImpl.class),
			daoConfig.getCountryDao()
		);
	}
	
	@Bean
	public CategoryService getCategoryService() {
		return new CategoryServiceImpl(
			LoggerFactory.getLogger(CategoryServiceImpl.class),
			daoConfig.getCategoryDao()
		);
	}
	
	@Bean
	public CollectionService getCollectionService() {
		return new CollectionServiceImpl(
			LoggerFactory.getLogger(CollectionServiceImpl.class),
			daoConfig.getCollectionDao()
		);
	}
	
	@Bean
	public CronService getCronService() {
		return new CronServiceImpl(
			LoggerFactory.getLogger(CronServiceImpl.class),
			getCategoryService(),
			getCountryService(),
			getCollectionService(),
			getSeriesService(),
			getSuspiciousActivityService(),
			getUserService(),
			getUsersActivationService(),
			getMailService()
		);
	}
	
	@Bean
	public DownloaderService getImageDownloaderService() {
		return new TimedDownloaderService(
			LoggerFactory.getLogger(TimedDownloaderService.class),
			new HttpURLConnectionDownloaderService(
				new String[]{"image/jpeg", "image/png"}
			)
		);
	}
	
	@Bean
	public DownloaderService getSeriesDownloaderService() {
		return new TimedDownloaderService(
			LoggerFactory.getLogger(TimedDownloaderService.class),
			new HttpURLConnectionDownloaderService(
				new String[]{"text/html", "image/jpeg", "image/png"}
			)
		);
	}
	
	@Bean
	public ImageService getImageService() {
		return new ImageServiceImpl(
			LoggerFactory.getLogger(ImageServiceImpl.class),
			strategiesConfig.getImagePersistenceStrategy(),
			new TimedImagePreviewStrategy(
				LoggerFactory.getLogger(TimedImagePreviewStrategy.class),
				new ThumbnailatorImagePreviewStrategy()
			),
			daoConfig.getImageDao()
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
	public UsersActivationService getUsersActivationService() {
		return new UsersActivationServiceImpl(
			LoggerFactory.getLogger(UsersActivationServiceImpl.class),
			daoConfig.getUsersActivationDao(),
			getMailService()
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
	public SeriesService getSeriesService() {
		return new SeriesServiceImpl(
			LoggerFactory.getLogger(SeriesServiceImpl.class),
			daoConfig.getSeriesDao(),
			getImageService(),
			getMichelCatalogService(),
			getScottCatalogService(),
			getYvertCatalogService(),
			getGibbonsCatalogService(),
			getSolovyovCatalogService(),
			getZagorskiCatalogService()
		);
	}
	
	@Bean
	public SeriesImportService getSeriesImportService() {
		return new SeriesImportServiceImpl(
			LoggerFactory.getLogger(SeriesImportServiceImpl.class),
			daoConfig.getSeriesImportDao(),
			getSeriesService(),
			getSeriesInfoExtractorService(),
			eventPublisher
		);
	}
	
	@Bean
	public SeriesInfoExtractorService getSeriesInfoExtractorService() {
		return new TimedSeriesInfoExtractorService(
			LoggerFactory.getLogger(TimedSeriesInfoExtractorService.class),
			new SeriesInfoExtractorServiceImpl(
				LoggerFactory.getLogger(SeriesInfoExtractorServiceImpl.class),
				getCategoryService(),
				getCountryService()
			)
		);
	}
	
	@Bean
	public SeriesSalesService getSeriesSalesService() {
		return new SeriesSalesServiceImpl(
			LoggerFactory.getLogger(SeriesSalesServiceImpl.class),
			daoConfig.getSeriesSalesDao()
		);
	}
	
	@Bean
	public SiteService getSiteService() {
		return new SiteServiceImpl(
			LoggerFactory.getLogger(SiteServiceImpl.class),
			daoConfig.getSuspiciousActivityDao()
		);
	}
	
	@Bean
	public UserService getUserService() {
		return new UserServiceImpl(
			LoggerFactory.getLogger(UserServiceImpl.class),
			daoConfig.getUserDao(),
			getUsersActivationService(),
			getCollectionService(),
			securityConfig.getPasswordEncoder()
		);
	}
	
	@Bean
	public StampsCatalogService getMichelCatalogService() {
		return new StampsCatalogServiceImpl(
			LoggerFactory.getLogger(StampsCatalogServiceImpl.class),
			"Michel",
			daoConfig.getMichelCatalogDao()
		);
	}
	
	@Bean
	public StampsCatalogService getScottCatalogService() {
		return new StampsCatalogServiceImpl(
			LoggerFactory.getLogger(StampsCatalogServiceImpl.class),
			"Scott",
			daoConfig.getScottCatalogDao()
		);
	}
	
	@Bean
	public StampsCatalogService getYvertCatalogService() {
		return new StampsCatalogServiceImpl(
			LoggerFactory.getLogger(StampsCatalogServiceImpl.class),
			"Yvert",
			daoConfig.getYvertCatalogDao()
		);
	}
	
	@Bean
	public StampsCatalogService getGibbonsCatalogService() {
		return new StampsCatalogServiceImpl(
			LoggerFactory.getLogger(StampsCatalogServiceImpl.class),
			"Gibbons",
			daoConfig.getGibbonsCatalogDao()
		);
	}
	
	@Bean
	public StampsCatalogService getSolovyovCatalogService() {
		return new StampsCatalogServiceImpl(
			LoggerFactory.getLogger(StampsCatalogServiceImpl.class),
			"Solovyov",
			daoConfig.getSolovyovCatalogDao()
		);
	}
	
	@Bean
	public StampsCatalogService getZagorskiCatalogService() {
		return new StampsCatalogServiceImpl(
			LoggerFactory.getLogger(StampsCatalogServiceImpl.class),
			"Zagorski",
			daoConfig.getZagorskiCatalogDao()
		);
	}
	
	@Bean
	public TransactionParticipantService getTransactionParticipantService() {
		return new TransactionParticipantServiceImpl(
			LoggerFactory.getLogger(TransactionParticipantServiceImpl.class),
			daoConfig.getTransactionParticipantDao()
		);
	}
	
}
