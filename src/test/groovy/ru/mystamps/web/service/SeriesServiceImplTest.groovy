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
import ru.mystamps.web.dao.SeriesDao
import ru.mystamps.web.entity.Category
import ru.mystamps.web.entity.Country
import ru.mystamps.web.entity.Collection
import ru.mystamps.web.entity.Currency
import ru.mystamps.web.entity.GibbonsCatalog
import ru.mystamps.web.entity.Image
import ru.mystamps.web.entity.MichelCatalog
import ru.mystamps.web.entity.ScottCatalog
import ru.mystamps.web.entity.Series
import ru.mystamps.web.entity.User
import ru.mystamps.web.entity.YvertCatalog
import ru.mystamps.web.model.AddSeriesForm
import ru.mystamps.web.service.dto.SitemapInfoDto
import ru.mystamps.web.tests.DateUtils

class SeriesServiceImplTest extends Specification {
	private static final BigDecimal ANY_PRICE = new BigDecimal("17")
	
	private ImageService imageService = Mock()
	private SeriesDao seriesDao = Mock()
	private JdbcSeriesDao jdbcSeriesDao = Mock()
	private MultipartFile multipartFile = Mock()
	
	private SeriesService service
	private AddSeriesForm form
	private User user
	
	def setup() {
		form = new AddSeriesForm()
		form.setQuantity(2)
		form.setPerforated(false)
		form.setCategory(TestObjects.createCategory())
		
		user = TestObjects.createUser()
		
		imageService.save(_) >> TestObjects.createImage()
		
		service = new SeriesServiceImpl(seriesDao, jdbcSeriesDao, imageService)
	}
	
	//
	// Tests for add()
	//
	
	def "add() should throw exception argument is null"() {
		when:
			service.add(null, user, false)
		then:
			thrown IllegalArgumentException
	}
	
	def "add() should throw exception if quantity is null"() {
		given:
			form.setQuantity(null)
		when:
			service.add(form, user, false)
		then:
			thrown IllegalArgumentException
	}
	
	def "add() should throw exception if perforated is null"() {
		given:
			form.setPerforated(null)
		when:
			service.add(form, user, false)
		then:
			thrown IllegalArgumentException
	}
	
	def "add() should throw exception if category is null"() {
		given:
			form.setCategory(null)
		when:
			service.add(form, user, false)
		then:
			thrown IllegalArgumentException
	}
	
	def "add() should throw exception when user is null"() {
		when:
			service.add(form, null, false)
		then:
			thrown IllegalArgumentException
	}
	
	def "add() should pass entity to series dao"() {
		when:
			service.add(form, user, false)
		then:
			1 * seriesDao.save(_ as Series) >> TestObjects.createSeries()
	}
	
	def "add() should load and pass country to series dao if country present"() {
		given:
			Country expectedCountry = TestObjects.createCountry()
			form.setCountry(expectedCountry)
		when:
			service.add(form, user, false)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.country == expectedCountry
				return true
			}) >> TestObjects.createSeries()
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
			service.add(form, user, false)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.releaseDay == expectedDay
				assert series?.releaseMonth == expectedMonth
				assert series?.releaseYear == expectedYear
				return true
			}) >> TestObjects.createSeries()
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
			Category expectedCategory = TestObjects.createCategory()
			form.setCategory(expectedCategory)
		when:
			service.add(form, user, false)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.category == expectedCategory
				return true
			}) >> TestObjects.createSeries()
	}
	
	def "add() should pass quantity to series dao"() {
		given:
			Integer expectedQuantity = 3
			form.setQuantity(expectedQuantity)
		when:
			service.add(form, user, false)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.quantity == expectedQuantity
				return true
			}) >> TestObjects.createSeries()
	}
	
	def "add() should pass perforated to series dao"() {
		given:
			Boolean expectedResult = true
			form.setPerforated(expectedResult)
		when:
			service.add(form, user, false)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.perforated == expectedResult
				return true
			}) >> TestObjects.createSeries()
	}

	def "add() should pass null to series dao if michel numbers is null"() {
		given:
			assert form.getMichelNumbers() == null
		when:
			service.add(form, user, false)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.michel == null
				return true
			}) >> TestObjects.createSeries()
	}
	
	def "add() should pass michel numbers to series dao"() {
		given:
			Set<MichelCatalog> expectedNumbers = [
				new MichelCatalog('1'),
				new MichelCatalog('2')
			] as Set
			form.setMichelNumbers(expectedNumbers.join(','))
		when:
			service.add(form, user, false)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.michel == expectedNumbers
				return true
			}) >> TestObjects.createSeries()
	}
	
	def "add() should pass michel price to series dao"() {
		given:
			BigDecimal expectedPrice = ANY_PRICE
			form.setMichelPrice(expectedPrice)
		and:
			Currency expectedCurrency = Currency.EUR
		when:
			service.add(form, user, false)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.michelPrice?.value == expectedPrice
				assert series?.michelPrice?.currency == expectedCurrency
				return true
			}) >> TestObjects.createSeries()
	}
	
	def "add() should pass null to series dao if michel price is null"() {
		given:
			form.setMichelPrice(null)
		when:
			service.add(form, user, false)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.michelPrice == null
				return true
			}) >> TestObjects.createSeries()
	}
	
	def "add() should pass null to series dao if scott numbers is null"() {
		given:
			assert form.getScottNumbers() == null
		when:
			service.add(form, user, false)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.scott == null
				return true
			}) >> TestObjects.createSeries()
	}
	
	def "add() should pass scott numbers to series dao"() {
		given:
			Set<ScottCatalog> expectedNumbers = [
				new ScottCatalog('1'),
				new ScottCatalog('2')
			] as Set
			form.setScottNumbers(expectedNumbers.join(','))
		when:
			service.add(form, user, false)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.scott == expectedNumbers
				return true
			}) >> TestObjects.createSeries()
	}
	
	def "add() should pass scott price to series dao"() {
		given:
			BigDecimal expectedPrice = ANY_PRICE
			form.setScottPrice(expectedPrice)
		and:
			Currency expectedCurrency = Currency.USD
		when:
			service.add(form, user, false)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.scottPrice?.value == expectedPrice
				assert series?.scottPrice?.currency == expectedCurrency
				return true
			}) >> TestObjects.createSeries()
	}
	
	def "add() should pass null to series dao if scott price is null"() {
		given:
			form.setScottPrice(null)
		when:
			service.add(form, user, false)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.scottPrice == null
				return true
			}) >> TestObjects.createSeries()
	}
	
	def "add() should pass null to series dao if yvert numbers is null"() {
		given:
			assert form.getYvertNumbers() == null
		when:
			service.add(form, user, false)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.yvert == null
				return true
			}) >> TestObjects.createSeries()
	}
	
	def "add() should pass yvert numbers to series dao"() {
		given:
			Set<YvertCatalog> expectedNumbers = [
				new YvertCatalog('1'),
				new YvertCatalog('2')
			] as Set
			form.setYvertNumbers(expectedNumbers.join(','))
		when:
			service.add(form, user, false)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.yvert == expectedNumbers
				return true
			}) >> TestObjects.createSeries()
	}
	
	def "add() should pass yvert price to series dao"() {
		given:
			BigDecimal expectedPrice = ANY_PRICE
			form.setYvertPrice(expectedPrice)
		and:
			Currency expectedCurrency = Currency.EUR
		when:
			service.add(form, user, false)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.yvertPrice?.value == expectedPrice
				assert series?.yvertPrice?.currency == expectedCurrency
				return true
			}) >> TestObjects.createSeries()
	}
	
	def "add() should pass null to series dao if yvert price is null"() {
		given:
			form.setYvertPrice(null)
		when:
			service.add(form, user, false)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.yvertPrice == null
				return true
			}) >> TestObjects.createSeries()
	}
	
	def "add() should pass null to series dao if gibbons numbers is null"() {
		given:
			assert form.getGibbonsNumbers() == null
		when:
			service.add(form, user, false)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.gibbons == null
				return true
			}) >> TestObjects.createSeries()
	}
	
	def "add() should pass gibbons numbers to series dao"() {
		given:
			Set<GibbonsCatalog> expectedNumbers = [
				new GibbonsCatalog('1'),
				new GibbonsCatalog('2')
			] as Set
			form.setGibbonsNumbers(expectedNumbers.join(','))
		when:
			service.add(form, user, false)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.gibbons == expectedNumbers
				return true
			}) >> TestObjects.createSeries()
	}
	
	def "add() should pass gibbons price to series dao"() {
		given:
			BigDecimal expectedPrice = ANY_PRICE
			form.setGibbonsPrice(expectedPrice)
		and:
			Currency expectedCurrency = Currency.GBP
		when:
			service.add(form, user, false)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.gibbonsPrice?.value == expectedPrice
				assert series?.gibbonsPrice?.currency == expectedCurrency
				return true
			}) >> TestObjects.createSeries()
	}
	
	def "add() should pass null to series dao if gibbons price is null"() {
		given:
			form.setGibbonsPrice(null)
		when:
			service.add(form, user, false)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.gibbonsPrice == null
				return true
			}) >> TestObjects.createSeries()
	}
	
	def "add() should pass image to image service"() {
		given:
			form.setImage(multipartFile)
		and:
			seriesDao.save(_ as Series) >> TestObjects.createSeries()
		when:
			service.add(form, user, false)
		then:
			1 * imageService.save({ MultipartFile passedFile ->
				assert passedFile == multipartFile
				return true
			}) >> TestObjects.createImage()
	}
	
	def "add() should pass image to series dao"() {
		given:
			Image expectedImage = TestObjects.createImage()
		when:
			service.add(form, user, false)
		then:
			imageService.save(_) >> expectedImage
		and:
			1 * seriesDao.save({ Series series ->
				assert !series?.images.empty
				assert series?.images.iterator().next() == expectedImage
				return true
			}) >> TestObjects.createSeries()
	}
	
	def "add() should throw exception if comment is empty"() {
		given:
			form.setComment('  ')
		when:
			service.add(form, user, true)
		then:
			thrown IllegalArgumentException
	}
	
	@Unroll
	def "add() should pass '#expectedComment' as comment to series dao if user can add comment is #canAddComment"(boolean canAddComment, String comment, String expectedComment) {
		given:
			form.setComment(comment)
		when:
			service.add(form, user, canAddComment)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.comment == expectedComment
				return true
			}) >> TestObjects.createSeries()
		where:
			canAddComment | comment     || expectedComment
			false         | null        || null
			false         | 'test'      || null
			true          | null        || null
			true          | 'Some text' || 'Some text'
	}
	
	def "add() should assign created at to current date"() {
		when:
			service.add(form, user, false)
		then:
			1 * seriesDao.save({ Series series ->
				assert DateUtils.roughlyEqual(series?.metaInfo?.createdAt, new Date())
				return true
			}) >> TestObjects.createSeries()
	}
	
	def "add() should assign updated at to current date"() {
		when:
			service.add(form, user, false)
		then:
			1 * seriesDao.save({ Series series ->
				assert DateUtils.roughlyEqual(series?.metaInfo?.updatedAt, new Date())
				return true
			}) >> TestObjects.createSeries()
	}
	
	def "add() should assign created by to user"() {
		when:
			service.add(form, user, false)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.metaInfo?.createdBy == user
				return true
			}) >> TestObjects.createSeries()
	}
	
	def "add() should assign updated by to user"() {
		when:
			service.add(form, user, false)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.metaInfo?.updatedBy == user
				return true
			}) >> TestObjects.createSeries()
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
			Category expectedCategory = TestObjects.createCategory()
		and:
			Iterable<Category> expectedResult = [ expectedCategory ]
		and:
			jdbcSeriesDao.findByCategoryIdAsSeriesInfo(_ as Integer, _ as String) >> expectedResult
		when:
			Iterable<Category> result = service.findByCategoryId(10, 'any')
		then:
			result == expectedResult
	}
	
	//
	// Tests for findBy(Country)
	//
	
	def "findBy(Country) should throw exception if country is null"() {
		when:
			service.findBy(null as Country, 'any')
		then:
			thrown IllegalArgumentException
	}
	
	def "findBy(Country) should call dao and return result"() {
		given:
			Country expectedCountry = TestObjects.createCountry()
		and:
			Iterable<Country> expectedResult = [ expectedCountry ]
		and:
			jdbcSeriesDao.findByCountryIdAsSeriesInfo(_ as Integer, _ as String) >> expectedResult
		when:
			Iterable<Country> result = service.findBy(expectedCountry, 'any')
		then:
			result == expectedResult
	}
	
	//
	// Tests for findBy(Collection)
	//
	
	def "findBy(Collection) should throw exception when collection is null"() {
		when:
			service.findBy(null as Collection, 'whatever')
		then:
			thrown IllegalArgumentException
	}
	
	def "findBy(Collection) should throw exception when collection id is null"() {
		given:
			Collection collection = Mock()
			collection.getId() >> null
		when:
			service.findBy(collection, 'whatever')
		then:
			thrown IllegalArgumentException
	}
	
	def "findBy(Collection) should pass arguments to dao"() {
		given:
			Integer expectedCollectionId = 16
		and:
			String expectedLang = 'expected'
		and:
			Collection expectedCollection = Mock()
			expectedCollection.getId() >> expectedCollectionId
		when:
			service.findBy(expectedCollection, expectedLang)
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
