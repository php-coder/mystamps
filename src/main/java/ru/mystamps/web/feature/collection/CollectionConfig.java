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
package ru.mystamps.web.feature.collection;

import org.slf4j.LoggerFactory;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.feature.category.CategoryService;
import ru.mystamps.web.feature.country.CountryService;

/**
 * Spring configuration that is required for using collections in an application.
 *
 * The beans are grouped into two classes to make possible to register a controller
 * and the services in the separated application contexts.
 */
@Configuration
public class CollectionConfig {
	
	@RequiredArgsConstructor
	public static class Controllers {
		
		private final CategoryService categoryService;
		private final CollectionService collectionService;
		private final CountryService countryService;
		private final MessageSource messageSource;
		
		@Bean
		public CollectionController collectionController() {
			return new CollectionController(
				categoryService,
				collectionService,
				countryService,
				messageSource
			);
		}
		
	}
	
	@RequiredArgsConstructor
	public static class Services {
		
		private final NamedParameterJdbcTemplate jdbcTemplate;
		
		@Bean
		public CollectionService collectionService(CollectionDao collectionDao) {
			return new CollectionServiceImpl(
				LoggerFactory.getLogger(CollectionServiceImpl.class),
				collectionDao
			);
		}
		
		@Bean
		public CollectionDao collectionDao() {
			return new JdbcCollectionDao(jdbcTemplate);
		}
		
	}
	
}
