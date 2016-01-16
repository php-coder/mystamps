/*
 * Copyright (C) 2009-2016 Slava Semushin <slava.semushin@gmail.com>
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

import java.util.List;

import ru.mystamps.web.dao.dto.AddSeriesDbDto;
import ru.mystamps.web.service.dto.SeriesInfoDto;
import ru.mystamps.web.service.dto.SitemapInfoDto;

// TODO: move stamps related methods to separate interface (#88)
@SuppressWarnings("PMD.TooManyMethods")
public interface JdbcSeriesDao {
	Integer add(AddSeriesDbDto series);
	Iterable<SitemapInfoDto> findAllForSitemap();
	Iterable<SeriesInfoDto> findLastAdded(int quantity, String lang);
	List<SeriesInfoDto> findByIdsAsSeriesInfo(List<Integer> seriesIds, String lang);
	Iterable<SeriesInfoDto> findByCategoryIdAsSeriesInfo(Integer categoryId, String lang);
	Iterable<SeriesInfoDto> findByCountryIdAsSeriesInfo(Integer countryId, String lang);
	Iterable<SeriesInfoDto> findByCollectionIdAsSeriesInfo(Integer collectionId, String lang);
	long countAll();
	long countAllStamps();
	long countSeriesOfCollection(Integer collectionId);
	long countStampsOfCollection(Integer collectionId);
	
	List<Integer> findSeriesIdsByMichelNumberCode(String michelNumber);
	List<Integer> findSeriesIdsByScottNumberCode(String scottNumber);
	List<Integer> findSeriesIdsByYvertNumberCode(String yvertNumber);
	List<Integer> findSeriesIdsByGibbonsNumberCode(String gibbonsNumber);
}
