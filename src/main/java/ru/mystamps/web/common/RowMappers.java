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
package ru.mystamps.web.common;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class RowMappers {
	
	private RowMappers() {
	}
	
	public static LinkEntityDto forLinkEntityDto(ResultSet rs, int unused) throws SQLException {
		return createLinkEntityDto(rs, "id", "slug", "name");
	}
	
	public static EntityWithParentDto forEntityWithParentDto(ResultSet rs, int unused)
		throws SQLException {
		
		return new EntityWithParentDto(
			rs.getString("id"),
			rs.getString("name"),
			rs.getString("parent_name")
		);
	}
	
	public static Integer forInteger(ResultSet rs, int unused) throws SQLException {
		return rs.getInt("id");
	}
	
	public static LinkEntityDto createLinkEntityDto(
		ResultSet rs,
		String idColumn,
		String slugColumn,
		String nameColumn)
		throws SQLException {
		
		Integer id = JdbcUtils.getInteger(rs, idColumn);
		if (id == null) {
			return null;
		}
		
		return new LinkEntityDto(
			id,
			rs.getString(slugColumn),
			rs.getString(nameColumn)
		);
	}
	
}
