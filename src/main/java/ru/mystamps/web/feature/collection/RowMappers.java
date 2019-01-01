/*
 * Copyright (C) 2009-2019 Slava Semushin <slava.semushin@gmail.com>
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

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

import ru.mystamps.web.dao.dto.Currency;
import ru.mystamps.web.support.jdbc.JdbcUtils;

final class RowMappers {
	
	private RowMappers() {
	}
	
	/* default */ static CollectionInfoDto forCollectionInfoDto(ResultSet rs, int unused)
		throws SQLException {
		
		return new CollectionInfoDto(
			rs.getInt("id"),
			rs.getString("slug"),
			rs.getString("name")
		);
	}
	
	/* default */ static SeriesInCollectionDto forSeriesInCollectionDto(ResultSet rs, int unused)
		throws SQLException {
		
		Integer seriesId     = rs.getInt("id");
		String category      = rs.getString("category");
		String country       = rs.getString("country");
		Integer releaseDay   = JdbcUtils.getInteger(rs, "release_day");
		Integer releaseMonth = JdbcUtils.getInteger(rs, "release_month");
		Integer releaseYear  = JdbcUtils.getInteger(rs, "release_year");
		Integer quantity     = rs.getInt("quantity");
		Boolean perforated   = rs.getBoolean("perforated");
		Integer numberOfStamps = rs.getInt("number_of_stamps");
		
		return new SeriesInCollectionDto(
			seriesId,
			category,
			country,
			releaseDay,
			releaseMonth,
			releaseYear,
			perforated,
			quantity,
			numberOfStamps
		);
	}
	
	/* default */ static SeriesInCollectionWithPriceDto forSeriesInCollectionWithPriceDto(
		ResultSet rs,
		int unused)
		throws SQLException {

		Integer id             = rs.getInt("id");
		Integer releaseYear    = JdbcUtils.getInteger(rs, "release_year");
		Integer quantity       = rs.getInt("quantity");
		Boolean perforated     = rs.getBoolean("perforated");
		Integer numberOfStamps = rs.getInt("number_of_stamps");
		String country         = rs.getString("country_name");
		BigDecimal price       = rs.getBigDecimal("price");
		Currency currency      = JdbcUtils.getCurrency(rs, "currency");
		
		return new SeriesInCollectionWithPriceDto(
			id,
			releaseYear,
			quantity,
			numberOfStamps,
			perforated,
			country,
			price,
			currency
		);
	}
	
}
