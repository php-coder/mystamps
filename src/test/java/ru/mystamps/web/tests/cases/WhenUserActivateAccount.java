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
import static ru.mystamps.web.validation.ValidationRules.LOGIN_MIN_LENGTH;
import static ru.mystamps.web.validation.ValidationRules.LOGIN_MAX_LENGTH;
import static ru.mystamps.web.validation.ValidationRules.NAME_MAX_LENGTH;
import static ru.mystamps.web.validation.ValidationRules.PASSWORD_MIN_LENGTH;
import static ru.mystamps.web.validation.ValidationRules.ACT_KEY_LENGTH;

import static ru.mystamps.web.SiteMap.ACTIVATE_ACCOUNT_PAGE_WITH_KEY_URL;
import static ru.mystamps.web.SiteMap.AUTHENTICATION_PAGE_URL;
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
		assertThat(page.getFieldValue("activationKey")).isEqualTo(key);
		
		page.open();
	}
	
	@Test
	public void loginAndPasswordShouldBeDifferent() {
		page.fillLogin("admin");
		page.fillPassword("admin");
		page.submit();
		
		assertThat(page.getFieldError("password")).isEqualTo(tr("password.login.match"));
	}
	
	@Test
	public void passwordAndConfirmationShouldMatch() {
		page.fillPassword("password123");
		page.fillField("passwordConfirm", "password321");
		page.submit();
		
		assertThat(page.getFieldError("passwordConfirm")).isEqualTo(tr("password.mismatch"));
	}
	
	@Test
	public void loginShouldNotBeTooShort() {
		page.fillLogin("a");
		page.submit();
		
		assertThat(page.getFieldError("login")).isEqualTo(tr("value.too-short", LOGIN_MIN_LENGTH));
	}
	
	@Test
	public void mostShortLoginShouldBeAccepted() {
		page.fillLogin("ab");
		page.submit();
		
		assertThat(page.isFieldHasError("login")).isFalse();
	}
	
	@Test
	public void loginShouldNotBeTooLong() {
		page.fillLogin("abcde12345fghkl6");
		page.submit();
		
		assertThat(page.getFieldError("login")).isEqualTo(tr("value.too-long", LOGIN_MAX_LENGTH));
	}
	
	@Test
	public void mostLongLoginShouldBeAccepted() {
		page.fillLogin("abcde1234567890");
		page.submit();
		
		assertThat(page.isFieldHasError("login")).isFalse();
	}
	
	@Test
	public void loginWithAllowedCharactersShouldBeAccepted() {
		page.fillLogin("t3s7-T_E_S_T");
		page.submit();
		
		assertThat(page.isFieldHasError("login")).isFalse();
	}
	
	@Test
	public void loginWithForbiddenCharactersShouldBeRejected() {
		page.fillLogin("'t@$t'");
		page.submit();
		
		assertThat(page.getFieldError("login")).isEqualTo(tr("login.invalid"));
	}
	
	@Test
	public void loginShouldBeUnique() {
		page.fillLogin(validUserLogin);
		page.submit();
		
		assertThat(page.getFieldError("login")).isEqualTo(tr("login.exists"));
	}
	
	@Test
	public void nameShouldNotBeTooLong() {
		page.fillName(StringUtils.repeat("0", NAME_MAX_LENGTH + 1));
		page.submit();
		
		assertThat(page.getFieldError("name")).isEqualTo(tr("value.too-long", NAME_MAX_LENGTH));
	}
	
	@Test
	public void nameWithAllowedCharactersShouldBeAccepted() {
		final String[] names = new String[] {
			"x",
			"Slava Se-mushin",
			"Семён Якушев"
		};
		
		for (final String name : names) {
			page.fillName(name);
			page.submit();
			
			assertThat(page.isFieldHasError("name")).isFalse();
		}
	}
	
	@Test
	public void nameWithForbiddenCharactersShouldBeRejected() {
		page.fillName("M@st3r_");
		page.submit();
		
		assertThat(page.getFieldError("name")).isEqualTo(tr("name.invalid"));
	}
	
	@Test
	public void nameShouldNotStartsFromHyphen() {
		page.fillName("-test");
		page.submit();
		
		assertThat(page.getFieldError("name")).isEqualTo(tr("name.hyphen"));
	}
	
	@Test
	public void nameShouldNotEndsWithHyphen() {
		page.fillName("test-");
		page.submit();
		
		assertThat(page.getFieldError("name")).isEqualTo(tr("name.hyphen"));
	}
	
	@Test
	public void nameShouldBeStripedFromLeadingAndTrailingSpaces() {
		page.fillName(" test ");
		page.submit();
		
		assertThat(page.getFieldValue("name")).isEqualTo("test");
	}
	
	@Test
	public void passwordShouldNotBeTooShort() {
		page.fillPassword("123");
		page.submit();
		
		assertThat(page.getFieldError("password"))
			.isEqualTo(tr("value.too-short", PASSWORD_MIN_LENGTH));
	}
	
	@Test
	public void mostShortPasswordShouldBeAccepted() {
		page.fillPassword("1234");
		page.submit();
		
		assertThat(page.isFieldHasError("password")).isFalse();
	}
	
	@Test
	public void passwordWithAllowedCharactersShouldBeAccepted() {
		page.fillPassword("t3s7-T_E_S_T");
		page.submit();
		
		assertThat(page.isFieldHasError("password")).isFalse();
	}
	
	@Test
	public void passwordWithForbiddenCharactersShouldBeRejected() {
		page.fillPassword("'t@$t'");
		page.submit();
		
		assertThat(page.getFieldError("password")).isEqualTo(tr("password.invalid"));
	}
	
	@Test
	public void activationKeyShouldNotBeTooShort() {
		page.fillActivationKey("12345");
		page.submit();
		
		assertThat(page.getFieldError("activationKey"))
			.isEqualTo(tr("value.invalid-length", ACT_KEY_LENGTH));
	}
	
	@Test
	public void activationKeyShouldNotBeTooLong() {
		page.fillActivationKey("1234567890123");
		page.submit();
		
		assertThat(page.getFieldError("activationKey"))
			.isEqualTo(tr("value.invalid-length", ACT_KEY_LENGTH));
	}
	
	@Test
	public void activationKeyWithForbiddenCharactersShouldBeRejected() {
		page.fillActivationKey("A123=+TEST");
		page.submit();
		
		assertThat(page.getFieldError("activationKey")).isEqualTo(tr("key.invalid"));
	}
	
	@Test
	public void wrongActivationKeyShouldBeRejected() {
		page.fillActivationKey(StringUtils.repeat("1", ACT_KEY_LENGTH));
		page.submit();
		
		assertThat(page.getFieldError("activationKey")).isEqualTo(tr("key.not-exists"));
	}
	
	@Test
	public void afterActivationShouldExistsMessageWithLinkForAuthentication() {
		page.fillLogin("test-login");
		page.fillName("Test Suite");
		page.fillPassword("test-password");
		page.fillField("passwordConfirm", "test-password");
		page.fillActivationKey(notActivatedUserActKey);
		page.submit();
		
		assertThat(page.getCurrentUrl()).isEqualTo(SUCCESSFUL_ACTIVATION_PAGE_URL);
		
		assertThat(page.textPresent(stripHtmlTags(tr("t_activation_successful")))).isTrue();
		
		assertThat(page.linkHasLabelAndPointsTo("authentication", AUTHENTICATION_PAGE_URL))
			.overridingErrorMessage("should exists link to authentication page")
			.isTrue();
	}
	
}
