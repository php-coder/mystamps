/*
 * Copyright (C) 2009-2020 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.feature.series;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

@Getter
@RequiredArgsConstructor
public class CatalogInfoDto {
	private final List<String> numbers;
	private final BigDecimal price;
	
	// used to emulate <fmt:formatNumber pattern="###.##" />
	public String getFormattedPrice() {
		NumberFormat formatter = NumberFormat.getInstance(Locale.ENGLISH);
		if (formatter instanceof DecimalFormat) {
			((DecimalFormat)formatter).applyPattern("###.##");
		}
		return formatter.format(price.doubleValue());
	}
	
}
