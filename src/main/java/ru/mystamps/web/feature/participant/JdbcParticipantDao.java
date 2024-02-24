/*
 * Copyright (C) 2009-2024 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.feature.participant;

import org.apache.commons.lang3.Validate;
import org.springframework.core.env.Environment;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import ru.mystamps.web.common.EntityWithParentDto;
import ru.mystamps.web.common.JdbcUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// The String literal "name" appears 4 times in this file
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class JdbcParticipantDao implements ParticipantDao {
	
	private final NamedParameterJdbcTemplate jdbcTemplate;
	private final String addParticipantSql;
	private final String findBuyersWithParentNamesSql;
	private final String findSellersWithParentNamesSql;
	private final String findSellerIdByNameSql;
	private final String findSellerIdByNameAndUrlSql;
	private final String findAllGroupsSql;
	private final String findGroupIdByNameSql;
	
	public JdbcParticipantDao(Environment env, NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate                  = jdbcTemplate;
		this.addParticipantSql             = env.getRequiredProperty("transaction_participant.create");
		this.findBuyersWithParentNamesSql  = env.getRequiredProperty("transaction_participant.find_buyers_with_parent_names");
		this.findSellersWithParentNamesSql = env.getRequiredProperty("transaction_participant.find_sellers_with_parent_names");
		this.findSellerIdByNameSql         = env.getRequiredProperty("transaction_participant.find_seller_id_by_name");
		this.findSellerIdByNameAndUrlSql   = env.getRequiredProperty("transaction_participant.find_seller_id_by_name_and_url");
		this.findAllGroupsSql              = env.getRequiredProperty("transaction_participant_group.find_all");
		this.findGroupIdByNameSql          = env.getRequiredProperty("transaction_participant_group.find_id_by_name");
	}
	
	@Override
	public Integer add(AddParticipantDbDto participant) {
		Map<String, Object> params = new HashMap<>();
		params.put("name", participant.getName());
		params.put("url", participant.getUrl());
		params.put("group_id", participant.getGroupId());
		params.put("buyer", participant.getBuyer());
		params.put("seller", participant.getSeller());
		
		KeyHolder holder = new GeneratedKeyHolder();
		
		int affected = jdbcTemplate.update(
			addParticipantSql,
			new MapSqlParameterSource(params),
			holder,
			JdbcUtils.ID_KEY_COLUMN
		);
		
		Validate.validState(
			affected == 1,
			"Unexpected number of affected rows after creation of participant: %d",
			affected
		);
		
		return Integer.valueOf(holder.getKey().intValue());
	}
	
	@Override
	public List<EntityWithParentDto> findBuyersWithParents() {
		return jdbcTemplate.query(
			findBuyersWithParentNamesSql,
			ru.mystamps.web.common.RowMappers::forEntityWithParentDto
		);
	}
	
	@Override
	public List<EntityWithParentDto> findSellersWithParents() {
		return jdbcTemplate.query(
			findSellersWithParentNamesSql,
			ru.mystamps.web.common.RowMappers::forEntityWithParentDto
		);
	}
	
	@Override
	public Integer findSellerId(String name) {
		try {
			return jdbcTemplate.queryForObject(
				findSellerIdByNameSql,
				Collections.singletonMap("name", name),
				Integer.class
			);
		} catch (EmptyResultDataAccessException ignored) {
			return null;
		}
	}
	
	@Override
	public Integer findSellerId(String name, String url) {
		Map<String, Object> params = new HashMap<>();
		params.put("name", name);
		params.put("url", url);
		
		try {
			return jdbcTemplate.queryForObject(findSellerIdByNameAndUrlSql, params, Integer.class);
		} catch (EmptyResultDataAccessException ignored) {
			return null;
		}
	}
	
	@Override
	public List<EntityWithIdDto> findAllGroups() {
		return jdbcTemplate.query(findAllGroupsSql, RowMappers::forEntityWithIdDto);
	}
	
	@Override
	public Integer findGroupIdByName(String name) {
		try {
			return jdbcTemplate.queryForObject(
				findGroupIdByNameSql,
				Collections.singletonMap("name", name),
				Integer.class
			);
			
		} catch (EmptyResultDataAccessException ignored) {
			return null;
		}
	}
	
}
