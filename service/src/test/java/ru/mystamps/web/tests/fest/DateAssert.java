/*
 * Copyright (C) 2009-2012 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.tests.fest;

import java.util.concurrent.TimeUnit;
import java.util.Date;

import org.fest.assertions.api.AbstractAssert;
import org.fest.assertions.api.Assertions;

/**
 * Custom assertions for Date class.
 *
 * @see https://github.com/alexruiz/fest-assert-2.x/wiki/Creating-specific-assertions
 **/
public final class DateAssert extends AbstractAssert<DateAssert, Date> {
	
	private static final long TIME_DELTA = 10;
	
	private DateAssert(Date actual) {
		super(actual, DateAssert.class);
	}
	
	public static DateAssert assertThat(Date actual) {
		return new DateAssert(actual);
	}
	
	/**
	 * Verifies that date equals to current date (up to no more than the seconds).
	 **/
	public DateAssert isCurrentDate() {
		isNotNull();
		
		Date now = new Date();
		String msg = String.format("%s is not current date. Expected: %s", actual, now);
		
		Assertions.assertThat(actual)
			.overridingErrorMessage(msg)
			.isCloseTo(now, TimeUnit.SECONDS.toMillis(TIME_DELTA));
		
		return this;
	}
	
}
