/*
 * Copyright (C) 2009-2024 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.feature.image;

import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

/**
 * Spring configuration that is required for using images in an application.
 *
 * The beans are grouped into two classes to make possible to register a controller
 * and the services in the separated application contexts.
 */
@Configuration
public class ImageConfig {
	
	@RequiredArgsConstructor
	public static class Controllers {
		
		private final ImageService imageService;
		
		@Bean
		public ImageController imageController() {
			return new ImageController(imageService);
		}
		
	}
	
	@RequiredArgsConstructor
	@Import({ DbStrategyConfig.class, FsStrategyConfig.class })
	@PropertySource("classpath:sql/image_dao_queries.properties")
	public static class Services {
		
		private final Environment env;
		private final NamedParameterJdbcTemplate jdbcTemplate;
		private final ImagePersistenceStrategy imagePersistenceStrategy;
		
		@Bean
		public ImageService imageService(ImageDao imageDao) {
			return new ImageServiceImpl(
				LoggerFactory.getLogger(ImageServiceImpl.class),
				imagePersistenceStrategy,
				new TimedImagePreviewStrategy(
					LoggerFactory.getLogger(TimedImagePreviewStrategy.class),
					new ThumbnailatorImagePreviewStrategy()
				),
				imageDao
			);
		}
		
		@Bean
		public ImageDao imageDao() {
			return new JdbcImageDao(env, jdbcTemplate);
		}
		
	}
	
	// @todo #1054 Introduce profiles for image persistence strategies
	@Profile({ "test", "postgres" })
	@RequiredArgsConstructor
	public static class DbStrategyConfig {
		
		private final Environment env;
		private final NamedParameterJdbcTemplate jdbcTemplate;
		
		@Bean
		public ImagePersistenceStrategy imagePersistenceStrategy(ImageDataDao imageDataDao) {
			return new DatabaseImagePersistenceStrategy(
				LoggerFactory.getLogger(DatabaseImagePersistenceStrategy.class),
				imageDataDao
			);
		}
		
		@Bean
		public ImageDataDao imageDataDao() {
			return new JdbcImageDataDao(env, jdbcTemplate);
		}
		
	}
	
	@Profile({ "prod", "travis" })
	@RequiredArgsConstructor
	public static class FsStrategyConfig {
		
		private final Environment env;
		
		@Bean
		public ImagePersistenceStrategy imagePersistenceStrategy() {
			return new FilesystemImagePersistenceStrategy(
				LoggerFactory.getLogger(FilesystemImagePersistenceStrategy.class),
				env.getRequiredProperty("app.upload.dir"),
				env.getRequiredProperty("app.preview.dir")
			);
		}
		
	}
	
}
