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
package ru.mystamps.web.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.mystamps.web.entity.Category;
import ru.mystamps.web.entity.Country;
import ru.mystamps.web.entity.Series;
import ru.mystamps.web.service.dto.SeriesInfoDto;

public interface SeriesDao extends CrudRepository<Series, Integer> {
	
	@Query("SELECT COUNT(*) FROM Series s INNER JOIN s.michel m WHERE m.code = :michelCode")
	int countByMichelNumberCode(@Param("michelCode") String michelNumberCode);
	
	@Query("SELECT COUNT(*) FROM Series s INNER JOIN s.scott m WHERE m.code = :scottCode")
	int countByScottNumberCode(@Param("scottCode") String scottNumberCode);
	
	@Query("SELECT COUNT(*) FROM Series s INNER JOIN s.yvert m WHERE m.code = :yvertCode")
	int countByYvertNumberCode(@Param("yvertCode") String yvertNumberCode);
	
	@Query("SELECT COUNT(*) FROM Series s INNER JOIN s.gibbons m WHERE m.code = :gibbonsCode")
	int countByGibbonsNumberCode(@Param("gibbonsCode") String gibbonsNumberCode);
	
	@Query(
		"SELECT NEW ru.mystamps.web.service.dto.SeriesInfoDto("
			+ "s.id, "
			+ "cat.id, cat.slug, CASE WHEN (:lang = 'ru') THEN cat.nameRu ELSE cat.name END, "
			+ "c.id, c.slug, CASE WHEN (:lang = 'ru') THEN c.nameRu ELSE c.name END, "
			+ "s.releaseDay, "
			+ "s.releaseMonth, "
			+ "s.releaseYear, "
			+ "s.quantity, "
			+ "s.perforated"
		+ ") "
		+ "FROM Series s "
		+ "JOIN s.category cat "
		+ "LEFT JOIN s.country c "
		+ "WHERE s.category = :category"
	)
	Iterable<SeriesInfoDto> findByAsSeriesInfo(
		@Param("category") Category category,
		@Param("lang") String lang
	);

	@Query(
		"SELECT NEW ru.mystamps.web.service.dto.SeriesInfoDto("
			+ "s.id, "
			+ "cat.id, cat.slug, CASE WHEN (:lang = 'ru') THEN cat.nameRu ELSE cat.name END, "
			+ "c.id, c.slug, CASE WHEN (:lang = 'ru') THEN c.nameRu ELSE c.name END, "
			+ "s.releaseDay, "
			+ "s.releaseMonth, "
			+ "s.releaseYear, "
			+ "s.quantity, "
			+ "s.perforated"
		+ ") "
		+ "FROM Series s "
		+ "JOIN s.category cat "
		+ "LEFT JOIN s.country c "
		+ "WHERE s.country = :country"
	)
	Iterable<SeriesInfoDto> findByAsSeriesInfo(
		@Param("country") Country country,
		@Param("lang") String lang
	);
	
	@Query(
		"SELECT NEW ru.mystamps.web.service.dto.SeriesInfoDto("
			+ "s.id, "
			+ "cat.id, cat.slug, CASE WHEN (:lang = 'ru') THEN cat.nameRu ELSE cat.name END, "
			+ "c.id, c.slug, CASE WHEN (:lang = 'ru') THEN c.nameRu ELSE c.name END, "
			+ "s.releaseDay, "
			+ "s.releaseMonth, "
			+ "s.releaseYear, "
			+ "s.quantity, "
			+ "s.perforated"
		+ ") "
		+ "FROM Collection coll "
		+ "JOIN coll.series s "
		+ "JOIN s.category cat "
		+ "LEFT JOIN s.country c "
		+ "WHERE coll.id = :collectionId"
	)
	Iterable<SeriesInfoDto> findByAsSeriesInfo(
		@Param("collectionId") Integer collectionId,
		@Param("lang") String lang
	);
}
