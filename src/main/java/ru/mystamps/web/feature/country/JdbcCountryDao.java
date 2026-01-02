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
package ru.mystamps.web.feature.country;

import org.apache.commons.lang3.Validate;
import org.springframework.core.env.Environment;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import ru.mystamps.web.common.JdbcUtils;
import ru.mystamps.web.common.LinkEntityDto;
import ru.mystamps.web.common.RowMappers;
import ru.mystamps.web.common.SitemapInfoDto;
import ru.mystamps.web.support.spring.jdbc.MapStringIntegerResultSetExtractor;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JdbcCountryDao implements CountryDao {
	
	private static final ResultSetExtractor<Map<String, Integer>> NAME_COUNTER_EXTRACTOR =
		new MapStringIntegerResultSetExtractor("name", "counter");
	
	private final NamedParameterJdbcTemplate jdbcTemplate;
	private final String addCountrySql;
	private final String countAllSql;
	private final String countBySlugSql;
	private final String countByNameSql;
	private final String countByNameRuSql;
	private final String countCountriesOfCollectionSql;
	private final String countCountriesAddedSinceSql;
	private final String countUntranslatedNamesSinceSql;
	private final String countStampsByCountriesSql;
	private final String findIdsByNamesSql;
	private final String findIdsByNamePatternSql;
	private final String findCountriesNamesWithSlugSql;
	private final String findAllForSitemapSql;
	private final String findCountryLinkEntityBySlugSql;
	private final String findFromLastCreatedSeriesByUserSql;
	private final String findPopularCountryInCollectionSql;
	private final String findLastCountryCreatedByUserSql;
	
	public JdbcCountryDao(Environment env, NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate                       = jdbcTemplate;
		this.addCountrySql                      = env.getRequiredProperty("country.create");
		this.countAllSql                        = env.getRequiredProperty("country.count_all_countries");
		this.countBySlugSql                     = env.getRequiredProperty("country.count_countries_by_slug");
		this.countByNameSql                     = env.getRequiredProperty("country.count_countries_by_name");
		this.countByNameRuSql                   = env.getRequiredProperty("country.count_countries_by_name_ru");
		this.countCountriesOfCollectionSql      = env.getRequiredProperty("country.count_countries_of_collection");
		this.countCountriesAddedSinceSql        = env.getRequiredProperty("country.count_countries_added_since");
		this.countUntranslatedNamesSinceSql     = env.getRequiredProperty("country.count_untranslated_names_since");
		this.countStampsByCountriesSql          = env.getRequiredProperty("country.count_stamps_by_countries");
		this.findIdsByNamesSql                  = env.getRequiredProperty("country.find_ids_by_names");
		this.findIdsByNamePatternSql            = env.getRequiredProperty("country.find_ids_by_name_pattern");
		this.findCountriesNamesWithSlugSql      = env.getRequiredProperty("country.find_all_countries_names_with_slug");
		this.findAllForSitemapSql               = env.getRequiredProperty("country.find_all_for_sitemap");
		this.findCountryLinkEntityBySlugSql     = env.getRequiredProperty("country.find_country_link_info_by_slug");
		this.findFromLastCreatedSeriesByUserSql = env.getRequiredProperty("country.find_from_last_created_series_by_user");
		this.findPopularCountryInCollectionSql  = env.getRequiredProperty("country.find_popular_country_from_user_collection");
		this.findLastCountryCreatedByUserSql    = env.getRequiredProperty("country.find_last_country_created_by_user");
	}
	
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
			holder,
			JdbcUtils.ID_KEY_COLUMN
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
	public Map<String, Integer> getStatisticsOf(Integer collectionId, String lang) {
		Map<String, Object> params = new HashMap<>();
		params.put("collection_id", collectionId);
		params.put("lang", lang);
		
		return jdbcTemplate.query(
			countStampsByCountriesSql,
			params,
			NAME_COUNTER_EXTRACTOR
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
	public List<SitemapInfoDto> findAllForSitemap() {
		return jdbcTemplate.query(
			findAllForSitemapSql,
			Collections.emptyMap(),
			RowMappers::forSitemapInfoDto
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
