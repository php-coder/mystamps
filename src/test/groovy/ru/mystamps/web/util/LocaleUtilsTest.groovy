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
