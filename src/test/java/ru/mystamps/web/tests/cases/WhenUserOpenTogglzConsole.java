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
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.mystamps.web.Url;
import ru.mystamps.web.tests.page.ForbiddenErrorPage;

import static org.fest.assertions.api.Assertions.assertThat;
import static ru.mystamps.web.tests.TranslationUtils.tr;

public class WhenUserOpenTogglzConsole extends WhenAnyUserAtAnyPage<ForbiddenErrorPage> {
	
	@Value("${valid_user_login}")
	private String validUserLogin;
	
	@Value("${valid_user_password}")
	private String validUserPassword;
	
	public WhenUserOpenTogglzConsole() {
		super(ForbiddenErrorPage.class);
	}
	
	@BeforeClass
	public void setUp() {
		page.login(validUserLogin, validUserPassword);
		page.open(Url.TOGGLZ_CONSOLE_PAGE);
	}
	
	@AfterClass(alwaysRun = true)
	public void tearDown() {
		page.logout();
	}
	
	@Test(groups = "misc")
	public void shouldExistsErrorMessage() {
		assertThat(page.getErrorMessage()).isEqualTo(tr("t_403_description"));
	}
	
	@Test(groups = "misc")
	public void shouldExistsErrorCode() {
		assertThat(page.getErrorCode()).isEqualTo("403");
	}
	
}
