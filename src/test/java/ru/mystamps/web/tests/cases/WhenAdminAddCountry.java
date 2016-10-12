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

import ru.mystamps.web.tests.page.AddCountryPage;

import static ru.mystamps.web.tests.TranslationUtils.tr;
import static ru.mystamps.web.tests.fest.PageWithFormAssert.assertThat;

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
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void countryNameEnShouldNotContainRepeatedHyphens() {
		page.addCountry("te--st", TEST_COUNTRY_NAME_RU);

		assertThat(page).field("name").hasError(tr("value.repeating_hyphen"));
	}

	@Test(groups = "invalid", dependsOnGroups = "std")
	public void countryNameRuShouldNotContainRepeatedHyphens() {
		page.addCountry(TEST_COUNTRY_NAME_EN, "те--ст");

		assertThat(page).field("nameRu").hasError(tr("value.repeating_hyphen"));
	}

	@Test(groups = "misc", dependsOnGroups = "std")
	public void countryNameEnShouldReplaceRepeatedSpacesByOne() {
		page.addCountry("t3  st", TEST_COUNTRY_NAME_RU);

		assertThat(page).field("name").hasValue("t3 st");
	}

	@Test(groups = "misc", dependsOnGroups = "std")
	public void countryNameRuShouldReplaceRepeatedSpacesByOne() {
		page.addCountry(TEST_COUNTRY_NAME_EN, "т3  ст");

		assertThat(page).field("nameRu").hasValue("т3 ст");
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
