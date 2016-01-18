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

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.Validate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.access.prepost.PreAuthorize;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.dao.JdbcSeriesDao;
import ru.mystamps.web.dao.dto.AddSeriesDbDto;
import ru.mystamps.web.entity.Image;
import ru.mystamps.web.service.dto.AddImageDto;
import ru.mystamps.web.service.dto.AddSeriesDto;
import ru.mystamps.web.service.dto.Currency;
import ru.mystamps.web.service.dto.SeriesInfoDto;
import ru.mystamps.web.service.dto.SitemapInfoDto;
import ru.mystamps.web.util.CatalogUtils;

// TODO: move stamps related methods to separate interface (#88)
@SuppressWarnings("PMD.TooManyMethods")
@RequiredArgsConstructor
public class SeriesServiceImpl implements SeriesService {
	private static final Logger LOG = LoggerFactory.getLogger(SeriesServiceImpl.class);
	
	private final JdbcSeriesDao seriesDao;
	private final ImageService imageService;
	private final MichelCatalogService michelCatalogService;
	private final ScottCatalogService scottCatalogService;
	private final YvertCatalogService yvertCatalogService;
	private final GibbonsCatalogService gibbonsCatalogService;
	
	@Override
	@Transactional
	@PreAuthorize("hasAuthority('CREATE_SERIES')")
	@SuppressWarnings({ "PMD.NPathComplexity", "PMD.ModifiedCyclomaticComplexity" })
	public Integer add(AddSeriesDto dto, Integer userId, boolean userCanAddComments) {
		Validate.isTrue(dto != null, "DTO must be non null");
		Validate.isTrue(dto.getQuantity() != null, "Stamps quantity must be non null");
		Validate.isTrue(
			dto.getPerforated() != null,
			"Stamps perforated property must be non null"
		);
		Validate.isTrue(dto.getCategory() != null, "Category must be non null");
		Validate.isTrue(dto.getCategory().getId() != null, "Category id must be non null");
		Validate.isTrue(userId != null, "Current user id must be non null");
		
		AddSeriesDbDto series = new AddSeriesDbDto();
		
		if (dto.getCountry() != null) {
			series.setCountryId(dto.getCountry().getId());
		}
		
		setDateOfReleaseIfProvided(dto, series);
		
		series.setCategoryId(dto.getCategory().getId());
		series.setQuantity(dto.getQuantity());
		series.setPerforated(dto.getPerforated());
		
		if (dto.getMichelPrice() != null) {
			series.setMichelPrice(dto.getMichelPrice());
			series.setMichelCurrency(Currency.EUR.toString());
		}

		if (dto.getScottPrice() != null) {
			series.setScottPrice(dto.getScottPrice());
			series.setScottCurrency(Currency.USD.toString());
		}

		if (dto.getYvertPrice() != null) {
			series.setYvertPrice(dto.getYvertPrice());
			series.setYvertCurrency(Currency.EUR.toString());
		}

		if (dto.getGibbonsPrice() != null) {
			series.setGibbonsPrice(dto.getGibbonsPrice());
			series.setGibbonsCurrency(Currency.GBP.toString());
		}

		if (userCanAddComments && dto.getComment() != null) {
			Validate.isTrue(
				!dto.getComment().trim().isEmpty(),
				"Comment must be non empty"
			);
			
			series.setComment(dto.getComment());
		}
		
		Date now = new Date();
		series.setCreatedAt(now);
		series.setUpdatedAt(now);
		
		series.setCreatedBy(userId);
		series.setUpdatedBy(userId);
		
		Integer id = seriesDao.add(series);
		LOG.info("Series #{} has been created ({})", id, series);
		
		Image image = imageService.save(dto.getImage());
		imageService.addToSeries(id, image.getId());
		
		Set<String> michelNumbers = CatalogUtils.parseCatalogNumbers(dto.getMichelNumbers());
		if (!michelNumbers.isEmpty()) {
			michelCatalogService.add(michelNumbers);
			michelCatalogService.addToSeries(id, michelNumbers);
		}
		
		Set<String> scottNumbers = CatalogUtils.parseCatalogNumbers(dto.getScottNumbers());
		if (!scottNumbers.isEmpty()) {
			scottCatalogService.add(scottNumbers);
			scottCatalogService.addToSeries(id, scottNumbers);
		}
		
		Set<String> yvertNumbers = CatalogUtils.parseCatalogNumbers(dto.getYvertNumbers());
		if (!yvertNumbers.isEmpty()) {
			yvertCatalogService.add(yvertNumbers);
			yvertCatalogService.addToSeries(id, yvertNumbers);
		}
		
		Set<String> gibbonsNumbers = CatalogUtils.parseCatalogNumbers(dto.getGibbonsNumbers());
		if (!gibbonsNumbers.isEmpty()) {
			gibbonsCatalogService.add(gibbonsNumbers);
			gibbonsCatalogService.addToSeries(id, gibbonsNumbers);
		}
		
		return id;
	}

	@Override
	@Transactional
	@PreAuthorize("hasAuthority('ADD_IMAGES_TO_SERIES')")
	public void addImageToSeries(AddImageDto dto, Integer seriesId, Integer userId) {
		Validate.isTrue(dto != null, "DTO must be non null");
		Validate.isTrue(seriesId != null, "Series id must be non null");
		Validate.isTrue(userId != null, "User id must be non null");
		
		Image image = imageService.save(dto.getImage());
		
		imageService.addToSeries(seriesId, image.getId());
		
		LOG.info(
			"Image #{} was added to series #{} by user #{}",
			image.getId(),
			seriesId,
			userId
		);
	}
	
	@Override
	@Transactional(readOnly = true)
	public long countAll() {
		return seriesDao.countAll();
	}
	
	@Override
	@Transactional(readOnly = true)
	public long countAllStamps() {
		return seriesDao.countAllStamps();
	}
	
	@Override
	@Transactional(readOnly = true)
	public long countSeriesOf(Integer collectionId) {
		Validate.isTrue(collectionId != null, "Collection id must be non null");
		
		return seriesDao.countSeriesOfCollection(collectionId);
	}
	
	@Override
	@Transactional(readOnly = true)
	public long countStampsOf(Integer collectionId) {
		Validate.isTrue(collectionId != null, "Collection id must be non null");
		
		return seriesDao.countStampsOfCollection(collectionId);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<SeriesInfoDto> findByMichelNumber(String michelNumberCode, String lang) {
		Validate.isTrue(michelNumberCode != null, "Michel number code must be non null");
		Validate.isTrue(!michelNumberCode.trim().isEmpty(), "Michel number code must be non empty");
		
		List<Integer> seriesIds = seriesDao.findSeriesIdsByMichelNumberCode(michelNumberCode);
		if (seriesIds.isEmpty()) {
			return Collections.emptyList();
		}
		
		return seriesDao.findByIdsAsSeriesInfo(seriesIds, lang);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<SeriesInfoDto> findByScottNumber(String scottNumberCode, String lang) {
		Validate.isTrue(scottNumberCode != null, "Scott number code must be non null");
		Validate.isTrue(!scottNumberCode.trim().isEmpty(), "Scott number code must be non empty");
		
		List<Integer> seriesIds = seriesDao.findSeriesIdsByScottNumberCode(scottNumberCode);
		if (seriesIds.isEmpty()) {
			return Collections.emptyList();
		}
		
		return seriesDao.findByIdsAsSeriesInfo(seriesIds, lang);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<SeriesInfoDto> findByYvertNumber(String yvertNumberCode, String lang) {
		Validate.isTrue(yvertNumberCode != null, "Yvert number code must be non null");
		Validate.isTrue(!yvertNumberCode.trim().isEmpty(), "Yvert number code must be non empty");
		
		List<Integer> seriesIds = seriesDao.findSeriesIdsByYvertNumberCode(yvertNumberCode);
		if (seriesIds.isEmpty()) {
			return Collections.emptyList();
		}
		
		return seriesDao.findByIdsAsSeriesInfo(seriesIds, lang);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<SeriesInfoDto> findByGibbonsNumber(String gibbonsNumberCode, String lang) {
		Validate.isTrue(gibbonsNumberCode != null, "Gibbons number code must be non null");
		Validate.isTrue(
			!gibbonsNumberCode.trim().isEmpty(),
			"Gibbons number code must be non empty"
		);
		
		List<Integer> seriesIds = seriesDao.findSeriesIdsByGibbonsNumberCode(gibbonsNumberCode);
		if (seriesIds.isEmpty()) {
			return Collections.emptyList();
		}
		
		return seriesDao.findByIdsAsSeriesInfo(seriesIds, lang);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Iterable<SeriesInfoDto> findByCategoryId(Integer categoryId, String lang) {
		Validate.isTrue(categoryId != null, "Category id must be non null");
		
		return seriesDao.findByCategoryIdAsSeriesInfo(categoryId, lang);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Iterable<SeriesInfoDto> findByCountryId(Integer countryId, String lang) {
		Validate.isTrue(countryId != null, "Country id must be non null");
		
		return seriesDao.findByCountryIdAsSeriesInfo(countryId, lang);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Iterable<SeriesInfoDto> findByCollectionId(Integer collectionId, String lang) {
		Validate.isTrue(collectionId != null, "Collection id must be non null");
		
		return seriesDao.findByCollectionIdAsSeriesInfo(collectionId, lang);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Iterable<SeriesInfoDto> findRecentlyAdded(int quantity, String lang) {
		Validate.isTrue(quantity > 0, "Quantity of recently added series must be greater than 0");
		
		return seriesDao.findLastAdded(quantity, lang);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Iterable<SitemapInfoDto> findAllForSitemap() {
		return seriesDao.findAllForSitemap();
	}

	private static void setDateOfReleaseIfProvided(AddSeriesDto dto, AddSeriesDbDto series) {
		if (dto.getYear() == null) {
			return;
		}
		
		series.setReleaseYear(dto.getYear());
		
		if (dto.getMonth() == null) {
			return;
		}
		
		series.setReleaseMonth(dto.getMonth());
		
		// even if day is null it won't change anything
		series.setReleaseDay(dto.getDay());
	}
	
}
