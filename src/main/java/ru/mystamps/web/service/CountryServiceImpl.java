/*
 * Copyright (C) 2009-2013 Slava Semushin <slava.semushin@gmail.com>
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

import javax.inject.Inject;

import java.util.Date;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import org.apache.commons.lang3.Validate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.mystamps.web.entity.Country;
import ru.mystamps.web.entity.User;
import ru.mystamps.web.dao.CountryDao;
import ru.mystamps.web.service.dto.AddCountryDto;

public class CountryServiceImpl implements CountryService {
	private static final Logger LOG = LoggerFactory.getLogger(CountryServiceImpl.class);
	
	private final CountryDao countryDao;
	
	@Inject
	public CountryServiceImpl(CountryDao countryDao) {
		this.countryDao = countryDao;
	}
	
	@Override
	@Transactional
	@PreAuthorize("hasAuthority('ROLE_USER')")
	public Country add(AddCountryDto dto, User user) {
		Validate.isTrue(dto != null, "DTO should be non null");
		Validate.isTrue(dto.getName() != null, "Country name should be non null");
		Validate.isTrue(user != null, "Current user must be non null");
		
		Country country = new Country();
		country.setName(dto.getName());
		
		Date now = new Date();
		country.getMetaInfo().setCreatedAt(now);
		country.getMetaInfo().setUpdatedAt(now);
		
		country.getMetaInfo().setCreatedBy(user);
		country.getMetaInfo().setUpdatedBy(user);

		Country entity = countryDao.save(country);
		LOG.debug("Created country ({})", entity);
		
		return entity;
	}
	
	@Override
	@Transactional(readOnly = true)
	public Iterable<Country> findAll() {
		return countryDao.findAll();
	}
	
	@Override
	@Transactional(readOnly = true)
	public Country findByName(String name) {
		Validate.isTrue(name != null, "Name should be non null");
		return countryDao.findByName(name);
	}
	
}
