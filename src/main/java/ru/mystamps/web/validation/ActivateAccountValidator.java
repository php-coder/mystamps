package ru.mystamps.web.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.ValidationUtils;

import ru.mystamps.web.service.UserService;
import ru.mystamps.web.model.ActivateAccountForm;

import static ru.mystamps.web.validation.ValidationRules.LOGIN_MIN_LENGTH;
import static ru.mystamps.web.validation.ValidationRules.LOGIN_MAX_LENGTH;
import static ru.mystamps.web.validation.ValidationRules.LOGIN_REGEXP;
import static ru.mystamps.web.validation.ValidationRules.NAME_MAX_LENGTH;
import static ru.mystamps.web.validation.ValidationRules.NAME_REGEXP1;
import static ru.mystamps.web.validation.ValidationRules.NAME_REGEXP2;
import static ru.mystamps.web.validation.ValidationRules.PASSWORD_MIN_LENGTH;
import static ru.mystamps.web.validation.ValidationRules.PASSWORD_REGEXP;
import static ru.mystamps.web.validation.ValidationRules.ACT_KEY_LENGTH;
import static ru.mystamps.web.validation.ValidationRules.ACT_KEY_REGEXP;

@Component
public class ActivateAccountValidator implements Validator {
	
	@Autowired
	private UserService userService;
	
	@Override
	public boolean supports(final Class clazz) {
		return ActivateAccountForm.class.isAssignableFrom(clazz);
	}
	
	@Override
	public void validate(final Object target, final Errors errors) {
		final ActivateAccountForm form = (ActivateAccountForm)target;
		
		validateLogin(form, errors);
		validateName(form, errors);
		validatePassword(form, errors);
		validatePasswordConfirm(form, errors);
		validateActivationKey(form, errors);
	}
	
	private void validateLogin(final ActivateAccountForm form, final Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "login", "value.required");
		if (errors.hasFieldErrors("login")) {
			return;
		}
		
		final String login = form.getLogin();
		
		if (login.length() < LOGIN_MIN_LENGTH.intValue()) {
			errors.rejectValue(
				"login",
				"value.too-short",
				new Object[]{LOGIN_MIN_LENGTH},
				"XXX");
			return;
		}
			
		if (login.length() > LOGIN_MAX_LENGTH.intValue()) {
			errors.rejectValue(
				"login",
				"value.too-long",
				new Object[]{LOGIN_MAX_LENGTH},
				"XXX");
			return;
		}
		
		// TODO: use Pattern class
		if (!login.matches(LOGIN_REGEXP)) {
			errors.rejectValue("login", "login.invalid");
			return;
		}
		
		try {
			if (userService.findByLogin(login) != null) {
				errors.rejectValue("login", "login.exists");
			}
		} catch (final Exception ex) {
			errors.rejectValue("login", "error.internal");
		}
		
	}
	
	private static void validateName(final ActivateAccountForm form, final Errors errors) {
		final String name = form.getName();
		
		if (name.isEmpty()) {
			return;
		}
		
		if (name.length() > NAME_MAX_LENGTH.intValue()) {
			errors.rejectValue(
				"name",
				"value.too-long",
				new Object[]{NAME_MAX_LENGTH},
				"XXX");
			return;
		}
		
		// TODO: use Pattern class
		if (!name.matches(NAME_REGEXP1)) {
			errors.rejectValue("name", "name.invalid");
			return;
		}
		
		// TODO: use Pattern class
		if (!name.matches(NAME_REGEXP2)) {
			errors.rejectValue("name", "name.hyphen");
			return;
		}
	}
	
	private static void validatePassword(final ActivateAccountForm form, final Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "value.required");
		if (errors.hasFieldErrors("password")) {
			return;
		}
		
		final String password = form.getPassword();
		final String login = form.getLogin();
		
		if (password.length() < PASSWORD_MIN_LENGTH.intValue()) {
			errors.rejectValue(
				"password",
				"value.too-short",
				new Object[]{PASSWORD_MIN_LENGTH},
				"XXX");
			return;
		}
		
		// TODO: use Pattern class
		if (!password.matches(PASSWORD_REGEXP)) {
			errors.rejectValue("password", "password.invalid");
			return;
		}
		
		if (!errors.hasFieldErrors("login") &&
				password.equals(login)) {
			errors.rejectValue("password", "password.login.match");
			return;
		}
	}
	
	private static void validatePasswordConfirm(
			final ActivateAccountForm form,
			final Errors errors) {
		
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "passwordConfirm", "value.required");
		if (errors.hasFieldErrors("passwordConfirm")) {
			return;
		}
		
		final String password = form.getPassword();
		final String passwordConfirm = form.getPasswordConfirm();
		
		if (!errors.hasFieldErrors("password") &&
				!passwordConfirm.equals(password)) {
			errors.rejectValue("passwordConfirm", "password.mismatch");
			return;
		}
	}
	
	private void validateActivationKey(final ActivateAccountForm form, final Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "activationKey", "value.required");
		if (errors.hasFieldErrors("activationKey")) {
			return;
		}
		
		final String key = form.getActivationKey();
		
		if (key.length() < ACT_KEY_LENGTH.intValue()) {
			errors.rejectValue(
					"activationKey",
					"value.too-short",
					new Object[]{ACT_KEY_LENGTH},
					"XXX");
			return;
		}
		
		if (key.length() > ACT_KEY_LENGTH.intValue()) {
			errors.rejectValue(
					"activationKey",
					"value.too-long",
					new Object[]{ACT_KEY_LENGTH},
					"XXX");
			return;
		}
		
		// TODO: use Pattern class
		if (!key.matches(ACT_KEY_REGEXP)) {
			errors.rejectValue("activationKey", "key.invalid");
			return;
		}
		
		try {
			if (userService.findRegistrationRequestByActivationKey(key) == null) {
				errors.rejectValue("activationKey", "key.not-exists");
			}
		} catch (final Exception ex) {
			errors.rejectValue("activationKey", "error.internal");
		}
		
	}
	
}

