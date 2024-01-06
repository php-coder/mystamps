/*
 * Copyright (C) 2009-2024 Slava Semushin <slava.semushin@gmail.com>
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
import org.springframework.format.annotation.NumberFormat;
import ru.mystamps.web.common.Currency;
import ru.mystamps.web.feature.series.sale.AddSeriesSalesDto;
import ru.mystamps.web.feature.series.sale.SeriesCondition;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

// @todo #695 /series/import/request/{id}: seller's name and url are required when sellerId is empty
// @todo #695 /series/import/request/{id}(seriesSale):
//  add integration test for validation of required fields
@Getter
@Setter
public class ImportSeriesSalesForm implements AddSeriesSalesDto {
	
	private Integer sellerId;
	
	@NotNull
	@NumberFormat(pattern = "###.##")
	private BigDecimal price;
	
	@NotNull
	private Currency currency;
	
	// CheckStyle: ignore LineLength for next 1 line
	// @todo #1230 /series/import/request/{id}: validate that both alt price/currency are present or absent
	private BigDecimal altPrice;
	private Currency altCurrency;
	
	// @todo #1326 Series import: add integration test for series condition
	private SeriesCondition condition;
	
	// We don't expose these fields to a form because we know already what
	// values should be. Even if user will try to provide its own values, it's not
	// a problem as we always rewrite them in the controller.
	private Date date;
	private String url;
	
	//
	// The method below is required for AddSeriesSalesDto interface.
	// It is no-op method because we don't support all values during series import.
	//
	
	@Override
	public Integer getBuyerId() {
		return null;
	}
	
}
