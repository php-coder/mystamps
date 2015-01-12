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

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import ru.mystamps.web.service.dto.SeriesInfoDto;

class SeriesInfoDtoRowMapper implements RowMapper<SeriesInfoDto> {
	
	@Override
	public SeriesInfoDto mapRow(ResultSet resultSet, int i) throws SQLException {
		Integer seriesId     = resultSet.getInt("id");
		Integer releaseDay   = JdbcUtils.getInteger(resultSet, "release_day");
		Integer releaseMonth = JdbcUtils.getInteger(resultSet, "release_month");
		Integer releaseYear  = JdbcUtils.getInteger(resultSet, "release_year");
		Integer quantity     = resultSet.getInt("quantity");
		Boolean perforated   = resultSet.getBoolean("perforated");
		Integer categoryId   = resultSet.getInt("category_id");
		String categorySlug  = resultSet.getString("category_slug");
		String categoryName  = resultSet.getString("category_name");
		Integer countryId    = JdbcUtils.getInteger(resultSet, "country_id");
		String countrySlug   = resultSet.getString("country_slug");
		String countryName   = resultSet.getString("country_name");
		
		return new SeriesInfoDto(
			seriesId,
			categoryId,
			categorySlug,
			categoryName,
			countryId,
			countrySlug,
			countryName,
			releaseDay,
			releaseMonth,
			releaseYear,
			quantity,
			perforated
		);
	}
	
}
