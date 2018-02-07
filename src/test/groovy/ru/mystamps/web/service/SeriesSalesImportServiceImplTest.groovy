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

import org.slf4j.helpers.NOPLogger

import spock.lang.Specification

import ru.mystamps.web.dao.SeriesSalesImportDao
import ru.mystamps.web.dao.dto.SeriesSaleParsedDataDto
import ru.mystamps.web.dao.dto.SeriesSalesParsedDataDbDto
import ru.mystamps.web.tests.Random

@SuppressWarnings(['ClassJavadoc', 'MethodName', 'NoDef', 'NoTabCharacter', 'TrailingWhitespace'])
class SeriesSalesImportServiceImplTest extends Specification {
	
	private final SeriesSalesImportDao seriesSalesImportDao = Mock()
	
	private SeriesSalesImportService service
	
	def setup() {
		service = new SeriesSalesImportServiceImpl(NOPLogger.NOP_LOGGER, seriesSalesImportDao)
	}
	
	//
	// Tests for saveParsedData()
	//
	
	def 'saveParsedData() should throw exception when request id is null'() {
		when:
			service.saveParsedData(null, TestObjects.createSeriesSalesParsedDataDbDto())
		then:
			IllegalArgumentException ex = thrown()
			ex.message == 'Request id must be non null'
	}
	
	def 'saveParsedData() should throw exception when parsed data is null'() {
		when:
			service.saveParsedData(Random.id(), null)
		then:
			IllegalArgumentException ex = thrown()
			ex.message == 'Parsed data must be non null'
	}
	
	def 'saveParsedData() should save series sales parsed data'() {
		given:
			Integer expectedRequestId = Random.id()
			SeriesSalesParsedDataDbDto expectedParsedData = TestObjects.createSeriesSalesParsedDataDbDto()
		when:
			service.saveParsedData(expectedRequestId, expectedParsedData)
		then:
			1 * seriesSalesImportDao.addParsedData(expectedRequestId, expectedParsedData)
	}
	
	//
	// Tests for getParsedData()
	//
	
	def 'getParsedData() should throw exception when request id is null'() {
		when:
			service.getParsedData(null)
		then:
			IllegalArgumentException ex = thrown()
			ex.message == 'Request id must be non null'
	}
	
	def 'getParsedData() should invoke dao and return its result'() {
		given:
			Integer expectedRequestId = Random.id()
			SeriesSaleParsedDataDto expectedResult = TestObjects.createSeriesSaleParsedDataDto()
		when:
			SeriesSaleParsedDataDto result = service.getParsedData(expectedRequestId)
		then:
			1 * seriesSalesImportDao.findParsedDataByRequestId(expectedRequestId) >> expectedResult
		and:
			result == expectedResult
	}
	
}
