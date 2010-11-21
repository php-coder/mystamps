package ru.mystamps.web.validation;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.ValidationUtils;

import ru.mystamps.db.Users;
import ru.mystamps.web.model.AuthAccountForm;

import static ru.mystamps.web.validation.ValidationRules.LOGIN_MIN_LENGTH;
import static ru.mystamps.web.validation.ValidationRules.LOGIN_MAX_LENGTH;
import static ru.mystamps.web.validation.ValidationRules.LOGIN_REGEXP;
import static ru.mystamps.web.validation.ValidationRules.PASSWORD_MIN_LENGTH;
import static ru.mystamps.web.validation.ValidationRules.PASSWORD_REGEXP;

@Component
public class AuthAccountValidator implements Validator {
	
	@Autowired
	private Users users;
	
	@Override
	public boolean supports(final Class clazz) {
		return AuthAccountForm.class.isAssignableFrom(clazz);
	}
	
	@Override
	public void validate(final Object target, final Errors errors) {
		final AuthAccountForm form = (AuthAccountForm)target;
		
		validateLogin(form, errors);
		validatePassword(form, errors);
		validateLoginPasswordPair(form, errors);
	}
	
	private static void validateLogin(final AuthAccountForm form, final Errors errors) {
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
		if (! login.matches(LOGIN_REGEXP)) {
			errors.rejectValue("login", "login.invalid");
			return;
		}
	}
	
	private static void validatePassword(final AuthAccountForm form, final Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "value.required");
		if (errors.hasFieldErrors("password")) {
			return;
		}
		
		final String password = form.getPassword();
		
		if (password.length() < PASSWORD_MIN_LENGTH.intValue()) {
			errors.rejectValue(
				"password",
				"value.too-short",
				new Object[]{PASSWORD_MIN_LENGTH},
				"XXX");
			return;
		}
		
		// TODO: use Pattern class
		if (! password.matches(PASSWORD_REGEXP)) {
			errors.rejectValue("password", "password.invalid");
			return;
		}
	}
	
	private void validateLoginPasswordPair(final AuthAccountForm form, final Errors errors) {
		if (errors.hasFieldErrors("login") || errors.hasFieldErrors("password")) {
			return;
		}
		
		final String login = form.getLogin();
		final String password = form.getPassword();
		
		try {
			final Long userId = users.auth(login, password);
			
			if (userId == null) {
				errors.reject("login.password.invalid");
			}
			
		} catch (final SQLException ex) {
			errors.reject("error.internal");
		}
	}
	
}

