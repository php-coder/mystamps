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
package ru.mystamps.web.feature.image;

import org.apache.commons.lang3.Validate;
import org.springframework.core.env.Environment;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import ru.mystamps.web.common.JdbcUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JdbcImageDao implements ImageDao {
	
	private final NamedParameterJdbcTemplate jdbcTemplate;
	private final String addImageSql;
	private final String replaceImageSql;
	private final String addImageToSeriesSql;
	private final String findByIdSql;
	private final String findBySeriesIdSql;
	
	public JdbcImageDao(Environment env, NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate        = jdbcTemplate;
		this.addImageSql         = env.getRequiredProperty("image.add");
		this.replaceImageSql     = env.getRequiredProperty("image.replace");
		this.addImageToSeriesSql = env.getRequiredProperty("series_image.add");
		this.findByIdSql         = env.getRequiredProperty("image.find_by_id");
		this.findBySeriesIdSql   = env.getRequiredProperty("series_image.find_by_series_id");
	}
	
	@Override
	public Integer add(String type, String filename) {
		Map<String, Object> params = new HashMap<>();
		params.put("type", type);
		params.put("filename", filename);
		KeyHolder holder = new GeneratedKeyHolder();
		
		int affected = jdbcTemplate.update(
			addImageSql,
			new MapSqlParameterSource(params),
			holder,
			JdbcUtils.ID_KEY_COLUMN
		);
		
		Validate.validState(
			affected == 1,
			"Unexpected number of affected rows after adding image: %d",
			affected
		);
		
		return Integer.valueOf(holder.getKey().intValue());
	}
	
	@Override
	public void replace(Integer id, String type, String filename) {
		Map<String, Object> params = new HashMap<>();
		params.put("id", id);
		params.put("type", type);
		params.put("filename", filename);
		
		int affected = jdbcTemplate.update(replaceImageSql, params);
		
		Validate.validState(
			affected == 1,
			"Unexpected number of affected rows after replacing image #%d: %d",
			id,
			affected
		);
	}
	
	@Override
	public void addToSeries(Integer seriesId, Integer imageId) {
		Map<String, Object> params = new HashMap<>();
		params.put("series_id", seriesId);
		params.put("image_id", imageId);
		
		jdbcTemplate.update(addImageToSeriesSql, params);
	}
	
	@Override
	public ImageInfoDto findById(Integer imageId) {
		try {
			return jdbcTemplate.queryForObject(
				findByIdSql,
				Collections.singletonMap("id", imageId),
				RowMappers::forImageInfoDto
			);
		} catch (EmptyResultDataAccessException ignored) {
			return null;
		}
	}
	
	@Override
	public List<Integer> findBySeriesId(Integer seriesId, boolean hidden) {
		Map<String, Object> params = new HashMap<>();
		params.put("series_id", seriesId);
		params.put("hidden", hidden);
		
		return jdbcTemplate.queryForList(
			findBySeriesIdSql,
			params,
			Integer.class
		);
	}
	
}
