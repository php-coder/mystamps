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
package ru.mystamps.web.feature.series;

import ru.mystamps.web.common.JdbcUtils;
import ru.mystamps.web.common.LinkEntityDto;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

import static ru.mystamps.web.common.RowMappers.createLinkEntityDto;

// complains on "release_year", "quantity" and "perforated"
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
final class RowMappers {
	
	private RowMappers() {
	}
	
	/* default */ static SeriesLinkDto forSeriesLinkDto(ResultSet rs, int unused)
		throws SQLException {
		
		Integer id         = rs.getInt("id");
		Integer year       = JdbcUtils.getInteger(rs, "release_year");
		Integer quantity   = rs.getInt("quantity");
		Boolean perforated = rs.getBoolean("perforated");
		String country     = rs.getString("country_name");
		
		return new SeriesLinkDto(id, year, quantity, perforated, country);
	}

	/* default */ static SeriesInfoDto forSeriesInfoDto(ResultSet rs, int unused)
		throws SQLException {
		
		Integer seriesId     = rs.getInt("id");
		Integer releaseYear  = JdbcUtils.getInteger(rs, "release_year");
		Integer quantity     = rs.getInt("quantity");
		Boolean perforated   = rs.getBoolean("perforated");
		
		LinkEntityDto category =
			createLinkEntityDto(rs, "category_id", "category_slug", "category_name");
		LinkEntityDto country =
			createLinkEntityDto(rs, "country_id", "country_slug", "country_name");
		
		return new SeriesInfoDto(
			seriesId,
			category,
			country,
			releaseYear,
			quantity,
			perforated
		);
	}
	
	/* default */ static SeriesInGalleryDto forSeriesInGalleryDto(ResultSet rs, int unused)
		throws SQLException {
		
		Integer seriesId       = rs.getInt("id");
		Integer releaseYear    = JdbcUtils.getInteger(rs, "release_year");
		Integer quantity       = rs.getInt("quantity");
		Boolean perforated     = rs.getBoolean("perforated");
		Integer previewId      = JdbcUtils.getInteger(rs, "preview_id");
		Integer numberOfImages = rs.getInt("number_of_images");
		String category        = rs.getString("category");
		
		return new SeriesInGalleryDto(
			seriesId,
			releaseYear,
			quantity,
			perforated,
			previewId,
			numberOfImages,
			category
		);
	}

	/* default */ static SeriesFullInfoDto forSeriesFullInfoDto(ResultSet rs, int unused)
		throws SQLException {
		
		Integer seriesId     = rs.getInt("id");
		Integer releaseDay   = JdbcUtils.getInteger(rs, "release_day");
		Integer releaseMonth = JdbcUtils.getInteger(rs, "release_month");
		Integer releaseYear  = JdbcUtils.getInteger(rs, "release_year");
		Integer quantity     = rs.getInt("quantity");
		Boolean perforated   = rs.getBoolean("perforated");
		String comment       = rs.getString("comment");
		Integer createdBy    = rs.getInt("created_by");
		
		BigDecimal michelPrice   = rs.getBigDecimal("michel_price");
		BigDecimal scottPrice    = rs.getBigDecimal("scott_price");
		BigDecimal yvertPrice    = rs.getBigDecimal("yvert_price");
		BigDecimal gibbonsPrice  = rs.getBigDecimal("gibbons_price");
		BigDecimal solovyovPrice = rs.getBigDecimal("solovyov_price");
		BigDecimal zagorskiPrice = rs.getBigDecimal("zagorski_price");
		
		LinkEntityDto category =
			createLinkEntityDto(rs, "category_id", "category_slug", "category_name");
		
		LinkEntityDto country =
			createLinkEntityDto(rs, "country_id", "country_slug", "country_name");
		
		return new SeriesFullInfoDto(
			seriesId,
			category,
			country,
			releaseDay,
			releaseMonth,
			releaseYear,
			quantity,
			perforated,
			comment,
			createdBy,
			michelPrice,
			scottPrice,
			yvertPrice,
			gibbonsPrice,
			solovyovPrice,
			zagorskiPrice
		);
	}
	
}
