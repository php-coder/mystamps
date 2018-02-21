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

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

// Getters/setters are being used in unit test
@Getter(AccessLevel.PROTECTED)
@Setter(AccessLevel.PROTECTED)
public class SiteParser {
	private static final Logger LOG = LoggerFactory.getLogger(SiteParser.class);
	
	// When you're adding a new field don't forget to also update:
	// - SiteParser.setField()
	// - SiteParser.isFullyInitialized() (optionally)
	// - SiteParserTest.describe()
	private String name;
	private String matchedUrl;
	private String categoryLocator;
	private String countryLocator;
	private String shortDescriptionLocator;
	private String imageUrlLocator;
	private String imageUrlAttribute;
	private String issueDateLocator;
	
	public boolean setField(String name, String value) {
		Validate.validState(StringUtils.isNotBlank(name), "Field name must be non-blank");
		Validate.validState(StringUtils.isNotBlank(value), "Field value must be non-blank");
		
		boolean valid = false;
		
		switch (name) {
			
			case "name":
				setName(value);
				valid = true;
				break;
			
			case "matched-url":
				setMatchedUrl(value);
				valid = true;
				break;
			
			case "category-locator":
				setCategoryLocator(value);
				valid = true;
				break;
			
			case "country-locator":
				setCountryLocator(value);
				valid = true;
				break;
			
			case "short-description-locator":
				setShortDescriptionLocator(value);
				valid = true;
				break;
			
			case "image-url-locator":
				setImageUrlLocator(value);
				valid = true;
				break;
			
			case "image-url-attribute":
				setImageUrlAttribute(value);
				break;
			
			case "issue-date-locator":
				setIssueDateLocator(value);
				valid = true;
				break;
			
			default:
				break;
		}
		
		return valid;
	}
	
	public boolean isFullyInitialized() {
		return name != null
			&& matchedUrl != null
			&& (
				categoryLocator != null
				|| countryLocator != null
				|| shortDescriptionLocator != null
				|| imageUrlLocator != null
				|| issueDateLocator != null
			);
	}
	
	public boolean canParse(String url) {
		Validate.validState(url != null, "Site URL must be non-null");
		Validate.validState(matchedUrl != null, "Matched URL must be set");
		
		return url.startsWith(matchedUrl);
	}
	
	/**
	 * Parse HTML document to get info about series.
	 *
	 * @return info about a series from the document
	 */
	public SeriesInfo parse(String htmlPage) {
		Validate.isTrue(StringUtils.isNotBlank(htmlPage), "Page content must be non-blank");
		
		String baseUri = matchedUrl;
		Document doc = Jsoup.parse(htmlPage, baseUri);
		Element body = doc.body();
		
		SeriesInfo info = new SeriesInfo();
		
		info.setCategoryName(extractCategory(body));
		info.setCountryName(extractCountry(body));
		info.setImageUrl(extractImageUrl(body));
		info.setIssueDate(extractIssueDate(body));
		
		return info;
	}

	@Override
	public String toString() {
		return name;
	}
	
	protected String extractCategory(Element body) {
		String locator = ObjectUtils.firstNonNull(categoryLocator, shortDescriptionLocator);
		if (locator == null) {
			return null;
		}
		
		Element elem = body.selectFirst(locator);
		if (elem == null) {
			return null;
		}
		
		String category = elem.text();
		LOG.debug("Extracted category: '{}'", category);
		return category;
	}
	
	protected String extractCountry(Element body) {
		String locator = ObjectUtils.firstNonNull(countryLocator, shortDescriptionLocator);
		if (locator == null) {
			return null;
		}
		
		Element elem = body.selectFirst(locator);
		if (elem == null) {
			return null;
		}
		
		String country = elem.text();
		LOG.debug("Extracted country: '{}'", country);
		return country;
	}
	
	protected String extractImageUrl(Element body) {
		if (imageUrlLocator == null) {
			return null;
		}
		
		Element elem = body.selectFirst(imageUrlLocator);
		if (elem == null) {
			return null;
		}
		
		String attrName = ObjectUtils.firstNonNull(imageUrlAttribute, "href");
		String url = elem.absUrl(attrName);
		LOG.debug("Extracted image url: '{}'", url);
		return StringUtils.trimToNull(url);
	}
	
	protected String extractIssueDate(Element body) {
		String locator = ObjectUtils.firstNonNull(issueDateLocator, shortDescriptionLocator);
		if (locator == null) {
			return null;
		}
		
		Element elem = body.selectFirst(locator);
		if (elem == null) {
			return null;
		}
		
		String date = elem.text();
		LOG.debug("Extracted issue date: '{}'", date);
		return date;
	}
	
}
