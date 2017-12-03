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

import static io.qala.datagen.RandomShortApi.nullOrBlank

import org.slf4j.helpers.NOPLogger

import spock.lang.Specification

@SuppressWarnings(['ClassJavadoc', 'MethodName', 'NoDef', 'NoTabCharacter', 'TrailingWhitespace'])
class SeriesInfoExtractorServiceImplTest extends Specification {
	
	private final CategoryService categoryService = Mock()
	private final CountryService countryService = Mock()
	private final SeriesInfoExtractorService service = new SeriesInfoExtractorServiceImpl(
		NOPLogger.NOP_LOGGER,
		categoryService,
		countryService
	)
	
	//
	// Tests for extractCategory()
	//
	
	def 'extractCategory() should return empty result when fragment is null, empty or blank'() {
		given:
			String fragment = nullOrBlank()
		when:
			List<Integer> result = service.extractCategory(fragment)
		then:
			result.isEmpty()
	}
	
	//
	// Tests for extractCountry()
	//
	
	def 'extractCountry() should return empty result when fragment is null, empty or blank'() {
		given:
			String fragment = nullOrBlank()
		when:
			List<Integer> result = service.extractCountry(fragment)
		then:
			result.isEmpty()
	}
	
	//
	// Tests for extractReleaseYear()
	//
	
	def 'extractReleaseYear() should return null when fragment is null, empty or blank'() {
		given:
			String fragment = nullOrBlank()
		when:
			Integer year = service.extractReleaseYear(fragment)
		then:
			year == null
	}
	
}
