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

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import ru.mystamps.web.common.JdbcUtils;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class JdbcUserDao implements UserDao {
	
	private final NamedParameterJdbcTemplate jdbcTemplate;
	
	@Value("${user.count_users_by_login}")
	private String countByLoginSql;
	
	@Value("${user.count_activated_since}")
	private String countActivatedSinceSql;
	
	@Value("${user.find_user_details_by_login}")
	private String findUserDetailsByLoginSql;
	
	@Value("${user.create}")
	private String addUserSql;
	
	@Override
	public long countByLogin(String login) {
		return jdbcTemplate.queryForObject(
			countByLoginSql,
			Collections.singletonMap("login", login),
			Long.class
		);
	}
	
	@Override
	public long countActivatedSince(Date date) {
		return jdbcTemplate.queryForObject(
			countActivatedSinceSql,
			Collections.singletonMap("date", date),
			Long.class
		);
	}
	
	@Override
	public UserDetails findUserDetailsByLogin(String login) {
		try {
			return jdbcTemplate.queryForObject(
				findUserDetailsByLoginSql,
				Collections.singletonMap("login", login),
				RowMappers::forUserDetails
			);
		} catch (EmptyResultDataAccessException ignored) {
			return null;
		}
	}
	
	@Override
	public Integer add(AddUserDbDto user) {
		Map<String, Object> params = new HashMap<>();
		params.put("login", user.getLogin());
		params.put("role", String.valueOf(user.getRole()));
		params.put("name", user.getName());
		params.put("email", user.getEmail());
		params.put("registered_at", user.getRegisteredAt());
		params.put("activated_at", user.getActivatedAt());
		params.put("hash", user.getHash());
		
		KeyHolder holder = new GeneratedKeyHolder();
		
		int affected = jdbcTemplate.update(
			addUserSql,
			new MapSqlParameterSource(params),
			holder,
			JdbcUtils.ID_KEY_COLUMN
		);
		
		Validate.validState(
			affected == 1,
			"Unexpected number of affected rows after creation of user: %d",
			affected
		);
		
		return Integer.valueOf(holder.getKey().intValue());
	}
	
}
