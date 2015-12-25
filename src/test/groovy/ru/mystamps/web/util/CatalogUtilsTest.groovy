/**
 * Copyright (C) 2009-2015 Slava Semushin <slava.semushin@gmail.com>
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

import ru.mystamps.web.entity.MichelCatalog

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
			Set<MichelCatalog> empty = [] as Set
		when:
			String numbers = CatalogUtils.toShortForm(empty)
		then:
			numbers == ''
	}
	
	def "toShortForm() should return single number as is"() {
		given:
			Set<MichelCatalog> singleNumber = [ new MichelCatalog('1') ] as Set
		when:
			String numbers = CatalogUtils.toShortForm(singleNumber)
		then:
			numbers == '1'
	}
	
	def "toShortForm() should return pair of numbers as comma separated"() {
		given:
			Set<MichelCatalog> setOfNumbers = [
				new MichelCatalog('1'),
				new MichelCatalog('2')
			] as Set
		when:
			String numbers = CatalogUtils.toShortForm(setOfNumbers)
		then:
			numbers == '1, 2'
	}
	
	def "toShortForm() should produce range for sequence"() {
		given:
			Set<MichelCatalog> setOfNumbers = [
				new MichelCatalog('1'),
				new MichelCatalog('2'),
				new MichelCatalog('3')
			] as Set
		when:
			String numbers = CatalogUtils.toShortForm(setOfNumbers)
		then:
			numbers == '1-3'
	}
	
	def "toShortForm() should return comma separated numbers if they are not a sequence"() {
		given:
			Set<MichelCatalog> setOfNumbers = [
				new MichelCatalog('1'),
				new MichelCatalog('2'),
				new MichelCatalog('4'),
				new MichelCatalog('5')
			] as Set
		when:
			String numbers = CatalogUtils.toShortForm(setOfNumbers)
		then:
			numbers == '1, 2, 4, 5'
	}
	
	def "toShortForm() should produce two ranges for two sequences"() {
		given:
			Set<MichelCatalog> setOfNumbers = [
				new MichelCatalog('1'),
				new MichelCatalog('2'),
				new MichelCatalog('3'),
				new MichelCatalog('10'),
				new MichelCatalog('19'),
				new MichelCatalog('20'),
				new MichelCatalog('21')
			] as Set
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
