/*
 * Copyright (C) 2009-2025 Slava Semushin <slava.semushin@gmail.com>
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

import org.apache.commons.lang3.Validate;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JdbcUsersActivationDao implements UsersActivationDao {
	
	private final NamedParameterJdbcTemplate jdbcTemplate;
	private final String findByActivationKeySql;
	private final String findOlderThanDateSql;
	private final String countByActivationKeySql;
	private final String countCreatedSinceSql;
	private final String removeByActivationKeySql;
	private final String addActivationKeySql;
	
	public JdbcUsersActivationDao(Environment env, NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate             = jdbcTemplate;
		this.findByActivationKeySql   = env.getRequiredProperty("users_activation.find_by_activation_key");
		this.findOlderThanDateSql     = env.getRequiredProperty("users_activation.find_older_than");
		this.countByActivationKeySql  = env.getRequiredProperty("users_activation.count_by_activation_key");
		this.countCreatedSinceSql     = env.getRequiredProperty("users_activation.count_created_since");
		this.removeByActivationKeySql = env.getRequiredProperty("users_activation.remove_by_activation_key");
		this.addActivationKeySql      = env.getRequiredProperty("users_activation.create");
	}
	
	@Override
	public UsersActivationDto findByActivationKey(String activationKey) {
		return jdbcTemplate.queryForObject(
			findByActivationKeySql,
			Collections.singletonMap("activation_key", activationKey),
			RowMappers::forUsersActivationDto
		);
	}
	
	@Override
	public List<UsersActivationFullDto> findOlderThan(Date date) {
		return jdbcTemplate.query(
			findOlderThanDateSql,
			Collections.singletonMap("date", date),
			RowMappers::forUsersActivationFullDto
		);
	}
	
	@Override
	public long countByActivationKey(String activationKey) {
		return jdbcTemplate.queryForObject(
			countByActivationKeySql,
			Collections.singletonMap("activation_key", activationKey),
			Long.class
		);
	}
	
	@Override
	public long countCreatedSince(Date date) {
		return jdbcTemplate.queryForObject(
			countCreatedSinceSql,
			Collections.singletonMap("date", date),
			Long.class
		);
	}
	
	@Override
	public void removeByActivationKey(String activationKey) {
		int affected = jdbcTemplate.update(
			removeByActivationKeySql,
			Collections.singletonMap("activation_key", activationKey)
		);
		
		Validate.validState(
			affected == 1,
			"Unexpected number of affected rows after removing activation key '%s': %d",
			activationKey,
			affected
		);
	}
	
	@Override
	public void add(AddUsersActivationDbDto activation) {
		Map<String, Object> params = new HashMap<>();
		params.put("activation_key", activation.getActivationKey());
		params.put("email", activation.getEmail());
		params.put("lang", activation.getLang());
		params.put("created_at", activation.getCreatedAt());
		
		int affected = jdbcTemplate.update(
			addActivationKeySql,
			params
		);
		
		Validate.validState(
			affected == 1,
			"Unexpected number of affected rows after creation of user's activation: %d",
			affected
		);
	}
	
}
