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

import org.springframework.jdbc.core.RowMapper;

import ru.mystamps.web.entity.UsersActivation;

class UsersActivationRowMapper implements RowMapper<UsersActivation> {
	
	@Override
	public UsersActivation mapRow(ResultSet resultSet, int i) throws SQLException {
		String activationKey  = resultSet.getString("act_key");
		String email          = resultSet.getString("email");
		String lang           = resultSet.getString("lang");
		Date createdAt        = resultSet.getTimestamp("created_at");
		
		return new UsersActivation(activationKey, email, lang, createdAt);
	}
	
}
