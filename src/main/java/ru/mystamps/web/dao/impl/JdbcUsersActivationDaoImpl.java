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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.Validate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.dao.JdbcUsersActivationDao;
import ru.mystamps.web.entity.UsersActivation;

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
@RequiredArgsConstructor
public class JdbcUsersActivationDaoImpl implements JdbcUsersActivationDao {
	
	private static final RowMapper<UsersActivation> USERS_ACTIVATION_ROW_MAPPER =
		new UsersActivationRowMapper();
	
	private final NamedParameterJdbcTemplate jdbcTemplate;
	
	@Value("${users_activation.find_by_activation_key}")
	private String findByActivationKeySql;
	
	@Value("${users_activation.count_by_activation_key}")
	private String countByActivationKeySql;
	
	@Value("${users_activation.remove_by_activation_key}")
	private String removeByActivationKeySql;
	
	@Value("${users_activation.create}")
	private String addActivationKeySql;
	
	@Override
	public UsersActivation findByActivationKey(String activationKey) {
		return jdbcTemplate.queryForObject(
			findByActivationKeySql,
			Collections.singletonMap("activation_key", activationKey),
			USERS_ACTIVATION_ROW_MAPPER
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
	public void add(UsersActivation activation) {
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
