/*
 * Copyright (C) 2009-2024 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.feature.category;

import org.apache.commons.lang3.Validate;
import org.springframework.core.env.Environment;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import ru.mystamps.web.common.EntityWithParentDto;
import ru.mystamps.web.common.JdbcUtils;
import ru.mystamps.web.common.LinkEntityDto;
import ru.mystamps.web.common.RowMappers;
import ru.mystamps.web.common.SitemapInfoDto;
import ru.mystamps.web.support.spring.jdbc.MapStringIntegerResultSetExtractor;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JdbcCategoryDao implements CategoryDao {
	
	private static final ResultSetExtractor<Map<String, Integer>> NAME_COUNTER_EXTRACTOR =
		new MapStringIntegerResultSetExtractor("name", "counter");
	
	private final NamedParameterJdbcTemplate jdbcTemplate;
	private final String addCategorySql;
	private final String countAllSql;
	private final String countBySlugSql;
	private final String countByNameSql;
	private final String countByNameRuSql;
	private final String countCategoriesOfCollectionSql;
	private final String countCategoriesAddedSinceSql;
	private final String countUntranslatedNamesSinceSql;
	private final String countStampsByCategoriesSql;
	private final String findIdsByNamesSql;
	private final String findIdsByNamePatternSql;
	private final String findCategoriesNamesWithSlugSql;
	private final String findAllForSitemapSql;
	private final String findLinkEntityBySlugSql;
	private final String findCategoriesWithParentNamesSql;
	private final String findFromLastCreatedSeriesByUserSql;
	
	public JdbcCategoryDao(Environment env, NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate                       = jdbcTemplate;
		this.addCategorySql                     = env.getRequiredProperty("category.create");
		this.countAllSql                        = env.getRequiredProperty("category.count_all_categories");
		this.countBySlugSql                     = env.getRequiredProperty("category.count_categories_by_slug");
		this.countByNameSql                     = env.getRequiredProperty("category.count_categories_by_name");
		this.countByNameRuSql                   = env.getRequiredProperty("category.count_categories_by_name_ru");
		this.countCategoriesOfCollectionSql     = env.getRequiredProperty("category.count_categories_of_collection");
		this.countCategoriesAddedSinceSql       = env.getRequiredProperty("category.count_categories_added_since");
		this.countUntranslatedNamesSinceSql     = env.getRequiredProperty("category.count_untranslated_names_since");
		this.countStampsByCategoriesSql         = env.getRequiredProperty("category.count_stamps_by_categories");
		this.findIdsByNamesSql                  = env.getRequiredProperty("category.find_ids_by_names");
		this.findIdsByNamePatternSql            = env.getRequiredProperty("category.find_ids_by_name_pattern");
		this.findCategoriesNamesWithSlugSql     = env.getRequiredProperty("category.find_all_categories_names_with_slug");
		this.findAllForSitemapSql               = env.getRequiredProperty("category.find_all_for_sitemap");
		this.findLinkEntityBySlugSql            = env.getRequiredProperty("category.find_category_link_info_by_slug");
		this.findCategoriesWithParentNamesSql   = env.getRequiredProperty("category.find_categories_with_parent_names");
		this.findFromLastCreatedSeriesByUserSql = env.getRequiredProperty("category.find_from_last_created_series_by_user");
	}
	
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
			holder,
			JdbcUtils.ID_KEY_COLUMN
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
		return jdbcTemplate.queryForObject(countAllSql, Collections.emptyMap(), Long.class);
	}
	
	@Override
	public long countBySlug(String slug) {
		return jdbcTemplate.queryForObject(
			countBySlugSql,
			Collections.singletonMap("slug", slug),
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
	public long countUntranslatedNamesSince(Date date) {
		return jdbcTemplate.queryForObject(
			countUntranslatedNamesSinceSql,
			Collections.singletonMap("date", date),
			Long.class
		);
	}
	
	@Override
	public Map<String, Integer> getStatisticsOf(Integer collectionId, String lang) {
		Map<String, Object> params = new HashMap<>();
		params.put("collection_id", collectionId);
		params.put("lang", lang);
		
		return jdbcTemplate.query(
			countStampsByCategoriesSql,
			params,
			NAME_COUNTER_EXTRACTOR
		);
	}
	
	@Override
	public List<Integer> findIdsByNames(List<String> names) {
		return jdbcTemplate.query(
			findIdsByNamesSql,
			Collections.singletonMap("names", names),
			RowMappers::forInteger
		);
	}
	
	@Override
	public List<Integer> findIdsByNamePattern(String pattern) {
		return jdbcTemplate.query(
			findIdsByNamePatternSql,
			Collections.singletonMap("pattern", pattern),
			RowMappers::forInteger
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
	public List<SitemapInfoDto> findAllForSitemap() {
		return jdbcTemplate.query(
			findAllForSitemapSql,
			Collections.emptyMap(),
			RowMappers::forSitemapInfoDto
		);
	}
	
	@Override
	public LinkEntityDto findOneAsLinkEntity(String slug, String lang) {
		Map<String, Object> params = new HashMap<>();
		params.put("slug", slug);
		params.put("lang", lang);
		
		try {
			return jdbcTemplate.queryForObject(
				findLinkEntityBySlugSql,
				params,
				RowMappers::forLinkEntityDto
			);
		} catch (EmptyResultDataAccessException ignored) {
			return null;
		}
	}
	
	@Override
	public List<EntityWithParentDto> findCategoriesWithParents(String lang) {
		return jdbcTemplate.query(
			findCategoriesWithParentNamesSql,
			Collections.singletonMap("lang", lang),
			RowMappers::forEntityWithParentDto
		);
	}
	
	@Override
	public String findCategoryOfLastCreatedSeriesByUser(Integer userId) {
		try {
			return jdbcTemplate.queryForObject(
				findFromLastCreatedSeriesByUserSql,
				Collections.singletonMap("created_by", userId),
				String.class
			);
		} catch (EmptyResultDataAccessException ignored) {
			return null;
		}
	}
	
}
