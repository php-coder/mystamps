/*
 * Copyright (C) 2011-2012 Slava Semushin <slava.semushin@gmail.com>
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

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.fest.assertions.Assertions.assertThat;

import static com.google.common.collect.Iterables.elementsEqual;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ru.mystamps.web.dao.CountryDao;
import ru.mystamps.web.entity.Country;

public class CountryServiceTest {
	
	private static final Integer TEST_COUNTRY_ID = 1;
	private static final String TEST_COUNTRY_NAME = "Somewhere";
	
	@Mock
	private CountryDao countryDao;
	
	@Captor
	private ArgumentCaptor<Country> countryCaptor;
	
	@InjectMocks
	private CountryService service = new CountryService();
	
	@BeforeMethod
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}
	
	//
	// Tests for add()
	//
	
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void addShouldThrowExceptionWhenCountryNameIsNull() {
		service.add(null);
	}
	
	@Test
	public void addShouldCallDao() {
		final Country expected = getCountry();
		when(countryDao.save(any(Country.class))).thenReturn(expected);
		
		final Country actual = service.add(TEST_COUNTRY_NAME);
		
		assertThat(actual).isEqualTo(expected);
	}
	
	@Test
	public void addShouldPassCountryNameToDao() {
		service.add(TEST_COUNTRY_NAME);
		
		verify(countryDao).save(countryCaptor.capture());
		
		assertThat(countryCaptor.getValue().getName()).isEqualTo(TEST_COUNTRY_NAME);
	}
	
	@Test
	public void addShouldAssignCurrentDate() {
		service.add(TEST_COUNTRY_NAME);
		
		verify(countryDao).save(countryCaptor.capture());
		
		assertThat(countryCaptor.getValue().getCreatedAt()).isNotNull();
	}
	
	//
	// Tests for findAll()
	//
	
	@Test
	public void findAllShouldCallDao() {
		final List<Country> expectedCountries = new ArrayList<Country>();
		
		final Country country1 = getCountry();
		country1.setName("First Country");
		expectedCountries.add(country1);
		
		final Country country2 = getCountry();
		country1.setName("Second Country");
		expectedCountries.add(country2);
		
		when(countryDao.findAll()).thenReturn(expectedCountries);
		
		final Iterable<Country> resultCountries = service.findAll();
		
		assertThat(elementsEqual(resultCountries, expectedCountries)).isTrue();
	}
	
	//
	// Tests for findByName()
	//
	
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void findByNameShouldThrowExceptionWhenNameIsNull() {
		service.findByName(null);
	}
	
	@Test
	public void findByNameShouldCallDao() {
		final Country expectedCountry = getCountry();
		when(countryDao.findByName(anyString())).thenReturn(expectedCountry);
		
		final Country country = service.findByName(TEST_COUNTRY_NAME);
		
		assertThat(country).isEqualTo(expectedCountry);
	}
	
	@Test
	public void findByNameShouldPassCountryNameToDao() {
		service.findByName(TEST_COUNTRY_NAME);
		
		verify(countryDao).findByName(eq(TEST_COUNTRY_NAME));
	}
	
	//
	// Tests for findById()
	//
	
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void findByIdShouldThrowExceptionWhenIdIsNull() {
		service.findById(null);
	}
	
	@Test
	public void findByIdShouldCallDao() {
		final Country expectedCountry = getCountry();
		when(countryDao.findOne(anyInt())).thenReturn(expectedCountry);
		
		final Country country = service.findById(TEST_COUNTRY_ID);
		
		assertThat(country).isEqualTo(expectedCountry);
	}
	
	@Test
	public void findByIdShouldPassIdToDao() {
		service.findById(TEST_COUNTRY_ID);
		
		verify(countryDao).findOne(eq(TEST_COUNTRY_ID));
	}
	
	private Country getCountry() {
		final Country country = new Country();
		country.setId(TEST_COUNTRY_ID);
		country.setName(TEST_COUNTRY_NAME);
		country.setCreatedAt(new Date());
		return country;
	}
	
}
