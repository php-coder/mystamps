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
package ru.mystamps.web.controller.dto;

import java.math.BigDecimal;

import javax.validation.GroupSequence;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;

import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.URL;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

import ru.mystamps.web.controller.converter.annotation.Category;
import ru.mystamps.web.controller.converter.annotation.Country;
import ru.mystamps.web.dao.dto.LinkEntityDto;
import ru.mystamps.web.service.dto.AddSeriesDto;
import ru.mystamps.web.support.beanvalidation.CatalogNumbers;
import ru.mystamps.web.support.beanvalidation.HasImageOrImageUrl;
import ru.mystamps.web.support.beanvalidation.ImageFile;
import ru.mystamps.web.support.beanvalidation.MaxFileSize;
import ru.mystamps.web.support.beanvalidation.MaxFileSize.Unit;
import ru.mystamps.web.support.beanvalidation.NotEmptyFile;
import ru.mystamps.web.support.beanvalidation.NotEmptyFilename;
import ru.mystamps.web.support.beanvalidation.NotNullIfFirstField;
import ru.mystamps.web.support.beanvalidation.Price;
import ru.mystamps.web.support.beanvalidation.ReleaseDateIsNotInFuture;
import ru.mystamps.web.support.beanvalidation.RequireImageOrImageUrl;

import static ru.mystamps.web.validation.ValidationRules.MAX_DAYS_IN_MONTH;
import static ru.mystamps.web.validation.ValidationRules.MAX_IMAGE_SIZE;
import static ru.mystamps.web.validation.ValidationRules.MAX_MONTHS_IN_YEAR;
import static ru.mystamps.web.validation.ValidationRules.MAX_SERIES_COMMENT_LENGTH;
import static ru.mystamps.web.validation.ValidationRules.MAX_STAMPS_IN_SERIES;
import static ru.mystamps.web.validation.ValidationRules.MIN_RELEASE_YEAR;
import static ru.mystamps.web.validation.ValidationRules.MIN_STAMPS_IN_SERIES;

@Getter
@Setter
// TODO: combine price with currency to separate class
@SuppressWarnings({ "PMD.TooManyFields", "PMD.AvoidDuplicateLiterals" })
@RequireImageOrImageUrl(groups = AddSeriesForm.ImageUrl1Checks.class)
@NotNullIfFirstField.List({
	@NotNullIfFirstField(
		first = "month", second = "year", message = "{month.requires.year}",
		groups = AddSeriesForm.ReleaseDate1Checks.class
	),
	@NotNullIfFirstField(
		first = "day", second = "month", message = "{day.requires.month}",
		groups = AddSeriesForm.ReleaseDate1Checks.class
	)
})
@ReleaseDateIsNotInFuture(groups = AddSeriesForm.ReleaseDate3Checks.class)
public class AddSeriesForm implements AddSeriesDto, HasImageOrImageUrl, NullableImageUrl {
	
	// FIXME: change type to plain Integer
	@NotNull
	@Category
	private LinkEntityDto category;
	
	// FIXME: change type to plain Integer
	@Country
	private LinkEntityDto country;
	
	@Range(min = 1, max = MAX_DAYS_IN_MONTH, message = "{day.invalid}")
	private Integer day;
	
	@Range(min = 1, max = MAX_MONTHS_IN_YEAR, message = "{month.invalid}")
	private Integer month;
	
	@Min(
		value = MIN_RELEASE_YEAR,
		message = "{series.too-early-release-year}",
		groups = ReleaseDate2Checks.class
	)
	private Integer year;
	
	@NotNull
	@Min(MIN_STAMPS_IN_SERIES)
	@Max(MAX_STAMPS_IN_SERIES)
	private Integer quantity;
	
	@NotNull
	private Boolean perforated;
	
	@CatalogNumbers
	private String michelNumbers;
	
	@Price
	private BigDecimal michelPrice;
	
	@CatalogNumbers
	private String scottNumbers;
	
	@Price
	private BigDecimal scottPrice;
	
	@CatalogNumbers
	private String yvertNumbers;
	
	@Price
	private BigDecimal yvertPrice;
	
	@CatalogNumbers
	private String gibbonsNumbers;
	
	@Price
	private BigDecimal gibbonsPrice;
	
	@Size(max = MAX_SERIES_COMMENT_LENGTH, message = "{value.too-long}")
	private String comment;
	
	// Name of this field should match with the value of
	// DownloadImageInterceptor.UPLOADED_IMAGE_FIELD_NAME.
	@NotEmptyFilename(groups = RequireImageCheck.class)
	@NotEmptyFile(groups = Image1Checks.class)
	@MaxFileSize(value = MAX_IMAGE_SIZE, unit = Unit.Kbytes, groups = Image2Checks.class)
	@ImageFile(groups = Image2Checks.class)
	private MultipartFile image;
	
	// Name of this field must match with the value of DownloadImageInterceptor.URL_PARAMETER_NAME.
	@URL(groups = ImageUrl2Checks.class)
	private String imageUrl;
	
	// This field holds a file that was downloaded from imageUrl.
	// Name of this field must match with the value of
	// DownloadImageInterceptor.DOWNLOADED_IMAGE_FIELD_NAME.
	@NotEmptyFile(groups = Image1Checks.class)
	@MaxFileSize(value = MAX_IMAGE_SIZE, unit = Unit.Kbytes, groups = Image2Checks.class)
	@ImageFile(groups = Image2Checks.class)
	private MultipartFile downloadedImage;
	
	@Override
	public MultipartFile getImage() {
		if (hasImage()) {
			return image;
		}
		
		return downloadedImage;
	}
	
	// This method has to be implemented to satisfy HasImageOrImageUrl requirements.
	// The latter is being used by RequireImageOrImageUrl validator.
	@Override
	public boolean hasImage() {
		return image != null && StringUtils.isNotEmpty(image.getOriginalFilename());
	}
	
	// This method has to be implemented to satisfy HasImageOrImageUrl requirements.
	// The latter is being used by RequireImageOrImageUrl validator.
	@Override
	public boolean hasImageUrl() {
		return StringUtils.isNotEmpty(imageUrl);
	}
	
	@GroupSequence({
		ReleaseDate1Checks.class,
		ReleaseDate2Checks.class,
		ReleaseDate3Checks.class
	})
	public interface ReleaseDateChecks {
	}
	
	public interface ReleaseDate1Checks {
	}
	
	public interface ReleaseDate2Checks {
	}
	
	public interface ReleaseDate3Checks {
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
		Image1Checks.class,
		Image2Checks.class
	})
	public interface ImageChecks {
	}
	
	public interface Image1Checks {
	}
	
	public interface Image2Checks {
	}
	
}
