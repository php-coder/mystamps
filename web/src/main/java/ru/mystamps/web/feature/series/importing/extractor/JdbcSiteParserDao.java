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
package ru.mystamps.web.feature.series.importing.extractor;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JdbcSiteParserDao implements SiteParserDao {
	
	private static final ResultSetExtractor<Map<String, String>> PARAMS_EXTRACTOR =
		new MapResultSetExtractor("name", "value");
	
	private final NamedParameterJdbcTemplate jdbcTemplate;
	
	@Value("${site_parser.find_like_matched_url}")
	private String findParserIdByMatchedUrlSql;
	
	@Value("${site_parser.find_names}")
	private String findParserNamesSql;
	
	@SuppressWarnings("PMD.LongVariable")
	@Value("${site_parser_param.find_all_with_parser_name}")
	private String findParametersWithParserNameSql;
	
	@Override
	public Integer findParserIdForUrl(String url) {
		try {
			return jdbcTemplate.queryForObject(
				findParserIdByMatchedUrlSql,
				Collections.singletonMap("url", url),
				Integer.class
			);
		} catch (EmptyResultDataAccessException ignored) {
			return null;
		}
	}
	
	@Override
	public List<String> findParserNames() {
		return jdbcTemplate.queryForList(
			findParserNamesSql,
			Collections.emptyMap(),
			String.class
		);
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
