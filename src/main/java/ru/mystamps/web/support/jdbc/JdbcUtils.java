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
package ru.mystamps.web.support.jdbc;

import ru.mystamps.web.dao.dto.Currency;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class JdbcUtils {
	
	public static final String[] ID_KEY_COLUMN = new String[]{"id"};
	
	private JdbcUtils() {
	}
	
	// @see http://stackoverflow.com/q/2920364/checking-for-a-null-int-value-from-a-java-resultset
	@SuppressWarnings("PMD.PrematureDeclaration")
	public static Integer getInteger(ResultSet resultSet, String fieldName) throws SQLException {
		int value = resultSet.getInt(fieldName);
		if (resultSet.wasNull()) {
			return null;
		}
		
		return Integer.valueOf(value);
	}
	
	@SuppressWarnings("PMD.PrematureDeclaration")
	public static Boolean getBoolean(ResultSet resultSet, String fieldName) throws SQLException {
		boolean value = resultSet.getBoolean(fieldName);
		if (resultSet.wasNull()) {
			return null;
		}
		
		return Boolean.valueOf(value);
	}
	
	/**
	 * @author Sergey Chechenev
	 */
	@SuppressWarnings("PMD.PrematureDeclaration")
	public static Currency getCurrency(ResultSet resultSet, String fieldName) throws SQLException {
		String value = resultSet.getString(fieldName);
		if (resultSet.wasNull()) {
			return null;
		}
		
		return Currency.valueOf(value);
	}
}
