/*
 * Copyright (C) 2009-2018 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.tests.cases;

import java.util.List;

import ru.mystamps.web.tests.page.AbstractPageWithForm;
import ru.mystamps.web.tests.page.element.Form.Field;

import static ru.mystamps.web.tests.TranslationUtils.tr;

import static org.fest.assertions.api.Assertions.assertThat;

@SuppressWarnings("checkstyle:abstractclassname")
abstract class WhenAnyUserAtAnyPageWithForm<T extends AbstractPageWithForm>
	extends WhenAnyUserAtAnyPage<T> {
	
	WhenAnyUserAtAnyPageWithForm(Class<T> pageClass) {
		super(pageClass);
	}
	
	protected void checkStandardStructure() {
		shouldHaveFields();
		
		emptyValueShouldBeForbiddenForRequiredFields();
	}
	
	private void shouldHaveFields() {
		for (Field field : page.getForm().getFields()) {
			if (field.isAccessibleByAll()) {
				assertThat(page.isFieldExists(field))
					.overridingErrorMessage("field with XPath '" + field + "' should exists")
					.isTrue();
			}
		}
	}
	
	protected void emptyValueShouldBeForbiddenForRequiredFields() {
		List<Field> requiredFields = page.getForm().getRequiredFields();
		if (requiredFields.isEmpty()) {
			return;
		}
		
		page.submit();
		
		for (Field field : requiredFields) {
			assertThat(page.getFieldError(field.getId()))
				.overridingErrorMessage(
					String.format(
						"required field with id '%s' should not accept empty value", field.getId()
					)
				)
				.isEqualTo(tr("org.hibernate.validator.constraints.NotEmpty.message"));
		}
	}
	
}
