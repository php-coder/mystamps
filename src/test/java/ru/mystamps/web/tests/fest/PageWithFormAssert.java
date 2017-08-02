/*
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
package ru.mystamps.web.tests.fest;

import org.fest.assertions.api.AbstractAssert;
import org.fest.assertions.api.Assertions;

import org.apache.commons.lang3.Validate;

import ru.mystamps.web.tests.page.AbstractPageWithForm;

/**
 * Custom assertions for AbstractPageWithForm class.
 *
 * @see https://github.com/alexruiz/fest-assert-2.x/wiki/Creating-specific-assertions
 **/
public final class PageWithFormAssert
	extends AbstractAssert<PageWithFormAssert, AbstractPageWithForm> {
	
	private String fieldName;
	
	private PageWithFormAssert(AbstractPageWithForm actual) {
		super(actual, PageWithFormAssert.class);
	}
	
	public static PageWithFormAssert assertThat(AbstractPageWithForm actual) {
		return new PageWithFormAssert(actual);
	}
	
	public PageWithFormAssert field(String fieldName) {
		this.fieldName = fieldName;
		return this;
	}
	
	public PageWithFormAssert hasError(String expectedErrorMessage) {
		isNotNull();
		Validate.validState(
			fieldName != null,
			"Error in test case: field name must be specified"
		);
		
		String errorMessage = actual.getFieldError(fieldName);
		
		String msg = String.format(
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
	
	public PageWithFormAssert hasNoError() {
		isNotNull();
		Validate.validState(
			fieldName != null,
			"Error in test case: field name must be specified"
		);
		
		String msg = String.format(
			"Expected that field '%s' should not have any error",
			fieldName
		);
		
		Assertions.assertThat(actual.isFieldHasError(fieldName))
			.overridingErrorMessage(msg)
			.isFalse();
		
		return this;
	}
	
	public PageWithFormAssert hasValue(String expectedValue) {
		isNotNull();
		Validate.validState(
			fieldName != null,
			"Error in test case: field name must be specified"
		);
		
		String value = actual.getFieldValue(fieldName);
		
		String msg = String.format(
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
