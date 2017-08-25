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

import org.slf4j.helpers.NOPLogger

import spock.lang.Specification

import ru.mystamps.web.dao.StampsCatalogDao

@SuppressWarnings(['ClassJavadoc', 'MethodName', 'NoDef', 'NoTabCharacter', 'TrailingWhitespace'])
class StampsCatalogServiceImplTest extends Specification {
	
	private final StampsCatalogDao stampsCatalogDao = Mock()
	
	private final StampsCatalogService service = new StampsCatalogServiceImpl(
		NOPLogger.NOP_LOGGER,
		'TestCatalog',
		stampsCatalogDao
	)
	
	//
	// Tests for add()
	//
	
	def "add() should throw exception when numbers is null"() {
		when:
			service.add(null)
		then:
			thrown IllegalArgumentException
	}
	
	def "add() should throw exception when numbers is empty"() {
		when:
			service.add([] as Set)
		then:
			thrown IllegalArgumentException
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "add() should add catalog numbers"() {
		given:
			Set<String> expectedNumbers = [ '8', '9' ] as Set
		when:
			service.add(expectedNumbers)
		then:
			1 * stampsCatalogDao.add({ Set<String> numbers ->
				assert numbers == expectedNumbers
				return true
			}) >> [ '8' ]
	}
	
	//
	// Tests for addToSeries()
	//
	
	def "addToSeries() should throw exception when series id is null"() {
		when:
			service.addToSeries(null, [ '1', '2' ] as Set)
		then:
			thrown IllegalArgumentException
	}
	
	def "addToSeries() should throw exception when numbers is null"() {
		when:
			service.addToSeries(123, null)
		then:
			thrown IllegalArgumentException
	}
	
	def "addToSeries() should throw exception when numbers is empty"() {
		when:
			service.addToSeries(123, [] as Set)
		then:
			thrown IllegalArgumentException
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "addToSeries() should add catalog numbers to series"() {
		given:
			Integer expectedSeriesId = 100
			Set<String> expectedNumbers = [ '8', '9' ] as Set
		when:
			service.addToSeries(expectedSeriesId, expectedNumbers)
		then:
			1 * stampsCatalogDao.addToSeries({ Integer seriesId ->
				assert seriesId == expectedSeriesId
				return true
			}, { Set<String> numbers ->
				assert numbers == expectedNumbers
				return true
			})
	}
	
	//
	// Tests for findBySeriesId()
	//
	
	def "findBySeriesId() should throw exception when series id is null"() {
		when:
			service.findBySeriesId(null)
		then:
			thrown IllegalArgumentException
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "findBySeriesId() should invoke dao, pass argument and return result from dao"() {
		given:
			Integer expectedSeriesId = 50
		and:
			List<String> expectedResult = [ '75', '76' ]
		when:
			List<String> result = service.findBySeriesId(expectedSeriesId)
		then:
			1 * stampsCatalogDao.findBySeriesId({ Integer seriesId ->
				assert seriesId == expectedSeriesId
				return true
			}) >> expectedResult
		and:
			result == expectedResult
	}
	
}
