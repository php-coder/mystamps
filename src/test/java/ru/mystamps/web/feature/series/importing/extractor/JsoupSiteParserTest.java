/*
 * Copyright (C) 2009-2022 Slava Semushin <slava.semushin@gmail.com>
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

import org.assertj.core.api.WithAssertions;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.mystamps.web.tests.Random;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static io.qala.datagen.RandomShortApi.nullOr;
import static io.qala.datagen.RandomShortApi.nullOrBlank;
import static io.qala.datagen.RandomShortApi.positiveInteger;

public class JsoupSiteParserTest implements WithAssertions {
	
	private JsoupSiteParser parser;
	
	@BeforeEach
	public void init() {
		parser = new JsoupSiteParser();
	}
	
	//
	// Tests for parse()
	//
	
	@Test
	public void parseShouldRequireNonBlankPageContent() {
		// given
		// when
		Throwable thrown = catchThrowable(() -> {
			parser.parse(nullOrBlank());
		});
		// then
		assertThat(thrown)
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Page content must be non-blank");
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
		String expectedAltPrice = Random.price().toString();
		String expectedAltCurrency = Random.currency().toString();
		
		parser.setMatchedUrl(baseUri);
		parser.setCategoryLocator("#category-name");
		parser.setCountryLocator("#country-name");
		parser.setIssueDateLocator("#issue-date");
		parser.setImageUrlLocator("#image-url");
		parser.setSellerLocator("#seller-info");
		parser.setPriceLocator("#price");
		parser.setCurrencyLocator("#currency");
		parser.setAltPriceLocator("#alt-price");
		parser.setAltCurrencyLocator("#alt-currency");
		
		SeriesInfo expectedInfo = new SeriesInfo();
		expectedInfo.setCategoryName(expectedCategory);
		expectedInfo.setCountryName(expectedCountry);
		expectedInfo.setIssueDate(expectedIssueDate);
		expectedInfo.setImageUrls(Collections.singletonList(expectedImageUrl));
		expectedInfo.setSellerName(expectedSellerName);
		expectedInfo.setSellerUrl(expectedSellerUrl);
		expectedInfo.setPrice(expectedPrice);
		expectedInfo.setCurrency(expectedCurrency);
		expectedInfo.setAltPrice(expectedAltPrice);
		expectedInfo.setAltCurrency(expectedAltCurrency);
		
		String html = String.format(
			"<html>"
				+ "<body>"
					+ "<p id='category-name'>%s</p>"
					+ "<p id='country-name'>%s</p>"
					+ "<p id='issue-date'>%s</p>"
					+ "<a id='image-url' href='%s'>look at image</a>"
					+ "<a id='seller-info' href='%s'>%s</a>"
					+ "<p id='price'>%s</p>"
					+ "<p id='currency'>%s</p>"
					+ "<p id='alt-price'>%s</p>"
					+ "<p id='alt-currency'>%s</p>"
				+ "</body>"
			+ "</html",
			expectedCategory,
			expectedCountry,
			expectedIssueDate,
			imageUrl,
			expectedSellerUrl,
			expectedSellerName,
			expectedPrice,
			expectedCurrency,
			expectedAltPrice,
			expectedAltCurrency
		);
		
		SeriesInfo info = parser.parse(html);
		
		assertThat(info).isEqualTo(expectedInfo);
	}
	
	@Test
	public void parseShouldExtractSeriesInfoFromFirstMatchedElements() {
		String baseUri = "http://base.uri";
		String expectedCategory = Random.categoryName();
		String expectedCountry = Random.countryName();
		String expectedIssueDate = Random.issueYear().toString();
		String firstImageUrl = String.format(
			"/%s-%s-%s-1.png",
			expectedCountry.toLowerCase(Locale.ENGLISH),
			expectedCategory.toLowerCase(Locale.ENGLISH),
			expectedIssueDate
		);
		String secondImageUrl = String.format(
			"/%s-%s-%s-2.png",
			expectedCountry.toLowerCase(Locale.ENGLISH),
			expectedCategory.toLowerCase(Locale.ENGLISH),
			expectedIssueDate
		);
		List<String> expectedImageUrls = Arrays.asList(
			baseUri + firstImageUrl,
			baseUri + secondImageUrl
		);
		String sellerUrl = String.format("/seller/%d/info.htm", positiveInteger());
		String expectedSellerName = Random.sellerName();
		String expectedSellerUrl = baseUri + sellerUrl;
		String expectedPrice = Random.price().toString();
		String expectedCurrency = Random.currency().toString();
		String expectedAltPrice = Random.price().toString();
		String expectedAltCurrency = Random.currency().toString();
		
		parser.setMatchedUrl(baseUri);
		parser.setCategoryLocator("h1");
		parser.setCountryLocator("p");
		parser.setIssueDateLocator("span");
		parser.setImageUrlLocator("a.image");
		parser.setSellerLocator("a.seller");
		parser.setPriceLocator("b");
		parser.setCurrencyLocator("div");
		parser.setAltPriceLocator("i");
		parser.setAltCurrencyLocator("em");
		
		SeriesInfo expectedInfo = new SeriesInfo();
		expectedInfo.setCategoryName(expectedCategory);
		expectedInfo.setCountryName(expectedCountry);
		expectedInfo.setIssueDate(expectedIssueDate);
		// in case of image URLs, it should find all of them
		expectedInfo.setImageUrls(expectedImageUrls);
		expectedInfo.setSellerName(expectedSellerName);
		expectedInfo.setSellerUrl(expectedSellerUrl);
		expectedInfo.setPrice(expectedPrice);
		expectedInfo.setCurrency(expectedCurrency);
		expectedInfo.setAltPrice(expectedAltPrice);
		expectedInfo.setAltCurrency(expectedAltCurrency);
		
		String html = String.format(
			"<html>"
				+ "<body>"
					+ "<h1>%s</h1>"
					+ "<p>%s</p>"
					+ "<span>%s</span>"
					+ "<a class='image' href='%s'>look at image</a>"
					+ "<a class='seller' href='%s'>%s</a>"
					+ "<b>%s</b>"
					+ "<div>%s</div>"
					+ "<i>%s</i>"
					+ "<em>%s</em>"
					+ "<h1>ignored</h1>"
					+ "<p>ignored</p>"
					+ "<span>ignored</span>"
					+ "<a class='image' href='%s'>look at image</a>"
					+ "<a class='seller' href='none'>seller name</a>"
					+ "<b>ignored</b>"
					+ "<div>ignored</div>"
					+ "<i>ignored</i>"
					+ "<em>ignored</em>"
				+ "</body>"
			+ "</html",
			expectedCategory,
			expectedCountry,
			expectedIssueDate,
			firstImageUrl,
			expectedSellerUrl,
			expectedSellerName,
			expectedPrice,
			expectedCurrency,
			expectedAltPrice,
			expectedAltCurrency,
			secondImageUrl
		);
		
		SeriesInfo info = parser.parse(html);
		
		assertThat(info).isEqualTo(expectedInfo);
	}
	
	//
	// Tests for toString()
	//
	
	@Test
	public void toStringShouldReturnName() {
		String expectedName = nullOr(Random.name());
		parser.setName(expectedName);
		
		assertThat(parser.toString()).isEqualTo(expectedName);
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
		
		assertThat(category).isNull();
	}
	
	@Test
	public void extractCategoryShouldReturnNullWhenElementNotFound() {
		parser.setCategoryLocator(Random.jsoupLocator());
		Element doc = createEmptyDocument();
		
		String category = parser.extractCategory(doc);
		
		assertThat(category).isNull();
	}
	
	@Test
	public void extractCategoryShouldReturnTextOfCategoryLocator() {
		parser.setCategoryLocator("#category");
		
		String expectedName = Random.categoryName();
		String html = String.format("<div id='category'>%s</div>", expectedName);
		Element doc = createDocumentFromText(html);
		
		String category = parser.extractCategory(doc);
		
		assertThat(category).as("couldn't extract a category from '%s'", doc)
			.isEqualTo(expectedName);
	}
	
	@Test
	public void extractCategoryShouldReturnTextOfShortDescriptionLocator() {
		parser.setCategoryLocator(null);
		parser.setShortDescriptionLocator("#desc");
		
		String expectedName = Random.categoryName();
		String html = String.format("<div id='desc'>%s</div>", expectedName);
		Element doc = createDocumentFromText(html);
		
		String category = parser.extractCategory(doc);
		
		assertThat(category).as("couldn't extract a category from '%s'", doc)
			.isEqualTo(expectedName);
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
		
		assertThat(country).isNull();
	}
	
	@Test
	public void extractCountryShouldReturnNullWhenElementNotFound() {
		parser.setCountryLocator(Random.jsoupLocator());
		Element doc = createEmptyDocument();
		
		String country = parser.extractCountry(doc);
		
		assertThat(country).isNull();
	}
	
	@Test
	public void extractCountryShouldReturnTextOfCountryLocator() {
		parser.setCountryLocator("#country");
		
		String expectedName = Random.countryName();
		String html = String.format("<div id='country'>%s</div>", expectedName);
		Element doc = createDocumentFromText(html);
		
		String country = parser.extractCountry(doc);
		
		assertThat(country).as("couldn't extract a country from '%s'", doc)
			.isEqualTo(expectedName);
	}
	
	@Test
	public void extractCountryShouldReturnTextOfShortDescriptionLocator() {
		parser.setCountryLocator(null);
		parser.setShortDescriptionLocator("#desc");
		
		String expectedName = Random.countryName();
		String html = String.format("<div id='desc'>%s</div>", expectedName);
		Element doc = createDocumentFromText(html);
		
		String country = parser.extractCountry(doc);
		
		assertThat(country).as("couldn't extract a country from '%s'", doc)
			.isEqualTo(expectedName);
	}
	
	//
	// Tests for extractImageUrls()
	//
	
	@Test
	public void extractImageUrlsShouldReturnEmptyResultWhenLocatorIsNotSet() {
		parser.setImageUrlLocator(null);
		Element doc = createEmptyDocument();
		
		List<String> imageUrls = parser.extractImageUrls(doc);
		
		assertThat(imageUrls).isEmpty();
	}
	
	@Test
	public void extractImageUrlsShouldReturnEmptyResultWhenElementNotFound() {
		parser.setImageUrlLocator(Random.jsoupLocator());
		Element doc = createEmptyDocument();
		
		List<String> imageUrls = parser.extractImageUrls(doc);
		
		assertThat(imageUrls).isEmpty();
	}
	
	@Test
	public void extractImageUrlsShouldReturnValueOfImageUrlAttribute() {
		parser.setImageUrlLocator("a");
		parser.setImageUrlAttribute("data-full-path");
		
		String expectedImageUrl = Random.url();
		String html = String.format(
			"<a href='%s' data-full-path='%s'>test</a>",
			Random.url(),
			expectedImageUrl
		);
		Element doc = createDocumentFromText(html);
		
		List<String> imageUrls = parser.extractImageUrls(doc);
		
		assertThat(imageUrls).as("couldn't extract image urls from '%s'", doc)
			.hasOnlyOneElementSatisfying(
				url -> assertThat(url).isEqualTo(expectedImageUrl)
			);
	}
	
	@Test
	public void extractImageUrlsShouldReturnValueOfHrefAttributeByDefault() {
		parser.setImageUrlLocator("a");
		parser.setImageUrlAttribute(null);
		
		String expectedImageUrl = Random.url();
		String html = String.format(
			"<a href='%s' data-full-path='%s'>test</a>",
			expectedImageUrl,
			Random.url()
		);
		Element doc = createDocumentFromText(html);
		
		List<String> imageUrls = parser.extractImageUrls(doc);
		
		assertThat(imageUrls).as("couldn't extract image urls from '%s'", doc)
			.hasOnlyOneElementSatisfying(
				url -> assertThat(url).isEqualTo(expectedImageUrl)
			);
	}
	
	@Test
	public void extractImageUrlsShouldIgnoreEmptyUrls() {
		parser.setImageUrlLocator("a");
		
		String html = "<a href=''>test</a>";
		Element doc = createDocumentFromText(html);
		
		List<String> imageUrls = parser.extractImageUrls(doc);
		
		assertThat(imageUrls).isEmpty();
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
		
		assertThat(date).isNull();
	}
	
	@Test
	public void extractIssueDateShouldReturnNullWhenElementNotFound() {
		parser.setIssueDateLocator(Random.jsoupLocator());
		Element doc = createEmptyDocument();
		
		String date = parser.extractIssueDate(doc);
		
		assertThat(date).isNull();
	}
	
	@Test
	public void extractIssueDateShouldReturnTextOfIssueDateLocator() {
		parser.setIssueDateLocator("#issue-date");
		
		String expectedDate = Random.issueYear().toString();
		String html = String.format("<div id='issue-date'>%s</div>", expectedDate);
		Element doc = createDocumentFromText(html);
		
		String date = parser.extractIssueDate(doc);
		
		assertThat(date).as("couldn't extract issue date from '%s'", doc)
			.isEqualTo(expectedDate);
	}
	
	@Test
	public void extractIssueDateShouldReturnTextOfShortDescriptionLocator() {
		parser.setIssueDateLocator(null);
		parser.setShortDescriptionLocator("#desc");
		
		String expectedDate = Random.issueYear().toString();
		String html = String.format("<div id='desc'>%s</div>", expectedDate);
		Element doc = createDocumentFromText(html);
		
		String date = parser.extractIssueDate(doc);
		
		assertThat(date).as("couldn't extract issue date from '%s'", doc)
			.isEqualTo(expectedDate);
	}
	
	//
	// Tests for extractQuantity()
	//
	
	@Test
	public void extractQuantityShouldReturnNullWhenShortDescriptionLocatorIsNotSet() {
		parser.setShortDescriptionLocator(null);
		Element doc = createEmptyDocument();
		
		String quantity = parser.extractQuantity(doc);
		
		assertThat(quantity).isNull();
	}
	
	@Test
	public void extractQuantityShouldReturnNullWhenElementNotFound() {
		parser.setShortDescriptionLocator(Random.jsoupLocator());
		Element doc = createEmptyDocument();
		
		String quantity = parser.extractQuantity(doc);
		
		assertThat(quantity).isNull();
	}
	
	@Test
	public void extractQuantityShouldReturnTextOfShortDescriptionLocator() {
		parser.setShortDescriptionLocator("#desc");
		
		String expectedQuantity = Random.quantity().toString();
		String html = String.format("<div id='desc'>%s</div>", expectedQuantity);
		Element doc = createDocumentFromText(html);
		
		String quantity = parser.extractQuantity(doc);
		
		assertThat(quantity).as("couldn't extract quantity from '%s'", doc)
			.isEqualTo(expectedQuantity);
	}
	
	//
	// Tests for extractPerforated()
	//
	
	@Test
	public void extractPerforatedShouldReturnNullWhenShortDescriptionLocatorIsNotSet() {
		parser.setShortDescriptionLocator(null);
		Element doc = createEmptyDocument();
		
		String perforated = parser.extractPerforated(doc);
		
		assertThat(perforated).isNull();
	}
	
	@Test
	public void extractPerforatedShouldReturnNullWhenElementNotFound() {
		parser.setShortDescriptionLocator(Random.jsoupLocator());
		Element doc = createEmptyDocument();
		
		String perforated = parser.extractPerforated(doc);
		
		assertThat(perforated).isNull();
	}
	
	@Test
	public void extractPerforatedShouldReturnTextOfShortDescriptionLocator() {
		parser.setShortDescriptionLocator("#desc");
		
		String expectedValue = String.valueOf(Random.perforated());
		String html = String.format("<div id='desc'>%s</div>", expectedValue);
		Element doc = createDocumentFromText(html);
		
		String perforated = parser.extractPerforated(doc);
		
		assertThat(perforated).as("couldn't extract perforated flag from '%s'", doc)
			.isEqualTo(expectedValue);
	}
	
	//
	// Tests for extractSellerName()
	//
	
	@Test
	public void extractSellerNameShouldReturnNullWhenSellerLocatorIsNotSet() {
		parser.setSellerLocator(null);
		Element doc = createEmptyDocument();
		
		String name = parser.extractSellerName(doc);
		
		assertThat(name).isNull();
	}
	
	@Test
	public void extractSellerNameShouldReturnNullWhenElementNotFound() {
		parser.setSellerLocator(Random.jsoupLocator());
		Element doc = createEmptyDocument();
		
		String name = parser.extractSellerName(doc);
		
		assertThat(name).isNull();
	}
	
	@Test
	public void extractSellerNameShouldReturnTextOfSellerLocator() {
		parser.setSellerLocator("#seller");
		
		String expectedValue = Random.sellerName();
		String html = String.format("<a id='seller'>%s</a>", expectedValue);
		Element doc = createDocumentFromText(html);
		
		String name = parser.extractSellerName(doc);
		
		assertThat(name).as("couldn't extract seller name from '%s'", doc)
			.isEqualTo(expectedValue);
	}
	
	//
	// Tests for extractSellerUrl()
	//
	
	@Test
	public void extractSellerUrlShouldReturnNullWhenSellerLocatorIsNotSet() {
		parser.setSellerLocator(null);
		Element doc = createEmptyDocument();
		
		String url = parser.extractSellerUrl(doc);
		
		assertThat(url).isNull();
	}
	
	@Test
	public void extractSellerUrlShouldReturnNullWhenElementNotFound() {
		parser.setSellerLocator(Random.jsoupLocator());
		Element doc = createEmptyDocument();
		
		String url = parser.extractSellerUrl(doc);
		
		assertThat(url).isNull();
	}
	
	@Test
	public void extractSellerUrlShouldReturnValueOfSellerLocator() {
		parser.setSellerLocator("a");
		
		String expectedUrl = Random.url();
		String html = String.format("<a href='%s'>test</a>", expectedUrl);
		Element doc = createDocumentFromText(html);
		
		String url = parser.extractSellerUrl(doc);
		
		assertThat(url).as("couldn't extract seller url from '%s'", doc)
			.isEqualTo(expectedUrl);
	}
	
	@Test
	public void extractSellerUrlShouldReturnValueOfSellerUrlLocator() {
		parser.setSellerUrlLocator("#seller");
		parser.setSellerLocator("a");
		
		String expectedUrl = Random.url();
		String html = String.format(
			"<a href='http://example.com/test'>test</a><a id='seller' href='%s'>seller</a>",
			expectedUrl
		);
		Element doc = createDocumentFromText(html);
		
		String url = parser.extractSellerUrl(doc);
		
		assertThat(url).as("couldn't extract seller url from '%s'", doc)
			.isEqualTo(expectedUrl);
	}
	
	//
	// Tests for extractPrice()
	//
	
	@Test
	public void extractPriceShouldReturnNullWhenPriceLocatorIsNotSet() {
		parser.setPriceLocator(null);
		Element doc = createEmptyDocument();
		
		String price = parser.extractPrice(doc);
		
		assertThat(price).isNull();
	}
	
	@Test
	public void extractPriceShouldReturnNullWhenElementNotFound() {
		parser.setPriceLocator(Random.jsoupLocator());
		Element doc = createEmptyDocument();
		
		String price = parser.extractPrice(doc);
		
		assertThat(price).isNull();
	}
	
	@Test
	public void extractPriceShouldReturnTextOfPriceLocator() {
		parser.setPriceLocator("#price");
		
		String expectedValue = String.valueOf(Random.price());
		String html = String.format("<span id='price'>%s</span>", expectedValue);
		Element doc = createDocumentFromText(html);
		
		String price = parser.extractPrice(doc);
		
		assertThat(price).as("couldn't extract price from '%s'", doc)
			.isEqualTo(expectedValue);
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
		
		assertThat(price).as("couldn't extract price from '%s'", doc)
			.isEqualTo(expectedValue);
	}
	
	@Test
	public void extractPriceShouldFallbackToTextOfChildrenTags() {
		parser.setPriceLocator("#price");
		
		String expectedPrice = String.valueOf(Random.price());
		String expectedValue = "price:" + expectedPrice;
		String html = String.format(
			"<span id='price'><b>price:</b><b>%s</b></span>",
			expectedPrice
		);
		Element doc = createDocumentFromText(html);
		
		String price = parser.extractPrice(doc);
		
		assertThat(price).as("couldn't extract price from '%s'", doc)
			.isEqualTo(expectedValue);
	}
	
	//
	// Tests for extractCurrency()
	//
	
	@Test
	public void extractCurrencyShouldReturnNullWhenCurrencyValuesAreNotSet() {
		parser.setCurrencyValue(null);
		parser.setCurrencyLocator(null);
		
		String currency = parser.extractCurrency(null);
		
		assertThat(currency).isNull();
	}
	
	@Test
	public void extractCurrencyShouldReturnValueOfCurrencyLocator() {
		String expectedValue = "CZK";
		
		parser.setCurrencyLocator("#currency");
		parser.setCurrencyValue("RUB");
		
		String html = String.format("<span id='currency'>%s</span>", expectedValue);
		Element doc = createDocumentFromText(html);
		
		String currency = parser.extractCurrency(doc);
		
		assertThat(currency).as("couldn't extract currency from '%s'", doc)
			.isEqualTo(expectedValue);
	}
	
	@Test
	public void extractCurrencyShouldReturnCurrencyValue() {
		String expectedCurrency = Random.currency().toString();
		parser.setCurrencyValue(expectedCurrency);
		parser.setCurrencyLocator(Random.jsoupLocator());
		Element doc = createEmptyDocument();
		
		String currency = parser.extractCurrency(doc);
		
		assertThat(currency).isEqualTo(expectedCurrency);
	}
	
	//
	// Tests for extractAltPrice()
	//
	
	@Test
	public void extractAltPriceShouldReturnNullWhenAltPriceLocatorIsNotSet() {
		// given
		parser.setAltPriceLocator(null);
		Element doc = createEmptyDocument();
		// when
		String price = parser.extractAltPrice(doc);
		// then
		assertThat(price).isNull();
	}
	
	@Test
	public void extractAltPriceShouldReturnNullWhenElementNotFound() {
		// given
		parser.setAltPriceLocator(Random.jsoupLocator());
		Element doc = createEmptyDocument();
		// when
		String price = parser.extractAltPrice(doc);
		// then
		assertThat(price).isNull();
	}
	
	@Test
	public void extractAltPriceShouldReturnTextOfAltPriceLocator() {
		// given
		parser.setAltPriceLocator("#alt-price");
		
		String expectedValue = String.valueOf(Random.price());
		String html = String.format("<span id='alt-price'>%s</span>", expectedValue);
		Element doc = createDocumentFromText(html);
		// when
		String price = parser.extractAltPrice(doc);
		// then
		assertThat(price).as("couldn't extract alternative price from '%s'", doc)
			.isEqualTo(expectedValue);
	}
	
	//
	// Tests for extractAltCurrency()
	//
	
	@Test
	public void extractAltCurrencyShouldReturnNullWhenAltCurrencyLocatorIsNotSet() {
		// given
		parser.setAltCurrencyLocator(null);
		Element doc = createEmptyDocument();
		// when
		String currency = parser.extractAltCurrency(doc);
		// then
		assertThat(currency).isNull();
	}
	
	@Test
	public void extractAltCurrencyShouldReturnNullWhenElementNotFound() {
		// given
		parser.setAltCurrencyLocator(Random.jsoupLocator());
		Element doc = createEmptyDocument();
		// when
		String currency = parser.extractAltCurrency(doc);
		// then
		assertThat(currency).isNull();
	}
	
	@Test
	public void extractAltCurrencyShouldReturnTextOfAltCurrencyLocator() {
		// given
		parser.setAltCurrencyLocator("#alt-currency");
		
		String expectedValue = Random.currency().toString();
		String html = String.format("<span id='alt-currency'>%s</span>", expectedValue);
		Element doc = createDocumentFromText(html);
		// when
		String currency = parser.extractAltCurrency(doc);
		// then
		assertThat(currency).as("couldn't extract alternative currency from '%s'", doc)
			.isEqualTo(expectedValue);
	}
	
	private static Element createDocumentFromText(String html) {
		Document doc = Jsoup.parseBodyFragment(html);
		return doc.body();
	}
	
	private static Element createEmptyDocument() {
		return createDocumentFromText("");
	}
	
}
