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

package ru.mystamps.web.tests.page.element;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

public class Form {
	@Getter private List<Field> fields;
	@Getter private List<SubmitButton> submitButtons;
	
	private Form() {
		fields = new ArrayList<Field>();
		submitButtons = new ArrayList<SubmitButton>();
	}
	
	public static Form with(final Field... fields) {
		final Form form = new Form();
		
		for (final Field field : fields) {
			form.with(field);
		}
		
		return form;
	}
	
	public Form with(final Field field) {
		fields.add(field);
		return this;
	}
	
	public Form with(final SubmitButton button) {
		submitButtons.add(button);
		return this;
	}
	
	public List<Field> getRequiredFields() {
		final List<Field> requiredFields = new ArrayList<Field>();
		
		for (final Field field : fields) {
			if (field.isRequired()) {
				requiredFields.add(field);
			}
		}
		
		return requiredFields;
	}
	
	public Form and() {
		return this;
	}
	
	public static Field required(final Field field) {
		field.setRequired(true);
		return field;
	}
	
	public static Field inputField(final String name) {
		return new InputField(name);
	}
	
	public static Field checkboxField(final String name) {
		return new CheckboxField(name);
	}
	
	public static Field uploadFileField(final String name) {
		return new UploadFileField(name);
	}
	
	public static Field passwordField(final String name) {
		return new PasswordField(name);
	}
	
	public static Field selectField(final String name) {
		return new SelectField(name);
	}
	
	public static Field textareaField(final String name) {
		return new TextareaField(name);
	}
	
	public static SubmitButton submitButton(final String value) {
		return new SubmitButton(value);
	}
	
	//
	// Inner classes
	//
	
	@RequiredArgsConstructor
	public static class SubmitButton {
		@Getter private final String value;
	}
	
	public static class Field {
		@Getter private final String id;
		@Getter private final String name;
		@Setter private boolean required;
		@Getter private String label;
		@Getter private String invalidValue;
		private boolean preserveInvalidValue = true;
		
		protected Field(final String name) {
			this.name = name;
			
			// Assume that name and id is similar by default
			this.id = name;
		}
		
		public Field and() {
			return this;
		}
		
		public boolean isRequired() {
			return required;
		}
		
		public boolean hasLabel() {
			return label != null;
		}
		
		public Field withLabel(final String label) {
			this.label = label;
			return this;
		}
		
		public boolean hasInvalidValue() {
			return invalidValue != null;
		}
		
		public Field invalidValue(final String invalidValue) {
			if ("".equals(invalidValue)) {
				throw new IllegalArgumentException("Invalid value for field should be non empty");
			}
			
			this.invalidValue = invalidValue;
			return this;
		}
		
		protected void preserveInvalidValue(boolean preserveInvalidValue) {
			this.preserveInvalidValue = preserveInvalidValue;
		}
		
		public boolean shouldPreserveInvalidValue() {
			return preserveInvalidValue;
		}
	}
	
	public static class InputField extends Field {
		public InputField(final String name) {
			super(name);
		}
	}
	
	public static class CheckboxField extends Field {
		public CheckboxField(final String name) {
			super(name);
		}
	}
	
	public static class UploadFileField extends Field {
		public UploadFileField(final String name) {
			super(name);
		}
	}
	
	public static class PasswordField extends Field {
		public PasswordField(final String name) {
			super(name);
			preserveInvalidValue(false);
		}
	}
	
	public static class SelectField extends Field {
		public SelectField(final String name) {
			super(name);
		}
	}
	
	public static class TextareaField extends Field {
		public TextareaField(final String name) {
			super(name);
		}
	}
	
}
