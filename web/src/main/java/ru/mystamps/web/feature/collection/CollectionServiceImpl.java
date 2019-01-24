/*
 * Copyright (C) 2009-2019 Slava Semushin <slava.semushin@gmail.com>
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

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import org.slf4j.Logger;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.access.prepost.PreAuthorize;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.dao.dto.LinkEntityDto;
import ru.mystamps.web.support.spring.security.HasAuthority;
import ru.mystamps.web.util.SlugUtils;

@RequiredArgsConstructor
@SuppressWarnings("PMD.TooManyMethods")
public class CollectionServiceImpl implements CollectionService {
	
	private final Logger log;
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
		collection.setUpdatedAt(new Date());
		
		Integer id = collectionDao.add(collection);
		
		log.info("Collection #{} has been created ({})", id, collection);
	}
	
	@Override
	@Transactional
	@PreAuthorize(HasAuthority.UPDATE_COLLECTION)
	public void addToCollection(Integer userId, AddToCollectionDto dto) {
		Validate.isTrue(userId != null, "User id must be non null");
		Validate.isTrue(dto != null, "DTO must be non null");
		Validate.isTrue(dto.getNumberOfStamps() != null, "Number of stamps must be non null");
		Validate.isTrue(dto.getSeriesId() != null, "Series id must be non null");
		
		AddToCollectionDbDto collectionDto = new AddToCollectionDbDto();
		collectionDto.setOwnerId(userId);
		collectionDto.setSeriesId(dto.getSeriesId());
		collectionDto.setNumberOfStamps(dto.getNumberOfStamps());
		
		if (dto.getPrice() != null) {
			Validate.validState(
				dto.getCurrency() != null,
				"Currency must be non null when price is specified"
			);
			collectionDto.setPrice(dto.getPrice());
			collectionDto.setCurrency(dto.getCurrency().toString());
		}
		
		collectionDao.addSeriesToUserCollection(collectionDto);
		collectionDao.markAsModified(userId, new Date());
		
		log.info(
			"Series #{} ({}) has been added to collection of user #{}",
			dto.getSeriesId(),
			formatSeriesInfo(collectionDto),
			userId
		);
	}
	
	@Override
	@Transactional
	@PreAuthorize(HasAuthority.UPDATE_COLLECTION)
	public void removeFromCollection(Integer userId, Integer seriesId) {
		Validate.isTrue(userId != null, "User id must be non null");
		Validate.isTrue(seriesId != null, "Series id must be non null");
		
		collectionDao.removeSeriesFromUserCollection(userId, seriesId);
		collectionDao.markAsModified(userId, new Date());
		
		log.info("Series #{} has been removed from collection of user #{}", seriesId, userId);
	}
	
	@Override
	@Transactional(readOnly = true)
	public boolean isSeriesInCollection(Integer userId, Integer seriesId) {
		Validate.isTrue(seriesId != null, "Series id must be non null");
		
		if (userId == null) {
			// Anonymous user doesn't have collection
			return false;
		}
		
		return collectionDao.isSeriesInUserCollection(userId, seriesId);
	}
	
	@Override
	@Transactional(readOnly = true)
	public long countCollectionsOfUsers() {
		return collectionDao.countCollectionsOfUsers();
	}
	
	@Override
	@Transactional(readOnly = true)
	public long countUpdatedSince(Date date) {
		Validate.isTrue(date != null, "Date must be non null");
		
		return collectionDao.countUpdatedSince(date);
	}
	
	@Override
	@Transactional(readOnly = true)
	public long countSeriesOf(Integer collectionId) {
		Validate.isTrue(collectionId != null, "Collection id must be non null");
		
		return collectionDao.countSeriesOfCollection(collectionId);
	}
	
	@Override
	@Transactional(readOnly = true)
	public long countStampsOf(Integer collectionId) {
		Validate.isTrue(collectionId != null, "Collection id must be non null");
		
		return collectionDao.countStampsOfCollection(collectionId);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<LinkEntityDto> findRecentlyCreated(int quantity) {
		Validate.isTrue(quantity > 0, "Quantity must be greater than 0");
		
		return collectionDao.findLastCreated(quantity);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<SeriesInCollectionDto> findSeriesInCollection(Integer collectionId, String lang) {
		Validate.isTrue(collectionId != null, "Collection id must be non null");
		
		return collectionDao.findSeriesByCollectionId(collectionId, lang);
	}
	
	@Override
	@Transactional(readOnly = true)
	@PreAuthorize(HasAuthority.ADD_SERIES_PRICE_AND_COLLECTION_OWNER)
	public List<SeriesInCollectionWithPriceDto> findSeriesWithPricesBySlug(
		String slug,
		String lang) {
		
		Validate.isTrue(slug != null, "Collection slug must be non null");
		
		return collectionDao.findSeriesWithPricesBySlug(slug, lang);
	}
	
	@Override
	@Transactional(readOnly = true)
	public CollectionInfoDto findBySlug(String slug) {
		Validate.isTrue(slug != null, "Collection slug must be non null");
		
		return collectionDao.findCollectionInfoBySlug(slug);
	}
	
	private static String formatSeriesInfo(AddToCollectionDbDto collectionDto) {
		StringBuilder sb = new StringBuilder();

		// FIXME: it would be good to include number of stamps in series vs in collection
		sb.append("stamps=")
			.append(collectionDto.getNumberOfStamps());

		if (collectionDto.getPrice() != null) {
			sb.append(", price=")
				.append(collectionDto.getPrice())
				.append(' ')
				.append(collectionDto.getCurrency());
		}

		return sb.toString();
	}
	
}
