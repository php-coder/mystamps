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
package ru.mystamps.web.service;

import java.util.Date;
import java.util.List;

import ru.mystamps.web.dao.dto.PurchaseAndSaleDto;
import ru.mystamps.web.dao.dto.SeriesInCollectionDto;
import ru.mystamps.web.dao.dto.SeriesInfoDto;
import ru.mystamps.web.dao.dto.SitemapInfoDto;
import ru.mystamps.web.service.dto.AddImageDto;
import ru.mystamps.web.service.dto.AddSeriesDto;
import ru.mystamps.web.service.dto.SeriesDto;

// TODO: move stamps related methods to separate interface (#88)
@SuppressWarnings("PMD.TooManyMethods")
public interface SeriesService {
	Integer add(AddSeriesDto dto, Integer userId, boolean userCanAddComments);
	void addImageToSeries(AddImageDto dto, Integer seriesId, Integer userId);
	long countAll();
	long countAllStamps();
	long countSeriesOf(Integer collectionId);
	// @todo #477 SeriesService.countStampsOf(): rename to CollectionService.countStampsOf()
	long countStampsOf(Integer collectionId);
	long countAddedSince(Date date);
	long countUpdatedSince(Date date);
	boolean isSeriesExist(Integer seriesId);
	Integer findQuantityById(Integer seriesId);
	
	SeriesDto findFullInfoById(Integer seriesId, String lang);
	
	List<SeriesInfoDto> findByMichelNumber(String michelNumberCode, String lang);
	List<SeriesInfoDto> findByScottNumber(String scottNumberCode, String lang);
	List<SeriesInfoDto> findByYvertNumber(String yvertNumberCode, String lang);
	List<SeriesInfoDto> findByGibbonsNumber(String gibbonsNumberCode, String lang);
	List<SeriesInfoDto> findBySolovyovNumber(String solovyovCatalogNumber, String lang);
	List<SeriesInfoDto> findByZagorskiNumber(String zagorskiCatalogNumber, String lang);
	
	List<SeriesInfoDto> findByCategorySlug(String slug, String lang);
	List<SeriesInfoDto> findByCountrySlug(String slug, String lang);
	// @todo #477 SeriesService.findByCollectionId():
	//  rename to CollectionService.findSeriesInCollection()
	List<SeriesInCollectionDto> findByCollectionId(Integer collectionId, String lang);
	List<SeriesInfoDto> findRecentlyAdded(int quantity, String lang);
	List<SitemapInfoDto> findAllForSitemap();
	
	List<PurchaseAndSaleDto> findPurchasesAndSales(Integer seriesId);
}
