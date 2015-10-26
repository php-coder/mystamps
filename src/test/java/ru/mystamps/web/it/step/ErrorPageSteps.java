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

import ru.mystamps.web.Url;
import ru.mystamps.web.it.page.ErrorPage;

import static org.junit.Assert.assertThat;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

public class ErrorPageSteps {
	
	private final ErrorPage page;
	
	@Autowired
	public ErrorPageSteps(ErrorPage page) {
		this.page = page;
	}
	
	@When("^I open create category page but I don't have enough permissions$")
	public void openAddCategoryPage() {
		page.open(Url.SITE + Url.ADD_CATEGORY_PAGE);
	}
	
	@When("^I open create country page but I don't have enough permissions$")
	public void openAddCountryPage() {
		page.open(Url.SITE + Url.ADD_COUNTRY_PAGE);
	}
	
	@When("^I open non-existing category page$")
	public void openNonExistingCategoryPage() {
		String absentCategoryId = "888";
		String url = Url.SITE + Url.INFO_CATEGORY_PAGE
			.replace("{id}", absentCategoryId)
			.replace("{slug}", "category-404-error-test");
		
		page.open(url);
	}
	
	@When("^I open non-existing country page$")
	public void openNonExistingCountryPage() {
		String absentCountryId = "999";
		String url = Url.SITE + Url.INFO_COUNTRY_PAGE
			.replace("{id}", absentCountryId)
			.replace("{slug}", "country-404-error-test");
		
		page.open(url);
	}
	
	@Then("^I see error message \"([^\"]*)\"$")
	public void shouldSeeErrorMessage(String errorMessage) {
		assertThat(page.getErrorMessage(), is(equalTo(errorMessage)));
	}
	
	@And("^I see error code \"([^\"]*)\"$")
	public void shouldSeeErrorCode(String errorCode) {
		assertThat(page.getErrorCode(), is(equalTo(errorCode)));
	}
	
}
