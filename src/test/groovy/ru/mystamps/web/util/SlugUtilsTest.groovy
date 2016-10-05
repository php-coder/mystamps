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
package ru.mystamps.web.util

import spock.lang.Specification
import spock.lang.Unroll

@SuppressWarnings(['ClassJavadoc', 'MethodName', 'NoDef', 'NoTabCharacter', 'TrailingWhitespace'])
class SlugUtilsTest extends Specification {
	
	//
	// Tests for slugify()
	//
	
	def "slugify() should throw exception when argument is null"() {
		when:
			SlugUtils.slugify(null)
		then:
			thrown IllegalArgumentException
	}
	
	@Unroll
	@SuppressWarnings('UnnecessaryBooleanExpression') // false positive
	def "slugify() should transform text '#input' to '#output'"(String input, String output) {
		when:
			String result = SlugUtils.slugify(input)
		then:
			result == output
		where:
			input      || output
			''         || ''
			'-_'       || ''
			'тест'     || ''
			'test'     || 'test'
			'TEST'     || 'test'
			'test!'    || 'test'
			'_test_'   || 'test'
			'foo3'     || 'foo3'
			'one two'  || 'one-two'
			'one+two'  || 'one-two'
			'one||two' || 'one-two'
	}
	
}
