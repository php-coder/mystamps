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

package ru.mystamps.web.tests.page;

import org.openqa.selenium.WebDriver;

import static ru.mystamps.web.SiteMap.ACTIVATE_ACCOUNT_PAGE_URL;

import static ru.mystamps.web.tests.TranslationUtils.tr;
import static ru.mystamps.web.tests.page.element.Form.with;
import static ru.mystamps.web.tests.page.element.Form.required;
import static ru.mystamps.web.tests.page.element.Form.inputField;
import static ru.mystamps.web.tests.page.element.Form.passwordField;
import static ru.mystamps.web.tests.page.element.Form.submitButton;

public class ActivateAccountPage extends AbstractPageWithForm {
	
	public ActivateAccountPage(final WebDriver driver) {
		super(driver, ACTIVATE_ACCOUNT_PAGE_URL);
		
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
				
				required(passwordField("passwordConfirm"))
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
			final String login,
			final String name,
			final String password,
			final String passwordConfirmation,
			final String activationKey) {
		
		if (login == null
			&& name == null
			&& password == null
			&& passwordConfirmation == null
			&& activationKey == null) {
			
			throw new IllegalStateException(
				"Login, name, password with confirmation and activation key should not be null"
			);
		}
		
		fillLogin(login);
		fillName(name);
		fillPassword(password);
		fillPasswordConfirmation(passwordConfirmation);
		fillActivationKey(activationKey);
		submit();
	}
	
	private void fillLogin(final String login) {
		if (login != null) {
			fillField("login", login);
		}
	}
	
	private void fillName(final String name) {
		if (name != null) {
			fillField("name", name);
		}
	}
	
	private void fillPassword(final String password) {
		if (password != null) {
			fillField("password", password);
		}
	}
	
	private void fillPasswordConfirmation(final String passwordConfirmation) {
		if (passwordConfirmation != null) {
			fillField("passwordConfirm", passwordConfirmation);
		}
	}
	
	private void fillActivationKey(final String activationKey) {
		if (activationKey != null) {
			fillField("activationKey", activationKey);
		}
	}
	
}
