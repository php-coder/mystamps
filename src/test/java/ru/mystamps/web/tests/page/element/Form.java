/*
 * Copyright (C) 2009-2015 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.tests.page.element;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.Validate;

public final class Form {
	private static final String              FORM_LOCATOR = "//form";
	private static final String       INPUT_FIELD_LOCATOR = "//input[@name=\"%s\"]";
	// CheckStyle: ignore LineLength for next 2 lines
	private static final String    PASSWORD_FIELD_LOCATOR = "//input[@name=\"%s\"][@type=\"password\"]";
	private static final String    CHECKBOX_FIELD_LOCATOR = "//input[@name=\"%s\"][@type=\"checkbox\"]";
	private static final String      UPLOAD_FIELD_LOCATOR = "//input[@name=\"%s\"][@type=\"file\"]";
	private static final String      SELECT_FIELD_LOCATOR = "//select[@name=\"%s\"]";
	private static final String    TEXTAREA_FIELD_LOCATOR = "//textarea[@name=\"%s\"]";
	private static final String     SUBMIT_BUTTON_LOCATOR = "//input[@type=\"submit\"]";
	// CheckStyle: ignore LineLength for next 1 line
	private static final String SUBMIT_WITH_VALUE_LOCATOR = "//input[@type=\"submit\"][@value=\"%s\"]";
	
	@Getter private List<Field> fields;
	@Getter private List<SubmitButton> submitButtons;
	
	private Form() {
		fields = new ArrayList<>();
		submitButtons = new ArrayList<>();
	}
	
	@Override
	public String toString() {
		return FORM_LOCATOR;
	}
	
	public static Form with(Field... fields) {
		Form form = new Form();
		
		for (Field field : fields) {
			form.with(field);
		}
		
		return form;
	}
	
	public Form with(Field field) {
		fields.add(field);
		return this;
	}
	
	public Form with(SubmitButton button) {
		submitButtons.add(button);
		return this;
	}
	
	public List<Field> getRequiredFields() {
		List<Field> requiredFields = new ArrayList<>();
		
		for (Field field : fields) {
			if (field.isRequired()) {
				requiredFields.add(field);
			}
		}
		
		return requiredFields;
	}
	
	public Field getField(String name) {
		for (Field field : fields) {
			if (field.getName().equals(name)) {
				return field;
			}
		}
		
		throw new IllegalStateException(
			"Form does not have field with name '" + name + "'"
		);
	}
	
	public Form and() {
		return this;
	}
	
	public static Field required(Field field) {
		field.setRequired(true);
		return field;
	}
	
	public static Field inputField(String name) {
		return new InputField(name);
	}
	
	public static Field checkboxField(String name) {
		return new CheckboxField(name);
	}
	
	public static Field uploadFileField(String name) {
		return new UploadFileField(name);
	}
	
	public static Field passwordField(String name) {
		return new PasswordField(name);
	}
	
	public static Field selectField(String name) {
		return new SelectField(name);
	}
	
	public static Field textareaField(String name) {
		return new TextareaField(name);
	}
	
	public static SubmitButton submitButton(String value) {
		return new SubmitButton(value);
	}
	
	//
	// Inner classes
	//
	
	public static class SubmitButton {
		@Getter private final String value;
		
		private final String xpath;
		
		public SubmitButton() {
			this.value = "";
			this.xpath = SUBMIT_BUTTON_LOCATOR;
		}
		
		public SubmitButton(String value) {
			this.value = value;
			this.xpath = String.format(SUBMIT_WITH_VALUE_LOCATOR, value);
		}
		
		@Override
		public String toString() {
			return xpath;
		}
	}
	
	public static class Field {
		@Getter private final String id;
		@Getter private final String name;
		@Setter private boolean required;
		@Getter private String label;
		@Getter private String invalidValue;
		
		private final String xpath;
		private boolean preserveInvalidValue = true;
		private boolean accessibleByAll = false;
		
		protected Field(String name, String xpath) {
			this.name = name;
			this.xpath = String.format(xpath, name);
			
			// Assume that name and id is similar by default
			this.id = name;
		}
		
		public Field and() {
			return this;
		}
		
		public boolean isRequired() {
			return required;
		}
		
		public Field accessibleByAll(boolean visible) {
			accessibleByAll = visible;
			return this;
		}
		
		public boolean isAccessibleByAll() {
			return accessibleByAll;
		}
		
		public boolean hasLabel() {
			return label != null;
		}
		
		public Field withLabel(String label) {
			this.label = label;
			return this;
		}
		
		public boolean hasInvalidValue() {
			return invalidValue != null;
		}
		
		public Field invalidValue(String invalidValue) {
			Validate.isTrue(!"".equals(invalidValue), "Invalid value for field must be non empty");
			this.invalidValue = invalidValue;
			return this;
		}
		
		public Field preserveInvalidValue(boolean preserveInvalidValue) {
			this.preserveInvalidValue = preserveInvalidValue;
			return this;
		}
		
		public boolean shouldPreserveInvalidValue() {
			return preserveInvalidValue;
		}
		
		@Override
		public String toString() {
			return xpath;
		}
	}
	
	public static class InputField extends Field {
		public InputField(String name) {
			super(name, INPUT_FIELD_LOCATOR);
		}
	}
	
	public static class CheckboxField extends Field {
		public CheckboxField(String name) {
			super(name, CHECKBOX_FIELD_LOCATOR);
		}
	}
	
	public static class UploadFileField extends Field {
		public UploadFileField(String name) {
			super(name, UPLOAD_FIELD_LOCATOR);
		}
	}
	
	public static class PasswordField extends Field {
		public PasswordField(String name) {
			super(name, PASSWORD_FIELD_LOCATOR);
			preserveInvalidValue(false);
		}
	}
	
	public static class SelectField extends Field {
		public SelectField(String name) {
			super(name, SELECT_FIELD_LOCATOR);
		}
	}
	
	public static class TextareaField extends Field {
		public TextareaField(String name) {
			super(name, TEXTAREA_FIELD_LOCATOR);
		}
	}
	
}
