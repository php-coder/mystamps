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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Value;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import ru.mystamps.web.tests.page.AbstractPage;
import ru.mystamps.web.tests.page.AddSeriesPage;
import ru.mystamps.web.tests.page.InfoSeriesPage;

import static ru.mystamps.web.tests.TranslationUtils.tr;
import static ru.mystamps.web.tests.fest.PageWithFormAssert.assertThat;
import static ru.mystamps.web.validation.ValidationRules.MAX_SERIES_COMMENT_LENGTH;
import static ru.mystamps.web.validation.ValidationRules.MAX_STAMPS_IN_SERIES;
import static ru.mystamps.web.validation.ValidationRules.MIN_STAMPS_IN_SERIES;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * The main difference between this test and {@link WhenUserAddSeries} is that as admin we have
 * additional field for comment.
 */
public class WhenAdminAddSeries extends WhenAnyUserAtAnyPageWithForm<AddSeriesPage> {
	
	private static final int SINCE_YEAR     = 1840;
	private static final int CURRENT_YEAR   = new GregorianCalendar().get(Calendar.YEAR);
	
	private static final List<String> EXPECTED_YEARS =
		new ArrayList<>(CURRENT_YEAR - SINCE_YEAR + 1);
	
	private static final String SAMPLE_IMAGE_NAME = "test.png";
	private static final String EMPTY_IMAGE_NAME  = "empty.jpg";
	private static final String SAMPLE_IMAGE_PATH;
	private static final String EMPTY_IMAGE_PATH;
	
	static {
		EXPECTED_YEARS.add("Year");
		// years in reverse order
		for (int i = CURRENT_YEAR; i >= SINCE_YEAR; i--) {
			EXPECTED_YEARS.add(String.valueOf(i));
		}
		
		try {
			SAMPLE_IMAGE_PATH = new File(
				WhenAdminAddSeries.class.getClassLoader().getResource(SAMPLE_IMAGE_NAME).toURI()
			).getAbsolutePath();
			
			EMPTY_IMAGE_PATH = new File(
				WhenAdminAddSeries.class.getClassLoader().getResource(EMPTY_IMAGE_NAME).toURI()
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
		hasTitle(tr("t_add_series"));
		hasHeader(StringUtils.capitalize(tr("t_add_series")));
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
	
	@Test(groups = "valid", dependsOnGroups = "std", dataProvider = "validCatalogNumbers")
	public void catalogNumbersShouldAcceptValidValues(String numbers, Object whatever) {
		page.showCatalogNumbers();
		
		page.fillMichelNumbers(numbers);
		page.fillScottNumbers(numbers);
		page.fillYvertNumbers(numbers);
		page.fillGibbonsNumbers(numbers);
		
		page.submit();
		
		assertThat(page).field("michelNumbers").hasNoError();
		assertThat(page).field("scottNumbers").hasNoError();
		assertThat(page).field("yvertNumbers").hasNoError();
		assertThat(page).field("gibbonsNumbers").hasNoError();
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void quantityShouldBeNotLessThanLimit() {
		page.fillQuantity(String.valueOf(MIN_STAMPS_IN_SERIES - 1));
		
		page.submit();
		
		assertThat(page)
			.field("quantity")
			.hasError(tr("javax.validation.constraints.Min.message", MIN_STAMPS_IN_SERIES));
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void quantityShouldNotBeGreaterThanLimit() {
		page.fillQuantity(String.valueOf(MAX_STAMPS_IN_SERIES + 1));
		
		page.submit();
		
		assertThat(page)
			.field("quantity")
			.hasError(tr("javax.validation.constraints.Max.message", MAX_STAMPS_IN_SERIES));
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std", dataProvider = "invalidCatalogNumbers")
	public void catalogNumbersShouldRejectInvalidValues(String numbers, String msg) {
		page.showCatalogNumbers();
		
		page.fillMichelNumbers(numbers);
		page.fillScottNumbers(numbers);
		page.fillYvertNumbers(numbers);
		page.fillGibbonsNumbers(numbers);
		
		page.submit();
		
		assertThat(page).field("michelNumbers").hasError(msg);
		assertThat(page).field("scottNumbers").hasError(msg);
		assertThat(page).field("yvertNumbers").hasError(msg);
		assertThat(page).field("gibbonsNumbers").hasError(msg);
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std", dataProvider = "invalidCatalogPrices")
	public void catalogPricesShouldRejectInvalidValues(String price, String msg) {
		page.showCatalogNumbers();
		
		page.fillMichelPrice(price);
		page.fillScottPrice(price);
		page.fillYvertPrice(price);
		page.fillGibbonsPrice(price);
		
		page.submit();
		
		assertThat(page).field("michelPrice").hasError(msg);
		assertThat(page).field("scottPrice").hasError(msg);
		assertThat(page).field("yvertPrice").hasError(msg);
		assertThat(page).field("gibbonsPrice").hasError(msg);
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void commentShouldNotBeTooLong() {
		page.showComment();
		page.fillComment(StringUtils.repeat("x", MAX_SERIES_COMMENT_LENGTH + 1));
		
		page.submit();
		
		assertThat(page)
			.field("comment")
			.hasError(tr("value.too-long", MAX_SERIES_COMMENT_LENGTH));
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void imageSizeMustBeGreaterThanZero() {
		page.fillImage(EMPTY_IMAGE_PATH);
		
		page.submit();
		
		assertThat(page)
			.field("image")
			.hasError(tr("ru.mystamps.web.validation.jsr303.NotEmptyFile.message"));
	}
	
	@Test(groups = "misc", dependsOnGroups = "std")
	public void issueYearFieldShouldHaveOptionsForRangeFrom1840ToCurrentYear() {
		page.showDateOfRelease();
		
		assertThat(page.getYearFieldValues()).isEqualTo(EXPECTED_YEARS);
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
	
	@Test(groups = "logic", dependsOnGroups = { "std", "valid", "invalid", "misc" })
	public void shouldIgnoreDuplicatedCatalogNumbers() {
		page.fillCategory(validCategoryName);
		page.fillQuantity("2");
		page.fillImage(SAMPLE_IMAGE_PATH);
		page.showCatalogNumbers();
		
		page.fillMichelNumbers("104,105,104");
		page.fillScottNumbers("114,115,114");
		page.fillYvertNumbers("124,125,124");
		page.fillGibbonsNumbers("134,135,134");
		
		AbstractPage next = page.submit();
		assertThat(next).isInstanceOf(InfoSeriesPage.class);
		
		InfoSeriesPage nextPage = (InfoSeriesPage)next;
		assertThat(nextPage.getMichelCatalogInfo()).isEqualTo("#104, 105");
		assertThat(nextPage.getScottCatalogInfo()).isEqualTo("#114, 115");
		assertThat(nextPage.getYvertCatalogInfo()).isEqualTo("#124, 125");
		assertThat(nextPage.getGibbonsCatalogInfo()).isEqualTo("#134, 135");
	}
	
	@Test(groups = "logic", dependsOnGroups = { "std", "valid", "invalid", "misc" })
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
	
	@DataProvider(name = "validCatalogNumbers")
	public Object[][] getValidCatalogNumbers() {
		return new Object[][] {
			{"7", null},
			{"7,8", null},
			{"71, 81, 91", null},
			{"1000", null}
		};
	}
	
	@DataProvider(name = "invalidCatalogNumbers")
	public Object[][] getInvalidCatalogNumbers() {
		String expectedErrorMessage =
			tr("ru.mystamps.web.validation.jsr303.CatalogNumbers.message");
		
		return new Object[][] {
			{"t", expectedErrorMessage},
			{"t,t", expectedErrorMessage},
			{",1", expectedErrorMessage},
			{"1,", expectedErrorMessage},
			{"1,,2", expectedErrorMessage},
			{"0", expectedErrorMessage},
			{"05", expectedErrorMessage},
			{"1,09", expectedErrorMessage},
			{"10000", expectedErrorMessage}
		};
	}
	
	@DataProvider(name = "invalidCatalogPrices")
	public Object[][] getInvalidCatalogPrices() {
		String expectedErrorMessage = tr("ru.mystamps.web.validation.jsr303.Price.message");
		
		return new Object[][] {
			{"0", expectedErrorMessage},
			{"-1", expectedErrorMessage},
			{"NaN", expectedErrorMessage}
		};
	}
	
	@Override
	protected void shouldHaveUserBar() {
		// Ignore this check because when user authenticated there is no links for login/register.
	}
	
}
