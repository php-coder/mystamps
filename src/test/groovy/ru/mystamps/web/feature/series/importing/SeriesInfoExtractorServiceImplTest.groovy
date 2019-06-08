/**
 * Copyright (C) 2009-2019 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.feature.series.importing

import org.slf4j.helpers.NOPLogger
import ru.mystamps.web.feature.category.CategoryService
import ru.mystamps.web.feature.category.CategoryValidation
import ru.mystamps.web.feature.country.CountryService
import ru.mystamps.web.feature.country.CountryValidation
import ru.mystamps.web.feature.participant.ParticipantService
import ru.mystamps.web.tests.Random
import ru.mystamps.web.validation.ValidationRules
import spock.lang.Specification
import spock.lang.Unroll

import java.time.Year

import static io.qala.datagen.RandomElements.from
import static io.qala.datagen.RandomShortApi.nullOrBlank
import static io.qala.datagen.RandomValue.between
import static ru.mystamps.web.feature.series.importing.SeriesInfoExtractorServiceImpl.MAX_SUPPORTED_RELEASE_YEAR
import static ru.mystamps.web.validation.ValidationRules.MAX_STAMPS_IN_SERIES

@SuppressWarnings([
	'ClassJavadoc',
	'MethodName',
	'MisorderedStaticImports',
	'NoDef',
	'NoTabCharacter',
	'TrailingWhitespace',
])
class SeriesInfoExtractorServiceImplTest extends Specification {
	
	private final CategoryService categoryService = Mock()
	private final CountryService countryService = Mock()
	private final ParticipantService participantService = Mock()
	private final SeriesInfoExtractorService service = new SeriesInfoExtractorServiceImpl(
		NOPLogger.NOP_LOGGER,
		categoryService,
		countryService,
		participantService
	)
	
	//
	// Tests for extractCategory()
	//
	
	def 'extractCategory() should return empty result when fragment is null, empty or blank'() {
		given:
			String fragment = nullOrBlank()
		when:
			List<Integer> result = service.extractCategory(fragment)
		then:
			result.isEmpty()
	}
	
	def 'extractCategory() should try to search by category names'() {
		given:
			String fragment = 'Lorem ipsum   dolor\tsit\namet, consectetur.'
			List<String> expectedCandidates = [ 'Lorem', 'ipsum', 'dolor', 'sit', 'amet', 'consectetur' ]
		and:
			List<Integer> expectedResult = Random.listOfIntegers()
		when:
			List<Integer> result = service.extractCategory(fragment)
		then:
			1 * categoryService.findIdsByNames(expectedCandidates) >> expectedResult
		and:
			result == expectedResult
	}
	
	def 'extractCategory() should filter out invalid candidates'() {
		given:
			String shortName = 'ok'
			String longName = 'x' * (CategoryValidation.CATEGORY_NAME_MAX_LENGTH + 1)
			String invalidEnName = 't3st_'
			String invalidRuName = 'т_е_с_т'
			String validEnName = 'valid'
			String validRuName = 'норм'
			List<String> expectedCandidates = [ validEnName, validRuName ]
			String fragment = [ shortName, longName, invalidEnName, invalidRuName, validEnName, validRuName ].join(' ')
		when:
			service.extractCategory(fragment)
		then:
			1 * categoryService.findIdsByNames(expectedCandidates) >> Random.listOfIntegers()
	}
	
	def 'extractCategory() should deduplicate candidates'() {
		given:
			String fragment = 'foo bar foo'
			List<String> expectedCandidates = [ 'foo', 'bar' ]
		when:
			service.extractCategory(fragment)
		then:
			1 * categoryService.findIdsByNames(expectedCandidates) >> Random.listOfIntegers()
	}
	
	def 'extractCategory() should try to search category names with candidate as a prefix'() {
		given:
			List<Integer> expectedResult = Random.listOfIntegers()
		when:
			List<Integer> result = service.extractCategory('Sport Space')
		then:
			// in order to search by prefix, we shouldn't find anything by name
			1 * categoryService.findIdsByNames(_ as List<String>) >> Collections.emptyList()
		and:
			// the first lookup will find nothing
			1 * categoryService.findIdsWhenNameStartsWith('Sport') >> Collections.emptyList()
		and:
			// the second lookup will return a result
			1 * categoryService.findIdsWhenNameStartsWith('Space') >> expectedResult
		and:
			result == expectedResult
	}
	
	def 'extractCategory() should return an empty result when nothing has been found'() {
		when:
			List<Integer> result = service.extractCategory('foo')
		then:
			1 * categoryService.findIdsByNames(_ as List<String>) >> Collections.emptyList()
		and:
			1 * categoryService.findIdsWhenNameStartsWith(_ as String) >> Collections.emptyList()
		and:
			result.isEmpty()
	}
	
	//
	// Tests for extractCountry()
	//
	
	def 'extractCountry() should return empty result when fragment is null, empty or blank'() {
		given:
			String fragment = nullOrBlank()
		when:
			List<Integer> result = service.extractCountry(fragment)
		then:
			result.isEmpty()
	}
	
	def 'extractCountry() should try to search by country names'() {
		given:
			String fragment = 'Lorem ipsum   dolor\tsit\namet, consectetur.'
			List<String> expectedCandidates = [ 'Lorem', 'ipsum', 'dolor', 'sit', 'amet', 'consectetur' ]
		and:
			List<Integer> expectedResult = Random.listOfIntegers()
		when:
			List<Integer> result = service.extractCountry(fragment)
		then:
			1 * countryService.findIdsByNames(expectedCandidates) >> expectedResult
		and:
			result == expectedResult
	}
	
	def 'extractCountry() should filter out invalid candidates'() {
		given:
			String shortName = 'ok'
			String longName = 'x' * (CountryValidation.COUNTRY_NAME_MAX_LENGTH + 1)
			String invalidEnName = 't3st_'
			String invalidRuName = 'т_е_с_т'
			String validEnName = 'valid'
			String validRuName = 'норм'
			List<String> expectedCandidates = [ validEnName, validRuName ]
			String fragment = [ shortName, longName, invalidEnName, invalidRuName, validEnName, validRuName ].join(' ')
		when:
			service.extractCountry(fragment)
		then:
			1 * countryService.findIdsByNames(expectedCandidates) >> Random.listOfIntegers()
	}
	
	def 'extractCountry() should deduplicate candidates'() {
		given:
			String fragment = 'foo bar foo'
			List<String> expectedCandidates = [ 'foo', 'bar' ]
		when:
			service.extractCountry(fragment)
		then:
			1 * countryService.findIdsByNames(expectedCandidates) >> Random.listOfIntegers()
	}
	
	def 'extractCountry() should generate additional candidates by split words by a hyphen'() {
		given:
			String fragment = 'foo-bar'
			List<String> expectedCandidates = [ 'foo-bar', 'foo', 'bar' ]
		when:
			service.extractCountry(fragment)
		then:
			1 * countryService.findIdsByNames(expectedCandidates) >> Random.listOfIntegers()
	}
	
	def 'extractCountry() should try to search country names with candidate as a prefix'() {
		given:
			List<Integer> expectedResult = Random.listOfIntegers()
		when:
			List<Integer> result = service.extractCountry('Sweden Norway')
		then:
			// in order to search by prefix, we shouldn't find anything by name
			1 * countryService.findIdsByNames(_ as List<String>) >> Collections.emptyList()
		and:
			// the first lookup will find nothing
			1 * countryService.findIdsWhenNameStartsWith('Sweden') >> Collections.emptyList()
		and:
			// the second lookup will return a result
			1 * countryService.findIdsWhenNameStartsWith('Norway') >> expectedResult
		and:
			result == expectedResult
	}
	
	def 'extractCountry() should return an empty result when nothing has been found'() {
		when:
			List<Integer> result = service.extractCountry('foo')
		then:
			1 * countryService.findIdsByNames(_ as List<String>) >> Collections.emptyList()
		and:
			1 * countryService.findIdsWhenNameStartsWith(_ as String) >> Collections.emptyList()
		and:
			result.isEmpty()
	}
	
	//
	// Tests for extractReleaseYear()
	//
	
	def 'extractReleaseYear() should return null when fragment is null, empty or blank'() {
		given:
			String fragment = nullOrBlank()
		when:
			Integer year = service.extractReleaseYear(fragment)
		then:
			year == null
	}
	
	def 'extractReleaseYear() should extract year from XIX century'() {
		given:
			Integer expectedYear = between(ValidationRules.MIN_RELEASE_YEAR, 1899).integer()
		and:
			String fragment = String.valueOf(expectedYear)
		when:
			Integer year = service.extractReleaseYear(fragment)
		then:
			year == expectedYear
	}
	
	def 'extractReleaseYear() should extract year from XX century'() {
		given:
			Integer expectedYear = between(1900, 1999).integer()
		and:
			String fragment = String.valueOf(expectedYear)
		when:
			Integer year = service.extractReleaseYear(fragment)
		then:
			year == expectedYear
	}
	
	def 'extractReleaseYear() should extract year from XXI century'() {
		given:
			Integer expectedYear = between(2000, MAX_SUPPORTED_RELEASE_YEAR).integer()
		and:
			String fragment = String.valueOf(expectedYear)
		when:
			Integer year = service.extractReleaseYear(fragment)
		then:
			year == expectedYear
	}
	
	@Unroll
	def 'extractReleaseYear() should extract date from "#fragment"'(String fragment) {
		given:
			Integer expectedYear = 2010 // should be in sync with examples below
		when:
			Integer year = service.extractReleaseYear(fragment)
		then:
			year == expectedYear
		where:
			fragment                         | _
			'italy 2010'                     | _
			'2010 brazil'                    | _
			'2010\t\tbrazil'                 | _
			'2010     brazil'                | _
			'prehistoric animals 2010 congo' | _
			'2010г'                          | _
			'Палау 2010, 2 малых листа'      | _
			'test,2010'                      | _
	}
	
	@SuppressWarnings('UnnecessaryGetter')
	def 'extractReleaseYear() should return the first year if there are many'() {
		given:
			Integer currentYear = Year.now().getValue()
			Integer expectedYear = between(ValidationRules.MIN_RELEASE_YEAR, currentYear).integer()
		and:
			Integer anotherYear = between(ValidationRules.MIN_RELEASE_YEAR, currentYear).integer()
		and:
			String fragment = String.format('%d %d', expectedYear, anotherYear)
		when:
			Integer year = service.extractReleaseYear(fragment)
		then:
			year == expectedYear
	}
	
	@SuppressWarnings('UnnecessaryGetter')
	def 'extractReleaseYear() should skip invalid date'() {
		given:
			Integer unsupportedYearInPast = between(0, ValidationRules.MIN_RELEASE_YEAR - 1).integer()
			Integer unsupportedYearInFuture = between(MAX_SUPPORTED_RELEASE_YEAR + 1, Integer.MAX_VALUE).integer()
			Integer unsupportedYear = from(unsupportedYearInPast, unsupportedYearInFuture).sample()
		and:
			Integer currentYear = Year.now().getValue()
			Integer expectedYear = between(ValidationRules.MIN_RELEASE_YEAR, currentYear).integer()
		and:
			String fragment = String.format('%d %d', unsupportedYear, expectedYear)
		when:
			Integer year = service.extractReleaseYear(fragment)
		then:
			year == expectedYear
	}
	
	def 'extractReleaseYear() shouldn\'t extract dates before 1840'() {
		given:
			Integer unsupportedYear = between(0, ValidationRules.MIN_RELEASE_YEAR - 1).integer()
			String fragment = String.valueOf(unsupportedYear)
		when:
			Integer year = service.extractReleaseYear(fragment)
		then:
			year == null
	}
	
	def 'extractReleaseYear() shouldn\'t extract dates after 2099'() {
		given:
			Integer unsupportedYear = between(MAX_SUPPORTED_RELEASE_YEAR + 1, Integer.MAX_VALUE).integer()
			String fragment = String.valueOf(unsupportedYear)
		when:
			Integer year = service.extractReleaseYear(fragment)
		then:
			year == null
	}
	
	@Unroll
	def 'extractReleaseYear() shouldn\'t extract date from "#fragment"'(String fragment) {
		when:
			Integer year = service.extractReleaseYear(fragment)
		then:
			year == null
		where:
			fragment           | _
			'-2000'            | _
			'test2000'         | _
			'test-2000'        | _
			'test/2000'        | _
			'part of word2000' | _
	}

	//
	// Tests for extractQuantity()
	//
	
	def 'extractQuantity() should return null when fragment is null, empty or blank'() {
		expect:
			service.extractQuantity(nullOrBlank()) == null
	}
	
	@Unroll
	def 'extractQuantity() should return null for invalid quantity (#fragment)'(String fragment) {
		expect:
			service.extractQuantity(fragment) == null
		where:
			fragment                              | _
			'0 марок'                             | _
			(MAX_STAMPS_IN_SERIES + 1) + ' марок' | _
	}
	
	@Unroll
	@SuppressWarnings('UnnecessaryBooleanExpression')
	def 'extractQuantity() should extract quantity from "#fragment"'(String fragment, Integer expectedQuantity) {
		expect:
			service.extractQuantity(fragment) == expectedQuantity
		where:
			fragment               || expectedQuantity
			'5 марок'              ||  5
			'5 МАРОК'              ||  5
			'22 марки'             || 22
			'13 беззубцовые марок' || 13
			'4 беззубцовые марки'  ||  4
			'32 БЕЗЗУБЦОВЫЕ МАРКИ' || 32
			'6 блоков'             || 6
			'6 зубцовых блоков'    || 6
	}

	//
	// Tests for extractPerforated()
	//
	
	def 'extractPerforated() should return null when fragment is null, empty or blank'() {
		expect:
			service.extractPerforated(nullOrBlank()) == null
	}
	
	def 'extractPerforated() should return null when nothing to extract'() {
		expect:
			service.extractPerforated('10 марок') == null
	}
	
	@Unroll
	def 'extractPerforated() should extract perforated from "#fragment"'(String fragment) {
		expect:
			service.extractPerforated(fragment) == false
		where:
			fragment       | _
			'б/з'          | _
			'Б/З'          | _
			'беззубцовые'  | _
			'БЕЗЗУБЦОВЫЕ'  | _
			'б/перфорации' | _
	}
	
	//
	// Tests for extractMichelNumbers()
	//
	
	@Unroll
	@SuppressWarnings('UnnecessaryBooleanExpression') // false positive
	def 'extractMichelNumbers() should extract "#expected" from "#fragment"'(String fragment, Set<String> expected) {
		expect:
			service.extractMichelNumbers(fragment) == expected
		where:
			fragment      || expected
			nullOrBlank() || []
			'# 1-3'       || [ '1', '2', '3' ]
			'#9997-9999'  || [ '9997', '9998', '9999' ]
			'#9999-9997'  || []
			'#0997-0999'  || []
	}
	
	//
	// Tests for extractSeller()
	//
	
	def 'extractSeller() should return null when seller name is null, empty or blank'() {
		when:
			String result = service.extractSeller(null, nullOrBlank(), Random.url())
		then:
			result == null
	}
	
	def 'extractSeller() should return null when seller url is null, empty or blank'() {
		when:
			String result = service.extractSeller(null, Random.sellerName(), nullOrBlank())
		then:
			result == null
	}
	
	def 'extractSeller() should return null when page url is null, empty or blank'() {
		when:
			String result = service.extractSeller(nullOrBlank(), nullOrBlank(), nullOrBlank())
		then:
			result == null
	}
	
	@Unroll
	def 'extractSeller() should find by name/url and return (#expectedResult)'(Integer expectedResult) {
		given:
			String expectedName = Random.sellerName()
			String expectedUrl = Random.url()
		when:
			Integer result = service.extractSeller(null, expectedName, expectedUrl)
		then:
			1 * participantService.findSellerId(expectedName, expectedUrl) >> expectedResult
		and:
			result == expectedResult
		where:
			expectedResult | _
			null           | _
			Random.id()    | _
	}
	
	@Unroll
	def 'extractSeller() should return null for invalid or unknown page (#pageUrl)'(String pageUrl) {
		when:
			Integer sellerId = service.extractSeller(pageUrl, nullOrBlank(), nullOrBlank())
		then:
			sellerId == null
		where:
			pageUrl               | _
			'localhost'           | _
			'https://example.org' | _
	}
	
	def 'extractSeller() should find by site name'() {
		given:
			Integer expectedSellerId = Random.id()
		when:
			Integer sellerId = service.extractSeller('https://test.ru/some/page', nullOrBlank(), nullOrBlank())
		then:
			1 * participantService.findSellerId('test.ru') >> expectedSellerId
		and:
			sellerId == expectedSellerId
	}
	
	//
	// Tests for extractSellerGroup()
	//
	
	def 'extractSellerGroup() should return null when id is not null'() {
		expect:
			service.extractSellerGroup(Random.id(), Random.url()) == null
	}
	
	@Unroll
	def 'extractSellerGroup() should return null for invalid url or unknown group (#sellerUrl)'(String sellerUrl) {
		when:
			Integer groupId = service.extractSellerGroup(null, sellerUrl)
		then:
			groupId == null
		where:
			sellerUrl             | _
			nullOrBlank()         | _
			'localhost'           | _
			'https://example.org' | _
	}
	
	def 'extractSellerGroup() should return seller group id'() {
		given:
			Integer expectedGroupId = Random.id()
		when:
			Integer groupId = service.extractSellerGroup(null, 'https://test.ru/about/me')
		then:
			1 * participantService.findGroupIdByName('test.ru') >> expectedGroupId
		and:
			groupId == expectedGroupId
	}
	
	//
	// Tests for extractSellerName()
	//
	
	@Unroll
	@SuppressWarnings('UnnecessaryBooleanExpression') // false positive
	def 'extractSellerName() should return "#expected" for id=#id/name=#name'(Integer id, String name, String expected) {
		expect:
			service.extractSellerName(id, name) == expected
		where:
			id          | name                || expected
			Random.id() | Random.sellerName() || null
			null        | 'Seller Name'       || 'Seller Name'
	}
	
	//
	// Tests for extractSellerUrl()
	//
	
	@Unroll
	@SuppressWarnings('UnnecessaryBooleanExpression') // false positive
	def 'extractSellerUrl() should return "#expected" for id=#id/url=#url'(Integer id, String url, String expected) {
		expect:
			service.extractSellerUrl(id, url) == expected
		where:
			id          | url                  || expected
			Random.id() | Random.url()         || null
			null        | 'http://example.com' || 'http://example.com'
	}
	
	//
	// Tests for extractPrice()
	//
	
	def 'extractPrice() should return null when fragment is null, empty or blank'() {
		when:
			BigDecimal result = service.extractPrice(nullOrBlank())
		then:
			result == null
	}
	
	def 'extractPrice() should return null for invalid price'() {
		given:
			String invalidPrice = '20x'
		when:
			BigDecimal result = service.extractPrice(invalidPrice)
		then:
			result == null
	}

	def 'extractPrice() should extract price from a fragment'() {
		given:
			BigDecimal expectedPrice = Random.price()
		when:
			BigDecimal result = service.extractPrice(expectedPrice.toString())
		then:
			result == expectedPrice
	}
	
	//
	// Tests for extractCurrency()
	//
	
	def 'extractCurrency() should return null when fragment is null, empty or blank'() {
		when:
			String result = service.extractCurrency(nullOrBlank())
		then:
			result == null
	}
	
	def 'extractCurrency() should return null for unknown currency'() {
		given:
			String invalidCurrency = 'CAD'
		when:
			String result = service.extractCurrency(invalidCurrency)
		then:
			result == null
	}
	
	def 'extractCurrency() should extract currency from a fragment'() {
		given:
			String validCurrency = Random.currency()
		when:
			String result = service.extractCurrency(validCurrency)
		then:
			result == validCurrency
	}
	
}
