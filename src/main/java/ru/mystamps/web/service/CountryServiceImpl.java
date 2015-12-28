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

import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.access.prepost.PreAuthorize;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.dao.JdbcCountryDao;
import ru.mystamps.web.dao.dto.AddCountryDbDto;
import ru.mystamps.web.service.dto.AddCountryDto;
import ru.mystamps.web.service.dto.LinkEntityDto;
import ru.mystamps.web.service.dto.SelectEntityDto;
import ru.mystamps.web.service.dto.UrlEntityDto;
import ru.mystamps.web.util.SlugUtils;

@RequiredArgsConstructor
public class CountryServiceImpl implements CountryService {
	private static final Logger LOG = LoggerFactory.getLogger(CountryServiceImpl.class);
	
	private final JdbcCountryDao countryDao;
	
	@Override
	@Transactional
	@PreAuthorize("hasAuthority('CREATE_COUNTRY')")
	public UrlEntityDto add(AddCountryDto dto, Integer userId) {
		Validate.isTrue(dto != null, "DTO should be non null");
		Validate.isTrue(dto.getName() != null, "Country name on English should be non null");
		Validate.isTrue(dto.getNameRu() != null, "Country name on Russian should be non null");
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
		
		return new UrlEntityDto(id, slug);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Iterable<SelectEntityDto> findAllAsSelectEntities(String lang) {
		return countryDao.findAllAsSelectEntities(lang);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Iterable<LinkEntityDto> findAllAsLinkEntities(String lang) {
		return countryDao.findAllAsLinkEntities(lang);
	}
	
	@Override
	@Transactional(readOnly = true)
	public LinkEntityDto findOneAsLinkEntity(Integer countryId, String lang) {
		Validate.isTrue(countryId != null, "Country id must be non null");
		
		return countryDao.findOneAsLinkEntity(countryId, lang);
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
	public long countByName(String name) {
		Validate.isTrue(name != null, "Name should be non null");
		return countryDao.countByName(name);
	}
	
	@Override
	@Transactional(readOnly = true)
	public long countByNameRu(String name) {
		Validate.isTrue(name != null, "Name on Russian should be non null");
		return countryDao.countByNameRu(name);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Map<String, Integer> getStatisticsOf(Integer collectionId, String lang) {
		Validate.isTrue(collectionId != null, "Collection id must be non null");
		
		return countryDao.getStatisticsOf(collectionId, lang);
	}
	
}
