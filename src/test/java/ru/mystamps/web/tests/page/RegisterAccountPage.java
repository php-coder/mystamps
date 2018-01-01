/*
 * Copyright (C) 2009-2018 Slava Semushin <slava.semushin@gmail.com>
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

import org.openqa.selenium.WebDriver;

import ru.mystamps.web.Url;

import static ru.mystamps.web.tests.TranslationUtils.tr;
import static ru.mystamps.web.tests.page.element.Form.with;
import static ru.mystamps.web.tests.page.element.Form.required;
import static ru.mystamps.web.tests.page.element.Form.inputField;
import static ru.mystamps.web.tests.page.element.Form.submitButton;

public class RegisterAccountPage extends AbstractPageWithForm {
	
	public RegisterAccountPage(WebDriver driver) {
		super(driver, Url.REGISTRATION_PAGE);
		
		hasForm(
			with(required(inputField("email")))
			.and()
			.with(submitButton(tr("t_register")))
		);
	}
	
	public boolean registrationFormExists() {
		// TODO: probably better to check for form tag presence?
		return elementWithIdExists("registerAccountForm");
	}
	
	public void registerUser(String email) {
		fillEmail(email);
		submit();
	}
	
	private void fillEmail(String email) {
		fillField("email", email);
	}
	
}
