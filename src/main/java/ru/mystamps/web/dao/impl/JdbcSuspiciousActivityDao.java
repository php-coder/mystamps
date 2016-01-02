/*
 * Copyright (C) 2009-2016 Slava Semushin <slava.semushin@gmail.com>
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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.Validate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.dao.SuspiciousActivityDao;
import ru.mystamps.web.entity.SuspiciousActivity;

@RequiredArgsConstructor
public class JdbcSuspiciousActivityDao implements SuspiciousActivityDao {
	
	private final NamedParameterJdbcTemplate jdbcTemplate;
	
	@Value("${suspicious_activity.create}")
	private String addSuspiciousActivitySql;
	
	@Override
	public void add(SuspiciousActivity activity) {
		Map<String, Object> params = new HashMap<>();
		params.put("type", activity.getType().getName());
		params.put("occurred_at", activity.getOccurredAt());
		params.put("page", activity.getPage());
		Integer userId = activity.getUser() != null // NOPMD
			? activity.getUser().getId()
			: null;
		params.put("user_id", userId);
		params.put("ip", activity.getIp());
		params.put("method", activity.getMethod());
		params.put("referer_page", activity.getRefererPage());
		params.put("user_agent", activity.getUserAgent());
		
		int affected = jdbcTemplate.update(
			addSuspiciousActivitySql,
			params
		);
		
		Validate.validState(
			affected == 1,
			"Unexpected number of affected rows after creation of suspicious activity: %d",
			affected
		);
	}
	
}
