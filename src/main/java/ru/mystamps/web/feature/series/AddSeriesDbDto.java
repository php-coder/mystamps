/*
 * Copyright (C) 2009-2023 Slava Semushin <slava.semushin@gmail.com>
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
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@ToString(exclude = { "createdAt", "createdBy", "updatedAt", "updatedBy" })
@SuppressWarnings("PMD.TooManyFields")
public class AddSeriesDbDto {
	private Integer categoryId;
	private Integer countryId;
	
	private Integer quantity;
	private Boolean perforated;
	
	private BigDecimal michelPrice;
	private BigDecimal scottPrice;
	private BigDecimal yvertPrice;
	private BigDecimal gibbonsPrice;
	private BigDecimal solovyovPrice;
	private BigDecimal zagorskiPrice;
	
	private Integer releaseDay;
	private Integer releaseMonth;
	private Integer releaseYear;
	
	private Date createdAt;
	private Integer createdBy;
	private Date updatedAt;
	private Integer updatedBy;
}
