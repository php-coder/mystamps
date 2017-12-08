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
		
		// @todo #685 SiteParserTest: introduce a method for generating random valid locator
		String anyValidLocator = "#id";
		
		parser.setField(nullOrBlank(), anyValidLocator);
	}
	
	@Test
	public void setFieldShouldRequireNonBlankValue() {
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("Field value must be non-blank");
		
		// @todo #685 SiteParserTest: introduce a method for generating random valid field name
		String anyValidFieldName = "name";
		
		parser.setField(anyValidFieldName, nullOrBlank());
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
		// @todo #685 SiteParserTest:
		//  introduce a method for generating random string of arbitrary length
		String expectedName = nullOr(between(1, 15).unicode());
		parser.setName(expectedName);
		
		assertThat(parser.toString(), equalTo(expectedName));
	}
	
}
