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

import java.util.List;
import java.util.Set;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import ru.mystamps.web.dao.StampsCatalogDao;

public class JdbcStampsCatalogDao extends JdbcCatalogDao implements StampsCatalogDao {
	
	private final String addCatalogNumberSql;
	private final String addCatalogNumbersToSeriesSql;
	private final String findBySeriesIdSql;
	
	public JdbcStampsCatalogDao(
		NamedParameterJdbcTemplate jdbcTemplate,
		String addCatalogNumberSql,
		String addCatalogNumbersToSeriesSql,
		String findBySeriesIdSql) {
		super(jdbcTemplate);
		this.addCatalogNumberSql = addCatalogNumberSql;
		this.addCatalogNumbersToSeriesSql = addCatalogNumbersToSeriesSql;
		this.findBySeriesIdSql = findBySeriesIdSql;
	}
	
	@Override
	public List<String> add(Set<String> catalogNumbers) {
		return add(catalogNumbers, addCatalogNumberSql);
	}
	
	@Override
	public void addToSeries(Integer seriesId, Set<String> catalogNumbers) {
		addToSeries(seriesId, catalogNumbers, addCatalogNumbersToSeriesSql);
	}
	
	@Override
	public List<String> findBySeriesId(Integer seriesId) {
		return findBySeriesId(seriesId, findBySeriesIdSql);
	}
	
}
