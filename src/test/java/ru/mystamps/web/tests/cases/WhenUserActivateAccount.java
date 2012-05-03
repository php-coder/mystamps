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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

package ru.mystamps.web.tests.cases;

import static org.fest.assertions.Assertions.assertThat;

import org.apache.commons.lang.StringUtils;

import org.springframework.beans.factory.annotation.Value;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import ru.mystamps.web.tests.page.ActivateAccountPage;

import static ru.mystamps.web.tests.TranslationUtils.stripHtmlTags;
import static ru.mystamps.web.tests.TranslationUtils.tr;
import static ru.mystamps.web.tests.fest.AbstractPageWithFormAssert.assertThat;
import static ru.mystamps.web.validation.ValidationRules.LOGIN_MIN_LENGTH;
import static ru.mystamps.web.validation.ValidationRules.LOGIN_MAX_LENGTH;
import static ru.mystamps.web.validation.ValidationRules.NAME_MAX_LENGTH;
import static ru.mystamps.web.validation.ValidationRules.PASSWORD_MIN_LENGTH;
import static ru.mystamps.web.validation.ValidationRules.ACT_KEY_LENGTH;

import static ru.mystamps.web.SiteMap.ACTIVATE_ACCOUNT_PAGE_WITH_KEY_URL;
import static ru.mystamps.web.SiteMap.AUTHENTICATION_PAGE_URL;
import static ru.mystamps.web.SiteMap.SUCCESSFUL_ACTIVATION_PAGE_URL;

public class WhenUserActivateAccount extends WhenUserAtAnyPageWithForm<ActivateAccountPage> {
	
	@Value("#{test.valid_user_login}")
	private String validUserLogin;
	
	@Value("#{test.not_activated_user1_act_key}")
	private String firstNotActivatedUserActKey;
	
	@Value("#{test.not_activated_user2_act_key}")
	private String secondNotActivatedUserActKey;
	
	public WhenUserActivateAccount() {
		super(ActivateAccountPage.class);
		hasTitle(tr("t_activation_title"));
		hasHeader(tr("t_activation_on_site"));
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
		final String key = "7777744444";
		final String url = ACTIVATE_ACCOUNT_PAGE_WITH_KEY_URL.replace("{key}", key);
		
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
		page.activateAccount("t3s7-T_E_S_T", null, null, null, null);
		
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
			.hasError(tr("ru.mystamps.web.validation.jsr303.UniqueLogin.message"));
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void nameShouldNotBeTooLong() {
		page.activateAccount(null, StringUtils.repeat("0", NAME_MAX_LENGTH + 1), null, null, null);
		
		assertThat(page)
			.field("name")
			.hasError(tr("value.too-long", NAME_MAX_LENGTH));
	}
	
	@Test(groups = "valid", dependsOnGroups = "std", dataProvider = "validNames")
	public void nameWithAllowedCharactersShouldBeAccepted(
		final String name,
		final Object whatever) {
		
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
			.hasError(tr("name.hyphen"));
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void nameShouldNotEndsWithHyphen() {
		page.activateAccount(null, "test-", null, null, null);
		
		assertThat(page)
			.field("name")
			.hasError(tr("name.hyphen"));
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
	public void passwordWithForbiddenCharactersShouldBeRejected() {
		page.activateAccount(null, null, "'t@$t'", null, null);
		
		assertThat(page)
			.field("password")
			.hasError(tr("password.invalid"));
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
			.hasError(tr("ru.mystamps.web.validation.jsr303.ExistingActivationKey.message"));
	}
	
	@Test(groups = "logic", dependsOnGroups = {
		"std", "invalid", "valid", "misc"
	})
	public void afterActivationShouldExistsMessageWithLinkForAuthentication() {
		page.activateAccount(
			"1st-test-login",
			"Test Suite",
			"test-password",
			"test-password",
			firstNotActivatedUserActKey
		);
		
		assertThat(page.getCurrentUrl()).isEqualTo(SUCCESSFUL_ACTIVATION_PAGE_URL);
		
		assertThat(page.textPresent(stripHtmlTags(tr("t_activation_successful")))).isTrue();
		
		assertThat(page.existsLinkTo(AUTHENTICATION_PAGE_URL))
			.overridingErrorMessage("should exists link to authentication page")
			.isTrue();
	}
	
	@Test(groups = "logic", dependsOnGroups = {
		"std", "invalid", "valid", "misc"
	})
	public void activationShouldPassWhenUserProvidedEmptyName() {
		page.activateAccount(
			"2nd-test-login",
			"",
			"test-password",
			"test-password",
			secondNotActivatedUserActKey
		);
		
		assertThat(page.getCurrentUrl()).isEqualTo(SUCCESSFUL_ACTIVATION_PAGE_URL);
		
		assertThat(page.textPresent(stripHtmlTags(tr("t_activation_successful")))).isTrue();
	}
	
	@DataProvider(name = "validNames")
	public Object[][] getValidNames() {
		return new Object[][] {
			{"x", null},
			{"Slava Se-mushin", null},
			{"Семён Якушев", null}
		};
	}
	
}
