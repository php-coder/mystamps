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

import java.util.Map;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
// optional fields can't be made final because they have setters
@SuppressWarnings("PMD.ImmutableField")
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
	private String priceLocator;
	private String currencyValue;
	
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
		priceLocator            = params.get("price-locator");
		currencyValue           = params.get("currency-value");
	}
	
}
