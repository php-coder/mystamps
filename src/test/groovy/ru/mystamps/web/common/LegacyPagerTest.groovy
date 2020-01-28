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
package ru.mystamps.web.common

import spock.lang.Specification
import spock.lang.Unroll

@SuppressWarnings(['ClassJavadoc', 'MethodName', 'NoDef', 'NoTabCharacter', 'TrailingWhitespace'])
class LegacyPagerTest extends Specification {
	
	//
	// Tests for Pager()
	//
	
	def "Pager() should throw exception when total records is too big"() {
		when:
			new Pager(Long.MAX_VALUE, 10, 1)
		then:
			ArithmeticException ex = thrown()
			ex.message == 'integer overflow'
	}
	
	def "Pager() should throw exception when total records is less than 0"() {
		when:
			new Pager(-1, 10, 1)
		then:
			IllegalArgumentException ex = thrown()
			ex.message == 'Total records must be greater than or equal to zero'
	}
	
	@Unroll
	def "Pager() should throw exception when records per page (#recordsPerPage) is less than 1"(int recordsPerPage) {
		when:
			new Pager(10, recordsPerPage, 1)
		then:
			IllegalArgumentException ex = thrown()
			ex.message == 'Records per page must be greater than zero'
		where:
			recordsPerPage | _
			-1             | _
			0              | _
	}
	
	@Unroll
	def "Pager() should throw exception when current page (#currentPage) is less than 1"(int currentPage) {
		when:
			new Pager(10, 10, currentPage)
		then:
			IllegalArgumentException ex = thrown()
			ex.message == 'Current page must be greater than zero'
		where:
			currentPage | _
			-1          | _
			0           | _
	}
	
	//
	// Tests for getCurrentPage()
	//
	
	@Unroll
	def "getCurrentPage() should return #currentPage"(int currentPage) {
		when:
			Pager pager = new Pager(10, 1, currentPage)
		then:
			pager.currentPage == currentPage
		where:
			currentPage | _
			1           | _
			2           | _
	}
	
	//
	// Tests for getItems()
	//
	
	@Unroll
	def "getItems() should handle [ ] 1 [ ] and totalRecords = #totalRecords"(int totalRecords) {
		when:
			Pager pager = new Pager(totalRecords, 10, 1)
		then:
			pager.items == [ 1 ]
		where:
			totalRecords | _
			0            | _
			1            | _
			9            | _
			10           | _
	}
	
	@Unroll
	def "getItems() should handle [ ] 1 2 [ ] and currentPage = #currentPage"(int currentPage) {
		when:
			Pager pager = new Pager(20, 10, currentPage)
		then:
			pager.items == [ 1, 2 ]
		where:
			currentPage | _
			1           | _
			2           | _
	}
	
	@Unroll
	def "getItems() should handle [ ] 1 2 3 [ ] and currentPage = #currentPage"(int currentPage) {
		when:
			Pager pager = new Pager(30, 10, currentPage)
		then:
			pager.items == [ 1, 2, 3 ]
		where:
			currentPage | _
			1           | _
			2           | _
			3           | _
	}
	
	@Unroll
	def "getItems() should handle [ ] 1 2 3 4 [ ] and currentPage = #currentPage"(int currentPage) {
		when:
			Pager pager = new Pager(40, 10, currentPage)
		then:
			pager.items == [ 1, 2, 3, 4 ]
		where:
			currentPage | _
			1           | _
			2           | _
			3           | _
			4           | _
	}
	
	@Unroll
	def "getItems() should handle [ ] 1 2 3 4 5 [ ] and currentPage = #currentPage"(int currentPage) {
		when:
			Pager pager = new Pager(50, 10, currentPage)
		then:
			pager.items == [ 1, 2, 3, 4, 5 ]
		where:
			currentPage | _
			1           | _
			2           | _
			3           | _
			4           | _
			5           | _
	}
	
	@Unroll
	def "getItems() should handle [ ] 1 2 3 4 5 [>>] and currentPage = #currentPage"(int currentPage) {
		when:
			Pager pager = new Pager(51, 10, currentPage)
		then:
			pager.items == [ 1, 2, 3, 4, 5 ]
		where:
			currentPage | _
			1           | _
			2           | _
			3           | _
	}
	
	def "getItems() should handle [<<] 2 3 4 5 6 [>>]"() {
		when:
			Pager pager = new Pager(90, 10, 4)
		then:
			pager.items == [ 2, 3, 4, 5, 6 ]
	}
	
	@Unroll
	def "getItems() should handle [<<] 2 3 4 5 6 [ ] and currentPage = #currentPage"(int currentPage) {
		when:
			Pager pager = new Pager(60, 10, currentPage)
		then:
			pager.items == [ 2, 3, 4, 5, 6 ]
		where:
			currentPage | _
			6           | _
			5           | _
			4           | _
	}
	
	//
	// Tests for getPrev()
	//
	
	@Unroll
	@SuppressWarnings('UnnecessaryBooleanExpression') // false positive
	def "getPrev() should return #prev for when page = #currentPage"(int currentPage, Integer prev) {
		when:
			Pager pager = new Pager(3, 1, currentPage)
		then:
			pager.prev == prev
		where:
			currentPage || prev
			1           || null
			2           || 1
			3           || 2
	}
	
	//
	// Tests for getNext()
	//
	
	@Unroll
	@SuppressWarnings('UnnecessaryBooleanExpression') // false positive
	def "getNext() should return #next for when page = #currentPage"(int currentPage, Integer next) {
		when:
			Pager pager = new Pager(3, 1, currentPage)
		then:
			pager.next == next
		where:
			currentPage || next
			1           || 2
			2           || 3
			3           || null
	}
	
	def "getNext() should return null when total records less than records per page"() {
		when:
			Pager pager = new Pager(2, 25, 1)
		then:
			pager.next == null
	}
	
}
