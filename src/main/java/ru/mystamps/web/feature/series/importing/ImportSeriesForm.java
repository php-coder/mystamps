/*
 * Copyright (C) 2009-2024 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.feature.series.importing;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.URL;
import org.springframework.web.multipart.MultipartFile;
import ru.mystamps.web.common.LinkEntityDto;
import ru.mystamps.web.feature.category.Category;
import ru.mystamps.web.feature.country.Country;
import ru.mystamps.web.feature.series.AddSeriesDto;
import ru.mystamps.web.feature.series.CatalogNumbers;
import ru.mystamps.web.feature.series.NullableImageUrl;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

import static ru.mystamps.web.feature.series.SeriesValidation.MAX_DAYS_IN_MONTH;
import static ru.mystamps.web.feature.series.SeriesValidation.MAX_MONTHS_IN_YEAR;
import static ru.mystamps.web.feature.series.SeriesValidation.MAX_STAMPS_IN_SERIES;
import static ru.mystamps.web.feature.series.SeriesValidation.MIN_RELEASE_YEAR;
import static ru.mystamps.web.feature.series.SeriesValidation.MIN_STAMPS_IN_SERIES;

// @todo #1287 /series/import/request/{id}: add integration tests for release day and month
// @todo #1287 /series/import/request/{id}: month is required when day is specified
// @todo #1287 /series/import/request/{id}: year is required when month is specified
// @todo #1287 /series/import/request/{id}: release date should be in past
@Getter
@Setter
public class ImportSeriesForm implements AddSeriesDto, NullableImageUrl {
	
	// @todo #709 /series/import/request/{id}(category): add integration test for required field
	@NotNull
	@Category
	private LinkEntityDto category;
	
	@Country
	private LinkEntityDto country;
	
	// @todo #709 /series/import/request/{id}(quantity): add integration test for required field
	// @todo #709 /series/import/request/{id}(quantity): add integration test for min value
	// @todo #709 /series/import/request/{id}(quantity): add integration test for max value
	@NotNull
	@Min(MIN_STAMPS_IN_SERIES)
	@Max(MAX_STAMPS_IN_SERIES)
	private Integer quantity;
	
	// @todo #709 /series/import/request/{id}(perforated): add integration test for required field
	@NotNull
	private Boolean perforated;
	
	// Name of this field must match with the value of DownloadImageInterceptor.IMAGE_URL_FIELD_NAME
	// @todo #709 /series/import/request/{id}(imageUrl): add validation for valid url
	@NotNull
	@URL
	private String imageUrl;
	
	// @todo #1287 /series/import/request/{id}(day): add integration test for invalid day
	@Range(min = 1, max = MAX_DAYS_IN_MONTH, message = "{day.invalid}")
	private Integer day;
	
	// @todo #1287 /series/import/request/{id}(month): add integration test for invalid month
	@Range(min = 1, max = MAX_MONTHS_IN_YEAR, message = "{month.invalid}")
	private Integer month;
	
	// @todo #709 /series/import/request/{id}(year): add validation for min value
	// @todo #709 /series/import/request/{id}(year): add validation for year in future
	@Min(value = MIN_RELEASE_YEAR, message = "{series.too-early-release-year}")
	private Integer year;
	
	// This field holds a file that was downloaded from imageUrl.
	// Name of this field must match with the value of
	// DownloadImageInterceptor.DOWNLOADED_IMAGE_FIELD_NAME.
	// @todo #709 /series/import/request/{id}(imageUrl): add integration test for required field
	// @todo #709 /series/import/request/{id}(imageUrl): add validation for non-empty file
	// @todo #709 /series/import/request/{id}(imageUrl): add validation for file size
	// @todo #709 /series/import/request/{id}(imageUrl): add validation for file type
	@NotNull
	private MultipartFile downloadedImage;
	
	// @todo #694 Import series: add support for Scott catalog numbers
	// @todo #694 Import series: add support for Yvert catalog numbers
	// @todo #694 Import series: add support for Gibbons catalog numbers
	// @todo #694 Import series: add support for Zagorski catalog numbers
	// @todo #694 Import series: add support for Solovyov catalog numbers
	// @todo #694 /series/import/request/{id}(michelNumbers): add integration test for validation
	@CatalogNumbers
	private String michelNumbers;
	
	@Valid
	private ImportSellerForm seller;
	
	@Valid
	private ImportSeriesSalesForm seriesSale;
	
	//
	// The methods bellow required for AddSeriesDto interface.
	// They are no-op methods because we don't support all values during series import.
	//
	
	@Override
	public MultipartFile getImage() {
		return downloadedImage;
	}
	
	@Override
	public BigDecimal getMichelPrice() {
		return null;
	}
	
	@Override
	public String getScottNumbers() {
		return null;
	}
	
	@Override
	public BigDecimal getScottPrice() {
		return null;
	}
	
	@Override
	public String getYvertNumbers() {
		return null;
	}
	
	@Override
	public BigDecimal getYvertPrice() {
		return null;
	}
	
	@Override
	public String getGibbonsNumbers() {
		return null;
	}
	
	@Override
	public BigDecimal getGibbonsPrice() {
		return null;
	}
	
	@Override
	public String getSolovyovNumbers() {
		return null;
	}
	
	@Override
	public BigDecimal getSolovyovPrice() {
		return null;
	}
	
	@Override
	public String getZagorskiNumbers() {
		return null;
	}
	
	@Override
	public BigDecimal getZagorskiPrice() {
		return null;
	}
	
}
