/*
 * Copyright (C) 2009-2016 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.tests.page;

import java.util.Arrays;
import java.util.Collection;

import org.openqa.selenium.WebDriver;

import ru.mystamps.web.Url;

import org.apache.commons.lang3.Validate;

import static org.apache.commons.collections.CollectionUtils.exists;
import static org.apache.commons.collections.PredicateUtils.notNullPredicate;

import static ru.mystamps.web.tests.TranslationUtils.tr;
import static ru.mystamps.web.tests.page.element.Form.with;
import static ru.mystamps.web.tests.page.element.Form.required;
import static ru.mystamps.web.tests.page.element.Form.inputField;
import static ru.mystamps.web.tests.page.element.Form.passwordField;
import static ru.mystamps.web.tests.page.element.Form.submitButton;

public class ActivateAccountPage extends AbstractPageWithForm {
	
	public ActivateAccountPage(WebDriver driver) {
		super(driver, Url.ACTIVATE_ACCOUNT_PAGE);
		
		hasForm(
			with(
				required(inputField("login"))
					.withLabel(tr("t_login"))
					.and().
					invalidValue("x"),
				
				inputField("name")
					.withLabel(tr("t_name"))
					.and()
					.invalidValue("x"),
				
				required(passwordField("password"))
					.withLabel(tr("t_password"))
					.and()
					.invalidValue("x"),
				
				required(passwordField("passwordConfirmation"))
					.withLabel(tr("t_password_again"))
					.and()
					.invalidValue("x"),
				
				required(inputField("activationKey"))
					.withLabel(tr("t_activation_key"))
					.and()
					.invalidValue("x")
			)
			.and()
			.with(submitButton(tr("t_activate")))
		);
	}
	
	public void activateAccount(
			String login,
			String name,
			String password,
			String passwordConfirmation,
			String activationKey) {
		
		Collection<String> fieldNames = Arrays.asList(
			login, name, password, passwordConfirmation, activationKey
		);
		
		Validate.validState(
			exists(fieldNames, notNullPredicate()),
			"Login, name, password with confirmation and activation key should not be null"
		);
		
		fillLogin(login);
		fillName(name);
		fillPassword(password);
		fillPasswordConfirmation(passwordConfirmation);
		fillActivationKey(activationKey);
		submit();
	}
	
	public boolean activationFormExists() {
		return elementWithIdExists("activateAccountForm");
	}
	
	private void fillLogin(String login) {
		if (login != null) {
			fillField("login", login);
		}
	}
	
	private void fillName(String name) {
		if (name != null) {
			fillField("name", name);
		}
	}
	
	private void fillPassword(String password) {
		if (password != null) {
			fillField("password", password);
		}
	}
	
	private void fillPasswordConfirmation(String passwordConfirmation) {
		if (passwordConfirmation != null) {
			fillField("passwordConfirmation", passwordConfirmation);
		}
	}
	
	private void fillActivationKey(String activationKey) {
		if (activationKey != null) {
			fillField("activationKey", activationKey);
		}
	}
	
}
