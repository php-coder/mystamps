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

import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import org.openqa.selenium.StaleElementReferenceException;

import static org.fest.assertions.api.Assertions.assertThat;

import ru.mystamps.web.Url;
import ru.mystamps.web.tests.WebDriverFactory;
import ru.mystamps.web.tests.page.AddCountryPage;
import ru.mystamps.web.tests.page.AddSeriesPage;

import static ru.mystamps.web.tests.TranslationUtils.tr;

public class WhenAdminAddCountry extends WhenAnyUserAtAnyPageWithForm<AddCountryPage> {
	
	private static final String TEST_COUNTRY_NAME_EN = "Russia";
	private static final String TEST_COUNTRY_NAME_RU = "Россия";
	
	@Value("${valid_admin_login}")
	private String validAdminLogin;
	
	@Value("${valid_admin_password}")
	private String validAdminPassword;
	
	public WhenAdminAddCountry() {
		super(AddCountryPage.class);
		hasTitle(tr("t_add_country"));
		hasHeader(StringUtils.capitalize(tr("t_add_country")));
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
	public void shouldBeRedirectedToPageWithInfoAboutCountryAfterCreation() {
		page.addCountry(TEST_COUNTRY_NAME_EN, TEST_COUNTRY_NAME_RU);
		
		String expectedUrl = Url.INFO_COUNTRY_PAGE
			.replace("{id}", "\\d+")
			.replace("{slug}", TEST_COUNTRY_NAME_EN.toLowerCase());
		
		assertThat(page.getCurrentUrl()).matches(expectedUrl);
		assertThat(page.getHeader()).isEqualTo(TEST_COUNTRY_NAME_EN);
	}
	
	@Test(
		groups = "logic",
		dependsOnMethods = "shouldBeRedirectedToPageWithInfoAboutCountryAfterCreation"
	)
	public void countryShouldBeAvailableForChoosingAtPageWithSeries() {
		page.open(Url.ADD_SERIES_PAGE);
		
		AddSeriesPage seriesPage = new AddSeriesPage(WebDriverFactory.getDriver());
		
		try {
			assertThat(seriesPage.getCountryFieldValues()).contains(TEST_COUNTRY_NAME_EN);
			
		} catch (StaleElementReferenceException ex) {
			throw new SkipException(
				"Skipped because it fails with StaleElementReferenceException "
				+ "(see #280): " + ex.getMessage()
			);
		}
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
