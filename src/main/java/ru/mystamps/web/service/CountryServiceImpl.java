/*
 * Copyright (C) 2009-2017 Slava Semushin <slava.semushin@gmail.com>
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
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.access.prepost.PreAuthorize;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.dao.CountryDao;
import ru.mystamps.web.dao.dto.AddCountryDbDto;
import ru.mystamps.web.dao.dto.LinkEntityDto;
import ru.mystamps.web.service.dto.AddCountryDto;
import ru.mystamps.web.support.spring.security.HasAuthority;
import ru.mystamps.web.util.LocaleUtils;
import ru.mystamps.web.util.SlugUtils;

@RequiredArgsConstructor
@SuppressWarnings("PMD.TooManyMethods")
public class CountryServiceImpl implements CountryService {
	private static final Logger LOG = LoggerFactory.getLogger(CountryServiceImpl.class);
	
	private final CountryDao countryDao;
	
	@Override
	@Transactional
	@PreAuthorize(HasAuthority.CREATE_COUNTRY)
	public String add(AddCountryDto dto, Integer userId) {
		Validate.isTrue(dto != null, "DTO must be non null");
		Validate.isTrue(dto.getName() != null, "Country name in English must be non null");
		Validate.isTrue(userId != null, "User id must be non null");
		
		AddCountryDbDto country = new AddCountryDbDto();
		country.setName(dto.getName());
		country.setNameRu(dto.getNameRu());

		String slug = SlugUtils.slugify(dto.getName());
		Validate.isTrue(
			StringUtils.isNotEmpty(slug),
			"Slug for string '%s' must be non empty", dto.getName()
		);
		country.setSlug(slug);
		
		Date now = new Date();
		
		country.setCreatedAt(now);
		country.setCreatedBy(userId);
		country.setUpdatedAt(now);
		country.setUpdatedBy(userId);
		
		Integer id = countryDao.add(country);
		
		LOG.info("Country #{} has been created ({})", id, country);
		
		return slug;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<LinkEntityDto> findAllAsLinkEntities(String lang) {
		return countryDao.findAllAsLinkEntities(lang);
	}
	
	@Override
	@Transactional(readOnly = true)
	public LinkEntityDto findOneAsLinkEntity(String slug, String lang) {
		Validate.isTrue(slug != null, "Country slug must be non null");
		Validate.isTrue(!slug.trim().isEmpty(), "Country slug must be non empty");
		
		return countryDao.findOneAsLinkEntity(slug, lang);
	}
	
	@Override
	@Transactional(readOnly = true)
	public long countAll() {
		return countryDao.countAll();
	}
	
	@Override
	@Transactional(readOnly = true)
	public long countCountriesOf(Integer collectionId) {
		Validate.isTrue(collectionId != null, "Collection id must be non null");
		
		return countryDao.countCountriesOfCollection(collectionId);
	}
	
	@Override
	@Transactional(readOnly = true)
	public long countBySlug(String slug) {
		Validate.isTrue(slug != null, "Country slug must be non null");
		
		return countryDao.countBySlug(slug);
	}
	
	@Override
	@Transactional(readOnly = true)
	public long countByName(String name) {
		Validate.isTrue(name != null, "Name must be non null");
		
		// converting to lowercase to do a case-insensitive search
		String countryName = name.toLowerCase(Locale.ENGLISH);
		
		return countryDao.countByName(countryName);
	}
	
	@Override
	@Transactional(readOnly = true)
	public long countByNameRu(String name) {
		Validate.isTrue(name != null, "Name in Russian must be non null");
		
		// converting to lowercase to do a case-insensitive search
		String countryName = name.toLowerCase(LocaleUtils.RUSSIAN);
		
		return countryDao.countByNameRu(countryName);
	}
	
	@Override
	@Transactional(readOnly = true)
	public long countAddedSince(Date date) {
		Validate.isTrue(date != null, "Date must be non null");
		
		return countryDao.countAddedSince(date);
	}
	
	@Override
	@Transactional(readOnly = true)
	public long countUntranslatedNamesSince(Date date) {
		Validate.isTrue(date != null, "Date must be non null");
		
		return countryDao.countUntranslatedNamesSince(date);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Object[]> getStatisticsOf(Integer collectionId, String lang) {
		Validate.isTrue(collectionId != null, "Collection id must be non null");
		
		return countryDao.getStatisticsOf(collectionId, lang);
	}

	/**
	 * @author Shkarin John
	 */
	@Override
	@Transactional(readOnly = true)
	@PreAuthorize(HasAuthority.CREATE_SERIES)
	public String suggestCountryForUser(Integer userId) {
		Validate.isTrue(userId != null, "User id must be non null");

		return countryDao.suggestCountryForUser(userId);
	}
}
