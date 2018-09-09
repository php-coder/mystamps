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
package ru.mystamps.web.feature.image;

import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import org.slf4j.Logger;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.security.access.prepost.PreAuthorize;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.Db;
import ru.mystamps.web.dao.ImageDao;
import ru.mystamps.web.dao.dto.DbImageDto;
import ru.mystamps.web.dao.dto.ImageDto;
import ru.mystamps.web.service.ImagePersistenceStrategy;
import ru.mystamps.web.service.ImagePreviewStrategy;
import ru.mystamps.web.service.exception.CreateImagePreviewException;
import ru.mystamps.web.service.exception.ImagePersistenceException;
import ru.mystamps.web.support.spring.security.HasAuthority;
import ru.mystamps.web.support.togglz.Features;

import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.commons.lang3.StringUtils.substringBefore;

@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
	
	private final Logger log;
	private final ImagePersistenceStrategy imagePersistenceStrategy;
	private final ImagePreviewStrategy imagePreviewStrategy;
	private final ImageDao imageDao;
	
	@Override
	@Transactional
	@PreAuthorize("isAuthenticated()")
	public ImageInfoDto save(MultipartFile file) {
		Validate.isTrue(file != null, "File must be non null");
		Validate.isTrue(file.getSize() > 0, "Image size must be greater than zero");
		
		String contentType = file.getContentType();
		Validate.isTrue(contentType != null, "File type must be non null");
		
		String extension = extractExtensionFromContentType(contentType);
		Validate.validState(
				StringUtils.equalsAny(extension, "png", "jpeg"),
				"File type must be PNG or JPEG image, but '%s' (%s) were passed",
				contentType, extension
		);
		
		String imageType = extension.toUpperCase(Locale.ENGLISH);
		
		// Trim and abbreviate a filename. It shouldn't fail a process because field is optional.
		String filename = StringUtils.trimToNull(file.getOriginalFilename());
		filename = abbreviateIfLengthGreaterThan(filename, Db.Images.FILENAME_LENGTH);
		
		Integer imageId = imageDao.add(imageType, filename);
		if (imageId == null) {
			throw new ImagePersistenceException("Can't save image");
		}
		
		ImageInfoDto imageInfo = new ImageInfoDto(imageId, imageType);
		log.info("Image info has been saved to database ({})", imageInfo);
		
		imagePersistenceStrategy.save(file, imageInfo);
		
		return imageInfo;
	}
	
	@Override
	@Transactional(readOnly = true)
	public ImageDto get(Integer imageId) {
		Validate.isTrue(imageId != null, "Image id must be non null");
		Validate.isTrue(imageId > 0, "Image id must be greater than zero");
		
		ImageInfoDto image = imageDao.findById(imageId);
		if (image == null) {
			return null;
		}
		
		return imagePersistenceStrategy.get(image);
	}
	
	@Override
	@Transactional
	public ImageDto getOrCreatePreview(Integer imageId) {
		Validate.isTrue(imageId != null, "Image id must be non null");
		Validate.isTrue(imageId > 0, "Image id must be greater than zero");
		
		if (!Features.SHOW_IMAGES_PREVIEW.isActive()) {
			return null;
		}
		
		ImageInfoDto previewInfo = new ImageInfoDto(imageId, "jpeg");

		// NB: the race between getPreview() and createReview() is possible.
		// If this happens, the last request will overwrite the first.
		ImageDto image = imagePersistenceStrategy.getPreview(previewInfo);
		if (image != null) {
			return image;
		}
		
		image = get(imageId);
		if (image == null) {
			return null;
		}

		return createPreview(previewInfo, image.getData());
	}
	
	@Override
	@Transactional
	@PreAuthorize(HasAuthority.CREATE_SERIES)
	public void addToSeries(Integer seriesId, Integer imageId) {
		Validate.isTrue(seriesId != null, "Series id must be non null");
		Validate.isTrue(imageId != null, "Image id must be non null");
		
		imageDao.addToSeries(seriesId, imageId);
		
		log.info("Series #{}: image #{} was added", seriesId, imageId);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Integer> findBySeriesId(Integer seriesId) {
		Validate.isTrue(seriesId != null, "Series id must be non null");
		
		return imageDao.findBySeriesId(seriesId);
	}
	
	@Override
	public void removeIfPossible(ImageInfoDto imageInfo) {
		Validate.isTrue(imageInfo != null, "Image info must be non null");
		
		imagePersistenceStrategy.removeIfPossible(imageInfo);
	}
	
	private ImageDto createPreview(ImageInfoDto previewInfo, byte[] image) {
		try {
			byte[] preview = imagePreviewStrategy.createPreview(image);
			
			imagePersistenceStrategy.savePreview(preview, previewInfo);
			
			return new DbImageDto("jpeg", preview);
			
		} catch (CreateImagePreviewException | ImagePersistenceException ex) {
			log.warn("Image #{}: couldn't create/save preview", previewInfo.getId(), ex);
			return null;
		}
	}
	
	private static String extractExtensionFromContentType(String contentType) {
		// "image/jpeg; charset=UTF-8" -> "jpeg"
		return substringBefore(substringAfter(contentType, "/"), ";");
	}
	
	private String abbreviateIfLengthGreaterThan(String text, int maxLength) {
		if (text == null || text.length() <= maxLength) {
			return text;
		}
		
		// TODO(security): fix possible log injection
		log.warn(
			"Length of value for 'filename' field ({}) exceeds max field size ({}): '{}'",
			text.length(),
			maxLength,
			text
		);
		
		return StringUtils.abbreviate(text, maxLength);
	}
	
}
