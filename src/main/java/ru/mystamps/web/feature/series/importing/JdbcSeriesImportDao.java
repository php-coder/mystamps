/*
 * Copyright (C) 2009-2023 Slava Semushin <slava.semushin@gmail.com>
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

import org.apache.commons.lang3.Validate;
import org.springframework.core.env.Environment;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import ru.mystamps.web.common.JdbcUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// it complains that "request_id" is present many times
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class JdbcSeriesImportDao implements SeriesImportDao {
	
	private final NamedParameterJdbcTemplate jdbcTemplate;
	private final String createSeriesImportRequestSql;
	private final String setSeriesIdAndChangeStatusSql;
	private final String changeStatusSql;
	private final String findImportRequestByIdSql;
	private final String addRawContentSql;
	private final String findRawContentSql;
	private final String addParsedDataSql;
	private final String addParsedImageUrlSql;
	private final String findParsedDataSql;
	private final String findParsedImageUrlsSql;
	private final String findRequestInfoSql;
	private final String findAllSql;
	
	@SuppressWarnings("checkstyle:linelength")
	public JdbcSeriesImportDao(Environment env, NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate                  = jdbcTemplate;
		this.createSeriesImportRequestSql  = env.getRequiredProperty("series_import_requests.create");
		this.setSeriesIdAndChangeStatusSql = env.getRequiredProperty("series_import_requests.set_series_id_and_change_status");
		this.changeStatusSql               = env.getRequiredProperty("series_import_requests.change_status");
		this.findImportRequestByIdSql      = env.getRequiredProperty("series_import_requests.find_by_id");
		this.addRawContentSql              = env.getRequiredProperty("series_import_requests.add_raw_content");
		this.findRawContentSql             = env.getRequiredProperty("series_import_requests.find_raw_content_by_request_id");
		this.addParsedDataSql              = env.getRequiredProperty("series_import_requests.add_series_parsed_data");
		this.addParsedImageUrlSql          = env.getRequiredProperty("series_import_requests.add_series_parsed_image_url");
		this.findParsedDataSql             = env.getRequiredProperty("series_import_requests.find_series_parsed_data_by_request_id");
		this.findParsedImageUrlsSql        = env.getRequiredProperty("series_import_requests.find_series_parsed_image_urls_by_request_id");
		this.findRequestInfoSql            = env.getRequiredProperty("series_import_requests.find_request_info_by_series_id");
		this.findAllSql                    = env.getRequiredProperty("series_import_requests.find_all");
	}
	
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
			holder,
			JdbcUtils.ID_KEY_COLUMN
		);
		
		Validate.validState(
			affected == 1,
			"Unexpected number of affected rows after adding an import series request: %d",
			affected
		);
		
		return Integer.valueOf(holder.getKey().intValue());
	}

	// CheckStyle: ignore LineLength for next 2 lines
	@Override
	public void setSeriesIdAndChangeStatus(Integer seriesId, UpdateImportRequestStatusDbDto requestStatus) {
		Map<String, Object> params = new HashMap<>();
		params.put("id", requestStatus.getRequestId());
		params.put("series_id", seriesId);
		params.put("old_status", requestStatus.getOldStatus());
		params.put("new_status", requestStatus.getNewStatus());
		params.put("date", requestStatus.getDate());
		
		int affected = jdbcTemplate.update(setSeriesIdAndChangeStatusSql, params);
		
		Validate.validState(
			affected == 1,
			"Unexpected number of affected rows after setting series id on request #%d: %d",
			requestStatus.getRequestId(),
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
		params.put("release_day", data.getReleaseDay());
		params.put("release_month", data.getReleaseMonth());
		params.put("release_year", data.getReleaseYear());
		params.put("created_at", data.getCreatedAt());
		params.put("updated_at", data.getUpdatedAt());
		params.put("quantity", data.getQuantity());
		params.put("perforated", data.getPerforated());
		params.put("michel_numbers", data.getMichelNumbers());
		
		int affected = jdbcTemplate.update(addParsedDataSql, params);
		
		Validate.validState(
			affected == 1,
			"Unexpected number of affected rows after adding parsed data to request #%d: %d",
			requestId,
			affected
		);
		
		// for backward compatibility
		addParsedImageUrls(requestId, data.getImageUrls());
	}
	
	@Override
	@SuppressWarnings("checkstyle:linelength")
	public void addParsedImageUrls(Integer requestId, List<String> imageUrls) {
		if (imageUrls == null || imageUrls.isEmpty()) {
			return;
		}
		
		// manually construct SqlParameterSource[] instead of using
		// SqlParameterSourceUtils.createBatch() in order to reduce a number of temporary objects.
		// See also: https://www.baeldung.com/spring-jdbc-jdbctemplate#2-batch-operations-using-namedparameterjdbctemplate
		SqlParameterSource[] batchedParams = imageUrls.stream()
			.map(imageUrl -> new MapSqlParameterSource("request_id", requestId).addValue("url", imageUrl))
			.toArray(size -> new SqlParameterSource[size]);
		
		int[] affected = jdbcTemplate.batchUpdate(addParsedImageUrlSql, batchedParams);
		
		Validate.validState(
			affected.length == batchedParams.length,
			"Unexpected number of batches after inserting parsed image urls of request #%d: %d (expected: %d)",
			requestId,
			affected.length,
			batchedParams.length
		);
		
		long affectedRows = Arrays.stream(affected).sum();
		Validate.validState(
			affectedRows == imageUrls.size(),
			"Unexpected number of affected rows after inserting parsed image urls of request #%d: %d (expected: %d)",
			requestId,
			affectedRows,
			imageUrls.size()
		);
	}
	
	@Override
	public SeriesParsedDataDto findParsedDataByRequestId(Integer requestId, String lang) {
		try {
			Map<String, Object> params = new HashMap<>();
			params.put("request_id", requestId);
			params.put("lang", lang);
			
			SeriesParsedDataDto parsedData = jdbcTemplate.queryForObject(
				findParsedDataSql,
				params,
				RowMappers::forSeriesParsedDataDto
			);
			if (parsedData == null) {
				return null;
			}
			
			List<String> imageUrls = jdbcTemplate.queryForList(
				findParsedImageUrlsSql,
				Collections.singletonMap("request_id", requestId),
				String.class
			);
			parsedData.setImageUrls(imageUrls);
			
			return parsedData;
			
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
