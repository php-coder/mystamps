package ru.mystamps.web.validation;

import org.apache.commons.validator.EmailValidator;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.ValidationUtils;

import ru.mystamps.web.model.RegisterAccountForm;

import static ru.mystamps.web.validation.ValidationRules.EMAIL_MAX_LENGTH;

public class RegisterAccountValidator implements Validator {
	
	@Override
	public boolean supports(final Class clazz) {
		return RegisterAccountForm.class.isAssignableFrom(clazz);
	}
	
	@Override
	public void validate(final Object target, final Errors errors) {
		final RegisterAccountForm form = (RegisterAccountForm)target;
		
		validateEmail(form, errors);
	}
	
	private static void validateEmail(final RegisterAccountForm form, final Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "value.required");
		if (errors.hasFieldErrors("email")) {
			return;
		}
		
		final String email = form.getEmail();
		
		if (email.length() > EMAIL_MAX_LENGTH.intValue()) {
			errors.rejectValue(
				"email",
				"value.too-long",
				new Object[]{EMAIL_MAX_LENGTH},
				"XXX");
			return;
		}
		
		// TODO: Deprecated. Use the new EmailValidator in the
		// routines package. (Or move to using Hibernate validators.)
		EmailValidator validator = EmailValidator.getInstance();
		if (!validator.isValid(email)) {
			errors.rejectValue("email", "email.invalid");
		}
		
	}
	
}

