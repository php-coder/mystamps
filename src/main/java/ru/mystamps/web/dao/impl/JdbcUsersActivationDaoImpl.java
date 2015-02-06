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

import javax.sql.DataSource;

import org.apache.commons.lang3.Validate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import ru.mystamps.web.dao.JdbcUsersActivationDao;

public class JdbcUsersActivationDaoImpl implements JdbcUsersActivationDao {
	private final NamedParameterJdbcTemplate jdbcTemplate;
	
	@Value("${users_activation.count_by_activation_key}")
	private String countByActivationKeySql;
	
	@Value("${users_activation.remove_by_activation_key}")
	private String removeByActivationKeySql;
	
	public JdbcUsersActivationDaoImpl(DataSource dataSource) {
		jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
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
	
}
