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
package ru.mystamps.web.feature.series;

import javax.validation.GroupSequence;

import org.apache.commons.lang3.StringUtils;

import org.hibernate.validator.constraints.URL;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

import ru.mystamps.web.controller.interceptor.DownloadImageInterceptor;
import ru.mystamps.web.support.beanvalidation.Group;
import ru.mystamps.web.support.beanvalidation.ImageFile;
import ru.mystamps.web.support.beanvalidation.MaxFileSize;
import ru.mystamps.web.support.beanvalidation.MaxFileSize.Unit;
import ru.mystamps.web.support.beanvalidation.NotEmptyFile;
import ru.mystamps.web.support.beanvalidation.NotEmptyFilename;

import static ru.mystamps.web.validation.ValidationRules.MAX_IMAGE_SIZE;

@Getter
@Setter
@RequireImageOrImageUrl(
	imageFieldName = DownloadImageInterceptor.UPLOADED_IMAGE_FIELD_NAME,
	imageUrlFieldName = DownloadImageInterceptor.IMAGE_URL_FIELD_NAME,
	groups = AddImageForm.ImageUrl1Checks.class
)
public class AddImageForm implements AddImageDto, HasImageOrImageUrl, NullableImageUrl {
	
	// Name of this field should match with the value of
	// DownloadImageInterceptor.UPLOADED_IMAGE_FIELD_NAME.
	@NotEmptyFilename(groups = RequireImageCheck.class)
	@NotEmptyFile(groups = Group.Level1.class)
	@MaxFileSize(value = MAX_IMAGE_SIZE, unit = Unit.Kbytes, groups = Group.Level2.class)
	@ImageFile(groups = Group.Level2.class)
	private MultipartFile uploadedImage;
	
	// Name of this field must match with the value of DownloadImageInterceptor.IMAGE_URL_FIELD_NAME
	@URL(groups = ImageUrl2Checks.class)
	private String imageUrl;
	
	// This field holds a file that was downloaded from imageUrl.
	// Name of this field must match with the value of
	// DownloadImageInterceptor.DOWNLOADED_IMAGE_FIELD_NAME.
	@NotEmptyFile(groups = Group.Level1.class)
	@MaxFileSize(value = MAX_IMAGE_SIZE, unit = Unit.Kbytes, groups = Group.Level2.class)
	@ImageFile(groups = Group.Level2.class)
	private MultipartFile downloadedImage;
	
	@Override
	public MultipartFile getImage() {
		if (hasImage()) {
			return uploadedImage;
		}
		
		return downloadedImage;
	}
	
	// This method has to be implemented to satisfy HasImageOrImageUrl requirements.
	// The latter is being used by RequireImageOrImageUrl validator.
	@Override
	public boolean hasImage() {
		return uploadedImage != null && StringUtils.isNotEmpty(uploadedImage.getOriginalFilename());
	}
	
	// This method has to be implemented to satisfy HasImageOrImageUrl requirements.
	// The latter is being used by RequireImageOrImageUrl validator.
	@Override
	public boolean hasImageUrl() {
		return StringUtils.isNotEmpty(imageUrl);
	}
	
	public interface RequireImageCheck {
	}
	
	@GroupSequence({
		ImageUrl1Checks.class,
		ImageUrl2Checks.class,
	})
	public interface ImageUrlChecks {
	}
	
	public interface ImageUrl1Checks {
	}
	
	public interface ImageUrl2Checks {
	}
	
	@GroupSequence({
		Group.Level1.class,
		Group.Level2.class
	})
	public interface ImageChecks {
	}
	
}
