/*
 * Copyright (C) 2011-2012 Slava Semushin <slava.semushin@gmail.com>
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

import org.fest.assertions.api.AbstractAssert;
import org.fest.assertions.api.Assertions;

import static com.google.common.base.Preconditions.checkState;

import ru.mystamps.web.tests.page.AbstractPageWithForm;

/**
 * Custom assertions for AbstractPageWithForm class.
 *
 * @see https://github.com/alexruiz/fest-assert-2.x/wiki/Creating-specific-assertions
 **/
public final class AbstractPageWithFormAssert
	extends AbstractAssert<AbstractPageWithFormAssert, AbstractPageWithForm> {
	
	private String fieldName;
	
	private AbstractPageWithFormAssert(final AbstractPageWithForm actual) {
		super(actual, AbstractPageWithFormAssert.class);
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
		checkState(fieldName != null, "Error in test case: field name should be specified");
		
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
		checkState(fieldName != null, "Error in test case: field name should be specified");
		
		final String msg = String.format(
			"Expected that field '%s' should not have any error",
			fieldName
		);
		
		Assertions.assertThat(actual.isFieldHasError(fieldName))
			.overridingErrorMessage(msg)
			.isFalse();
		
		return this;
	}
	
	public AbstractPageWithFormAssert hasValue(final String expectedValue) {
		isNotNull();
		checkState(fieldName != null, "Error in test case: field name should be specified");
		
		final String value = actual.getFieldValue(fieldName);
		
		final String msg = String.format(
			"Expected value of field '%s' is '%s' but was '%s'",
			fieldName,
			expectedValue,
			value
		);
		
		Assertions.assertThat(value)
			.overridingErrorMessage(msg)
			.isEqualTo(expectedValue);
		
		return this;
	}
	
}
