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
package ru.mystamps.web.feature.series.importing.extractor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.Validate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JdbcSiteParserDao implements SiteParserDao {
	
	private static final ResultSetExtractor<Map<String, String>> PARAMS_EXTRACTOR =
		new MapResultSetExtractor("name", "value");
	
	private final NamedParameterJdbcTemplate jdbcTemplate;
	
	@Value("${site_parser.create}")
	private String addParserSql;
	
	@Value("${site_parser_param.create}")
	private String addParserParameterSql;
	
	@Value("${site_parser.find_like_matched_url}")
	private String findParserIdByMatchedUrl;
	
	@SuppressWarnings("PMD.LongVariable")
	@Value("${site_parser_param.find_all_with_parser_name}")
	private String findParametersWithParserNameSql;
	
	@Override
	public Integer addParser(String name) {
		KeyHolder holder = new GeneratedKeyHolder();
		
		int affected = jdbcTemplate.update(
			addParserSql,
			new MapSqlParameterSource("name", name),
			holder
		);
		
		Validate.validState(
			affected == 1,
			"Unexpected number of affected rows after creation of site parser: %d",
			affected
		);
		
		return holder.getKey().intValue();
	}

	@Override
	public void addParserParameter(AddParserParameterDbDto param) {
		Map<String, Object> params = new HashMap<>();
		params.put("parser_id", param.getParserId());
		params.put("name", param.getName());
		params.put("value", param.getValue());
		
		int affected = jdbcTemplate.update(addParserParameterSql, params);
		
		Validate.validState(
			affected == 1,
			"Unexpected number of affected rows after adding parser parameter: %d",
			affected
		);
	}
	
	@Override
	public Integer findParserIdForUrl(String url) {
		try {
			return jdbcTemplate.queryForObject(
				findParserIdByMatchedUrl,
				Collections.singletonMap("url", url),
				Integer.class
			);
		} catch (EmptyResultDataAccessException ignored) {
			return null;
		}
	}
	
	@Override
	public SiteParserConfiguration findConfigurationForParser(Integer parserId) {
		Map<String, String> params = jdbcTemplate.query(
			findParametersWithParserNameSql,
			Collections.singletonMap("parser_id", parserId),
			PARAMS_EXTRACTOR
		);
		
		return new SiteParserConfiguration(params);
	}
	
}
