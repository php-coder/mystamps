package ru.mystamps.web.validation;

import java.sql.SQLException;

import javax.naming.NamingException;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.ValidationUtils;

import ru.mystamps.db.Users;
import ru.mystamps.db.UsersActivation;
import ru.mystamps.web.model.ActivateAccountForm;
import ru.mystamps.web.validation.ValidationRules;

public class ActivateAccountValidator implements Validator {
	
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
	
	private static void validateLogin(final ActivateAccountForm form, final Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "login", "value.required");
		if (errors.hasFieldErrors("login")) {
			return;
		}
		
		final String login = form.getLogin();
		
		if (login.length() < ValidationRules.LOGIN_MIN_LENGTH) {
			errors.rejectValue(
				"login",
				"value.too-short",
				new Object[]{new Integer(ValidationRules.LOGIN_MIN_LENGTH)},
				"XXX");
			return;
		}
			
		if (login.length() > ValidationRules.LOGIN_MAX_LENGTH) {
			errors.rejectValue(
				"login",
				"value.too-long",
				new Object[]{new Integer(ValidationRules.LOGIN_MAX_LENGTH)},
				"XXX");
			return;
		}
		
		// TODO: use Pattern class
		if (! login.matches(ValidationRules.LOGIN_REGEXP)) {
			errors.rejectValue("login", "login.invalid");
			return;
		}
		
		try {
			// TODO: make it static?
			final Users users = new Users();
			if (users.loginExists(login)) {
				errors.rejectValue("login", "login.exists");
			}
			
		} catch (final NamingException ex) {
			errors.rejectValue("login", "error.internal");
			
		} catch (final SQLException ex) {
			errors.rejectValue("login", "error.internal");
		}
		
	}
	
	private static void validateName(final ActivateAccountForm form, final Errors errors) {
		final String name = form.getName();
		
		if (name.length() == 0) {
			// don't check empty value
			return;
		}
		
		if (name.length() > ValidationRules.NAME_MAX_LENGTH) {
			errors.rejectValue(
				"name",
				"value.too-long",
				new Object[]{new Integer(ValidationRules.NAME_MAX_LENGTH)},
				"XXX");
			return;
		}
		
		// TODO: use Pattern class
		if (!name.matches(ValidationRules.NAME_REGEXP1)) {
			errors.rejectValue("name", "name.invalid");
			return;
		}
		
		// TODO: use Pattern class
		if (!name.matches(ValidationRules.NAME_REGEXP2)) {
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
		
		if (password.length() < ValidationRules.PASSWORD_MIN_LENGTH) {
			errors.rejectValue(
				"password",
				"value.too-short",
				new Object[]{new Integer(ValidationRules.PASSWORD_MIN_LENGTH)},
				"XXX");
			return;
		}
		
		// TODO: use Pattern class
		if (! password.matches(ValidationRules.PASSWORD_REGEXP)) {
			errors.rejectValue("password", "password.invalid");
			return;
		}
		
		if (! errors.hasFieldErrors("login") &&
				password.equals(login)) {
			errors.rejectValue("password", "password.login.match");
			return;
		}
	}
	
	private static void validatePasswordConfirm(final ActivateAccountForm form, final Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "passwordConfirm", "value.required");
		if (errors.hasFieldErrors("passwordConfirm")) {
			return;
		}
		
		final String password = form.getPassword();
		final String passwordConfirm = form.getPasswordConfirm();
		
		if (! errors.hasFieldErrors("password") &&
				!passwordConfirm.equals(password)) {
			errors.rejectValue("passwordConfirm", "password.mismatch");
			return;
		}
	}
	
	private static void validateActivationKey(final ActivateAccountForm form, final Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "activationKey", "value.required");
		if (errors.hasFieldErrors("activationKey")) {
			return;
		}
		
		final String key = form.getActivationKey();
		
		if (key.length() < ValidationRules.ACT_KEY_LENGTH) {
			errors.rejectValue(
					"activationKey",
					"value.too-short",
					new Object[]{new Integer(ValidationRules.ACT_KEY_LENGTH)},
					"XXX");
			return;
		}
		
		if (key.length() > ValidationRules.ACT_KEY_LENGTH) {
			errors.rejectValue(
					"activationKey",
					"value.too-long",
					new Object[]{new Integer(ValidationRules.ACT_KEY_LENGTH)},
					"XXX");
			return;
		}
			
		// TODO: use Pattern class
		if (!key.matches(ValidationRules.ACT_KEY_REGEXP)) {
			errors.rejectValue("activationKey", "key.invalid");
			return;
		}
		
		try {
			// TODO: make it static?
			final UsersActivation activations = new UsersActivation();
			if (! activations.actKeyExists(key)) {
				errors.rejectValue("activationKey", "key.not-exists");
			}
			
		} catch (final NamingException ex) {
			errors.rejectValue("activationKey", "error.internal");
			
		} catch (final SQLException ex) {
			errors.rejectValue("activationKey", "error.internal");
		}
	}
	
}

