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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

package ru.mystamps.web.tests.cases;

import static org.fest.assertions.api.Assertions.assertThat;

import static ru.mystamps.web.tests.TranslationUtils.tr;
import static ru.mystamps.web.tests.TranslationUtils.stripHtmlTags;
import static ru.mystamps.web.tests.fest.AbstractPageWithFormAssert.assertThat;
import static ru.mystamps.web.validation.ValidationRules.EMAIL_MAX_LENGTH;

import org.apache.commons.lang.StringUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import ru.mystamps.web.Url;
import ru.mystamps.web.tests.page.RegisterAccountPage;

public class WhenUserRegisterAccount extends WhenUserAtAnyPageWithForm<RegisterAccountPage> {
	
	public WhenUserRegisterAccount() {
		super(RegisterAccountPage.class);
		hasTitle(tr("t_registration_title"));
		hasHeader(tr("t_registration_on_site"));
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
	public void shouldExistsMessageWithLinkToAuthenticationPage() {
		assertThat(page.getFormHints()).contains(stripHtmlTags(tr("t_if_you_already_registered")));
		
		assertThat(page.existsLinkTo(Url.AUTHENTICATION_PAGE))
			//.overridingErrorMessage("should exists link to authentication page")
			.isTrue();
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void emailShouldNotBeTooLong() {
		page.registerUser(StringUtils.repeat("0", EMAIL_MAX_LENGTH) + "@mail.ru");
		
		assertThat(page)
			.field("email")
			.hasError(tr("value.too-long", EMAIL_MAX_LENGTH));
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std", dataProvider = "invalidEmails")
	public void emailShouldBeValid(final String invalidEmail, final String expectedMessage) {
		page.registerUser(invalidEmail);
		
		assertThat(page).field("email").hasError(expectedMessage);
	}
	
	@Test(groups = "misc", dependsOnGroups = "std")
	public void emailShouldBeStripedFromLeadingAndTrailingSpaces() {
		page.registerUser(" test ");
		
		assertThat(page).field("email").hasValue("test");
	}
	
	@Test(groups = "logic", dependsOnGroups = { "std", "invalid", "misc" })
	public void successfulMessageShouldBeShownAfterRegistration() {
		page.registerUser("coder@rock.home");
		
		assertThat(page.getCurrentUrl()).isEqualTo(Url.SUCCESSFUL_REGISTRATION_PAGE);
		
		assertThat(page.textPresent(tr("t_activation_sent_message"))).isTrue();
	}
	
	@DataProvider(name = "invalidEmails")
	public Object[][] getInvalidEmails() {
		final String expectedErrorMessage =
			tr("ru.mystamps.web.validation.jsr303.Email.message");
		
		return new Object[][] {
			{"login", expectedErrorMessage},
			{"login@domain", expectedErrorMessage}
		};
	}
	
}
