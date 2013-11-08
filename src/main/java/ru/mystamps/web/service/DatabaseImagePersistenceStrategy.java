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

import javax.inject.Inject;

import org.apache.commons.lang3.Validate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.multipart.MultipartFile;

import ru.mystamps.web.dao.ImageDao;
import ru.mystamps.web.dao.ImageDataDao;
import ru.mystamps.web.entity.Image;
import ru.mystamps.web.entity.ImageData;
import ru.mystamps.web.service.dto.DbImageDto;
import ru.mystamps.web.service.dto.ImageDto;
import ru.mystamps.web.service.exception.ImagePersistenceException;

public class DatabaseImagePersistenceStrategy implements ImagePersistenceStrategy {
	private static final Logger LOG =
		LoggerFactory.getLogger(DatabaseImagePersistenceStrategy.class);
	
	private final ImageDao imageDao;
	private final ImageDataDao imageDataDao;
	
	@Inject
	public DatabaseImagePersistenceStrategy(ImageDao imageDao, ImageDataDao imageDataDao) {
		this.imageDao = imageDao;
		this.imageDataDao = imageDataDao;
	}
	
	@Override
	public void save(MultipartFile file, Image entity) {
		try {
			ImageData imageData = new ImageData();
			imageData.setImage(entity);
			imageData.setContent(file.getBytes());
			imageDataDao.save(imageData);

		} catch (IOException e) {
			// throw RuntimeException for rolling back transaction
			throw new ImagePersistenceException(e);
		}
	}
	
	@Override
	public ImageDto get(Integer id) {
		Validate.isTrue(id != null, "Image id must be non null");
		Validate.isTrue(id > 0, "Image id must be greater than zero");

		Image image = imageDao.findOne(id);
		if (image == null) {
			return null;
		}
		
		ImageData imageData = imageDataDao.findByImage(image);
		if (imageData == null) {
			LOG.warn("Found image without content: #{}", id);
			return null;
		}
		
		return new DbImageDto(imageData);
	}
	
}
