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

import ru.mystamps.web.tests.page.AddCategoryPage;

import static ru.mystamps.web.tests.TranslationUtils.tr;
import static ru.mystamps.web.tests.fest.PageWithFormAssert.assertThat;

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
