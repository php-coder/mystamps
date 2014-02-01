/**
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
package ru.mystamps.web.service

import spock.lang.Specification

import ru.mystamps.web.dao.CountryDao
import ru.mystamps.web.entity.Country
import ru.mystamps.web.entity.User
import ru.mystamps.web.model.AddCountryForm
import ru.mystamps.web.tests.DateUtils

class CountryServiceTest extends Specification {
	
	private AddCountryForm form
	private User user
	
	private CountryDao countryDao = Mock()
	private CountryService service = new CountryServiceImpl(countryDao)
	
	def setup() {
		form = new AddCountryForm()
		form.setName("Any country name")
		
		user = TestObjects.createUser()
	}
	
	//
	// Tests for add()
	//
	
	def "add() should throw exception when dto is null"() {
		when:
			service.add(null, user)
		then:
			thrown IllegalArgumentException
	}
	
	def "add() should throw exception when country name is null"() {
		given:
			form.setName(null)
		when:
			service.add(form, user)
		then:
			thrown IllegalArgumentException
	}
	
	def "add() should throw exception when user is null"() {
		when:
			service.add(form, null)
		then:
			thrown IllegalArgumentException
	}
	
	def "add() should call dao"() {
		given:
			Country expected = TestObjects.createCountry()
			countryDao.save(_ as Country) >> expected
		when:
			Country actual = service.add(form, user)
		then:
			actual == expected
	}
	
	def "add() should pass country name to dao"() {
		given:
			String expectedCountryName = "Italy"
			form.setName(expectedCountryName)
		when:
			service.add(form, user)
		then:
			1 * countryDao.save({ Country country ->
				assert country?.name == expectedCountryName
				return true
			})
	}
	
	def "add() should assign created at to current date"() {
		when:
			service.add(form, user)
		then:
			1 * countryDao.save({ Country country ->
				assert DateUtils.roughlyEqual(country?.metaInfo?.createdAt, new Date())
				return true
			})
	}
	
	def "add() should assign updated at to current date"() {
		when:
			service.add(form, user)
		then:
			1 * countryDao.save({ Country country ->
				assert DateUtils.roughlyEqual(country?.metaInfo?.updatedAt, new Date())
				return true
			})
	}
	
	def "add() should assign created by to user"() {
		when:
			service.add(form, user)
		then:
			1 * countryDao.save({ Country country ->
				assert country?.metaInfo?.createdBy == user
				return true
			})
	}
	
	def "add() should assign updated by to user"() {
		when:
			service.add(form, user)
		then:
			1 * countryDao.save({ Country country ->
				assert country?.metaInfo?.updatedBy == user
				return true
			})
	}
	
	//
	// Tests for findAll()
	//
	
	def "findAll() should call dao"() {
		given:
			Country country1 = TestObjects.createCountry()
			country1.setName("First Country")
		and:
			Country country2 = TestObjects.createCountry()
			country2.setName("Second Country")
		and:
			List<Country> expectedCountries = [ country1, country2 ]
		and:
			countryDao.findAll() >> expectedCountries
		when:
			Iterable<Country> resultCountries = service.findAll()
		then:
			resultCountries == expectedCountries
	}
	
	//
	// Tests for countAll()
	//
	
	def "countAll() should call dao and returns result"() {
		given:
			long expectedResult = 20
		when:
			long result = service.countAll()
		then:
			1 * countryDao.count() >> expectedResult
		and:
			result == expectedResult
	}
	
	//
	// Tests for countByName()
	//
	
	def "countByName() should throw exception when name is null"() {
		when:
			service.countByName(null)
		then:
			thrown IllegalArgumentException
	}
	
	def "countByName() should call dao"() {
		given:
			countryDao.countByName(_ as String) >> 2
		when:
			int result = service.countByName("Any name here")
		then:
			result == 2
	}
	
	def "countByName() should pass country name to dao"() {
		when:
			service.countByName("Canada")
		then:
			1 * countryDao.countByName({ String name ->
				assert name == "Canada"
				return true
			})
	}
	
}
