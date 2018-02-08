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

import static io.qala.datagen.RandomShortApi.nullOr

import org.slf4j.helpers.NOPLogger

import spock.lang.Specification

import ru.mystamps.web.service.dto.RawParsedDataDto
import ru.mystamps.web.service.dto.SeriesExtractedInfo

@SuppressWarnings(['ClassJavadoc', 'MethodName', 'NoDef', 'NoTabCharacter', 'TrailingWhitespace'])
class TimedSeriesInfoExtractorServiceTest extends Specification {
	
	private final SeriesInfoExtractorService origService = Mock()
	
	private SeriesInfoExtractorService service
	
	def setup() {
		service = new TimedSeriesInfoExtractorService(NOPLogger.NOP_LOGGER, origService)
	}
	
	//
	// Tests for extract()
	//
	
	def 'extract() should invoke original service and return its result'() {
		given:
			RawParsedDataDto expectedParsedData = TestObjects.createRawParsedDataDto()
			SeriesExtractedInfo expectedResult = nullOr(TestObjects.createSeriesExtractedInfo())
		when:
			SeriesExtractedInfo result = service.extract(expectedParsedData)
		then:
			1 * origService.extract(expectedParsedData) >> expectedResult
		and:
			result == expectedResult
	}
	
}
