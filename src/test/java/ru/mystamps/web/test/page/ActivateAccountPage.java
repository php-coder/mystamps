/*
 * Copyright (C) 2009-2017 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.test.page;

import org.openqa.selenium.WebDriver;

import ru.mystamps.web.Url;

import org.apache.commons.lang3.Validate;

import static ru.mystamps.web.test.TranslationUtils.tr;
import static ru.mystamps.web.test.page.element.Form.with;
import static ru.mystamps.web.test.page.element.Form.required;
import static ru.mystamps.web.test.page.element.Form.inputField;
import static ru.mystamps.web.test.page.element.Form.passwordField;
import static ru.mystamps.web.test.page.element.Form.submitButton;

public class ActivateAccountPage extends AbstractPageWithForm {
	
	public ActivateAccountPage(WebDriver driver) {
		super(driver, Url.ACTIVATE_ACCOUNT_PAGE);
		
		hasForm(
			with(
				required(inputField("login")),
				inputField("name"),
				required(passwordField("password")),
				required(passwordField("passwordConfirmation")),
				required(inputField("activationKey"))
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
		
		Validate.validState(
			login != null
			|| name != null
			|| password != null
			|| passwordConfirmation != null
			|| activationKey != null,
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
