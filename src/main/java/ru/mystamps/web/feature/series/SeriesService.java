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
package ru.mystamps.web.feature.series;

import ru.mystamps.web.common.SitemapInfoDto;

import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static ru.mystamps.web.feature.series.SeriesValidation.MAX_SERIES_COMMENT_LENGTH;

// FIXME: move stamps related methods to separate interface (#88)
public interface SeriesService {
	Integer add(AddSeriesDto dto, Integer userId);

	// @todo #1411 Configure MethodValidationPostProcessor to use messages from ValidationMessages.properties
	void addComment(
		Integer seriesId,
		Integer userId,
		// @NotBlank isn't needed because PatchRequest.value has @NotEmpty and its setter trims blank values to null
		@Size(max = MAX_SERIES_COMMENT_LENGTH, message = "Value is greater than allowable maximum of {max} characters")
		String comment
	);
	
	void addReleaseYear(Integer seriesId, Integer year, Integer userId);
	void addCatalogPrice(StampsCatalog catalog, Integer seriesId, BigDecimal price, Integer userId);
	void addCatalogNumbers(StampsCatalog catalog, Integer seriesId, String numbers, Integer userId);
	void addImageToSeries(AddImageDto dto, Integer seriesId, Integer userId);
	void replaceImage(ReplaceImageDto dto, Integer seriesId, Integer userId);
	long countAll();
	long countAllStamps();
	long countAddedSince(Date date);
	long countUpdatedSince(Date date);
	boolean isSeriesExist(Integer seriesId);
	Integer findQuantityById(Integer seriesId);
	
	SeriesDto findFullInfoById(
		Integer seriesId,
		Integer userId,
		String lang,
		boolean userCanSeeHiddenImages
	);
	
	List<SeriesInfoDto> findByMichelNumber(String michelNumberCode, String lang);
	List<SeriesInfoDto> findByScottNumber(String scottNumberCode, String lang);
	List<SeriesInfoDto> findByYvertNumber(String yvertNumberCode, String lang);
	List<SeriesInfoDto> findByGibbonsNumber(String gibbonsNumberCode, String lang);
	List<SeriesInfoDto> findBySolovyovNumber(String solovyovCatalogNumber, String lang);
	List<SeriesInfoDto> findByZagorskiNumber(String zagorskiCatalogNumber, String lang);
	
	List<SeriesInfoDto> findByCategorySlug(String slug, String lang);
	List<SeriesInGalleryDto> findByCountrySlug(String slug, String lang);
	List<SeriesLinkDto> findRecentlyAdded(int quantity, String lang);
	List<SeriesLinkDto> findSimilarSeries(Integer seriesId, String lang);
	List<SitemapInfoDto> findAllForSitemap();
	
	void markAsSimilar(AddSimilarSeriesForm dto);
}
