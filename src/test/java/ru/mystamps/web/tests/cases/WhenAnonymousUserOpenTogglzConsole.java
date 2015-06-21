/*
 * Copyright (C) 2009-2015 Slava Semushin <slava.semushin@gmail.com>
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

import java.net.HttpURLConnection;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import ru.mystamps.web.Url;
import ru.mystamps.web.tests.page.ForbiddenErrorPage;

import static ru.mystamps.web.tests.TranslationUtils.tr;

import static org.fest.assertions.api.Assertions.assertThat;

public class WhenAnonymousUserOpenTogglzConsole extends WhenAnyUserAtAnyPage<ForbiddenErrorPage> {
	
	public WhenAnonymousUserOpenTogglzConsole() {
		super(ForbiddenErrorPage.class);
		hasTitleWithoutStandardPrefix(tr("t_403_title"));
		hasResponseServerCode(HttpURLConnection.HTTP_FORBIDDEN);
	}
	
	@BeforeClass
	public void setUp() {
		page.open(Url.TOGGLZ_CONSOLE_PAGE);
	}
	
	@Test(groups = "std")
	public void shouldHaveStandardStructure() {
		checkStandardStructure();
	}
	
	@Test(groups = "misc", dependsOnGroups = "std")
	public void shouldExistsErrorMessage() {
		assertThat(page.getErrorMessage()).isEqualTo(tr("t_403_description"));
	}
	
	@Test(groups = "misc", dependsOnGroups = "std")
	public void shouldExistsErrorCode() {
		assertThat(page.getErrorCode()).isEqualTo("403");
	}
	
}
