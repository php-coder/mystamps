/*
 * Copyright (C) 2009-2016 Slava Semushin <slava.semushin@gmail.com>
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
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;

import ru.mystamps.web.support.spring.security.SecurityConfig;
import ru.mystamps.web.support.togglz.TogglzConfig;

@Configuration
@Import({
	DbConfig.class,
	SecurityConfig.class,
	DaoConfig.class,
	ServicesConfig.class,
	StrategiesConfig.class,
	TogglzConfig.class
})
@SuppressWarnings({"checkstyle:hideutilityclassconstructor", "PMD.UseUtilityClass"})
public class ApplicationContext {
	
	@Bean
	public static PropertySourcesPlaceholderConfigurer getPropertySourcesPlaceholderConfigurer() {
		PropertySourcesPlaceholderConfigurer configurer =
			new PropertySourcesPlaceholderConfigurer();
		configurer.setLocations(
			new ClassPathResource("sql/category_dao_queries.properties"),
			new ClassPathResource("sql/country_dao_queries.properties"),
			new ClassPathResource("sql/collection_dao_queries.properties"),
			new ClassPathResource("sql/gibbons_catalog_dao_queries.properties"),
			new ClassPathResource("sql/image_dao_queries.properties"),
			new ClassPathResource("sql/michel_catalog_dao_queries.properties"),
			new ClassPathResource("sql/scott_catalog_dao_queries.properties"),
			new ClassPathResource("sql/user_dao_queries.properties"),
			new ClassPathResource("sql/users_activation_dao_queries.properties"),
			new ClassPathResource("sql/series_dao_queries.properties"),
			new ClassPathResource("sql/suspicious_activity_dao_queries.properties"),
			new ClassPathResource("sql/yvert_catalog_dao_queries.properties")
		);
		return configurer;
	}
	
}
