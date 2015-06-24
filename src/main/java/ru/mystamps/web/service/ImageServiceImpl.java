/*
 * Copyright (C) 2009-2015 Slava Semushin <slava.semushin@gmail.com>
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

import java.util.Locale;

import org.apache.commons.lang3.Validate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.dao.ImageDao;
import ru.mystamps.web.entity.Image;
import ru.mystamps.web.service.dto.ImageDto;
import ru.mystamps.web.service.exception.ImagePersistenceException;

import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.commons.lang3.StringUtils.substringBefore;

@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
	private static final Logger LOG = LoggerFactory.getLogger(ImageServiceImpl.class);
	
	private final ImagePersistenceStrategy imagePersistenceStrategy;
	private final ImageDao imageDao;
	
	@Override
	@Transactional
	public Image save(MultipartFile file) {
		Validate.isTrue(file != null, "File should be non null");
		Validate.isTrue(file.getSize() > 0, "Image size must be greater than zero");
		
		String contentType = file.getContentType();
		Validate.isTrue(contentType != null, "File type must be non null");
		
		String extension = extractExtensionFromContentType(contentType);
		Validate.validState(
				"png".equals(extension) || "jpeg".equals(extension),
				"File type must be PNG or JPEG image, but '%s' (%s) were passed",
				contentType, extension
		);
		
		Image image = new Image();
		image.setType(Image.Type.valueOf(extension.toUpperCase(Locale.US)));
		
		Image entity = imageDao.save(image);
		if (entity == null) {
			throw new ImagePersistenceException("Can't save image");
		}
		
		LOG.info("Image entity was saved to database ({})", entity);
		
		imagePersistenceStrategy.save(file, entity);
		
		return entity;
	}
	
	@Override
	@Transactional(readOnly = true)
	public ImageDto get(Integer imageId) {
		Validate.isTrue(imageId != null, "Image id must be non null");
		Validate.isTrue(imageId > 0, "Image id must be greater than zero");
		
		Image image = imageDao.findOne(imageId);
		if (image == null) {
			return null;
		}
		
		return imagePersistenceStrategy.get(image);
	}
	
	private static String extractExtensionFromContentType(String contentType) {
		// "image/jpeg; charset=UTF-8" -> "jpeg"
		return substringBefore(substringAfter(contentType, "/"), ";");
	}
	
}
