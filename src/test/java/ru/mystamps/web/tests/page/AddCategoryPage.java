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

import org.apache.commons.lang3.Validate;

import org.openqa.selenium.WebDriver;

import ru.mystamps.web.Url;

import static ru.mystamps.web.tests.TranslationUtils.tr;
import static ru.mystamps.web.tests.page.element.Form.inputField;
import static ru.mystamps.web.tests.page.element.Form.required;
import static ru.mystamps.web.tests.page.element.Form.submitButton;
import static ru.mystamps.web.tests.page.element.Form.with;

public class AddCategoryPage extends AbstractPageWithForm {
	
	public AddCategoryPage(WebDriver driver) {
		super(driver, Url.ADD_CATEGORY_PAGE);
		
		hasForm(
			with(
				required(inputField("name")).withLabel(tr("t_category_on_english")),
				inputField("nameRu").withLabel(tr("t_category_on_russian"))
			)
			.and()
			.with(submitButton(tr("t_add")))
		);
	}
	
	public void addCategory(String nameEn, String nameRu) {
		Validate.validState(nameEn != null, "Category name in English must be non null");
		
		fillNameEn(nameEn);
		fillNameRu(nameRu);
		submit();
	}
	
	private void fillNameEn(String name) {
		if (name != null) {
			fillField("name", name);
		}
	}
	
	private void fillNameRu(String name) {
		if (name != null) {
			fillField("nameRu", name);
		}
	}
	
}
