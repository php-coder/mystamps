/*
 * Copyright (C) 2009-2014 Slava Semushin <slava.semushin@gmail.com>
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

import org.openqa.selenium.WebDriver;

import ru.mystamps.web.Url;

import org.apache.commons.lang3.Validate;

import static ru.mystamps.web.tests.TranslationUtils.tr;
import static ru.mystamps.web.tests.page.element.Form.with;
import static ru.mystamps.web.tests.page.element.Form.required;
import static ru.mystamps.web.tests.page.element.Form.inputField;
import static ru.mystamps.web.tests.page.element.Form.submitButton;

public class AddCountryPage extends AbstractPageWithForm {
	
	public AddCountryPage(WebDriver driver) {
		super(driver, Url.ADD_COUNTRY_PAGE);
		
		hasForm(
			with(
				required(inputField("name")).withLabel(tr("t_country"))
			)
			.and()
			.with(submitButton(tr("t_add")))
		);
	}
	
	public void addCountry(String countryName) {
		Validate.validState(countryName != null, "Country name should be non null");
		
		fillName(countryName);
		submit();
	}
	
	private void fillName(String name) {
		if (name != null) {
			fillField("name", name);
		}
	}
	
}
