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
package ru.mystamps.web.feature.series.importing.sale;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@ToString(exclude = { "createdAt", "updatedAt" })
public class SeriesSalesParsedDataDbDto {
	private Integer sellerId;
	private Integer sellerGroupId;
	private String sellerName;
	private String sellerUrl;
	private BigDecimal price;
	private String currency;
	private BigDecimal altPrice;
	private String altCurrency;
	private String condition;
	private Date createdAt;
	private Date updatedAt;
	
	// they aren't useless
	@SuppressWarnings("PMD.UselessParentheses")
	public boolean hasAtLeastOneFieldFilled() {
		return sellerId != null
			|| (sellerName != null && sellerUrl != null)
			|| (price != null && currency != null);
	}
	
}
