/*
 * Copyright (C) 2009-2022 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.feature.country;

import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

/**
 * Spring configuration that is required for using countries in an application.
 *
 * The beans are grouped into different classes to make possible to register a controller
 * and the services in the separate application contexts. DAOs have been extracted to use
 * them independently from services in the tests.
 */
@Configuration
public class CountryConfig {
	
	@RequiredArgsConstructor
	public static class Controllers {
		
		private final CountryService countryService;
		
		@Bean
		public CountryController countryController() {
			return new CountryController(countryService);
		}
		
		@Bean
		public SuggestionController suggestionCountryController() {
			return new SuggestionController(countryService);
		}
		
	}
	
	@RequiredArgsConstructor
	public static class Services {
		
		private final CountryDao countryDao;
		private final Environment env;
		private final RestTemplateBuilder restTemplateBuilder;
		
		@Bean
		public CountryService countryService() {
			return new TogglzWithFallbackCountryService(
				new ApiCountryService(restTemplateBuilder, env),
				new CountryServiceImpl(
					LoggerFactory.getLogger(CountryServiceImpl.class),
					countryDao
				)
			);
		}
		
	}
	
	@RequiredArgsConstructor
	@PropertySource("classpath:sql/country_dao_queries.properties")
	public static class Daos {
		
		private final NamedParameterJdbcTemplate jdbcTemplate;
		private final Environment env;
		
		@Bean
		public CountryDao countryDao() {
			return new JdbcCountryDao(env, jdbcTemplate);
		}
		
	}
	
}
