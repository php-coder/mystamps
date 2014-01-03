/*
 * Copyright (C) 2009-2014 Slava Semushin <slava.semushin@gmail.com>
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

import javax.inject.Inject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ru.mystamps.web.dao.*; // NOCHECKSTYLE: AvoidStarImportCheck, NOPMD: UnusedImports
import ru.mystamps.web.service.*; // NOCHECKSTYLE: AvoidStarImportCheck, NOPMD: UnusedImports

@Configuration
public class ServicesConfig {
	
	@Inject
	private CountryDao countryDao;
	
	@Inject
	private SecurityConfig securityConfig;
	
	@Inject
	private SeriesDao seriesDao;
	
	@Inject
	private SuspiciousActivityDao suspiciousActivityDao;
	
	@Inject
	private SuspiciousActivityTypeDao suspiciousActivityTypeDao;
	
	@Inject
	private UserDao userDao;
	
	@Inject
	private UsersActivationDao usersActivationDao;
	
	@Inject
	private StrategiesConfig strategiesConfig;
	
	@Inject
	private ImageDao imageDao;
	
	@Bean
	public CountryService getCountryService() {
		return new CountryServiceImpl(countryDao);
	}
	
	@Bean
	public CronService getCronService() {
		return new CronServiceImpl(usersActivationDao);
	}
	
	@Bean
	public ImageService getImageService() {
		return new ImageServiceImpl(strategiesConfig.getImagePersistenceStrategy(), imageDao);
	}
	
	@Bean
	public SeriesService getSeriesService() {
		return new SeriesServiceImpl(seriesDao, getImageService());
	}
	
	@Bean
	public SiteService getSiteService() {
		return new SiteServiceImpl(suspiciousActivityDao, suspiciousActivityTypeDao);
	}
	
	@Bean
	public UserService getUserService() {
		return new UserServiceImpl(
			userDao,
			usersActivationDao,
			securityConfig.getPasswordEncoder()
		);
	}
	
}
