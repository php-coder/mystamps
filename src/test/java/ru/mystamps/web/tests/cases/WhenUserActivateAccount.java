/*
 * Copyright (C) 2009-2011 Slava Semushin <slava.semushin@gmail.com>
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
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.beans.factory.annotation.Value;

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
import static ru.mystamps.web.SiteMap.SUCCESSFUL_ACTIVATION_PAGE_URL;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/TestContext.xml")
public class WhenUserActivateAccount extends WhenUserAtAnyPageWithForm<ActivateAccountPage> {
	
	@Value("#{test.valid_user_login}")
	private String validUserLogin;
	
	@Value("#{test.not_activated_user_act_key}")
	private String notActivatedUserActKey;
	
	public WhenUserActivateAccount() {
		super(ActivateAccountPage.class);
		hasTitle(tr("t_activation_title"));
		hasHeader(tr("t_activation_on_site"));
		
		page.open();
	}
	
	@Test
	public void shouldHaveStandardStructure() {
		checkStandardStructure();
	}
	
	@Test
	public void activationKeyShouldBeAutoFilledFromURL() {
		final String key = "7777744444";
		final String url = ACTIVATE_ACCOUNT_PAGE_WITH_KEY_URL.replace("{key}", key);
		
		page.open(url);
		assertThat(page).field("activationKey").hasValue(key);
		
		page.open();
	}
	
	@Test
	public void loginAndPasswordShouldBeDifferent() {
		page.activateAccount("admin", null, "admin", null, null);
		
		assertThat(page)
			.field("password")
			.hasError(tr("password.login.match"));
	}
	
	@Test
	public void passwordAndConfirmationShouldMatch() {
		page.activateAccount(null, null, "password123", "password321", null);
		
		assertThat(page)
			.field("passwordConfirm")
			.hasError(tr("password.mismatch"));
	}
	
	@Test
	public void loginShouldNotBeTooShort() {
		page.activateAccount("a", null, null, null, null);
		
		assertThat(page)
			.field("login")
			.hasError(tr("value.too-short", LOGIN_MIN_LENGTH));
	}
	
	@Test
	public void mostShortLoginShouldBeAccepted() {
		page.activateAccount("ab", null, null, null, null);
		
		assertThat(page).field("login").hasNoError();
	}
	
	@Test
	public void loginShouldNotBeTooLong() {
		page.activateAccount("abcde12345fghkl6", null, null, null, null);
		
		assertThat(page)
			.field("login")
			.hasError(tr("value.too-long", LOGIN_MAX_LENGTH));
	}
	
	@Test
	public void mostLongLoginShouldBeAccepted() {
		page.activateAccount("abcde1234567890", null, null, null, null);
		
		assertThat(page).field("login").hasNoError();
	}
	
	@Test
	public void loginWithAllowedCharactersShouldBeAccepted() {
		page.activateAccount("t3s7-T_E_S_T", null, null, null, null);
		
		assertThat(page).field("login").hasNoError();
	}
	
	@Test
	public void loginWithForbiddenCharactersShouldBeRejected() {
		page.activateAccount("'t@$t'", null, null, null, null);
		
		assertThat(page)
			.field("login")
			.hasError(tr("login.invalid"));
	}
	
	@Test
	public void loginShouldBeUnique() {
		page.activateAccount(validUserLogin, null, null, null, null);
		
		assertThat(page)
			.field("login")
			.hasError(tr("login.exists"));
	}
	
	@Test
	public void nameShouldNotBeTooLong() {
		page.activateAccount(null, StringUtils.repeat("0", NAME_MAX_LENGTH + 1), null, null, null);
		
		assertThat(page)
			.field("name")
			.hasError(tr("value.too-long", NAME_MAX_LENGTH));
	}
	
	@Test
	public void nameWithAllowedCharactersShouldBeAccepted() {
		final String[] names = new String[] {
			"x",
			"Slava Se-mushin",
			"Семён Якушев"
		};
		
		for (final String name : names) {
			page.activateAccount(null, name, null, null, null);
			
			assertThat(page).field("name").hasNoError();
		}
	}
	
	@Test
	public void nameWithForbiddenCharactersShouldBeRejected() {
		page.activateAccount(null, "M@st3r_", null, null, null);
		
		assertThat(page)
			.field("name")
			.hasError(tr("name.invalid"));
	}
	
	@Test
	public void nameShouldNotStartsFromHyphen() {
		page.activateAccount(null, "-test", null, null, null);
		
		assertThat(page)
			.field("name")
			.hasError(tr("name.hyphen"));
	}
	
	@Test
	public void nameShouldNotEndsWithHyphen() {
		page.activateAccount(null, "test-", null, null, null);
		
		assertThat(page)
			.field("name")
			.hasError(tr("name.hyphen"));
	}
	
	@Test
	public void nameShouldBeStripedFromLeadingAndTrailingSpaces() {
		page.activateAccount(null, " test ", null, null, null);
		
		assertThat(page).field("name").hasValue("test");
	}
	
	@Test
	public void passwordShouldNotBeTooShort() {
		page.activateAccount(null, null, "123", null, null);
		
		assertThat(page)
			.field("password")
			.hasError(tr("value.too-short", PASSWORD_MIN_LENGTH));
	}
	
	@Test
	public void mostShortPasswordShouldBeAccepted() {
		page.activateAccount(null, null, "1234", null, null);
		
		assertThat(page).field("password").hasNoError();
	}
	
	@Test
	public void passwordWithAllowedCharactersShouldBeAccepted() {
		page.activateAccount(null, null, "t3s7-T_E_S_T", null, null);
		
		assertThat(page).field("password").hasNoError();
	}
	
	@Test
	public void passwordWithForbiddenCharactersShouldBeRejected() {
		page.activateAccount(null, null, "'t@$t'", null, null);
		
		assertThat(page)
			.field("password")
			.hasError(tr("password.invalid"));
	}
	
	@Test
	public void activationKeyShouldNotBeTooShort() {
		page.activateAccount(null, null, null, null, "12345");
		
		assertThat(page)
			.field("activationKey")
			.hasError(tr("value.invalid-length", ACT_KEY_LENGTH));
	}
	
	@Test
	public void activationKeyShouldNotBeTooLong() {
		page.activateAccount(null, null, null, null, "1234567890123");
		
		assertThat(page)
			.field("activationKey")
			.hasError(tr("value.invalid-length", ACT_KEY_LENGTH));
	}
	
	@Test
	public void activationKeyWithForbiddenCharactersShouldBeRejected() {
		page.activateAccount(null, null, null, null, "A123=+TEST");
		
		assertThat(page)
			.field("activationKey")
			.hasError(tr("key.invalid"));
	}
	
	@Test
	public void wrongActivationKeyShouldBeRejected() {
		page.activateAccount(null, null, null, null, StringUtils.repeat("1", ACT_KEY_LENGTH));
		
		assertThat(page)
			.field("activationKey")
			.hasError(tr("key.not-exists"));
	}
	
	@Test
	public void afterActivationShouldExistsMessageWithLinkForAuthentication() {
		page.activateAccount(
			"test-login",
			"Test Suite",
			"test-password",
			"test-password",
			notActivatedUserActKey
		);
		
		assertThat(page.getCurrentUrl()).isEqualTo(SUCCESSFUL_ACTIVATION_PAGE_URL);
		
		assertThat(page.textPresent(stripHtmlTags(tr("t_activation_successful")))).isTrue();
		
		assertThat(page.linkWithLabelExists("authentication"))
			.overridingErrorMessage("should exists link to authentication page")
			.isTrue();
	}
	
}
