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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Getters/setters/no-arg constructor are being used in unit tests
@Getter(AccessLevel.PROTECTED)
@Setter(AccessLevel.PROTECTED)
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@SuppressWarnings({
	"PMD.GodClass",
	"PMD.TooManyMethods",
	// the fields are effectively final but I keep them non-final to simplify testing
	"PMD.ImmutableField"
})
public class JsoupSiteParser implements SiteParser {
	private static final Logger LOG = LoggerFactory.getLogger(JsoupSiteParser.class);
	
	// When you add a new field, don't forget to also update SiteParserConfiguration.
	private String name;
	private String matchedUrl;
	private String categoryLocator;
	private String countryLocator;
	private String shortDescriptionLocator;
	private String imageUrlLocator;
	private String imageUrlAttribute;
	private String issueDateLocator;
	private String sellerLocator;
	private String priceLocator;
	private String currencyValue;
	
	// @todo #975 SiteParserServiceImpl: add unit tests for constructor
	public JsoupSiteParser(SiteParserConfiguration cfg) {
		name                    = cfg.getName();
		matchedUrl              = cfg.getMatchedUrl();
		categoryLocator         = cfg.getCategoryLocator();
		countryLocator          = cfg.getCountryLocator();
		shortDescriptionLocator = cfg.getShortDescriptionLocator();
		imageUrlLocator         = cfg.getImageUrlLocator();
		imageUrlAttribute       = cfg.getImageUrlAttribute();
		issueDateLocator        = cfg.getIssueDateLocator();
		sellerLocator           = cfg.getSellerLocator();
		priceLocator            = cfg.getPriceLocator();
		currencyValue           = cfg.getCurrencyValue();
	}
	
	/**
	 * Parse HTML document to get info about series.
	 *
	 * @return info about a series from the document
	 */
	@Override
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
		info.setQuantity(extractQuantity(body));
		info.setPerforated(extractPerforated(body));
		info.setMichelNumbers(extractMichelNumbers(body));
		info.setSellerName(extractSellerName(body));
		info.setSellerUrl(extractSellerUrl(body));
		info.setPrice(extractPrice(body));
		info.setCurrency(extractCurrency(body));
		
		return info;
	}

	@Override
	public String toString() {
		return name;
	}
	
	protected String extractCategory(Element body) {
		String locator = ObjectUtils.firstNonNull(categoryLocator, shortDescriptionLocator);
		
		String category = getTextOfTheFirstElement(body, locator);
		if (category == null) {
			return null;
		}
		
		LOG.debug("Extracted category: '{}'", category);
		return category;
	}
	
	protected String extractCountry(Element body) {
		String locator = ObjectUtils.firstNonNull(countryLocator, shortDescriptionLocator);
		
		String country = getTextOfTheFirstElement(body, locator);
		if (country == null) {
			return null;
		}
		
		LOG.debug("Extracted country: '{}'", country);
		return country;
	}
	
	protected String extractImageUrl(Element body) {
		Element elem = getFirstElement(body, imageUrlLocator);
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
		
		String date = getTextOfTheFirstElement(body, locator);
		if (date == null) {
			return null;
		}
		
		LOG.debug("Extracted issue date: '{}'", date);
		return date;
	}
	
	protected String extractQuantity(Element body) {
		String quantity = getTextOfTheFirstElement(body, shortDescriptionLocator);
		if (quantity == null) {
			return null;
		}
		
		LOG.debug("Extracted quantity: '{}'", quantity);
		return quantity;
	}
	
	protected String extractPerforated(Element body) {
		String perforated = getTextOfTheFirstElement(body, shortDescriptionLocator);
		if (perforated == null) {
			return null;
		}

		LOG.debug("Extracted perforated flag: '{}'", perforated);
		return perforated;
	}
	
	// @todo #694 Support for a separate locator for a field with michel numbers
	protected String extractMichelNumbers(Element body) {
		String description = getTextOfTheFirstElement(body, shortDescriptionLocator);
		if (description == null) {
			return null;
		}
		
		LOG.debug("Extracted michel numbers: '{}'", description);
		return description;
		
	}
	
	protected String extractSellerName(Element body) {
		String sellerName = getTextOfTheFirstElement(body, sellerLocator);
		if (sellerName == null) {
			return null;
		}
		
		LOG.debug("Extracted seller name: '{}'", sellerName);
		return sellerName;
	}
	
	protected String extractSellerUrl(Element body) {
		Element elem = getFirstElement(body, sellerLocator);
		if (elem == null) {
			return null;
		}
		
		String url = elem.absUrl("href");
		LOG.debug("Extracted seller url: '{}'", url);
		return url;
	}
	
	protected String extractPrice(Element body) {
		Element elem = getFirstElement(body, priceLocator);
		if (elem == null) {
			return null;
		}
		
		String price = elem.ownText();
		LOG.debug("Extracted price: '{}'", price);
		return price;
	}
	
	protected String extractCurrency(Element body) {
		if (currencyValue == null) {
			return null;
		}
		
		LOG.debug("Extracted currency: '{}'", currencyValue);
		return currencyValue;
	}
	
	private static Element getFirstElement(Element body, String locator) {
		if (locator == null) {
			return null;
		}
		
		return body.selectFirst(locator);
	}
	
	private static String getTextOfTheFirstElement(Element body, String locator) {
		Element elem = getFirstElement(body, locator);
		if (elem == null) {
			return null;
		}
		
		return elem.text();
	}
	
}
