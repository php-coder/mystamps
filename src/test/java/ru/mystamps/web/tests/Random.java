/*
 * Copyright (C) 2009-2025 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.tests;

import io.qala.datagen.RandomElements;
import io.qala.datagen.RandomShortApi;
import org.apache.commons.lang3.StringUtils;
import ru.mystamps.web.common.Currency;
import ru.mystamps.web.feature.account.AccountValidation;
import ru.mystamps.web.feature.category.CategoryValidation;
import ru.mystamps.web.feature.country.CountryValidation;
import ru.mystamps.web.feature.participant.ParticipantValidation;
import ru.mystamps.web.feature.series.SeriesValidation;
import ru.mystamps.web.feature.site.SiteUrl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static io.qala.datagen.RandomShortApi.bool;
import static io.qala.datagen.RandomShortApi.integer;
import static io.qala.datagen.RandomShortApi.sample;
import static io.qala.datagen.RandomShortApi.sampleMultiple;
import static io.qala.datagen.RandomValue.between;
import static io.qala.datagen.StringModifier.Impls.multipleOf;
import static io.qala.datagen.StringModifier.Impls.oneOf;
import static org.apache.commons.lang3.StringUtils.SPACE;

public final class Random {
	
	private Random() {
	}
	
	public static Integer id() {
		return RandomShortApi.positiveInteger();
	}
	
	public static Integer userId() {
		return RandomShortApi.positiveInteger();
	}
	
	public static Date date() {
		try {
			
			SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
			fmt.setLenient(false);
			return between(fmt.parse("2017-01-01"), fmt.parse("2017-12-20")).date();
		
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static BigDecimal price() {
		// @todo #769 Random.price(): return randomized values
		return RandomElements.from(new BigDecimal("17"), new BigDecimal("100.99")).sample();
	}
	
	public static String login() {
		String login = between(
			AccountValidation.LOGIN_MIN_LENGTH,
			AccountValidation.LOGIN_MAX_LENGTH
		)
			.with(multipleOf(" -_"))
			.alphanumeric();
		
		if (StringUtils.containsAny(login, SPACE, "--", "__")) {
			return login();
		}
		
		return login;
	}
	
	public static Currency currency() {
		return sample(Currency.values());
	}
	
	public static String url() {
		final long minLength = 5;
		final long maxLength = 15;
		String randomPart = between(minLength, maxLength).with(multipleOf('/')).alphanumeric();
		return "http://example.com/page/" + randomPart;
	}
	
	public static String lang() {
		return sample(Locale.getISOLanguages());
	}
	
	public static String name() {
		final long enoughLongLength = 15;
		return between(1, enoughLongLength).english();
	}
	
	public static String categoryName() {
		String name = between(
				CategoryValidation.NAME_MIN_LENGTH,
				CategoryValidation.NAME_MAX_LENGTH
			)
			.with(oneOf(" -"))
			.english();
		
		if (StringUtils.startsWithAny(name, SPACE, "-")
			|| StringUtils.endsWithAny(name, SPACE, "-")) {
			return countryName();
		}
		
		return name;
	}
	
	public static String countryName() {
		String name = between(
				CountryValidation.NAME_MIN_LENGTH,
				CountryValidation.NAME_MAX_LENGTH
			)
			.with(oneOf(" -"))
			.english();
		
		if (StringUtils.startsWithAny(name, SPACE, "-")
			|| StringUtils.endsWithAny(name, SPACE, "-")) {
			return countryName();
		}
		
		return name;
	}
	
	public static String participantName() {
		return between(
			ParticipantValidation.NAME_MIN_LENGTH,
			ParticipantValidation.NAME_MAX_LENGTH
		).english();
	}
	
	public static String sellerName() {
		return participantName();
	}
	
	public static Integer dayOfMonth() {
		return between(1L, SeriesValidation.MAX_DAYS_IN_MONTH).integer();
	}
	
	public static Integer monthOfYear() {
		return between(1L, SeriesValidation.MAX_MONTHS_IN_YEAR).integer();
	}
	
	public static Integer issueYear() {
		return between(
			SeriesValidation.MIN_RELEASE_YEAR,
			Year.now(ZoneOffset.UTC).getValue()
		).integer();
	}
	
	public static Integer quantity() {
		return between(
			SeriesValidation.MIN_STAMPS_IN_SERIES,
			SeriesValidation.MAX_STAMPS_IN_SERIES
		).integer();
	}
	
	public static boolean perforated() {
		return bool();
	}
	
	public static List<Integer> listOfIntegers() {
		final int minSize = 1;
		final int maxSize = 3;
		int size = integer(minSize, maxSize);
		return sampleMultiple(size, integer(), integer(), integer());
	}
	
	public static String jsoupLocator() {
		return sample("#id", "a[href]", "img[src$=.png]", "div#logo");
	}
	
	public static String host() {
		return sample(SiteUrl.SITE, SiteUrl.PUBLIC_URL);
	}
	
}
