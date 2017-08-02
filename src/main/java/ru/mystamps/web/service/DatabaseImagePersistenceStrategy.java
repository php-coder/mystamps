/*
 * Copyright (C) 2009-2017 Slava Semushin <slava.semushin@gmail.com>
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.dao.ImageDataDao;
import ru.mystamps.web.dao.dto.AddImageDataDbDto;
import ru.mystamps.web.dao.dto.DbImageDto;
import ru.mystamps.web.dao.dto.ImageDto;
import ru.mystamps.web.dao.dto.ImageInfoDto;
import ru.mystamps.web.service.exception.ImagePersistenceException;

@RequiredArgsConstructor
public class DatabaseImagePersistenceStrategy implements ImagePersistenceStrategy {
	private static final Logger LOG =
		LoggerFactory.getLogger(DatabaseImagePersistenceStrategy.class);
	
	private final ImageDataDao imageDataDao;
	
	@PostConstruct
	public void init() {
		LOG.info("Images will be saved into in-memory database");
	}
	
	@Override
	public void save(MultipartFile file, ImageInfoDto image) {
		try {
			AddImageDataDbDto imageData = new AddImageDataDbDto();
			imageData.setImageId(image.getId());
			imageData.setContent(file.getBytes());
			imageData.setPreview(false);
			
			Integer id = imageDataDao.add(imageData);
			LOG.info("Image #{}: meta data has been saved to #{}", image.getId(), id);

		} catch (IOException e) {
			// throw RuntimeException for rolling back transaction
			throw new ImagePersistenceException(e);
		}
	}
	
	@Override
	public void savePreview(byte[] data, ImageInfoDto image) {
		AddImageDataDbDto imageData = new AddImageDataDbDto();
		imageData.setImageId(image.getId());
		imageData.setContent(data);
		imageData.setPreview(true);
		
		imageDataDao.add(imageData);
		
		LOG.info("Image #{}: preview has been saved", image.getId());
	}
	
	@Override
	public ImageDto get(ImageInfoDto image) {
		DbImageDto imageDto = imageDataDao.findByImageId(image.getId(), false);
		if (imageDto == null) {
			LOG.warn("Image #{}: content not found", image.getId());
			return null;
		}
		
		return imageDto;
	}
	
	@Override
	public ImageDto getPreview(ImageInfoDto image) {
		DbImageDto imageDto = imageDataDao.findByImageId(image.getId(), true);
		if (imageDto == null) {
			LOG.info("Image #{}: preview not found", image.getId());
			return null;
		}
		
		return imageDto;
	}
	
}
