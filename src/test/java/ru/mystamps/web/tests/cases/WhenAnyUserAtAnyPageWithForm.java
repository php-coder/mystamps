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
package ru.mystamps.web.tests.cases;

import java.util.List;

import ru.mystamps.web.tests.page.AbstractPageWithForm;
import ru.mystamps.web.tests.page.element.Form.Field;
import ru.mystamps.web.tests.page.element.Form.SubmitButton;

import static ru.mystamps.web.tests.TranslationUtils.tr;

import static org.fest.assertions.api.Assertions.assertThat;

@SuppressWarnings("checkstyle:abstractclassname")
abstract class WhenAnyUserAtAnyPageWithForm<T extends AbstractPageWithForm>
	extends WhenAnyUserAtAnyPage<T> {
	
	WhenAnyUserAtAnyPageWithForm(Class<T> pageClass) {
		super(pageClass);
	}
	
	protected void checkStandardStructure() {
		shouldHaveForm();
		shouldHaveFields();
		shouldHaveLabels();
		shouldHaveSubmitButton();
		
		requiredFieldsShouldBeMarkedByAsterisk();
		mayExistsLegendAboutRequiredFields();
		emptyValueShouldBeForbiddenForRequiredFields();
		
		fieldsValuesShouldBePreservedWhenErrorOccurs();
	}
	
	private void shouldHaveForm() {
		assertThat(page.formExists())
			.overridingErrorMessage("form tag should exists")
			.isTrue();
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
	
	private void shouldHaveLabels() {
		for (Field field : page.getForm().getFields()) {
			if (!field.isAccessibleByAll() || !field.hasLabel()) {
				continue;
			}
			
			String msg = String.format(
				"field with id '%s' should have label '%s'",
				field.getId(),
				field.getLabel()
			);
			
			assertThat(page.getInputLabelValue(field.getId()))
				.overridingErrorMessage(msg)
				.isEqualTo(field.getLabel());
		}
	}
	
	private void shouldHaveSubmitButton() {
		for (SubmitButton button : page.getForm().getSubmitButtons()) {
			assertThat(page.isSubmitButtonExists(button))
				.overridingErrorMessage(
					String.format(
						"submit button with value '%s' should exists", button.getValue()
					)
				)
				.isTrue();
		}
	}
	
	private void requiredFieldsShouldBeMarkedByAsterisk() {
		for (Field field : page.getForm().getRequiredFields()) {
			assertThat(page.inputHasAsterisk(field.getId()))
				.overridingErrorMessage(
					String.format(
						"required field with id '%s' should be marked by asterisk", field.getId()
					)
				)
				.isTrue();
		}
	}
	
	private void mayExistsLegendAboutRequiredFields() {
		if (page.getForm().getRequiredFields().isEmpty()) {
			return;
		}
		
		assertThat(page.getFormHints())
			.overridingErrorMessage("legend about required fields should exists")
			.contains(tr("t_required_fields_legend", "*"));
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
	
	private void fieldsValuesShouldBePreservedWhenErrorOccurs() {
		for (Field field : page.getForm().getFields()) {
			if (!field.hasInvalidValue()) {
				System.err.println(
					"NOTICE: Invalid value not defined for field '"
					+ field.getName()
					+ "' at page "
					+ page.getUrl()
				);
				continue;
			}
			
			page.fillField(field.getName(), field.getInvalidValue());
			page.submit();
			
			if (field.shouldPreserveInvalidValue()) {
				String msg = String.format(
					"field named '%s' should preserve input value after error",
					field.getName()
				);
				
				assertThat(page.getFieldValue(field.getName()))
					.overridingErrorMessage(msg)
					.isEqualTo(field.getInvalidValue());
				
			} else {
				String msg = String.format(
					"field named '%s' should not preserve input value after error",
					field.getName()
				);
				
				assertThat(page.getFieldValue(field.getName()))
					.overridingErrorMessage(msg)
					.isEmpty();
			}
		}
	}
	
}
