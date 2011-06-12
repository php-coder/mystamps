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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

import static ru.mystamps.web.SiteMap.ACTIVATE_ACCOUNT_PAGE_URL;
import static ru.mystamps.web.SiteMap.AUTHENTICATION_PAGE_URL;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/TestContext.xml")
public class WhenUserActivateAccount extends WhenUserAtAnyPageWithForm<ActivateAccountPage> {
	
	@Value("#{test.valid_user_login}")
	private String VALID_USER_LOGIN;
	
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
		
		page.open(ACTIVATE_ACCOUNT_PAGE_URL + "?key=" + key);
		assertEquals(key, page.getFieldValue("activationKey"));
		
		page.open();
	}
	
	@Test
	public void loginAndPasswordShouldBeDifferent() {
		page.fillField("login", "admin");
		page.fillField("password", "admin");
		page.submit();
		
		assertEquals(tr("password.login.match"), page.getFieldError("password"));
	}
	
	@Test
	public void passwordAndConfirmationShouldMatch() {
		page.fillField("password", "password123");
		page.fillField("passwordConfirm", "password321");
		page.submit();
		
		assertEquals(tr("password.mismatch"), page.getFieldError("passwordConfirm"));
	}
	
	@Test
	public void loginShouldNotBeTooShort() {
		page.fillField("login", "a");
		page.submit();
		
		assertEquals(tr("value.too-short", LOGIN_MIN_LENGTH), page.getFieldError("login"));
	}
	
	@Test
	public void mostShortLoginShouldBeAccepted() {
		page.fillField("login", "ab");
		page.submit();
		
		assertFalse(page.isFieldHasError("login"));
	}
	
	@Test
	public void loginShouldNotBeTooLong() {
		page.fillField("login", "abcde12345fghkl6");
		page.submit();
		
		assertEquals(tr("value.too-long", LOGIN_MAX_LENGTH), page.getFieldError("login"));
	}
	
	@Test
	public void mostLongLoginShouldBeAccepted() {
		page.fillField("login", "abcde1234567890");
		page.submit();
		
		assertFalse(page.isFieldHasError("login"));
	}
	
	@Test
	public void loginWithAllowedCharactersShouldBeAccepted() {
		page.fillField("login", "t3s7-T_E_S_T");
		page.submit();
		
		assertFalse(page.isFieldHasError("login"));
	}
	
	@Test
	public void loginWithForbiddenCharactersShouldBeRejected() {
		page.fillField("login", "'t@$t'");
		page.submit();
		
		assertEquals(tr("login.invalid"), page.getFieldError("login"));
	}
	
	@Test
	public void loginShouldBeUnique() {
		page.fillField("login", VALID_USER_LOGIN);
		page.submit();
		
		assertEquals(tr("login.exists"), page.getFieldError("login"));
	}
	
	@Test
	public void nameShouldNotBeTooLong() {
		page.fillField("name", StringUtils.repeat("0", NAME_MAX_LENGTH + 1));
		page.submit();
		
		assertEquals(tr("value.too-long", NAME_MAX_LENGTH), page.getFieldError("name"));
	}
	
	@Test
	public void nameWithAllowedCharactersShouldBeAccepted() {
		// TODO: test Russian letters (like 'SемЄн як-ушев')
		page.fillField("name", "Slava Se-mushin");
		page.submit();
		
		assertFalse(page.isFieldHasError("name"));
	}
	
	@Test
	public void nameWithForbiddenCharactersShouldBeRejected() {
		page.fillField("name", "M@st3r_");
		page.submit();
		
		assertEquals(tr("name.invalid"), page.getFieldError("name"));
	}
	
	@Test
	public void nameShouldNotStartsFromHyphen() {
		page.fillField("name", "-test");
		page.submit();
		
		assertEquals(tr("name.hyphen"), page.getFieldError("name"));
	}
	
	@Test
	public void nameShouldNotEndsWithHyphen() {
		page.fillField("name", "test-");
		page.submit();
		
		assertEquals(tr("name.hyphen"), page.getFieldError("name"));
	}
	
	@Test
	public void nameShouldBeStripedFromLeadingAndTrailingSpaces() {
		page.fillField("name", " test ");
		page.submit();
		
		assertEquals("test", page.getFieldValue("name"));
	}
	
	@Test
	public void passwordShouldNotBeTooShort() {
		page.fillField("password", "123");
		page.submit();
		
		assertEquals(tr("value.too-short", PASSWORD_MIN_LENGTH), page.getFieldError("password"));
	}
	
	@Test
	public void mostShortPasswordShouldBeAccepted() {
		page.fillField("password", "1234");
		page.submit();
		
		assertFalse(page.isFieldHasError("password"));
	}
	
	@Test
	public void passwordWithAllowedCharactersShouldBeAccepted() {
		page.fillField("password", "t3s7-T_E_S_T");
		page.submit();
		
		assertFalse(page.isFieldHasError("password"));
	}
	
	@Test
	public void passwordWithForbiddenCharactersShouldBeRejected() {
		page.fillField("password", "'t@$t'");
		page.submit();
		
		assertEquals(tr("password.invalid"), page.getFieldError("password"));
	}
	
	@Test
	public void activationKeyShouldNotBeTooShort() {
		page.fillField("activationKey", "12345");
		page.submit();
		
		assertEquals(tr("value.too-short", ACT_KEY_LENGTH), page.getFieldError("activationKey"));
	}
	
	@Test
	public void activationKeyShouldNotBeTooLong() {
		page.fillField("activationKey", "1234567890123");
		page.submit();
		
		assertEquals(tr("value.too-long", ACT_KEY_LENGTH), page.getFieldError("activationKey"));
	}
	
	@Test
	public void activationKeyWithForbiddenCharactersShouldBeRejected() {
		page.fillField("activationKey", "A123=+TEST");
		page.submit();
		
		assertEquals(tr("key.invalid"), page.getFieldError("activationKey"));
	}
	
	@Test
	public void wrongActivationKeyShouldBeRejected() {
		page.fillField("activationKey", StringUtils.repeat("1", ACT_KEY_LENGTH));
		page.submit();
		
		assertEquals(tr("key.not-exists"), page.getFieldError("activationKey"));
	}
	
	@Test
	public void afterActivationShouldExistsMessageWithLinkForAuthentication() {
		// NOTE: this test depends from
		// WhenUserRegisterAccount::successfulMessageShouldBeShownAfterRegistration()
		// (see also #96)
		//
		// TODO: get activation key from database (#98)
		// TODO: delete user after activation
		
		page.fillField("login", "test-login");
		page.fillField("name", "Test Suite");
		page.fillField("password", "test-password");
		page.fillField("passwordConfirm", "test-password");
		page.fillField("activationKey", "7777744444");
		page.submit();
		
		assertTrue(page.textPresent(stripHtmlTags(tr("t_activation_successful"))));
		
		assertTrue(
			"should exists link to authentication page",
			page.linkHasLabelAndPointsTo("authentication", AUTHENTICATION_PAGE_URL)
		);
	}
	
}
