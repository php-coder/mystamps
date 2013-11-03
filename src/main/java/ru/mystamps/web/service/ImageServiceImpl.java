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

import javax.inject.Inject;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import ru.mystamps.web.entity.Image;
import ru.mystamps.web.service.dto.ImageDto;

public class ImageServiceImpl implements ImageService {
	
	private final ImagePersistenceStrategy imagePersistenceStrategy;
	
	@Inject
	public ImageServiceImpl(ImagePersistenceStrategy imagePersistenceStrategy) {
		this.imagePersistenceStrategy = imagePersistenceStrategy;
	}
	
	@Override
	@Transactional
	public String save(MultipartFile file) {
		Integer imageId = imagePersistenceStrategy.save(file);
		
		return GET_IMAGE_PAGE.replace("{id}", String.valueOf(imageId));
	}
	
	@Override
	@Transactional(readOnly = true)
	public ImageDto get(Integer imageId) {
		Image image = imagePersistenceStrategy.get(imageId);
		if (image == null) {
			return null;
		}
		
		return new ImageDtoImpl(image);
	}
	
	private final class ImageDtoImpl implements ImageDto {
		private final Image image;

		ImageDtoImpl(Image image) {
			// image must be non null, caller must care about this
			this.image = image;
		}

		@Override
		public String getType() {
			return image.getType().toString();
		}

		@Override
		public byte[] getData() {
			return image.getData();
		}
	}
	
}
