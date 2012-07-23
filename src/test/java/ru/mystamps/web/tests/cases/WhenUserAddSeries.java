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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

package ru.mystamps.web.tests.cases;

import static org.fest.assertions.api.Assertions.assertThat;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import ru.mystamps.web.tests.page.AbstractPage;
import ru.mystamps.web.tests.page.AddSeriesPage;
import ru.mystamps.web.tests.page.InfoSeriesPage;
import ru.mystamps.web.Url;

import static ru.mystamps.web.tests.fest.AbstractPageWithFormAssert.assertThat;
import static ru.mystamps.web.tests.TranslationUtils.tr;
import static ru.mystamps.web.validation.ValidationRules.MAX_SERIES_COMMENT_LENGTH;
import static ru.mystamps.web.validation.ValidationRules.MAX_STAMPS_IN_SERIES;
import static ru.mystamps.web.validation.ValidationRules.MIN_STAMPS_IN_SERIES;

public class WhenUserAddSeries extends WhenUserAtAnyPageWithForm<AddSeriesPage> {
	
	private static final int SINCE_YEAR     = 1840;
	private static final int CURRENT_YEAR   = new GregorianCalendar().get(Calendar.YEAR);
	
	private static final List<String> EXPECTED_YEARS =
		new ArrayList<String>(CURRENT_YEAR - SINCE_YEAR + 1);
	
	private static final String SAMPLE_IMAGE_NAME = "test.png";
	private static final String SAMPLE_IMAGE_PATH;
	
	static {
		EXPECTED_YEARS.add("");
		// years in reverse order
		for (int i = CURRENT_YEAR; i >= SINCE_YEAR; i--) {
			EXPECTED_YEARS.add(String.valueOf(i));
		}
		
		try {
			SAMPLE_IMAGE_PATH = new File(
				WhenUserAddSeries.class.getClassLoader().getResource(SAMPLE_IMAGE_NAME).toURI()
			).getAbsolutePath();
		} catch (final URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
	
	public WhenUserAddSeries() {
		super(AddSeriesPage.class);
		hasTitle(tr("t_add_series"));
		hasHeader(tr("t_add_series_ucfirst"));
	}
	
	@BeforeMethod
	public void openPage() {
		page.open();
	}
	
	@Test(groups = "std")
	public void shouldHaveStandardStructure() {
		checkStandardStructure();
	}
	
	@Test(groups = "valid", dependsOnGroups = "std", dataProvider = "validCatalogNumbers")
	public void michelNumbersShouldAcceptValidValues(final String numbers, final Object whatever) {
		page.fillMichelNumbers(numbers);
		
		page.submit();
		
		assertThat(page).field("michelNumbers").hasNoError();
	}
	
	@Test(groups = "valid", dependsOnGroups = "std", dataProvider = "validCatalogNumbers")
	public void scottNumbersShouldAcceptValidValues(final String numbers, final Object whatever) {
		page.fillScottNumbers(numbers);
		
		page.submit();
		
		assertThat(page).field("scottNumbers").hasNoError();
	}
	
	@Test(groups = "valid", dependsOnGroups = "std", dataProvider = "validCatalogNumbers")
	public void yvertNumbersShouldAcceptValidValues(final String numbers, final Object whatever) {
		page.fillYvertNumbers(numbers);
		
		page.submit();
		
		assertThat(page).field("yvertNumbers").hasNoError();
	}
	
	@Test(groups = "valid", dependsOnGroups = "std", dataProvider = "validCatalogNumbers")
	public void gibbonsNumbersShouldAcceptValidValues(final String numbers, final Object whatever) {
		page.fillGibbonsNumbers(numbers);
		
		page.submit();
		
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
	public void michelNumbersShouldRejectInvalidValues(final String numbers, final String msg) {
		page.fillMichelNumbers(numbers);
		
		page.submit();
		
		assertThat(page).field("michelNumbers").hasError(msg);
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std", dataProvider = "invalidCatalogNumbers")
	public void scottNumbersShouldRejectInvalidValues(final String numbers, final String msg) {
		page.fillScottNumbers(numbers);
		
		page.submit();
		
		assertThat(page).field("scottNumbers").hasError(msg);
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std", dataProvider = "invalidCatalogNumbers")
	public void yvertNumbersShouldRejectInvalidValues(final String numbers, final String msg) {
		page.fillYvertNumbers(numbers);
		
		page.submit();
		
		assertThat(page).field("yvertNumbers").hasError(msg);
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std", dataProvider = "invalidCatalogNumbers")
	public void gibbonsNumbersShouldRejectInvalidValues(final String numbers, final String msg) {
		page.fillGibbonsNumbers(numbers);
		
		page.submit();
		
		assertThat(page).field("gibbonsNumbers").hasError(msg);
	}
	
	@Test(groups = "invalid", dependsOnGroups = "std")
	public void commentShouldNotBeTooLong() {
		page.fillComment(StringUtils.repeat("x", MAX_SERIES_COMMENT_LENGTH + 1));
		
		page.submit();
		
		assertThat(page)
			.field("comment")
			.hasError(tr("value.too-long", MAX_SERIES_COMMENT_LENGTH));
	}
	
	@Test(groups = "misc", dependsOnGroups = "std")
	public void issueYearFieldShouldHaveOptionsForRangeFrom1840ToCurrentYear() {
		assertThat(page.getYearFieldValues()).isEqualTo(EXPECTED_YEARS);
	}
	
	@Test(groups = "misc", dependsOnGroups = "std")
	public void michelNumbersShouldBeStripedFromSpaces() {
		page.fillMichelNumbers(" 1 , 2 ");
		
		page.submit();
		
		assertThat(page).field("michelNumbers").hasValue("1,2");
	}
	
	@Test(groups = "misc", dependsOnGroups = "std")
	public void scottNumbersShouldBeStripedFromSpaces() {
		page.fillScottNumbers(" 3 , 4 ");
		
		page.submit();
		
		assertThat(page).field("scottNumbers").hasValue("3,4");
	}
	
	@Test(groups = "misc", dependsOnGroups = "std")
	public void yvertNumbersShouldBeStripedFromSpaces() {
		page.fillYvertNumbers(" 5 , 6 ");
		
		page.submit();
		
		assertThat(page).field("yvertNumbers").hasValue("5,6");
	}
	
	@Test(groups = "misc", dependsOnGroups = "std")
	public void gibbonsNumbersShouldBeStripedFromSpaces() {
		page.fillGibbonsNumbers(" 7 , 8 ");
		
		page.submit();
		
		assertThat(page).field("gibbonsNumbers").hasValue("7,8");
	}
	
	@Test(groups = "misc", dependsOnGroups = "std")
	public void commentShouldBeStripedFromLeadingAndTrailingSpaces() {
		page.fillComment(" example comment ");
		
		page.submit();
		
		assertThat(page).field("comment").hasValue("example comment");
	}
	
	@Test(groups = "logic", dependsOnGroups = { "std", "valid", "invalid", "misc" })
	public void shouldCreateSeriesWithOnlyRequiredFieldsFilled() {
		final String expectedQuantity = "2";
		final String expectedPageUrl  = Url.INFO_SERIES_PAGE.replace("{id}", "\\d+");
		final String expectedImageUrl = Url.SITE + Url.GET_IMAGE_PAGE.replace("{id}", "\\d+");
		
		page.fillQuantity(expectedQuantity);
		page.fillImage(SAMPLE_IMAGE_PATH);
		
		final AbstractPage next = page.submit();
		assertThat(next).isInstanceOf(InfoSeriesPage.class);
		
		final InfoSeriesPage nextPage = (InfoSeriesPage)next;
		
		assertThat(nextPage.getCurrentUrl()).matches(expectedPageUrl);
		assertThat(nextPage.getImageUrl()).matches(expectedImageUrl);
		assertThat(nextPage.getQuantity()).isEqualTo(expectedQuantity);
		assertThat(nextPage.getPerforated()).isEqualTo(tr("t_yes"));
	}
	
	@Test(groups = "logic", dependsOnGroups = { "std", "valid", "invalid", "misc" })
	public void shouldCreateSeriesWithAllFieldsFilled() {
		final String expectedPageUrl        = Url.INFO_SERIES_PAGE.replace("{id}", "\\d+");
		final String expectedImageUrl       = Url.SITE + Url.GET_IMAGE_PAGE.replace("{id}", "\\d+");
		final String expectedQuantity       = "3";
		final String expectedYear         = "1999";
		final String expectedCountryName  = "Italy";
		final String expectedMichelNumbers  = "1, 2, 3";
		final String expectedScottNumbers   = "10, 11, 12";
		final String expectedYvertNumbers   = "20, 21, 22";
		final String expectedGibbonsNumbers = "30, 31, 32";
		final String expectedComment        = "Any text";
		
		page.fillCountry(expectedCountryName);
		page.fillYear(expectedYear);
		page.fillQuantity(expectedQuantity);
		page.fillPerforated(false);
		page.fillMichelNumbers(expectedMichelNumbers);
		page.fillScottNumbers(expectedScottNumbers);
		page.fillYvertNumbers(expectedYvertNumbers);
		page.fillGibbonsNumbers(expectedGibbonsNumbers);
		page.fillComment(expectedComment);
		page.fillImage(SAMPLE_IMAGE_PATH);
		
		final AbstractPage next = page.submit();
		assertThat(next).isInstanceOf(InfoSeriesPage.class);
		
		final InfoSeriesPage nextPage = (InfoSeriesPage)next;
		
		assertThat(nextPage.getCurrentUrl()).matches(expectedPageUrl);
		assertThat(nextPage.getImageUrl()).matches(expectedImageUrl);
		
		assertThat(nextPage.getCountry()).isEqualTo(expectedCountryName);
		assertThat(nextPage.getYear()).isEqualTo(expectedYear);
		assertThat(nextPage.getQuantity()).isEqualTo(expectedQuantity);
		assertThat(nextPage.getPerforated()).isEqualTo(tr("t_no"));
		assertThat(nextPage.getMichelNumbers()).isEqualTo(expectedMichelNumbers);
		assertThat(nextPage.getScottNumbers()).isEqualTo(expectedScottNumbers);
		assertThat(nextPage.getYvertNumbers()).isEqualTo(expectedYvertNumbers);
		assertThat(nextPage.getGibbonsNumbers()).isEqualTo(expectedGibbonsNumbers);
		assertThat(nextPage.getComment()).isEqualTo(expectedComment);
	}
	
	@Test(groups = "logic", dependsOnGroups = { "std", "valid", "invalid", "misc" })
	public void shouldIgnoreDuplicatedMichelNumbers() {
		page.fillQuantity("2");
		page.fillImage(SAMPLE_IMAGE_PATH);
		page.fillMichelNumbers("4,5,4");
		
		final AbstractPage next = page.submit();
		assertThat(next).isInstanceOf(InfoSeriesPage.class);
		
		final InfoSeriesPage nextPage = (InfoSeriesPage)next;
		assertThat(nextPage.getMichelNumbers()).isEqualTo("4, 5");
	}
	
	@Test(groups = "logic", dependsOnGroups = { "std", "valid", "invalid", "misc" })
	public void shouldIgnoreDuplicatedScottNumbers() {
		page.fillQuantity("2");
		page.fillImage(SAMPLE_IMAGE_PATH);
		page.fillScottNumbers("14,15,14");
		
		final AbstractPage next = page.submit();
		assertThat(next).isInstanceOf(InfoSeriesPage.class);
		
		final InfoSeriesPage nextPage = (InfoSeriesPage)next;
		assertThat(nextPage.getScottNumbers()).isEqualTo("14, 15");
	}
	
	@Test(groups = "logic", dependsOnGroups = { "std", "valid", "invalid", "misc" })
	public void shouldIgnoreDuplicatedYvertNumbers() {
		page.fillQuantity("2");
		page.fillImage(SAMPLE_IMAGE_PATH);
		page.fillYvertNumbers("24,25,24");
		
		final AbstractPage next = page.submit();
		assertThat(next).isInstanceOf(InfoSeriesPage.class);
		
		final InfoSeriesPage nextPage = (InfoSeriesPage)next;
		assertThat(nextPage.getYvertNumbers()).isEqualTo("24, 25");
	}
	
	@Test(groups = "logic", dependsOnGroups = { "std", "valid", "invalid", "misc" })
	public void shouldIgnoreDuplicatedGibbonsNumbers() {
		page.fillQuantity("2");
		page.fillImage(SAMPLE_IMAGE_PATH);
		page.fillGibbonsNumbers("34,35,34");
		
		final AbstractPage next = page.submit();
		assertThat(next).isInstanceOf(InfoSeriesPage.class);
		
		final InfoSeriesPage nextPage = (InfoSeriesPage)next;
		assertThat(nextPage.getGibbonsNumbers()).isEqualTo("34, 35");
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
		final String expectedErrorMessage = tr("catalog-numbers.invalid");
		
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
	
}
