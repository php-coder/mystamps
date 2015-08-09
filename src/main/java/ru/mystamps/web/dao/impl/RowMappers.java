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

import ru.mystamps.web.entity.UsersActivation;
import ru.mystamps.web.service.dto.LinkEntityDto;
import ru.mystamps.web.service.dto.SelectEntityDto;
import ru.mystamps.web.service.dto.SeriesInfoDto;
import ru.mystamps.web.service.dto.SitemapInfoDto;

final class RowMappers {
	
	private static final RowMapper<LinkEntityDto> LINK_ENTITY_DTO_ROW_MAPPER =
		new LinkEntityDtoRowMapper();
	
	private static final RowMapper<Pair<String, Integer>> NAME_AND_COUNTER_ROW_MAPPER =
		new StringIntegerPairRowMapper("name", "counter");
	
	private static final RowMapper<Pair<String, Integer>> NAME_AND_ID_ROW_MAPPER =
		new StringIntegerPairRowMapper("name", "id");
	
	private static final RowMapper<SitemapInfoDto> SITEMAP_INFO_DTO_ROW_MAPPER =
		new SitemapInfoDtoRowMapper();
	
	private static final RowMapper<SeriesInfoDto> SERIES_INFO_DTO_ROW_MAPPER =
		new SeriesInfoDtoRowMapper();
	
	private static final RowMapper<UsersActivation> USERS_ACTIVATION_ROW_MAPPER =
		new UsersActivationRowMapper();
	
	private RowMappers() {
	}
	
	public static RowMapper<LinkEntityDto> forLinkEntityDto() {
		return LINK_ENTITY_DTO_ROW_MAPPER;
	}
	
	public static RowMapper<Pair<String, Integer>> forNameAndCounter() {
		return NAME_AND_COUNTER_ROW_MAPPER;
	}
	
	public static RowMapper<Pair<String, Integer>> forNameAndId() {
		return NAME_AND_ID_ROW_MAPPER;
	}
	
	public static RowMapper<SitemapInfoDto> forSitemapInfoDto() {
		return SITEMAP_INFO_DTO_ROW_MAPPER;
	}
	
	public static SelectEntityDto forSelectEntityDto(ResultSet rs, int i) throws SQLException {
		return new SelectEntityDto(
			rs.getInt("id"),
			rs.getString("name")
		);
	}
	
	public static RowMapper<SeriesInfoDto> forSeriesInfoDto() {
		return SERIES_INFO_DTO_ROW_MAPPER;
	}
	
	public static RowMapper<UsersActivation> forUsersActivation() {
		return USERS_ACTIVATION_ROW_MAPPER;
	}
	
}
