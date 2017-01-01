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
import static ru.mystamps.web.tests.fest.PageWithFormAssert.assertThat;
import static ru.mystamps.web.validation.ValidationRules.CATEGORY_NAME_MAX_LENGTH;
import static ru.mystamps.web.validation.ValidationRules.CATEGORY_NAME_MIN_LENGTH;

public class WhenAdminAddCategory extends WhenAnyUserAtAnyPageWithForm<AddCategoryPage> {
	
	private static final String TEST_CATEGORY_NAME_EN = "Space";
	private static final String TEST_CATEGORY_NAME_RU = "Космос";
	
	@Value("${valid_admin_login}")
	private String validAdminLogin;
	
	@Value("${valid_admin_password}")
	private String validAdminPassword;
	
	@Value("${valid_category_name_en}")
	private String existingCategoryNameEn;
	
	@Value("${valid_category_name_ru}")
	private String existingCategoryNameRu;
	
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
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void categoryNameEnShouldNotBeTooShort() {
		page.addCategory("ee", TEST_CATEGORY_NAME_RU);
		
		assertThat(page).field("name").hasError(tr("value.too-short", CATEGORY_NAME_MIN_LENGTH));
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void categoryNameRuShouldNotBeTooShort() {
		page.addCategory(TEST_CATEGORY_NAME_EN, "яя");
		
		assertThat(page).field("nameRu").hasError(tr("value.too-short", CATEGORY_NAME_MIN_LENGTH));
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void categoryNameEnShouldNotBeTooLong() {
		String longCategoryName = StringUtils.repeat("e", CATEGORY_NAME_MAX_LENGTH + 1);
		page.addCategory(longCategoryName, TEST_CATEGORY_NAME_RU);
		
		assertThat(page).field("name").hasError(tr("value.too-long", CATEGORY_NAME_MAX_LENGTH));
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void categoryNameRuShouldNotBeTooLong() {
		String longCategoryName = StringUtils.repeat("я", CATEGORY_NAME_MAX_LENGTH + 1);
		page.addCategory(TEST_CATEGORY_NAME_EN, longCategoryName);
		
		assertThat(page).field("nameRu").hasError(tr("value.too-long", CATEGORY_NAME_MAX_LENGTH));
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void categoryNameEnShouldBeUnique() {
		page.addCategory(existingCategoryNameEn, TEST_CATEGORY_NAME_RU);
		
		assertThat(page)
			.field("name")
			.hasError(tr("ru.mystamps.web.validation.jsr303.UniqueCategoryName.message"));
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void categoryNameRuShouldBeUnique() {
		page.addCategory(TEST_CATEGORY_NAME_EN, existingCategoryNameRu);
		
		assertThat(page)
			.field("nameRu")
			.hasError(tr("ru.mystamps.web.validation.jsr303.UniqueCategoryName.message"));
	}
	
	@Test(groups = "valid", dependsOnGroups = "std")
	public void categoryNameEnWithAllowedCharactersShouldBeAccepted() {
		page.addCategory("Valid-Name Category", "InvalidRussianCategory");
		
		assertThat(page).field("name").hasNoError();
	}
	
	@Test(groups = "valid", dependsOnGroups = "std")
	public void categoryNameRuWithAllowedCharactersShouldBeAccepted() {
		page.addCategory("НевернаяКатегорияНаАнглийском", "Категория Ёё");
		
		assertThat(page).field("nameRu").hasNoError();
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void categoryNameEnWithForbiddenCharactersShouldBeRejected() {
		page.addCategory("S0m3+CategoryN_ame", TEST_CATEGORY_NAME_RU);
		
		assertThat(page).field("name").hasError(tr("category-name-en.invalid"));
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void categoryNameRuWithForbiddenCharactersShouldBeRejected() {
		page.addCategory(TEST_CATEGORY_NAME_EN, "Категория123");
		
		assertThat(page).field("nameRu").hasError(tr("category-name-ru.invalid"));
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void categoryNameEnShouldNotStartsFromHyphen() {
		page.addCategory("-test", TEST_CATEGORY_NAME_RU);
		
		assertThat(page).field("name").hasError(tr("value.hyphen"));
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void categoryNameRuShouldNotStartsFromHyphen() {
		page.addCategory(TEST_CATEGORY_NAME_EN, "-тест");
		
		assertThat(page).field("nameRu").hasError(tr("value.hyphen"));
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void categoryNameEnShouldNotEndsWithHyphen() {
		page.addCategory("test-", TEST_CATEGORY_NAME_RU);
		
		assertThat(page).field("name").hasError(tr("value.hyphen"));
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void categoryNameRuShouldNotEndsWithHyphen() {
		page.addCategory(TEST_CATEGORY_NAME_EN, "тест-");
		
		assertThat(page).field("nameRu").hasError(tr("value.hyphen"));
	}

	@Test(groups = "invalid", dependsOnGroups = "std")
	public void categoryNameEnShouldNotContainRepeatedHyphens() {
		page.addCategory("te--st", TEST_CATEGORY_NAME_RU);

		assertThat(page).field("name").hasError(tr("value.repeating_hyphen"));
	}

	@Test(groups = "invalid", dependsOnGroups = "std")
	public void categoryNameRuShouldNotContainRepeatedHyphens() {
		page.addCategory(TEST_CATEGORY_NAME_EN, "те--ст");

		assertThat(page).field("nameRu").hasError(tr("value.repeating_hyphen"));
	}
	
	@Test(groups = "misc", dependsOnGroups = "std")
	public void categoryNameEnShouldBeStripedFromLeadingAndTrailingSpaces() {
		page.addCategory(" t3st ", TEST_CATEGORY_NAME_RU);
		
		assertThat(page).field("name").hasValue("t3st");
	}
	
	@Test(groups = "misc", dependsOnGroups = "std")
	public void categoryNameRuShouldBeStripedFromLeadingAndTrailingSpaces() {
		page.addCategory(TEST_CATEGORY_NAME_EN, " т3ст ");
		
		assertThat(page).field("nameRu").hasValue("т3ст");
	}

	@Test(groups = "misc", dependsOnGroups = "std")
	public void categoryNameEnShouldReplaceRepeatedSpacesByOne() {
		page.addCategory("t3  st", TEST_CATEGORY_NAME_RU);

		assertThat(page).field("name").hasValue("t3 st");
	}

	@Test(groups = "misc", dependsOnGroups = "std")
	public void categoryNameRuShouldReplaceRepeatedSpacesByOne() {
		page.addCategory(TEST_CATEGORY_NAME_EN, "т3  ст");

		assertThat(page).field("nameRu").hasValue("т3 ст");
	}
	
	@Test(groups = "logic", dependsOnGroups = { "std", "invalid", "valid", "misc" })
	public void shouldCreateCategoryWithNameInEnglishOnly() {
		page.addCategory("Cars", null);
		
		String expectedUrl = Url.INFO_CATEGORY_PAGE.replace("{slug}", "cars");
		
		assertThat(page.getCurrentUrl()).isEqualTo(expectedUrl);
		assertThat(page.getHeader()).isEqualTo("Cars");
	}
	
	@Test(groups = "logic", dependsOnGroups = { "std", "invalid", "valid", "misc" })
	public void shouldCreateCategoryWithNameInTwoLanguages() {
		page.addCategory(TEST_CATEGORY_NAME_EN, TEST_CATEGORY_NAME_RU);
		
		String expectedUrl = Url.INFO_CATEGORY_PAGE
			.replace("{slug}", TEST_CATEGORY_NAME_EN.toLowerCase());
		
		assertThat(page.getCurrentUrl()).isEqualTo(expectedUrl);
		assertThat(page.getHeader()).isEqualTo(TEST_CATEGORY_NAME_EN);
	}
	
	@Test(groups = "logic", dependsOnMethods = "shouldCreateCategoryWithNameInTwoLanguages")
	public void categoryShouldBeAvailableForChoosingAtPageWithSeries() {
		page.open(Url.ADD_SERIES_PAGE);
		
		AddSeriesPage seriesPage = new AddSeriesPage(WebDriverFactory.getDriver());
		
		assertThat(seriesPage.getCategoryFieldValues()).contains(TEST_CATEGORY_NAME_EN);
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
