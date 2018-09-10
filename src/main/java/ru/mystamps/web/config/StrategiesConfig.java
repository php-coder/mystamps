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

import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.feature.image.DatabaseImagePersistenceStrategy;
import ru.mystamps.web.service.FilesystemImagePersistenceStrategy;
import ru.mystamps.web.service.ImagePersistenceStrategy;

@Configuration
public interface StrategiesConfig {
	
	ImagePersistenceStrategy getImagePersistenceStrategy();
	
	@Profile("test")
	@RequiredArgsConstructor
	class DbStrategiesConfig implements StrategiesConfig {
		
		private final DaoConfig daoConfig;
		
		@Bean
		@Override
		public ImagePersistenceStrategy getImagePersistenceStrategy() {
			return new DatabaseImagePersistenceStrategy(
				LoggerFactory.getLogger(DatabaseImagePersistenceStrategy.class),
				daoConfig.getImageDataDao()
			);
		}
		
	}
	
	@Profile({ "prod", "travis" })
	@RequiredArgsConstructor
	class FsStrategiesConfig implements StrategiesConfig {
		
		private final Environment env;
		
		@Bean
		@Override
		public ImagePersistenceStrategy getImagePersistenceStrategy() {
			return new FilesystemImagePersistenceStrategy(
				LoggerFactory.getLogger(FilesystemImagePersistenceStrategy.class),
				env.getRequiredProperty("app.upload.dir"),
				env.getRequiredProperty("app.preview.dir")
			);
		}
		
	}
	
}
