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

import ru.mystamps.web.dao.JdbcCategoryDao;
import ru.mystamps.web.service.dto.LinkEntityDto;
import ru.mystamps.web.service.dto.SelectEntityDto;

@RequiredArgsConstructor
public class JdbcCategoryDaoImpl implements JdbcCategoryDao {
	
	private final NamedParameterJdbcTemplate jdbcTemplate;
	
	@Value("${category.count_all_categories}")
	private String countAllSql;
	
	@Value("${category.count_categories_by_name}")
	private String countByNameSql;
	
	@Value("${category.count_categories_by_name_ru}")
	private String countByNameRuSql;
	
	@Value("${category.count_categories_of_collection}")
	private String countCategoriesOfCollectionSql;
	
	@Value("${category.count_stamps_by_categories}")
	private String countStampsByCategoriesSql;
	
	@Value("${category.find_all_categories_names_with_ids}")
	private String findCategoriesNamesWithIdsSql;
	
	@Value("${category.find_all_categories_names_with_slug}")
	private String findCategoriesNamesWithSlugSql;
	
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
	public long countCategoriesOfCollection(Integer collectionId) {
		return jdbcTemplate.queryForObject(
			countCategoriesOfCollectionSql,
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
			countStampsByCategoriesSql,
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
	public Iterable<SelectEntityDto> findAllAsSelectEntries(String lang) {
		return jdbcTemplate.query(
			findCategoriesNamesWithIdsSql,
			Collections.singletonMap("lang", lang),
			RowMappers::forSelectEntityDto
		);
	}
	
	@Override
	public Iterable<LinkEntityDto> findAllAsLinkEntities(String lang) {
		return jdbcTemplate.query(
			findCategoriesNamesWithSlugSql,
			Collections.singletonMap("lang", lang),
			RowMappers::forLinkEntityDto
		);
	}
	
}
