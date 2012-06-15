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

import static org.fest.assertions.api.Assertions.assertThat;

import org.springframework.beans.factory.annotation.Value;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import ru.mystamps.web.Url;
import ru.mystamps.web.tests.page.AuthAccountPage;

import static ru.mystamps.web.tests.TranslationUtils.stripHtmlTags;
import static ru.mystamps.web.tests.TranslationUtils.tr;
import static ru.mystamps.web.tests.fest.AbstractPageWithFormAssert.assertThat;
import static ru.mystamps.web.validation.ValidationRules.LOGIN_MAX_LENGTH;
import static ru.mystamps.web.validation.ValidationRules.LOGIN_MIN_LENGTH;
import static ru.mystamps.web.validation.ValidationRules.PASSWORD_MIN_LENGTH;

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
	public void shouldExistsMessageWithLinkAboutPasswordRecovery() {
		assertThat(page.getFormHints()).contains(stripHtmlTags(tr("t_if_you_forget_password")));
		
		assertThat(page.existsLinkTo(Url.RESTORE_PASSWORD_PAGE))
			//.overridingErrorMessage("should exists link to password restoration page")
			.isTrue();
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void loginShouldNotBeTooShort() {
		page.authorizeUser("a", null);
		
		assertThat(page)
			.field("login")
			.hasError(tr("value.too-short", LOGIN_MIN_LENGTH));
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void loginShouldNotBeTooLong() {
		page.authorizeUser("abcde12345fghkl6", null);
		
		assertThat(page)
			.field("login")
			.hasError(tr("value.too-long", LOGIN_MAX_LENGTH));
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void loginWithForbiddenCharactersShouldBeRejected() {
		page.authorizeUser("'t@$t'", null);
		
		assertThat(page)
			.field("login")
			.hasError(tr("login.invalid"));
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void passwordShouldNotBeTooShort() {
		page.authorizeUser(null, "123");
		
		assertThat(page)
			.field("password")
			.hasError(tr("value.too-short", PASSWORD_MIN_LENGTH));
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void passwordWithForbiddenCharacterShouldBeRejected() {
		page.authorizeUser(null, "'t@$t'");
		
		assertThat(page)
			.field("password")
			.hasError(tr("password.invalid"));
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void invalidCredentialsShouldBeRejected() {
		page.authorizeUser(invalidUserLogin, invalidUserPassword);
		
		assertThat(page.getFormError())
			.isEqualTo(tr("ru.mystamps.web.validation.jsr303.ValidCredentials.message"));
	}
	
	@Test(groups = "logic", dependsOnGroups = "std")
	public void validCredentialsShouldAuthenticateUserOnSite() {
		page.authorizeUser(validUserLogin, validUserPassword);
		
		assertThat(page.getCurrentUrl())
			//.overridingErrorMessage("after login we should be redirected to main page")
			.isEqualTo(Url.INDEX_PAGE);
		
		assertThat(page.getUserBarEntries())
			//.overridingErrorMessage("after login user name should be in user bar")
			.contains(validUserName);
		
		assertThat(page.getUserBarEntries())
			//.overridingErrorMessage("after login link for logout should be in user bar")
			.contains(tr("t_logout"));
		
		page.logout();
	}
	
}
