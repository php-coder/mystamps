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

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.Url;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
public class AddSeriesPage {
	
	private static final String COUNTRY_OPTION_LOCATOR =
		"//*[contains(@class, \"selectize-control\")]"
		+ "/*[contains(@class, \"selectize-dropdown\")]"
		+ "/*[contains(@class, \"selectize-dropdown-content\")]"
		+ "/*[contains(@class, \"option\")]";
	
	// in seconds
	private static final int WAITING_FOR_ELEMENT_TIMEOUT = 5;
	
	private final WebDriver driver;
	
	@FindBy(id = "category")
	private WebElement categoryField;
	
	@FindBy(id = "category.errors")
	private WebElement categoryErrorMessage;
	
	@FindBy(className = "selectize-input")
	private List<WebElement> selectizedFields;
	
	@FindBy(id = "quantity")
	private WebElement quantityField;
	
	@FindBy(id = "quantity.errors")
	private WebElement quantityErrorMessage;
	
	@FindBy(id = "image")
	private WebElement imageField;
	
	@FindBy(id = "image.errors")
	private WebElement imageErrorMessage;
	
	@FindBy(id = "add-catalog-numbers-link")
	private WebElement catalogNumbersLink;
	
	@FindBy(id = "michelNumbers")
	private WebElement michelNumbersField;
	
	@FindBy(id = "michelNumbers.errors")
	private WebElement michelNumbersErrorMessage;
	
	@FindBy(id = "scottNumbers")
	private WebElement scottNumbersField;
	
	@FindBy(id = "scottNumbers.errors")
	private WebElement scottNumbersErrorMessage;
	
	@FindBy(id = "yvertNumbers")
	private WebElement yvertNumbersField;
	
	@FindBy(id = "yvertNumbers.errors")
	private WebElement yvertNumbersErrorMessage;
	
	@FindBy(id = "gibbonsNumbers")
	private WebElement gibbonsNumbersField;
	
	@FindBy(id = "gibbonsNumbers.errors")
	private WebElement gibbonsNumbersErrorMessage;
	
	@FindBy(id = "add-series-btn")
	private WebElement addSeriesButton;
	
	public void open() {
		PageFactory.initElements(driver, this);
		driver.navigate().to(Url.SITE + Url.ADD_SERIES_PAGE);
	}
	
	public void fillFieldByName(String fieldName, String value) {
		clearAndTypeIntoField(fieldNameToField(fieldName), value);
	}
	
	public void showSection(String section) {
		switch (section) {
			case "Add information from stamps catalogues":
				catalogNumbersLink.click();
				break;
			default:
				throw new IllegalStateException("Unknown section name: " + section);
		}
	}
	
	public void submitForm() {
		addSeriesButton.submit();
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
	
	public List<String> getValuesByFieldName(String fieldName) {
		if ("Country".equals(fieldName)) {
			return getCountryFieldValues();
		}
		
		Select select = new Select(fieldNameToField(fieldName));
		
		return select
			.getOptions()
			.stream()
			.map(WebElement::getText)
			.collect(toList());
	}
	
	// TODO: move to helper or parent
	private static void clearAndTypeIntoField(WebElement element, String value) {
		element.clear();
		element.sendKeys(value);
	}
	
	private List<String> getCountryFieldValues() {
		WebElement countryField = selectizedFields.get(0);
		
		countryField.click();
		
		return new WebDriverWait(driver, WAITING_FOR_ELEMENT_TIMEOUT)
			.until(
				ExpectedConditions.visibilityOfAllElementsLocatedBy(
					By.xpath(COUNTRY_OPTION_LOCATOR)
				)
			)
			.stream()
			.map(WebElement::getText)
			.collect(toList());
	}
	
	private WebElement fieldNameToField(String fieldName) {
		return fieldNameToElement(fieldName, false);
	}
	
	private WebElement fieldNameToErrorMessage(String fieldName) {
		return fieldNameToElement(fieldName, true);
	}
	
	private WebElement fieldNameToElement(String fieldName, boolean toErrorMessage) {
		switch (fieldName) {
			case "Category":
				return toErrorMessage ? categoryErrorMessage : categoryField;
			case "Quantity":
				return toErrorMessage ? quantityErrorMessage : quantityField;
			case "Image":
				return toErrorMessage ? imageErrorMessage : imageField;
			case "Michel":
				return toErrorMessage ? michelNumbersErrorMessage : michelNumbersField;
			case "Scott":
				return toErrorMessage ? scottNumbersErrorMessage : scottNumbersField;
			case "Yvert":
				return toErrorMessage ? yvertNumbersErrorMessage : yvertNumbersField;
			case "Gibbons":
				return toErrorMessage ? gibbonsNumbersErrorMessage : gibbonsNumbersField;
			default:
				throw new IllegalStateException("Unknown field name: " + fieldName);
		}
	}
	
}
