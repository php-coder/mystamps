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
package ru.mystamps.web.feature.category;

import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

/**
 * Spring configuration that is required for using categories in an application.
 *
 * The beans are grouped into different classes to make possible to register a controller
 * and the services in the separate application contexts. DAOs have been extracted to use
 * them independently from services in the tests.
 */
@Configuration
public class CategoryConfig {
	
	@RequiredArgsConstructor
	public static class Controllers {
		
		private final CategoryService categoryService;
		
		@Bean
		public CategoryController categoryController() {
			return new CategoryController(categoryService);
		}
		
		@Bean
		public SuggestionController suggestionCategoryController() {
			return new SuggestionController(categoryService);
		}
		
	}
	
	@RequiredArgsConstructor
	public static class Services {
		
		private final CategoryDao categoryDao;
		
		@Bean
		public CategoryService categoryService() {
			return new CategoryServiceImpl(
				LoggerFactory.getLogger(CategoryServiceImpl.class),
				categoryDao
			);
		}
		
	}
	
	@RequiredArgsConstructor
	public static class Daos {
		
		private final NamedParameterJdbcTemplate jdbcTemplate;
		
		@Bean
		public CategoryDao categoryDao() {
			return new JdbcCategoryDao(jdbcTemplate);
		}
		
	}
	
}
