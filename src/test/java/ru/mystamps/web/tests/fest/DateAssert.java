/*
 * Copyright (C) 2012 Slava Semushin <slava.semushin@gmail.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

package ru.mystamps.web.tests.fest;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;

import org.fest.assertions.GenericAssert;
import org.fest.assertions.Assertions;

/**
 * Custom assertions for Date class.
 *
 * @see http://docs.codehaus.org/display/FEST/Extending+FEST-Assert+with+Custom+Assertions
 **/
public final class DateAssert extends GenericAssert<DateAssert, Date> {
	
	private DateAssert(final Date actual) {
		super(DateAssert.class, actual);
	}
	
	public static DateAssert assertThat(final Date actual) {
		return new DateAssert(actual);
	}
	
	/**
	 * Verifies that date equals to current date (up to no more than the seconds).
	 **/
	public DateAssert isCurrentDate() {
		isNotNull();
		
		final Date now = new Date();
		final String msg = String.format("%s is not current date. Expected: %s", actual, now);
		
		Assertions.assertThat(DateUtils.truncatedEquals(now, actual, Calendar.SECOND))
			.overridingErrorMessage(msg)
			.isTrue();
		
		return this;
	}
	
}
