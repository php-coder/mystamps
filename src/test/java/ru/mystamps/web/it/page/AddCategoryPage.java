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
package ru.mystamps.web.it.page;

import org.apache.commons.lang3.StringUtils;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.Url;

@RequiredArgsConstructor
public class AddCategoryPage {
	
	private final WebDriver driver;
	
	@FindBy(id = "name")
	private WebElement nameField;
	
	@FindBy(id = "name.errors")
	private WebElement nameErrorMessage;
	
	@FindBy(id = "nameRu")
	private WebElement nameRuField;
	
	@FindBy(id = "nameRu.errors")
	private WebElement nameRuErrorMessage;
	
	@FindBy(id = "add-category-btn")
	private WebElement addCategoryButton;
	
	public void open() {
		PageFactory.initElements(driver, this);
		driver.navigate().to(Url.SITE + Url.ADD_CATEGORY_PAGE);
	}
	
	public void fillForm(String nameEn, String nameRu) {
		clearAndTypeIntoField(nameField, nameEn);
		clearAndTypeIntoField(nameRuField, nameRu);
	}
	
	public void fillFieldByName(String fieldName, String value) {
		clearAndTypeIntoField(fieldNameToField(fieldName), value);
	}
	
	public void submitForm() {
		addCategoryButton.submit();
	}
	
	public boolean fieldHasError(String fieldName) {
		try {
			return StringUtils.isNotEmpty(getErrorByFieldName(fieldName));
		} catch (NoSuchElementException ex) {
			return false;
		}
	}
	
	public String getErrorByFieldName(String fieldName) {
		return fieldNameToErrorMessage(fieldName).getText();
	}
	
	public String getValueByFieldName(String fieldName) {
		return fieldNameToField(fieldName).getAttribute("value");
	}
		
	// TODO: move to helper or parent
	private static void clearAndTypeIntoField(WebElement element, String value) {
		element.clear();
		element.sendKeys(value);
	}
	
	private WebElement fieldNameToField(String fieldName) {
		return fieldNameToElement(fieldName, false);
	}
	
	private WebElement fieldNameToErrorMessage(String fieldName) {
		return fieldNameToElement(fieldName, true);
	}
	
	private WebElement fieldNameToElement(String fieldName, boolean toErrorMessage) {
		switch (fieldName) {
			case "Name (in English)":
				return toErrorMessage ? nameErrorMessage : nameField;
			case "Name (in Russian)":
				return toErrorMessage ? nameRuErrorMessage : nameRuField;
			default:
				throw new IllegalStateException("Unknown field name: " + fieldName);
		}
	}
	
}
