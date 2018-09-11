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
package ru.mystamps.web.dao;

import java.util.Date;
import java.util.List;

import ru.mystamps.web.dao.dto.AddSeriesDbDto;
import ru.mystamps.web.dao.dto.PurchaseAndSaleDto;
import ru.mystamps.web.dao.dto.SeriesFullInfoDto;
import ru.mystamps.web.dao.dto.SeriesInfoDto;
import ru.mystamps.web.dao.dto.SeriesLinkDto;
import ru.mystamps.web.dao.dto.SitemapInfoDto;

// TODO: move stamps related methods to separate interface (#88)
@SuppressWarnings("PMD.TooManyMethods")
public interface SeriesDao {
	Integer add(AddSeriesDbDto series);
	void markAsModified(Integer seriesId, Date updateAt, Integer updatedBy);
	List<SitemapInfoDto> findAllForSitemap();
	List<SeriesLinkDto> findLastAdded(int quantity, String lang);
	SeriesFullInfoDto findByIdAsSeriesFullInfo(Integer seriesId, String lang);
	List<SeriesInfoDto> findByIdsAsSeriesInfo(List<Integer> seriesIds, String lang);
	List<SeriesInfoDto> findByCategorySlugAsSeriesInfo(String slug, String lang);
	List<SeriesInfoDto> findByCountrySlugAsSeriesInfo(String slug, String lang);
	List<PurchaseAndSaleDto> findPurchasesAndSales(Integer seriesId);

	long countAll();
	long countAllStamps();
	long countSeriesById(Integer seriesId);
	long countAddedSince(Date date);
	long countUpdatedSince(Date date);
	
	Integer findQuantityById(Integer seriesId);
}
