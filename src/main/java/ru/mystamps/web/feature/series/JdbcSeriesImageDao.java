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
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.HashMap;
import java.util.Map;

public class JdbcSeriesImageDao implements SeriesImageDao {
	
	private final NamedParameterJdbcTemplate jdbcTemplate;
	private final String hideImageSql;
	
	public JdbcSeriesImageDao(Environment env, NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
		this.hideImageSql = env.getRequiredProperty("series_images.mark_as_hidden");
	}
	
	@Override
	public void hideImage(Integer seriesId, Integer imageId) {
		Map<String, Object> params = new HashMap<>();
		params.put("series_id", seriesId);
		params.put("image_id", imageId);
		
		int affected = jdbcTemplate.update(hideImageSql, params);
		
		Validate.validState(
			affected == 1,
			"Unexpected number of affected rows after hiding image #%d in series #%d: %d",
			imageId,
			seriesId,
			affected
		);
	}
	
}
