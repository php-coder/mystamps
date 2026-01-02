/*
 * Copyright (C) 2009-2026 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.feature.site;

import org.apache.commons.lang3.Validate;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JdbcSuspiciousActivityDao implements SuspiciousActivityDao {
	
	private final NamedParameterJdbcTemplate jdbcTemplate;
	private final String addSuspiciousActivitySql;
	private final String countAllSql;
	private final String countByTypeSinceSql;
	private final String findAllSql;
	
	public JdbcSuspiciousActivityDao(Environment env, NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate             = jdbcTemplate;
		this.addSuspiciousActivitySql = env.getRequiredProperty("suspicious_activity.create");
		this.countAllSql              = env.getRequiredProperty("suspicious_activity.count_all");
		this.countByTypeSinceSql      = env.getRequiredProperty("suspicious_activity.count_by_type_since");
		this.findAllSql               = env.getRequiredProperty("suspicious_activity.find_all");
	}
	
	@Override
	public void add(AddSuspiciousActivityDbDto activity) {
		Map<String, Object> params = new HashMap<>();
		params.put("type", activity.getType());
		params.put("occurred_at", activity.getOccurredAt());
		params.put("page", activity.getPage());
		params.put("user_id", activity.getUserId());
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
	
	@Override
	public long countAll() {
		return jdbcTemplate.queryForObject(
			countAllSql,
			Collections.<String, Object>emptyMap(),
			Long.class
		);
	}
	
	@Override
	public long countByTypeSince(String type, Date date) {
		Map<String, Object> params = new HashMap<>();
		params.put("type", type);
		params.put("date", date);
		
		return jdbcTemplate.queryForObject(
			countByTypeSinceSql,
			params,
			Long.class
		);
	}
	
	/**
	 * @author Sergey Chechenev
	 * @author Slava Semushin
	 */
	@Override
	public List<SuspiciousActivityDto> findAll(int page, int recordsPerPage) {
		Map<String, Object> params = new HashMap<>();
		params.put("limit", recordsPerPage);
		params.put("offset", (page - 1) * recordsPerPage);
		
		return jdbcTemplate.query(
			findAllSql,
			params,
			RowMappers::forSuspiciousActivityDto
		);
	}
	
}
