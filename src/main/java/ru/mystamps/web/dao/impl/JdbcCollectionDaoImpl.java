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
import java.util.Map;

import org.apache.commons.lang3.Validate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.dao.JdbcCollectionDao;
import ru.mystamps.web.dao.dto.AddCollectionDbDto;
import ru.mystamps.web.service.dto.LinkEntityDto;
import ru.mystamps.web.service.dto.UrlEntityDto;

@RequiredArgsConstructor
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class JdbcCollectionDaoImpl implements JdbcCollectionDao {
	
	private final NamedParameterJdbcTemplate jdbcTemplate;
	
	@Value("${collection.find_last_created}")
	private String findLastCreatedCollectionsSql;
	
	@Value("${collection.count_collections_of_users}")
	private String countCollectionsOfUsersSql;
	
	@Value("${collection.create}")
	private String addCollectionSql;
	
	@Value("${collection.is_series_in_collection}")
	private String isSeriesInUserCollectionSql;
	
	@SuppressWarnings("PMD.LongVariable")
	@Value("${collection.find_id_and_slug_by_user_id}")
	private String findCollectionIdAndSlugByUserIdSql;
	
	@Value("${collection.add_series_to_collection}")
	private String addSeriesToCollectionSql;
	
	@Override
	public Iterable<LinkEntityDto> findLastCreated(int quantity) {
		return jdbcTemplate.query(
			findLastCreatedCollectionsSql,
			Collections.singletonMap("quantity", quantity),
			RowMappers::forLinkEntityDto
		);
	}
	
	@Override
	public long countCollectionsOfUsers() {
		return jdbcTemplate.queryForObject(
			countCollectionsOfUsersSql,
			Collections.<String, Object>emptyMap(),
			Long.class
		);
	}
	
	@Override
	public Integer add(AddCollectionDbDto collection) {
		Map<String, Object> params = new HashMap<>();
		params.put("user_id", collection.getOwnerId());
		params.put("slug", collection.getSlug());
		
		KeyHolder holder = new GeneratedKeyHolder();
		
		int affected = jdbcTemplate.update(
			addCollectionSql,
			new MapSqlParameterSource(params),
			holder
		);
		
		Validate.validState(
			affected == 1,
			"Unexpected number of affected rows after creation of collection of user #%d: %d",
			params.get("user_id"),
			affected
		);
		
		return Integer.valueOf(holder.getKey().intValue());
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
	public UrlEntityDto findCollectionUrlEntityByUserId(Integer userId) {
		return jdbcTemplate.queryForObject(
			findCollectionIdAndSlugByUserIdSql,
			Collections.singletonMap("user_id", userId),
			RowMappers::forUrlEntityDto
		);
	}
	
	@Override
	public void addSeriesToCollection(Integer collectionId, Integer seriesId) {
		Map<String, Object> params = new HashMap<>();
		params.put("collection_id", collectionId);
		params.put("series_id", seriesId);
		
		int affected = jdbcTemplate.update(addSeriesToCollectionSql, params);
		Validate.validState(
			affected == 1,
			"Unexpected number of affected rows after adding series #%d to collection #%d: %d",
			seriesId,
			collectionId,
			affected
		);
	}
	
}
