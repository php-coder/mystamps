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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import ru.mystamps.web.tests.page.ActivateAccountPage;

import static ru.mystamps.web.tests.TranslationUtils.tr;
import static ru.mystamps.web.tests.fest.PageWithFormAssert.assertThat;
import static ru.mystamps.web.validation.ValidationRules.ACT_KEY_LENGTH;

public class WhenAnonymousUserActivateAccount
	extends WhenAnyUserAtAnyPageWithForm<ActivateAccountPage> {
	
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
	
}
