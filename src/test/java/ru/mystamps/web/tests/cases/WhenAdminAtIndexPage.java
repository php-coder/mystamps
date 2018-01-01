/*
 * Copyright (C) 2009-2018 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.tests.cases;

import org.springframework.beans.factory.annotation.Value;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import ru.mystamps.web.tests.page.IndexSitePage;

import static org.fest.assertions.api.Assertions.assertThat;

import static ru.mystamps.web.tests.TranslationUtils.tr;

public class WhenAdminAtIndexPage extends WhenAnyUserAtAnyPage<IndexSitePage> {
	
	@Value("${valid_admin_login}")
	private String validAdminLogin;
	
	@Value("${valid_admin_password}")
	private String validAdminPassword;
	
	public WhenAdminAtIndexPage() {
		super(IndexSitePage.class);
	}
	
	@BeforeClass
	public void setUp() {
		page.open();
		page.login(validAdminLogin, validAdminPassword);
	}
	
	@AfterClass(alwaysRun = true)
	public void tearDown() {
		page.logout();
	}
	
	@Test(groups = "misc")
	public void shouldExistsWelcomeText() {
		assertThat(page.textPresent(tr("t_you_may"))).isTrue();
	}
	
	@Test(groups = "misc")
	public void shouldExistsLinkForAddingSeries() {
		assertThat(page.linkWithLabelExists(tr("t_add_series")))
			.overridingErrorMessage("should exists link to page for adding series of stamps")
			.isTrue();
	}
	
	@Test(groups = "misc")
	public void shouldExistsLinkForAddingCountries() {
		assertThat(page.linkWithLabelExists(tr("t_add_country")))
			.overridingErrorMessage("should exists link to page for adding countries")
			.isTrue();
	}
	
	@Test(groups = "misc")
	public void shouldExistsLinkForListingCountries() {
		assertThat(page.linkWithLabelExists(tr("t_show_countries_list")))
			.overridingErrorMessage("should exists link to page for listing countries")
			.isTrue();
	}
	
	@Test(groups = "misc")
	public void shouldExistsLinkForAddingCategories() {
		assertThat(page.linkWithLabelExists(tr("t_create_category")))
			.overridingErrorMessage("should exists link to page for adding categories")
			.isTrue();
	}
	
	@Test(groups = "misc")
	public void shouldExistsLinkForListingCategories() {
		assertThat(page.linkWithLabelExists(tr("t_show_categories_list")))
			.overridingErrorMessage("should exists link to page for listing categories")
			.isTrue();
	}
	
}
