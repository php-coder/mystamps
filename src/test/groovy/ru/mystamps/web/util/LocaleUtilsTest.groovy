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

class LocaleUtilsTest extends Specification {
	
	//
	// Tests for getLanguageOrNull()
	//
	
	@Unroll
	def "getLanguageOrNull() should extract language '#language' from locale '#locale'"(Locale locale, String language) {
		when:
			String result = LocaleUtils.getLanguageOrNull(locale)
		then:
			result == language
		where:
			locale                 || language
			null                   || null
			Locale.ENGLISH         || 'en'
			new Locale('ru', 'RU') || 'ru'
	}
	
	//
	// Tests for getLanguageOrDefault()
	//
	
	@Unroll
	def "getLanguageOrDefault() should returns '#expected' for #locale/#value"(Locale locale, String value, String expected) {
		when:
			String result = LocaleUtils.getLanguageOrDefault(locale, value)
		then:
			result == expected
		where:
			locale         | value || expected
			null           | null  || null
			null           | "en"  || "en"
			Locale.ENGLISH | "ru"  || "en"
			Locale.ENGLISH | null  || "en"
	}
	
}
