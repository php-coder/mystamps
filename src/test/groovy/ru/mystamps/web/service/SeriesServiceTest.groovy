/*
 * Copyright (C) 2009-2013 Slava Semushin <slava.semushin@gmail.com>
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

import org.apache.commons.lang3.StringUtils

import org.springframework.web.multipart.MultipartFile

import spock.lang.Specification

import ru.mystamps.web.dao.SeriesDao
import ru.mystamps.web.entity.Country
import ru.mystamps.web.entity.Currency
import ru.mystamps.web.entity.GibbonsCatalog
import ru.mystamps.web.entity.MichelCatalog
import ru.mystamps.web.entity.ScottCatalog
import ru.mystamps.web.entity.Series
import ru.mystamps.web.entity.User
import ru.mystamps.web.entity.YvertCatalog
import ru.mystamps.web.model.AddSeriesForm
import ru.mystamps.web.tests.DateUtils

public class SeriesServiceTest extends Specification {
	private static final Double ANY_PRICE = 17d
	
	private ImageService imageService = Mock()
	private SeriesDao seriesDao = Mock()
	private MultipartFile multipartFile = Mock()
	
	private SeriesService service
	private AddSeriesForm form
	private User user
	
	def setup() {
		form = new AddSeriesForm()
		form.setQuantity(2)
		form.setPerforated(false)
		
		user = TestObjects.createUser()
		
		imageService.save(_) >> "/fake/path/to/image"
		
		service = new SeriesServiceImpl(seriesDao, imageService)
	}
	
	//
	// Tests for add()
	//
	
	def "add() should throw exception argument is null"() {
		when:
			service.add(null, user)
		then:
			thrown IllegalArgumentException
	}
	
	def "add() should throw exception if quantity is null"() {
		given:
			form.setQuantity(null)
		when:
			service.add(form, user)
		then:
			thrown IllegalArgumentException
	}
	
	def "add() should throw exception if perforated is null"() {
		given:
			form.setPerforated(null)
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
	
	def "add() should pass entity to series dao"() {
		when:
			service.add(form, user)
		then:
			1 * seriesDao.save(_ as Series)
	}
	
	def "add() should load and pass country to series dao if country present"() {
		given:
			Country expectedCountry = TestObjects.createCountry()
			form.setCountry(expectedCountry)
		when:
			service.add(form, user)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.country == expectedCountry
				return true
			})
	}
	
	def "add() should pass date with specified year to series dao if year present"() {
		given:
			int expectedYear = 2000
			Date passedDate = null
			form.setYear(expectedYear)
		when:
			service.add(form, user)
		then:
			1 * seriesDao.save({ Series series ->
				passedDate = series?.releasedAt
				return true
			})
		and:
			Calendar cal = GregorianCalendar.getInstance()
			cal.setTime(passedDate)
			int actualYear = cal.get(Calendar.YEAR)
			
			actualYear == expectedYear
	}
	
	def "add() should pass quantity to series dao"() {
		given:
			Integer expectedQuantity = 3
			form.setQuantity(expectedQuantity)
		when:
			service.add(form, user)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.quantity == expectedQuantity
				return true
			})
	}
	
	def "add() should pass perforated to series dao"() {
		given:
			Boolean expectedResult = true
			form.setPerforated(expectedResult)
		when:
			service.add(form, user)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.perforated == expectedResult
				return true
			})
	}

	def "add() should pass null to series dao if michel numbers is null"() {
		given:
			assert form.getMichelNumbers() == null
		when:
			service.add(form, user)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.michel == null
				return true
			})
	}
	
	def "add() should pass michel numbers to series dao"() {
		given:
			Set<MichelCatalog> expectedNumbers = newSet(
				new MichelCatalog("1"),
				new MichelCatalog("2")
			)
			form.setMichelNumbers(StringUtils.join(expectedNumbers, ','))
		when:
			service.add(form, user)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.michel == expectedNumbers
				return true
			})
	}
	
	def "add() should throw exception if michel price specified without currency"() {
		given:
			form.setMichelPrice(ANY_PRICE)
		and:
			form.setMichelCurrency(null)
		when:
			service.add(form, user)
		then:
			thrown IllegalArgumentException
	}
	
	def "add() should pass michel price to series dao"() {
		given:
			Double expectedPrice = ANY_PRICE
			form.setMichelPrice(expectedPrice)
		and:
			Currency expectedCurrency = Currency.RUR
			form.setMichelCurrency(expectedCurrency)
		when:
			service.add(form, user)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.michelPrice?.value == expectedPrice
				assert series?.michelPrice?.currency == expectedCurrency
				return true
			})
	}
	
	def "add() should pass null to series dao if michel price is null"() {
		given:
			form.setMichelPrice(null)
		and:
			form.setMichelCurrency(null)
		when:
			service.add(form, user)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.michelPrice == null
				return true
			})
	}
	
	def "add() should pass null to series dao if scott numbers is null"() {
		given:
			assert form.getScottNumbers() == null
		when:
			service.add(form, user)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.scott == null
				return true
			})
	}
	
	def "add() should pass scott numbers to series dao"() {
		given:
			Set<ScottCatalog> expectedNumbers = newSet(
				new ScottCatalog("1"),
				new ScottCatalog("2")
			)
			form.setScottNumbers(StringUtils.join(expectedNumbers, ','))
		when:
			service.add(form, user)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.scott == expectedNumbers
				return true
			})
	}
	
	def "add() should throw exception if scott price specified without currency"() {
		given:
			form.setScottPrice(ANY_PRICE)
		and:
			form.setScottCurrency(null)
		when:
			service.add(form, user)
		then:
			thrown IllegalArgumentException
	}
	
	def "add() should pass scott price to series dao"() {
		given:
			Double expectedPrice = ANY_PRICE
			form.setScottPrice(expectedPrice)
		and:
			Currency expectedCurrency = Currency.RUR
			form.setScottCurrency(expectedCurrency)
		when:
			service.add(form, user)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.scottPrice?.value == expectedPrice
				assert series?.scottPrice?.currency == expectedCurrency
				return true
			})
	}
	
	def "add() should pass null to series dao if scott price is null"() {
		given:
			form.setScottPrice(null)
		and:
			form.setScottCurrency(null)
		when:
			service.add(form, user)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.scottPrice == null
				return true
			})
	}
	
	def "add() should pass null to series dao if yvert numbers is null"() {
		given:
			assert form.getYvertNumbers() == null
		when:
			service.add(form, user)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.yvert == null
				return true
			})
	}
	
	def "add() should pass yvert numbers to series dao"() {
		given:
			Set<YvertCatalog> expectedNumbers = newSet(
				new YvertCatalog("1"),
				new YvertCatalog("2")
			)
			form.setYvertNumbers(StringUtils.join(expectedNumbers, ','))
		when:
			service.add(form, user)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.yvert == expectedNumbers
				return true
			})
	}
	
	def "add() should throw exception if yvert price specified without currency"() {
		given:
			form.setYvertPrice(ANY_PRICE)
		and:
			form.setYvertCurrency(null)
		when:
			service.add(form, user)
		then:
			thrown IllegalArgumentException
	}
	
	def "add() should pass yvert price to series dao"() {
		given:
			Double expectedPrice = ANY_PRICE
			form.setYvertPrice(expectedPrice)
		and:
			Currency expectedCurrency = Currency.RUR
			form.setYvertCurrency(expectedCurrency)
		when:
			service.add(form, user)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.yvertPrice?.value == expectedPrice
				assert series?.yvertPrice?.currency == expectedCurrency
				return true
			})
	}
	
	def "add() should pass null to series dao if yvert price is null"() {
		given:
			form.setYvertPrice(null)
		and:
			form.setYvertCurrency(null)
		when:
			service.add(form, user)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.yvertPrice == null
				return true
			})
	}
	
	def "add() should pass null to series dao if gibbons numbers is null"() {
		given:
			assert form.getGibbonsNumbers() == null
		when:
			service.add(form, user)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.gibbons == null
				return true
			})
	}
	
	def "add() should pass gibbons numbers to series dao"() {
		given:
			Set<GibbonsCatalog> expectedNumbers = newSet(
				new GibbonsCatalog("1"),
				new GibbonsCatalog("2")
			)
			form.setGibbonsNumbers(StringUtils.join(expectedNumbers, ','))
		when:
			service.add(form, user)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.gibbons == expectedNumbers
				return true
			})
	}
	
	def "add() should throw exception if gibbons price specified without currency"() {
		given:
			form.setGibbonsPrice(ANY_PRICE)
		and:
			form.setGibbonsCurrency(null)
		when:
			service.add(form, user)
		then:
			thrown IllegalArgumentException
	}
	
	def "add() should pass gibbons price to series dao"() {
		given:
			Double expectedPrice = ANY_PRICE
			form.setGibbonsPrice(expectedPrice)
		and:
			Currency expectedCurrency = Currency.RUR
			form.setGibbonsCurrency(expectedCurrency)
		when:
			service.add(form, user)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.gibbonsPrice?.value == expectedPrice
				assert series?.gibbonsPrice?.currency == expectedCurrency
				return true
			})
	}
	
	def "add() should pass null to series dao if gibbons price is null"() {
		given:
			form.setGibbonsPrice(null)
		and:
			form.setGibbonsCurrency(null)
		when:
			service.add(form, user)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.gibbonsPrice == null
				return true
			})
	}
	
	def "add() should pass image to image service"() {
		given:
			form.setImage(multipartFile)
		when:
			service.add(form, user)
		then:
			1 * imageService.save({ MultipartFile passedFile ->
				assert passedFile == multipartFile
				return true
			}) >> "/any/path"
	}
	
	def "add() should throw exception if image url is null"() {
		when:
			service.add(form, user)
		then:
			// override setup() settings
			imageService.save(_) >> null
		and:
			thrown IllegalStateException
	}
	
	def "add() should throw exception if image url too long"() {
		when:
			service.add(form, user)
		then:
			// override setup() settings
			imageService.save(_) >> "x" * (Series.IMAGE_URL_LENGTH + 1)
		and:
			thrown IllegalStateException
	}
	
	def "add() should pass image url to series dao"() {
		given:
			String expectedUrl = "http://example.org/example.jpg"
		when:
			service.add(form, user)
		then:
			imageService.save(_) >> expectedUrl
		and:
			1 * seriesDao.save({ Series series ->
				assert series?.imageUrl == expectedUrl
				return true
			})
	}
	
	def "add() should throw exception if comment is empty"() {
		given:
			form.setComment("  ")
		when:
			service.add(form, user)
		then:
			thrown IllegalArgumentException
	}
	
	def "add() should pass comment to series dao if present"() {
		given:
			String expectedComment = "Some text"
			form.setComment(expectedComment)
		when:
			service.add(form, user)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.comment == expectedComment
				return true
			})
	}
	
	def "add() should assign created at to current date"() {
		when:
			service.add(form, user)
		then:
			1 * seriesDao.save({ Series series ->
				assert DateUtils.roughlyEqual(series?.metaInfo?.createdAt, new Date())
				return true
			})
	}
	
	def "add() should assign updated at to current date"() {
		when:
			service.add(form, user)
		then:
			1 * seriesDao.save({ Series series ->
				assert DateUtils.roughlyEqual(series?.metaInfo?.updatedAt, new Date())
				return true
			})
	}
	
	def "add() should assign created by to user"() {
		when:
			service.add(form, user)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.metaInfo?.createdBy == user
				return true
			})
	}
	
	def "add() should assign updated by to user"() {
		when:
			service.add(form, user)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.metaInfo?.updatedBy == user
				return true
			})
	}
	
	private static <T> Set<T> newSet(T... elements) {
		Set<T> result = new LinkedHashSet<T>()
		
		for (T element : elements) {
			result.add(element)
		}
		
		return result
	}
	
}
