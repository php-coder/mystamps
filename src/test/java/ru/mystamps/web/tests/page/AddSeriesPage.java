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

import java.util.List;

import org.apache.commons.lang3.Validate;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import ru.mystamps.web.Url;

import static java.util.stream.Collectors.toList;

import static ru.mystamps.web.tests.TranslationUtils.tr;
import static ru.mystamps.web.tests.page.element.Form.with;
import static ru.mystamps.web.tests.page.element.Form.inputField;
import static ru.mystamps.web.tests.page.element.Form.checkboxField;
import static ru.mystamps.web.tests.page.element.Form.required;
import static ru.mystamps.web.tests.page.element.Form.selectField;
import static ru.mystamps.web.tests.page.element.Form.textareaField;
import static ru.mystamps.web.tests.page.element.Form.uploadFileField;
import static ru.mystamps.web.tests.page.element.Form.submitButton;

public class AddSeriesPage extends AbstractPageWithForm {
	
	private static final String COUNTRY_OPTION_LOCATOR =
		"//*[contains(@class, \"selectize-control\")]"
		+ "/*[contains(@class, \"selectize-dropdown\")]"
		+ "/*[contains(@class, \"selectize-dropdown-content\")]"
		+ "/*[contains(@class, \"option\")]";
	
	private static final String COUNTRY_BY_NAME_OPTION_LOCATOR_FMT =
		"//*[contains(@class, \"selectize-control\")]"
		+ "/*[contains(@class, \"selectize-dropdown\")]"
		+ "/*[contains(@class, \"selectize-dropdown-content\")]"
		+ "/*[contains(@class, \"option\") and text()='%s']";
	
	// in seconds
	private static final int WAITING_FOR_ELEMENT_TIMEOUT = 5;
	
	public AddSeriesPage(WebDriver driver) {
		super(driver, Url.ADD_SERIES_PAGE);
		
		hasForm(
			with(
				selectField("country"),
				selectField("day"),
				selectField("month"),
				selectField("year"),
				required(inputField("quantity")),
				checkboxField("perforated"),
				inputField("michelNumbers"),
				inputField("michelPrice"),
				inputField("scottNumbers"),
				inputField("scottPrice"),
				inputField("yvertNumbers"),
				inputField("yvertPrice"),
				inputField("gibbonsNumbers"),
				inputField("gibbonsPrice"),
				inputField("solovyovNumbers"),
				inputField("solovyovPrice"),
				inputField("zagorskiNumbers"),
				inputField("zagorskiPrice"),
				textareaField("comment").accessibleByAll(false),
				uploadFileField("image")
			)
			.and()
			.with(submitButton(tr("t_add")))
		);
	}
	
	@Override
	public AbstractPage submit() {
		AbstractPage nextPage = super.submit();
		if (nextPage == null) {
			nextPage = new InfoSeriesPage(driver);
		}
		
		return nextPage;
	}

	public List<String> getCategoryFieldValues() {
		return getSelectOptions("category");
	}
	
	public void fillCategory(String value) {
		if (value != null) {
			new Select(getElementByName("category")).selectByVisibleText(value);
		}
	}
	
	public List<String> getCountryFieldValues() {
		List<WebElement> selectizedFields = getElementsByClassName("selectize-input");
		Validate.isTrue(
			selectizedFields.size() == 1,
			"At page for series creation must be exactly one selectized field"
		);
		
		selectizedFields.get(0).click();
		
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
	
	public void fillCountry(String value) {
		if (value == null) {
			return;
		}
		
		List<WebElement> selectizedFields = getElementsByClassName("selectize-input");
		Validate.isTrue(
			selectizedFields.size() == 1,
			"At page for series creation must be exactly one selectized field"
		);
		
		selectizedFields.get(0).click();
		
		new WebDriverWait(driver, WAITING_FOR_ELEMENT_TIMEOUT)
			.until(
				ExpectedConditions.visibilityOfElementLocated(
					By.xpath(String.format(COUNTRY_BY_NAME_OPTION_LOCATOR_FMT, value))
				)
			)
			.click();
	}
	
	public List<String> getYearFieldValues() {
		return getSelectOptions("year");
	}
	
	public void fillDay(String value) {
		if (value != null) {
			new Select(getElementByName("day")).selectByVisibleText(value);
		}
	}
	
	public void fillMonth(String value) {
		if (value != null) {
			new Select(getElementByName("month")).selectByVisibleText(value);
		}
	}
	
	public void fillYear(String value) {
		if (value != null) {
			new Select(getElementByName("year")).selectByVisibleText(value);
		}
	}
	
	public void fillQuantity(String value) {
		if (value != null) {
			fillField("quantity", value);
		}
	}
	
	public void fillPerforated(boolean turnOn) {
		WebElement el = getElementByName("perforated");
		if (el.isSelected()) {
			if (!turnOn) {
				el.click();
			}
			
		} else {
			if (turnOn) {
				el.click();
			}
		}
	}
	
	public void showDateOfRelease() {
		getElementById("specify-issue-date-link").click();
	}
	
	public void showCatalogNumbers() {
		getElementById("add-catalog-numbers-link").click();
	}
	
	public void fillMichelNumbers(String value) {
		if (value != null) {
			fillField("michelNumbers", value);
		}
	}
	
	public void fillMichelPrice(String value) {
		if (value != null) {
			fillField("michelPrice", value);
		}
	}
	
	public void fillScottNumbers(String value) {
		if (value != null) {
			fillField("scottNumbers", value);
		}
	}
	
	public void fillScottPrice(String value) {
		if (value != null) {
			fillField("scottPrice", value);
		}
	}
	
	public void fillYvertNumbers(String value) {
		if (value != null) {
			fillField("yvertNumbers", value);
		}
	}
	
	public void fillYvertPrice(String value) {
		if (value != null) {
			fillField("yvertPrice", value);
		}
	}
	
	public void fillGibbonsNumbers(String value) {
		if (value != null) {
			fillField("gibbonsNumbers", value);
		}
	}
	
	public void fillGibbonsPrice(String value) {
		if (value != null) {
			fillField("gibbonsPrice", value);
		}
	}
	
	public void fillSolovyovNumbers(String value) {
		if (value != null) {
			fillField("solovyovNumbers", value);
		}
	}
	
	public void fillSolovyovPrice(String value) {
		if (value != null) {
			fillField("solovyovPrice", value);
		}
	}
	
	public void fillZagorskiNumbers(String value) {
		if (value != null) {
			fillField("zagorskiNumbers", value);
		}
	}
	
	public void fillZagorskiPrice(String value) {
		if (value != null) {
			fillField("zagorskiPrice", value);
		}
	}
	
	public void showComment() {
		getElementById("add-comment-link").click();
	}
	
	public void fillComment(String value) {
		if (value != null) {
			fillField("comment", value);
		}
	}
	
	public void fillImage(String value) {
		if (value != null) {
			fillField("uploadedImage", value);
		}
	}
	
}
