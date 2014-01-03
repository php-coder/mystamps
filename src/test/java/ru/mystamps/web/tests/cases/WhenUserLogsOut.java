/*
 * Copyright (C) 2009-2014 Slava Semushin <slava.semushin@gmail.com>
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
import org.testng.annotations.Test;

import static org.fest.assertions.api.Assertions.assertThat;

import ru.mystamps.web.Url;
import ru.mystamps.web.tests.page.LogoutAccountPage;

import static ru.mystamps.web.tests.TranslationUtils.tr;

public class WhenUserLogsOut extends WhenAnyUserAtAnyPage<LogoutAccountPage> {
	
	@Value("${valid_user_login}")
	private String validUserLogin;
	
	@Value("${valid_user_password}")
	private String validUserPassword;
	
	public WhenUserLogsOut() {
		super(LogoutAccountPage.class);
	}
	
	@Test(groups = "logic")
	public void shouldRedirectAndClearSession() {
		page.login(validUserLogin, validUserPassword);
		page.open();
		
		assertThat(page.getCurrentUrl())
			.overridingErrorMessage("after logout we should be redirected to main page")
			.isEqualTo(Url.INDEX_PAGE);
		
		assertThat(page.linkWithLabelExists(tr("t_enter")))
			.overridingErrorMessage("should exists link to authentication page")
			.isTrue();
		
		assertThat(page.linkWithLabelExists(tr("t_register")))
			.overridingErrorMessage("should exists link to registration page")
			.isTrue();
	}
	
}
