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
package ru.mystamps.web.it.page;

import org.apache.commons.lang3.Validate;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.Url;

@RequiredArgsConstructor
public class AuthAccountPage {
	
	private final WebDriver driver;
	
	@FindBy(id = "login")
	private WebElement loginField;
	
	@FindBy(id = "password")
	private WebElement passwordField;
	
	@FindBy(id = "sign-in-btn")
	private WebElement signInButton;
	
	public void open() {
		driver.navigate().to(Url.SITE + Url.AUTHENTICATION_PAGE);
	}
	
	public void loginAs(String login, String password) {
		loginField.sendKeys(login);
		passwordField.sendKeys(password);
		signInButton.submit();

		ensureCurrentPageUrl(Url.SITE + Url.INDEX_PAGE);
	}
	
	private void ensureCurrentPageUrl(String expectedUrl) {
		String currentUrl = driver.getCurrentUrl();
		Validate.validState(
			expectedUrl.equals(currentUrl),
			"Current URL should be %s but it's %s",
			expectedUrl,
			currentUrl
		);
	}
	
}
