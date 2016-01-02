/*
 * Copyright (C) 2009-2016 Slava Semushin <slava.semushin@gmail.com>
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

import static org.fest.assertions.api.Assertions.assertThat;

import ru.mystamps.web.tests.page.UnauthorizedErrorPage;

import ru.mystamps.web.Url;

import static ru.mystamps.web.tests.TranslationUtils.tr;

public class WhenAnonymousUserAddCountry extends WhenAnyUserAtAnyPage<UnauthorizedErrorPage> {
	
	public WhenAnonymousUserAddCountry() {
		super(UnauthorizedErrorPage.class);
		hasTitleWithoutStandardPrefix(tr("t_401_title"));
		hasResponseServerCode(HttpURLConnection.HTTP_UNAUTHORIZED);
	}
	
	@BeforeClass
	public void setUp() {
		page.open(Url.ADD_COUNTRY_PAGE);
	}
	
	@Test(groups = "std")
	public void shouldHaveStandardStructure() {
		checkStandardStructure();
	}
	
	@Test(groups = "misc", dependsOnGroups = "std")
	public void shouldExistsErrorMessage() {
		assertThat(page.getErrorMessage()).isEqualTo(tr("t_401_description", "\n"));
	}
	
	@Test(groups = "misc", dependsOnGroups = "std")
	public void shouldExistsErrorCode() {
		assertThat(page.getErrorCode()).isEqualTo("401");
	}
	
}
