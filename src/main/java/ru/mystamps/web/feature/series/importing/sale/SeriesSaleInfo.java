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
package ru.mystamps.web.feature.series.importing.sale;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Representation of series sale info.
 */
@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class SeriesSaleInfo {
	private final String sellerName;
	private final String sellerUrl;
	private final String price;
	private final String currency;
	
	/**
	 * Check whether info about a series sale is available.
	 */
	public boolean isEmpty() {
		// The following fields aren't taken into account because:
		// - sellerUrl: SeriesInfoExtractorService might deduce it based on a page URL
		// - currency: SeriesInfoExtractorService might use a default value
		return sellerName == null && price == null;
	}
	
}
