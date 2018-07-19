/*
 * Copyright (C) 2009-2018 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.feature.country;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.dao.dto.LinkEntityDto;
import ru.mystamps.web.support.jdbc.RowMappers;

@RequiredArgsConstructor
@SuppressWarnings({ "PMD.AvoidDuplicateLiterals", "PMD.TooManyMethods", "PMD.TooManyFields" })
public class JdbcCountryDao implements CountryDao {
	
	private final NamedParameterJdbcTemplate jdbcTemplate;
	
	@Value("${country.create}")
	private String addCountrySql;
	
	@Value("${country.count_all_countries}")
	private String countAllSql;
	
	@Value("${country.count_countries_by_slug}")
	private String countBySlugSql;
	
	@Value("${country.count_countries_by_name}")
	private String countByNameSql;
	
	@Value("${country.count_countries_by_name_ru}")
	private String countByNameRuSql;
	
	@Value("${country.count_countries_of_collection}")
	private String countCountriesOfCollectionSql;
	
	@Value("${country.count_countries_added_since}")
	private String countCountriesAddedSinceSql;
	
	@Value("${country.count_untranslated_names_since}")
	private String countUntranslatedNamesSinceSql;
	
	@Value("${country.count_stamps_by_countries}")
	private String countStampsByCountriesSql;
	
	@Value("${country.find_ids_by_names}")
	private String findIdsByNamesSql;
	
	@Value("${country.find_ids_by_name_pattern}")
	private String findIdsByNamePatternSql;
	
	@Value("${country.find_all_countries_names_with_slug}")
	private String findCountriesNamesWithSlugSql;
	
	@Value("${country.find_country_link_info_by_slug}")
	private String findCountryLinkEntityBySlugSql;

	@SuppressWarnings("PMD.LongVariable")
	@Value("${country.find_from_last_created_series_by_user}")
	private String findFromLastCreatedSeriesByUserSql;

	@SuppressWarnings("PMD.LongVariable")
	@Value("${country.find_popular_country_from_user_collection}")
	private String findPopularCountryInCollectionSql;

	@SuppressWarnings("PMD.LongVariable")
	@Value("${country.find_last_country_created_by_user}")
	private String findLastCountryCreatedByUserSql;
	
	@Override
	public Integer add(AddCountryDbDto country) {
		Map<String, Object> params = new HashMap<>();
		params.put("name", country.getName());
		params.put("name_ru", country.getNameRu());
		params.put("slug", country.getSlug());
		params.put("created_at", country.getCreatedAt());
		params.put("created_by", country.getCreatedBy());
		params.put("updated_at", country.getUpdatedAt());
		params.put("updated_by", country.getUpdatedBy());
		
		KeyHolder holder = new GeneratedKeyHolder();
		
		int affected = jdbcTemplate.update(
			addCountrySql,
			new MapSqlParameterSource(params),
			holder
		);
		
		Validate.validState(
			affected == 1,
			"Unexpected number of affected rows after creation of country: %d",
			affected
		);
		
		return Integer.valueOf(holder.getKey().intValue());
	}
	
	@Override
	public long countAll() {
		return jdbcTemplate.queryForObject(countAllSql, Collections.emptyMap(), Long.class);
	}
	
	@Override
	public long countBySlug(String slug) {
		return jdbcTemplate.queryForObject(
			countBySlugSql,
			Collections.singletonMap("slug", slug),
			Long.class
		);
	}
	
	@Override
	public long countByName(String name) {
		return jdbcTemplate.queryForObject(
			countByNameSql,
			Collections.singletonMap("name", name),
			Long.class
		);
	}
	
	@Override
	public long countByNameRu(String name) {
		return jdbcTemplate.queryForObject(
			countByNameRuSql,
			Collections.singletonMap("name", name),
			Long.class
		);
	}
	
	@Override
	public long countCountriesOfCollection(Integer collectionId) {
		return jdbcTemplate.queryForObject(
			countCountriesOfCollectionSql,
			Collections.singletonMap("collection_id", collectionId),
			Long.class
		);
	}
	
	@Override
	public long countAddedSince(Date date) {
		return jdbcTemplate.queryForObject(
			countCountriesAddedSinceSql,
			Collections.singletonMap("date", date),
			Long.class
		);
	}
	
	@Override
	public long countUntranslatedNamesSince(Date date) {
		return jdbcTemplate.queryForObject(
			countUntranslatedNamesSinceSql,
			Collections.singletonMap("date", date),
			Long.class
		);
	}
	
	@Override
	public List<Object[]> getStatisticsOf(Integer collectionId, String lang) {
		Map<String, Object> params = new HashMap<>();
		params.put("collection_id", collectionId);
		params.put("lang", lang);
		
		return jdbcTemplate.query(
			countStampsByCountriesSql,
			params,
			RowMappers::forNameAndCounter
		);
	}
	
	@Override
	public List<Integer> findIdsByNames(List<String> names) {
		return jdbcTemplate.query(
			findIdsByNamesSql,
			Collections.singletonMap("names", names),
			RowMappers::forInteger
		);
	}
	
	@Override
	public List<Integer> findIdsByNamePattern(String pattern) {
		return jdbcTemplate.query(
			findIdsByNamePatternSql,
			Collections.singletonMap("pattern", pattern),
			RowMappers::forInteger
		);
	}
	
	@Override
	public List<LinkEntityDto> findAllAsLinkEntities(String lang) {
		return jdbcTemplate.query(
			findCountriesNamesWithSlugSql,
			Collections.singletonMap("lang", lang),
			RowMappers::forLinkEntityDto
		);
	}
	
	@Override
	public LinkEntityDto findOneAsLinkEntity(String slug, String lang) {
		Map<String, Object> params = new HashMap<>();
		params.put("slug", slug);
		params.put("lang", lang);
		
		try {
			return jdbcTemplate.queryForObject(
				findCountryLinkEntityBySlugSql,
				params,
				RowMappers::forLinkEntityDto
			);
		} catch (EmptyResultDataAccessException ignored) {
			return null;
		}
	}

	/**
	 * @author Shkarin John
	 * @author Slava Semushin
	 */
	@Override
	public String findCountryOfLastCreatedSeriesByUser(Integer userId) {
		try {
			return jdbcTemplate.queryForObject(
				findFromLastCreatedSeriesByUserSql,
				Collections.singletonMap("created_by", userId),
				String.class
			);
		} catch (EmptyResultDataAccessException ignored) {
			return null;
		}
	}

	/**
	 * @author Shkarin John
	 */
	@Override
	public String findPopularCountryInCollection(Integer userId) {
		try {
			return jdbcTemplate.queryForObject(
				findPopularCountryInCollectionSql,
				Collections.singletonMap("user_id", userId),
				String.class
			);
		} catch (EmptyResultDataAccessException ignored) {
			return null;
		}
	}

	/**
	 * @author Shkarin John
	 */
	@Override
	public String findLastCountryCreatedByUser(Integer userId) {
		try {
			return jdbcTemplate.queryForObject(
				findLastCountryCreatedByUserSql,
				Collections.singletonMap("created_by", userId),
				String.class
			);
		} catch (EmptyResultDataAccessException ignored) {
			return null;
		}
	}
	
}
