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
package ru.mystamps.web.controller.dto;

import java.math.BigDecimal;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import ru.mystamps.web.dao.dto.Currency;
import ru.mystamps.web.service.dto.AddToCollectionDto;

import static ru.mystamps.web.validation.ValidationRules.MIN_STAMPS_IN_SERIES;

// @todo #477 Add to collection: integration test for invisible quantity for a series with 1 stamp
// @todo #477 Add to collection: series quantity should be specified by default
// @todo #477 Add to collection: add integration test for custom number of stamps
// @todo #663 Add to collection: add integration test for specifying a price
// @todo #663 Add a page with a list of series, their prices and total cost
@Getter
@Setter
public class AddToCollectionForm implements AddToCollectionDto {
	
	// @todo #477 /series/{id}(quantity): add validation for max value
	@NotNull
	@Min(MIN_STAMPS_IN_SERIES)
	private Integer numberOfStamps;
	
	// @todo #663 /series/{id}(price): must be greater than zero
	private BigDecimal price;
	
	// @todo #663 /series/{id}(currency): must be required when price is specified
	private Currency currency;
}
