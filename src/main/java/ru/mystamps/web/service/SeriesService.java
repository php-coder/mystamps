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
package ru.mystamps.web.service;

import java.util.List;

import ru.mystamps.web.service.dto.AddImageDto;
import ru.mystamps.web.service.dto.AddSeriesDto;
import ru.mystamps.web.service.dto.SeriesInfoDto;
import ru.mystamps.web.service.dto.SitemapInfoDto;

// TODO: move stamps related methods to separate interface (#88)
@SuppressWarnings("PMD.TooManyMethods")
public interface SeriesService {
	Integer add(AddSeriesDto dto, Integer userId, boolean userCanAddComments);
	void addImageToSeries(AddImageDto dto, Integer seriesId, Integer userId);
	long countAll();
	long countAllStamps();
	long countSeriesOf(Integer collectionId);
	long countStampsOf(Integer collectionId);
	
	List<SeriesInfoDto> findByMichelNumber(String michelNumberCode, String lang);
	List<SeriesInfoDto> findByScottNumber(String scottNumberCode, String lang);
	List<SeriesInfoDto> findByYvertNumber(String yvertNumberCode, String lang);
	List<SeriesInfoDto> findByGibbonsNumber(String gibbonsNumberCode, String lang);
	
	Iterable<SeriesInfoDto> findByCategoryId(Integer categoryId, String lang);
	Iterable<SeriesInfoDto> findByCountryId(Integer countryId, String lang);
	Iterable<SeriesInfoDto> findByCollectionId(Integer collectionId, String lang);
	Iterable<SeriesInfoDto> findRecentlyAdded(int quantity, String lang);
	Iterable<SitemapInfoDto> findAllForSitemap();
}
