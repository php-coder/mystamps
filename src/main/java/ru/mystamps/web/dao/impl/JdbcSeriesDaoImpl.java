/*
 * Copyright (C) 2009-2014 Slava Semushin <slava.semushin@gmail.com>
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

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import ru.mystamps.web.dao.JdbcSeriesDao;
import ru.mystamps.web.service.dto.SeriesInfoDto;

public class JdbcSeriesDaoImpl implements JdbcSeriesDao {
	
	private static final RowMapper<SeriesInfoDto> SERIES_INFO_DTO_ROW_MAPPER =
		new SeriesInfoDtoRowMapper();
	
	private final NamedParameterJdbcTemplate jdbcTemplate;
	
	@Value("${series.find_last_added_sql}")
	private String findLastAddedSeriesSql;
	
	@Value("${series.find_last_added_ru_sql}")
	private String findLastAddedSeriesRuSql;
	
	public JdbcSeriesDaoImpl(DataSource dataSource) {
		jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	@Override
	public Iterable<SeriesInfoDto> findLastAdded(int quantity, String lang) {
		String sql;
		if ("ru".equals(lang)) {
			sql = findLastAddedSeriesRuSql;
		} else {
			sql = findLastAddedSeriesSql;
		}
		
		return jdbcTemplate.query(
			sql,
			Collections.singletonMap("quantity", quantity),
			SERIES_INFO_DTO_ROW_MAPPER
		);
	}
	
}
