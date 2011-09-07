/*
 * Copyright (C) 2011 Slava Semushin <slava.semushin@gmail.com>
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

import org.fest.assertions.GenericAssert;
import org.fest.assertions.Assertions;

import ru.mystamps.web.tests.page.AbstractPageWithForm;

/**
  * Custom assertions for AbstractPageWithForm class.
  *
  * @see http://docs.codehaus.org/display/FEST/Extending+FEST-Assert+with+Custom+Assertions
  **/
public final class AbstractPageWithFormAssert
	extends GenericAssert<AbstractPageWithFormAssert, AbstractPageWithForm> {
	
	private String fieldName;
	
	private AbstractPageWithFormAssert(final AbstractPageWithForm actual) {
		super(AbstractPageWithFormAssert.class, actual);
	}
	
	public static AbstractPageWithFormAssert assertThat(final AbstractPageWithForm actual) {
		return new AbstractPageWithFormAssert(actual);
	}
	
	public AbstractPageWithFormAssert field(final String fieldName) {
		this.fieldName = fieldName;
		return this;
	}
	
	public AbstractPageWithFormAssert hasError(final String expectedErrorMessage) {
		isNotNull();
		checkThatFieldNameNotNull();
		
		final String errorMessage = actual.getFieldError(fieldName);
		
		final String msg = String.format(
			"Expected that field '%s' should have error '%s' but was '%s'",
			fieldName,
			expectedErrorMessage,
			errorMessage
		);
		
		Assertions.assertThat(errorMessage)
			.overridingErrorMessage(msg)
			.isEqualTo(expectedErrorMessage);
		
		return this;
	}
	
	public AbstractPageWithFormAssert hasNoError() {
		isNotNull();
		checkThatFieldNameNotNull();
		
		final String msg = String.format(
			"Expected that field '%s' should not have any error",
			fieldName
		);
		
		Assertions.assertThat(actual.isFieldHasError(fieldName))
			.overridingErrorMessage(msg)
			.isFalse();
		
		return this;
	}
	
	private void checkThatFieldNameNotNull() {
		if (fieldName == null) {
			throw new IllegalArgumentException("Error in test case: field name not specified");
		}
	}
	
}
