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

import ru.mystamps.web.dao.dto.CollectionInfoDto;
import ru.mystamps.web.dao.dto.LinkEntityDto;
import ru.mystamps.web.dao.dto.UrlEntityDto;

public interface CollectionService {
	void createCollection(Integer ownerId, String ownerLogin);
	UrlEntityDto addToCollection(Integer userId, Integer seriesId);
	UrlEntityDto removeFromCollection(Integer userId, Integer seriesId);
	boolean isSeriesInCollection(Integer userId, Integer seriesId);
	long countCollectionsOfUsers();
	List<LinkEntityDto> findRecentlyCreated(int quantity);
	CollectionInfoDto findById(Integer collectionId);
}
