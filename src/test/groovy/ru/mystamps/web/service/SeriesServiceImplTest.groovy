/**
 * Copyright (C) 2009-2017 Slava Semushin <slava.semushin@gmail.com>
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

import org.springframework.web.multipart.MultipartFile

import spock.lang.Specification
import spock.lang.Unroll

import ru.mystamps.web.dao.SeriesDao
import ru.mystamps.web.dao.dto.AddSeriesDbDto
import ru.mystamps.web.dao.dto.Currency
import ru.mystamps.web.dao.dto.SeriesFullInfoDto
import ru.mystamps.web.model.AddImageForm
import ru.mystamps.web.model.AddSeriesForm
import ru.mystamps.web.dao.dto.LinkEntityDto
import ru.mystamps.web.dao.dto.PurchaseAndSaleDto
import ru.mystamps.web.dao.dto.SeriesInfoDto
import ru.mystamps.web.dao.dto.SitemapInfoDto
import ru.mystamps.web.service.dto.SeriesDto
import ru.mystamps.web.tests.DateUtils

@SuppressWarnings(['ClassJavadoc', 'MethodName', 'NoDef', 'NoTabCharacter', 'TrailingWhitespace'])
class SeriesServiceImplTest extends Specification {
	private static final BigDecimal ANY_PRICE = new BigDecimal('17')
	private static final Integer ANY_IMAGE_ID = 18
	
	private final ImageService imageService = Mock()
	private final SeriesDao seriesDao = Mock()
	private final StampsCatalogService michelCatalogService = Mock()
	private final StampsCatalogService scottCatalogService = Mock()
	private final StampsCatalogService yvertCatalogService = Mock()
	private final StampsCatalogService gibbonsCatalogService = Mock()
	private final MultipartFile multipartFile = Mock()
	
	private SeriesService service
	private AddSeriesForm form
	private AddImageForm imageForm
	private Integer userId
	
	def setup() {
		form = new AddSeriesForm()
		form.setQuantity(2)
		form.setPerforated(false)
		form.setCategory(TestObjects.createLinkEntityDto())
		
		imageForm = new AddImageForm()
		
		userId = TestObjects.TEST_USER_ID
		
		imageService.save(_) >> ANY_IMAGE_ID
		
		service = new SeriesServiceImpl(
			seriesDao,
			imageService,
			michelCatalogService,
			scottCatalogService,
			yvertCatalogService,
			gibbonsCatalogService
		)
	}
	
	//
	// Tests for add()
	//
	
	def "add() should throw exception argument is null"() {
		when:
			service.add(null, userId, false)
		then:
			thrown IllegalArgumentException
	}
	
	def "add() should throw exception if quantity is null"() {
		given:
			form.setQuantity(null)
		when:
			service.add(form, userId, false)
		then:
			thrown IllegalArgumentException
	}
	
	def "add() should throw exception if perforated is null"() {
		given:
			form.setPerforated(null)
		when:
			service.add(form, userId, false)
		then:
			thrown IllegalArgumentException
	}
	
	def "add() should throw exception if category is null"() {
		given:
			form.setCategory(null)
		when:
			service.add(form, userId, false)
		then:
			thrown IllegalArgumentException
	}
	
	def "add() should throw exception if category id is null"() {
		given:
			form.setCategory(new LinkEntityDto(null, 'test', 'Test'))
		when:
			service.add(form, userId, false)
		then:
			thrown IllegalArgumentException
	}
	
	def "add() should throw exception when user is null"() {
		when:
			service.add(form, null, false)
		then:
			thrown IllegalArgumentException
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "add() should load and pass country to series dao if country present"() {
		given:
			LinkEntityDto country = TestObjects.createLinkEntityDto()
		and:
			Integer expectedCountryId = country.id
		and:
			form.setCountry(country)
		when:
			service.add(form, userId, false)
		then:
			1 * seriesDao.add({ AddSeriesDbDto series ->
				assert series?.countryId == expectedCountryId
				return true
			}) >> 123
	}
	
	@Unroll
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "add() should pass date of release (#expectedDay/#expectedMonth/#expectedYear) when it's #day/#month/#year"(
		Integer day, Integer month, Integer year,
		Integer expectedDay, Integer expectedMonth, Integer expectedYear) {
		
		given:
			form.setDay(day)
			form.setMonth(month)
			form.setYear(year)
		when:
			service.add(form, userId, false)
		then:
			1 * seriesDao.add({ AddSeriesDbDto series ->
				assert series?.releaseDay == expectedDay
				assert series?.releaseMonth == expectedMonth
				assert series?.releaseYear == expectedYear
				return true
			}) >> 123
		where:
			day  | month | year || expectedDay | expectedMonth | expectedYear
			null | null  | null || null        | null          | null
			null | null  | 1996 || null        | null          | 1996
			null | 6     | 1996 || null        | 6             | 1996
			7    | 6     | 1996 ||  7          | 6             | 1996
			7    | 6     | null || null        | null          | null
			7    | null  | null || null        | null          | null
			7    | null  | 1996 || null        | null          | 1996
			null | 6     | null || null        | null          | null
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "add() should pass category to series dao"() {
		given:
			LinkEntityDto category = TestObjects.createLinkEntityDto()
		and:
			Integer expectedCategoryId = category.id
		and:
			form.setCategory(category)
		when:
			service.add(form, userId, false)
		then:
			1 * seriesDao.add({ AddSeriesDbDto series ->
				assert series?.categoryId == expectedCategoryId
				return true
			}) >> 123
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "add() should pass quantity to series dao"() {
		given:
			Integer expectedQuantity = 3
			form.setQuantity(expectedQuantity)
		when:
			service.add(form, userId, false)
		then:
			1 * seriesDao.add({ AddSeriesDbDto series ->
				assert series?.quantity == expectedQuantity
				return true
			}) >> 123
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "add() should pass perforated to series dao"() {
		given:
			Boolean expectedResult = true
			form.setPerforated(expectedResult)
		when:
			service.add(form, userId, false)
		then:
			1 * seriesDao.add({ AddSeriesDbDto series ->
				assert series?.perforated == expectedResult
				return true
			}) >> 123
	}
	
	@Unroll
	@SuppressWarnings([
		'ClosureAsLastMethodParameter',
		'UnnecessaryReturnKeyword',
		'LineLength',
		/* false positive: */ 'UnnecessaryBooleanExpression',
	])
	def "add() should pass michel price and currency to series dao"(BigDecimal price, BigDecimal expectedPrice, String expectedCurrency) {
		given:
			form.setMichelPrice(price)
		when:
			service.add(form, userId, false)
		then:
			1 * seriesDao.add({ AddSeriesDbDto series ->
				assert series?.michelPrice == expectedPrice
				assert series?.michelCurrency == expectedCurrency
				return true
			}) >> 123
		where:
			price     || expectedPrice | expectedCurrency
			ANY_PRICE || ANY_PRICE     | Currency.EUR.toString()
			null      || null          | null
	}
	
	@Unroll
	@SuppressWarnings([
		'ClosureAsLastMethodParameter',
		'LineLength',
		'UnnecessaryReturnKeyword',
		/* false positive: */ 'UnnecessaryBooleanExpression',
	])
	def "add() should pass scott price and currency to series dao"(BigDecimal price, BigDecimal expectedPrice, String expectedCurrency) {
		given:
			form.setScottPrice(price)
		when:
			service.add(form, userId, false)
		then:
			1 * seriesDao.add({ AddSeriesDbDto series ->
				assert series?.scottPrice == expectedPrice
				assert series?.scottCurrency == expectedCurrency
				return true
			}) >> 123
		where:
			price     || expectedPrice | expectedCurrency
			ANY_PRICE || ANY_PRICE     | Currency.USD.toString()
			null      || null          | null
	}
	
	@Unroll
	@SuppressWarnings([
		'ClosureAsLastMethodParameter',
		'LineLength',
		'UnnecessaryReturnKeyword',
		/* false positive: */ 'UnnecessaryBooleanExpression',
	])
	def "add() should pass yvert price and currency to series dao"(BigDecimal price, BigDecimal expectedPrice, String expectedCurrency) {
		given:
			form.setYvertPrice(price)
		when:
			service.add(form, userId, false)
		then:
			1 * seriesDao.add({ AddSeriesDbDto series ->
				assert series?.yvertPrice == expectedPrice
				assert series?.yvertCurrency == expectedCurrency
				return true
			}) >> 123
		where:
			price     || expectedPrice | expectedCurrency
			ANY_PRICE || ANY_PRICE     | Currency.EUR.toString()
			null      || null          | null
	}
	
	@Unroll
	@SuppressWarnings([
		'ClosureAsLastMethodParameter',
		'LineLength',
		'UnnecessaryReturnKeyword',
		/* false positive: */ 'UnnecessaryBooleanExpression',
	])
	def "add() should pass gibbons price and currency to series dao"(BigDecimal price, BigDecimal expectedPrice, String expectedCurrency) {
		given:
			form.setGibbonsPrice(price)
		when:
			service.add(form, userId, false)
		then:
			1 * seriesDao.add({ AddSeriesDbDto series ->
				assert series?.gibbonsPrice == expectedPrice
				assert series?.gibbonsCurrency == expectedCurrency
				return true
			}) >> 123
		where:
			price     || expectedPrice | expectedCurrency
			ANY_PRICE || ANY_PRICE     | Currency.GBP.toString()
			null      || null          | null
	}
	
	def "add() should throw exception if comment is empty"() {
		given:
			form.setComment('  ')
		when:
			service.add(form, userId, true)
		then:
			thrown IllegalArgumentException
	}
	
	@Unroll
	@SuppressWarnings([
		'ClosureAsLastMethodParameter',
		'LineLength',
		'UnnecessaryReturnKeyword',
		/* false positive: */ 'UnnecessaryBooleanExpression',
	])
	def "add() should pass '#expectedComment' as comment to series dao if user can add comment is #canAddComment"(boolean canAddComment, String comment, String expectedComment) {
		given:
			form.setComment(comment)
		when:
			service.add(form, userId, canAddComment)
		then:
			1 * seriesDao.add({ AddSeriesDbDto series ->
				assert series?.comment == expectedComment
				return true
			}) >> 123
		where:
			canAddComment | comment     || expectedComment
			false         | null        || null
			false         | 'test'      || null
			true          | null        || null
			true          | 'Some text' || 'Some text'
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "add() should assign created/updated at/by to current date/user"() {
		given:
			Integer expectedUserId = 789
		when:
			service.add(form, expectedUserId, false)
		then:
			1 * seriesDao.add({ AddSeriesDbDto series ->
				assert series?.createdBy == expectedUserId
				assert series?.updatedBy == expectedUserId
				assert DateUtils.roughlyEqual(series?.createdAt, new Date())
				assert DateUtils.roughlyEqual(series?.updatedAt, new Date())
				return true
			}) >> 123
	}
	
	def "add() should pass dto to series dao and return its result"() {
		given:
			Integer expected = 456
		when:
			Integer actual = service.add(form, userId, false)
		then:
			1 * seriesDao.add(_ as AddSeriesDbDto) >> expected
		and:
			actual == expected
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "add() should pass image to image service"() {
		given:
			form.setImage(multipartFile)
		when:
			service.add(form, userId, false)
		then:
			1 * imageService.save({ MultipartFile passedFile ->
				assert passedFile == multipartFile
				return true
			}) >> ANY_IMAGE_ID
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "add() should add image to the series"() {
		given:
			Integer expectedSeriesId = 123
		and:
			seriesDao.add(_ as AddSeriesDbDto) >> expectedSeriesId
		and:
			Integer expectedImageId = 456
		when:
			service.add(form, userId, false)
		then:
			// FIXME: why we can't use _ as MultipartFile here?
			imageService.save(_) >> expectedImageId
		and:
			1 * imageService.addToSeries({ Integer seriesId ->
				assert seriesId == expectedSeriesId
				return true
			}, { Integer imageId ->
				assert imageId == expectedImageId
				return true
			})
	}
	
	@Unroll
	def "add() should not call services if michel numbers is '#numbers'"(String numbers) {
		given:
			form.setMichelNumbers(numbers)
		when:
			service.add(form, userId, false)
		then:
			0 * michelCatalogService.add(_ as Set<String>)
		and:
			0 * michelCatalogService.addToSeries(_ as Integer, _ as Set<String>)
		where:
			numbers | _
			''      | _
			null    | _
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "add() should add michel numbers to series"() {
		given:
			Set<String> expectedNumbers = [ '1', '2' ] as Set
		and:
			form.setMichelNumbers(expectedNumbers.join(','))
		and:
			Integer expectedSeriesId = 456
		and:
			seriesDao.add(_ as AddSeriesDbDto) >> expectedSeriesId
		when:
			service.add(form, userId, false)
		then:
			1 * michelCatalogService.add({ Set<String> numbers ->
				assert numbers == expectedNumbers
				return true
			})
		and:
			1 * michelCatalogService.addToSeries({ Integer seriesId ->
				assert seriesId == expectedSeriesId
				return true
			}, { Set<String> numbers ->
				assert numbers == expectedNumbers
				return true
			})
	}
	
	@Unroll
	def "add() should not call services if scott numbers is '#numbers'"(String numbers) {
		given:
			form.setScottNumbers(numbers)
		when:
			service.add(form, userId, false)
		then:
			0 * scottCatalogService.add(_ as Set<String>)
		and:
			0 * scottCatalogService.addToSeries(_ as Integer, _ as Set<String>)
		where:
			numbers | _
			''      | _
			null    | _
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "add() should add scott numbers to series"() {
		given:
			Set<String> expectedNumbers = [ '1', '2' ] as Set
		and:
			form.setScottNumbers(expectedNumbers.join(','))
		and:
			Integer expectedSeriesId = 456
		and:
			seriesDao.add(_ as AddSeriesDbDto) >> expectedSeriesId
		when:
			service.add(form, userId, false)
		then:
			1 * scottCatalogService.add({ Set<String> numbers ->
				assert numbers == expectedNumbers
				return true
			})
		and:
			1 * scottCatalogService.addToSeries({ Integer seriesId ->
				assert seriesId == expectedSeriesId
				return true
			}, { Set<String> numbers ->
				assert numbers == expectedNumbers
				return true
			})
	}
	
	@Unroll
	def "add() should not call services if yvert numbers is '#numbers'"(String numbers) {
		given:
			form.setYvertNumbers(numbers)
		when:
			service.add(form, userId, false)
		then:
			0 * yvertCatalogService.add(_ as Set<String>)
		and:
			0 * yvertCatalogService.addToSeries(_ as Integer, _ as Set<String>)
		where:
			numbers | _
			''      | _
			null    | _
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "add() should add yvert numbers to series"() {
		given:
			Set<String> expectedNumbers = [ '1', '2' ] as Set
		and:
			form.setYvertNumbers(expectedNumbers.join(','))
		and:
			Integer expectedSeriesId = 456
		and:
			seriesDao.add(_ as AddSeriesDbDto) >> expectedSeriesId
		when:
			service.add(form, userId, false)
		then:
			1 * yvertCatalogService.add({ Set<String> numbers ->
				assert numbers == expectedNumbers
				return true
			})
		and:
			1 * yvertCatalogService.addToSeries({ Integer seriesId ->
				assert seriesId == expectedSeriesId
				return true
			}, { Set<String> numbers ->
				assert numbers == expectedNumbers
				return true
			})
	}
	
	@Unroll
	def "add() should not call services if gibbons numbers is '#numbers'"(String numbers) {
		given:
			form.setGibbonsNumbers(numbers)
		when:
			service.add(form, userId, false)
		then:
			0 * gibbonsCatalogService.add(_ as Set<String>)
		and:
			0 * gibbonsCatalogService.addToSeries(_ as Integer, _ as Set<String>)
		where:
			numbers | _
			''      | _
			null    | _
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "add() should add gibbons numbers to series"() {
		given:
			Set<String> expectedNumbers = [ '1', '2' ] as Set
		and:
			form.setGibbonsNumbers(expectedNumbers.join(','))
		and:
			Integer expectedSeriesId = 456
		and:
			seriesDao.add(_ as AddSeriesDbDto) >> expectedSeriesId
		when:
			service.add(form, userId, false)
		then:
			1 * gibbonsCatalogService.add({ Set<String> numbers ->
				assert numbers == expectedNumbers
				return true
			})
		and:
			1 * gibbonsCatalogService.addToSeries({ Integer seriesId ->
				assert seriesId == expectedSeriesId
				return true
			}, { Set<String> numbers ->
				assert numbers == expectedNumbers
				return true
			})
	}

	//
	// Tests for addImageToSeries()
	//
	
	def "addImageToSeries() should throw exception when dto is null"() {
		when:
			service.addImageToSeries(null, 111, 222)
		then:
			thrown IllegalArgumentException
	}
	
	def "addImageToSeries() should throw exception when series id is null"() {
		when:
			service.addImageToSeries(imageForm, null, 222)
		then:
			thrown IllegalArgumentException
	}
	
	def "addImageToSeries() should throw exception when user id is null"() {
		when:
			service.addImageToSeries(imageForm, 111, null)
		then:
			thrown IllegalArgumentException
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "addImageToSeries() should save image"() {
		given:
			imageForm.setImage(multipartFile)
		when:
			service.addImageToSeries(imageForm, 111, 222)
		then:
			1 * imageService.save({ MultipartFile passedFile ->
				assert passedFile == multipartFile
				return true
			}) >> ANY_IMAGE_ID
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "addImageToSeries() should add image to series"() {
		given:
			Integer expectedSeriesId = 123
			Integer expectedUserId = 321
			Integer expectedImageId = 456
		when:
			service.addImageToSeries(imageForm, expectedSeriesId, expectedUserId)
		then:
			imageService.save(_) >> expectedImageId
		and:
			1 * imageService.addToSeries({ Integer seriesId ->
				assert seriesId == expectedSeriesId
				return true
			}, { Integer imageId ->
				assert imageId == expectedImageId
				return true
			})
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "addImageToSeries() should mark series as modified"() {
		given:
			Integer expectedSeriesId = 123
			Integer expectedUserId = 321
		when:
			service.addImageToSeries(imageForm, expectedSeriesId, expectedUserId)
		then:
			1 * seriesDao.markAsModified({ Integer seriesId ->
				assert seriesId == expectedSeriesId
				return true
			}, { Date updatedAt ->
				assert DateUtils.roughlyEqual(updatedAt, new Date())
				return true
			}, { Integer userId ->
				assert userId == expectedUserId
				return true
			})
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
			1 * seriesDao.countAll() >> expectedResult
		and:
			result == expectedResult
	}
	
	//
	// Tests for countAllStamps()
	//
	
	def "countAllStamps() should call dao and returns result"() {
		given:
			long expectedResult = 30
		when:
			long result = service.countAllStamps()
		then:
			1 * seriesDao.countAllStamps() >> expectedResult
		and:
			result == expectedResult
	}
	
	//
	// Tests for countSeriesOf()
	//
	
	def "countSeriesOf() should throw exception when argument is null"() {
		when:
			service.countSeriesOf(null)
		then:
			thrown IllegalArgumentException
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "countSeriesOf() should pass argument to dao"() {
		given:
			Integer expectedCollectionId = 7
		when:
			service.countSeriesOf(expectedCollectionId)
		then:
			1 * seriesDao.countSeriesOfCollection({ Integer collectionId ->
				assert expectedCollectionId == collectionId
				return true
			}) >> 0L
	}
	
	//
	// Tests for countStampsOf()
	//
	
	def "countStampsOf() should throw exception when argument is null"() {
		when:
			service.countStampsOf(null)
		then:
			thrown IllegalArgumentException
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "countStampsOf() should pass arguments to dao"() {
		given:
			Integer expectedCollectionId = 8
		when:
			service.countStampsOf(expectedCollectionId)
		then:
			1 * seriesDao.countStampsOfCollection({ Integer collectionId ->
				assert expectedCollectionId == collectionId
				return true
			}) >> 0L
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
			long expectedResult = 35
		when:
			long result = service.countAddedSince(expectedDate)
		then:
			1 * seriesDao.countAddedSince({ Date date ->
				assert date == expectedDate
				return true
			}) >> expectedResult
		and:
			result == expectedResult
	}
	
	//
	// Tests for countUpdatedSince()
	//
	
	def "countUpdatedSince() should throw exception when date is null"() {
		when:
			service.countUpdatedSince(null)
		then:
			thrown IllegalArgumentException
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "countUpdatedSince() should invoke dao, pass argument and return result from dao"() {
		given:
			Date expectedDate = new Date()
		and:
			long expectedResult = 45
		when:
			long result = service.countUpdatedSince(expectedDate)
		then:
			1 * seriesDao.countUpdatedSince({ Date date ->
				assert date == expectedDate
				return true
			}) >> expectedResult
		and:
			result == expectedResult
	}
	
	//
	// Tests for isSeriesExist()
	//
	
	def "isSeriesExist() should throw exception when series id is null"() {
		when:
			service.isSeriesExist(null)
		then:
			thrown IllegalArgumentException
	}
	
	@Unroll
	@SuppressWarnings([
		'ClosureAsLastMethodParameter',
		'UnnecessaryReturnKeyword',
		'LineLength',
		/* false positive: */ 'UnnecessaryBooleanExpression',
	])
	def "isSeriesExist() should invoke dao, pass argument and return result from dao"(Integer daoReturnValue, boolean expectedResult) {
		given:
			Integer expectedSeriesId = 13
		when:
			boolean result = service.isSeriesExist(expectedSeriesId)
		then:
			1 * seriesDao.countSeriesById({ Integer seriesId ->
				assert seriesId == expectedSeriesId
				return true
			}) >> daoReturnValue
		and:
			result == expectedResult
		where:
			daoReturnValue || expectedResult
			0              || false
			2              || true
	}
	
	//
	// Tests for findFullInfoById()
	//
	
	def "findFullInfoById() should throw exception when series id is null"() {
		when:
			service.findFullInfoById(null, null)
		then:
			thrown(IllegalArgumentException)
	}
	
	def "findFullInfoById() should return null when series not found"() {
		when:
			SeriesDto result = service.findFullInfoById(10, 'de')
		then:
			1 * seriesDao.findByIdAsSeriesFullInfo(_ as Integer, _ as String)
		and:
			0 * michelCatalogService.findBySeriesId(_ as Integer)
			0 * scottCatalogService.findBySeriesId(_ as Integer)
			0 * yvertCatalogService.findBySeriesId(_ as Integer)
			0 * gibbonsCatalogService.findBySeriesId(_ as Integer)
			0 * imageService.findBySeriesId(_ as Integer)
		and:
			result == null
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword', 'UnnecessaryObjectReferences'])
	def "findFullInfoById() should return info about series"() {
		given:
			Integer expectedSeriesId = 20
			String expectedLang = 'kz'
			SeriesFullInfoDto expectedInfo = TestObjects.createSeriesFullInfoDto()
			List<String> expectedMichelNumbers  = [ '1', '2' ]
			List<String> expectedScottNumbers   = [ '3', '4' ]
			List<String> expectedYvertNumbers   = [ '5', '6' ]
			List<String> expectedGibbonsNumbers = [ '7', '8' ]
			List<Integer> expectedImageIds      = [ 9, 10 ]
		when:
			SeriesDto result = service.findFullInfoById(expectedSeriesId, expectedLang)
		then:
			1 * seriesDao.findByIdAsSeriesFullInfo({ Integer seriesId ->
				assert seriesId == expectedSeriesId
				return true
			}, { String lang ->
				assert lang == expectedLang
				return true
			}) >> expectedInfo
		and:
			1 * michelCatalogService.findBySeriesId({ Integer seriesId ->
				assert seriesId == expectedSeriesId
				return true
			}) >> expectedMichelNumbers
		and:
			1 * scottCatalogService.findBySeriesId({ Integer seriesId ->
				assert seriesId == expectedSeriesId
				return true
			}) >> expectedScottNumbers
		and:
			1 * yvertCatalogService.findBySeriesId({ Integer seriesId ->
				assert seriesId == expectedSeriesId
				return true
			}) >> expectedYvertNumbers
		and:
			1 * gibbonsCatalogService.findBySeriesId({ Integer seriesId ->
				assert seriesId == expectedSeriesId
				return true
			}) >> expectedGibbonsNumbers
		and:
			1 * imageService.findBySeriesId({ Integer seriesId ->
				assert seriesId == expectedSeriesId
				return true
			}) >> expectedImageIds
		and:
			result != null
			result.id           == expectedInfo.id
			result.category     == expectedInfo.category
			result.country      == expectedInfo.country
			
			result.releaseDay   == expectedInfo.releaseDay
			result.releaseMonth == expectedInfo.releaseMonth
			result.releaseYear  == expectedInfo.releaseYear
			
			result.quantity     == expectedInfo.quantity
			result.perforated   == expectedInfo.perforated
			result.comment      == expectedInfo.comment
			result.createdBy    == expectedInfo.createdBy
			
			result.imageIds     == expectedImageIds

			result.michel?.numbers   == expectedMichelNumbers
			result.michel?.price     == expectedInfo.michelPrice
			result.michel?.currency  == Currency.valueOf(expectedInfo.michelCurrency)
			
			result.scott?.numbers    == expectedScottNumbers
			result.scott?.price      == expectedInfo.scottPrice
			result.scott?.currency   == Currency.valueOf(expectedInfo.scottCurrency)
			
			result.yvert?.numbers    == expectedYvertNumbers
			result.yvert?.price      == expectedInfo.yvertPrice
			result.yvert?.currency   == Currency.valueOf(expectedInfo.yvertCurrency)
			
			result.gibbons?.numbers  == expectedGibbonsNumbers
			result.gibbons?.price    == expectedInfo.gibbonsPrice
			result.gibbons?.currency == Currency.valueOf(expectedInfo.gibbonsCurrency)
	}
	
	//
	// Tests for findByMichelNumber()
	//
	
	@Unroll
	def "findByMichelNumber() should throw exception for invalid argument '#catalogNumber'"(String catalogNumber) {
		when:
			service.findByMichelNumber(catalogNumber, 'en')
		then:
			thrown IllegalArgumentException
		where:
			catalogNumber | _
			null          | _
			''            | _
			' '           | _
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "findByMichelNumber() should find series ids"() {
		given:
			String expectedMichelNumber = '5'
		when:
			service.findByMichelNumber(expectedMichelNumber, 'en')
		then:
			1 * seriesDao.findSeriesIdsByMichelNumberCode({ String michelNumber ->
				assert michelNumber == expectedMichelNumber
				return true
			}) >> []
	}
	
	def "findByMichelNumber() shouldn't try to find series info if there are no series"() {
		given:
			seriesDao.findSeriesIdsByMichelNumberCode(_ as String) >> []
		when:
			List<SeriesInfoDto> result = service.findByMichelNumber('5', 'en')
		then:
			0 * seriesDao.findByIdsAsSeriesInfo(_ as List, _ as String)
		and:
			result.empty
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "findByMichelNumber() should find and return series info"() {
		given:
			String expectedLang = 'en'
		and:
			List<Integer> expectedSeriesIds = [ 1 ]
		and:
			seriesDao.findSeriesIdsByMichelNumberCode(_ as String) >> expectedSeriesIds
		and:
			List<SeriesInfoDto> expectedResult = []
		when:
			List<SeriesInfoDto> result = service.findByMichelNumber('5', expectedLang)
		then:
			1 * seriesDao.findByIdsAsSeriesInfo({ List<Integer> seriesIds ->
				assert seriesIds == expectedSeriesIds
				return true
			}, { String lang ->
				assert lang == expectedLang
				return true
			}) >> expectedResult
		and:
			result == expectedResult
	}
	
	//
	// Tests for findByScottNumber()
	//
	
	@Unroll
	def "findByScottNumber() should throw exception for invalid argument '#catalogNumber'"(String catalogNumber) {
		when:
			service.findByScottNumber(catalogNumber, 'en')
		then:
			thrown IllegalArgumentException
		where:
			catalogNumber | _
			null          | _
			''            | _
			' '           | _
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "findByScottNumber() should find series ids"() {
		given:
			String expectedScottNumber = '5'
		when:
			service.findByScottNumber(expectedScottNumber, 'en')
		then:
			1 * seriesDao.findSeriesIdsByScottNumberCode({ String scottNumber ->
				assert scottNumber == expectedScottNumber
				return true
			}) >> []
	}
	
	def "findByScottNumber() shouldn't try to find series info if there are no series"() {
		given:
			seriesDao.findSeriesIdsByScottNumberCode(_ as String) >> []
		when:
			List<SeriesInfoDto> result = service.findByScottNumber('5', 'en')
		then:
			0 * seriesDao.findByIdsAsSeriesInfo(_ as List, _ as String)
		and:
			result.empty
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "findByScottNumber() should find and return series info"() {
		given:
			String expectedLang = 'en'
		and:
			List<Integer> expectedSeriesIds = [ 1 ]
		and:
			seriesDao.findSeriesIdsByScottNumberCode(_ as String) >> expectedSeriesIds
		and:
			List<SeriesInfoDto> expectedResult = []
		when:
			List<SeriesInfoDto> result = service.findByScottNumber('5', expectedLang)
		then:
			1 * seriesDao.findByIdsAsSeriesInfo({ List<Integer> seriesIds ->
				assert seriesIds == expectedSeriesIds
				return true
			}, { String lang ->
				assert lang == expectedLang
				return true
			}) >> expectedResult
		and:
			result == expectedResult
	}
	
	//
	// Tests for findByYvertNumber()
	//
	
	@Unroll
	def "findByYvertNumber() should throw exception for invalid argument '#catalogNumber'"(String catalogNumber) {
		when:
			service.findByYvertNumber(catalogNumber, 'en')
		then:
			thrown IllegalArgumentException
		where:
			catalogNumber | _
			null          | _
			''            | _
			' '           | _
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "findByYvertNumber() should find series ids"() {
		given:
			String expectedYvertNumber = '5'
		when:
			service.findByYvertNumber(expectedYvertNumber, 'en')
		then:
			1 * seriesDao.findSeriesIdsByYvertNumberCode({ String yvertNumber ->
				assert yvertNumber == expectedYvertNumber
				return true
			}) >> []
	}
	
	def "findByYvertNumber() shouldn't try to find series info if there are no series"() {
		given:
			seriesDao.findSeriesIdsByYvertNumberCode(_ as String) >> []
		when:
			List<SeriesInfoDto> result = service.findByYvertNumber('5', 'en')
		then:
			0 * seriesDao.findByIdsAsSeriesInfo(_ as List, _ as String)
		and:
			result.empty
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "findByYvertNumber() should find and return series info"() {
		given:
			String expectedLang = 'en'
		and:
			List<Integer> expectedSeriesIds = [ 1 ]
		and:
			seriesDao.findSeriesIdsByYvertNumberCode(_ as String) >> expectedSeriesIds
		and:
			List<SeriesInfoDto> expectedResult = []
		when:
			List<SeriesInfoDto> result = service.findByYvertNumber('5', expectedLang)
		then:
			1 * seriesDao.findByIdsAsSeriesInfo({ List<Integer> seriesIds ->
				assert seriesIds == expectedSeriesIds
				return true
			}, { String lang ->
				assert lang == expectedLang
				return true
			}) >> expectedResult
		and:
			result == expectedResult
	}
	
	//
	// Tests for findByGibbonsNumber()
	//
	
	@Unroll
	def "findByGibbonsNumber() should throw exception for invalid argument '#catalogNumber'"(String catalogNumber) {
		when:
			service.findByGibbonsNumber(catalogNumber, 'en')
		then:
			thrown IllegalArgumentException
		where:
			catalogNumber | _
			null          | _
			''            | _
			' '           | _
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "findByGibbonsNumber() should find series ids"() {
		given:
			String expectedGibbonsNumber = '5'
		when:
			service.findByGibbonsNumber(expectedGibbonsNumber, 'en')
		then:
			1 * seriesDao.findSeriesIdsByGibbonsNumberCode({ String gibbonsNumber ->
				assert gibbonsNumber == expectedGibbonsNumber
				return true
			}) >> []
	}
	
	def "findByGibbonsNumber() shouldn't try to find series info if there are no series"() {
		given:
			seriesDao.findSeriesIdsByGibbonsNumberCode(_ as String) >> []
		when:
			List<SeriesInfoDto> result = service.findByGibbonsNumber('5', 'en')
		then:
			0 * seriesDao.findByIdsAsSeriesInfo(_ as List, _ as String)
		and:
			result.empty
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "findByGibbonsNumber() should find and return series info"() {
		given:
			String expectedLang = 'en'
		and:
			List<Integer> expectedSeriesIds = [ 1 ]
		and:
			seriesDao.findSeriesIdsByGibbonsNumberCode(_ as String) >> expectedSeriesIds
		and:
			List<SeriesInfoDto> expectedResult = []
		when:
			List<SeriesInfoDto> result = service.findByGibbonsNumber('5', expectedLang)
		then:
			1 * seriesDao.findByIdsAsSeriesInfo({ List<Integer> seriesIds ->
				assert seriesIds == expectedSeriesIds
				return true
			}, { String lang ->
				assert lang == expectedLang
				return true
			}) >> expectedResult
		and:
			result == expectedResult
	}
	
	//
	// Tests for findByCategorySlug()
	//
	
	def "findByCategorySlug() should throw exception if category slug is null"() {
		when:
			service.findByCategorySlug(null, 'any')
		then:
			thrown IllegalArgumentException
	}
	
	def "findByCategorySlug() should call dao and return result"() {
		given:
			SeriesInfoDto series = TestObjects.createSeriesInfoDto()
		and:
			List<SeriesInfoDto> expectedResult = [ series ]
		and:
			seriesDao.findByCategorySlugAsSeriesInfo(_ as String, _ as String) >> expectedResult
		when:
			List<SeriesInfoDto> result = service.findByCategorySlug('iceland', 'any')
		then:
			result == expectedResult
	}
	
	//
	// Tests for findByCountrySlug()
	//
	
	def "findByCountrySlug() should throw exception if country slug is null"() {
		when:
			service.findByCountrySlug(null, 'any')
		then:
			thrown IllegalArgumentException
	}
	
	def "findByCountrySlug() should call dao and return result"() {
		given:
			SeriesInfoDto series = TestObjects.createSeriesInfoDto()
		and:
			List<SeriesInfoDto> expectedResult = [ series ]
		and:
			seriesDao.findByCountrySlugAsSeriesInfo(_ as String, _ as String) >> expectedResult
		when:
			List<SeriesInfoDto> result = service.findByCountrySlug('germany', 'any')
		then:
			result == expectedResult
	}
	
	//
	// Tests for findByCollectionId()
	//
	
	def "findByCollectionId() should throw exception when collection id is null"() {
		when:
			service.findByCollectionId(null, 'whatever')
		then:
			thrown IllegalArgumentException
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "findByCollectionId() should pass arguments to dao"() {
		given:
			Integer expectedCollectionId = 16
		and:
			String expectedLang = 'expected'
		when:
			service.findByCollectionId(expectedCollectionId, expectedLang)
		then:
			1 * seriesDao.findByCollectionIdAsSeriesInfo(
				{ Integer collectionId ->
					assert expectedCollectionId == collectionId
					return true
				},
				{ String lang ->
					assert expectedLang == lang
					return true
				}
			) >> []
	}
	
	//
	// Tests for findRecentlyAdded
	//
	
	@Unroll
	def "findRecentlyAdded should throw exception when quantity is #quantity"(Integer quantity) {
		when:
			service.findRecentlyAdded(quantity, null)
		then:
			thrown IllegalArgumentException
		where:
			quantity | _
			-1       | _
			0        | _
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "findRecentlyAdded should pass arguments to dao"() {
		given:
			int expectedQuantity = 3
		and:
			String expectedLang = 'expected'
		when:
			service.findRecentlyAdded(expectedQuantity, expectedLang)
		then:
			1 * seriesDao.findLastAdded(
				{ int quantity ->
					assert expectedQuantity == quantity
					return true
				},
				{ String lang ->
					assert expectedLang == lang
					return true
				}
			) >> []
	}
	
	//
	// Tests for findAllForSitemap()
	//
	
	def "findAllForSitemap() should call dao and returns result"() {
		given:
			List<SitemapInfoDto> expectedResult =
				Collections.singletonList(TestObjects.createSitemapInfoDto())
		when:
			List<SitemapInfoDto> result = service.findAllForSitemap()
		then:
			1 * seriesDao.findAllForSitemap() >> expectedResult
		and:
			result == expectedResult
	}
	
	//
	// Tests for findPurchasesAndSales()
	//
	
	def "findPurchasesAndSales() should throw exception when series id is null"() {
		when:
			service.findPurchasesAndSales(null)
		then:
			thrown IllegalArgumentException
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "findPurchasesAndSales() should invoke dao, pass argument and return result from dao"() {
		given:
			Integer expectedSeriesId = 88
		and:
			List<PurchaseAndSaleDto> expectedResult = [ TestObjects.createPurchaseAndSaleDto() ]
		when:
			List<PurchaseAndSaleDto> result = service.findPurchasesAndSales(expectedSeriesId)
		then:
			1 * seriesDao.findPurchasesAndSales({ Integer seriesId ->
				assert seriesId == expectedSeriesId
				return true
			}) >> expectedResult
		and:
			result == expectedResult
	}
	
}
