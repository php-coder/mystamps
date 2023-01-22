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
package ru.mystamps.web.feature.series.sale;

import org.apache.commons.lang3.Validate;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JdbcSeriesSalesDao implements SeriesSalesDao {
	
	private final NamedParameterJdbcTemplate jdbcTemplate;
	private final String addSeriesSaleSql;
	private final String findSeriesSalesBySeriesIdSql;
	
	@SuppressWarnings("checkstyle:linelength")
	public JdbcSeriesSalesDao(Environment env, NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate                 = jdbcTemplate;
		this.addSeriesSaleSql             = env.getRequiredProperty("series_sales.add");
		this.findSeriesSalesBySeriesIdSql = env.getRequiredProperty("series_sales.find_sales_by_series_id");
	}
	
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
		params.put("condition", sale.getCondition());
		params.put("created_at", sale.getCreatedAt());
		params.put("created_by", sale.getCreatedBy());
		
		int affected = jdbcTemplate.update(addSeriesSaleSql, params);
		
		Validate.validState(
			affected == 1,
			"Unexpected number of affected rows after adding series sales: %d",
			affected
		);
	}
	
	/**
	 * @author Sergey Chechenev
	 */
	@Override
	public List<SeriesSaleDto> findSeriesSales(Integer seriesId) {
		return jdbcTemplate.query(
			findSeriesSalesBySeriesIdSql,
			Collections.singletonMap("series_id", seriesId),
			RowMappers::forSeriesSaleDto
		);
	}
	
}
