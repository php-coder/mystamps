/*
 * Copyright (C) 2009-2025 Slava Semushin <slava.semushin@gmail.com>
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

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.Validate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.EMPTY;

@RequiredArgsConstructor
public class JdbcStampsCatalogDao implements StampsCatalogDao {
	
	private final NamedParameterJdbcTemplate jdbcTemplate;
	private final String addCatalogNumberSql;
	private final String addCatalogNumbersToSeriesSql;
	private final String findBySeriesIdSql;
	private final String findSeriesIdsByNumberSql;
	
	@Override
	public List<String> add(Set<String> catalogNumbers) {
		Validate.validState(!EMPTY.equals(addCatalogNumberSql), "Query must be non empty");
		
		List<String> inserted = new ArrayList<>();
		for (String number : catalogNumbers) {
			int affected = jdbcTemplate.update(
				addCatalogNumberSql,
				Collections.singletonMap("code", number)
			);
			if (affected > 0) {
				inserted.add(number);
			}
		}
		
		return inserted;
	}
	
	@Override
	public void addToSeries(Integer seriesId, Set<String> catalogNumbers) {
		Validate.validState(seriesId != null, "Series id must be non null");
		Validate.validState(!catalogNumbers.isEmpty(), "Catalog numbers must be non empty");
		Validate.validState(!EMPTY.equals(addCatalogNumbersToSeriesSql), "Query must be non empty");
		
		Map<String, Object> params = new HashMap<>();
		params.put("series_id", seriesId);
		params.put("numbers", catalogNumbers);
		
		jdbcTemplate.update(addCatalogNumbersToSeriesSql, params);
	}
	
	@Override
	public List<String> findBySeriesId(Integer seriesId) {
		return jdbcTemplate.queryForList(
			findBySeriesIdSql,
			Collections.singletonMap("series_id", seriesId),
			String.class
		);
	}
	
	@Override
	public List<Integer> findSeriesIdsByNumber(String catalogNumber) {
		return jdbcTemplate.queryForList(
			findSeriesIdsByNumberSql,
			Collections.singletonMap("number", catalogNumber),
			Integer.class
		);
	}
	
}
