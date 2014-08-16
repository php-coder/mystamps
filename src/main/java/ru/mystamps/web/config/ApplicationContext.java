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

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import ru.mystamps.web.support.spring.security.SecurityConfig;

import org.togglz.core.manager.TogglzConfig;

import ru.mystamps.web.support.togglz.FeatureConfig;

@Configuration
@Import({
	DbConfig.class,
	LiquibaseConfig.class,
	MailConfig.class,
	SecurityConfig.class,
	ServicesConfig.class,
	StrategiesConfig.class
})
public class ApplicationContext {
	
	@Inject
	private DataSourceConfig dataSourceConfig;
	
	@Bean(name = "messageSource")
	public MessageSource getMessageSource() {
		ReloadableResourceBundleMessageSource messageSource =
			new ReloadableResourceBundleMessageSource();
		
		messageSource.setBasenames(
			"classpath:ru/mystamps/i18n/SpringSecurityMessages",
			"classpath:ru/mystamps/i18n/MailTemplates"
		);
		messageSource.setDefaultEncoding("UTF-8");
		messageSource.setFallbackToSystemLocale(false);
		
		return messageSource;
	}
	
	@Bean
	public TogglzConfig getTogglzConfig() {
		return new FeatureConfig(dataSourceConfig.getDataSource());
	}
	
}
