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

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import ru.mystamps.web.dao.JdbcSeriesDao;
import ru.mystamps.web.service.dto.SeriesInfoDto;
import ru.mystamps.web.service.dto.SitemapInfoDto;

public class JdbcSeriesDaoImpl implements JdbcSeriesDao {
	
	private static final RowMapper<SitemapInfoDto> SITEMAP_INFO_DTO_ROW_MAPPER =
		new SitemapInfoDtoRowMapper();
	
	private static final RowMapper<SeriesInfoDto> SERIES_INFO_DTO_ROW_MAPPER =
		new SeriesInfoDtoRowMapper();
	
	private final NamedParameterJdbcTemplate jdbcTemplate;
	
	@Value("${series.find_all_for_sitemap}")
	private String findAllForSitemapSql;
	
	@Value("${series.find_last_added}")
	private String findLastAddedSeriesSql;
	
	@Value("${series.find_by_category_id}")
	private String findByCategoryIdSql;
	
	@Value("${series.find_by_country_id}")
	private String findByCountryIdSql;
	
	@Value("${series.count_all_series}")
	private String countAllSql;
	
	@Value("${series.count_all_stamps}")
	private String countAllStampsSql;
	
	@Value("${series.count_series_of_collection}")
	private String countSeriesOfCollectionSql;
	
	@Value("${series.count_stamps_of_collection}")
	private String countStampsOfCollectionSql;
	
	public JdbcSeriesDaoImpl(DataSource dataSource) {
		jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	@Override
	public Iterable<SitemapInfoDto> findAllForSitemap() {
		return jdbcTemplate.query(
			findAllForSitemapSql,
			Collections.<String, Object>emptyMap(),
			SITEMAP_INFO_DTO_ROW_MAPPER
		);
	}
	
	@Override
	public Iterable<SeriesInfoDto> findLastAdded(int quantity, String lang) {
		Map<String, Object> params = new HashMap<>();
		params.put("quantity", quantity);
		params.put("lang", lang);
		
		return jdbcTemplate.query(findLastAddedSeriesSql, params, SERIES_INFO_DTO_ROW_MAPPER);
	}
	
	@Override
	public Iterable<SeriesInfoDto> findByCategoryIdAsSeriesInfo(Integer categoryId, String lang) {
		Map<String, Object> params = new HashMap<>();
		params.put("category_id", categoryId);
		params.put("lang", lang);
		
		return jdbcTemplate.query(findByCategoryIdSql, params, SERIES_INFO_DTO_ROW_MAPPER);
	}
	
	@Override
	public Iterable<SeriesInfoDto> findByCountryIdAsSeriesInfo(Integer countryId, String lang) {
		Map<String, Object> params = new HashMap<>();
		params.put("country_id", countryId);
		params.put("lang", lang);
		
		return jdbcTemplate.query(findByCountryIdSql, params, SERIES_INFO_DTO_ROW_MAPPER);
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
	public long countAllStamps() {
		return jdbcTemplate.queryForObject(
			countAllStampsSql,
			Collections.<String, Object>emptyMap(),
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
	
}
