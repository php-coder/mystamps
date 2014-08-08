/**
 * Copyright (C) 2009-2014 Slava Semushin <slava.semushin@gmail.com>
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

import ru.mystamps.web.entity.LocalizedEntity
import ru.mystamps.web.service.TestObjects

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
			Locale.ENGLISH         || "en"
			new Locale("ru", "RU") || "ru"
	}
	
	//
	// Tests for getLocalizedName()
	//
	
	def "getLocalizedName() should throw exception when entity is null"() {
		when:
			LocaleUtils.getLocalizedName(Locale.ENGLISH, null)
		then:
			thrown IllegalArgumentException
	}
	
	@Unroll
	def "getLocalizedName() should returns '#expectedName' for locale '#locale'"(Locale locale, LocalizedEntity entity, String expectedName) {
		when:
			String name = LocaleUtils.getLocalizedName(locale, entity)
		then:
			name == expectedName
		where:
			locale                 | entity                      || expectedName
			null                   | TestObjects.createCountry() || TestObjects.TEST_COUNTRY_EN_NAME
			Locale.ENGLISH         | TestObjects.createCountry() || TestObjects.TEST_COUNTRY_EN_NAME
			Locale.FRENCH          | TestObjects.createCountry() || TestObjects.TEST_COUNTRY_EN_NAME
			new Locale("ru", "RU") | TestObjects.createCountry() || TestObjects.TEST_COUNTRY_RU_NAME
	}
	
}
