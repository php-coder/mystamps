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

import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.dao.JdbcImageDataDao;
import ru.mystamps.web.service.dto.DbImageDto;

@RequiredArgsConstructor
public class JdbcImageDataDaoImpl implements JdbcImageDataDao {
	
	private final NamedParameterJdbcTemplate jdbcTemplate;
	
	@Value("${image_data.find_by_image_id}")
	private String findByImageIdSql;
	
	@Override
	public DbImageDto findByImageId(Integer imageId) {
		try {
			return jdbcTemplate.queryForObject(
				findByImageIdSql,
				Collections.singletonMap("image_id", imageId),
				RowMappers::forDbImageDto
			);
		} catch (EmptyResultDataAccessException ex) {
			return null;
		}
	}
	
}
