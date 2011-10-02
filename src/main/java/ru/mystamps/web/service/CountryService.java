/*
 * Copyright (C) 2011 Slava Semushin <slava.semushin@gmail.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

package ru.mystamps.web.service;

import javax.inject.Inject;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.google.common.base.Preconditions.checkArgument;

import ru.mystamps.web.entity.Country;
import ru.mystamps.web.dao.CountryDao;

@Service
public class CountryService {
	
	@Inject
	private CountryDao countryDao;
	
	@Transactional
	public Integer add(final String countryName) {
		checkArgument(countryName != null, "Country name should be non null");
		
		final Country country = new Country();
		country.setName(countryName);
		country.setCreatedAt(new Date());
		
		return countryDao.add(country);
	}
	
	@Transactional(readOnly = true)
	public List<Country> findAll() {
		return countryDao.findAll();
	}
	
	@Transactional(readOnly = true)
	public Country findByName(final String name) {
		checkArgument(name != null, "Name should be non null");
		return countryDao.findByName(name);
	}
	
	@Transactional(readOnly = true)
	public Country findById(final Integer id) {
		checkArgument(id != null, "Id should be non null");
		return countryDao.findById(id);
	}
	
}
