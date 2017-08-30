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

import org.slf4j.helpers.NOPLogger

import spock.lang.Specification
import spock.lang.Unroll

@SuppressWarnings(['ClassJavadoc', 'MethodName', 'NoDef', 'NoTabCharacter', 'TrailingWhitespace'])
class TimedImagePreviewStrategyTest extends Specification {
	
	private final ImagePreviewStrategy origStrategy = Mock()
	
	private ImagePreviewStrategy strategy
	
	def setup() {
		strategy = new TimedImagePreviewStrategy(NOPLogger.NOP_LOGGER, origStrategy)
	}
	
	//
	// Tests for createPreview()
	//
	
	@Unroll
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'FactoryMethodName', 'UnnecessaryReturnKeyword'])
	def 'createPreview() should pass #expectedData and return result of original strategy'(byte[] expectedData) {
		given:
			byte[] expectedResult = 'foobar'.bytes
		when:
			byte[] result = strategy.createPreview(expectedData)
		then:
			1 * origStrategy.createPreview({ byte[] image ->
				image == expectedData
			return true
			}) >> expectedResult
		and:
			result == expectedResult
		where:
			expectedData | _
			null         | _
			'foo'.bytes  | _
	}
	
}
