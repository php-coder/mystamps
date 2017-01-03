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
package ru.mystamps.web.util

import spock.lang.Specification

@SuppressWarnings(['ClassJavadoc', 'MethodName', 'NoDef', 'NoTabCharacter', 'TrailingWhitespace'])
class CatalogUtilsTest extends Specification {
	
	//
	// Tests for toShortForm()
	//
	
	def "toShortForm() should throw exception if numbers is null"() {
		when:
			CatalogUtils.toShortForm(null)
		then:
			thrown IllegalArgumentException
	}
	
	def "toShortForm() should return empty string for empty numbers"() {
		given:
			List<String> empty = []
		when:
			String numbers = CatalogUtils.toShortForm(empty)
		then:
			numbers == ''
	}
	
	def "toShortForm() should return single number as is"() {
		given:
			List<String> singleNumber = [ '1' ]
		when:
			String numbers = CatalogUtils.toShortForm(singleNumber)
		then:
			numbers == '1'
	}
	
	def "toShortForm() should return pair of numbers as comma separated"() {
		given:
			List<String> setOfNumbers = [ '1', '2' ]
		when:
			String numbers = CatalogUtils.toShortForm(setOfNumbers)
		then:
			numbers == '1, 2'
	}
	
	def "toShortForm() should produce range for sequence"() {
		given:
			List<String> setOfNumbers = [ '1', '2', '3' ]
		when:
			String numbers = CatalogUtils.toShortForm(setOfNumbers)
		then:
			numbers == '1-3'
	}
	
	def "toShortForm() should return comma separated numbers if they are not a sequence"() {
		given:
			List<String> setOfNumbers = [ '1', '2', '4', '5' ]
		when:
			String numbers = CatalogUtils.toShortForm(setOfNumbers)
		then:
			numbers == '1, 2, 4, 5'
	}
	
	def "toShortForm() should produce two ranges for two sequences"() {
		given:
			List<String> setOfNumbers = [ '1', '2', '3', '10', '19', '20', '21' ]
		when:
			String numbers = CatalogUtils.toShortForm(setOfNumbers)
		then:
			numbers == '1-3, 10, 19-21'
	}
	
	//
	// Tests for parseCatalogNumbers()
	//
	
	def "parseCatalogNumbers() should return empty collection if catalog numbers is null"() {
		when:
			Set<String> numbers = CatalogUtils.parseCatalogNumbers(null)
		then:
			numbers.isEmpty()
	}
	
	def "parseCatalogNumbers() should return empty collection if catalog numbers is empty"() {
		when:
			Set<String> numbers = CatalogUtils.parseCatalogNumbers('')
		then:
			numbers.isEmpty()
	}
	
	def "parseCatalogNumbers() should return one element if catalog numbers contains one number"() {
		when:
			Set<String> numbers = CatalogUtils.parseCatalogNumbers('1')
		then:
			numbers.size() == 1
	}
	
	def "parseCatalogNumbers() should return one element if catalog numbers contains extra comma"() {
		when:
			Set<String> numbers = CatalogUtils.parseCatalogNumbers('1,')
		then:
			numbers.size() == 1
	}
	
	def "parseCatalogNumbers() should return two elements if catalog numbers contains two numbers"() {
		when:
			Set<String> numbers = CatalogUtils.parseCatalogNumbers('1,2')
		then:
			numbers.size() == 2
	}
	
	def "parseCatalogNumbers() should throw exception if one of catalog numbers is a blank string"() {
		when:
			CatalogUtils.parseCatalogNumbers('1, ')
		then:
			thrown IllegalStateException
	}
	
}
