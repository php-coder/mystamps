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
package ru.mystamps.web.service

import java.io.IOException
import java.util.Locale

import javax.inject.Inject

import org.apache.commons.lang3.{StringUtils, Validate}

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

import ru.mystamps.web.dao.ImageDao
import ru.mystamps.web.entity.Image

@Service
class ImageServiceImpl implements ImageService {
	
	@Inject
	private ImageDao imageDao
	
	@Transactional
	override def save(file: MultipartFile): String = {
		Validate.isTrue(file != null, "File should be non null")
		Validate.isTrue(file.getSize() > 0, "Image size must be greater than zero")
		
		String contentType = file.getContentType()
		Validate.isTrue(contentType != null, "File type must be non null")
		
		String extension = StringUtils.substringAfter(contentType, "/")
		Validate.validState(
			"png".equals(extension) || "jpeg".equals(extension),
			"File type must be PNG or JPEG image"
		)
		
		Image image = new Image()
		image.setType(Image.Type.valueOf(extension.toUpperCase(Locale.US)))
		
		try {
			image.setData(file.getBytes())
		} catch (IOException e) {
			// throw RuntimeException for rolling back transaction
			throw new RuntimeException(e) // NOPMD
		}
		
		Image entity = imageDao.save(image)
		
		return GET_IMAGE_PAGE.replace("{id}", String.valueOf(entity.getId()))
	}
	
}
