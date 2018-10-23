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
package ru.mystamps.web.feature.series.sale

import org.slf4j.helpers.NOPLogger

import spock.lang.Specification
import spock.lang.Unroll

import ru.mystamps.web.dao.SeriesSalesDao
import ru.mystamps.web.dao.dto.AddSeriesSalesDbDto
import ru.mystamps.web.dao.dto.Currency
import ru.mystamps.web.tests.DateUtils

@SuppressWarnings(['ClassJavadoc', 'MethodName', 'NoDef', 'NoTabCharacter', 'TrailingWhitespace'])
class SeriesSalesServiceImplTest extends Specification {
	
	private final SeriesSalesDao seriesSalesDao = Mock()
	private final SeriesSalesService service = new SeriesSalesServiceImpl(
		NOPLogger.NOP_LOGGER,
		seriesSalesDao
	)
	
	private AddSeriesSalesForm form
	
	def setup() {
		form = new AddSeriesSalesForm()
		form.setSellerId(7)
		form.setPrice(new BigDecimal('14'))
		form.setCurrency(Currency.EUR)
	}
	
	//
	// Tests for add()
	//
	
	def 'add() should throw exception when dto is null'() {
		when:
			service.add(null, 123, 456)
		then:
			thrown IllegalArgumentException
	}
	
	def 'add() should throw exception when seller id is null'() {
		given:
			form.setSellerId(null)
		when:
			service.add(form, 123, 456)
		then:
			thrown IllegalArgumentException
	}
	
	def 'add() should throw exception when price is null'() {
		given:
			form.setPrice(null)
		when:
			service.add(form, 123, 456)
		then:
			thrown IllegalArgumentException
	}
	
	def 'add() should throw exception when currency is null'() {
		given:
			form.setCurrency(null)
		when:
			service.add(form, 123, 456)
		then:
			thrown IllegalArgumentException
	}
	
	def 'add() should throw exception when series id is null'() {
		when:
			service.add(form, null, 456)
		then:
			thrown IllegalArgumentException
	}
	
	def 'add() should throw exception when user id is null'() {
		when:
			service.add(form, 123, null)
		then:
			thrown IllegalArgumentException
	}
	
	@Unroll
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryObjectReferences', 'UnnecessaryReturnKeyword'])
	def 'add() should create series sales'(
		Date date,
		String url,
		BigDecimal altPrice, Currency altCurrency,
		Integer buyerId) {
		
		given:
			// mandatory fields
			form.setSellerId(444)
			form.setPrice(new BigDecimal('100'))
			form.setCurrency(Currency.EUR)
			// optional fields
			form.setDate(date)
			form.setUrl(url)
			form.setAltPrice(altPrice)
			form.setAltCurrency(altCurrency)
			form.setBuyerId(buyerId)
		and:
			Integer expectedSeriesId = 777
			Integer expectedUserId = 888
		when:
			service.add(form, expectedSeriesId, expectedUserId)
		then:
			1 * seriesSalesDao.add({ AddSeriesSalesDbDto dto ->
				assert dto?.date        == form.date
				assert dto?.sellerId    == form.sellerId
				assert dto?.url         == form.url
				assert dto?.price       == form.price
				assert dto?.currency    == form.currency?.toString()
				assert dto?.altPrice    == form.altPrice
				assert dto?.altCurrency == form.altCurrency?.toString()
				assert dto?.buyerId     == form.buyerId
				assert dto?.createdBy   == expectedUserId
				assert dto?.seriesId    == expectedSeriesId
				assert DateUtils.roughlyEqual(dto?.createdAt, new Date())
				return true
			})
		where:
			date            | url           | altPrice               | altCurrency  | buyerId
			null            | null          | null                   | null         | null
			new Date() - 10 | 'example.com' | new BigDecimal('6200') | Currency.RUB | 555
	}
	
}
