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
package ru.mystamps.web.feature.series.sale;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class JdbcSeriesSalesDao implements SeriesSalesDao {
	
	private final NamedParameterJdbcTemplate jdbcTemplate;
	
	@Value("${series_sales.add}")
	private String addSeriesSaleSql;
	
	@Override
	public void add(AddSeriesSalesDbDto sale) {
		Map<String, Object> params = new HashMap<>();
		params.put("series_id", sale.getSeriesId());
		params.put("date", sale.getDate());
		params.put("seller_id", sale.getSellerId());
		params.put("url", sale.getUrl());
		params.put("price", sale.getPrice());
		params.put("currency", sale.getCurrency());
		params.put("alt_price", sale.getAltPrice());
		params.put("alt_currency", sale.getAltCurrency());
		params.put("buyer_id", sale.getBuyerId());
		params.put("created_at", sale.getCreatedAt());
		params.put("created_by", sale.getCreatedBy());
		
		int affected = jdbcTemplate.update(addSeriesSaleSql, params);
		
		Validate.validState(
			affected == 1,
			"Unexpected number of affected rows after adding series sales: %d",
			affected
		);
	}
	
}
