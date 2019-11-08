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

package ru.mystamps.web.feature.category;

import org.assertj.core.api.WithAssertions;
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

@JdbcTest
// @todo #1143 Introduce a dedicated config for DAO classes
@ContextConfiguration(classes = CategoryConfig.Services.class)
@TestPropertySource(
	properties = {
		// don't load test data, start with an empty database
		"liquibase.contexts=scheme,init-data",
		// overrides settings from application-test.properties to keep the console clean,
		// comment this out when you need to debug tests. See also logback-test.xml
		"logging.level.=WARN", "logging.level.ru.mystamps=WARN"
	},
	// @todo #1143 Improve a way of importing properties files in the tests
	locations = "/sql/category_dao_queries.properties")
@RunWith(SpringRunner.class)
public class JdbcCategoryDaoTest implements WithAssertions {
	
	@Autowired
	private CategoryDao categoryDao;
	
	//
	// Tests for getStatisticsOf()
	//
	
	@Test
	public void getStatisticsOfWithEmptyCollection() {
		// given
		// when
		Map<String, Integer> statistics = categoryDao.getStatisticsOf(Random.id(), Random.lang());
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
			"/db/categories-fauna.sql",
			"/db/series-1-fauna-qty5.sql",
			"/db/series-2-sport-qty3.sql",
			"/db/series-3-sport-qty7.sql"
		},
		statements = {
			"INSERT INTO collections_series(collection_id, series_id, number_of_stamps) "
				+ "VALUES (1, 1, 5), (1, 2, 3), (1, 3, 7)"
		}
	)
	public void getStatisticsOfWithSeriesWithAllStamps() {
		// given
		final Integer expectedStampsInFaunaCategory = 5;
		final Integer expectedStampsInSportCategory = 10;
		// when
		Map<String, Integer> statistics = categoryDao.getStatisticsOf(1, "en");
		// then
		assertThat(statistics)
			.containsEntry("Fauna", expectedStampsInFaunaCategory)
			.containsEntry("Sport", expectedStampsInSportCategory)
			.hasSize(2);
	}
	
	@Test
	@Sql(
		scripts = {
			"/db/users-coder.sql",
			"/db/collections-coder.sql",
			"/db/categories-fauna.sql",
			"/db/series-1-fauna-qty5.sql",
		},
		statements = {
			"INSERT INTO collections_series(collection_id, series_id, number_of_stamps) "
				+ "VALUES (1, 1, 2)"
		}
	)
	public void getStatisticsOfWithIncompleteSeries() {
		// given
		// when
		Map<String, Integer> statistics = categoryDao.getStatisticsOf(1, "en");
		// then
		assertThat(statistics).containsEntry("Fauna", 2);
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
	public void getStatisticsOfInRussian() {
		// given
		// when
		Map<String, Integer> statisticsInRussian = categoryDao.getStatisticsOf(1, "ru");
		// then
		assertThat(statisticsInRussian).containsKeys("Фауна");
	}
	
	@Test
	@Sql(
		scripts = {
			"/db/users-coder.sql",
			"/db/collections-coder.sql",
			"/db/categories-sport.sql",
			"/db/series-2-sport-qty3.sql",
		},
		statements = {
			"INSERT INTO collections_series(collection_id, series_id, number_of_stamps) "
				+ "VALUES (1, 2, 3)"
		}
	)
	public void getStatisticsOfInRussianWithFallbackToEnglish() {
		// given
		// when
		Map<String, Integer> statistics = categoryDao.getStatisticsOf(1, "ru");
		// then
		assertThat(statistics).containsKey("Sport");
	}
	
	@Test
	@Sql(
		scripts = {
			"/db/users-coder.sql",
			"/db/collections-coder.sql",
			"/db/categories-fauna.sql",
			"/db/series-1-fauna-qty5.sql",
		},
		statements = {
			"INSERT INTO collections_series(collection_id, series_id, number_of_stamps) "
				+ "VALUES (1, 1, 5)"
		}
	)
	public void getStatisticsOfInUnsupportedLanguageWithFallbackToEnglish() {
		// given
		// when
		Map<String, Integer> statistics = categoryDao.getStatisticsOf(1, "fr");
		// then
		assertThat(statistics).containsKey("Fauna");
	}
	
}
