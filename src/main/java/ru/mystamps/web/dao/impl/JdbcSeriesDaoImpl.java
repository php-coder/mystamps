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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.dao.JdbcSeriesDao;
import ru.mystamps.web.dao.dto.AddSeriesDbDto;
import ru.mystamps.web.service.dto.SeriesInfoDto;
import ru.mystamps.web.service.dto.SitemapInfoDto;

// TODO: move stamps related methods to separate interface (#88)
@SuppressWarnings({ "PMD.AvoidDuplicateLiterals", "PMD.TooManyMethods" })
@RequiredArgsConstructor
public class JdbcSeriesDaoImpl implements JdbcSeriesDao {
	
	private final NamedParameterJdbcTemplate jdbcTemplate;
	
	@Value("${series.create}")
	private String createSeriesSql;
	
	@Value("${series.find_all_for_sitemap}")
	private String findAllForSitemapSql;
	
	@Value("${series.find_last_added}")
	private String findLastAddedSeriesSql;
	
	@Value("${series.find_by_ids}")
	private String findByIdsSql;
	
	@Value("${series.find_by_category_id}")
	private String findByCategoryIdSql;
	
	@Value("${series.find_by_country_id}")
	private String findByCountryIdSql;
	
	@Value("${series.find_by_collection_id}")
	private String findByCollectionIdSql;
	
	@Value("${series.count_all_series}")
	private String countAllSql;
	
	@Value("${series.count_all_stamps}")
	private String countAllStampsSql;
	
	@Value("${series.count_series_of_collection}")
	private String countSeriesOfCollectionSql;
	
	@Value("${series.count_stamps_of_collection}")
	private String countStampsOfCollectionSql;
	
	@Value("${series.find_series_ids_by_michel_number}")
	private String findSeriesIdsByMichelNumberSql;
	
	@Value("${series.find_series_ids_by_scott_number}")
	private String findSeriesIdsByScottNumberSql;
	
	@Value("${series.find_series_ids_by_yvert_number}")
	private String findSeriesIdsByYvertNumberSql;
	
	@Value("${series.find_series_ids_by_gibbons_number}")
	private String findSeriesIdsByGibbonsNumberSql;
	
	@Override
	public Integer add(AddSeriesDbDto series) {
		Map<String, Object> params = new HashMap<>();
		params.put("category_id", series.getCategoryId());
		params.put("country_id", series.getCountryId());
		params.put("quantity", series.getQuantity());
		params.put("perforated", series.getPerforated());
		params.put("release_day", series.getReleaseDay());
		params.put("release_month", series.getReleaseMonth());
		params.put("release_year", series.getReleaseYear());
		params.put("michel_price", series.getMichelPrice());
		params.put("michel_currency", series.getMichelCurrency());
		params.put("scott_price", series.getScottPrice());
		params.put("scott_currency", series.getScottCurrency());
		params.put("yvert_price", series.getYvertPrice());
		params.put("yvert_currency", series.getYvertCurrency());
		params.put("gibbons_price", series.getGibbonsPrice());
		params.put("gibbons_currency", series.getGibbonsCurrency());
		params.put("comment", series.getComment());
		params.put("created_at", series.getCreatedAt());
		params.put("created_by", series.getCreatedBy());
		params.put("updated_at", series.getUpdatedAt());
		params.put("updated_by", series.getUpdatedBy());
		
		KeyHolder holder = new GeneratedKeyHolder();
		
		int affected = jdbcTemplate.update(
			createSeriesSql,
			new MapSqlParameterSource(params),
			holder
		);
		
		Validate.validState(
			affected == 1,
			"Unexpected number of affected rows after creation of series: %d",
			affected
		);
		
		return Integer.valueOf(holder.getKey().intValue());
	}
	
	@Override
	public Iterable<SitemapInfoDto> findAllForSitemap() {
		return jdbcTemplate.query(
			findAllForSitemapSql,
			Collections.<String, Object>emptyMap(),
			RowMappers::forSitemapInfoDto
		);
	}
	
	@Override
	public Iterable<SeriesInfoDto> findLastAdded(int quantity, String lang) {
		Map<String, Object> params = new HashMap<>();
		params.put("quantity", quantity);
		params.put("lang", lang);
		
		return jdbcTemplate.query(findLastAddedSeriesSql, params, RowMappers::forSeriesInfoDto);
	}
	
	@Override
	public List<SeriesInfoDto> findByIdsAsSeriesInfo(List<Integer> seriesIds, String lang) {
		Map<String, Object> params = new HashMap<>();
		params.put("series_ids", seriesIds);
		params.put("lang", lang);

		return jdbcTemplate.query(findByIdsSql, params, RowMappers::forSeriesInfoDto);
	}

	@Override
	public Iterable<SeriesInfoDto> findByCategoryIdAsSeriesInfo(Integer categoryId, String lang) {
		Map<String, Object> params = new HashMap<>();
		params.put("category_id", categoryId);
		params.put("lang", lang);
		
		return jdbcTemplate.query(findByCategoryIdSql, params, RowMappers::forSeriesInfoDto);
	}
	
	@Override
	public Iterable<SeriesInfoDto> findByCountryIdAsSeriesInfo(Integer countryId, String lang) {
		Map<String, Object> params = new HashMap<>();
		params.put("country_id", countryId);
		params.put("lang", lang);
		
		return jdbcTemplate.query(findByCountryIdSql, params, RowMappers::forSeriesInfoDto);
	}
	
	@Override
	// CheckStyle: ignore LineLength for next 1 line
	public Iterable<SeriesInfoDto> findByCollectionIdAsSeriesInfo(Integer collectionId, String lang) {
		Map<String, Object> params = new HashMap<>();
		params.put("collection_id", collectionId);
		params.put("lang", lang);
		
		return jdbcTemplate.query(findByCollectionIdSql, params, RowMappers::forSeriesInfoDto);
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
	
	@Override
	public List<Integer> findSeriesIdsByMichelNumberCode(String michelNumber) {
		return jdbcTemplate.queryForList(
			findSeriesIdsByMichelNumberSql,
			Collections.singletonMap("michel_number", michelNumber),
			Integer.class);
	}
	
	@Override
	public List<Integer> findSeriesIdsByScottNumberCode(String scottNumber) {
		return jdbcTemplate.queryForList(
			findSeriesIdsByScottNumberSql,
			Collections.singletonMap("scott_number", scottNumber),
			Integer.class);
	}
	
	@Override
	public List<Integer> findSeriesIdsByYvertNumberCode(String yvertNumber) {
		return jdbcTemplate.queryForList(
			findSeriesIdsByYvertNumberSql,
			Collections.singletonMap("yvert_number", yvertNumber),
			Integer.class);
	}
	
	@Override
	public List<Integer> findSeriesIdsByGibbonsNumberCode(String gibbonsNumber) {
		return jdbcTemplate.queryForList(
			findSeriesIdsByGibbonsNumberSql,
			Collections.singletonMap("gibbons_number", gibbonsNumber),
			Integer.class);
	}
	
}
