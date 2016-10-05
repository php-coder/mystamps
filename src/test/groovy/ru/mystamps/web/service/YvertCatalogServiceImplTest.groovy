/**
 * Copyright (C) 2009-2016 Slava Semushin <slava.semushin@gmail.com>
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

import spock.lang.Specification

import ru.mystamps.web.dao.StampsCatalogDao

@SuppressWarnings(['ClassJavadoc', 'MethodName', 'NoDef', 'NoTabCharacter', 'TrailingWhitespace'])
class YvertCatalogServiceImplTest extends Specification {
	
	private final StampsCatalogDao yvertCatalogDao = Mock()
	private final StampsCatalogService service = new YvertCatalogServiceImpl(yvertCatalogDao)
	
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
			Integer expectedSeriesId = 51
		and:
			List<String> expectedResult = [ '77', '78' ]
		when:
			List<String> result = service.findBySeriesId(expectedSeriesId)
		then:
			1 * yvertCatalogDao.findBySeriesId({ Integer seriesId ->
				assert seriesId == expectedSeriesId
				return true
			}) >> expectedResult
		and:
			result == expectedResult
	}
	
}
