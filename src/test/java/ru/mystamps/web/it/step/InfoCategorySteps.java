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

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;

import ru.mystamps.web.it.page.InfoCategoryPage;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class InfoCategorySteps {
	
	private final InfoCategoryPage page;
	
	@Autowired
	public InfoCategorySteps(InfoCategoryPage page) {
		this.page = page;
	}
	
	@Then("^I'm on a category info page$")
	public void shouldBeOnInfoCategoryPage() {
		assertThat(page.isAtInfoCategoryPage(), is(true));
	}
	
	@And("^I see a header \"([^\"]*)\" on category info page$")
	public void shouldHaveHeaderWithText(String header) {
		assertThat(page.getHeaderText(), is(equalTo(header)));
	}
	
}
