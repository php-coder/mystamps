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
import ru.mystamps.web.tests.page.AddCountryPage;
import ru.mystamps.web.tests.page.AddSeriesPage;

import static ru.mystamps.web.tests.TranslationUtils.tr;
import static ru.mystamps.web.tests.fest.AbstractPageWithFormAssert.assertThat;
import static ru.mystamps.web.validation.ValidationRules.COUNTRY_NAME_MAX_LENGTH;
import static ru.mystamps.web.validation.ValidationRules.COUNTRY_NAME_MIN_LENGTH;

public class WhenUserAddCountry extends WhenAnyUserAtAnyPageWithForm<AddCountryPage> {
	
	private static final String TEST_COUNTRY_NAME = "Russia";
	
	@Value("${valid_user_login}")
	private String validUserLogin;
	
	@Value("${valid_user_password}")
	private String validUserPassword;
	
	@Value("${valid_country_name}")
	private String validCountryName;
	
	public WhenUserAddCountry() {
		super(AddCountryPage.class);
		hasTitle(tr("t_add_country"));
		hasHeader(tr("t_add_country_ucfirst"));
	}
	
	@BeforeClass
	public void login() {
		page.login(validUserLogin, validUserPassword);
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
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void countryNameShouldNotBeTooShort() {
		page.addCountry("ee");
		
		assertThat(page)
			.field("name")
			.hasError(tr("value.too-short", COUNTRY_NAME_MIN_LENGTH));
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void countryNameShouldNotBeTooLong() {
		page.addCountry(StringUtils.repeat("e", COUNTRY_NAME_MAX_LENGTH + 1));
		
		assertThat(page)
			.field("name")
			.hasError(tr("value.too-long", COUNTRY_NAME_MAX_LENGTH));
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void countryNameShouldBeUnique() {
		page.addCountry(validCountryName);
		
		assertThat(page)
			.field("name")
			.hasError(tr("ru.mystamps.web.validation.jsr303.UniqueCountryName.message"));
	}
	
	@Test(groups = "valid", dependsOnGroups = "std")
	public void countryNameWithAllowedCharactersShouldBeAccepted() {
		page.addCountry("Valid-Name Country");
		
		assertThat(page).field("name").hasNoError();
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void countryNameWithForbiddenCharactersShouldBeRejected() {
		page.addCountry("S0m3+CountryN_ame");
		
		assertThat(page)
			.field("name")
			.hasError(tr("country-name.invalid"));
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void countryNameShouldNotStartsFromHyphen() {
		page.addCountry("-test");
		
		assertThat(page)
			.field("name")
			.hasError(tr("country-name.hyphen"));
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void countryNameShouldNotEndsWithHyphen() {
		page.addCountry("test-");
		
		assertThat(page)
			.field("name")
			.hasError(tr("country-name.hyphen"));
	}
	
	@Test(groups = "misc", dependsOnGroups = "std")
	public void countryNameShouldBeStripedFromLeadingAndTrailingSpaces() {
		page.addCountry(" t3st ");
		
		assertThat(page).field("name").hasValue("t3st");
	}
	
	@Test(groups = "logic", dependsOnGroups = { "std", "invalid", "valid", "misc" })
	public void shouldBeRedirectedToPageWithInfoAboutCountryAfterCreation() {
		page.addCountry(TEST_COUNTRY_NAME);
		
		String expectedUrl = Url.INFO_COUNTRY_PAGE.replace("{id}", "\\d+");
		
		assertThat(page.getCurrentUrl()).matches(expectedUrl);
		assertThat(page.getHeader()).isEqualTo(TEST_COUNTRY_NAME);
	}
	
	@Test(
		groups = "logic",
		dependsOnMethods = "shouldBeRedirectedToPageWithInfoAboutCountryAfterCreation"
	)
	public void countryShouldBeAvailableForChoosingAtPageWithSeries() {
		page.open(Url.ADD_SERIES_PAGE);
		
		AddSeriesPage seriesPage = new AddSeriesPage(WebDriverFactory.getDriver());
		
		assertThat(seriesPage.getCountryFieldValues()).contains(TEST_COUNTRY_NAME);
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
