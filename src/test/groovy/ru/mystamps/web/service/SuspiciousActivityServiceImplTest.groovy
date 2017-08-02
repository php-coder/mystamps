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
import spock.lang.Unroll

import ru.mystamps.web.dao.SuspiciousActivityDao
import ru.mystamps.web.dao.dto.SuspiciousActivityDto

class SuspiciousActivityServiceImplTest extends Specification {
	
	private SuspiciousActivityDao suspiciousActivityDao = Mock()
	
	private SuspiciousActivityService service = new SuspiciousActivityServiceImpl(suspiciousActivityDao)
	
	//
	// Tests for countAll()
	//
	
	def "countAll() should invoke dao and return its result"() {
		given:
			long expectedResult = 21
		when:
			long result = service.countAll()
		then:
			1 * suspiciousActivityDao.countAll() >> expectedResult
		and:
			result == expectedResult
	}
	
	//
	// Tests for countByTypeSince()
	//
	
	@Unroll
	def "countByTypeSince() should throw exception when type = '#type'"(String type) {
		when:
			service.countByTypeSince(type, new Date())
		then:
			thrown IllegalArgumentException
		where:
			type | _
			null | _
			''   | _
			'  ' | _
	}
	
	def "countByTypeSince() should throw exception when date is null"() {
		when:
			service.countByTypeSince('AnyType', null)
		then:
			thrown IllegalArgumentException
	}
	
	def "countByTypeSince() should invoke dao, pass arguments and return result from dao"() {
		given:
			String expectedType = 'ExpectedType'
			Date expectedDate = new Date().plus(1)
			long expectedResult = 47
		when:
			long result = service.countByTypeSince(expectedType, expectedDate)
		then:
			1 * suspiciousActivityDao.countByTypeSince({ String type ->
				assert type == expectedType
				return true
			}, { Date date ->
				assert date == expectedDate
				return true
			}) >> expectedResult
		and:
			result == expectedResult
	}
	
	//
	// Tests for findSuspiciousActivities()
	//
	
	@Unroll
	def "findSuspiciousActivities() should throw exception when page = #page"(int page) {
		when:
			service.findSuspiciousActivities(page, 10)
		then:
			thrown IllegalArgumentException
		where:
			page | _
			-1   | _
			0    | _
	}
	
	@Unroll
	def "findSuspiciousActivities() should throw exception when recordsPerPage = #recordsPerPage"(int recordsPerPage) {
		when:
			service.findSuspiciousActivities(1, recordsPerPage)
		then:
			thrown IllegalArgumentException
		where:
			recordsPerPage | _
			-1             | _
			0              | _
	}
	
	def "findSuspiciousActivities() should invoke dao and return result from dao"() {
		given:
			int expectedPage = 5
			int expectedRecordsPerPage = 10
			List<SuspiciousActivityDto> expectedResult = [ TestObjects.createSuspiciousActivityDto() ] as List
		when:
			List<SuspiciousActivityDto> result =
				service.findSuspiciousActivities(expectedPage, expectedRecordsPerPage)
		then:
			1 * suspiciousActivityDao.findAll({ int page ->
				assert page == expectedPage
				return true
			}, { int recordsPerPage ->
				assert recordsPerPage == expectedRecordsPerPage
				return true
			}) >> expectedResult
		and:
			result == expectedResult
	}
	
}
