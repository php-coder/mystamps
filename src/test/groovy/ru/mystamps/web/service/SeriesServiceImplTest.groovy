/**
 * Copyright (C) 2009-2018 Slava Semushin <slava.semushin@gmail.com>
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

import static io.qala.datagen.RandomShortApi.bool
import static io.qala.datagen.RandomShortApi.nullOr

import org.springframework.web.multipart.MultipartFile

import spock.lang.Specification
import spock.lang.Unroll

import org.slf4j.helpers.NOPLogger

import ru.mystamps.web.dao.SeriesDao
import ru.mystamps.web.dao.dto.AddSeriesDbDto
import ru.mystamps.web.dao.dto.Currency
import ru.mystamps.web.dao.dto.ImageInfoDto
import ru.mystamps.web.dao.dto.SeriesFullInfoDto
import ru.mystamps.web.controller.dto.AddImageForm
import ru.mystamps.web.controller.dto.AddSeriesForm
import ru.mystamps.web.dao.dto.LinkEntityDto
import ru.mystamps.web.dao.dto.PurchaseAndSaleDto
import ru.mystamps.web.dao.dto.SeriesInfoDto
import ru.mystamps.web.dao.dto.SitemapInfoDto
import ru.mystamps.web.service.dto.SeriesDto
import ru.mystamps.web.tests.DateUtils
import ru.mystamps.web.tests.Random

@SuppressWarnings(['ClassJavadoc', 'MethodName', 'NoDef', 'NoTabCharacter', 'TrailingWhitespace'])
class SeriesServiceImplTest extends Specification {
	private final ImageService imageService = Mock()
	private final SeriesDao seriesDao = Mock()
	private final StampsCatalogService michelCatalogService = Mock()
	private final StampsCatalogService scottCatalogService = Mock()
	private final StampsCatalogService yvertCatalogService = Mock()
	private final StampsCatalogService gibbonsCatalogService = Mock()
	private final StampsCatalogService solovyovCatalogService = Mock()
	private final StampsCatalogService zagorskiCatalogService = Mock()
	private final MultipartFile multipartFile = Mock()
	
	private SeriesService service
	private AddSeriesForm form
	private AddImageForm imageForm
	
	@SuppressWarnings('UnnecessaryGetter')
	def setup() {
		form = new AddSeriesForm()
		form.setQuantity(2)
		form.setPerforated(false)
		form.setCategory(TestObjects.createLinkEntityDto())
		
		imageForm = new AddImageForm()
		
		imageService.save(_) >> TestObjects.createImageInfoDto()
		
		service = new SeriesServiceImpl(
			NOPLogger.NOP_LOGGER,
			seriesDao,
			imageService,
			michelCatalogService,
			scottCatalogService,
			yvertCatalogService,
			gibbonsCatalogService,
			solovyovCatalogService,
			zagorskiCatalogService
		)
		
		multipartFile.getOriginalFilename() >> '/path/to/test/file.ext'
	}
	
	//
	// Tests for add()
	//
	
	def "add() should throw exception if dto is null"() {
		when:
			service.add(null, Random.userId(), false)
		then:
			thrown IllegalArgumentException
	}
	
	def "add() should throw exception if quantity is null"() {
		given:
			form.setQuantity(null)
		when:
			service.add(form, Random.userId(), false)
		then:
			thrown IllegalArgumentException
	}
	
	def "add() should throw exception if perforated is null"() {
		given:
			form.setPerforated(null)
		when:
			service.add(form, Random.userId(), false)
		then:
			thrown IllegalArgumentException
	}
	
	def "add() should throw exception if category is null"() {
		given:
			form.setCategory(null)
		when:
			service.add(form, Random.userId(), false)
		then:
			thrown IllegalArgumentException
	}
	
	def "add() should throw exception if category id is null"() {
		given:
			form.setCategory(new LinkEntityDto(null, 'test', 'Test'))
		when:
			service.add(form, Random.userId(), false)
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
			service.add(form, Random.userId(), false)
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
			service.add(form, Random.userId(), false)
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
			service.add(form, Random.userId(), false)
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
			service.add(form, Random.userId(), false)
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
			service.add(form, Random.userId(), false)
		then:
			1 * seriesDao.add({ AddSeriesDbDto series ->
				assert series?.perforated == expectedResult
				return true
			}) >> 123
	}
	
	@SuppressWarnings([ 'ClosureAsLastMethodParameter', 'UnnecessaryObjectReferences', 'UnnecessaryReturnKeyword' ])
	def 'add() should pass catalog prices to series dao'() {
		given:
			BigDecimal expectedPrice = nullOr(Random.price())
		and:
			form.setMichelPrice(expectedPrice)
			form.setScottPrice(expectedPrice)
			form.setYvertPrice(expectedPrice)
			form.setGibbonsPrice(expectedPrice)
			form.setSolovyovPrice(expectedPrice)
			form.setZagorskiPrice(expectedPrice)
		when:
			service.add(form, Random.userId(), bool())
		then:
			1 * seriesDao.add({ AddSeriesDbDto series ->
				assert series?.michelPrice == expectedPrice
				assert series?.scottPrice == expectedPrice
				assert series?.yvertPrice == expectedPrice
				assert series?.gibbonsPrice == expectedPrice
				assert series?.solovyovPrice == expectedPrice
				assert series?.zagorskiPrice == expectedPrice
				return true
			}) >> Random.id()
	}
	
	def "add() should throw exception if comment is empty"() {
		given:
			form.setComment('  ')
		when:
			service.add(form, Random.userId(), true)
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
			service.add(form, Random.userId(), canAddComment)
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
			Integer actual = service.add(form, Random.userId(), false)
		then:
			1 * seriesDao.add(_ as AddSeriesDbDto) >> expected
		and:
			actual == expected
	}
	
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
			service.add(form, Random.userId(), false)
		then:
			1 * michelCatalogService.add(expectedNumbers)
		and:
			1 * michelCatalogService.addToSeries(expectedSeriesId, expectedNumbers)
	}
	
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
			service.add(form, Random.userId(), false)
		then:
			1 * scottCatalogService.add(expectedNumbers)
		and:
			1 * scottCatalogService.addToSeries(expectedSeriesId, expectedNumbers)
	}
	
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
			service.add(form, Random.userId(), false)
		then:
			1 * yvertCatalogService.add(expectedNumbers)
		and:
			1 * yvertCatalogService.addToSeries(expectedSeriesId, expectedNumbers)
	}
	
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
			service.add(form, Random.userId(), false)
		then:
			1 * gibbonsCatalogService.add(expectedNumbers)
		and:
			1 * gibbonsCatalogService.addToSeries(expectedSeriesId, expectedNumbers)
	}

	def 'add() should add solovyov numbers to series'() {
		given:
			Set<String> expectedNumbers = Random.solovyovNumbers()
		and:
			form.setSolovyovNumbers(expectedNumbers.join(','))
		and:
			Integer expectedSeriesId = Random.id()
		and:
			seriesDao.add(_ as AddSeriesDbDto) >> expectedSeriesId
		when:
			service.add(form, Random.userId(), bool())
		then:
			1 * solovyovCatalogService.add(expectedNumbers)
		and:
			1 * solovyovCatalogService.addToSeries(expectedSeriesId, expectedNumbers)
	}
	
	def 'add() should add zagorski numbers to series'() {
		given:
			Set<String> expectedNumbers = Random.zagorskiNumbers()
		and:
			form.setZagorskiNumbers(expectedNumbers.join(','))
		and:
			Integer expectedSeriesId = Random.id()
		and:
			seriesDao.add(_ as AddSeriesDbDto) >> expectedSeriesId
		when:
			service.add(form, Random.userId(), bool())
		then:
			1 * zagorskiCatalogService.add(expectedNumbers)
		and:
			1 * zagorskiCatalogService.addToSeries(expectedSeriesId, expectedNumbers)
	}
	
	def "add() should pass image to image service"() {
		given:
			form.setImage(multipartFile)
		when:
			service.add(form, Random.userId(), false)
		then:
			1 * imageService.save(multipartFile) >> TestObjects.createImageInfoDto()
	}
	
	def "add() should add image to the series"() {
		given:
			Integer expectedSeriesId = 123
		and:
			seriesDao.add(_ as AddSeriesDbDto) >> expectedSeriesId
		and:
			Integer expectedImageId = 456
		when:
			service.add(form, Random.userId(), false)
		then:
			// FIXME: why we can't use _ as MultipartFile here?
			imageService.save(_) >> new ImageInfoDto(expectedImageId, 'JPEG')
		and:
			1 * imageService.addToSeries(expectedSeriesId, expectedImageId)
	}
	
	def "add() should remove image when exception occurs"() {
		given:
			ImageInfoDto expectedImageInfo = new ImageInfoDto(654, 'JPEG')
		and:
			seriesDao.add(_ as AddSeriesDbDto) >> 111
		and:
			imageService.addToSeries(_ as Integer, _ as Integer) >> { throw new IllegalStateException() }
		when:
			service.add(form, Random.userId(), false)
		then:
			imageService.save(_) >> expectedImageInfo
		and:
			1 * imageService.removeIfPossible(expectedImageInfo)
		and:
			thrown IllegalStateException
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
	
	@SuppressWarnings('UnnecessaryReturnKeyword')
	def "addImageToSeries() should mark series as modified"() {
		given:
			Integer expectedSeriesId = 123
			Integer expectedUserId = 321
		when:
			service.addImageToSeries(imageForm, expectedSeriesId, expectedUserId)
		then:
			1 * seriesDao.markAsModified(
				expectedSeriesId,
				{ Date updatedAt ->
					assert DateUtils.roughlyEqual(updatedAt, new Date())
					return true
				},
				expectedUserId
			)
	}
	
	def "addImageToSeries() should save image"() {
		given:
			imageForm.setImage(multipartFile)
		when:
			service.addImageToSeries(imageForm, 111, 222)
		then:
			1 * imageService.save(multipartFile) >> TestObjects.createImageInfoDto()
	}
	
	def "addImageToSeries() should add image to series"() {
		given:
			Integer expectedSeriesId = 123
			Integer expectedUserId = 321
			Integer expectedImageId = 456
		when:
			service.addImageToSeries(imageForm, expectedSeriesId, expectedUserId)
		then:
			imageService.save(_) >> new ImageInfoDto(expectedImageId, 'JPEG')
		and:
			1 * imageService.addToSeries(expectedSeriesId, expectedImageId)
	}
	
	def "addImageToSeries() should remove image when exception occurs"() {
		given:
			ImageInfoDto expectedImageInfo = new ImageInfoDto(456, 'JPEG')
		and:
			imageService.addToSeries(_ as Integer, _ as Integer) >> { throw new IllegalStateException() }
		when:
			service.addImageToSeries(imageForm, 111, 222)
		then:
			imageService.save(_) >> expectedImageInfo
		and:
			1 * imageService.removeIfPossible(expectedImageInfo)
		and:
			thrown IllegalStateException
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
	
	def "countSeriesOf() should pass argument to dao"() {
		given:
			Integer expectedCollectionId = 7
		when:
			service.countSeriesOf(expectedCollectionId)
		then:
			1 * seriesDao.countSeriesOfCollection(expectedCollectionId) >> 0L
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
	
	def "countStampsOf() should pass arguments to dao"() {
		given:
			Integer expectedCollectionId = 8
		when:
			service.countStampsOf(expectedCollectionId)
		then:
			1 * seriesDao.countStampsOfCollection(expectedCollectionId) >> 0L
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
	
	def "countAddedSince() should invoke dao, pass argument and return result from dao"() {
		given:
			Date expectedDate = new Date()
		and:
			long expectedResult = 35
		when:
			long result = service.countAddedSince(expectedDate)
		then:
			1 * seriesDao.countAddedSince(expectedDate) >> expectedResult
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
	
	def "countUpdatedSince() should invoke dao, pass argument and return result from dao"() {
		given:
			Date expectedDate = new Date()
		and:
			long expectedResult = 45
		when:
			long result = service.countUpdatedSince(expectedDate)
		then:
			1 * seriesDao.countUpdatedSince(expectedDate) >> expectedResult
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
	@SuppressWarnings([ 'LineLength', /* false positive: */ 'UnnecessaryBooleanExpression' ])
	def "isSeriesExist() should return #expectedResult when dao returns #daoReturnValue"(Integer daoReturnValue, boolean expectedResult) {
		given:
			Integer expectedSeriesId = 13
		when:
			boolean result = service.isSeriesExist(expectedSeriesId)
		then:
			1 * seriesDao.countSeriesById(expectedSeriesId) >> daoReturnValue
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
	
	@SuppressWarnings('UnnecessaryObjectReferences')
	def "findFullInfoById() should return info about series"() {
		given:
			Integer expectedSeriesId = 20
			String expectedLang = 'kz'
			SeriesFullInfoDto expectedInfo = TestObjects.createSeriesFullInfoDto()
			List<String> expectedMichelNumbers   = [ '1', '2' ]
			List<String> expectedScottNumbers    = [ '3', '4' ]
			List<String> expectedYvertNumbers    = [ '5', '6' ]
			List<String> expectedGibbonsNumbers  = [ '7', '8' ]
			List<String> expectedZagorskiNumbers = Random.zagorskiNumbers().toList()
			List<String> expectedSolovyovNumbers = Random.solovyovNumbers().toList()
			List<Integer> expectedImageIds       = [ 9, 10 ]
		when:
			SeriesDto result = service.findFullInfoById(expectedSeriesId, expectedLang)
		then:
			1 * seriesDao.findByIdAsSeriesFullInfo(expectedSeriesId, expectedLang) >> expectedInfo
		and:
			1 * michelCatalogService.findBySeriesId(expectedSeriesId) >> expectedMichelNumbers
		and:
			1 * scottCatalogService.findBySeriesId(expectedSeriesId) >> expectedScottNumbers
		and:
			1 * yvertCatalogService.findBySeriesId(expectedSeriesId) >> expectedYvertNumbers
		and:
			1 * gibbonsCatalogService.findBySeriesId(expectedSeriesId) >> expectedGibbonsNumbers
		and:
			1 * solovyovCatalogService.findBySeriesId(expectedSeriesId) >> expectedSolovyovNumbers
		and:
			1 * zagorskiCatalogService.findBySeriesId(expectedSeriesId) >> expectedZagorskiNumbers
		and:
			1 * imageService.findBySeriesId(expectedSeriesId) >> expectedImageIds
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
			
			result.solovyov?.numbers  == expectedSolovyovNumbers
			result.solovyov?.price    == expectedInfo.solovyovPrice
			
			result.zagorski?.numbers  == expectedZagorskiNumbers
			result.zagorski?.price    == expectedInfo.zagorskiPrice
	}
	
	//
	// Tests for findByMichelNumber()
	//
	
	def "findByMichelNumber() should find series ids"() {
		given:
			String expectedNumber = '5'
		when:
			service.findByMichelNumber(expectedNumber, 'en')
		then:
			1 * michelCatalogService.findSeriesIdsByNumber(expectedNumber) >> []
	}
	
	def "findByMichelNumber() shouldn't try to find series info if there are no series"() {
		given:
			michelCatalogService.findSeriesIdsByNumber(_ as String) >> []
		when:
			List<SeriesInfoDto> result = service.findByMichelNumber('5', 'en')
		then:
			0 * seriesDao.findByIdsAsSeriesInfo(_ as List, _ as String)
		and:
			result.empty
	}
	
	def "findByMichelNumber() should find and return series info"() {
		given:
			String expectedLang = 'en'
		and:
			List<Integer> expectedSeriesIds = [ 1 ]
		and:
			michelCatalogService.findSeriesIdsByNumber(_ as String) >> expectedSeriesIds
		and:
			List<SeriesInfoDto> expectedResult = []
		when:
			List<SeriesInfoDto> result = service.findByMichelNumber('5', expectedLang)
		then:
			1 * seriesDao.findByIdsAsSeriesInfo(expectedSeriesIds, expectedLang) >> expectedResult
		and:
			result == expectedResult
	}
	
	//
	// Tests for findByScottNumber()
	//
	
	def "findByScottNumber() should find series ids"() {
		given:
			String expectedNumber = '5'
		when:
			service.findByScottNumber(expectedNumber, 'en')
		then:
			1 * scottCatalogService.findSeriesIdsByNumber(expectedNumber) >> []
	}
	
	def "findByScottNumber() shouldn't try to find series info if there are no series"() {
		given:
			scottCatalogService.findSeriesIdsByNumber(_ as String) >> []
		when:
			List<SeriesInfoDto> result = service.findByScottNumber('5', 'en')
		then:
			0 * seriesDao.findByIdsAsSeriesInfo(_ as List, _ as String)
		and:
			result.empty
	}
	
	def "findByScottNumber() should find and return series info"() {
		given:
			String expectedLang = 'en'
		and:
			List<Integer> expectedSeriesIds = [ 1 ]
		and:
			scottCatalogService.findSeriesIdsByNumber(_ as String) >> expectedSeriesIds
		and:
			List<SeriesInfoDto> expectedResult = []
		when:
			List<SeriesInfoDto> result = service.findByScottNumber('5', expectedLang)
		then:
			1 * seriesDao.findByIdsAsSeriesInfo(expectedSeriesIds, expectedLang) >> expectedResult
		and:
			result == expectedResult
	}
	
	//
	// Tests for findByYvertNumber()
	//
	
	def "findByYvertNumber() should find series ids"() {
		given:
			String expectedNumber = '5'
		when:
			service.findByYvertNumber(expectedNumber, 'en')
		then:
			1 * yvertCatalogService.findSeriesIdsByNumber(expectedNumber) >> []
	}
	
	def "findByYvertNumber() shouldn't try to find series info if there are no series"() {
		given:
			yvertCatalogService.findSeriesIdsByNumber(_ as String) >> []
		when:
			List<SeriesInfoDto> result = service.findByYvertNumber('5', 'en')
		then:
			0 * seriesDao.findByIdsAsSeriesInfo(_ as List, _ as String)
		and:
			result.empty
	}
	
	def "findByYvertNumber() should find and return series info"() {
		given:
			String expectedLang = 'en'
		and:
			List<Integer> expectedSeriesIds = [ 1 ]
		and:
			yvertCatalogService.findSeriesIdsByNumber(_ as String) >> expectedSeriesIds
		and:
			List<SeriesInfoDto> expectedResult = []
		when:
			List<SeriesInfoDto> result = service.findByYvertNumber('5', expectedLang)
		then:
			1 * seriesDao.findByIdsAsSeriesInfo(expectedSeriesIds, expectedLang) >> expectedResult
		and:
			result == expectedResult
	}
	
	//
	// Tests for findByGibbonsNumber()
	//
	
	def "findByGibbonsNumber() should find series ids"() {
		given:
			String expectedNumber = '5'
		when:
			service.findByGibbonsNumber(expectedNumber, 'en')
		then:
			1 * gibbonsCatalogService.findSeriesIdsByNumber(expectedNumber) >> []
	}
	
	def "findByGibbonsNumber() shouldn't try to find series info if there are no series"() {
		given:
			gibbonsCatalogService.findSeriesIdsByNumber(_ as String) >> []
		when:
			List<SeriesInfoDto> result = service.findByGibbonsNumber('5', 'en')
		then:
			0 * seriesDao.findByIdsAsSeriesInfo(_ as List, _ as String)
		and:
			result.empty
	}
	
	def "findByGibbonsNumber() should find and return series info"() {
		given:
			String expectedLang = 'en'
		and:
			List<Integer> expectedSeriesIds = [ 1 ]
		and:
			gibbonsCatalogService.findSeriesIdsByNumber(_ as String) >> expectedSeriesIds
		and:
			List<SeriesInfoDto> expectedResult = []
		when:
			List<SeriesInfoDto> result = service.findByGibbonsNumber('5', expectedLang)
		then:
			1 * seriesDao.findByIdsAsSeriesInfo(expectedSeriesIds, expectedLang) >> expectedResult
		and:
			result == expectedResult
	}
	
	//
	// Tests for findBySolovyovNumber()
	//
	
	def 'findBySolovyovNumber() should find series ids'() {
		given:
			String expectedNumber = Random.catalogNumber()
		when:
			service.findBySolovyovNumber(expectedNumber, Random.lang())
		then:
			1 * solovyovCatalogService.findSeriesIdsByNumber(expectedNumber) >> []
	}
	
	def 'findBySolovyovNumber() shouldn\'t try to find series info if there are no series'() {
		given:
			solovyovCatalogService.findSeriesIdsByNumber(_ as String) >> []
		when:
			List<SeriesInfoDto> result = service.findBySolovyovNumber(Random.catalogNumber(), Random.lang())
		then:
			0 * seriesDao.findByIdsAsSeriesInfo(_ as List, _ as String)
		and:
			result.empty
	}
	
	def 'findBySolovyovNumber() should find and return series info'() {
		given:
			String expectedLang = Random.lang()
			List<Integer> expectedSeriesIds = Random.listOfIntegers()
			List<SeriesInfoDto> expectedResult = Random.listOfSeriesInfoDto()
		and:
			solovyovCatalogService.findSeriesIdsByNumber(_ as String) >> expectedSeriesIds
		when:
			List<SeriesInfoDto> result = service.findBySolovyovNumber(Random.catalogNumber(), expectedLang)
		then:
			1 * seriesDao.findByIdsAsSeriesInfo(expectedSeriesIds, expectedLang) >> expectedResult
		and:
			result == expectedResult
	}
	
	//
	// Tests for findByZagorskiNumber()
	//
	
	def 'findByZagorskiNumber() should find series ids'() {
		given:
			String expectedNumber = Random.catalogNumber()
		when:
			service.findByZagorskiNumber(expectedNumber, Random.lang())
		then:
			1 * zagorskiCatalogService.findSeriesIdsByNumber(expectedNumber) >> []
	}
	
	def 'findByZagorskiNumber() shouldn\'t try to find series info if there are no series'() {
		given:
			zagorskiCatalogService.findSeriesIdsByNumber(_ as String) >> []
		when:
			List<SeriesInfoDto> result = service.findByZagorskiNumber(Random.catalogNumber(), Random.lang())
		then:
			0 * seriesDao.findByIdsAsSeriesInfo(_ as List, _ as String)
		and:
			result.empty
	}
	
	def 'findByZagorskiNumber() should find and return series info'() {
		given:
			String expectedLang = Random.lang()
			List<Integer> expectedSeriesIds = Random.listOfIntegers()
			List<SeriesInfoDto> expectedResult = Random.listOfSeriesInfoDto()
		and:
			zagorskiCatalogService.findSeriesIdsByNumber(_ as String) >> expectedSeriesIds
		when:
			List<SeriesInfoDto> result = service.findByZagorskiNumber(Random.catalogNumber(), expectedLang)
		then:
			1 * seriesDao.findByIdsAsSeriesInfo(expectedSeriesIds, expectedLang) >> expectedResult
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
	
	def "findByCollectionId() should pass arguments to dao"() {
		given:
			Integer expectedCollectionId = 16
		and:
			String expectedLang = 'expected'
		when:
			service.findByCollectionId(expectedCollectionId, expectedLang)
		then:
			1 * seriesDao.findByCollectionIdAsSeriesInfo(expectedCollectionId, expectedLang) >> []
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
	
	def "findRecentlyAdded should pass arguments to dao"() {
		given:
			int expectedQuantity = 3
		and:
			String expectedLang = 'expected'
		when:
			service.findRecentlyAdded(expectedQuantity, expectedLang)
		then:
			1 * seriesDao.findLastAdded(expectedQuantity, expectedLang) >> []
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
	
	def "findPurchasesAndSales() should invoke dao, pass argument and return result from dao"() {
		given:
			Integer expectedSeriesId = 88
		and:
			List<PurchaseAndSaleDto> expectedResult = [ TestObjects.createPurchaseAndSaleDto() ]
		when:
			List<PurchaseAndSaleDto> result = service.findPurchasesAndSales(expectedSeriesId)
		then:
			1 * seriesDao.findPurchasesAndSales(expectedSeriesId) >> expectedResult
		and:
			result == expectedResult
	}
	
}
