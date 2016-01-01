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

import ru.mystamps.web.dao.JdbcCountryDao
import ru.mystamps.web.dao.dto.AddCountryDbDto
import ru.mystamps.web.model.AddCountryForm
import ru.mystamps.web.service.dto.LinkEntityDto
import ru.mystamps.web.service.dto.SelectEntityDto
import ru.mystamps.web.service.dto.UrlEntityDto
import ru.mystamps.web.tests.DateUtils
import ru.mystamps.web.util.SlugUtils

class CountryServiceImplTest extends Specification {
	
	private AddCountryForm form
	private Integer userId = 321
	
	private JdbcCountryDao countryDao = Mock()
	private CountryService service = new CountryServiceImpl(countryDao)
	
	def setup() {
		form = new AddCountryForm()
		form.setName('Any country name')
		form.setNameRu('Любое название страны')
	}
	
	//
	// Tests for add()
	//
	
	def "add() should throw exception when dto is null"() {
		when:
			service.add(null, userId)
		then:
			thrown IllegalArgumentException
	}
	
	def "add() should throw exception when country name in English is null"() {
		given:
			form.setName(null)
		when:
			service.add(form, userId)
		then:
			thrown IllegalArgumentException
	}
	
	def "add() should throw exception when country name in Russian is null"() {
		given:
			form.setNameRu(null)
		when:
			service.add(form, userId)
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
			Integer expectedId = 10
		and:
			form.setName('Example Country')
		and:
			String expectedSlug = 'example-country'
		and:
			countryDao.add(_ as AddCountryDbDto) >> expectedId
		and:
			UrlEntityDto expected = new UrlEntityDto(expectedId, expectedSlug)
		when:
			UrlEntityDto actual = service.add(form, userId)
		then:
			actual == expected
	}
	
	def "add() should pass country name in English to dao"() {
		given:
			String expectedCountryName = 'Italy'
			form.setName(expectedCountryName)
		when:
			service.add(form, userId)
		then:
			1 * countryDao.add({ AddCountryDbDto country ->
				assert country?.name == expectedCountryName
				return true
			}) >> 20
	}
	
	def "add() should pass country name in Russian to dao"() {
		given:
			String expectedCountryName = 'Италия'
			form.setNameRu(expectedCountryName)
		when:
			service.add(form, userId)
		then:
			1 * countryDao.add({ AddCountryDbDto country ->
				assert country?.nameRu == expectedCountryName
				return true
			}) >> 30
	}
	
	def "add() should throw exception when name can't be converted to slug"() {
		given:
			form.setName(null)
		when:
			service.add(form, userId)
		then:
			thrown IllegalArgumentException
	}
	
	def "add() should pass slug to dao"() {
		given:
			String name = "-foo123 test_"
		and:
			String slug = SlugUtils.slugify(name)
		and:
			form.setName(name)
		when:
			service.add(form, userId)
		then:
			1 * countryDao.add({ AddCountryDbDto country ->
				assert country?.slug == slug
				return true
			}) >> 40
	}
	
	def "add() should assign created at to current date"() {
		when:
			service.add(form, userId)
		then:
			1 * countryDao.add({ AddCountryDbDto country ->
				assert DateUtils.roughlyEqual(country?.createdAt, new Date())
				return true
			}) >> 50
	}
	
	def "add() should assign updated at to current date"() {
		when:
			service.add(form, userId)
		then:
			1 * countryDao.add({ AddCountryDbDto country ->
				assert DateUtils.roughlyEqual(country?.updatedAt, new Date())
				return true
			}) >> 60
	}
	
	def "add() should assign created by to user"() {
		given:
			Integer expectedUserId = 10
		when:
			service.add(form, expectedUserId)
		then:
			1 * countryDao.add({ AddCountryDbDto country ->
				assert country?.createdBy == expectedUserId
				return true
			}) >> 70
	}
	
	def "add() should assign updated by to user"() {
		given:
			Integer expectedUserId = 10
		when:
			service.add(form, expectedUserId)
		then:
			1 * countryDao.add({ AddCountryDbDto country ->
				assert country?.updatedBy == expectedUserId
				return true
			}) >> 80
	}
	
	//
	// Tests for findAllAsSelectEntities(String)
	//
	
	def "findAllAsSelectEntities(String) should call dao"() {
		given:
			SelectEntityDto country1 = new SelectEntityDto(1, 'First Country')
		and:
			SelectEntityDto country2 = new SelectEntityDto(2, 'Second Country')
		and:
			List<SelectEntityDto> expectedCountries = [ country1, country2 ]
		and:
			countryDao.findAllAsSelectEntities(_ as String) >> expectedCountries
		when:
			Iterable<SelectEntityDto> resultCountries = service.findAllAsSelectEntities('de')
		then:
			resultCountries == expectedCountries
	}
	
	@Unroll
	def "findAllAsSelectEntities(String) should pass language '#expectedLanguage' to dao"(String expectedLanguage) {
		when:
			service.findAllAsSelectEntities(expectedLanguage)
		then:
			1 * countryDao.findAllAsSelectEntities({ String language ->
				assert language == expectedLanguage
				return true
			})
		where:
			expectedLanguage | _
			'ru'             | _
			null             | _
	}
	
	//
	// Tests for findAllAsLinkEntities(String)
	//
	
	def "findAllAsLinkEntities(String) should call dao"() {
		given:
			LinkEntityDto country1 = new LinkEntityDto(1, 'first-country', 'First Country')
		and:
			LinkEntityDto country2 = new LinkEntityDto(2, 'second-country', 'Second Country')
		and:
			List<LinkEntityDto> expectedCountries = [ country1, country2 ]
		and:
			countryDao.findAllAsLinkEntities(_ as String) >> expectedCountries
		when:
			Iterable<LinkEntityDto> resultCountries = service.findAllAsLinkEntities('de')
		then:
			resultCountries == expectedCountries
	}
	
	@Unroll
	def "findAllAsLinkEntities(String) should pass language '#expectedLanguage' to dao"(String expectedLanguage) {
		when:
			service.findAllAsLinkEntities(expectedLanguage)
		then:
			1 * countryDao.findAllAsLinkEntities({ String language ->
				assert language == expectedLanguage
				return true
			})
		where:
			expectedLanguage | _
			'ru'             | _
			null             | _
	}
	
	//
	// Tests for findOneAsLinkEntity()
	//
	
	def "findOneAsLinkEntity() should throw exception when country id is null"() {
		when:
			service.findOneAsLinkEntity(null, 'ru')
		then:
			thrown IllegalArgumentException
	}
	
	def "findOneAsLinkEntity() should pass arguments to dao"() {
		given:
			Integer expectedCountryId = 15
		and:
			String expectedLang = 'fr'
		and:
			LinkEntityDto expectedDto = TestObjects.createLinkEntityDto()
		when:
			LinkEntityDto actualDto = service.findOneAsLinkEntity(expectedCountryId, expectedLang)
		then:
			1 * countryDao.findOneAsLinkEntity(
				{ Integer countryId ->
					assert expectedCountryId == countryId
					return true
				},
				{ String lang ->
					assert expectedLang == lang
					return true
				}
			) >> expectedDto
		and:
			actualDto == expectedDto
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
			1 * countryDao.countAll() >> expectedResult
		and:
			result == expectedResult
	}
	
	//
	// Tests for countCountriesOf()
	//
	
	def "countCountriesOf() should throw exception when collection id is null"() {
		when:
			service.countCountriesOf(null)
		then:
			thrown IllegalArgumentException
	}
	
	def "countCountriesOf() should pass arguments to dao"() {
		given:
			Integer expectedCollectionId = 9
		when:
			service.countCountriesOf(expectedCollectionId)
		then:
			1 * countryDao.countCountriesOfCollection({ Integer collectionId ->
				assert expectedCollectionId == collectionId
				return true
			}) >> 0L
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
			countryDao.countByName(_ as String) >> 2L
		when:
			long result = service.countByName('Any name here')
		then:
			result == 2L
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
			countryDao.countByNameRu(_ as String) >> 2L
		when:
			long result = service.countByNameRu('Any name here')
		then:
			result == 2L
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
	
	def "getStatisticsOf() should throw exception when collection id is null"() {
		when:
			service.getStatisticsOf(null, 'whatever')
		then:
			thrown IllegalArgumentException
	}
	
	def "getStatisticsOf() should pass arguments to dao"() {
		given:
			Integer expectedCollectionId = 17
		and:
			String expectedLang = 'expected'
		when:
			service.getStatisticsOf(expectedCollectionId, expectedLang)
		then:
			1 * countryDao.getStatisticsOf(
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
