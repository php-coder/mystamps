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
package ru.mystamps.web.it.step;

import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;

import ru.mystamps.web.it.page.AddSeriesPage;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import static ru.mystamps.web.validation.ValidationRules.MAX_SERIES_COMMENT_LENGTH;

public class AddSeriesSteps {
	
	private final AddSeriesPage page;
	
	@Autowired
	public AddSeriesSteps(AddSeriesPage page) {
		this.page = page;
	}
	
	@Then("^I open add series page$")
	public void openAddSeriesPage() {
		page.open();
	}
	
	@And("^Field \"([^\"]*)\" in create series form contains \"([^\"]*)\"$")
	public void fieldShouldContainsValue(String fieldName, String value) {
		assertThat(page.getValuesByFieldName(fieldName), hasItem(value));
	}
	
	@And("^I fill field \"([^\"]*)\" with value \"([^\"]*)\" in add series form$")
	public void fillField(String fieldName, String value) {
		page.fillFieldByName(fieldName, value);
	}
	
	@And("^I fill field \"([^\"]*)\" with too long text in add series form$")
	public void fillFieldWithLongText(String fieldName) {
		if (!"Comment".equals(fieldName)) {
			throw new IllegalStateException("Unknown field " + fieldName);
		}
		
		String veryLongText = StringUtils.repeat("x", MAX_SERIES_COMMENT_LENGTH + 1);
		page.fillFieldByName(fieldName, veryLongText);
	}
	
	@And("^I show up \"([^\"]*)\" section at add series page$")
	public void showUpSection(String section) {
		page.showSection(section);
	}
	
	@And("^I submit add series form$")
	public void submitForm() {
		page.submitForm();
	}
	
	@Then("^I see that field \"([^\"]*)\" has error \"([^\"]*)\" in add series form$")
	public void fieldShouldHaveAnError(String fieldName, String errorMessage) {
		assertThat(page.getErrorByFieldName(fieldName), is(equalTo(errorMessage)));
	}
	
	@Then("^I see that field \"([^\"]*)\" has no error in add series form$")
	public void fieldShouldNotHaveAnError(String fieldName) {
		assertThat(page.fieldHasError(fieldName), is(false));
	}
	
	@Then("^I see that field \"([^\"]*)\" has value \"([^\"]*)\" in add series form$")
	public void fieldShouldHaveValue(String fieldName, String expectedValue) {
		assertThat(page.getValueByFieldName(fieldName), is(equalTo(expectedValue)));
	}
	
}
