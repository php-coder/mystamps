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
package ru.mystamps.web.it.step;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import ru.mystamps.web.it.page.AddCategoryPage;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AddCategorySteps {
	
	// TODO: generate to make them unique
	private static final String VALID_CATEGORY_NAME_EN = "Space";
	private static final String VALID_CATEGORY_NAME_RU = "Космос";
	
	private static final String INVALID_CATEGORY_NAME_EN = "";
	private static final String INVALID_CATEGORY_NAME_RU = "";
	
	private final AddCategoryPage page;
	
	@Autowired
	public AddCategorySteps(AddCategoryPage page) {
		this.page = page;
	}
	
	@When("^I open create category page$")
	public void openCreateCategoryPage() {
		page.open();
	}
	
	@And("^I fill create category form with valid values$")
	public void fillFormWithValidValues() {
		page.fillForm(VALID_CATEGORY_NAME_EN, VALID_CATEGORY_NAME_RU);
	}
	
	@And("^I fill create category form with invalid values$")
	public void fillFormWithInvalidValues() {
		page.fillForm(INVALID_CATEGORY_NAME_EN, INVALID_CATEGORY_NAME_RU);
	}
	
	@And("^I fill field \"([^\"]*)\" with value \"([^\"]*)\" in create category form$")
	public void fillField(String fieldName, String value) {
		page.fillFieldByName(fieldName, value);
	}
	
	@And("^I submit create category form$")
	public void submitForm() {
		page.submitForm();
	}
	
	@Then("^I see that field \"([^\"]*)\" has error \"([^\"]*)\" in create category form$")
	public void fieldShouldHaveAnError(String fieldName, String errorMessage) {
		assertThat(page.getErrorByFieldName(fieldName), is(equalTo(errorMessage)));
	}
	
	@Then("^I see that field \"([^\"]*)\" has no error in create category form$")
	public void fieldShouldNotHaveAnError(String fieldName) {
		assertThat(page.fieldHasError(fieldName), is(false));
	}
	
	@Then("^I see that field \"([^\"]*)\" has value \"([^\"]*)\" in create category form$")
	public void fieldShouldHaveValue(String fieldName, String expectedValue) {
		assertThat(page.getValueByFieldName(fieldName), is(equalTo(expectedValue)));
	}
	
}
