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
package ru.mystamps.web.feature.collection;

import ru.mystamps.web.common.LinkEntityDto;
import ru.mystamps.web.common.SitemapInfoDto;

import java.util.Date;
import java.util.List;
import java.util.Map;

@SuppressWarnings("PMD.TooManyMethods")
public interface CollectionService {
	void createCollection(Integer ownerId, String ownerLogin);
	void addToCollection(Integer userId, AddToCollectionDto dto);
	void removeFromCollection(Integer userId, Integer seriesId, Integer seriesInstanceId);
	boolean isSeriesInCollection(Integer userId, Integer seriesId);
	Map<Integer, Integer> findSeriesInstances(Integer userId, Integer seriesId);
	long countCollectionsOfUsers();
	long countUpdatedSince(Date date);
	long countSeriesOf(Integer collectionId);
	long countStampsOf(Integer collectionId);
	List<LinkEntityDto> findRecentlyCreated(int quantity);
	List<SeriesInCollectionDto> findSeriesInCollection(Integer collectionId, String lang);
	List<SeriesInCollectionWithPriceDto> findSeriesWithPricesBySlug(String slug, String lang);
	CollectionInfoDto findBySlug(String slug);
	List<SitemapInfoDto> findAllForSitemap();
}
