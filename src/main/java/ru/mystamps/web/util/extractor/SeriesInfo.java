/*
 * Copyright (C) 2009-2018 Slava Semushin <slava.semushin@gmail.com>
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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Representation of a series info.
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class SeriesInfo {
	private String categoryName;
	private String countryName;
	private String imageUrl;
	private String issueDate;
	private String quantity;
	private String perforated;
	private String michelNumbers;
	private String sellerName;
	private String sellerUrl;
	private String price;
	private String currency;
	
	/**
	 * Check whether any info about a series is available.
	 */
	public boolean isEmpty() {
		return categoryName == null
			&& countryName == null
			&& imageUrl == null
			&& issueDate == null
			&& quantity == null
			&& perforated == null
			&& michelNumbers == null
			&& sellerName == null
			&& price == null;
	}
	
}
