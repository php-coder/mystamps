/*
 * Copyright (C) 2009-2017 Slava Semushin <slava.semushin@gmail.com>
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

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

import lombok.Getter;

import org.apache.commons.lang3.Validate;

import ru.mystamps.web.tests.WebElementUtils;
import ru.mystamps.web.tests.page.element.Form;
import ru.mystamps.web.tests.page.element.Form.Field;
import ru.mystamps.web.tests.page.element.Form.SubmitButton;

public abstract class AbstractPageWithForm extends AbstractPage {
	
	// CheckStyle: ignore LineLength for next 1 line
	private static final String          LABEL_LOCATOR = "//label[@for=\"%s\"]/span[@class=\"field-label\"]";
	private static final String    FIELD_ERROR_LOCATOR = "//span[@id=\"%s.errors\"]";
	private static final String FIELD_REQUIRED_LOCATOR = "//span[@id=\"%s.required\"]";
	private static final String     FORM_ERROR_LOCATOR = "//div[@id=\"form.errors\"]";
	
	@Getter private Form form;
	
	public AbstractPageWithForm(WebDriver driver, String pageUrl) {
		super(driver, pageUrl);
	}
	
	protected void hasForm(Form form) {
		this.form = form;
	}
	
	public boolean isFieldExists(Field field) {
		return elementWithXPathExists(field.toString());
	}
	
	public boolean isFieldHasError(String id) {
		return elementWithXPathExists(String.format(FIELD_ERROR_LOCATOR, id));
	}
	
	public boolean isSubmitButtonExists(SubmitButton button) {
		return elementWithXPathExists(button.toString());
	}
	
	public AbstractPage submit() {
		
		Validate.validState(
			form != null,
			"You are trying to submit form at page which does not have form"
		);
		
		List<SubmitButton> buttons = form.getSubmitButtons();
		
		Validate.validState(
			!buttons.isEmpty(),
			"You are trying to submit form at page which does not have submit button"
		);
		
		String xpathOfFirstSubmitButton = buttons.get(0).toString();
		
		getElementByXPath(xpathOfFirstSubmitButton).submit();
		
		if (pageUrlChanged()) {
			return null;
		}
		
		return this;
	}
	
	public boolean formExists() {
		Validate.validState(
			form != null,
			"You are trying to check form at page which does not has form"
		);
		
		return elementWithXPathExists(form.toString());
	}
	
	public String getInputLabelValue(String id) {
		return getTextOfElementByXPath(String.format(LABEL_LOCATOR, id));
	}
	
	public boolean inputHasAsterisk(String id) {
		try {
			String requiredFieldMark =
				getTextOfElementByXPath(String.format(FIELD_REQUIRED_LOCATOR, id));
			
			return "*".equals(requiredFieldMark);
		} catch (NoSuchElementException ex) {
			return false;
		}
	}
	
	public String getFieldValue(String name) {
		Validate.validState(
			form != null,
			"You are trying to find field at page which does not have form"
		);
		
		String xpathField = form.getField(name).toString();
		
		return getElementByXPath(xpathField).getAttribute("value");
	}
	
	public String getFieldError(String id) {
		return getTextOfElementByXPath(String.format(FIELD_ERROR_LOCATOR, id));
	}
	
	public String getFormError() {
		return getTextOfElementByXPath(FORM_ERROR_LOCATOR);
	}
	
	public List<String> getFormHints() {
		return WebElementUtils.convertToListWithText(
			getElementsByClassName("hint_item")
		);
	}
	
	public List<String> getSelectOptions(String id) {
		return WebElementUtils.convertToListWithText(
			new Select(getElementById(id)).getOptions()
		);
	}
	
	public void fillField(String name, String value) {
		getElementByName(name).sendKeys(value);
	}
	
	public void clearField(String name) {
		getElementByName(name).clear();
	}
	
}
