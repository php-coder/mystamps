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
package ru.mystamps.web.dao.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.Validate;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
class JdbcCatalogDao {
	
	private final NamedParameterJdbcTemplate jdbcTemplate;
	
	// Inserts all values with  multirow INSERT expression
	protected void add(Set<String> numbers, String addNumberSql) {
		Validate.validState(!numbers.isEmpty(), "Numbers must be non empty");
		Validate.validState(!"".equals(addNumberSql), "Query must be non empty");
		
		Map<String, Object> params = prepareNumberedParamsWithBaseName("code", numbers);
		String sql = prepareMultirowQueryWithSingleValue(addNumberSql, "code", numbers);
		
		jdbcTemplate.update(sql, new MapSqlParameterSource(params));
	}
	
	// CheckStyle: ignore LineLength for next 1 line
	protected void addToSeries(Integer seriesId, Set<String> numbers, String addNumbersToSeriesSql) {
		Validate.validState(seriesId != null, "Series id must be non null");
		Validate.validState(!numbers.isEmpty(), "Numbers must be non empty");
		Validate.validState(!"".equals(addNumbersToSeriesSql), "Query must be non empty");
		
		Map<String, Object> params = new HashMap<>();
		params.put("series_id", seriesId);
		params.put("numbers", numbers);
		
		jdbcTemplate.update(addNumbersToSeriesSql, params);
	}
	
	// func("code", [10, 20, 30]) -> { "code1":"10", "code2":"20", "code3":"30" }
	private static Map<String, Object> prepareNumberedParamsWithBaseName(
		String paramBaseName,
		Set<String> values) {
		
		Map<String, Object> params = new HashMap<>();
		int paramNum = 1;
		for (String value : values) {
			String key = paramBaseName + Integer.valueOf(paramNum);
			params.put(key, value);
			paramNum++;
		}
		return params;
	}
	
	// "INSERT INTO t(code) VALUES(:code1)" ->
	// "INSERT INTO t(code) VALUES(:code1),(:code2),(:code3)"
	private static String prepareMultirowQueryWithSingleValue(
		String sql,
		String paramBaseName,
		Set<String> values) {
		
		// don't modify query with single parameter
		if (values.size() == 1) { // NOPMD: AvoidLiteralsInIfCondition
			return sql;
		}
		
		StringBuilder sb = new StringBuilder(sql);
		
		// start from 2 to skip first parameter (it's already in SQL query)
		for (int paramNum = 2; paramNum <= values.size(); paramNum++) {
			sb.append(",(:").append(paramBaseName).append(paramNum).append(')');
		}
		
		return sb.toString();
	}
	
}
