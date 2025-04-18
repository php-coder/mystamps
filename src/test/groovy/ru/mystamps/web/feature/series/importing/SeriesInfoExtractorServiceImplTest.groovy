/**
 * Copyright (C) 2009-2025 Slava Semushin <slava.semushin@gmail.com>
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
import ru.mystamps.web.feature.series.SeriesValidation
import spock.lang.Specification
import spock.lang.Unroll

import static io.qala.datagen.RandomElements.from
import static io.qala.datagen.RandomShortApi.nullOrBlank
import static io.qala.datagen.RandomValue.between
import static ru.mystamps.web.feature.series.importing.SeriesInfoExtractorServiceImpl.MAX_SUPPORTED_RELEASE_YEAR

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
			String longName = 'x' * (CategoryValidation.NAME_MAX_LENGTH + 1)
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
			String longName = 'x' * (CountryValidation.NAME_MAX_LENGTH + 1)
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
	// Tests for extractIssueDate()
	//
	
	def 'extractIssueDate() should return empty map when fragment is null, empty or blank'() {
		given:
			String fragment = nullOrBlank()
		when:
			Map<String, Integer> date = service.extractIssueDate(fragment)
		then:
			date.isEmpty()
	}
	
	def 'extractIssueDate() should extract year from XIX century'() {
		given:
			Integer expectedYear = between(SeriesValidation.MIN_RELEASE_YEAR, 1899).integer()
		and:
			String fragment = String.valueOf(expectedYear)
		when:
			Map<String, Integer> date = service.extractIssueDate(fragment)
		then:
			date.get('year') == expectedYear
	}
	
	def 'extractIssueDate() should extract year from XX century'() {
		given:
			Integer expectedYear = between(1900, 1999).integer()
		and:
			String fragment = String.valueOf(expectedYear)
		when:
			Map<String, Integer> date = service.extractIssueDate(fragment)
		then:
			date.get('year') == expectedYear
	}
	
	def 'extractIssueDate() should extract year from XXI century'() {
		given:
			Integer expectedYear = between(2000, MAX_SUPPORTED_RELEASE_YEAR).integer()
		and:
			String fragment = String.valueOf(expectedYear)
		when:
			Map<String, Integer> date = service.extractIssueDate(fragment)
		then:
			date.get('year') == expectedYear
	}
	
	@Unroll
	def 'extractIssueDate() should extract date from "#fragment"'(String fragment) {
		given:
			Integer expectedYear = 2010 // should be in sync with examples below
		when:
			Map<String, Integer> date = service.extractIssueDate(fragment)
		then:
			date.get('year') == expectedYear
		where:
			fragment                         | _
			'italy 2010'                     | _
			'2010 brazil'                    | _
			'2010\t\tbrazil'                 | _
			'2010     brazil'                | _
			'prehistoric animals 2010 congo' | _
			'2010.'                          | _
			'2010г'                          | _
			'2010г.'                         | _
			'2010год'                        | _
			'Палау 2010, 2 малых листа'      | _
			'test,2010'                      | _
			'01.02.2010'                     | _
	}
	
	def 'extractIssueDate() should return the first year if there are many'() {
		given:
			Integer expectedYear = Random.issueYear()
		and:
			Integer anotherYear = Random.issueYear()
		and:
			String fragment = String.format('%d %d', expectedYear, anotherYear)
		when:
			Map<String, Integer> date = service.extractIssueDate(fragment)
		then:
			date.get('year') == expectedYear
	}
	
	def 'extractIssueDate() should skip invalid date'() {
		given:
			Integer unsupportedYearInPast = between(0, SeriesValidation.MIN_RELEASE_YEAR - 1).integer()
			Integer unsupportedYearInFuture = between(MAX_SUPPORTED_RELEASE_YEAR + 1, Integer.MAX_VALUE).integer()
			Integer unsupportedYear = from(unsupportedYearInPast, unsupportedYearInFuture).sample()
		and:
			Integer expectedYear = Random.issueYear()
		and:
			String fragment = String.format('%d %d', unsupportedYear, expectedYear)
		when:
			Map<String, Integer> date = service.extractIssueDate(fragment)
		then:
			date.get('year') == expectedYear
	}
	
	def 'extractIssueDate() shouldn\'t extract dates before 1840'() {
		given:
			Integer unsupportedYear = between(0, SeriesValidation.MIN_RELEASE_YEAR - 1).integer()
			String fragment = String.valueOf(unsupportedYear)
		when:
			Map<String, Integer> date = service.extractIssueDate(fragment)
		then:
			date.isEmpty()
	}
	
	def 'extractIssueDate() shouldn\'t extract dates after 2099'() {
		given:
			Integer unsupportedYear = between(MAX_SUPPORTED_RELEASE_YEAR + 1, Integer.MAX_VALUE).integer()
			String fragment = String.valueOf(unsupportedYear)
		when:
			Map<String, Integer> date = service.extractIssueDate(fragment)
		then:
			date.isEmpty()
	}
	
	@Unroll
	def 'extractIssueDate() shouldn\'t extract date from "#fragment"'(String fragment) {
		when:
			Map<String, Integer> date = service.extractIssueDate(fragment)
		then:
			date.isEmpty()
		where:
			fragment           | _
			'-2000'            | _
			'test2000'         | _
			'test-2000'        | _
			'test/2000'        | _
			'part of word2000' | _
	}

	def 'extractIssueDate() should extract a full issue date'() {
		given:
			Integer expectedDay = Random.dayOfMonth()
			Integer expectedMonth = Random.monthOfYear()
			Integer expectedYear = Random.issueYear()
		and:
			String fragment = String.format('%02d.%02d.%d', expectedDay, expectedMonth, expectedYear)
		when:
			Map<String, Integer> date = service.extractIssueDate(fragment)
		then:
			date.get('day')   == expectedDay
			date.get('month') == expectedMonth
			date.get('year')  == expectedYear
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
			fragment                                               | _
			'2 чего-либо'                                          | _
			'0 марок'                                              | _
			'№1576-1578=4,2МЕ'                                     | _
			'Велоцираптор 2003 Блок с/з'                           | _
			(SeriesValidation.MAX_STAMPS_IN_SERIES + 1) + ' марок' | _
	}
	
	@Unroll
	def 'extractQuantity() should extract quantity from "#fragment"'(String fragment, Integer expectedQuantity) {
		expect:
			service.extractQuantity(fragment) == expectedQuantity
		where:
			fragment               || expectedQuantity
			'3м'                   ||  3
			'3м**'                 ||  3
			'1 марка'              ||  1
			'5марок'               ||  5
			'5 марок'              ||  5
			'5 МАРОК'              ||  5
			'22 марки'             || 22
			'13 беззубцовые марок' || 13
			'1 беззубцовая марка'  ||  1
			'4 беззубцовые марки'  ||  4
			'32 БЕЗЗУБЦОВЫЕ МАРКИ' || 32
			'1 блок'               || 1
			'4 блока'              || 4
			'2 люкс блока'         || 2
			'2 люкс-блока'         || 2
			'6 блоков'             || 6
			'6 БЛ'                 || 6
			'6 зубцовых блоков'    || 6
			'серия из 5-ти марок'  || 5
			'серия из 21-ой марки' || 21
			'серия из 22-ух марок' || 22
			'серия из 5ти марок'   || 5
			'серия из 21ой марки'  || 21
			'серия из 22ух марок'  || 22
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
			fragment           | _
			'б.з.'             | _
			'б/з'              | _
			'Б/З'              | _
			'БЗ'               | _
			'беззубцовый'      | _
			'беззубцовые'      | _
			'беззубцовых'      | _
			'БЕЗЗУБЦОВЫЕ'      | _
			'Беззубц.'         | _
			'Без перфорации'   | _
			'б/перфорации'     | _
			'без перф.'        | _
			'без зуб'          | _
			'без зубцов'       | _
			'Динозавры (б\\з)' | _
			'neperf.'          | _
			'Динозавры (неперфорированный, красный)' | _
	}
	
	//
	// Tests for extractMichelNumbers()
	//
	
	@Unroll
	def 'extractMichelNumbers() should extract "#expected" from "#fragment"'(String fragment, Set<String> expected) {
		expect:
			service.extractMichelNumbers(fragment) == expected
		where:
			fragment         || expected
			// negative cases
			nullOrBlank()    || []
			'#9999-9997'     || []
			'#0997-0999'     || []
			// positive cases
			'# 1-3'          || [ '1', '2', '3' ]
			'#9997-9999'     || [ '9997', '9998', '9999' ]
			'Michel 222-223' || [ '222', '223' ]
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

	def 'extractPrice() should extract exactly specified price'() {
		given:
			BigDecimal expectedPrice = Random.price()
		when:
			BigDecimal result = service.extractPrice(expectedPrice.toString())
		then:
			result == expectedPrice
	}
	
	@Unroll
	def 'extractPrice() should extract "#expected" from "#fragment"'(String fragment, BigDecimal expected) {
		expect:
			service.extractPrice(fragment) == expected
		where:
			fragment        | expected
			'1$'            | BigDecimal.ONE
			'10$'           | BigDecimal.TEN
			'US$16.50'      | new BigDecimal('16.50')
			'10 EUR'        | BigDecimal.TEN
			'10.0 EUR'      | BigDecimal.TEN
			'10.00 EUR'     | BigDecimal.TEN
			'10,00 EUR'     | BigDecimal.TEN
			'€3.50'         | new BigDecimal('3.50')
			'10 руб 16 коп' | BigDecimal.TEN
			'RUB 1,218.79'  | new BigDecimal('1218.79')
	}
	
	@Unroll
	def 'extractPrice() should ignore a space in "#fragment"'(String fragment, BigDecimal result) {
		expect:
			service.extractPrice(fragment) == result
		where:
			fragment         || result
			'10 800,00 руб.' || new BigDecimal('10800')
			'1 200'          || new BigDecimal('1200')
			'1 000 000'      || new BigDecimal('1000000')
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
	
	@Unroll
	def 'extractCurrency() should return null for "#fragment"'(String fragment) {
		expect:
			service.extractCurrency(fragment) == null
		where:
			fragment | _
			'CAD'    | _
			'труб'   | _
			'агрн'   | _
			'грном'  | _
	}
	
	def 'extractCurrency() should extract exactly specified currency'() {
		given:
			String validCurrency = Random.currency()
		when:
			String result = service.extractCurrency(validCurrency)
		then:
			result == validCurrency
	}
	
	@Unroll
	def 'extractCurrency() should extract RUB currency from "#fragment"'(String fragment) {
		expect:
			service.extractCurrency(fragment) == 'RUB'
		where:
			fragment    | _
			'1 рубль'   | _
			'10 рублей' | _
			'100 руб'   | _
			'200руб'    | _
			'660 руб.'  | _
			'800 р.'    | _
			'RUB 1218'  | _
	}
	
	@Unroll
	def 'extractCurrency() should extract BYN currency from "#fragment"'(String fragment) {
		expect:
			service.extractCurrency(fragment) == 'BYN'
		where:
			fragment         | _
			'8,90 бел. руб.' | _
	}
	
	@Unroll
	def 'extractCurrency() should extract UAH currency from "#fragment"'(String fragment) {
		expect:
			service.extractCurrency(fragment) == 'UAH'
		where:
			fragment     | _
			'грн'        | _
			'135.74 грн' | _
	}
	
	@Unroll
	def 'extractCurrency() should extract USD currency from "#fragment"'(String fragment) {
		expect:
			service.extractCurrency(fragment) == 'USD'
		where:
			fragment   | _
			'1,36$'    | _
			'US$16.50' | _
	}
	
	@Unroll
	def 'extractCurrency() should extract EUR currency from "#fragment"'(String fragment) {
		expect:
			service.extractCurrency(fragment) == 'EUR'
		where:
			fragment | _
			'€3.50'  | _
	}
	
}
