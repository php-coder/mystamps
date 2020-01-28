/**
 * Copyright (C) 2009-2020 Slava Semushin <slava.semushin@gmail.com>
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
import ru.mystamps.web.service.TestObjects
import ru.mystamps.web.tests.Random
import spock.lang.Specification

import static io.qala.datagen.RandomShortApi.nullOr

@SuppressWarnings([
	'ClassJavadoc',
	'MethodName',
	'MisorderedStaticImports',
	'NoDef',
	'NoTabCharacter',
	'TrailingWhitespace',
])
class LegacyTimedSeriesInfoExtractorServiceTest extends Specification {
	
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
			String expectedPageUrl = Random.url()
			RawParsedDataDto expectedParsedData = TestObjects.createRawParsedDataDto()
			SeriesExtractedInfo expectedResult = nullOr(TestObjects.createSeriesExtractedInfo())
		when:
			SeriesExtractedInfo result = service.extract(expectedPageUrl, expectedParsedData)
		then:
			1 * origService.extract(expectedPageUrl, expectedParsedData) >> expectedResult
		and:
			result == expectedResult
	}
	
}
