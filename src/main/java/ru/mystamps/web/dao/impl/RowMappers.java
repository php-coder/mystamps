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
import java.util.Date;

import ru.mystamps.web.entity.UsersActivation;
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
	
	public static UsersActivation forUsersActivation(ResultSet rs, int i) throws SQLException {
		String activationKey  = rs.getString("act_key");
		String email          = rs.getString("email");
		String lang           = rs.getString("lang");
		Date createdAt        = rs.getTimestamp("created_at");
		
		return new UsersActivation(activationKey, email, lang, createdAt);
	}
	
}
