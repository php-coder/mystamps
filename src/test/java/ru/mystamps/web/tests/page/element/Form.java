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
			if (invalidValue == null || invalidValue.isEmpty()) {
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
