package ru.mystamps.web.validation;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.ValidationUtils;

import ru.mystamps.web.model.AddCountryForm;

public class AddCountryValidator implements Validator {
	
	@Override
	public boolean supports(final Class clazz) {
		return AddCountryForm.class.isAssignableFrom(clazz);
	}
	
	@Override
	public void validate(final Object target, final Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "country", "value.required");
	}
	
}

