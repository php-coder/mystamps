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
package ru.mystamps.web.feature.series;

import org.apache.commons.lang3.Validate;
import org.springframework.core.env.Environment;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import ru.mystamps.web.common.JdbcUtils;
import ru.mystamps.web.common.SitemapInfoDto;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// FIXME: move stamps related methods to separate interface (#88)
public class JdbcSeriesDao implements SeriesDao {
	
	private final NamedParameterJdbcTemplate jdbcTemplate;
	private final String createSeriesSql;
	private final String addCommentSql;
	private final String addReleaseYearSql;
	private final String markAsModifiedSql;
	private final String findAllForSitemapSql;
	private final String findSimilarSeriesSql;
	private final String findLastAddedSeriesSql;
	private final String findFullInfoByIdSql;
	private final String findByIdsSql;
	private final String findByCategorySlugSql;
	private final String findByCountrySlugSql;
	private final String countAllSql;
	private final String countAllStampsSql;
	private final String countSeriesByIdSql;
	private final String countSeriesAddedSinceSql;
	private final String countSeriesUpdatedSinceSql;
	private final String findQuantityByIdSql;
	private final String addSimilarSeriesSql;
	private final String addMichelPriceSql;
	private final String addScottPriceSql;
	private final String addYvertPriceSql;
	private final String addGibbonsPriceSql;
	private final String addSolovyovPriceSql;
	private final String addZagorskiPriceSql;
	
	public JdbcSeriesDao(Environment env, NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate               = jdbcTemplate;
		this.createSeriesSql            = env.getRequiredProperty("series.create");
		this.addCommentSql              = env.getRequiredProperty("series.add_comment");
		this.addReleaseYearSql          = env.getRequiredProperty("series.add_release_year");
		this.markAsModifiedSql          = env.getRequiredProperty("series.mark_as_modified");
		this.findAllForSitemapSql       = env.getRequiredProperty("series.find_all_for_sitemap");
		this.findSimilarSeriesSql       = env.getRequiredProperty("series.find_similar_series");
		this.findLastAddedSeriesSql     = env.getRequiredProperty("series.find_last_added");
		this.findFullInfoByIdSql        = env.getRequiredProperty("series.find_full_info_by_id");
		this.findByIdsSql               = env.getRequiredProperty("series.find_by_ids");
		this.findByCategorySlugSql      = env.getRequiredProperty("series.find_by_category_slug");
		this.findByCountrySlugSql       = env.getRequiredProperty("series.find_by_country_slug");
		this.countAllSql                = env.getRequiredProperty("series.count_all_series");
		this.countAllStampsSql          = env.getRequiredProperty("series.count_all_stamps");
		this.countSeriesByIdSql         = env.getRequiredProperty("series.count_series_by_id");
		this.countSeriesAddedSinceSql   = env.getRequiredProperty("series.count_series_added_since");
		this.countSeriesUpdatedSinceSql = env.getRequiredProperty("series.count_series_updated_since");
		this.findQuantityByIdSql        = env.getRequiredProperty("series.find_quantity_by_id");
		this.addSimilarSeriesSql        = env.getRequiredProperty("series.add_similar_series");
		this.addMichelPriceSql          = env.getRequiredProperty("series.add_michel_price");
		this.addScottPriceSql           = env.getRequiredProperty("series.add_scott_price");
		this.addYvertPriceSql           = env.getRequiredProperty("series.add_yvert_price");
		this.addGibbonsPriceSql         = env.getRequiredProperty("series.add_gibbons_price");
		this.addSolovyovPriceSql        = env.getRequiredProperty("series.add_solovyov_price");
		this.addZagorskiPriceSql        = env.getRequiredProperty("series.add_zagorski_price");
	}
	
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
		params.put("scott_price", series.getScottPrice());
		params.put("yvert_price", series.getYvertPrice());
		params.put("gibbons_price", series.getGibbonsPrice());
		params.put("solovyov_price", series.getSolovyovPrice());
		params.put("zagorski_price", series.getZagorskiPrice());
		params.put("created_at", series.getCreatedAt());
		params.put("created_by", series.getCreatedBy());
		params.put("updated_at", series.getUpdatedAt());
		params.put("updated_by", series.getUpdatedBy());
		
		KeyHolder holder = new GeneratedKeyHolder();
		
		int affected = jdbcTemplate.update(
			createSeriesSql,
			new MapSqlParameterSource(params),
			holder,
			JdbcUtils.ID_KEY_COLUMN
		);
		
		Validate.validState(
			affected == 1,
			"Unexpected number of affected rows after creation of series: %d",
			affected
		);
		
		return Integer.valueOf(holder.getKey().intValue());
	}
	
	@Override
	public void addComment(AddCommentDbDto dto) {
		Map<String, Object> params = new HashMap<>();
		params.put("series_id", dto.getSeriesId());
		params.put("user_id", dto.getUserId());
		params.put("comment", dto.getComment());
		params.put("created_at", dto.getCreatedAt());
		params.put("updated_at", dto.getUpdatedAt());
		
		int affected = jdbcTemplate.update(addCommentSql, params);
		
		// @todo #785 Update series: handle refuse to update an existing comment gracefully
		Validate.validState(
			affected == 1,
			"Unexpected number of affected rows after adding a series comment: %d",
			affected
		);
	}
	
	@Override
	public void addReleaseYear(AddReleaseYearDbDto dto) {
		Map<String, Object> params = new HashMap<>();
		params.put("series_id", dto.getSeriesId());
		params.put("release_year", dto.getReleaseYear());
		params.put("updated_at", dto.getUpdatedAt());
		params.put("updated_by", dto.getUpdatedBy());
		
		int affected = jdbcTemplate.update(addReleaseYearSql, params);
		
		// @todo #1343 Update series: handle refuse to update an existing release year gracefully
		Validate.validState(
			affected == 1,
			"Unexpected number of affected rows after updating series: %d",
			affected
		);
	}

	/**
	 * @author Sergey Chechenev
	 */
	@Override
	public void markAsModified(Integer seriesId, Date updatedAt, Integer updatedBy) {
		Map<String, Object> params = new HashMap<>();
		params.put("series_id", seriesId);
		params.put("updated_at", updatedAt);
		params.put("updated_by", updatedBy);
		
		int affected = jdbcTemplate.update(
			markAsModifiedSql,
			params
		);
		
		Validate.validState(
			affected == 1,
			"Unexpected number of affected rows after updating series: %d",
			affected
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
	public List<SeriesLinkDto> findSimilarSeries(Integer seriesId, String lang) {
		Map<String, Object> params = new HashMap<>();
		params.put("id", seriesId);
		params.put("lang", lang);
		
		return jdbcTemplate.query(findSimilarSeriesSql, params, RowMappers::forSeriesLinkDto);
	}
	
	@Override
	public List<SeriesLinkDto> findLastAdded(int quantity, String lang) {
		Map<String, Object> params = new HashMap<>();
		params.put("quantity", quantity);
		params.put("lang", lang);
		
		return jdbcTemplate.query(findLastAddedSeriesSql, params, RowMappers::forSeriesLinkDto);
	}
	
	@Override
	public SeriesFullInfoDto findByIdAsSeriesFullInfo(Integer seriesId, Integer userId, String lang) {
		Map<String, Object> params = new HashMap<>();
		params.put("series_id", seriesId);
		params.put("user_id", userId);
		params.put("lang", lang);
		
		try {
			return jdbcTemplate.queryForObject(
				findFullInfoByIdSql,
				params,
				RowMappers::forSeriesFullInfoDto
			);
		} catch (EmptyResultDataAccessException ignored) {
			return null;
		}
	}
	
	/**
	 * @author Sergey Chechenev
	 */
	@Override
	public List<SeriesInfoDto> findByIdsAsSeriesInfo(List<Integer> seriesIds, String lang) {
		Map<String, Object> params = new HashMap<>();
		params.put("series_ids", seriesIds);
		params.put("lang", lang);

		return jdbcTemplate.query(findByIdsSql, params, RowMappers::forSeriesInfoDto);
	}

	@Override
	public List<SeriesInfoDto> findByCategorySlugAsSeriesInfo(String slug, String lang) {
		Map<String, Object> params = new HashMap<>();
		params.put("slug", slug);
		params.put("lang", lang);
		
		return jdbcTemplate.query(findByCategorySlugSql, params, RowMappers::forSeriesInfoDto);
	}
	
	@Override
	public List<SeriesInGalleryDto> findByCountrySlug(String slug, String lang) {
		Map<String, Object> params = new HashMap<>();
		params.put("slug", slug);
		params.put("lang", lang);
		
		return jdbcTemplate.query(findByCountrySlugSql, params, RowMappers::forSeriesInGalleryDto);
	}
	
	@Override
	public long countAll() {
		return jdbcTemplate.queryForObject(countAllSql, Collections.emptyMap(), Long.class);
	}
	
	@Override
	public long countAllStamps() {
		return jdbcTemplate.queryForObject(countAllStampsSql, Collections.emptyMap(), Long.class);
	}
	
	@Override
	public long countSeriesById(Integer seriesId) {
		return jdbcTemplate.queryForObject(
			countSeriesByIdSql,
			Collections.singletonMap("series_id", seriesId),
			Long.class
		);
	}
	
	@Override
	public long countAddedSince(Date date) {
		return jdbcTemplate.queryForObject(
			countSeriesAddedSinceSql,
			Collections.singletonMap("date", date),
			Long.class
		);
	}
	
	@Override
	public long countUpdatedSince(Date date) {
		return jdbcTemplate.queryForObject(
			countSeriesUpdatedSinceSql,
			Collections.singletonMap("date", date),
			Long.class
		);
	}
	
	@Override
	public Integer findQuantityById(Integer seriesId) {
		try {
			return jdbcTemplate.queryForObject(
				findQuantityByIdSql,
				Collections.singletonMap("series_id", seriesId),
				Integer.class
			);
		} catch (EmptyResultDataAccessException ignored) {
			return null;
		}
	}
	
	@Override
	public void markAsSimilar(Integer seriesId, Integer similarSeriesId) {
		Map<String, Object> params = new HashMap<>();
		params.put("series_id", seriesId);
		params.put("similar_series_id", similarSeriesId);
		
		int affected = jdbcTemplate.update(addSimilarSeriesSql, params);
		
		Validate.validState(
			affected == 1,
			"Unexpected number of affected rows after adding similar series: %d",
			affected
		);
	}
	
	@Override
	public void addMichelPrice(AddCatalogPriceDbDto dto) {
		addCatalogPrice(addMichelPriceSql, dto);
	}
	
	@Override
	public void addScottPrice(AddCatalogPriceDbDto dto) {
		addCatalogPrice(addScottPriceSql, dto);
	}
	
	@Override
	public void addYvertPrice(AddCatalogPriceDbDto dto) {
		addCatalogPrice(addYvertPriceSql, dto);
	}
	
	@Override
	public void addGibbonsPrice(AddCatalogPriceDbDto dto) {
		addCatalogPrice(addGibbonsPriceSql, dto);
	}
	
	@Override
	public void addSolovyovPrice(AddCatalogPriceDbDto dto) {
		addCatalogPrice(addSolovyovPriceSql, dto);
	}
	
	@Override
	public void addZagorskiPrice(AddCatalogPriceDbDto dto) {
		addCatalogPrice(addZagorskiPriceSql, dto);
	}
	
	private void addCatalogPrice(String query, AddCatalogPriceDbDto dto) {
		Map<String, Object> params = new HashMap<>();
		params.put("series_id", dto.getSeriesId());
		params.put("price", dto.getPrice());
		params.put("updated_at", dto.getUpdatedAt());
		params.put("updated_by", dto.getUpdatedBy());
		
		int affected = jdbcTemplate.update(query, params);
		
		// @todo #1340 Update series: handle refuse to update an existing price gracefully
		Validate.validState(
			affected == 1,
			"Unexpected number of affected rows after updating series: %d",
			affected
		);
	}
	
}
