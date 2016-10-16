/**
 * Copyright (C) 2009-2016 Slava Semushin <slava.semushin@gmail.com>
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
import ru.mystamps.web.dao.dto.AddCountryDbDto
import ru.mystamps.web.model.AddCountryForm
import ru.mystamps.web.dao.dto.LinkEntityDto
import ru.mystamps.web.tests.DateUtils
import ru.mystamps.web.util.SlugUtils

@SuppressWarnings(['ClassJavadoc', 'MethodName', 'NoDef', 'NoTabCharacter', 'TrailingWhitespace'])
class CountryServiceImplTest extends Specification {
	
	private static final Integer USER_ID = 321
	
	private final CountryDao countryDao = Mock()
	private final CountryService service = new CountryServiceImpl(countryDao)
	
	private AddCountryForm form
	
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
			service.add(null, USER_ID)
		then:
			thrown IllegalArgumentException
	}
	
	def "add() should throw exception when country name in English is null"() {
		given:
			form.setName(null)
		when:
			service.add(form, USER_ID)
		then:
			thrown IllegalArgumentException
	}
	
	def "add() should throw exception when country name in Russian is null"() {
		given:
			form.setNameRu(null)
		when:
			service.add(form, USER_ID)
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
		when:
			String actualSlug = service.add(form, USER_ID)
		then:
			actualSlug == expectedSlug
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "add() should pass country name in English to dao"() {
		given:
			String expectedCountryName = 'Italy'
			form.setName(expectedCountryName)
		when:
			service.add(form, USER_ID)
		then:
			1 * countryDao.add({ AddCountryDbDto country ->
				assert country?.name == expectedCountryName
				return true
			}) >> 20
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "add() should pass country name in Russian to dao"() {
		given:
			String expectedCountryName = 'Италия'
			form.setNameRu(expectedCountryName)
		when:
			service.add(form, USER_ID)
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
			service.add(form, USER_ID)
		then:
			thrown IllegalArgumentException
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "add() should pass slug to dao"() {
		given:
			String name = '-foo123 test_'
		and:
			String slug = SlugUtils.slugify(name)
		and:
			form.setName(name)
		when:
			service.add(form, USER_ID)
		then:
			1 * countryDao.add({ AddCountryDbDto country ->
				assert country?.slug == slug
				return true
			}) >> 40
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "add() should assign created at to current date"() {
		when:
			service.add(form, USER_ID)
		then:
			1 * countryDao.add({ AddCountryDbDto country ->
				assert DateUtils.roughlyEqual(country?.createdAt, new Date())
				return true
			}) >> 50
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "add() should assign updated at to current date"() {
		when:
			service.add(form, USER_ID)
		then:
			1 * countryDao.add({ AddCountryDbDto country ->
				assert DateUtils.roughlyEqual(country?.updatedAt, new Date())
				return true
			}) >> 60
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
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
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
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
			List<LinkEntityDto> resultCountries = service.findAllAsLinkEntities('de')
		then:
			resultCountries == expectedCountries
	}
	
	@Unroll
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
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
	@Unroll
	def "findOneAsLinkEntity() should throw exception when country slug is '#slug'"(String slug) {
		when:
			service.findOneAsLinkEntity(slug, 'ru')
		then:
			thrown IllegalArgumentException
		where:
			slug | _
			' '  | _
			''   | _
			null | _
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "findOneAsLinkEntity() should pass arguments to dao"() {
		given:
			String expectedSlug = 'france'
		and:
			String expectedLang = 'fr'
		and:
			LinkEntityDto expectedDto = TestObjects.createLinkEntityDto()
		when:
			LinkEntityDto actualDto = service.findOneAsLinkEntity(expectedSlug, 'fr')
		then:
			1 * countryDao.findOneAsLinkEntity(
				{ String countrySlug ->
					assert expectedSlug == countrySlug
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
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
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
	// Tests for countBySlug()
	//
	
	def "countBySlug() should throw exception when slug is null"() {
		when:
			service.countBySlug(null)
		then:
			thrown IllegalArgumentException
	}
	
	def "countBySlug() should call dao"() {
		given:
			countryDao.countBySlug(_ as String) >> 3L
		when:
			long result = service.countBySlug('any-slug')
		then:
			result == 3L
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
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "countByName() should pass country name to dao in lowercase"() {
		when:
			service.countByName('Canada')
		then:
			1 * countryDao.countByName({ String name ->
				assert name == 'canada'
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
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "countByNameRu() should pass category name to dao in lowercase"() {
		when:
			service.countByNameRu('Канада')
		then:
			1 * countryDao.countByNameRu({ String name ->
				assert name == 'канада'
				return true
			})
	}
	
	//
	// Tests for countAddedSince()
	//
	
	def "countAddedSince() should throw exception when date is null"() {
		when:
			service.countAddedSince(null)
		then:
			thrown IllegalArgumentException
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "countAddedSince() should invoke dao, pass argument and return result from dao"() {
		given:
			Date expectedDate = new Date()
		and:
			long expectedResult = 34
		when:
			long result = service.countAddedSince(expectedDate)
		then:
			1 * countryDao.countAddedSince({ Date date ->
				assert date == expectedDate
				return true
			}) >> expectedResult
		and:
			result == expectedResult
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
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
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
