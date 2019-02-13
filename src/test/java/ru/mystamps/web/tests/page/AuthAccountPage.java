/*
 * Copyright (C) 2009-2019 Slava Semushin <slava.semushin@gmail.com>
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

import org.apache.commons.lang3.Validate;
import org.openqa.selenium.WebDriver;
import ru.mystamps.web.Url;

import static ru.mystamps.web.tests.TranslationUtils.tr;
import static ru.mystamps.web.tests.page.element.Form.inputField;
import static ru.mystamps.web.tests.page.element.Form.passwordField;
import static ru.mystamps.web.tests.page.element.Form.required;
import static ru.mystamps.web.tests.page.element.Form.submitButton;
import static ru.mystamps.web.tests.page.element.Form.with;

public class AuthAccountPage extends AbstractPageWithForm {
	
	public AuthAccountPage(WebDriver driver) {
		super(driver, Url.AUTHENTICATION_PAGE);
		
		hasForm(
			with(
				required(inputField("login")),
				required(passwordField("password"))
			)
			.and()
			.with(submitButton(tr("t_enter")))
		);
	}
	
	public boolean authenticationFormExists() {
		// FIXME: probably better to check for form tag presence?
		return elementWithIdExists("auth-account-form");
	}
	
	public void authorizeUser(String login, String password) {
		Validate.validState(
			login != null || password != null,
			"Login and password should not be a null"
		);
		
		fillLogin(login);
		fillPassword(password);
		
		submit();
	}
	
	private void fillLogin(String login) {
		if (login != null) {
			clearField("login");
			fillField("login", login);
		}
	}
	
	private void fillPassword(String password) {
		if (password != null) {
			fillField("password", password);
		}
	}
	
}
