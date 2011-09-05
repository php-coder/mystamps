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

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.beans.factory.annotation.Value;

import static org.fest.assertions.Assertions.assertThat;

import static ru.mystamps.web.tests.TranslationUtils.tr;

import org.junit.Test;
import org.junit.runner.RunWith;

import ru.mystamps.web.tests.page.RegisterAccountPage;

/*
 * TODO: use @BeforeClass/@AfterClass annotations from TestNG (#92)
 * (For now we can't use them because JUnit required than them should be static.)
 *
 * TODO: split test to parts
 * (For now we can't do this because order of tests not defined and logout
 * may happens before some of test.)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/TestContext.xml")
public class WhenAuthenticatedUserRegisterAccount
	extends WhenUserAtAnyPageWithForm<RegisterAccountPage> {
	
	@Value("#{test.valid_user_login}")
	private String validUserLogin;
	
	@Value("#{test.valid_user_password}")
	private String validUserPassword;
	
	public WhenAuthenticatedUserRegisterAccount() {
		super(RegisterAccountPage.class);
		hasTitle(tr("t_registration_title"));
		
		page.open();
	}
	
	@Test
	public void shouldHaveStandardStructure() {
		checkStandardStructure();
	}
	
	@Test
	public void messageShouldBeShownAndFormWithLegendAreAbsent() {
		page.login(validUserLogin, validUserPassword);
		
		assertThat(page.textPresent(tr("t_already_registered"))).isTrue();
		assertThat(page.registrationFormExists()).isFalse();
		assertThat(page.getFormHints()).isEmpty();
		
		page.logout();
	}
	
}
