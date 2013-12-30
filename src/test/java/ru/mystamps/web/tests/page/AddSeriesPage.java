/*
 * Copyright (C) 2009-2013 Slava Semushin <slava.semushin@gmail.com>
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

import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import ru.mystamps.web.Url;

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
	
	public AddSeriesPage(WebDriver driver) {
		super(driver, Url.ADD_SERIES_PAGE);
		
		hasForm(
			with(
				selectField("country").withLabel(tr("t_country")),
				selectField("year").withLabel(tr("t_issue_year")),
				required(inputField("quantity").withLabel(tr("t_quantity"))),
				checkboxField("perforated").withLabel(tr("t_perforated")),
				inputField("michelNumbers").withLabel(tr("t_michel")),
				inputField("michelPrice"),
				selectField("michelCurrency"),
				inputField("scottNumbers").withLabel(tr("t_scott")),
				inputField("scottPrice"),
				selectField("scottCurrency"),
				inputField("yvertNumbers").withLabel(tr("t_yvert")),
				inputField("yvertPrice"),
				selectField("yvertCurrency"),
				inputField("gibbonsNumbers").withLabel(tr("t_sg")),
				inputField("gibbonsPrice"),
				selectField("gibbonsCurrency"),
				textareaField("comment").withLabel(tr("t_comment")).accessibleByAll(false),
				required(uploadFileField("image").withLabel(tr("t_image")))
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
	
	public List<String> getCountryFieldValues() {
		return getSelectOptions("country");
	}
	
	public void fillCountry(String value) {
		if (value != null) {
			new Select(getElementByName("country")).selectByVisibleText(value);
		}
	}
	
	public List<String> getYearFieldValues() {
		return getSelectOptions("year");
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
	
	public List<String> getMichelCurrencies() {
		return getSelectOptions("michelCurrency");
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
	
	public void fillMichelCurrency(String value) {
		if (value != null) {
			new Select(getElementByName("michelCurrency")).selectByVisibleText(value);
		}
	}
	
	public List<String> getScottCurrencies() {
		return getSelectOptions("scottCurrency");
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
	
	public void fillScottCurrency(String value) {
		if (value != null) {
			new Select(getElementByName("scottCurrency")).selectByVisibleText(value);
		}
	}
	
	public List<String> getYvertCurrencies() {
		return getSelectOptions("yvertCurrency");
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
	
	public void fillYvertCurrency(String value) {
		if (value != null) {
			new Select(getElementByName("yvertCurrency")).selectByVisibleText(value);
		}
	}
	
	public List<String> getGibbonsCurrencies() {
		return getSelectOptions("gibbonsCurrency");
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
	
	public void fillGibbonsCurrency(String value) {
		if (value != null) {
			new Select(getElementByName("gibbonsCurrency")).selectByVisibleText(value);
		}
	}
	
	public void fillComment(String value) {
		if (value != null) {
			fillField("comment", value);
		}
	}
	
	public void fillImage(String value) {
		if (value != null) {
			fillField("image", value);
		}
	}
	
}
