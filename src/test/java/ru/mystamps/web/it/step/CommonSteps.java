/*
 * Copyright (C) 2009-2015 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.it.step;

import cucumber.api.java.en.Given;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import ru.mystamps.web.it.page.AuthAccountPage;
import ru.mystamps.web.tests.WebDriverFactory;

public class CommonSteps {
	
	private final AuthAccountPage page;
	
	public CommonSteps() {
		WebDriver driver = WebDriverFactory.getDriver();
		page = PageFactory.initElements(driver, AuthAccountPage.class);
	}
	
	@Given("^As anonymous user$")
	public void becomeAnonymousUser() {
		page.open();
		
		logoutIfCurrentUserIsAuthenticated(false);
	}
	
	@Given("^As authenticated user$")
	public void becomeAuthenticatedUser() {
		page.open();
		
		logoutIfCurrentUserIsAuthenticated(true);
		
		// TODO: get login/password from spring context
		page.loginAs("coder", "test");
	}
	
	@Given("^As administrator$")
	public void becomeAdministrator() {
		page.open();
		
		logoutIfCurrentUserIsAuthenticated(true);
		
		// TODO: get login/password from spring context
		page.loginAs("admin", "test");
	}
	
	private void logoutIfCurrentUserIsAuthenticated(boolean returnToAuthPageAfterLogout) {
		if (page.hasInfoMessage()) {
			page.logout();
			if (returnToAuthPageAfterLogout) {
				page.open();
			}
		}
	}
	
}
