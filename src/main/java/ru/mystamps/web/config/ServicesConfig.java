/*
 * Copyright (C) 2009-2013 Slava Semushin <slava.semushin@gmail.com>
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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ru.mystamps.web.service.*; // NOCHECKSTYLE: AvoidStarImportCheck, NOPMD: UnusedImports

@Configuration
public class ServicesConfig {
	
	@Bean
	public CountryService getCountryService() {
		return new CountryServiceImpl();
	}
	
	@Bean
	public CronService getCronService() {
		return new CronServiceImpl();
	}
	
	@Bean
	public ImageService getImageService() {
		return new ImageServiceImpl();
	}
	
	@Bean
	public SeriesService getSeriesService() {
		return new SeriesServiceImpl();
	}
	
	@Bean
	public SiteService getSiteService() {
		return new SiteServiceImpl();
	}
	
	@Bean
	public UserService getUserService() {
		return new UserServiceImpl();
	}
	
}
