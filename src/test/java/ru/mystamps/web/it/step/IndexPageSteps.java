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

import java.util.List;

import cucumber.api.java.en.And;
import cucumber.api.java.en.But;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import ru.mystamps.web.it.page.IndexPage;

import static org.junit.Assert.assertThat;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

import static ru.mystamps.web.tests.TranslationUtils.tr;

public class IndexPageSteps {
	
	private final IndexPage page;
	
	public IndexPageSteps() {
		page = new IndexPage();
	}
	
	@When("^I open index page$")
	public void openIndexPage() {
		page.open();
	}
	
	@Then("^I see welcome text$")
	public void shouldSeeWelcomeText() {
		assertThat(page.getWelcomeText(), containsString(tr("t_you_may")));
	}
	
	@And("^I see (\\d+) navigation links$")
	public void shouldHaveNavigationLinks(int size) {
		List<String> navigationLinks = page.getNavigationLinks();
		String msg = String.format("List of links (%s) should has size %d", navigationLinks, size);
		
		assertThat(msg, navigationLinks.size(), is(equalTo(size)));
	}
	
	@And("^I see link for list of categories$")
	public void shouldSeeLinkForListOfCategories() {
		assertThat(page.getNavigationLinks(), hasItem(tr("t_show_categories_list")));
	}
	
	@And("^I see link for list of countries$")
	public void shouldSeeLinkForListOfCountries() {
		assertThat(page.getNavigationLinks(), hasItem(tr("t_show_countries_list")));
	}
	
	@But("^I don't see link for adding series$")
	public void shouldNotSeeLinkForAddingSeries() {
		assertThat(page.getNavigationLinks(), not(hasItem(tr("t_add_series"))));
	}
	
	@But("^I don't see link for adding categories$")
	public void shouldNotSeeLinkForAddingCategories() {
		assertThat(page.getNavigationLinks(), not(hasItem(tr("t_create_category"))));
	}
	
	@But("^I don't see link for adding countries$")
	public void shouldNotSeeLinkForAddingCountries() {
		assertThat(page.getNavigationLinks(), not(hasItem(tr("t_add_country"))));
	}
	
}
