/*
 * Copyright (C) 2009-2024 Slava Semushin <slava.semushin@gmail.com>
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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class SiteParserConfiguration {
	private final String name;
	private final String matchedUrl;
	
	private String categoryLocator;
	private String countryLocator;
	private String shortDescriptionLocator;
	private String imageUrlLocator;
	private String imageUrlAttribute;
	private String issueDateLocator;
	private String sellerLocator;
	private String sellerUrlLocator;
	private String priceLocator;
	private String currencyLocator;
	private String currencyValue;
	private String altPriceLocator;
	private String altCurrencyLocator;
	
	/* default */ SiteParserConfiguration(Map<String, String> params) {
		name = params.get("name");
		matchedUrl = params.get("matched-url");
		
		categoryLocator         = params.get("category-locator");
		countryLocator          = params.get("country-locator");
		shortDescriptionLocator = params.get("short-description-locator");
		imageUrlLocator         = params.get("image-url-locator");
		imageUrlAttribute       = params.get("image-url-attribute");
		issueDateLocator        = params.get("issue-date-locator");
		sellerLocator           = params.get("seller-locator");
		// @todo #1281 Add integration test for import with seller-url-locator
		sellerUrlLocator        = params.get("seller-url-locator");
		priceLocator            = params.get("price-locator");
		// @todo #979 Add integration test for import of series with currency-locator
		currencyLocator         = params.get("currency-locator");
		currencyValue           = params.get("currency-value");
		altPriceLocator         = params.get("alt-price-locator");
		altCurrencyLocator      = params.get("alt-currency-locator");
	}
	
}
