/**
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
package ru.mystamps.web.service

import spock.lang.Specification
import spock.lang.Unroll

import ru.mystamps.web.dao.CountryDao
import ru.mystamps.web.dao.JdbcCountryDao
import ru.mystamps.web.entity.Country
import ru.mystamps.web.entity.Collection
import ru.mystamps.web.entity.User
import ru.mystamps.web.model.AddCountryForm
import ru.mystamps.web.service.dto.SelectEntityDto
import ru.mystamps.web.tests.DateUtils

class CountryServiceImplTest extends Specification {
	
	private AddCountryForm form
	private User user
	
	private CountryDao countryDao = Mock()
	private JdbcCountryDao jdbcCountryDao = Mock()
	private CountryService service = new CountryServiceImpl(countryDao, jdbcCountryDao)
	
	def setup() {
		form = new AddCountryForm()
		form.setName('Any country name')
		form.setNameRu('Любое название страны')
		
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
	
	def "add() should throw exception when country name on English is null"() {
		given:
			form.setName(null)
		when:
			service.add(form, user)
		then:
			thrown IllegalArgumentException
	}
	
	def "add() should throw exception when country name on Russian is null"() {
		given:
			form.setNameRu(null)
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
	
	def "add() should pass country name on English to dao"() {
		given:
			String expectedCountryName = 'Italy'
			form.setName(expectedCountryName)
		when:
			service.add(form, user)
		then:
			1 * countryDao.save({ Country country ->
				assert country?.name == expectedCountryName
				return true
			}) >> TestObjects.createCountry()
	}
	
	def "add() should pass country name on Russian to dao"() {
		given:
			String expectedCountryName = 'Италия'
			form.setNameRu(expectedCountryName)
		when:
			service.add(form, user)
		then:
			1 * countryDao.save({ Country country ->
				assert country?.nameRu == expectedCountryName
				return true
			}) >> TestObjects.createCountry()
	}
	
	def "add() should assign created at to current date"() {
		when:
			service.add(form, user)
		then:
			1 * countryDao.save({ Country country ->
				assert DateUtils.roughlyEqual(country?.metaInfo?.createdAt, new Date())
				return true
			}) >> TestObjects.createCountry()
	}
	
	def "add() should assign updated at to current date"() {
		when:
			service.add(form, user)
		then:
			1 * countryDao.save({ Country country ->
				assert DateUtils.roughlyEqual(country?.metaInfo?.updatedAt, new Date())
				return true
			}) >> TestObjects.createCountry()
	}
	
	def "add() should assign created by to user"() {
		when:
			service.add(form, user)
		then:
			1 * countryDao.save({ Country country ->
				assert country?.metaInfo?.createdBy == user
				return true
			}) >> TestObjects.createCountry()
	}
	
	def "add() should assign updated by to user"() {
		when:
			service.add(form, user)
		then:
			1 * countryDao.save({ Country country ->
				assert country?.metaInfo?.updatedBy == user
				return true
			}) >> TestObjects.createCountry()
	}
	
	//
	// Tests for findAll(String)
	//
	
	def "findAll(String) should call dao"() {
		given:
			SelectEntityDto country1 = new SelectEntityDto(1, 'First Country')
		and:
			SelectEntityDto country2 = new SelectEntityDto(2, 'Second Country')
		and:
			List<SelectEntityDto> expectedCountries = [ country1, country2 ]
		and:
			countryDao.findAllAsSelectEntries(_ as String) >> expectedCountries
		when:
			Iterable<SelectEntityDto> resultCountries = service.findAll('de')
		then:
			resultCountries == expectedCountries
	}
	
	@Unroll
	def "findAll(String) should pass language '#expectedLanguage' to dao"(String expectedLanguage, Object _) {
		when:
			service.findAll(expectedLanguage)
		then:
			1 * countryDao.findAllAsSelectEntries({ String language ->
				assert language == expectedLanguage
				return true
			})
		where:
			expectedLanguage | _
			'ru'             | _
			null             | _
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
			int result = service.countByName('Any name here')
		then:
			result == 2
	}
	
	def "countByName() should pass country name to dao"() {
		when:
			service.countByName('Canada')
		then:
			1 * countryDao.countByName({ String name ->
				assert name == 'Canada'
				return true
			})
	}
	
	//
	// Tests for countByNameRu()
	//
	
	def "countByNameRu() should throw exception when name is null"() {
		when:
			service.countByNameRu(null)
		then:
			thrown IllegalArgumentException
	}
	
	def "countByNameRu() should call dao"() {
		given:
			countryDao.countByNameRu(_ as String) >> 2
		when:
			int result = service.countByNameRu('Any name here')
		then:
			result == 2
	}
	
	def "countByNameRu() should pass category name to dao"() {
		when:
			service.countByNameRu('Канада')
		then:
			1 * countryDao.countByNameRu({ String name ->
				assert name == 'Канада'
				return true
			})
	}
	
	//
	// Tests for getStatisticsOf()
	//
	
	def "getStatisticsOf() should throw exception when collection is null"() {
		when:
			service.getStatisticsOf(null, 'whatever')
		then:
			thrown IllegalArgumentException
	}
	
	def "getStatisticsOf() should throw exception when collection id is null"() {
		given:
			Collection collection = Mock()
			collection.getId() >> null
		when:
			service.getStatisticsOf(collection, 'whatever')
		then:
			thrown IllegalArgumentException
	}
	
	def "getStatisticsOf() should pass arguments to dao"() {
		given:
			Integer expectedCollectionId = 17
		and:
			String expectedLang = 'expected'
		and:
			Collection expectedCollection = Mock()
			expectedCollection.getId() >> expectedCollectionId
		when:
			service.getStatisticsOf(expectedCollection, expectedLang)
		then:
			1 * jdbcCountryDao.getStatisticsOf(
				{ Integer collectionId ->
					assert expectedCollectionId == collectionId
					return true
				},
				{ String lang ->
					assert expectedLang == lang
					return true
				}
			) >> null
	}
	
}
