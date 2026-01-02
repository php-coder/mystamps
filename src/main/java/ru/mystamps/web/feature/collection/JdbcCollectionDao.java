/*
 * Copyright (C) 2009-2026 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.feature.collection;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import ru.mystamps.web.common.JdbcUtils;
import ru.mystamps.web.common.LinkEntityDto;
import ru.mystamps.web.common.SitemapInfoDto;
import ru.mystamps.web.support.spring.jdbc.MapIntegerIntegerResultSetExtractor;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JdbcCollectionDao implements CollectionDao {
	private static final Logger LOG = LoggerFactory.getLogger(JdbcCollectionDao.class);
	
	private static final ResultSetExtractor<Map<Integer, Integer>> INSTANCES_COUNTER_EXTRACTOR =
		new MapIntegerIntegerResultSetExtractor("id", "number_of_stamps");
	
	private final NamedParameterJdbcTemplate jdbcTemplate;
	private final String findLastCreatedCollectionsSql;
	private final String findSeriesByCollectionIdSql;
	private final String findSeriesWithPricesBySlugSql;
	private final String findAllForSitemapSql;
	private final String countCollectionsOfUsersSql;
	private final String countUpdatedSinceSql;
	private final String countSeriesOfCollectionSql;
	private final String countStampsOfCollectionSql;
	private final String addCollectionSql;
	private final String markAsModifiedSql;
	private final String isSeriesInUserCollectionSql;
	private final String findSeriesInstancesSql;
	private final String addSeriesToCollectionSql;
	private final String removeSeriesInstanceSql;
	private final String findCollectionInfoBySlugSql;
	
	public JdbcCollectionDao(Environment env, NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate                  = jdbcTemplate;
		this.findLastCreatedCollectionsSql = env.getRequiredProperty("collection.find_last_created");
		this.findSeriesByCollectionIdSql   = env.getRequiredProperty("collection.find_series_by_collection_id");
		this.findSeriesWithPricesBySlugSql = env.getRequiredProperty("collection.find_series_with_prices_by_slug");
		this.findAllForSitemapSql          = env.getRequiredProperty("collection.find_all_for_sitemap");
		this.countCollectionsOfUsersSql    = env.getRequiredProperty("collection.count_collections_of_users");
		this.countUpdatedSinceSql          = env.getRequiredProperty("collection.count_updated_since");
		this.countSeriesOfCollectionSql    = env.getRequiredProperty("collection.count_series_of_collection");
		this.countStampsOfCollectionSql    = env.getRequiredProperty("collection.count_stamps_of_collection");
		this.addCollectionSql              = env.getRequiredProperty("collection.create");
		this.markAsModifiedSql             = env.getRequiredProperty("collection.mark_as_modified");
		this.isSeriesInUserCollectionSql   = env.getRequiredProperty("collection.is_series_in_collection");
		this.findSeriesInstancesSql        = env.getRequiredProperty("collection.find_series_instances");
		this.addSeriesToCollectionSql      = env.getRequiredProperty("collection.add_series_to_collection");
		this.removeSeriesInstanceSql       = env.getRequiredProperty("collection.remove_series_instance_from_collection");
		this.findCollectionInfoBySlugSql   = env.getRequiredProperty("collection.find_info_by_slug");
	}
	
	@Override
	public List<LinkEntityDto> findLastCreated(int quantity) {
		return jdbcTemplate.query(
			findLastCreatedCollectionsSql,
			Collections.singletonMap("quantity", quantity),
			ru.mystamps.web.common.RowMappers::forLinkEntityDto
		);
	}
	
	@Override
	public List<SeriesInCollectionDto> findSeriesByCollectionId(Integer collectionId, String lang) {
		Map<String, Object> params = new HashMap<>();
		params.put("collection_id", collectionId);
		params.put("lang", lang);
		
		return jdbcTemplate.query(
			findSeriesByCollectionIdSql,
			params,
			RowMappers::forSeriesInCollectionDto
		);
	}
	
	@Override
	public List<SeriesInCollectionWithPriceDto> findSeriesWithPricesBySlug(
		String slug,
		String lang) {
		
		Map<String, Object> params = new HashMap<>();
		params.put("slug", slug);
		params.put("lang", lang);
		
		return jdbcTemplate.query(
			findSeriesWithPricesBySlugSql,
			params,
			RowMappers::forSeriesInCollectionWithPriceDto
		);
	}
	
	@Override
	public List<SitemapInfoDto> findAllForSitemap() {
		return jdbcTemplate.query(
			findAllForSitemapSql,
			Collections.emptyMap(),
			ru.mystamps.web.common.RowMappers::forSitemapInfoDto
		);
	}
	
	@Override
	public long countCollectionsOfUsers() {
		return jdbcTemplate.queryForObject(
			countCollectionsOfUsersSql,
			Collections.emptyMap(),
			Long.class
		);
	}
	
	@Override
	public long countUpdatedSince(Date date) {
		return jdbcTemplate.queryForObject(
			countUpdatedSinceSql,
			Collections.singletonMap("date", date),
			Long.class
		);
	}
	
	@Override
	public long countSeriesOfCollection(Integer collectionId) {
		return jdbcTemplate.queryForObject(
			countSeriesOfCollectionSql,
			Collections.singletonMap("collection_id", collectionId),
			Long.class
		);
	}
	
	@Override
	public long countStampsOfCollection(Integer collectionId) {
		return jdbcTemplate.queryForObject(
			countStampsOfCollectionSql,
			Collections.singletonMap("collection_id", collectionId),
			Long.class
		);
	}
	
	@Override
	public Integer add(AddCollectionDbDto collection) {
		Map<String, Object> params = new HashMap<>();
		params.put("user_id", collection.getOwnerId());
		params.put("slug", collection.getSlug());
		params.put("updated_at", collection.getUpdatedAt());
		params.put("updated_by", collection.getOwnerId());
		
		KeyHolder holder = new GeneratedKeyHolder();
		
		int affected = jdbcTemplate.update(
			addCollectionSql,
			new MapSqlParameterSource(params),
			holder,
			JdbcUtils.ID_KEY_COLUMN
		);
		
		Validate.validState(
			affected == 1,
			"Unexpected number of affected rows after creation of collection of user #%d: %d",
			params.get("user_id"),
			affected
		);
		
		return Integer.valueOf(holder.getKey().intValue());
	}

	/**
	 * @author John Shkarin
	 * @author Slava Semushin
	 */
	@Override
	public void markAsModified(Integer userId, Date updatedAt) {
		Map<String, Object> params = new HashMap<>();
		params.put("user_id", userId);
		params.put("updated_at", updatedAt);
		params.put("updated_by", userId);

		int affected = jdbcTemplate.update(
			markAsModifiedSql,
			params
		);

		Validate.validState(
			affected == 1,
			"Unexpected number of affected rows after updating collection: %d",
			affected
		);
	}
	
	@Override
	public boolean isSeriesInUserCollection(Integer userId, Integer seriesId) {
		Map<String, Object> params = new HashMap<>();
		params.put("user_id", userId);
		params.put("series_id", seriesId);
		
		Long result = jdbcTemplate.queryForObject(isSeriesInUserCollectionSql, params, Long.class);
		Validate.validState(result != null, "Query returned null instead of long");
		
		return result > 0;
	}
	
	@Override
	public Map<Integer, Integer> findSeriesInstances(Integer userId, Integer seriesId) {
		Map<String, Object> params = new HashMap<>();
		params.put("user_id", userId);
		params.put("series_id", seriesId);
		
		return jdbcTemplate.query(
			findSeriesInstancesSql,
			params,
			INSTANCES_COUNTER_EXTRACTOR
		);
	}
	
	@Override
	public Integer addSeriesToUserCollection(AddToCollectionDbDto dto) {
		Map<String, Object> params = new HashMap<>();
		params.put("user_id", dto.getOwnerId());
		params.put("series_id", dto.getSeriesId());
		params.put("number_of_stamps", dto.getNumberOfStamps());
		params.put("price", dto.getPrice());
		params.put("currency", dto.getCurrency());
		params.put("added_at", dto.getAddedAt());
		
		KeyHolder holder = new GeneratedKeyHolder();
		
		int affected = jdbcTemplate.update(
			addSeriesToCollectionSql,
			new MapSqlParameterSource(params),
			holder,
			JdbcUtils.ID_KEY_COLUMN
		);
		
		Validate.validState(
			affected == 1,
			"Unexpected number of affected rows after adding series #%d to collection of user #%d: %d",
			dto.getSeriesId(),
			dto.getOwnerId(),
			affected
		);
		
		return holder.getKey().intValue();
	}
	
	@Override
	public void removeSeriesFromUserCollection(Integer userId, Integer seriesInstanceId) {
		Map<String, Object> params = new HashMap<>();
		params.put("id", seriesInstanceId);
		params.put("user_id", userId);
		
		int affected = jdbcTemplate.update(removeSeriesInstanceSql, params);
		if (affected != 1) {
			LOG.warn(
				"Unexpected number of affected rows after removing series instance #{} from collection of user #{}: {}",
				seriesInstanceId,
				userId,
				affected
			);
		}
	}
	
	@Override
	public CollectionInfoDto findCollectionInfoBySlug(String slug) {
		try {
			return jdbcTemplate.queryForObject(
				findCollectionInfoBySlugSql,
				Collections.singletonMap("slug", slug),
				RowMappers::forCollectionInfoDto
			);
		} catch (EmptyResultDataAccessException ignored) {
			return null;
		}
	}
	
}
