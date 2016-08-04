/*
 * Copyright (C) 2009-2016 Slava Semushin <slava.semushin@gmail.com>
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.dao.CategoryDao;
import ru.mystamps.web.dao.dto.AddCategoryDbDto;
import ru.mystamps.web.dao.dto.LinkEntityDto;
import ru.mystamps.web.dao.dto.SelectEntityDto;

@RequiredArgsConstructor
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class JdbcCategoryDao implements CategoryDao {
	
	private final NamedParameterJdbcTemplate jdbcTemplate;
	
	@Value("${category.create}")
	private String addCategorySql;
	
	@Value("${category.count_all_categories}")
	private String countAllSql;
	
	@Value("${category.count_categories_by_name}")
	private String countByNameSql;
	
	@Value("${category.count_categories_by_name_ru}")
	private String countByNameRuSql;
	
	@Value("${category.count_categories_of_collection}")
	private String countCategoriesOfCollectionSql;
	
	@Value("${category.count_categories_added_since}")
	private String countCategoriesAddedSinceSql;
	
	@Value("${category.count_stamps_by_categories}")
	private String countStampsByCategoriesSql;
	
	@Value("${category.find_all_categories_names_with_ids}")
	private String findCategoriesNamesWithIdsSql;
	
	@Value("${category.find_all_categories_names_with_slug}")
	private String findCategoriesNamesWithSlugSql;
	
	@Value("${category.find_category_link_info_by_id}")
	private String findCategoryLinkEntityByIdSql;
	
	@Override
	public Integer add(AddCategoryDbDto category) {
		Map<String, Object> params = new HashMap<>();
		params.put("name", category.getName());
		params.put("name_ru", category.getNameRu());
		params.put("slug", category.getSlug());
		params.put("created_at", category.getCreatedAt());
		params.put("created_by", category.getCreatedBy());
		params.put("updated_at", category.getUpdatedAt());
		params.put("updated_by", category.getUpdatedBy());
		
		KeyHolder holder = new GeneratedKeyHolder();
		
		int affected = jdbcTemplate.update(
			addCategorySql,
			new MapSqlParameterSource(params),
			holder
		);
		
		Validate.validState(
			affected == 1,
			"Unexpected number of affected rows after creation of category: %d",
			affected
		);
		
		return Integer.valueOf(holder.getKey().intValue());
	}
	
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
	public long countAddedSince(Date date) {
		return jdbcTemplate.queryForObject(
			countCategoriesAddedSinceSql,
			Collections.singletonMap("date", date),
			Long.class
		);
	}
	
	@Override
	public List<Object[]> getStatisticsOf(Integer collectionId, String lang) {
		Map<String, Object> params = new HashMap<>();
		params.put("collection_id", collectionId);
		params.put("lang", lang);
		
		return jdbcTemplate.query(
			countStampsByCategoriesSql,
			params,
			RowMappers::forNameAndCounter
		);
	}
	
	@Override
	public Iterable<SelectEntityDto> findAllAsSelectEntities(String lang) {
		return jdbcTemplate.query(
			findCategoriesNamesWithIdsSql,
			Collections.singletonMap("lang", lang),
			RowMappers::forSelectEntityDto
		);
	}
	
	@Override
	public List<LinkEntityDto> findAllAsLinkEntities(String lang) {
		return jdbcTemplate.query(
			findCategoriesNamesWithSlugSql,
			Collections.singletonMap("lang", lang),
			RowMappers::forLinkEntityDto
		);
	}
	
	@Override
	public LinkEntityDto findOneAsLinkEntity(Integer categoryId, String lang) {
		Map<String, Object> params = new HashMap<>();
		params.put("category_id", categoryId);
		params.put("lang", lang);
		
		try {
			return jdbcTemplate.queryForObject(
				findCategoryLinkEntityByIdSql,
				params,
				RowMappers::forLinkEntityDto
			);
		} catch (EmptyResultDataAccessException ignored) {
			return null;
		}
	}
	
}
