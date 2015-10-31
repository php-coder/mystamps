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

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.Url;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
public class AddSeriesPage {
	
	private final WebDriver driver;
	
	@FindBy(id = "category")
	private WebElement categoryField;
	
	public void open() {
		PageFactory.initElements(driver, this);
		driver.navigate().to(Url.SITE + Url.ADD_SERIES_PAGE);
	}
	
	public List<String> getValuesByFieldName(String fieldName) {
		Select select = new Select(fieldNameToField(fieldName));
		
		return select
			.getOptions()
			.stream()
			.map(WebElement::getText)
			.collect(toList());
	}
	
	private WebElement fieldNameToField(String fieldName) {
		return fieldNameToElement(fieldName);
	}
	
	private WebElement fieldNameToElement(String fieldName) {
		switch (fieldName) {
			case "Category":
				return categoryField;
			default:
				throw new IllegalStateException("Unknown field name: " + fieldName);
		}
	}
	
}
