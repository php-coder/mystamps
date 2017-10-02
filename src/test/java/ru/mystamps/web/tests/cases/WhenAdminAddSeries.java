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

import java.io.File;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Value;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import ru.mystamps.web.tests.page.AbstractPage;
import ru.mystamps.web.tests.page.AddSeriesPage;
import ru.mystamps.web.tests.page.InfoSeriesPage;

import static ru.mystamps.web.tests.fest.PageWithFormAssert.assertThat;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * The main difference between this test and {@link WhenUserAddSeries} is that as admin we have
 * additional field for comment.
 */
public class WhenAdminAddSeries extends WhenAnyUserAtAnyPageWithForm<AddSeriesPage> {
	
	private static final String SAMPLE_IMAGE_NAME = "test/test.png";
	private static final String SAMPLE_IMAGE_PATH;
	
	static {
		try {
			SAMPLE_IMAGE_PATH = new File(
				WhenAdminAddSeries.class.getClassLoader().getResource(SAMPLE_IMAGE_NAME).toURI()
			).getAbsolutePath();
			
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Value("${valid_admin_login}")
	private String validAdminLogin;
	
	@Value("${valid_admin_password}")
	private String validAdminPassword;
	
	@Value("${existing_michel_number}")
	private String existingMichelNumber;
	
	@Value("${existing_scott_number}")
	private String existingScottNumber;
	
	@Value("${existing_yvert_number}")
	private String existingYvertNumber;
	
	@Value("${existing_gibbons_number}")
	private String existingGibbonsNumber;
	
	@Value("${valid_category_name_en}")
	private String validCategoryName;
	
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
	
	@Test(groups = "logic", dependsOnGroups = { "std", "misc" })
	public void shouldAllowExistingCatalogNumbers() {
		page.fillCategory(validCategoryName);
		page.fillQuantity("2");
		page.fillImage(SAMPLE_IMAGE_PATH);
		page.showCatalogNumbers();
		page.fillMichelNumbers(existingMichelNumber);
		page.fillScottNumbers(existingScottNumber);
		page.fillYvertNumbers(existingYvertNumber);
		page.fillGibbonsNumbers(existingGibbonsNumber);
		
		AbstractPage next = page.submit();
		assertThat(next).isInstanceOf(InfoSeriesPage.class);
		
		InfoSeriesPage nextPage = (InfoSeriesPage)next;
		assertThat(nextPage.getMichelCatalogInfo()).isEqualTo("#" + existingMichelNumber);
		assertThat(nextPage.getScottCatalogInfo()).isEqualTo("#" + existingScottNumber);
		assertThat(nextPage.getYvertCatalogInfo()).isEqualTo("#" + existingYvertNumber);
		assertThat(nextPage.getGibbonsCatalogInfo()).isEqualTo("#" + existingGibbonsNumber);
	}
	
}
