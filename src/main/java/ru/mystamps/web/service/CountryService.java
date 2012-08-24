/*
 * Copyright (C) 2009-2012 Slava Semushin <slava.semushin@gmail.com>
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.apache.commons.lang3.Validate;

import ru.mystamps.web.entity.Country;
import ru.mystamps.web.entity.User;
import ru.mystamps.web.dao.CountryDao;

@Service
public class CountryService {
	
	@Inject
	private CountryDao countryDao;
	
	@Inject
	private UserService userService;
	
	@Transactional
	@PreAuthorize("hasAuthority('ROLE_USER')")
	public Country add(final String countryName) {
		Validate.isTrue(countryName != null, "Country name should be non null");
		
		final Country country = new Country();
		country.setName(countryName);
		
		final Date now = new Date();
		country.setCreatedAt(now);
		country.setUpdatedAt(now);
		
		final User currentUser = userService.getCurrentUser();
		Validate.validState(currentUser != null, "Current user must be non null");
		country.setCreatedBy(currentUser);
		country.setUpdatedBy(currentUser);
		
		return countryDao.save(country);
	}
	
	@Transactional(readOnly = true)
	public Iterable<Country> findAll() {
		return countryDao.findAll();
	}
	
	@Transactional(readOnly = true)
	public Country findByName(final String name) {
		Validate.isTrue(name != null, "Name should be non null");
		return countryDao.findByName(name);
	}
	
	@Transactional(readOnly = true)
	public Country findById(final Integer id) {
		Validate.isTrue(id != null, "Id should be non null");
		return countryDao.findOne(id);
	}
	
}
