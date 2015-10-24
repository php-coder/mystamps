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

import org.apache.commons.lang3.StringUtils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ErrorPage {
	
	private final WebDriver driver;
	
	@FindBy(id = "error-msg")
	private WebElement errorMessage;
	
	@FindBy(id = "error-code")
	private WebElement errorCode;
	
	public void open(String url) {
		driver.navigate().to(url);
	}
	
	public String getErrorMessage() {
		return StringUtils.replace(errorMessage.getText(), "\n", " ");
	}
	
	public String getErrorCode() {
		return errorCode.getText();
	}
	
}
