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

import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Value;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import ru.mystamps.web.Url;
import ru.mystamps.web.tests.page.ActivateAccountPage;

import static ru.mystamps.web.tests.TranslationUtils.tr;
import static ru.mystamps.web.tests.fest.PageWithFormAssert.assertThat;
import static ru.mystamps.web.validation.ValidationRules.LOGIN_MIN_LENGTH;
import static ru.mystamps.web.validation.ValidationRules.LOGIN_MAX_LENGTH;
import static ru.mystamps.web.validation.ValidationRules.NAME_MAX_LENGTH;
import static ru.mystamps.web.validation.ValidationRules.PASSWORD_MIN_LENGTH;
import static ru.mystamps.web.validation.ValidationRules.PASSWORD_MAX_LENGTH;
import static ru.mystamps.web.validation.ValidationRules.ACT_KEY_LENGTH;

public class WhenAnonymousUserActivateAccount
	extends WhenAnyUserAtAnyPageWithForm<ActivateAccountPage> {
	
	@Value("${valid_user_login}")
	private String validUserLogin;
	
	public WhenAnonymousUserActivateAccount() {
		super(ActivateAccountPage.class);
	}
	
	@BeforeMethod
	public void openPage() {
		page.open();
	}
	
	@Test(groups = "std")
	public void shouldHaveStandardStructure() {
		checkStandardStructure();
	}
	
	@Test(groups = "misc", dependsOnGroups = "std")
	public void activationKeyShouldBeAutoFilledFromURL() {
		String key = "7777744444";
		String url = Url.ACTIVATE_ACCOUNT_PAGE + "?key=" + key;
		
		page.open(url);
		assertThat(page).field("activationKey").hasValue(key);
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void loginAndPasswordShouldBeDifferent() {
		page.activateAccount("admin", null, "admin", null, null);
		
		assertThat(page)
			.field("password")
			.hasError(tr("password.login.match"));
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void passwordAndConfirmationShouldMatch() {
		page.activateAccount(null, null, "password123", "password321", null);
		
		assertThat(page)
			.field("passwordConfirmation")
			.hasError(tr("password.mismatch"));
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void loginShouldNotBeTooShort() {
		page.activateAccount("a", null, null, null, null);
		
		assertThat(page)
			.field("login")
			.hasError(tr("value.too-short", LOGIN_MIN_LENGTH));
	}
	
	@Test(groups = "valid", dependsOnGroups = "std")
	public void mostShortLoginShouldBeAccepted() {
		page.activateAccount("ab", null, null, null, null);
		
		assertThat(page).field("login").hasNoError();
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void loginShouldNotBeTooLong() {
		page.activateAccount("abcde12345fghkl6", null, null, null, null);
		
		assertThat(page)
			.field("login")
			.hasError(tr("value.too-long", LOGIN_MAX_LENGTH));
	}
	
	@Test(groups = "valid", dependsOnGroups = "std")
	public void mostLongLoginShouldBeAccepted() {
		page.activateAccount("abcde1234567890", null, null, null, null);
		
		assertThat(page).field("login").hasNoError();
	}
	
	@Test(groups = "valid", dependsOnGroups = "std")
	public void loginWithAllowedCharactersShouldBeAccepted() {
		page.activateAccount("t.3.s.7-T_E_S_T", null, null, null, null);
		
		assertThat(page).field("login").hasNoError();
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void loginWithForbiddenCharactersShouldBeRejected() {
		page.activateAccount("'t@$t'", null, null, null, null);
		
		assertThat(page)
			.field("login")
			.hasError(tr("login.invalid"));
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void loginShouldBeUnique() {
		page.activateAccount(validUserLogin, null, null, null, null);
		
		assertThat(page)
			.field("login")
			.hasError(tr("ru.mystamps.web.support.beanvalidation.UniqueLogin.message"));
	}

	@Test(groups = "invalid", dependsOnGroups = "std", dataProvider = "invalidLogins")
	public void loginShouldNotContainRepetitionSpecialCharacters(String login, Object whatever) {
		page.activateAccount(login, null, null, null, null);

		assertThat(page).field("login").hasError(tr("login.repetition_chars"));
	}
	
	@Test(groups = "misc", dependsOnGroups = "std")
	public void loginShouldBeStripedFromLeadingAndTrailingSpaces() {
		page.activateAccount(" testLogin ", null, null, null, null);

		assertThat(page).field("login").hasValue("testLogin");
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void nameShouldNotBeTooLong() {
		page.activateAccount(null, StringUtils.repeat("0", NAME_MAX_LENGTH + 1), null, null, null);
		
		assertThat(page)
			.field("name")
			.hasError(tr("value.too-long", NAME_MAX_LENGTH));
	}
	
	@Test(groups = "valid", dependsOnGroups = "std", dataProvider = "validNames")
	public void nameWithAllowedCharactersShouldBeAccepted(String name, Object whatever) {
		page.activateAccount(null, name, null, null, null);
		
		assertThat(page).field("name").hasNoError();
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void nameWithForbiddenCharactersShouldBeRejected() {
		page.activateAccount(null, "M@st3r_", null, null, null);
		
		assertThat(page)
			.field("name")
			.hasError(tr("name.invalid"));
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void nameShouldNotStartsFromHyphen() {
		page.activateAccount(null, "-test", null, null, null);
		
		assertThat(page)
			.field("name")
			.hasError(tr("value.hyphen"));
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void nameShouldNotEndsWithHyphen() {
		page.activateAccount(null, "test-", null, null, null);
		
		assertThat(page)
			.field("name")
			.hasError(tr("value.hyphen"));
	}
	
	@Test(groups = "misc", dependsOnGroups = "std")
	public void nameShouldBeStripedFromLeadingAndTrailingSpaces() {
		page.activateAccount(null, " test ", null, null, null);
		
		assertThat(page).field("name").hasValue("test");
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void passwordShouldNotBeTooShort() {
		page.activateAccount(null, null, "123", null, null);
		
		assertThat(page)
			.field("password")
			.hasError(tr("value.too-short", PASSWORD_MIN_LENGTH));
	}

	@Test(groups = "invalid", dependsOnGroups = "std")
	public void passwordShouldNotBeTooLong() {
		page.activateAccount(null, null, StringUtils.repeat("0", PASSWORD_MAX_LENGTH + 1),
			null, null);

		assertThat(page)
			.field("password")
			.hasError(tr("value.too-long", PASSWORD_MAX_LENGTH));
	}
	
	@Test(groups = "valid", dependsOnGroups = "std")
	public void mostShortPasswordShouldBeAccepted() {
		page.activateAccount(null, null, "1234", null, null);
		
		assertThat(page).field("password").hasNoError();
	}
	
	@Test(groups = "valid", dependsOnGroups = "std")
	public void passwordWithAllowedCharactersShouldBeAccepted() {
		page.activateAccount(null, null, "t3s7-T_E_S_T", null, null);
		
		assertThat(page).field("password").hasNoError();
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void activationKeyShouldNotBeTooShort() {
		page.activateAccount(null, null, null, null, "12345");
		
		assertThat(page)
			.field("activationKey")
			.hasError(tr("value.invalid-length", ACT_KEY_LENGTH));
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void activationKeyShouldNotBeTooLong() {
		page.activateAccount(null, null, null, null, "1234567890123");
		
		assertThat(page)
			.field("activationKey")
			.hasError(tr("value.invalid-length", ACT_KEY_LENGTH));
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void activationKeyWithForbiddenCharactersShouldBeRejected() {
		page.activateAccount(null, null, null, null, "A123=+TEST");
		
		assertThat(page)
			.field("activationKey")
			.hasError(tr("key.invalid"));
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void wrongActivationKeyShouldBeRejected() {
		page.activateAccount(null, null, null, null, StringUtils.repeat("1", ACT_KEY_LENGTH));
		
		assertThat(page)
			.field("activationKey")
			.hasError(tr("ru.mystamps.web.support.beanvalidation.ExistingActivationKey.message"));
	}
	
	@DataProvider(name = "validNames")
	public Object[][] getValidNames() {
		return new Object[][] {
			{"x", null},
			{"Slava Se-mushin", null},
			{"Семён Якушев", null}
		};
	}

	@DataProvider(name = "invalidLogins")
	public Object[][] getInvalidLogins() {
		return new Object[][] {
			{"te__st", null},
			{"te--st", null},
			{"te..st", null},
			{"te_-st", null},
			{"te-._st", null}
		};
	}
	
}
