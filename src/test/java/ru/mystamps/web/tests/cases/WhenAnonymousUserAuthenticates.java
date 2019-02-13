/*
 * Copyright (C) 2009-2019 Slava Semushin <slava.semushin@gmail.com>
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

import org.springframework.beans.factory.annotation.Value;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.mystamps.web.Url;
import ru.mystamps.web.tests.page.AuthAccountPage;

import static org.fest.assertions.api.Assertions.assertThat;
import static ru.mystamps.web.tests.TranslationUtils.tr;

public class WhenAnonymousUserAuthenticates extends WhenAnyUserAtAnyPageWithForm<AuthAccountPage> {
	
	@Value("${valid_user_login}")
	private String validUserLogin;
	
	@Value("${valid_user_password}")
	private String validUserPassword;
	
	@Value("${valid_user_name}")
	private String validUserName;
	
	@Value("${invalid_user_login}")
	private String invalidUserLogin;
	
	@Value("${invalid_user_password}")
	private String invalidUserPassword;
	
	public WhenAnonymousUserAuthenticates() {
		super(AuthAccountPage.class);
	}
	
	@BeforeMethod
	public void openPage() {
		page.open();
	}
	
	@Test(groups = "std")
	public void shouldHaveStandardStructure() {
		checkStandardStructure();
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void emptyValuesShouldBeConsideredAsInvalidCredentials() {
		page.authorizeUser("", "");
		
		assertThat(page.getFormError())
			.isEqualTo(tr("AbstractUserDetailsAuthenticationProvider.badCredentials"));
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void invalidCredentialsShouldBeRejected() {
		page.authorizeUser(invalidUserLogin, invalidUserPassword);
		
		assertThat(page.getFormError())
			.isEqualTo(tr("AbstractUserDetailsAuthenticationProvider.badCredentials"));
	}
	
	@Test(groups = "logic", dependsOnGroups = "std")
	public void validCredentialsShouldAuthenticateUserOnSite() {
		page.authorizeUser(validUserLogin, validUserPassword);
		
		assertThat(page.getCurrentUrl())
			.overridingErrorMessage("after login we should be redirected to main page")
			.isEqualTo(Url.INDEX_PAGE);
		
		assertThat(page.getUserBarEntries())
			.overridingErrorMessage("after login user name should be in user bar")
			.contains(validUserName);
		
		assertThat(page.getUserBarEntries())
			.overridingErrorMessage("after login link for logout should be in user bar")
			.contains(tr("t_logout"));
		
		page.logout();
	}
	
	@Override
	protected void emptyValueShouldBeForbiddenForRequiredFields() {
		// Ignore this check entirely because login page has another behavior:
		// message about invalid credentials displayed for all type of errors.
		// See also test emptyValuesShouldBeConsideredAsInvalidCredentials()
	}
	
}
