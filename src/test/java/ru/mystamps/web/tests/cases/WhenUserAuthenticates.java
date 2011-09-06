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

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.beans.factory.annotation.Value;

import ru.mystamps.web.tests.page.AuthAccountPage;

import static ru.mystamps.web.SiteMap.INDEX_PAGE_URL;
import static ru.mystamps.web.SiteMap.RESTORE_PASSWORD_PAGE_URL;
import static ru.mystamps.web.tests.TranslationUtils.stripHtmlTags;
import static ru.mystamps.web.tests.TranslationUtils.tr;
import static ru.mystamps.web.validation.ValidationRules.LOGIN_MAX_LENGTH;
import static ru.mystamps.web.validation.ValidationRules.LOGIN_MIN_LENGTH;
import static ru.mystamps.web.validation.ValidationRules.PASSWORD_MIN_LENGTH;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/TestContext.xml")
public class WhenUserAuthenticates extends WhenUserAtAnyPageWithForm<AuthAccountPage> {
	
	@Value("#{test.valid_user_login}")
	private String validUserLogin;
	
	@Value("#{test.valid_user_password}")
	private String validUserPassword;
	
	@Value("#{test.valid_user_name}")
	private String validUserName;
	
	@Value("#{test.invalid_user_login}")
	private String invalidUserLogin;
	
	@Value("#{test.invalid_user_password}")
	private String invalidUserPassword;
	
	public WhenUserAuthenticates() {
		super(AuthAccountPage.class);
		hasTitle(tr("t_auth_title"));
		hasHeader(tr("t_authentication_on_site"));
		
		page.open();
	}
	
	@Test
	public void shouldHaveStandardStructure() {
		checkStandardStructure();
	}
	
	@Test
	public void shouldExistsMessageWithLinkAboutPasswordRecovery() {
		assertThat(page.getFormHints()).contains(stripHtmlTags(tr("t_if_you_forget_password")));
		
		assertThat(page.linkHasLabelAndPointsTo("remind", RESTORE_PASSWORD_PAGE_URL))
			.overridingErrorMessage("should exists link to password restoration page")
			.isTrue();
	}
	
	@Test
	public void loginShouldNotBeTooShort() {
		page.authorizeUser("a", null);
		
		assertThat(page.getFieldError("login")).isEqualTo(tr("value.too-short", LOGIN_MIN_LENGTH));
	}
	
	@Test
	public void loginShouldNotBeTooLong() {
		page.authorizeUser("abcde12345fghkl6", null);
		
		assertThat(page.getFieldError("login")).isEqualTo(tr("value.too-long", LOGIN_MAX_LENGTH));
	}
	
	@Test
	public void loginWithForbiddenCharactersShouldBeRejected() {
		page.authorizeUser("'t@$t'", null);
		
		assertThat(page.getFieldError("login")).isEqualTo(tr("login.invalid"));
	}
	
	@Test
	public void passwordShouldNotBeTooShort() {
		page.authorizeUser(null, "123");
		
		assertThat(page.getFieldError("password"))
			.isEqualTo(tr("value.too-short", PASSWORD_MIN_LENGTH));
	}
	
	@Test
	public void passwordWithForbiddenCharacterShouldBeRejected() {
		page.authorizeUser(null, "'t@$t'");
		
		assertThat(page.getFieldError("password")).isEqualTo(tr("password.invalid"));
	}
	
	@Test
	public void invalidCredentialsShouldBeRejected() {
		page.authorizeUser(invalidUserLogin, invalidUserPassword);
		
		assertThat(page.getFormError()).isEqualTo(tr("login.password.invalid"));
	}
	
	@Test
	public void validCredentialsShouldAuthenticateUserOnSite() {
		page.authorizeUser(validUserLogin, validUserPassword);
		
		assertThat(page.getCurrentUrl())
			.overridingErrorMessage("after login we should be redirected to main page")
			.isEqualTo(INDEX_PAGE_URL);
		
		assertThat(page.getUserBarEntries())
			.overridingErrorMessage("after login user name should be in user bar")
			.contains(validUserName);
		
		assertThat(page.getUserBarEntries())
			.overridingErrorMessage("after login link for logout should be in user bar")
			.contains(tr("t_logout"));
		
		page.logout();
	}
	
}
