/*
 * Copyright (C) 2009-2019 Slava Semushin <slava.semushin@gmail.com>
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

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import ru.mystamps.web.support.jdbc.RowMappers;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class JdbcSeriesSalesImportDao implements SeriesSalesImportDao {
	
	private final NamedParameterJdbcTemplate jdbcTemplate;
	
	@Value("${series_import_requests.add_series_sales_parsed_data}")
	private String addParsedDataSql;
	
	@Value("${series_import_requests.find_series_sale_parsed_data_by_request_id}")
	private String findParsedDataSql;
	
	@Override
	public void addParsedData(Integer requestId, SeriesSalesParsedDataDbDto data) {
		Map<String, Object> params = new HashMap<>();
		params.put("request_id", requestId);
		params.put("seller_id", data.getSellerId());
		params.put("seller_group_id", data.getSellerGroupId());
		params.put("seller_name", data.getSellerName());
		params.put("seller_url", data.getSellerUrl());
		params.put("price", data.getPrice());
		params.put("currency", data.getCurrency());
		params.put("created_at", data.getCreatedAt());
		params.put("updated_at", data.getUpdatedAt());
		
		KeyHolder holder = new GeneratedKeyHolder();
		
		int affected = jdbcTemplate.update(
			addParsedDataSql,
			new MapSqlParameterSource(params),
			holder
		);
		
		Validate.validState(
			affected == 1,
			"Unexpected number of affected rows after adding parsed data to request #%d: %d",
			requestId,
			affected
		);
	}
	
	@Override
	public SeriesSaleParsedDataDto findParsedDataByRequestId(Integer requestId) {
		try {
			return jdbcTemplate.queryForObject(
				findParsedDataSql,
				Collections.singletonMap("request_id", requestId),
				RowMappers::forSeriesSaleParsedDataDto
			);
			
		} catch (EmptyResultDataAccessException ignored) {
			return null;
		}
	}
	
}
