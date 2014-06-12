/*
 * Copyright (C) 2009-2014 Slava Semushin <slava.semushin@gmail.com>
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
import java.util.Set;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import org.apache.commons.lang3.Validate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.dao.SeriesDao;
import ru.mystamps.web.entity.Category;
import ru.mystamps.web.entity.Country;
import ru.mystamps.web.entity.GibbonsCatalog;
import ru.mystamps.web.entity.MichelCatalog;
import ru.mystamps.web.entity.Price;
import ru.mystamps.web.entity.ScottCatalog;
import ru.mystamps.web.entity.Series;
import ru.mystamps.web.entity.User;
import ru.mystamps.web.entity.YvertCatalog;
import ru.mystamps.web.service.dto.AddSeriesDto;
import ru.mystamps.web.service.dto.SeriesInfoDto;
import ru.mystamps.web.util.CatalogUtils;

@RequiredArgsConstructor
public class SeriesServiceImpl implements SeriesService {
	private static final Logger LOG = LoggerFactory.getLogger(SeriesServiceImpl.class);
	
	private final SeriesDao seriesDao;
	private final ImageService imageService;
	
	@Override
	@Transactional
	@PreAuthorize("hasAuthority('CREATE_SERIES')")
	public Series add(AddSeriesDto dto, User user, boolean userCanAddComments) {
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
		
		if (dto.getYear() != null) {
			series.setReleaseYear(dto.getYear());

			if (dto.getMonth() != null) {
				series.setReleaseMonth(dto.getMonth());

				if (dto.getDay() != null) {
					series.setReleaseDay(dto.getDay());
				}
			}
		}
		
		series.setCategory(dto.getCategory());
		series.setQuantity(dto.getQuantity());
		series.setPerforated(dto.getPerforated());
		
		setMichelNumbersIfProvided(dto, series);
		setMichelPriceIfProvided(dto, series);
		
		setScottNumbersIfProvided(dto, series);
		setScottPriceIfProvided(dto, series);
		
		setYvertNumbersIfProvided(dto, series);
		setYvertPriceIfProvided(dto, series);
		
		setGibbonsNumbersIfProvided(dto, series);
		setGibbonsPriceIfProvided(dto, series);
		
		String imageUrl = imageService.save(dto.getImage());
		Validate.validState(imageUrl != null, "Image url must be non null");
		Validate.validState(imageUrl.length() <= Series.IMAGE_URL_LENGTH, "Too long image path");
		
		series.setImageUrl(imageUrl);
		
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
		
		return entity;
	}
	
	@Override
	@Transactional(readOnly = true)
	public long countAll() {
		return seriesDao.count();
	}
	
	@Override
	@Transactional(readOnly = true)
	public long countAllStamps() {
		return seriesDao.countAllStamps();
	}
	
	@Override
	@Transactional(readOnly = true)
	public int countByMichelNumber(String michelNumberCode) {
		Validate.isTrue(michelNumberCode != null, "Michel number code must be non null");
		Validate.isTrue(!michelNumberCode.trim().isEmpty(), "Michel number code must be non empty");
		
		return seriesDao.countByMichelNumberCode(michelNumberCode);
	}
	
	@Override
	@Transactional(readOnly = true)
	public int countByScottNumber(String scottNumberCode) {
		Validate.isTrue(scottNumberCode != null, "Scott number code must be non null");
		Validate.isTrue(!scottNumberCode.trim().isEmpty(), "Scott number code must be non empty");
		
		return seriesDao.countByScottNumberCode(scottNumberCode);
	}
	
	@Override
	@Transactional(readOnly = true)
	public int countByYvertNumber(String yvertNumberCode) {
		Validate.isTrue(yvertNumberCode != null, "Yvert number code must be non null");
		Validate.isTrue(!yvertNumberCode.trim().isEmpty(), "Yvert number code must be non empty");
		
		return seriesDao.countByYvertNumberCode(yvertNumberCode);
	}
	
	@Override
	@Transactional(readOnly = true)
	public int countByGibbonsNumber(String gibbonsNumberCode) {
		Validate.isTrue(gibbonsNumberCode != null, "Gibbons number code must be non null");
		Validate.isTrue(
			!gibbonsNumberCode.trim().isEmpty(),
			"Gibbons number code must be non empty"
		);
		
		return seriesDao.countByGibbonsNumberCode(gibbonsNumberCode);
	}
	
	@Override
	public Iterable<SeriesInfoDto> findBy(Category category, String lang) {
		Validate.isTrue(category != null, "Category must be non null");
		
		return seriesDao.findByAsSeriesInfo(category, lang);
	}
	
	@Override
	public Iterable<SeriesInfoDto> findBy(Country country, String lang) {
		Validate.isTrue(country != null, "Country must be non null");
		
		return seriesDao.findByAsSeriesInfo(country, lang);
	}
	
	private void setMichelNumbersIfProvided(AddSeriesDto dto, Series series) {
		Set<MichelCatalog> michelNumbers =
			CatalogUtils.fromString(dto.getMichelNumbers(), MichelCatalog.class);
		if (!michelNumbers.isEmpty()) {
			series.setMichel(michelNumbers);
		}
	}
	
	private static void setMichelPriceIfProvided(AddSeriesDto dto, Series series) {
		if (dto.getMichelPrice() == null) {
			return;
		}
		
		Validate.isTrue(
			dto.getMichelCurrency() != null,
			"Michel currency must be non null when price is specified"
		);
		series.setMichelPrice(new Price(dto.getMichelPrice(), dto.getMichelCurrency()));
	}
	
	private void setScottNumbersIfProvided(AddSeriesDto dto, Series series) {
		Set<ScottCatalog> scottNumbers =
			CatalogUtils.fromString(dto.getScottNumbers(), ScottCatalog.class);
		if (!scottNumbers.isEmpty()) {
			series.setScott(scottNumbers);
		}
	}
	
	private static void setScottPriceIfProvided(AddSeriesDto dto, Series series) {
		if (dto.getScottPrice() == null) {
			return;
		}
		
		Validate.isTrue(
			dto.getScottCurrency() != null,
			"Scott currency must be non null when price is specified"
		);
		series.setScottPrice(new Price(dto.getScottPrice(), dto.getScottCurrency()));
	}
	
	private void setYvertNumbersIfProvided(AddSeriesDto dto, Series series) {
		Set<YvertCatalog> yvertNumbers =
			CatalogUtils.fromString(dto.getYvertNumbers(), YvertCatalog.class);
		if (!yvertNumbers.isEmpty()) {
			series.setYvert(yvertNumbers);
		}
	}
	
	private static void setYvertPriceIfProvided(AddSeriesDto dto, Series series) {
		if (dto.getYvertPrice() == null) {
			return;
		}
		
		Validate.isTrue(
			dto.getYvertCurrency() != null,
			"Yvert currency must be non null when price is specified"
		);
		series.setYvertPrice(new Price(dto.getYvertPrice(), dto.getYvertCurrency()));
	}
	
	private void setGibbonsNumbersIfProvided(AddSeriesDto dto, Series series) {
		Set<GibbonsCatalog> gibbonsNumbers =
			CatalogUtils.fromString(dto.getGibbonsNumbers(), GibbonsCatalog.class);
		if (!gibbonsNumbers.isEmpty()) {
			series.setGibbons(gibbonsNumbers);
		}
	}
	
	private static void setGibbonsPriceIfProvided(AddSeriesDto dto, Series series) {
		if (dto.getGibbonsPrice() == null) {
			return;
		}
		
		Validate.isTrue(
			dto.getGibbonsCurrency() != null,
			"Gibbons currency must be non null when price is specified"
		);
		series.setGibbonsPrice(new Price(dto.getGibbonsPrice(), dto.getGibbonsCurrency()));
	}
	
}
