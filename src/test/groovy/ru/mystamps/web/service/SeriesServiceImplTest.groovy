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

import org.springframework.web.multipart.MultipartFile

import spock.lang.Specification
import spock.lang.Unroll

import ru.mystamps.web.dao.JdbcSeriesDao
import ru.mystamps.web.dao.dto.AddSeriesDbDto
import ru.mystamps.web.entity.Image
import ru.mystamps.web.model.AddSeriesForm
import ru.mystamps.web.service.dto.LinkEntityDto
import ru.mystamps.web.service.dto.SeriesInfoDto
import ru.mystamps.web.service.dto.SitemapInfoDto
import ru.mystamps.web.service.dto.Currency
import ru.mystamps.web.tests.DateUtils

class SeriesServiceImplTest extends Specification {
	private static final BigDecimal ANY_PRICE = new BigDecimal("17")
	
	private ImageService imageService = Mock()
	private JdbcSeriesDao jdbcSeriesDao = Mock()
	private MichelCatalogService michelCatalogService = Mock()
	private ScottCatalogService scottCatalogService = Mock()
	private YvertCatalogService yvertCatalogService = Mock()
	private GibbonsCatalogService gibbonsCatalogService = Mock()
	private MultipartFile multipartFile = Mock()
	
	private SeriesService service
	private AddSeriesForm form
	private Integer userId
	
	def setup() {
		form = new AddSeriesForm()
		form.setQuantity(2)
		form.setPerforated(false)
		form.setCategory(TestObjects.createLinkEntityDto())
		
		userId = TestObjects.createUser().getId()
		
		imageService.save(_) >> TestObjects.createImage()
		
		service = new SeriesServiceImpl(
			jdbcSeriesDao,
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
	
	def "add() should load and pass country to series dao if country present"() {
		given:
			LinkEntityDto country = TestObjects.createLinkEntityDto()
		and:
			Integer expectedCountryId = country.getId()
		and:
			form.setCountry(country)
		when:
			service.add(form, userId, false)
		then:
			1 * jdbcSeriesDao.add({ AddSeriesDbDto series ->
				assert series?.countryId == expectedCountryId
				return true
			}) >> 123
	}
	
	@Unroll
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
			1 * jdbcSeriesDao.add({ AddSeriesDbDto series ->
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
	
	def "add() should pass category to series dao"() {
		given:
			LinkEntityDto category = TestObjects.createLinkEntityDto()
		and:
			Integer expectedCategoryId = category.getId()
		and:
			form.setCategory(category)
		when:
			service.add(form, userId, false)
		then:
			1 * jdbcSeriesDao.add({ AddSeriesDbDto series ->
				assert series?.categoryId == expectedCategoryId
				return true
			}) >> 123
	}
	
	def "add() should pass quantity to series dao"() {
		given:
			Integer expectedQuantity = 3
			form.setQuantity(expectedQuantity)
		when:
			service.add(form, userId, false)
		then:
			1 * jdbcSeriesDao.add({ AddSeriesDbDto series ->
				assert series?.quantity == expectedQuantity
				return true
			}) >> 123
	}
	
	def "add() should pass perforated to series dao"() {
		given:
			Boolean expectedResult = true
			form.setPerforated(expectedResult)
		when:
			service.add(form, userId, false)
		then:
			1 * jdbcSeriesDao.add({ AddSeriesDbDto series ->
				assert series?.perforated == expectedResult
				return true
			}) >> 123
	}
	
	@Unroll
	def "add() should pass michel price and currency to series dao"(BigDecimal price, BigDecimal expectedPrice, String expectedCurrency) {
		given:
			form.setMichelPrice(price)
		when:
			service.add(form, userId, false)
		then:
			1 * jdbcSeriesDao.add({ AddSeriesDbDto series ->
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
	def "add() should pass scott price and currency to series dao"(BigDecimal price, BigDecimal expectedPrice, String expectedCurrency) {
		given:
			form.setScottPrice(price)
		when:
			service.add(form, userId, false)
		then:
			1 * jdbcSeriesDao.add({ AddSeriesDbDto series ->
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
	def "add() should pass yvert price and currency to series dao"(BigDecimal price, BigDecimal expectedPrice, String expectedCurrency) {
		given:
			form.setYvertPrice(price)
		when:
			service.add(form, userId, false)
		then:
			1 * jdbcSeriesDao.add({ AddSeriesDbDto series ->
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
	def "add() should pass gibbons price and currency to series dao"(BigDecimal price, BigDecimal expectedPrice, String expectedCurrency) {
		given:
			form.setGibbonsPrice(price)
		when:
			service.add(form, userId, false)
		then:
			1 * jdbcSeriesDao.add({ AddSeriesDbDto series ->
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
	def "add() should pass '#expectedComment' as comment to series dao if user can add comment is #canAddComment"(boolean canAddComment, String comment, String expectedComment) {
		given:
			form.setComment(comment)
		when:
			service.add(form, userId, canAddComment)
		then:
			1 * jdbcSeriesDao.add({ AddSeriesDbDto series ->
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
	
	def "add() should assign created at to current date"() {
		when:
			service.add(form, userId, false)
		then:
			1 * jdbcSeriesDao.add({ AddSeriesDbDto series ->
				assert DateUtils.roughlyEqual(series?.createdAt, new Date())
				return true
			}) >> 123
	}
	
	def "add() should assign updated at to current date"() {
		when:
			service.add(form, userId, false)
		then:
			1 * jdbcSeriesDao.add({ AddSeriesDbDto series ->
				assert DateUtils.roughlyEqual(series?.updatedAt, new Date())
				return true
			}) >> 123
	}
	
	def "add() should assign created by to user"() {
		given:
			Integer expectedUserId = 456
		when:
			service.add(form, expectedUserId, false)
		then:
			1 * jdbcSeriesDao.add({ AddSeriesDbDto series ->
				assert series?.createdBy == expectedUserId
				return true
			}) >> 123
	}
	
	def "add() should assign updated by to user"() {
		given:
			Integer expectedUserId = 789
		when:
			service.add(form, expectedUserId, false)
		then:
			1 * jdbcSeriesDao.add({ AddSeriesDbDto series ->
				assert series?.updatedBy == expectedUserId
				return true
			}) >> 123
	}
	
	def "add() should pass dto to series dao and returnds its result"() {
		given:
			Integer expected = 456
		when:
			Integer actual = service.add(form, userId, false)
		then:
			1 * jdbcSeriesDao.add(_ as AddSeriesDbDto) >> expected
		and:
			actual == expected
	}
	
	def "add() should pass image to image service"() {
		given:
			form.setImage(multipartFile)
		when:
			service.add(form, userId, false)
		then:
			1 * imageService.save({ MultipartFile passedFile ->
				assert passedFile == multipartFile
				return true
			}) >> TestObjects.createImage()
	}
	
	def "add() should add image to the series"() {
		given:
			Integer expectedSeriesId = 123
		and:
			jdbcSeriesDao.add(_ as AddSeriesDbDto) >> expectedSeriesId
		and:
			Integer expectedImageId = 456
		and:
			Image image = TestObjects.createImage()
			image.setId(expectedImageId)
		when:
			service.add(form, userId, false)
		then:
			// FIXME: why we can't use _ as MultipartFile here?
			imageService.save(_) >> image
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
			""      | _
			null    | _
	}
	
	def "add() should add michel numbers to series"() {
		given:
			Set<String> expectedNumbers = [ '1', '2' ] as Set
		and:
			form.setMichelNumbers(expectedNumbers.join(','))
		and:
			Integer expectedSeriesId = 456
		and:
			jdbcSeriesDao.add(_ as AddSeriesDbDto) >> expectedSeriesId
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
			""      | _
			null    | _
	}
	
	def "add() should add scott numbers to series"() {
		given:
			Set<String> expectedNumbers = [ '1', '2' ] as Set
		and:
			form.setScottNumbers(expectedNumbers.join(','))
		and:
			Integer expectedSeriesId = 456
		and:
			jdbcSeriesDao.add(_ as AddSeriesDbDto) >> expectedSeriesId
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
			""      | _
			null    | _
	}
	
	def "add() should add yvert numbers to series"() {
		given:
			Set<String> expectedNumbers = [ '1', '2' ] as Set
		and:
			form.setYvertNumbers(expectedNumbers.join(','))
		and:
			Integer expectedSeriesId = 456
		and:
			jdbcSeriesDao.add(_ as AddSeriesDbDto) >> expectedSeriesId
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
			""      | _
			null    | _
	}
	
	def "add() should add gibbons numbers to series"() {
		given:
			Set<String> expectedNumbers = [ '1', '2' ] as Set
		and:
			form.setGibbonsNumbers(expectedNumbers.join(','))
		and:
			Integer expectedSeriesId = 456
		and:
			jdbcSeriesDao.add(_ as AddSeriesDbDto) >> expectedSeriesId
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
	// Tests for countAll()
	//
	
	def "countAll() should call dao and returns result"() {
		given:
			long expectedResult = 20
		when:
			long result = service.countAll()
		then:
			1 * jdbcSeriesDao.countAll() >> expectedResult
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
			1 * jdbcSeriesDao.countAllStamps() >> expectedResult
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
			1 * jdbcSeriesDao.countSeriesOfCollection({ Integer collectionId ->
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
	
	def "countStampsOf() should pass arguments to dao"() {
		given:
			Integer expectedCollectionId = 8
		when:
			service.countStampsOf(expectedCollectionId)
		then:
			1 * jdbcSeriesDao.countStampsOfCollection({ Integer collectionId ->
				assert expectedCollectionId == collectionId
				return true
			}) >> 0L
	}
	
	//
	// Tests for countByMichelNumber()
	//
	
	@Unroll
	def "countByMichelNumber() should throw exception for invalid argument '#catalogCode'"(String catalogCode) {
		when:
			service.countByMichelNumber(catalogCode)
		then:
			thrown IllegalArgumentException
		where:
			catalogCode | _
			null        | _
			''          | _
			' '         | _
	}
	
	def "countByMichelNumber() should pass argument to dao and return result"() {
		given:
			long expectedResult = 1L
		when:
			long result = service.countByMichelNumber('7')
		then:
			1 * jdbcSeriesDao.countByMichelNumberCode({ String code ->
				assert code == '7'
				return true
			}) >> expectedResult
		and:
			result == expectedResult
	}
	
	//
	// Tests for countByScottNumber()
	//
	
	@Unroll
	def "countByScottNumber() should throw exception for invalid argument '#catalogCode'"(String catalogCode) {
		when:
			service.countByScottNumber(catalogCode)
		then:
			thrown IllegalArgumentException
		where:
			catalogCode | _
			null        | _
			''          | _
			' '         | _
	}
	
	def "countByScottNumber() should pass argument to dao and return result"() {
		given:
			long expectedResult = 2L
		when:
			long result = service.countByScottNumber('8')
		then:
			1 * jdbcSeriesDao.countByScottNumberCode({ String code ->
				assert code == '8'
				return true
			}) >> expectedResult
		and:
			result == expectedResult
	}
	
	//
	// Tests for countByYvertNumber()
	//
	
	@Unroll
	def "countByYvertNumber() should throw exception for invalid argument '#catalogCode'"(String catalogCode) {
		when:
			service.countByYvertNumber(catalogCode)
		then:
			thrown IllegalArgumentException
		where:
			catalogCode | _
			null        | _
			''          | _
			' '         | _
	}
	
	def "countByYvertNumber() should pass argument to dao and return result"() {
		given:
			long expectedResult = 3L
		when:
			long result = service.countByYvertNumber('9')
		then:
			1 * jdbcSeriesDao.countByYvertNumberCode({ String code ->
				assert code == '9'
				return true
			}) >> expectedResult
		and:
			result == expectedResult
	}
	
	//
	// Tests for countByGibbonsNumber()
	//
	
	@Unroll
	def "countByGibbonsNumber() should throw exception for invalid argument '#catalogCode'"(String catalogCode) {
		when:
			service.countByGibbonsNumber(catalogCode)
		then:
			thrown IllegalArgumentException
		where:
			catalogCode | _
			null        | _
			''          | _
			' '         | _
	}
	
	def "countByGibbonsNumber() should pass argument to dao and return result"() {
		given:
			long expectedResult = 4L
		when:
			long result = service.countByGibbonsNumber('10')
		then:
			1 * jdbcSeriesDao.countByGibbonsNumberCode({ String code ->
				assert code == '10'
				return true
			}) >> expectedResult
		and:
			result == expectedResult
	}
	
	//
	// Tests for findSeriesIdByMichelNumber()
	//
	
	@Unroll
	def "findSeriesIdByMichelNumber() should throw exception for invalid argument '#michelNumberCode'"(String michelNumberCode) {
		when:
			service.findSeriesIdByMichelNumber(michelNumberCode)
		then:
			thrown IllegalArgumentException
		where:
			michelNumberCode | _
			null             | _
			''               | _
			' '              | _
	}
	
	def "findSeriesIdByMichelNumber() should pass argument to dao and return result"() {
		given:
			Optional<Integer> expectedResult = Optional.of(1);
		when:
			Optional<Integer> result = service.findSeriesIdByMichelNumber('5');
		then:
			1 * jdbcSeriesDao.findSeriesIdByMichelNumberCode({ String code ->
				assert code == '5'
				return true
			}) >> expectedResult
		and:
			result == expectedResult
	}
	
	//
	// Tests for findSeriesIdByScottNumber()
	//
	
	@Unroll
	def "findSeriesIdByScottNumber() should throw exception for invalid argument '#scottNumberCode'"(String scottNumberCode) {
		when:
			service.findSeriesIdByScottNumber(scottNumberCode)
		then:
			thrown IllegalArgumentException
		where:
			scottNumberCode | _
			null            | _
			''              | _
			' '             | _
	}
	
	def "findSeriesIdByScottNumber() should pass argument to dao and return result"() {
		given:
			Optional<Integer> expectedResult = Optional.of(1);
		when:
			Optional<Integer> result = service.findSeriesIdByScottNumber('5');
		then:
			1 * jdbcSeriesDao.findSeriesIdByScottNumberCode({ String code ->
				assert code == '5'
				return true
			}) >> expectedResult
		and:
			result == expectedResult
	}
	
	//
	// Tests for findSeriesIdByYvertNumber()
	//
	
	@Unroll
	def "findSeriesIdByYvertNumber() should throw exception for invalid argument '#yvertNumberCode'"(String yvertNumberCode) {
		when:
			service.findSeriesIdByYvertNumber(yvertNumberCode)
		then:
			thrown IllegalArgumentException
		where:
			yvertNumberCode | _
			null            | _
			''              | _
			' '             | _
	}
	
	def "findSeriesIdByYvertNumber() should pass argument to dao and return result"() {
		given:
			Optional<Integer> expectedResult = Optional.of(1);
		when:
			Optional<Integer> result = service.findSeriesIdByYvertNumber('5');
		then:
			1 * jdbcSeriesDao.findSeriesIdByYvertNumberCode({ String code ->
				assert code == '5'
				return true
			}) >> expectedResult
		and:
			result == expectedResult
	}
	
	//
	// Tests for findSeriesIdByGibbonsNumber()
	//
	
	@Unroll
	def "findSeriesIdByGibbonsNumber() should throw exception for invalid argument '#gibbonsNumberCode'"(String gibbonsNumberCode) {
		when:
			service.findSeriesIdByGibbonsNumber(gibbonsNumberCode)
		then:
			thrown IllegalArgumentException
		where:
			gibbonsNumberCode | _
			null              | _
			''                | _
			' '               | _
	}
	
	def "findSeriesIdByGibbonsNumber() should pass argument to dao and return result"() {
		given:
			Optional<Integer> expectedResult = Optional.of(1);
		when:
			Optional<Integer> result = service.findSeriesIdByGibbonsNumber('5');
		then:
			1 * jdbcSeriesDao.findSeriesIdByGibbonsNumberCode({ String code ->
				assert code == '5'
				return true
			}) >> expectedResult
		and:
			result == expectedResult
	}
	
	//
	// Tests for findByCategoryId()
	//
	
	def "findByCategoryId() should throw exception if category id is null"() {
		when:
			service.findByCategoryId(null, 'any')
		then:
			thrown IllegalArgumentException
	}
	
	def "findByCategoryId() should call dao and return result"() {
		given:
			SeriesInfoDto series = TestObjects.createSeriesInfoDto()
		and:
			Iterable<SeriesInfoDto> expectedResult = [ series ]
		and:
			jdbcSeriesDao.findByCategoryIdAsSeriesInfo(_ as Integer, _ as String) >> expectedResult
		when:
			Iterable<SeriesInfoDto> result = service.findByCategoryId(10, 'any')
		then:
			result == expectedResult
	}
	
	//
	// Tests for findByCountryId()
	//
	
	def "findByCountryId() should throw exception if country id is null"() {
		when:
			service.findByCountryId(null, 'any')
		then:
			thrown IllegalArgumentException
	}
	
	def "findByCountryId() should call dao and return result"() {
		given:
			SeriesInfoDto series = TestObjects.createSeriesInfoDto()
		and:
			Iterable<SeriesInfoDto> expectedResult = [ series ]
		and:
			jdbcSeriesDao.findByCountryIdAsSeriesInfo(_ as Integer, _ as String) >> expectedResult
		when:
			Iterable<SeriesInfoDto> result = service.findByCountryId(20, 'any')
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
			1 * jdbcSeriesDao.findByCollectionIdAsSeriesInfo(
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
	
	def "findRecentlyAdded should pass arguments to dao"() {
		given:
			int expectedQuantity = 3
		and:
			String expectedLang = 'expected'
		when:
			service.findRecentlyAdded(expectedQuantity, expectedLang)
		then:
			1 * jdbcSeriesDao.findLastAdded(
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
			Iterable<SitemapInfoDto> expectedResult =
				Collections.singletonList(TestObjects.createSitemapInfoDto())
		when:
			Iterable<SitemapInfoDto> result = service.findAllForSitemap()
		then:
			1 * jdbcSeriesDao.findAllForSitemap() >> expectedResult
		and:
			result == expectedResult
	}
	
}
