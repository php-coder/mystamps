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
package ru.mystamps.web.support.spring.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class MapStringIntegerResultSetExtractor
	implements ResultSetExtractor<Map<String, Integer>> {
	
	private final String keyFieldName;
	private final String valueFieldName;
	
	@Override
	public Map<String, Integer> extractData(ResultSet rs)
		throws SQLException, DataAccessException {
		
		Map<String, Integer> result = new HashMap<>();
		
		while (rs.next()) {
			String key = rs.getString(keyFieldName);
			// NOTE: when value is NULL then 0 is returned
			Integer value = rs.getInt(valueFieldName);
			result.put(key, value);
		}
		
		return result;
	}
	
}
