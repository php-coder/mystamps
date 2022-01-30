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
package ru.mystamps.web.feature.series.importing;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString(exclude = { "createdAt", "updatedAt" })
public class AddSeriesParsedDataDbDto {
	private Integer categoryId;
	private Integer countryId;
	private List<String> imageUrls;
	private Integer releaseDay;
	private Integer releaseMonth;
	private Integer releaseYear;
	private Integer quantity;
	private Boolean perforated;
	private String michelNumbers;
	private Date createdAt;
	private Date updatedAt;
	
	// they aren't useless
	@SuppressWarnings("PMD.UselessParentheses")
	public boolean hasAtLeastOneFieldFilled() {
		return categoryId != null
			|| countryId != null
			|| (imageUrls != null && !imageUrls.isEmpty())
			|| releaseYear != null
			|| quantity != null
			|| perforated != null
			|| michelNumbers != null;
	}
	
}
