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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.access.prepost.PreAuthorize;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.dao.CollectionDao;
import ru.mystamps.web.dao.dto.AddCollectionDbDto;
import ru.mystamps.web.dao.dto.CollectionInfoDto;
import ru.mystamps.web.dao.dto.LinkEntityDto;
import ru.mystamps.web.dao.dto.UrlEntityDto;
import ru.mystamps.web.support.spring.security.HasAuthority;
import ru.mystamps.web.util.SlugUtils;

@RequiredArgsConstructor
public class CollectionServiceImpl implements CollectionService {
	private static final Logger LOG = LoggerFactory.getLogger(CollectionServiceImpl.class);
	
	private final CollectionDao collectionDao;
	
	@Override
	@Transactional
	public void createCollection(Integer ownerId, String ownerLogin) {
		Validate.isTrue(ownerId != null, "Owner id must be non null");
		Validate.isTrue(ownerLogin != null, "Owner login must be non null");
		
		AddCollectionDbDto collection = new AddCollectionDbDto();
		collection.setOwnerId(ownerId);
		
		String slug = SlugUtils.slugify(ownerLogin);
		Validate.isTrue(
			StringUtils.isNotEmpty(slug),
			"Slug for string '%s' must be non empty", ownerLogin
		);
		collection.setSlug(slug);
		
		Integer id = collectionDao.add(collection);
		
		LOG.info("Collection #{} has been created ({})", id, collection);
	}
	
	@Override
	@Transactional
	@PreAuthorize(HasAuthority.UPDATE_COLLECTION)
	public UrlEntityDto addToCollection(Integer userId, Integer seriesId) {
		Validate.isTrue(userId != null, "User id must be non null");
		Validate.isTrue(seriesId != null, "Series id must be non null");
		
		UrlEntityDto url = collectionDao.findCollectionUrlEntityByUserId(userId);
		Integer collectionId = url.getId();
		
		collectionDao.addSeriesToCollection(collectionId, seriesId);
		
		LOG.info(
			"Series #{} has been added to collection #{} of user #{}",
			seriesId,
			collectionId,
			userId
		);
		
		return url;
	}
	
	@Override
	@Transactional
	@PreAuthorize(HasAuthority.UPDATE_COLLECTION)
	public UrlEntityDto removeFromCollection(Integer userId, Integer seriesId) {
		Validate.isTrue(userId != null, "User id must be non null");
		Validate.isTrue(seriesId != null, "Series id must be non null");
		
		UrlEntityDto url = collectionDao.findCollectionUrlEntityByUserId(userId);
		Integer collectionId = url.getId();
		
		collectionDao.removeSeriesFromCollection(collectionId, seriesId);
		
		LOG.info(
			"Series #{} has been removed from collection of user #{}",
			seriesId,
			userId
		);
		
		return url;
	}
	
	@Override
	@Transactional(readOnly = true)
	public boolean isSeriesInCollection(Integer userId, Integer seriesId) {
		Validate.isTrue(seriesId != null, "Series id must be non null");
		
		if (userId == null) {
			// Anonymous user doesn't have collection
			return false;
		}
		
		boolean isSeriesInCollection = collectionDao.isSeriesInUserCollection(userId, seriesId);
		
		LOG.debug(
			"Series #{} belongs to collection of user #{}: {}",
			seriesId,
			userId,
			isSeriesInCollection
		);
		
		return isSeriesInCollection;
	}
	
	@Override
	@Transactional(readOnly = true)
	public long countCollectionsOfUsers() {
		return collectionDao.countCollectionsOfUsers();
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<LinkEntityDto> findRecentlyCreated(int quantity) {
		Validate.isTrue(quantity > 0, "Quantity must be greater than 0");
		
		return collectionDao.findLastCreated(quantity);
	}
	
	@Override
	@Transactional(readOnly = true)
	public CollectionInfoDto findById(Integer collectionId) {
		Validate.isTrue(collectionId != null, "Collection id must be non null");
		
		return collectionDao.findCollectionInfoById(collectionId);
	}
	
}
