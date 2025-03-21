/*
 * Copyright (C) 2009-2025 Slava Semushin <slava.semushin@gmail.com>
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

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.URL;
import org.springframework.web.multipart.MultipartFile;
import ru.mystamps.web.common.LinkEntityDto;
import ru.mystamps.web.feature.category.Category;
import ru.mystamps.web.feature.country.Country;
import ru.mystamps.web.support.beanvalidation.ImageFile;
import ru.mystamps.web.support.beanvalidation.MaxFileSize;
import ru.mystamps.web.support.beanvalidation.MaxFileSize.Unit;
import ru.mystamps.web.support.beanvalidation.NotEmptyFile;
import ru.mystamps.web.support.beanvalidation.NotEmptyFilename;
import ru.mystamps.web.support.beanvalidation.NotNullIfFirstField;
import ru.mystamps.web.support.beanvalidation.Price;

import javax.validation.GroupSequence;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

import static ru.mystamps.web.feature.image.ImageValidation.MAX_IMAGE_SIZE;
import static ru.mystamps.web.feature.series.SeriesValidation.MAX_DAYS_IN_MONTH;
import static ru.mystamps.web.feature.series.SeriesValidation.MAX_MONTHS_IN_YEAR;
import static ru.mystamps.web.feature.series.SeriesValidation.MAX_STAMPS_IN_SERIES;
import static ru.mystamps.web.feature.series.SeriesValidation.MIN_RELEASE_YEAR;
import static ru.mystamps.web.feature.series.SeriesValidation.MIN_STAMPS_IN_SERIES;

@Getter
@Setter
@RequireImageOrImageUrl(
	imageFieldName = DownloadImageInterceptor.UPLOADED_IMAGE_FIELD_NAME,
	imageUrlFieldName = DownloadImageInterceptor.IMAGE_URL_FIELD_NAME,
	groups = AddSeriesForm.ImageUrl1Checks.class
)
@NotNullIfFirstField(
	first = "month", second = "year", message = "{month.requires.year}",
	groups = AddSeriesForm.ReleaseDate1Checks.class
)
@NotNullIfFirstField(
	first = "day", second = "month", message = "{day.requires.month}",
	groups = AddSeriesForm.ReleaseDate1Checks.class
)
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
	
	// @todo #1277 /series/add: add integration test to check that Michel numbers may contain letter
	@CatalogNumbers(allowLetters = true)
	private String michelNumbers;
	
	@Price
	private BigDecimal michelPrice;
	
	// @todo #671 /series/add: add integration test to check that Scott numbers may contain letters
	// @todo #671 /series/add: add integration test for Scott numbers error message
	@CatalogNumbers(allowLetters = true)
	private String scottNumbers;
	
	@Price
	private BigDecimal scottPrice;
	
	// @todo #1421 /series/add: add integration test to check that Yvert numbers may contain letters
	@CatalogNumbers(allowLetters = true)
	private String yvertNumbers;
	
	@Price
	private BigDecimal yvertPrice;
	
	@CatalogNumbers
	private String gibbonsNumbers;
	
	@Price
	private BigDecimal gibbonsPrice;

	// @todo #770 /series/add: validate that Solovyov numbers are specified only for stamps
	//  from USSR/Russia
	@CatalogNumbers
	private String solovyovNumbers;

	@Price
	private BigDecimal solovyovPrice;
	
	// @todo #769 /series/add: validate that Zagorski numbers are specified only for stamps
	//  from USSR/Russia
	@CatalogNumbers
	private String zagorskiNumbers;

	@Price
	private BigDecimal zagorskiPrice;
	
	// Name of this field should match with the value of
	// DownloadImageInterceptor.UPLOADED_IMAGE_FIELD_NAME.
	@NotEmptyFilename(groups = RequireImageCheck.class)
	@NotEmptyFile(groups = Image1Checks.class)
	@MaxFileSize(value = MAX_IMAGE_SIZE, unit = Unit.Kbytes, groups = Image2Checks.class)
	@ImageFile(groups = Image2Checks.class)
	private MultipartFile uploadedImage;
	
	// Name of this field must match with the value of DownloadImageInterceptor.IMAGE_URL_FIELD_NAME
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
