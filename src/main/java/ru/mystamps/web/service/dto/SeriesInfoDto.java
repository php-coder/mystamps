/*
 * Copyright (C) 2009-2014 Slava Semushin <slava.semushin@gmail.com>
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

import java.util.Date;

import lombok.Getter;

@Getter
public class SeriesInfoDto {
	private final Integer id;
	private final SelectEntryDto category;
	private final SelectEntryDto country;
	private final Date releasedAt;
	private final Integer quantity;
	private final Boolean perforated;
	
	public SeriesInfoDto( // NOCHECKSTYLE: ParameterNumber
			Integer id,
			Integer categoryId, String categoryName,
			Integer countryId, String countryName,
			Date releasedAt,
			Integer quantity,
			Boolean perforated) {
		this.id = id;
		this.category = new SelectEntryDto(categoryId, categoryName);
		this.country = new SelectEntryDto(countryId, countryName);
		this.releasedAt = releasedAt;
		this.quantity = quantity;
		this.perforated = perforated;
	}
	
}
