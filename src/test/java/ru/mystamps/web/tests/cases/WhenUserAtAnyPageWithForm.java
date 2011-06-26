/*
 * Copyright (C) 2009-2011 Slava Semushin <slava.semushin@gmail.com>
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

package ru.mystamps.web.tests.cases;

import java.util.List;

import ru.mystamps.web.tests.page.AbstractPageWithForm;
import ru.mystamps.web.tests.page.element.Form.Field;
import ru.mystamps.web.tests.page.element.Form.SubmitButton;

import static ru.mystamps.web.tests.TranslationUtils.tr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;

abstract class WhenUserAtAnyPageWithForm<T extends AbstractPageWithForm>
	extends WhenUserAtAnyPage<T> {
	
	public WhenUserAtAnyPageWithForm(final Class<T> pageClass) {
		super(pageClass);
	}
	
	@Override
	protected void checkStandardStructure() {
		super.checkStandardStructure();
		
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
		assertTrue("form tag should exists", page.formExists());
	}
	
	private void shouldHaveFields() {
		for (final Field field : page.getForm().getFields()) {
			// TODO: improve message to showing input type.
			// Probably we need to call field.toString() there
			assertTrue(
				"field named '" + field.getName() + "' should exists",
				page.isFieldExists(field)
			);
		}
	}
	
	private void shouldHaveLabels() {
		for (final Field field : page.getForm().getFields()) {
			if (!field.hasLabel()) {
				continue;
			}
			
			assertEquals(
				String.format(
					"field with id '%s' should have label '%s'",
					field.getId(),
					field.getLabel()
				),
				field.getLabel(),
				page.getInputLabelValue(field.getId())
			);
		}
	}
	
	private void shouldHaveSubmitButton() {
		for (final SubmitButton button : page.getForm().getSubmitButtons()) {
			assertTrue(
				"submit button with value '" + button.getValue() + "' should exists",
				page.isSubmitButtonExists(button)
			);
		}
	}
	
	private void requiredFieldsShouldBeMarkedByAsterisk() {
		for (final Field field : page.getForm().getRequiredFields()) {
			assertTrue(
				"required field with id '" + field.getId() + "' should be marked by asterisk",
				page.inputHasAsterisk(field.getId())
			);
		}
	}
	
	private void mayExistsLegendAboutRequiredFields() {
		if (page.getForm().getRequiredFields().isEmpty()) {
			return;
		}
		
		assertThat(
			"legend about required fields should exist",
			page.getFormHints(),
			hasItem(tr("t_required_fields_legend", "*"))
		);
	}
	
	private void emptyValueShouldBeForbiddenForRequiredFields() {
		final List<Field> requiredFields = page.getForm().getRequiredFields();
		if (requiredFields.isEmpty()) {
			return;
		}
		
		page.submit();
		
		for (final Field field : requiredFields) {
			assertEquals(
				"required field with id '" + field.getId() + "' should not accept empty value",
				tr("value.required"),
				page.getFieldError(field.getId())
			);
		}
	}
	
	private void fieldsValuesShouldBePreservedWhenErrorOccurs() {
		for (final Field field : page.getForm().getFields()) {
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
				assertEquals(
					String.format(
						"field named '%s' should preserve input value after error",
						field.getName()
					),
					field.getInvalidValue(),
					page.getFieldValue(field.getName())
				);
			} else {
				assertEquals(
					String.format(
						"field named '%s' should not preserve input value after error",
						field.getName()
					),
					"",
					page.getFieldValue(field.getName())
				);
			}
		}
	}
	
}
