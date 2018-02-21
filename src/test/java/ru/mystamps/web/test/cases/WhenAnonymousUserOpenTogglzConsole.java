/*
 * Copyright (C) 2009-2017 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.test.cases;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import ru.mystamps.web.Url;
import ru.mystamps.web.test.page.ForbiddenErrorPage;

import static ru.mystamps.web.test.TranslationUtils.tr;

import static org.fest.assertions.api.Assertions.assertThat;

public class WhenAnonymousUserOpenTogglzConsole extends WhenAnyUserAtAnyPage<ForbiddenErrorPage> {
	
	public WhenAnonymousUserOpenTogglzConsole() {
		super(ForbiddenErrorPage.class);
	}
	
	@BeforeClass
	public void setUp() {
		page.open(Url.TOGGLZ_CONSOLE_PAGE);
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
