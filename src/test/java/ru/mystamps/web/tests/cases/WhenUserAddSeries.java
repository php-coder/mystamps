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
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.mystamps.web.Url;
import ru.mystamps.web.tests.page.AbstractPage;
import ru.mystamps.web.tests.page.AddSeriesPage;
import ru.mystamps.web.tests.page.InfoSeriesPage;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;
import static ru.mystamps.web.tests.TranslationUtils.tr;
import static ru.mystamps.web.tests.fest.PageWithFormAssert.assertThat;

public class WhenUserAddSeries extends WhenAnyUserAtAnyPage<AddSeriesPage> {
	
	private static final String SAMPLE_IMAGE_NAME = "test/test.png";
	private static final String SAMPLE_IMAGE_PATH;
	
	static {
		try {
			SAMPLE_IMAGE_PATH = new File(
				WhenUserAddSeries.class.getClassLoader().getResource(SAMPLE_IMAGE_NAME).toURI()
			).getAbsolutePath();
			
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Value("${valid_user_login}")
	private String validUserLogin;
	
	@Value("${valid_user_password}")
	private String validUserPassword;
	
	@Value("${valid_category_name_en}")
	private String validCategoryName;
	
	public WhenUserAddSeries() {
		super(AddSeriesPage.class);
	}
	
	@BeforeClass
	public void login() {
		page.login(validUserLogin, validUserPassword);
	}
	
	@BeforeMethod
	public void openPage() {
		page.open();
	}
	
	@AfterClass(alwaysRun = true)
	public void tearDown() {
		page.logout();
	}
	
	@Test(groups = "logic")
	public void shouldCreateSeriesWithOnlyRequiredFieldsFilled() {
		String expectedCategoryName = validCategoryName;
		String expectedQuantity     = "2";
		String expectedPageUrl      = Url.INFO_SERIES_PAGE.replace("{id}", "\\d+");
		String expectedImageUrl     = Url.SITE + Url.GET_IMAGE_PREVIEW_PAGE.replace("{id}", "\\d+");
		
		page.fillCategory(expectedCategoryName);
		page.fillQuantity(expectedQuantity);
		page.fillImage(SAMPLE_IMAGE_PATH);
		
		AbstractPage next = page.submit();
		assertThat(next).isInstanceOf(InfoSeriesPage.class);
		
		InfoSeriesPage nextPage = (InfoSeriesPage)next;
		
		assertThat(nextPage.getCurrentUrl()).matches(expectedPageUrl);
		
		List<String> imageUrls = nextPage.getImageUrls();
		assertThat(imageUrls).hasSize(1);
		assertThat(imageUrls.get(0)).matches(expectedImageUrl);
		
		assertThat(nextPage.getCategory()).isEqualTo(expectedCategoryName);
		assertThat(nextPage.getQuantity()).isEqualTo(expectedQuantity);
		assertThat(nextPage.getPerforated()).isEqualTo(tr("t_yes"));
	}
	
	@Test(groups = "logic")
	public void shouldCreateSeriesWithAllFieldsFilled() {
		String expectedPageUrl      = Url.INFO_SERIES_PAGE.replace("{id}", "\\d+");
		String expectedImageUrl     = Url.SITE + Url.GET_IMAGE_PREVIEW_PAGE.replace("{id}", "\\d+");
		String expectedQuantity     = "3";
		String day                  = "8";
		String month                = "9";
		String year                 = "1999";
		String expectedCountryName  = "Italy";
		String expectedCategoryName = validCategoryName;

		page.fillCategory(expectedCategoryName);
		page.fillCountry(expectedCountryName);
		
		page.showDateOfRelease();
		page.fillDay(day);
		page.fillMonth(month);
		page.fillYear(year);
		
		page.fillQuantity(expectedQuantity);
		page.fillPerforated(false);
		
		page.showCatalogNumbers();
		
		page.fillMichelNumbers("1, 2, 3");
		page.fillMichelPrice("10.5");
		
		page.fillScottNumbers("10, 11, 12");
		page.fillScottPrice("1000");
		
		page.fillYvertNumbers("20, 21, 22");
		page.fillYvertPrice("8.11");
		
		page.fillGibbonsNumbers("30, 31, 32");
		page.fillGibbonsPrice("400.335");
		
		page.fillSolovyovNumbers("40, 41, 42");
		page.fillSolovyovPrice("140.2");
		
		page.fillZagorskiNumbers("50, 51, 52");
		page.fillZagorskiPrice("150.2");
		
		page.fillImage(SAMPLE_IMAGE_PATH);
		
		AbstractPage next = page.submit();
		assertThat(next).isInstanceOf(InfoSeriesPage.class);
		
		InfoSeriesPage nextPage = (InfoSeriesPage)next;
		
		assertThat(nextPage.getCurrentUrl()).matches(expectedPageUrl);
		
		List<String> imageUrls = nextPage.getImageUrls();
		assertThat(imageUrls).hasSize(1);
		assertThat(imageUrls.get(0)).matches(expectedImageUrl);
		
		assertThat(nextPage.getCategory()).isEqualTo(expectedCategoryName);
		assertThat(nextPage.getCountry()).isEqualTo(expectedCountryName);
		assertThat(nextPage.getDateOfRelease()).isEqualTo("08.09.1999");
		assertThat(nextPage.getQuantity()).isEqualTo(expectedQuantity);
		assertThat(nextPage.getPerforated()).isEqualTo(tr("t_no"));
		
		assertThat(nextPage.getMichelCatalogInfo()).isEqualTo("#1-3 (10.5 EUR)");
		assertThat(nextPage.getScottCatalogInfo()).isEqualTo("#10-12 (1000 USD)");
		assertThat(nextPage.getYvertCatalogInfo()).isEqualTo("#20-22 (8.11 EUR)");
		// FIXME: disable rounding mode
		assertThat(nextPage.getGibbonsCatalogInfo()).isEqualTo("#30-32 (400.34 GBP)");
		assertThat(nextPage.getSolovyovCatalogInfo()).isEqualTo("#40-42 (140.2 RUB)");
		assertThat(nextPage.getZagorskiCatalogInfo()).isEqualTo("#50-52 (150.2 RUB)");
	}
	
}
