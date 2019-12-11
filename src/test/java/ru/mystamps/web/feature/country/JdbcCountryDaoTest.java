/*
 * Copyright (C) 2009-2019 Slava Semushin <slava.semushin@gmail.com>
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

import org.assertj.core.api.WithAssertions;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import ru.mystamps.web.tests.Random;

import java.util.Map;

// FIXME: deal with failing tests
@Ignore
@JdbcTest
// LATER: use a single application context with DAOs (see #1150)
@ContextConfiguration(classes = CountryConfig.Services.class)
@TestPropertySource(
	properties = {
		// don't load test data, start with an empty database
		"liquibase.contexts=scheme,init-data",
		// overrides settings from application-test.properties to keep the console clean,
		// comment this out when you need to debug tests. See also logback-test.xml
		"logging.level.=WARN", "logging.level.ru.mystamps=WARN"
	},
	// LATER: find a better way for importing properties files (see #1151)
	locations = "/sql/country_dao_queries.properties")
@RunWith(SpringRunner.class)
public class JdbcCountryDaoTest implements WithAssertions {
	
	@Autowired
	private CountryDao countryDao;
	
	//
	// Tests for countCountriesOfCollection()
	//
	
	@Test
	public void countCountriesOfCollectionWithEmptyCollection() {
		// given
		// when
		long numberOfCountries = countryDao.countCountriesOfCollection(Random.id());
		// then
		assertThat(numberOfCountries).isEqualTo(0);
	}
	
	@Test
	@Sql(
		scripts = {
			"/db/users-coder.sql",
			"/db/collections-coder.sql",
			"/db/categories-sport.sql",
			"/db/countries-italy.sql",
			"/db/countries-france.sql",
			"/db/series-4-italy-qty5.sql",
			"/db/series-5-france-qty4.sql",
			"/db/series-6-france-qty6.sql"
		},
		statements =  {
			"INSERT INTO collections_series(collection_id, series_id, number_of_stamps) "
				+ "VALUES (1, 4, 5), (1, 5, 4), (1, 6, 6)"
		}
	)
	public void countCountriesOfCollectionWithMultipleSeriesFromEachCountry() {
		// given
		// when
		long numberOfCountries = countryDao.countCountriesOfCollection(1);
		// then
		assertThat(numberOfCountries).isEqualTo(2);
	}
	
	@Test
	@Sql(
		scripts = {
			"/db/users-coder.sql",
			"/db/collections-coder.sql",
			"/db/categories-sport.sql",
			"/db/series-2-sport-qty3.sql",
			"/db/series-3-sport-qty7.sql"
		},
		statements =  {
			"INSERT INTO collections_series(collection_id, series_id, number_of_stamps) "
				+ "VALUES (1, 2, 3), (1, 3, 7)"
		}
	)
	public void countCountriesOfCollectionWithSeriesFromUnknownCountries() {
		// as countries are unknown, we assume the pessimistic scenario
		// where all the series belong to the same country
		
		// given
		long expectedNumberOfCountries = 1;
		// when
		long numberOfCountries = countryDao.countCountriesOfCollection(1);
		// then
		assertThat(numberOfCountries).isEqualTo(expectedNumberOfCountries);
	}
	
	//
	// Tests for getStatisticsOf()
	//
	
	@Test
	public void getStatisticsOfWithEmptyCollection() {
		// given
		// when
		Map<String, Integer> statistics = countryDao.getStatisticsOf(Random.id(), Random.lang());
		// then
		assertThat(statistics).isEmpty();
	}
	
	// LATER: extract all "scripts" to a class level. Requires @SqlMergeMode from Spring 5.2
	@Test
	@Sql(
		scripts = {
			"/db/users-coder.sql",
			"/db/collections-coder.sql",
			"/db/categories-sport.sql",
			"/db/countries-italy.sql",
			"/db/countries-france.sql",
			"/db/series-4-italy-qty5.sql",
			"/db/series-5-france-qty4.sql",
			"/db/series-6-france-qty6.sql"
		},
		statements =  {
			"INSERT INTO collections_series(collection_id, series_id, number_of_stamps) "
				+ "VALUES (1, 4, 5), (1, 5, 4), (1, 6, 6)"
		}
	)
	public void getStatisticsOfWithSeriesWithAllStamps() {
		// given
		final Integer expectedStampsFromItaly = 5;
		final Integer expectedStampsFromFrance = 10;
		// when
		Map<String, Integer> statistics = countryDao.getStatisticsOf(1, "en");
		// then
		assertThat(statistics)
			.containsEntry("Italy", expectedStampsFromItaly)
			.containsEntry("France", expectedStampsFromFrance)
			.hasSize(2);
	}
	
	@Test
	@Sql(
		scripts = {
			"/db/users-coder.sql",
			"/db/collections-coder.sql",
			"/db/categories-sport.sql",
			"/db/countries-italy.sql",
			"/db/series-4-italy-qty5.sql"
		},
		statements =  {
			"INSERT INTO collections_series(collection_id, series_id, number_of_stamps) "
				+ "VALUES (1, 4, 2)"
		}
	)
	public void getStatisticsOfWithIncompleteSeries() {
		// given
		// when
		Map<String, Integer> statistics = countryDao.getStatisticsOf(1, "en");
		// then
		assertThat(statistics).containsEntry("Italy", 2);
	}
	
	@Test
	@Sql(
		scripts = {
			"/db/users-coder.sql",
			"/db/collections-coder.sql",
			"/db/categories-fauna.sql",
			"/db/series-1-fauna-qty5.sql"
		},
		statements = {
			"INSERT INTO collections_series(collection_id, series_id, number_of_stamps) "
				+ "VALUES (1, 1, 5)"
		}
	)
	public void getStatisticsOfWithSeriesWithoutCountry() {
		// given
		// when
		Map<String, Integer> statisticsInEnglish = countryDao.getStatisticsOf(1, "en");
		Map<String, Integer> statisticsInRussian = countryDao.getStatisticsOf(1, "ru");
		// then
		assertThat(statisticsInEnglish).containsKeys("Unknown");
		assertThat(statisticsInRussian).containsKeys("Unknown");
	}
	
	@Test
	@Sql(
		scripts = {
			"/db/users-coder.sql",
			"/db/collections-coder.sql",
			"/db/categories-sport.sql",
			"/db/countries-italy.sql",
			"/db/series-4-italy-qty5.sql"
		},
		statements =  {
			"INSERT INTO collections_series(collection_id, series_id, number_of_stamps) "
				+ "VALUES (1, 4, 5)"
		}
	)
	public void getStatisticsOfInRussian() {
		// given
		// when
		Map<String, Integer> statisticsInRussian = countryDao.getStatisticsOf(1, "ru");
		// then
		assertThat(statisticsInRussian).containsKeys("Италия");
	}
	
	@Test
	@Sql(
		scripts = {
			"/db/users-coder.sql",
			"/db/collections-coder.sql",
			"/db/categories-sport.sql",
			"/db/countries-france.sql",
			"/db/series-5-france-qty4.sql"
		},
		statements = {
			"INSERT INTO collections_series(collection_id, series_id, number_of_stamps) "
				+ "VALUES (1, 5, 4)"
		}
	)
	public void getStatisticsOfInRussianWithFallbackToEnglish() {
		// given
		// when
		Map<String, Integer> statistics = countryDao.getStatisticsOf(1, "ru");
		// then
		assertThat(statistics).containsKey("France");
	}
	
	@Test
	@Sql(
		scripts = {
			"/db/users-coder.sql",
			"/db/collections-coder.sql",
			"/db/categories-sport.sql",
			"/db/countries-france.sql",
			"/db/series-5-france-qty4.sql"
		},
		statements = {
			"INSERT INTO collections_series(collection_id, series_id, number_of_stamps) "
				+ "VALUES (1, 5, 4)"
		}
	)
	public void getStatisticsOfInUnsupportedLanguageWithFallbackToEnglish() {
		// given
		// when
		Map<String, Integer> statistics = countryDao.getStatisticsOf(1, "fr");
		// then
		assertThat(statistics).containsKey("France");
	}
	
}
