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
package ru.mystamps.web.service;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.multipart.MultipartFile;

import ru.mystamps.web.dao.ImageDataDao;
import ru.mystamps.web.entity.Image;
import ru.mystamps.web.entity.ImageData;
import ru.mystamps.web.service.dto.DbImageDto;
import ru.mystamps.web.service.dto.ImageDto;
import ru.mystamps.web.service.exception.ImagePersistenceException;

public class DatabaseImagePersistenceStrategy implements ImagePersistenceStrategy {
	private static final Logger LOG =
		LoggerFactory.getLogger(DatabaseImagePersistenceStrategy.class);
	
	private final ImageDataDao imageDataDao;
	
	@Inject
	public DatabaseImagePersistenceStrategy(ImageDataDao imageDataDao) {
		this.imageDataDao = imageDataDao;
	}
	
	@PostConstruct
	public void init() {
		LOG.info("Images will be saved into in-memory database");
	}
	
	@Override
	public void save(MultipartFile file, Image image) {
		try {
			ImageData imageData = new ImageData();
			imageData.setImage(image);
			imageData.setContent(file.getBytes());
			
			ImageData entity = imageDataDao.save(imageData);
			LOG.debug("Image's data entity saved to database ({})", entity);

		} catch (IOException e) {
			// throw RuntimeException for rolling back transaction
			throw new ImagePersistenceException(e);
		}
	}
	
	@Override
	public ImageDto get(Image image) {
		ImageData imageData = imageDataDao.findByImage(image);
		if (imageData == null) {
			LOG.warn("Found image without content: #{}", image.getId());
			return null;
		}
		
		return new DbImageDto(imageData);
	}
	
}
