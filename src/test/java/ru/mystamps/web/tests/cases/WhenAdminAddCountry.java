/*
 * Copyright (C) 2009-2017 Slava Semushin <slava.semushin@gmail.com>
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
import static ru.mystamps.web.tests.fest.PageWithFormAssert.assertThat;

public class WhenAdminAddCountry extends WhenAnyUserAtAnyPageWithForm<AddCountryPage> {
	
	private static final String TEST_COUNTRY_NAME_EN = "Russia";
	private static final String TEST_COUNTRY_NAME_RU = "Россия";
	
	@Value("${valid_admin_login}")
	private String validAdminLogin;
	
	@Value("${valid_admin_password}")
	private String validAdminPassword;
	
	@Value("${valid_country_name_en}")
	private String existingCountryNameEn;
	
	@Value("${valid_country_name_ru}")
	private String existingCountryNameRu;
	
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
	public void countryNameEnShouldBeUnique() {
		page.addCountry(existingCountryNameEn, TEST_COUNTRY_NAME_RU);
		
		assertThat(page)
			.field("name")
			.hasError(tr("ru.mystamps.web.validation.jsr303.UniqueCountryName.message"));
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void countryNameRuShouldBeUnique() {
		page.addCountry(TEST_COUNTRY_NAME_EN, existingCountryNameRu);
		
		assertThat(page)
			.field("nameRu")
			.hasError(tr("ru.mystamps.web.validation.jsr303.UniqueCountryName.message"));
	}
	
	@Test(groups = "valid", dependsOnGroups = "std")
	public void countryNameEnWithAllowedCharactersShouldBeAccepted() {
		page.addCountry("Valid-Name Country", "НазваниеСтраны");
		
		assertThat(page).field("name").hasNoError();
	}
	
	@Test(groups = "valid", dependsOnGroups = "std")
	public void countryNameRuWithAllowedCharactersShouldBeAccepted() {
		page.addCountry("ValidName", "Ёё Нормальное-название страны");
		
		assertThat(page).field("nameRu").hasNoError();
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void countryNameEnWithForbiddenCharactersShouldBeRejected() {
		page.addCountry("S0m3+CountryN_ame", TEST_COUNTRY_NAME_RU);
		
		assertThat(page)
			.field("name")
			.hasError(tr("country-name-en.invalid"));
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void countryNameRuWithForbiddenCharactersShouldBeRejected() {
		page.addCountry(TEST_COUNTRY_NAME_EN, "Нек0торо3+наз_вание");
		
		assertThat(page)
			.field("nameRu")
			.hasError(tr("country-name-ru.invalid"));
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void countryNameEnShouldNotStartsFromHyphen() {
		page.addCountry("-test", TEST_COUNTRY_NAME_RU);
		
		assertThat(page)
			.field("name")
			.hasError(tr("value.hyphen"));
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void countryNameRuShouldNotStartsFromHyphen() {
		page.addCountry(TEST_COUNTRY_NAME_EN, "-тест");
		
		assertThat(page)
			.field("nameRu")
			.hasError(tr("value.hyphen"));
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void countryNameEnShouldNotEndsWithHyphen() {
		page.addCountry("test-", TEST_COUNTRY_NAME_RU);
		
		assertThat(page)
			.field("name")
			.hasError(tr("value.hyphen"));
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void countryNameRuShouldNotEndsWithHyphen() {
		page.addCountry(TEST_COUNTRY_NAME_EN, "тест-");
		
		assertThat(page)
			.field("nameRu")
			.hasError(tr("value.hyphen"));
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
	public void countryNameEnShouldBeStripedFromLeadingAndTrailingSpaces() {
		page.addCountry(" t3st ", TEST_COUNTRY_NAME_RU);
		
		assertThat(page).field("name").hasValue("t3st");
	}
	
	@Test(groups = "misc", dependsOnGroups = "std")
	public void countryNameRuShouldBeStripedFromLeadingAndTrailingSpaces() {
		page.addCountry(TEST_COUNTRY_NAME_EN, " т3ст ");
		
		assertThat(page).field("nameRu").hasValue("т3ст");
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
	
	@Test(groups = "logic", dependsOnGroups = { "std", "invalid", "valid", "misc" })
	public void shouldCreateCountryWithNameInEnglishOnly() {
		page.addCountry("Germany", null);
		
		String expectedUrl = Url.INFO_COUNTRY_PAGE.replace("{slug}", "germany");
		
		assertThat(page.getCurrentUrl()).matches(expectedUrl);
		assertThat(page.getHeader()).isEqualTo("Stamps of Germany");
	}
	
	@Test(groups = "logic", dependsOnGroups = { "std", "invalid", "valid", "misc" })
	public void shouldCreateCountryWithNameInTwoLanguages() {
		page.addCountry(TEST_COUNTRY_NAME_EN, TEST_COUNTRY_NAME_RU);
		
		String expectedUrl = Url.INFO_COUNTRY_PAGE
			.replace("{slug}", TEST_COUNTRY_NAME_EN.toLowerCase());
		
		assertThat(page.getCurrentUrl()).matches(expectedUrl);
		assertThat(page.getHeader()).isEqualTo("Stamps of " + TEST_COUNTRY_NAME_EN);
	}
	
	@Test(groups = "logic", dependsOnMethods = "shouldCreateCountryWithNameInTwoLanguages")
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
		// Ignore this check because server always returns 403 for anonymous user and our test suite
		// lack ability to check response code after authentication.
	}
	
	@Override
	protected void shouldHaveUserBar() {
		// Ignore this check because when user authenticated there is no links for login/register.
	}
	
}
