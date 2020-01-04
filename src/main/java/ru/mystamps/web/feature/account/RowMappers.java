/*
 * Copyright (C) 2009-2020 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.feature.account;

import java.sql.ResultSet;
import java.sql.SQLException;

final class RowMappers {
	
	private RowMappers() {
	}
		
	/* default */ static UsersActivationDto forUsersActivationDto(ResultSet rs, int unused)
		throws SQLException {
		
		return new UsersActivationDto(
			rs.getString("email"),
			rs.getTimestamp("created_at")
		);
	}
	
	/* default */ static UsersActivationFullDto forUsersActivationFullDto(ResultSet rs, int unused)
		throws SQLException {
		
		return new UsersActivationFullDto(
			rs.getString("activation_key"),
			rs.getString("email"),
			rs.getTimestamp("created_at")
		);
	}
	
	/* default */ static UserDetails forUserDetails(ResultSet rs, int unused)
		throws SQLException {
		
		return new UserDetails(
			rs.getInt("id"),
			rs.getString("login"),
			rs.getString("name"),
			rs.getString("hash"),
			UserDetails.Role.valueOf(rs.getString("role")),
			rs.getString("collection_slug")
		);
	}
	
}
