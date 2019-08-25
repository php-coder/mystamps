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
package ru.mystamps.web.feature.series.importing.extractor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import ru.mystamps.web.tests.Random;
import java.util.Locale;

import static io.qala.datagen.RandomShortApi.nullOr;
import static io.qala.datagen.RandomShortApi.nullOrBlank;
import static io.qala.datagen.RandomShortApi.positiveInteger;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class JsoupSiteParserTest {
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	private JsoupSiteParser parser;
	
	@Before
	public void init() {
		parser = new JsoupSiteParser();
	}
	
	//
	// Tests for parse()
	//
	
	@Test
	public void parseShouldRequireNonBlankPageContent() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Page content must be non-blank");
		
		parser.parse(nullOrBlank());
	}
	
	@Test
	public void parseShouldExtractSeriesInfo() {
		String baseUri = "http://base.uri";
		String expectedCategory = Random.categoryName();
		String expectedCountry = Random.countryName();
		String expectedIssueDate = Random.issueYear().toString();
		String imageUrl = String.format(
			"/%s-%s-%s.png",
			expectedCountry.toLowerCase(Locale.ENGLISH),
			expectedCategory.toLowerCase(Locale.ENGLISH),
			expectedIssueDate
		);
		String expectedImageUrl = baseUri + imageUrl;
		String expectedSellerName = Random.sellerName();
		String expectedSellerUrl = Random.url();
		String expectedPrice = Random.price().toString();
		String expectedCurrency = Random.currency().toString();
		
		parser.setMatchedUrl(baseUri);
		parser.setCategoryLocator("#category-name");
		parser.setCountryLocator("#country-name");
		parser.setIssueDateLocator("#issue-date");
		parser.setImageUrlLocator("#image-url");
		parser.setSellerLocator("#seller-info");
		parser.setPriceLocator("#price");
		parser.setCurrencyValue(expectedCurrency);
		
		SeriesInfo expectedInfo = new SeriesInfo();
		expectedInfo.setCategoryName(expectedCategory);
		expectedInfo.setCountryName(expectedCountry);
		expectedInfo.setIssueDate(expectedIssueDate);
		expectedInfo.setImageUrl(expectedImageUrl);
		expectedInfo.setSellerName(expectedSellerName);
		expectedInfo.setSellerUrl(expectedSellerUrl);
		expectedInfo.setPrice(expectedPrice);
		expectedInfo.setCurrency(expectedCurrency);
		
		String html = String.format(
			"<html>"
				+ "<body>"
					+ "<p id='category-name'>%s</p>"
					+ "<p id='country-name'>%s</p>"
					+ "<p id='issue-date'>%s</p>"
					+ "<a id='image-url' href='%s'>look at image</a>"
					+ "<a id='seller-info' href='%s'>%s</a>"
					+ "<p id='price'>%s</p>"
				+ "</body>"
			+ "</html",
			expectedCategory,
			expectedCountry,
			expectedIssueDate,
			imageUrl,
			expectedSellerUrl,
			expectedSellerName,
			expectedPrice
		);
		
		SeriesInfo info = parser.parse(html);
		
		assertThat(info, is(equalTo(expectedInfo)));
	}
	
	@Test
	public void parseShouldExtractSeriesInfoFromFirstMatchedElements() {
		String baseUri = "http://base.uri";
		String expectedCategory = Random.categoryName();
		String expectedCountry = Random.countryName();
		String expectedIssueDate = Random.issueYear().toString();
		String imageUrl = String.format(
			"/%s-%s-%s.png",
			expectedCountry.toLowerCase(Locale.ENGLISH),
			expectedCategory.toLowerCase(Locale.ENGLISH),
			expectedIssueDate
		);
		String sellerUrl = String.format("/seller/%d/info.htm", positiveInteger());
		String expectedImageUrl = baseUri + imageUrl;
		String expectedSellerName = Random.sellerName();
		String expectedSellerUrl = baseUri + sellerUrl;
		String expectedPrice = Random.price().toString();
		
		parser.setMatchedUrl(baseUri);
		parser.setCategoryLocator("h1");
		parser.setCountryLocator("p");
		parser.setIssueDateLocator("span");
		parser.setImageUrlLocator("a.image");
		parser.setSellerLocator("a.seller");
		parser.setPriceLocator("b");
		
		SeriesInfo expectedInfo = new SeriesInfo();
		expectedInfo.setCategoryName(expectedCategory);
		expectedInfo.setCountryName(expectedCountry);
		expectedInfo.setIssueDate(expectedIssueDate);
		expectedInfo.setImageUrl(expectedImageUrl);
		expectedInfo.setSellerName(expectedSellerName);
		expectedInfo.setSellerUrl(expectedSellerUrl);
		expectedInfo.setPrice(expectedPrice);
		
		String html = String.format(
			"<html>"
				+ "<body>"
					+ "<h1>%s</h1>"
					+ "<p>%s</p>"
					+ "<span>%s</span>"
					+ "<a class='image' href='%s'>look at image</a>"
					+ "<a class='seller' href='%s'>%s</a>"
					+ "<b>%s</b>"
					+ "<h1>ignored</h1>"
					+ "<p>ignored</p>"
					+ "<span>ignored</span>"
					+ "<a class='image' href='none'>look at image</a>"
					+ "<a class='seller' href='none'>seller name</a>"
					+ "<b>ignored</b>"
				+ "</body>"
			+ "</html",
			expectedCategory,
			expectedCountry,
			expectedIssueDate,
			expectedImageUrl,
			expectedSellerUrl,
			expectedSellerName,
			expectedPrice
		);
		
		SeriesInfo info = parser.parse(html);
		
		assertThat(info, is(equalTo(expectedInfo)));
	}
	
	//
	// Tests for toString()
	//
	
	@SuppressWarnings("checkstyle:magicnumber")
	@Test
	public void toStringShouldReturnName() {
		String expectedName = nullOr(Random.name());
		parser.setName(expectedName);
		
		assertThat(parser.toString(), equalTo(expectedName));
	}
	
	//
	// Tests for extractCategory()
	//
	
	@Test
	public void extractCategoryShouldReturnNullWhenLocatorsAreNotSet() {
		parser.setCategoryLocator(null);
		parser.setShortDescriptionLocator(null);
		Element doc = createEmptyDocument();
		
		String category = parser.extractCategory(doc);
		
		assertThat(category, is(nullValue()));
	}
	
	@Test
	public void extractCategoryShouldReturnNullWhenElementNotFound() {
		parser.setCategoryLocator(Random.jsoupLocator());
		Element doc = createEmptyDocument();
		
		String category = parser.extractCategory(doc);
		
		assertThat(category, is(nullValue()));
	}
	
	@Test
	public void extractCategoryShouldReturnTextOfCategoryLocator() {
		parser.setCategoryLocator("#category");
		
		String expectedName = Random.categoryName();
		String html = String.format("<div id='category'>%s</div>", expectedName);
		Element doc = createDocumentFromText(html);
		
		String category = parser.extractCategory(doc);
		
		String msg = String.format("couldn't extract a category from '%s'", doc);
		assertThat(msg, category, equalTo(expectedName));
	}
	
	@Test
	public void extractCategoryShouldReturnTextOfShortDescriptionLocator() {
		parser.setCategoryLocator(null);
		parser.setShortDescriptionLocator("#desc");
		
		String expectedName = Random.categoryName();
		String html = String.format("<div id='desc'>%s</div>", expectedName);
		Element doc = createDocumentFromText(html);
		
		String category = parser.extractCategory(doc);
		
		String msg = String.format("couldn't extract a category from '%s'", doc);
		assertThat(msg, category, equalTo(expectedName));
	}
	
	//
	// Tests for extractCountry()
	//
	
	@Test
	public void extractCountryShouldReturnNullWhenLocatorsAreNotSet() {
		parser.setCountryLocator(null);
		parser.setShortDescriptionLocator(null);
		Element doc = createEmptyDocument();
		
		String country = parser.extractCountry(doc);
		
		assertThat(country, is(nullValue()));
	}
	
	@Test
	public void extractCountryShouldReturnNullWhenElementNotFound() {
		parser.setCountryLocator(Random.jsoupLocator());
		Element doc = createEmptyDocument();
		
		String country = parser.extractCountry(doc);
		
		assertThat(country, is(nullValue()));
	}
	
	@Test
	public void extractCountryShouldReturnTextOfCountryLocator() {
		parser.setCountryLocator("#country");
		
		String expectedName = Random.countryName();
		String html = String.format("<div id='country'>%s</div>", expectedName);
		Element doc = createDocumentFromText(html);
		
		String country = parser.extractCountry(doc);
		
		String msg = String.format("couldn't extract a country from '%s'", doc);
		assertThat(msg, country, equalTo(expectedName));
	}
	
	@Test
	public void extractCountryShouldReturnTextOfShortDescriptionLocator() {
		parser.setCountryLocator(null);
		parser.setShortDescriptionLocator("#desc");
		
		String expectedName = Random.countryName();
		String html = String.format("<div id='desc'>%s</div>", expectedName);
		Element doc = createDocumentFromText(html);
		
		String country = parser.extractCountry(doc);
		
		String msg = String.format("couldn't extract a country from '%s'", doc);
		assertThat(msg, country, equalTo(expectedName));
	}
	
	//
	// Tests for extractImageUrl()
	//
	
	@Test
	public void extractImageUrlShouldReturnNullWhenLocatorIsNotSet() {
		parser.setImageUrlLocator(null);
		Element doc = createEmptyDocument();
		
		String imageUrl = parser.extractImageUrl(doc);
		
		assertThat(imageUrl, is(nullValue()));
	}
	
	@Test
	public void extractImageUrlShouldReturnNullWhenElementNotFound() {
		parser.setImageUrlLocator(Random.jsoupLocator());
		Element doc = createEmptyDocument();
		
		String imageUrl = parser.extractImageUrl(doc);
		
		assertThat(imageUrl, is(nullValue()));
	}
	
	@Test
	public void extractImageUrlShouldReturnValueOfImageUrlAttribute() {
		parser.setImageUrlLocator("a");
		parser.setImageUrlAttribute("data-full-path");
		
		String expectedImageUrl = Random.url();
		String html = String.format(
			"<a href='%s' data-full-path='%s'>test</a>",
			Random.url(),
			expectedImageUrl
		);
		Element doc = createDocumentFromText(html);
		
		String imageUrl = parser.extractImageUrl(doc);
		
		String msg = String.format("couldn't extract image url from '%s'", doc);
		assertThat(msg, imageUrl, equalTo(expectedImageUrl));
	}
	
	@Test
	public void extractImageUrlShouldReturnValueOfHrefAttributeByDefault() {
		parser.setImageUrlLocator("a");
		parser.setImageUrlAttribute(null);
		
		String expectedImageUrl = Random.url();
		String html = String.format(
			"<a href='%s' data-full-path='%s'>test</a>",
			expectedImageUrl,
			Random.url()
		);
		Element doc = createDocumentFromText(html);
		
		String imageUrl = parser.extractImageUrl(doc);
		
		String msg = String.format("couldn't extract image url from '%s'", doc);
		assertThat(msg, imageUrl, equalTo(expectedImageUrl));
	}
	
	@Test
	public void extractImageUrlShouldReturnNullInsteadOfEmptyString() {
		parser.setImageUrlLocator("a");
		
		String html = "<a href=''>test</a>";
		Element doc = createDocumentFromText(html);
		
		String imageUrl = parser.extractImageUrl(doc);
		
		assertThat(imageUrl, is(nullValue()));
	}
	
	//
	// Tests for extractIssueDate()
	//
	
	@Test
	public void extractIssueDateShouldReturnNullWhenLocatorsAreNotSet() {
		parser.setIssueDateLocator(null);
		parser.setShortDescriptionLocator(null);
		Element doc = createEmptyDocument();
		
		String date = parser.extractIssueDate(doc);
		
		assertThat(date, is(nullValue()));
	}
	
	@Test
	public void extractIssueDateShouldReturnNullWhenElementNotFound() {
		parser.setIssueDateLocator(Random.jsoupLocator());
		Element doc = createEmptyDocument();
		
		String date = parser.extractIssueDate(doc);
		
		assertThat(date, is(nullValue()));
	}
	
	@Test
	public void extractIssueDateShouldReturnTextOfIssueDateLocator() {
		parser.setIssueDateLocator("#issue-date");
		
		String expectedDate = Random.issueYear().toString();
		String html = String.format("<div id='issue-date'>%s</div>", expectedDate);
		Element doc = createDocumentFromText(html);
		
		String date = parser.extractIssueDate(doc);
		
		String msg = String.format("couldn't extract issue date from '%s'", doc);
		assertThat(msg, date, equalTo(expectedDate));
	}
	
	@Test
	public void extractIssueDateShouldReturnTextOfShortDescriptionLocator() {
		parser.setIssueDateLocator(null);
		parser.setShortDescriptionLocator("#desc");
		
		String expectedDate = Random.issueYear().toString();
		String html = String.format("<div id='desc'>%s</div>", expectedDate);
		Element doc = createDocumentFromText(html);
		
		String date = parser.extractIssueDate(doc);
		
		String msg = String.format("couldn't extract issue date from '%s'", doc);
		assertThat(msg, date, equalTo(expectedDate));
	}
	
	//
	// Tests for extractQuantity()
	//
	
	@Test
	public void extractQuantityShouldReturnNullWhenShortDescriptionLocatorIsNotSet() {
		parser.setShortDescriptionLocator(null);
		Element doc = createEmptyDocument();
		
		String quantity = parser.extractQuantity(doc);
		
		assertThat(quantity, is(nullValue()));
	}
	
	@Test
	public void extractQuantityShouldReturnNullWhenElementNotFound() {
		parser.setShortDescriptionLocator(Random.jsoupLocator());
		Element doc = createEmptyDocument();
		
		String quantity = parser.extractQuantity(doc);
		
		assertThat(quantity, is(nullValue()));
	}
	
	@Test
	public void extractQuantityShouldReturnTextOfShortDescriptionLocator() {
		parser.setShortDescriptionLocator("#desc");
		
		String expectedQuantity = Random.quantity().toString();
		String html = String.format("<div id='desc'>%s</div>", expectedQuantity);
		Element doc = createDocumentFromText(html);
		
		String quantity = parser.extractQuantity(doc);
		
		String msg = String.format("couldn't extract quantity from '%s'", doc);
		assertThat(msg, quantity, equalTo(expectedQuantity));
	}
	
	//
	// Tests for extractPerforated()
	//
	
	@Test
	public void extractPerforatedShouldReturnNullWhenShortDescriptionLocatorIsNotSet() {
		parser.setShortDescriptionLocator(null);
		Element doc = createEmptyDocument();
		
		String perforated = parser.extractPerforated(doc);
		
		assertThat(perforated, is(nullValue()));
	}
	
	@Test
	public void extractPerforatedShouldReturnNullWhenElementNotFound() {
		parser.setShortDescriptionLocator(Random.jsoupLocator());
		Element doc = createEmptyDocument();
		
		String perforated = parser.extractPerforated(doc);
		
		assertThat(perforated, is(nullValue()));
	}
	
	@Test
	public void extractPerforatedShouldReturnTextOfShortDescriptionLocator() {
		parser.setShortDescriptionLocator("#desc");
		
		String expectedValue = String.valueOf(Random.perforated());
		String html = String.format("<div id='desc'>%s</div>", expectedValue);
		Element doc = createDocumentFromText(html);
		
		String perforated = parser.extractPerforated(doc);
		
		String msg = String.format("couldn't extract perforated flag from '%s'", doc);
		assertThat(msg, perforated, equalTo(expectedValue));
	}
	
	//
	// Tests for extractSellerName()
	//
	
	@Test
	public void extractSellerNameShouldReturnNullWhenSellerLocatorIsNotSet() {
		parser.setSellerLocator(null);
		Element doc = createEmptyDocument();
		
		String name = parser.extractSellerName(doc);
		
		assertThat(name, is(nullValue()));
	}
	
	@Test
	public void extractSellerNameShouldReturnNullWhenElementNotFound() {
		parser.setSellerLocator(Random.jsoupLocator());
		Element doc = createEmptyDocument();
		
		String name = parser.extractSellerName(doc);
		
		assertThat(name, is(nullValue()));
	}
	
	@Test
	public void extractSellerNameShouldReturnTextOfSellerLocator() {
		parser.setSellerLocator("#seller");
		
		String expectedValue = Random.sellerName();
		String html = String.format("<a id='seller'>%s</a>", expectedValue);
		Element doc = createDocumentFromText(html);
		
		String name = parser.extractSellerName(doc);
		
		String msg = String.format("couldn't extract seller name from '%s'", doc);
		assertThat(msg, name, equalTo(expectedValue));
	}
	
	//
	// Tests for extractSellerUrl()
	//
	
	@Test
	public void extractSellerUrlShouldReturnNullWhenSellerLocatorIsNotSet() {
		parser.setSellerLocator(null);
		Element doc = createEmptyDocument();
		
		String url = parser.extractSellerUrl(doc);
		
		assertThat(url, is(nullValue()));
	}
	
	@Test
	public void extractSellerUrlShouldReturnNullWhenElementNotFound() {
		parser.setSellerLocator(Random.jsoupLocator());
		Element doc = createEmptyDocument();
		
		String url = parser.extractSellerUrl(doc);
		
		assertThat(url, is(nullValue()));
	}
	
	@Test
	public void extractSellerUrlShouldReturnValueOfHrefAttribute() {
		parser.setSellerLocator("a");
		
		String expectedUrl = Random.url();
		String html = String.format("<a href='%s'>test</a>", expectedUrl);
		Element doc = createDocumentFromText(html);
		
		String url = parser.extractSellerUrl(doc);
		
		String msg = String.format("couldn't extract seller url from '%s'", doc);
		assertThat(msg, url, equalTo(expectedUrl));
	}
	
	//
	// Tests for extractPrice()
	//
	
	@Test
	public void extractPriceShouldReturnNullWhenPriceLocatorIsNotSet() {
		parser.setPriceLocator(null);
		Element doc = createEmptyDocument();
		
		String price = parser.extractPrice(doc);
		
		assertThat(price, is(nullValue()));
	}
	
	@Test
	public void extractPriceShouldReturnNullWhenElementNotFound() {
		parser.setPriceLocator(Random.jsoupLocator());
		Element doc = createEmptyDocument();
		
		String price = parser.extractPrice(doc);
		
		assertThat(price, is(nullValue()));
	}
	
	@Test
	public void extractPriceShouldReturnTextOfPriceLocator() {
		parser.setPriceLocator("#price");
		
		String expectedValue = String.valueOf(Random.price());
		String html = String.format("<span id='price'>%s</span>", expectedValue);
		Element doc = createDocumentFromText(html);
		
		String price = parser.extractPrice(doc);
		
		String msg = String.format("couldn't extract price from '%s'", doc);
		assertThat(msg, price, equalTo(expectedValue));
	}
	
	@Test
	public void extractPriceShouldIgnoreTextOfChildrenTags() {
		parser.setPriceLocator("#price");
		
		String expectedValue = String.valueOf(Random.price());
		String html = String.format(
			"<span id='price'>%s<span class='currency'>RUB</span</span>",
			expectedValue
		);
		Element doc = createDocumentFromText(html);
		
		String price = parser.extractPrice(doc);
		
		String msg = String.format("couldn't extract price from '%s'", doc);
		assertThat(msg, price, equalTo(expectedValue));
	}
	
	//
	// Tests for extractCurrency()
	//
	
	@Test
	public void extractCurrencyShouldReturnNullWhenCurrencyValueIsNotSet() {
		parser.setCurrencyValue(null);
		
		String currency = parser.extractCurrency(null);
		
		assertThat(currency, is(nullValue()));
	}
	
	@Test
	public void extractCurrencyShouldReturnCurrencyValue() {
		String expectedCurrency = Random.currency().toString();
		parser.setCurrencyValue(expectedCurrency);
		
		String currency = parser.extractCurrency(null);
		
		assertThat(currency, equalTo(expectedCurrency));
	}
	
	private static Element createDocumentFromText(String html) {
		Document doc = Jsoup.parseBodyFragment(html);
		return doc.body();
	}
	
	private static Element createEmptyDocument() {
		return createDocumentFromText("");
	}
	
}
