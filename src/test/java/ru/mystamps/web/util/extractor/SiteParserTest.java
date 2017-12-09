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
package ru.mystamps.web.util.extractor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import ru.mystamps.web.tests.Random;

import static io.qala.datagen.RandomShortApi.nullOr;
import static io.qala.datagen.RandomShortApi.nullOrBlank;
import static io.qala.datagen.RandomValue.between;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class SiteParserTest {
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	private SiteParser parser;
	
	@Before
	public void init() {
		parser = new SiteParser();
	}
	
	//
	// Tests for setField()
	//
	
	@Test
	public void setFieldShouldRequireNonBlankName() {
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("Field name must be non-blank");
		
		parser.setField(nullOrBlank(), Random.jsoupLocator());
	}
	
	@Test
	public void setFieldShouldRequireNonBlankValue() {
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("Field value must be non-blank");
		
		// @todo #685 SiteParserTest: introduce a method for generating random valid field name
		String anyValidFieldName = "name";
		
		parser.setField(anyValidFieldName, nullOrBlank());
	}
	
	@Test
	public void setFieldShouldSupportSettingName() {
		String expectedName = Random.name();
		
		boolean valid = parser.setField("name", expectedName);
		
		assertThat(valid, is(true));
		assertThat(parser.getName(), equalTo(expectedName));
	}
	
	@Test
	public void setFieldShouldSupportSettingMatchedUrl() {
		String expectedUrl = Random.url();
		
		boolean valid = parser.setField("matched-url", expectedUrl);
		
		assertThat(valid, is(true));
		assertThat(parser.getMatchedUrl(), equalTo(expectedUrl));
	}
	
	@Test
	public void setFieldShouldSupportSettingCategoryLocator() {
		String expectedLocator = Random.jsoupLocator();
		
		boolean valid = parser.setField("category-locator", expectedLocator);
		
		assertThat(valid, is(true));
		assertThat(parser.getCategoryLocator(), equalTo(expectedLocator));
	}
	
	@Test
	public void setFieldShouldSupportSettingCountryLocator() {
		String expectedLocator = Random.jsoupLocator();
		
		boolean valid = parser.setField("country-locator", expectedLocator);
		
		assertThat(valid, is(true));
		assertThat(parser.getCountryLocator(), equalTo(expectedLocator));
	}
	
	@Test
	public void setFieldShouldSupportSettingShortDescriptionLocator() {
		String expectedLocator = Random.jsoupLocator();
		
		boolean valid = parser.setField("short-description-locator", expectedLocator);
		
		assertThat(valid, is(true));
		assertThat(parser.getShortDescriptionLocator(), equalTo(expectedLocator));
	}
	
	@Test
	public void setFieldShouldSupportSettingImageUrlLocator() {
		String expectedLocator = Random.jsoupLocator();
		
		boolean valid = parser.setField("image-url-locator", expectedLocator);
		
		assertThat(valid, is(true));
		assertThat(parser.getImageUrlLocator(), equalTo(expectedLocator));
	}
	
	@Test
	public void setFieldShouldSupportSettingImageUrlAttribute() {
		String expectedAttributeName = Random.tagAttributeName();
		
		boolean valid = parser.setField("image-url-attribute", expectedAttributeName);
		
		assertThat(valid, is(false));
		assertThat(parser.getImageUrlAttribute(), equalTo(expectedAttributeName));
	}
	
	@Test
	public void setFieldShouldSupportSettingIssueDateLocator() {
		String expectedLocator = Random.jsoupLocator();
		
		boolean valid = parser.setField("issue-date-locator", expectedLocator);
		
		assertThat(valid, is(true));
		assertThat(parser.getIssueDateLocator(), equalTo(expectedLocator));
	}
	
	@Test
	public void setFieldShouldIgnoreUnknownField() {
		boolean valid = parser.setField("unsupported-locator", Random.jsoupLocator());
		
		assertThat(valid, is(false));
	}
	
	//
	// Tests for isFullyInitialized()
	//
	
	@Test
	public void isFullyInitializedMayBeOnlyWhenNameIsSet() {
		parser.setMatchedUrl(Random.url());
		parser.setCategoryLocator(Random.jsoupLocator());
		parser.setCountryLocator(Random.jsoupLocator());
		parser.setShortDescriptionLocator(Random.jsoupLocator());
		parser.setImageUrlLocator(Random.jsoupLocator());
		parser.setImageUrlAttribute(Random.tagAttributeName());
		parser.setIssueDateLocator(Random.jsoupLocator());
		
		// ensure that required field is null
		parser.setName(null);

		String msg = describe(parser) + " expected to be not fully initialized";
		assertThat(msg, parser.isFullyInitialized(), is(false));
	}
	
	@Test
	public void isFullyInitializedMayBeOnlyWhenMatchedUrlIsSet() {
		parser.setName(Random.name());
		parser.setCategoryLocator(Random.jsoupLocator());
		parser.setCountryLocator(Random.jsoupLocator());
		parser.setShortDescriptionLocator(Random.jsoupLocator());
		parser.setImageUrlLocator(Random.jsoupLocator());
		parser.setImageUrlAttribute(Random.tagAttributeName());
		parser.setIssueDateLocator(Random.jsoupLocator());

		// ensure that required field is null
		parser.setMatchedUrl(null);

		String msg = describe(parser) + " expected to be not fully initialized";
		assertThat(msg, parser.isFullyInitialized(), is(false));
	}
	
	@Test
	public void isFullyInitializedMayBeOnlyWhenOneOfLocatorIsSet() {
		parser.setName(Random.name());
		parser.setMatchedUrl(Random.url());
		parser.setImageUrlAttribute(Random.tagAttributeName());

		// ensure that required fields are null
		parser.setCategoryLocator(null);
		parser.setCountryLocator(null);
		parser.setShortDescriptionLocator(null);
		parser.setImageUrlLocator(null);
		parser.setIssueDateLocator(null);
		
		String msg = describe(parser) + " expected to be not fully initialized";
		assertThat(msg, parser.isFullyInitialized(), is(false));
	}
	
	@Test
	public void isFullyInitializedWhenAllMandatoryFieldsAreSet() {
		parser.setName(Random.name());
		parser.setMatchedUrl(Random.url());
		
		final int countOfFieldsWithLocator = 5;
		String[] locators = new String[countOfFieldsWithLocator];
		
		for (int i = 0; i < locators.length; i++) {
			int guaranteedSetPosition = between(0, locators.length - 1).integer();
			if (i == guaranteedSetPosition) {
				locators[i] = Random.jsoupLocator();
			} else {
				locators[i] = nullOr(Random.jsoupLocator());
			}
		}
		
		parser.setCategoryLocator(locators[0]);
		parser.setCountryLocator(locators[1]);
		parser.setShortDescriptionLocator(locators[2]);
		// CheckStyle: ignore MagicNumber for next 2 lines
		parser.setImageUrlLocator(locators[3]);
		parser.setIssueDateLocator(locators[4]);
		
		parser.setImageUrlAttribute(nullOr(Random.tagAttributeName()));
		
		String msg = describe(parser) + " expected to be fully initialized";
		assertThat(msg, parser.isFullyInitialized(), is(true));
	}
	
	//
	// Tests for canParse()
	//
	
	@Test
	public void canParseShouldRequireNonNullUrl() {
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("Site URL must be non-null");
		
		parser.canParse(null);
	}
	
	@Test
	public void canParseShouldRequireNonNullMatchedUrl() {
		parser.setMatchedUrl(null);
		
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("Matched URL must be set");
		
		parser.canParse(Random.url());
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
	
	private static String describe(SiteParser parser) {
		StringBuilder sb = new StringBuilder();
		sb.append("SiteParser[name=")
			.append(parser.getName())
			.append(", matchedUrl=")
			.append(parser.getMatchedUrl())
			.append(", categoryLocator=")
			.append(parser.getCountryLocator())
			.append(", countryLocator=")
			.append(parser.getCountryLocator())
			.append(", shortDescriptionLocator=")
			.append(parser.getShortDescriptionLocator())
			.append(", imageUrlLocator=")
			.append(parser.getImageUrlLocator())
			.append(", imageUrlAttribute=")
			.append(parser.getImageUrlAttribute())
			.append(", issueDateLocator=")
			.append(parser.getIssueDateLocator())
			.append(']');
		return sb.toString();
	}
	
	private static Element createDocumentFromText(String html) {
		Document doc = Jsoup.parseBodyFragment(html);
		return doc.body();
	}
	
	private static Element createEmptyDocument() {
		return createDocumentFromText("");
	}
	
}
