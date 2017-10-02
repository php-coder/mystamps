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
package ru.mystamps.web.tests.cases;

import org.springframework.beans.factory.annotation.Value;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import ru.mystamps.web.tests.page.AddSeriesPage;

import static ru.mystamps.web.tests.fest.PageWithFormAssert.assertThat;

/**
 * The main difference between this test and {@link WhenUserAddSeries} is that as admin we have
 * additional field for comment.
 */
public class WhenAdminAddSeries extends WhenAnyUserAtAnyPageWithForm<AddSeriesPage> {
	
	@Value("${valid_admin_login}")
	private String validAdminLogin;
	
	@Value("${valid_admin_password}")
	private String validAdminPassword;
	
	public WhenAdminAddSeries() {
		super(AddSeriesPage.class);
	}
	
	@BeforeClass
	public void login() {
		page.login(validAdminLogin, validAdminPassword);
	}
	
	@BeforeMethod
	public void openPage() {
		page.open();
	}
	
	@AfterClass(alwaysRun = true)
	public void tearDown() {
		page.logout();
	}
	
	@Test(groups = "std")
	public void shouldHaveStandardStructure() {
		checkStandardStructure();
	}
	
	@Test(groups = "misc", dependsOnGroups = "std")
	public void catalogNumbersShouldBeStripedFromSpaces() {
		page.showCatalogNumbers();
		
		page.fillMichelNumbers(" 1 , 2 ");
		page.fillScottNumbers(" 3 , 4 ");
		page.fillYvertNumbers(" 5 , 6 ");
		page.fillGibbonsNumbers(" 7 , 8 ");
		
		page.submit();
		
		assertThat(page).field("michelNumbers").hasValue("1,2");
		assertThat(page).field("scottNumbers").hasValue("3,4");
		assertThat(page).field("yvertNumbers").hasValue("5,6");
		assertThat(page).field("gibbonsNumbers").hasValue("7,8");
	}
	
	@Test(groups = "misc", dependsOnGroups = "std")
	public void commentShouldBeStripedFromLeadingAndTrailingSpaces() {
		page.showComment();
		page.fillComment(" example comment ");
		
		page.submit();
		
		assertThat(page).field("comment").hasValue("example comment");
	}
	
}
