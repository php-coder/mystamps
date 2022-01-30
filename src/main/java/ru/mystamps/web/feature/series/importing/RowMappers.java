/*
 * Copyright (C) 2009-2022 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.feature.series.importing;

import ru.mystamps.web.common.JdbcUtils;
import ru.mystamps.web.common.LinkEntityDto;

import java.sql.ResultSet;
import java.sql.SQLException;

import static ru.mystamps.web.common.RowMappers.createLinkEntityDto;

final class RowMappers {
	
	private RowMappers() {
	}
	
	/* default */ static ImportRequestDto forImportRequestDto(ResultSet rs, int unused)
		throws SQLException {
		
		return new ImportRequestDto(
			rs.getString("url"),
			rs.getString("status"),
			JdbcUtils.getInteger(rs, "series_id")
		);
	}
	
	/* default */ static SeriesParsedDataDto forSeriesParsedDataDto(ResultSet rs, int unused)
		throws SQLException {
		
		LinkEntityDto category = createLinkEntityDto(
			rs,
			"category_id",
			"category_slug",
			"category_name"
		);
		
		LinkEntityDto country = createLinkEntityDto(
			rs,
			"country_id",
			"country_slug",
			"country_name"
		);
		
		Integer releaseDay = JdbcUtils.getInteger(rs, "release_day");
		Integer releaseMonth = JdbcUtils.getInteger(rs, "release_month");
		Integer releaseYear = JdbcUtils.getInteger(rs, "release_year");
		Integer quantity = JdbcUtils.getInteger(rs, "quantity");
		Boolean perforated = JdbcUtils.getBoolean(rs, "perforated");
		String michelNumbers = rs.getString("michel_numbers");
		
		return new SeriesParsedDataDto(
			category,
			country,
			releaseDay,
			releaseMonth,
			releaseYear,
			quantity,
			perforated,
			michelNumbers
		);
	}
	
	/* default */ static ImportRequestInfo forImportRequestInfo(ResultSet rs, int unused)
		throws SQLException {
		
		return new ImportRequestInfo(rs.getInt("id"), rs.getString("url"));
	}
	
	/* default */ static ImportRequestFullInfo forImportRequestFullInfo(ResultSet rs, int unused)
		throws SQLException {
		
		return new ImportRequestFullInfo(
			rs.getInt("id"),
			rs.getString("url"),
			rs.getString("status"),
			rs.getTimestamp("updated_at")
		);
	}
	
}
