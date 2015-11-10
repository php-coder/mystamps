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

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.Url;

@RequiredArgsConstructor
public class InfoCategoryPage {
	
	private final WebDriver driver;
	
	@FindBy(xpath = "//h3")
	private WebElement header;
	
	public boolean isAtInfoCategoryPage() {
		// It looks wrong to initialize page here, but I didn't find a better place
		PageFactory.initElements(driver, this);
		
		String urlRegexp = Url.SITE + Url.INFO_CATEGORY_PAGE
			.replace("{id}", "\\d+")
			.replace("{slug}", "\\w+");
		
		// TODO: move to the step and use regexp matchers
		return driver.getCurrentUrl().matches(urlRegexp);
	}
	
	public String getHeaderText() {
		return header.getText();
	}
	
}
