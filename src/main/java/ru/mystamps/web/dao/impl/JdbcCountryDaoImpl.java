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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.dao.JdbcCountryDao;
import ru.mystamps.web.service.dto.LinkEntityDto;
import ru.mystamps.web.service.dto.SelectEntityDto;

@RequiredArgsConstructor
public class JdbcCountryDaoImpl implements JdbcCountryDao {
	
	private final NamedParameterJdbcTemplate jdbcTemplate;
	
	@Value("${country.count_all_countries}")
	private String countAllSql;
	
	@Value("${country.count_countries_by_name}")
	private String countByNameSql;
	
	@Value("${country.count_countries_by_name_ru}")
	private String countByNameRuSql;
	
	@Value("${country.count_countries_of_collection}")
	private String countCountriesOfCollectionSql;
	
	@Value("${country.count_stamps_by_countries}")
	private String countStampsByCountriesSql;
	
	@Value("${country.find_all_countries_names_with_ids}")
	private String findCountriesNamesWithIdsSql;
	
	@Value("${country.find_all_countries_names_with_slug}")
	private String findCountriesNamesWithSlugSql;
	
	@Override
	public long countAll() {
		return jdbcTemplate.queryForObject(
			countAllSql,
			Collections.<String, Object>emptyMap(),
			Long.class
		);
	}
	
	@Override
	public long countByName(String name) {
		return jdbcTemplate.queryForObject(
			countByNameSql,
			Collections.singletonMap("name", name),
			Long.class
		);
	}
	
	@Override
	public long countByNameRu(String name) {
		return jdbcTemplate.queryForObject(
			countByNameRuSql,
			Collections.singletonMap("name", name),
			Long.class
		);
	}
	
	@Override
	public long countCountriesOfCollection(Integer collectionId) {
		return jdbcTemplate.queryForObject(
			countCountriesOfCollectionSql,
			Collections.singletonMap("collection_id", collectionId),
			Long.class
		);
	}
	
	@Override
	public Map<String, Integer> getStatisticsOf(Integer collectionId, String lang) {
		Map<String, Object> params = new HashMap<>();
		params.put("collection_id", collectionId);
		params.put("lang", lang);
		
		// TODO: find a better way of extracting results
		List<Pair<String, Integer>> rawResult = jdbcTemplate.query(
			countStampsByCountriesSql,
			params,
			RowMappers::forNameAndCounter
		);
		
		Map<String, Integer> result = new HashMap<>(rawResult.size(), 1.0f);
		for (Pair<String, Integer> pair : rawResult) {
			result.put(pair.getFirst(), pair.getSecond());
		}
		
		return result;
	}
	
	@Override
	public Iterable<SelectEntityDto> findAllAsSelectEntities(String lang) {
		return jdbcTemplate.query(
			findCountriesNamesWithIdsSql,
			Collections.singletonMap("lang", lang),
			RowMappers::forSelectEntityDto
		);
	}
	
	@Override
	public Iterable<LinkEntityDto> findAllAsLinkEntities(String lang) {
		return jdbcTemplate.query(
			findCountriesNamesWithSlugSql,
			Collections.singletonMap("lang", lang),
			RowMappers::forLinkEntityDto
		);
	}
	
}
