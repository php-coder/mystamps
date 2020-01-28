/**
 * Copyright (C) 2009-2020 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.feature.country

import org.slf4j.helpers.NOPLogger
import ru.mystamps.web.common.LinkEntityDto
import ru.mystamps.web.common.SlugUtils
import ru.mystamps.web.service.TestObjects
import ru.mystamps.web.tests.DateUtils
import ru.mystamps.web.tests.Random
import spock.lang.Specification
import spock.lang.Unroll

import static io.qala.datagen.RandomShortApi.nullOr
import static io.qala.datagen.RandomShortApi.nullOrBlank
import static io.qala.datagen.RandomShortApi.positiveInteger
import static io.qala.datagen.RandomValue.between
import static io.qala.datagen.StringModifier.Impls.oneOf

@SuppressWarnings([
	'ClassJavadoc',
	'MethodName',
	'MisorderedStaticImports',
	'NoDef',
	'NoTabCharacter',
	'TrailingWhitespace',
])
class LegacyCountryServiceImplTest extends Specification {
	
	private final CountryDao countryDao = Mock()
	private final CountryService service = new CountryServiceImpl(NOPLogger.NOP_LOGGER, countryDao)
	
	private AddCountryForm form
	
	def setup() {
		form = new AddCountryForm()
		form.setName(Random.countryName())
		form.setNameRu('Любое название страны')
	}
	
	//
	// Tests for add()
	//
	
	def 'add() should throw exception when dto is null'() {
		when:
			service.add(null, Random.userId())
		then:
			IllegalArgumentException ex = thrown()
			ex.message == 'DTO must be non null'
	}
	
	def 'add() should throw exception when country name in English is null'() {
		given:
			form.setName(null)
		when:
			service.add(form, Random.userId())
		then:
			IllegalArgumentException ex = thrown()
			ex.message == 'Country name in English must be non null'
	}
	
	def 'add() should throw exception when user is null'() {
		when:
			service.add(form, null)
		then:
			IllegalArgumentException ex = thrown()
			ex.message == 'User id must be non null'
	}
	
	def 'add() should call dao'() {
		given:
			Integer expectedId = 10
		and:
			form.setName('Example Country')
		and:
			String expectedSlug = 'example-country'
		and:
			countryDao.add(_ as AddCountryDbDto) >> expectedId
		when:
			String actualSlug = service.add(form, Random.userId())
		then:
			actualSlug == expectedSlug
	}
	
	def "add() should throw exception when name can't be converted to slug"() {
		given:
			form.setName('-')
		when:
			service.add(form, Random.userId())
		then:
			IllegalArgumentException ex = thrown()
			ex.message == "Slug for string '-' must be non empty"
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def 'add() should pass slug to dao'() {
		given:
			String name = '-foo123 test_'
		and:
			String slug = SlugUtils.slugify(name)
		and:
			form.setName(name)
		when:
			service.add(form, Random.userId())
		then:
			1 * countryDao.add({ AddCountryDbDto country ->
				assert country?.slug == slug
				return true
			}) >> 40
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def 'add() should pass values to dao'() {
		given:
			Integer expectedUserId = 10
			String expectedEnglishName = 'Italy'
			String expectedRussianName = 'Италия'
		and:
			form.setName(expectedEnglishName)
			form.setNameRu(expectedRussianName)
		when:
			service.add(form, expectedUserId)
		then:
			1 * countryDao.add({ AddCountryDbDto country ->
				assert country?.name == expectedEnglishName
				assert country?.nameRu == expectedRussianName
				assert country?.createdBy == expectedUserId
				assert country?.updatedBy == expectedUserId
				assert DateUtils.roughlyEqual(country?.createdAt, new Date())
				assert DateUtils.roughlyEqual(country?.updatedAt, new Date())
				return true
			}) >> 80
	}
	
	//
	// Tests for findIdsByNames()
	//
	
	def 'findIdsByNames() should return empty result when no names are specified'() {
		given:
			List<String> names = nullOr(Collections.emptyList())
		expect:
			service.findIdsByNames(names) == []
	}
	
	def 'findIdsByNames() should invoke dao, pass argument and return result from dao'() {
		given:
			List<String> expectedNames = Random.listOfStrings()
			List<Integer> expectedResult = Random.listOfIntegers()
		when:
			List<Integer> result = service.findIdsByNames(expectedNames)
		then:
			1 * countryDao.findIdsByNames(expectedNames) >> expectedResult
		and:
			result == expectedResult
	}
	
	def 'findIdsByNames() should pass names to dao in lower case'() {
		given:
			List<String> names = [ 'Foo', 'BAR' ]
			List<String> expectedNames = [ 'foo', 'bar' ]
		when:
			service.findIdsByNames(names)
		then:
			1 * countryDao.findIdsByNames(expectedNames) >> Random.listOfIntegers()
	}
	
	//
	// Tests for findIdsWhenNameStartsWith()
	//
	
	def 'findIdsWhenNameStartsWith() should throw exception when name is null, empty or blank'() {
		when:
			service.findIdsWhenNameStartsWith(nullOrBlank())
		then:
			IllegalArgumentException ex = thrown()
			ex.message == 'Name must be non-blank'
	}
	
	def 'findIdsWhenNameStartsWith() should throw exception when name contains percent or underscore character'() {
		given:
			String invalidName = between(1, 10).with(oneOf('%_')).english()
		when:
			service.findIdsWhenNameStartsWith(invalidName)
		then:
			IllegalArgumentException ex = thrown()
			ex.message == "Name must not contain '%' or '_' chars"
	}
	
	def 'findIdsWhenNameStartsWith() should invoke dao, pass argument and return result from dao'() {
		given:
			String name = between(1, 10).english().toLowerCase(Locale.ENGLISH)
			String expectedPattern = name + '%'
		and:
			List<Integer> expectedResult = Random.listOfIntegers()
		when:
			List<Integer> result = service.findIdsWhenNameStartsWith(name)
		then:
			1 * countryDao.findIdsByNamePattern(expectedPattern) >> expectedResult
		and:
			result == expectedResult
	}
	
	//
	// Tests for findAllAsLinkEntities(String)
	//
	
	def 'findAllAsLinkEntities(String) should call dao'() {
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
	def "findAllAsLinkEntities(String) should pass language '#expectedLanguage' to dao"(String expectedLanguage) {
		when:
			service.findAllAsLinkEntities(expectedLanguage)
		then:
			1 * countryDao.findAllAsLinkEntities(expectedLanguage)
		where:
			expectedLanguage | _
			'ru'             | _
			null             | _
	}
	
	//
	// Tests for findOneAsLinkEntity()
	//
	
	def 'findOneAsLinkEntity() should throw exception when country slug is null'() {
		when:
			service.findOneAsLinkEntity(null, Random.lang())
		then:
			IllegalArgumentException ex = thrown()
			ex.message == 'Country slug must be non null'
	}
	
	@Unroll
	def "findOneAsLinkEntity() should throw exception when country slug is '#slug'"(String slug) {
		when:
			service.findOneAsLinkEntity(slug, 'ru')
		then:
			IllegalArgumentException ex = thrown()
			ex.message == 'Country slug must be non empty'
		where:
			slug | _
			' '  | _
			''   | _
	}
	
	def 'findOneAsLinkEntity() should pass arguments to dao'() {
		given:
			String expectedSlug = 'france'
		and:
			String expectedLang = 'fr'
		and:
			LinkEntityDto expectedDto = TestObjects.createLinkEntityDto()
		when:
			LinkEntityDto actualDto = service.findOneAsLinkEntity(expectedSlug, expectedLang)
		then:
			1 * countryDao.findOneAsLinkEntity(expectedSlug, expectedLang) >> expectedDto
		and:
			actualDto == expectedDto
	}
	
	//
	// Tests for countAll()
	//
	
	def 'countAll() should call dao and returns result'() {
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
	
	def 'countCountriesOf() should throw exception when collection id is null'() {
		when:
			service.countCountriesOf(null)
		then:
			IllegalArgumentException ex = thrown()
			ex.message == 'Collection id must be non null'
	}
	
	def 'countCountriesOf() should pass arguments to dao'() {
		given:
			Integer expectedCollectionId = 9
		when:
			service.countCountriesOf(expectedCollectionId)
		then:
			1 * countryDao.countCountriesOfCollection(expectedCollectionId) >> 0L
	}
	
	//
	// Tests for countBySlug()
	//
	
	def 'countBySlug() should throw exception when slug is null'() {
		when:
			service.countBySlug(null)
		then:
			IllegalArgumentException ex = thrown()
			ex.message == 'Country slug must be non null'
	}
	
	def 'countBySlug() should call dao'() {
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
	
	def 'countByName() should throw exception when name is null'() {
		when:
			service.countByName(null)
		then:
			IllegalArgumentException ex = thrown()
			ex.message == 'Name must be non null'
	}
	
	def 'countByName() should call dao'() {
		given:
			countryDao.countByName(_ as String) >> 2L
		when:
			long result = service.countByName('Any name here')
		then:
			result == 2L
	}
	
	def 'countByName() should pass country name to dao in lowercase'() {
		when:
			service.countByName('Canada')
		then:
			1 * countryDao.countByName('canada')
	}
	
	//
	// Tests for countByNameRu()
	//
	
	def 'countByNameRu() should throw exception when name is null'() {
		when:
			service.countByNameRu(null)
		then:
			IllegalArgumentException ex = thrown()
			ex.message == 'Name in Russian must be non null'
	}
	
	def 'countByNameRu() should call dao'() {
		given:
			countryDao.countByNameRu(_ as String) >> 2L
		when:
			long result = service.countByNameRu('Any name here')
		then:
			result == 2L
	}
	
	def 'countByNameRu() should pass category name to dao in lowercase'() {
		when:
			service.countByNameRu('Канада')
		then:
			1 * countryDao.countByNameRu('канада')
	}
	
	//
	// Tests for countAddedSince()
	//
	
	def 'countAddedSince() should throw exception when date is null'() {
		when:
			service.countAddedSince(null)
		then:
			IllegalArgumentException ex = thrown()
			ex.message == 'Date must be non null'
	}
	
	def 'countAddedSince() should invoke dao, pass argument and return result from dao'() {
		given:
			Date expectedDate = new Date()
		and:
			long expectedResult = 34
		when:
			long result = service.countAddedSince(expectedDate)
		then:
			1 * countryDao.countAddedSince(expectedDate) >> expectedResult
		and:
			result == expectedResult
	}
	
	//
	// Tests for countUntranslatedNamesSince()
	//
	
	def 'countUntranslatedNamesSince() should throw exception when date is null'() {
		when:
			service.countUntranslatedNamesSince(null)
		then:
			IllegalArgumentException ex = thrown()
			ex.message == 'Date must be non null'
	}
	
	def 'countUntranslatedNamesSince() should invoke dao, pass argument and return result from dao'() {
		given:
			Date expectedDate = new Date()
		and:
			long expectedResult = 18
		when:
			long result = service.countUntranslatedNamesSince(expectedDate)
		then:
			1 * countryDao.countUntranslatedNamesSince(expectedDate) >> expectedResult
		and:
			result == expectedResult
	}
	
	//
	// Tests for getStatisticsOf()
	//
	
	def 'getStatisticsOf() should throw exception when collection id is null'() {
		when:
			service.getStatisticsOf(null, Random.lang())
		then:
			IllegalArgumentException ex = thrown()
			ex.message == 'Collection id must be non null'
	}
	
	def 'getStatisticsOf() should invoke dao, pass arguments and return result from dao'() {
		given:
			Integer expectedCollectionId = Random.id()
		and:
			String expectedLang = Random.lang()
		and:
			Map<String, Integer> expectedResult = [ (Random.countryName()):positiveInteger() ]
		when:
			Map<String, Integer> result = service.getStatisticsOf(expectedCollectionId, expectedLang)
		then:
			1 * countryDao.getStatisticsOf(expectedCollectionId, expectedLang) >> expectedResult
		and:
			result == expectedResult
	}
	
	//
	// Tests for suggestCountryForUser()
	//
	
	def 'suggestCountryForUser() should throw exception when user id is null'() {
		when:
			service.suggestCountryForUser(null)
		then:
			IllegalArgumentException ex = thrown()
			ex.message == 'User id must be non null'
	}
	
	def 'suggestCountryForUser() should return country of the last created series'() {
		given:
			Integer expectedUserId = Random.userId()
			String expectedSlug = Random.countrySlug()
		when:
			String slug = service.suggestCountryForUser(expectedUserId)
		then:
			1 * countryDao.findCountryOfLastCreatedSeriesByUser(expectedUserId) >> expectedSlug
		and:
			slug == expectedSlug
	}
	
	def 'suggestCountryForUser() should return popular country from collection'() {
		given:
			Integer expectedUserId = Random.userId()
			String expectedSlug = Random.countrySlug()
		when:
			String slug = service.suggestCountryForUser(expectedUserId)
		then:
			1 * countryDao.findPopularCountryInCollection(expectedUserId) >> expectedSlug
		and:
			slug == expectedSlug
	}

	def 'suggestCountryForUser() should return a recently created country'() {
		given:
			Integer expectedUserId = Random.userId()
			String expectedSlug = Random.countrySlug()
		when:
			String slug = service.suggestCountryForUser(expectedUserId)
		then:
			1 * countryDao.findLastCountryCreatedByUser(expectedUserId) >> expectedSlug
		and:
			slug == expectedSlug
	}

	def 'suggestCountryForUser() should return null when cannot suggest'() {
		when:
			String slug = service.suggestCountryForUser(Random.userId())
		then:
			1 * countryDao.findCountryOfLastCreatedSeriesByUser(_ as Integer) >> null
			1 * countryDao.findPopularCountryInCollection(_ as Integer) >> null
			1 * countryDao.findLastCountryCreatedByUser(_ as Integer) >> null
		and:
			slug == null
	}
	
}
