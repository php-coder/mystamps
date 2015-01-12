/*
 * Copyright (C) 2009-2015 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.service.dto;

import lombok.Getter;

@Getter
@SuppressWarnings("PMD.SingularField")
public class SeriesInfoDto {
	private final Integer id;
	private final LinkEntityDto category;
	private final LinkEntityDto country;
	private final Integer releaseDay;
	private final Integer releaseMonth;
	private final Integer releaseYear;
	private final Integer quantity;
	private final Boolean perforated;
	
	@SuppressWarnings({"checkstyle:parameternumber", "PMD.ExcessiveParameterList"})
	public SeriesInfoDto(
			Integer id,
			Integer categoryId, String categorySlug, String categoryName,
			Integer countryId, String countrySlug, String countryName,
			Integer releaseDay, Integer releaseMonth, Integer releaseYear,
			Integer quantity,
			Boolean perforated) {
		this.id = id;
		this.category = new LinkEntityDto(categoryId, categorySlug, categoryName);
		this.country = new LinkEntityDto(countryId, countrySlug, countryName);
		this.releaseDay = releaseDay;
		this.releaseMonth = releaseMonth;
		this.releaseYear = releaseYear;
		this.quantity = quantity;
		this.perforated = perforated;
	}
	
}
