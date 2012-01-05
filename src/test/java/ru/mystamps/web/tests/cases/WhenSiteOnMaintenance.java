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

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

import ru.mystamps.web.tests.page.MaintenanceSitePage;

import static ru.mystamps.web.tests.TranslationUtils.tr;

public class WhenSiteOnMaintenance extends WhenUserAtAnyPage<MaintenanceSitePage> {
	
	public WhenSiteOnMaintenance() {
		super(MaintenanceSitePage.class);
		hasTitle(tr("t_maintenance_title"));
		
		// TODO:
		//hasResponseServerCode(HttpURLConnection.HTTP_UNAVAILABLE);
		
		page.open();
	}
	
	@Test
	public void shouldHaveStandardStructure() {
		checkStandardStructure();
	}
	
	@Test
	public void shouldExistsMessage() {
		assertThat(page.getErrorMessage()).isEqualTo(tr("t_maintenance_on_site", "\n"));
	}
	
}
