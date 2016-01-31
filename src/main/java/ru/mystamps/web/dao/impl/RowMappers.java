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

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

import ru.mystamps.web.dao.dto.CollectionInfoDto;
import ru.mystamps.web.dao.dto.SeriesFullInfoDto;
import ru.mystamps.web.dao.dto.UsersActivationDto;
import ru.mystamps.web.service.dto.LinkEntityDto;
import ru.mystamps.web.service.dto.SelectEntityDto;
import ru.mystamps.web.service.dto.SeriesInfoDto;
import ru.mystamps.web.service.dto.SitemapInfoDto;
import ru.mystamps.web.service.dto.UrlEntityDto;

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
final class RowMappers {
	
	private RowMappers() {
	}
	
	public static LinkEntityDto forLinkEntityDto(ResultSet rs, int i) throws SQLException {
		return new LinkEntityDto(
			rs.getInt("id"),
			rs.getString("slug"),
			rs.getString("name")
		);
	}
	
	public static UrlEntityDto forUrlEntityDto(ResultSet rs, int i) throws SQLException {
		return new UrlEntityDto(
			rs.getInt("id"),
			rs.getString("slug")
		);
	}
	
	public static Pair<String, Integer> forNameAndCounter(ResultSet rs, int i) throws SQLException {
		return new Pair<>(
			rs.getString("name"),
			JdbcUtils.getInteger(rs, "counter")
		);
	}
	
	public static SitemapInfoDto forSitemapInfoDto(ResultSet rs, int i) throws SQLException {
		return new SitemapInfoDto(
			rs.getInt("id"),
			rs.getTimestamp("updated_at")
		);
	}
	
	public static SelectEntityDto forSelectEntityDto(ResultSet rs, int i) throws SQLException {
		return new SelectEntityDto(
			rs.getInt("id"),
			rs.getString("name")
		);
	}
	
	public static SeriesInfoDto forSeriesInfoDto(ResultSet rs, int i) throws SQLException {
		Integer seriesId     = rs.getInt("id");
		Integer releaseDay   = JdbcUtils.getInteger(rs, "release_day");
		Integer releaseMonth = JdbcUtils.getInteger(rs, "release_month");
		Integer releaseYear  = JdbcUtils.getInteger(rs, "release_year");
		Integer quantity     = rs.getInt("quantity");
		Boolean perforated   = rs.getBoolean("perforated");
		Integer categoryId   = rs.getInt("category_id");
		String categorySlug  = rs.getString("category_slug");
		String categoryName  = rs.getString("category_name");
		Integer countryId    = JdbcUtils.getInteger(rs, "country_id");
		String countrySlug   = rs.getString("country_slug");
		String countryName   = rs.getString("country_name");
		
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
	
	public static SeriesFullInfoDto forSeriesFullInfoDto(ResultSet rs, int i) throws SQLException {
		Integer seriesId     = rs.getInt("id");
		Integer releaseDay   = JdbcUtils.getInteger(rs, "release_day");
		Integer releaseMonth = JdbcUtils.getInteger(rs, "release_month");
		Integer releaseYear  = JdbcUtils.getInteger(rs, "release_year");
		Integer quantity     = rs.getInt("quantity");
		Boolean perforated   = rs.getBoolean("perforated");
		String comment       = rs.getString("comment");
		
		BigDecimal michelPrice = rs.getBigDecimal("michel_price");
		String michelCurrency  = rs.getString("michel_currency");
		
		BigDecimal scottPrice = rs.getBigDecimal("scott_price");
		String scottCurrency  = rs.getString("scott_currency");
		
		BigDecimal yvertPrice = rs.getBigDecimal("yvert_price");
		String yvertCurrency  = rs.getString("yvert_currency");
		
		BigDecimal gibbonsPrice = rs.getBigDecimal("gibbons_price");
		String gibbonsCurrency  = rs.getString("gibbons_currency");
		
		Integer categoryId     = rs.getInt("category_id");
		String categorySlug    = rs.getString("category_slug");
		String categoryName    = rs.getString("category_name");
		LinkEntityDto category = new LinkEntityDto(categoryId, categorySlug, categoryName);
		
		Integer countryId     = JdbcUtils.getInteger(rs, "country_id");
		LinkEntityDto country = null;
		if (countryId != null) {
			String countrySlug = rs.getString("country_slug");
			String countryName = rs.getString("country_name");
			country = new LinkEntityDto(countryId, countrySlug, countryName);
		}
		
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
			michelPrice,
			michelCurrency,
			scottPrice,
			scottCurrency,
			yvertPrice,
			yvertCurrency,
			gibbonsPrice,
			gibbonsCurrency
		);
	}
	
	// CheckStyle: ignore LineLength for next 1 line
	public static UsersActivationDto forUsersActivationDto(ResultSet rs, int i) throws SQLException {
		return new UsersActivationDto(
			rs.getString("email"),
			rs.getTimestamp("created_at")
		);
	}
	
	public static CollectionInfoDto forCollectionInfoDto(ResultSet rs, int i) throws SQLException {
		return new CollectionInfoDto(
			rs.getInt("id"),
			rs.getString("name")
		);
	}
	
}
