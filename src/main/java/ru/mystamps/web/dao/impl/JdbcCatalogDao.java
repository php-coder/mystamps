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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.Validate;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
class JdbcCatalogDao {
	
	private final NamedParameterJdbcTemplate jdbcTemplate;
	
	protected List<String> add(Set<String> numbers, String addNumberSql) {
		Validate.validState(!"".equals(addNumberSql), "Query must be non empty");
		
		List<String> inserted = new ArrayList<>();
		for (String number : numbers) {
			int affected = jdbcTemplate.update(
				addNumberSql,
				Collections.singletonMap("code", number)
			);
			if (affected > 0) {
				inserted.add(number);
			}
		}
		
		return inserted;
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
	
}
