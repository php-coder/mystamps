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
package ru.mystamps.web.feature.series.importing;

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

// it complains that "request_id" is present many times
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
@RequiredArgsConstructor
public class JdbcSeriesImportDao implements SeriesImportDao {
	
	private final NamedParameterJdbcTemplate jdbcTemplate;
	
	@Value("${series_import_requests.create}")
	private String createSeriesImportRequestSql;
	
	@Value("${series_import_requests.set_series_id_and_change_status}")
	private String setSeriesIdAndChangeStatusSql;
	
	@Value("${series_import_requests.change_status}")
	private String changeStatusSql;
	
	@Value("${series_import_requests.find_by_id}")
	private String findImportRequestByIdSql;
	
	@Value("${series_import_requests.add_raw_content}")
	private String addRawContentSql;
	
	@Value("${series_import_requests.find_raw_content_by_request_id}")
	private String findRawContentSql;
	
	@Value("${series_import_requests.add_series_parsed_data}")
	private String addParsedDataSql;
	
	@Value("${series_import_requests.find_series_parsed_data_by_request_id}")
	private String findParsedDataSql;
	
	@Value("${series_import_requests.find_request_info_by_series_id}")
	private String findRequestInfoSql;
	
	@Value("${series_import_requests.find_all}")
	private String findAllSql;
	
	@Override
	public Integer add(ImportSeriesDbDto importRequest) {
		Map<String, Object> params = new HashMap<>();
		params.put("url", importRequest.getUrl());
		params.put("status", importRequest.getStatus());
		params.put("updated_at", importRequest.getUpdatedAt());
		params.put("requested_at", importRequest.getRequestedAt());
		params.put("requested_by", importRequest.getRequestedBy());
		
		KeyHolder holder = new GeneratedKeyHolder();
		
		int affected = jdbcTemplate.update(
			createSeriesImportRequestSql,
			new MapSqlParameterSource(params),
			holder
		);
		
		Validate.validState(
			affected == 1,
			"Unexpected number of affected rows after adding an import series request: %d",
			affected
		);
		
		return Integer.valueOf(holder.getKey().intValue());
	}
	
	// @todo #735 SeriesImportDao.setSeriesIdAndChangeStatus(): replace arguments by dto object
	@Override
	public void setSeriesIdAndChangeStatus(
		Integer requestId,
		Integer seriesId,
		String oldStatus,
		String newStatus,
		Date updatedAt) {
		
		Map<String, Object> params = new HashMap<>();
		params.put("id", requestId);
		params.put("series_id", seriesId);
		params.put("old_status", oldStatus);
		params.put("new_status", newStatus);
		params.put("date", updatedAt);
		
		int affected = jdbcTemplate.update(setSeriesIdAndChangeStatusSql, params);
		
		Validate.validState(
			affected == 1,
			"Unexpected number of affected rows after setting series id on request #%d: %d",
			requestId,
			affected
		);
	}
	
	@Override
	public void changeStatus(UpdateImportRequestStatusDbDto requestStatus) {
		Map<String, Object> params = new HashMap<>();
		params.put("id", requestStatus.getRequestId());
		params.put("date", requestStatus.getDate());
		params.put("old_status", requestStatus.getOldStatus());
		params.put("new_status", requestStatus.getNewStatus());
		
		int affected = jdbcTemplate.update(changeStatusSql, params);
		
		Validate.validState(
			affected == 1,
			"Unexpected number of affected rows after updating status of request #%d: %d",
			requestStatus.getRequestId(),
			affected
		);
	}
	
	@Override
	public ImportRequestDto findById(Integer id) {
		try {
			return jdbcTemplate.queryForObject(
				findImportRequestByIdSql,
				Collections.singletonMap("id", id),
				RowMappers::forImportRequestDto
			);
		} catch (EmptyResultDataAccessException ignored) {
			return null;
		}
	}
	
	// @todo #660 JdbcSeriesImportDao.addRawContent(): introduce dao
	@Override
	public void addRawContent(Integer requestId, Date createdAt, Date updatedAt, String content) {
		Map<String, Object> params = new HashMap<>();
		params.put("request_id", requestId);
		params.put("created_at", createdAt);
		params.put("updated_at", updatedAt);
		params.put("content", content);
		
		KeyHolder holder = new GeneratedKeyHolder();
		
		int affected = jdbcTemplate.update(
			addRawContentSql,
			new MapSqlParameterSource(params),
			holder
		);
		
		Validate.validState(
			affected == 1,
			"Unexpected number of affected rows after adding raw content to request #%d: %d",
			requestId,
			affected
		);
	}
	
	@Override
	public String findRawContentByRequestId(Integer requestId) {
		try {
			return jdbcTemplate.queryForObject(
				findRawContentSql,
				Collections.singletonMap("request_id", requestId),
				String.class
			);
		} catch (EmptyResultDataAccessException ignored) {
			return null;
		}
	}
	
	@Override
	public void addParsedData(Integer requestId, AddSeriesParsedDataDbDto data) {
		Map<String, Object> params = new HashMap<>();
		params.put("request_id", requestId);
		params.put("category_id", data.getCategoryId());
		params.put("country_id", data.getCountryId());
		params.put("image_url", data.getImageUrl());
		params.put("release_year", data.getReleaseYear());
		params.put("created_at", data.getCreatedAt());
		params.put("updated_at", data.getUpdatedAt());
		params.put("quantity", data.getQuantity());
		params.put("perforated", data.getPerforated());
		params.put("michel_numbers", data.getMichelNumbers());
		
		KeyHolder holder = new GeneratedKeyHolder();
		
		int affected = jdbcTemplate.update(
			addParsedDataSql,
			new MapSqlParameterSource(params),
			holder
		);
		
		Validate.validState(
			affected == 1,
			"Unexpected number of affected rows after adding parsed data to request #%d: %d",
			requestId,
			affected
		);
	}
	
	@Override
	public SeriesParsedDataDto findParsedDataByRequestId(Integer requestId, String lang) {
		try {
			Map<String, Object> params = new HashMap<>();
			params.put("request_id", requestId);
			params.put("lang", lang);
			
			return jdbcTemplate.queryForObject(
				findParsedDataSql,
				params,
				RowMappers::forSeriesParsedDataDto
			);
			
		} catch (EmptyResultDataAccessException ignored) {
			return null;
		}
	}
	
	@Override
	public ImportRequestInfo findRequestInfo(Integer seriesId) {
		try {
			return jdbcTemplate.queryForObject(
				findRequestInfoSql,
				Collections.singletonMap("series_id", seriesId),
				RowMappers::forImportRequestInfo
			);

		} catch (EmptyResultDataAccessException ignored) {
			return null;
		}
	}
	
	@Override
	public List<ImportRequestFullInfo> findAll() {
		return jdbcTemplate.query(
			findAllSql,
			Collections.emptyMap(),
			RowMappers::forImportRequestFullInfo
		);
	}
	
}
