/*
 * Copyright (C) 2009-2015 Slava Semushin <slava.semushin@gmail.com>
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
import java.util.Optional;
import java.util.Set;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import org.apache.commons.lang3.Validate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.dao.JdbcSeriesDao;
import ru.mystamps.web.dao.SeriesDao;
import ru.mystamps.web.entity.Category;
import ru.mystamps.web.entity.Country;
import ru.mystamps.web.entity.Collection;
import ru.mystamps.web.entity.Currency;
import ru.mystamps.web.entity.GibbonsCatalog;
import ru.mystamps.web.entity.Image;
import ru.mystamps.web.entity.MichelCatalog;
import ru.mystamps.web.entity.Price;
import ru.mystamps.web.entity.ScottCatalog;
import ru.mystamps.web.entity.Series;
import ru.mystamps.web.entity.StampsCatalog;
import ru.mystamps.web.entity.User;
import ru.mystamps.web.entity.YvertCatalog;
import ru.mystamps.web.service.dto.AddImageDto;
import ru.mystamps.web.service.dto.AddSeriesDto;
import ru.mystamps.web.service.dto.SeriesInfoDto;
import ru.mystamps.web.service.dto.SitemapInfoDto;
import ru.mystamps.web.util.CatalogUtils;

// TODO: move stamps related methods to separate interface (#88)
@SuppressWarnings("PMD.TooManyMethods")
@RequiredArgsConstructor
public class SeriesServiceImpl implements SeriesService {
	private static final Logger LOG = LoggerFactory.getLogger(SeriesServiceImpl.class);
	
	private final SeriesDao seriesDao;
	private final JdbcSeriesDao jdbcSeriesDao;
	private final ImageService imageService;
	
	@Override
	@Transactional
	@PreAuthorize("hasAuthority('CREATE_SERIES')")
	public Integer add(AddSeriesDto dto, User user, boolean userCanAddComments) {
		Validate.isTrue(dto != null, "DTO must be non null");
		Validate.isTrue(dto.getQuantity() != null, "Stamps quantity must be non null");
		Validate.isTrue(
			dto.getPerforated() != null,
			"Stamps perforated property must be non null"
		);
		Validate.isTrue(dto.getCategory() != null, "Category must be non null");
		Validate.isTrue(user != null, "Current user must be non null");
		
		Series series = new Series();
		
		if (dto.getCountry() != null) {
			series.setCountry(dto.getCountry());
		}
		
		setDateOfReleaseIfProvided(dto, series);
		
		series.setCategory(dto.getCategory());
		series.setQuantity(dto.getQuantity());
		series.setPerforated(dto.getPerforated());
		
		series.setMichel(getCatalogNumbersOrNull(dto.getMichelNumbers(), MichelCatalog.class));
		series.setMichelPrice(Price.valueOf(dto.getMichelPrice(), Currency.EUR));
		
		series.setScott(getCatalogNumbersOrNull(dto.getScottNumbers(), ScottCatalog.class));
		series.setScottPrice(Price.valueOf(dto.getScottPrice(), Currency.USD));
		
		series.setYvert(getCatalogNumbersOrNull(dto.getYvertNumbers(), YvertCatalog.class));
		series.setYvertPrice(Price.valueOf(dto.getYvertPrice(), Currency.EUR));
		
		series.setGibbons(getCatalogNumbersOrNull(dto.getGibbonsNumbers(), GibbonsCatalog.class));
		series.setGibbonsPrice(Price.valueOf(dto.getGibbonsPrice(), Currency.GBP));
		
		Image image = imageService.save(dto.getImage());
		series.setImages(Collections.singleton(image));
		
		if (userCanAddComments && dto.getComment() != null) {
			Validate.isTrue(
				!dto.getComment().trim().isEmpty(),
				"Comment must be non empty"
			);
			
			series.setComment(dto.getComment());
		}
		
		Date now = new Date();
		series.getMetaInfo().setCreatedAt(now);
		series.getMetaInfo().setUpdatedAt(now);
		
		series.getMetaInfo().setCreatedBy(user);
		series.getMetaInfo().setUpdatedBy(user);

		Series entity = seriesDao.save(series);
		LOG.info("Series has been created ({})", entity);
		
		return entity.getId();
	}

	@Override
	@Transactional
	@PreAuthorize("hasAuthority('ADD_IMAGES_TO_SERIES')")
	public void addImageToSeries(AddImageDto dto, Series series, User user) {
		Validate.isTrue(dto != null, "DTO must be non null");
		Validate.isTrue(series != null, "Series must be non null");
		Validate.isTrue(user != null, "User must be non null");
		
		Image image = imageService.save(dto.getImage());
		
		series.addImage(image);
		
		seriesDao.save(series);
		
		LOG.info(
			"Image #{} was added to series #{} by user #{}",
			image.getId(),
			series.getId(),
			user.getId()
		);
	}
	
	@Override
	@Transactional(readOnly = true)
	public long countAll() {
		return jdbcSeriesDao.countAll();
	}
	
	@Override
	@Transactional(readOnly = true)
	public long countAllStamps() {
		return jdbcSeriesDao.countAllStamps();
	}
	
	@Override
	@Transactional(readOnly = true)
	public long countSeriesOf(Collection collection) {
		Validate.isTrue(collection != null, "Collection must be non null");
		Validate.isTrue(collection.getId() != null, "Collection id must be non null");
		
		return jdbcSeriesDao.countSeriesOfCollection(collection.getId());
	}
	
	@Override
	@Transactional(readOnly = true)
	public long countStampsOf(Collection collection) {
		Validate.isTrue(collection != null, "Collection must be non null");
		Validate.isTrue(collection.getId() != null, "Collection id must be non null");
		
		return jdbcSeriesDao.countStampsOfCollection(collection.getId());
	}
	
	@Override
	@Transactional(readOnly = true)
	public long countByMichelNumber(String michelNumberCode) {
		Validate.isTrue(michelNumberCode != null, "Michel number code must be non null");
		Validate.isTrue(!michelNumberCode.trim().isEmpty(), "Michel number code must be non empty");
		
		return jdbcSeriesDao.countByMichelNumberCode(michelNumberCode);
	}
	
	@Override
	@Transactional(readOnly = true)
	public long countByScottNumber(String scottNumberCode) {
		Validate.isTrue(scottNumberCode != null, "Scott number code must be non null");
		Validate.isTrue(!scottNumberCode.trim().isEmpty(), "Scott number code must be non empty");
		
		return jdbcSeriesDao.countByScottNumberCode(scottNumberCode);
	}
	
	@Override
	@Transactional(readOnly = true)
	public long countByYvertNumber(String yvertNumberCode) {
		Validate.isTrue(yvertNumberCode != null, "Yvert number code must be non null");
		Validate.isTrue(!yvertNumberCode.trim().isEmpty(), "Yvert number code must be non empty");
		
		return jdbcSeriesDao.countByYvertNumberCode(yvertNumberCode);
	}
	
	@Override
	@Transactional(readOnly = true)
	public long countByGibbonsNumber(String gibbonsNumberCode) {
		Validate.isTrue(gibbonsNumberCode != null, "Gibbons number code must be non null");
		Validate.isTrue(
			!gibbonsNumberCode.trim().isEmpty(),
			"Gibbons number code must be non empty"
		);
		
		return jdbcSeriesDao.countByGibbonsNumberCode(gibbonsNumberCode);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Optional<Integer> findSeriesIdByMichelNumber(String michelNumberCode) {
		Validate.isTrue(michelNumberCode != null, "Michel number code must be non null");
		Validate.isTrue(!michelNumberCode.trim().isEmpty(), "Michel number code must be non empty");
		
		return jdbcSeriesDao.findSeriesIdByMichelNumberCode(michelNumberCode);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Optional<Integer> findSeriesIdByScottNumber(String scottNumberCode) {
		Validate.isTrue(scottNumberCode != null, "Scott number code must be non null");
		Validate.isTrue(!scottNumberCode.trim().isEmpty(), "Scott number code must be non empty");
		
		return jdbcSeriesDao.findSeriesIdByScottNumberCode(scottNumberCode);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Optional<Integer> findSeriesIdByYvertNumber(String yvertNumberCode) {
		Validate.isTrue(yvertNumberCode != null, "Yvert number code must be non null");
		Validate.isTrue(!yvertNumberCode.trim().isEmpty(), "Yvert number code must be non empty");
		
		return jdbcSeriesDao.findSeriesIdByYvertNumberCode(yvertNumberCode);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Optional<Integer> findSeriesIdByGibbonsNumber(String gibbonsNumberCode) {
		Validate.isTrue(gibbonsNumberCode != null, "Gibbons number code must be non null");
		Validate.isTrue(
			!gibbonsNumberCode.trim().isEmpty(),
			"Gibbons number code must be non empty"
		);
		
		return jdbcSeriesDao.findSeriesIdByGibbonsNumberCode(gibbonsNumberCode);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Iterable<SeriesInfoDto> findBy(Category category, String lang) {
		Validate.isTrue(category != null, "Category must be non null");
		
		return jdbcSeriesDao.findByCategoryIdAsSeriesInfo(category.getId(), lang);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Iterable<SeriesInfoDto> findBy(Country country, String lang) {
		Validate.isTrue(country != null, "Country must be non null");
		
		return jdbcSeriesDao.findByCountryIdAsSeriesInfo(country.getId(), lang);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Iterable<SeriesInfoDto> findBy(Collection collection, String lang) {
		Validate.isTrue(collection != null, "Collection must be non null");
		Validate.isTrue(collection.getId() != null, "Collection id must be non null");
		
		return jdbcSeriesDao.findByCollectionIdAsSeriesInfo(collection.getId(), lang);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Iterable<SeriesInfoDto> findRecentlyAdded(int quantity, String lang) {
		Validate.isTrue(quantity > 0, "Quantity of recently added series must be greater than 0");
		
		return jdbcSeriesDao.findLastAdded(quantity, lang);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Iterable<SitemapInfoDto> findAllForSitemap() {
		return jdbcSeriesDao.findAllForSitemap();
	}

	private static void setDateOfReleaseIfProvided(AddSeriesDto dto, Series series) {
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
	
	private static <T extends StampsCatalog> Set<T> getCatalogNumbersOrNull(
		String catalogNumbers,
		Class<T> clazz) {
		
		Set<T> result = CatalogUtils.fromString(catalogNumbers, clazz);
		if (result.isEmpty()) {
			return null;
		}
		
		return result;
	}
	
}
