/*
 * Copyright (C) 2009-2012 Slava Semushin <slava.semushin@gmail.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

package ru.mystamps.web.tests.page;

import org.openqa.selenium.WebDriver;

import static com.google.common.base.Preconditions.checkState;

import static ru.mystamps.web.SiteMap.ADD_COUNTRY_PAGE_URL;

import static ru.mystamps.web.tests.TranslationUtils.tr;
import static ru.mystamps.web.tests.page.element.Form.with;
import static ru.mystamps.web.tests.page.element.Form.required;
import static ru.mystamps.web.tests.page.element.Form.inputField;
import static ru.mystamps.web.tests.page.element.Form.submitButton;

public class AddCountryPage extends AbstractPageWithForm {
	
	public AddCountryPage(final WebDriver driver) {
		super(driver, ADD_COUNTRY_PAGE_URL);
		
		hasForm(
			with(
				required(inputField("country")).withLabel(tr("t_country"))
			)
			.and()
			.with(submitButton(tr("t_add")))
		);
	}
	
	public void addCountry(final String countryName) {
		checkState(countryName != null, "Country name should be non null");
		
		fillName(countryName);
		submit();
	}
	
	private void fillName(final String name) {
		if (name != null) {
			fillField("country", name);
		}
	}
	
}
