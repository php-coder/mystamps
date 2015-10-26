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
package ru.mystamps.web.tests.cases;

import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Value;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.fest.assertions.api.Assertions.assertThat;

import ru.mystamps.web.Url;
import ru.mystamps.web.tests.WebDriverFactory;
import ru.mystamps.web.tests.page.AddCategoryPage;
import ru.mystamps.web.tests.page.AddSeriesPage;

import static ru.mystamps.web.tests.TranslationUtils.tr;

public class WhenAdminAddCategory extends WhenAnyUserAtAnyPageWithForm<AddCategoryPage> {
	
	private static final String TEST_CATEGORY_NAME_EN = "Space";
	private static final String TEST_CATEGORY_NAME_RU = "Космос";
	
	@Value("${valid_admin_login}")
	private String validAdminLogin;
	
	@Value("${valid_admin_password}")
	private String validAdminPassword;
	
	public WhenAdminAddCategory() {
		super(AddCategoryPage.class);
		hasTitle(tr("t_create_category"));
		hasHeader(StringUtils.capitalize(tr("t_create_category")));
	}
	
	@BeforeClass
	public void login() {
		page.login(validAdminLogin, validAdminPassword);
	}
	
	@BeforeMethod
	public void openPage() {
		page.open();
	}
	
	@AfterClass(alwaysRun = true)
	public void tearDown() {
		page.logout();
	}
	
	@Test(groups = "std")
	public void shouldHaveStandardStructure() {
		checkStandardStructure();
	}
	
	@Test(groups = "logic", dependsOnGroups = { "std", "invalid", "valid", "misc" })
	public void shouldBeRedirectedToPageWithInfoAboutCategoryAfterCreation() {
		page.addCategory(TEST_CATEGORY_NAME_EN, TEST_CATEGORY_NAME_RU);
		
		String expectedUrl = Url.INFO_CATEGORY_PAGE
			.replace("{id}", "\\d+")
			.replace("{slug}", TEST_CATEGORY_NAME_EN.toLowerCase());
		
		assertThat(page.getCurrentUrl()).matches(expectedUrl);
		assertThat(page.getHeader()).isEqualTo(TEST_CATEGORY_NAME_EN);
	}
	
	@Test(
		groups = "logic",
		dependsOnMethods = "shouldBeRedirectedToPageWithInfoAboutCategoryAfterCreation"
	)
	public void categoryShouldBeAvailableForChoosingAtPageWithSeries() {
		page.open(Url.ADD_SERIES_PAGE);
		
		AddSeriesPage seriesPage = new AddSeriesPage(WebDriverFactory.getDriver());
		
		assertThat(seriesPage.getCategoryFieldValues()).contains(TEST_CATEGORY_NAME_EN);
	}
	
	@Override
	protected void checkServerResponseCode() {
		// Ignore this check because server always returns 401 for anonymous user and our test suite
		// lack ability to check response code after authentication.
	}
	
	@Override
	protected void shouldHaveUserBar() {
		// Ignore this check because when user authenticated there is no links for login/register.
	}
	
}
