/*
 * Copyright (C) 2009-2025 Slava Semushin <slava.semushin@gmail.com>
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

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import ru.mystamps.web.feature.participant.AddParticipantDto;
import ru.mystamps.web.feature.participant.ParticipantService;
import ru.mystamps.web.feature.series.AddSeriesDto;
import ru.mystamps.web.feature.series.CatalogUtils;
import ru.mystamps.web.feature.series.SeriesService;
import ru.mystamps.web.feature.series.importing.SeriesImportDb.SeriesImportParsedData;
import ru.mystamps.web.feature.series.importing.SeriesImportDb.SeriesImportRequestStatus;
import ru.mystamps.web.feature.series.importing.event.ParsingFailed;
import ru.mystamps.web.feature.series.importing.sale.SeriesSalesImportService;
import ru.mystamps.web.feature.series.importing.sale.SeriesSalesParsedDataDbDto;
import ru.mystamps.web.feature.series.sale.AddSeriesSalesDto;
import ru.mystamps.web.feature.series.sale.SeriesCondition;
import ru.mystamps.web.feature.series.sale.SeriesSalesService;
import ru.mystamps.web.support.spring.security.HasAuthority;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public class SeriesImportServiceImpl implements SeriesImportService {
	
	private final Logger log;
	private final SeriesImportDao seriesImportDao;
	private final SeriesService seriesService;
	private final SeriesSalesService seriesSalesService;
	private final SeriesSalesImportService seriesSalesImportService;
	private final ParticipantService participantService;
	private final ApplicationEventPublisher eventPublisher;
	
	@Override
	@Transactional
	@PreAuthorize(HasAuthority.IMPORT_SERIES)
	public Integer addRequest(RequestImportDto dto, Integer userId) {
		Validate.isTrue(dto != null, "DTO must be non null");
		Validate.isTrue(dto.getUrl() != null, "URL must be non null");
		Validate.isTrue(userId != null, "User id must be non null");
		
		ImportSeriesDbDto importRequest = new ImportSeriesDbDto();
		importRequest.setStatus(SeriesImportRequestStatus.UNPROCESSED);
		
		try {
			String encodedUrl = new URI(dto.getUrl()).toASCIIString();
			importRequest.setUrl(encodedUrl);
		} catch (URISyntaxException ex) {
			throw new RuntimeException(ex);
		}
		
		Date now = new Date();
		importRequest.setUpdatedAt(now);
		importRequest.setRequestedAt(now);
		importRequest.setRequestedBy(userId);
		
		Integer id = seriesImportDao.add(importRequest);
		
		log.info("Request #{} for importing series from '{}' has been created", id, dto.getUrl());
		
		return id;
	}
	
	@Override
	@Transactional
	@PreAuthorize(HasAuthority.IMPORT_SERIES)
	public Integer addSeries(
		AddSeriesDto dto,
		AddParticipantDto sellerDto,
		AddSeriesSalesDto saleDto,
		Integer requestId,
		Integer userId) {
		
		Integer seriesId = seriesService.add(dto, userId);
		
		if (saleDto != null) {
			if (saleDto.getSellerId() == null && sellerDto != null) {
				Integer sellerId = participantService.add(sellerDto);
				saleDto.setSellerId(sellerId);
			}
			seriesSalesService.add(saleDto, seriesId, userId);
		}
		
		Date now = new Date();
		UpdateImportRequestStatusDbDto status = new UpdateImportRequestStatusDbDto(
			requestId,
			now,
			SeriesImportRequestStatus.PARSING_SUCCEEDED,
			SeriesImportRequestStatus.IMPORT_SUCCEEDED
		);
		
		seriesImportDao.setSeriesIdAndChangeStatus(seriesId, status);
		
		return seriesId;
	}
	
	@Override
	@Transactional
	public void changeStatus(Integer requestId, String oldStatus, String newStatus) {
		Validate.isTrue(requestId != null, "Request id must be non null");
		Validate.isTrue(StringUtils.isNotBlank(oldStatus), "Old status must be non-blank");
		Validate.isTrue(StringUtils.isNotBlank(newStatus), "New status must be non-blank");
		Validate.isTrue(!oldStatus.equals(newStatus), "Statuses must be different");
		
		Date now = new Date();
		UpdateImportRequestStatusDbDto status =
			new UpdateImportRequestStatusDbDto(requestId, now, oldStatus, newStatus);
		
		seriesImportDao.changeStatus(status);
	}
	
	@Override
	@Transactional(readOnly = true)
	@PreAuthorize(HasAuthority.IMPORT_SERIES)
	public ImportRequestDto findById(Integer requestId) {
		Validate.isTrue(requestId != null, "Request id must be non null");
		
		return seriesImportDao.findById(requestId);
	}
	
	@Override
	@Transactional
	public void saveDownloadedContent(Integer requestId, String content, boolean retry) {
		Validate.isTrue(requestId != null, "Request id must be non null");
		Validate.isTrue(StringUtils.isNotBlank(content), "Content must be non-blank");
		
		Date now = new Date();
		
		seriesImportDao.addRawContent(requestId, now, now, content);
		
		log.info("Request #{}: page were downloaded ({} characters)", requestId, content.length());
		
		String oldStatus = retry
			? SeriesImportRequestStatus.DOWNLOADING_FAILED
			: SeriesImportRequestStatus.UNPROCESSED;
		
		changeStatus(requestId, oldStatus, SeriesImportRequestStatus.DOWNLOADING_SUCCEEDED);
	}
	
	@Override
	@Transactional(readOnly = true)
	public String getDownloadedContent(Integer requestId) {
		Validate.isTrue(requestId != null, "Request id must be non null");
		
		return seriesImportDao.findRawContentByRequestId(requestId);
	}
	
	@Override
	@Transactional
	public void saveParsedData(Integer requestId, SeriesExtractedInfo seriesInfo, List<String> imageUrls) {
		Validate.isTrue(requestId != null, "Request id must be non null");
		Validate.isTrue(seriesInfo != null, "Series info must be non null");
		Validate.isTrue(imageUrls != null, "Image URLs must be non null");
		
		Integer categoryId = getFirstElement(seriesInfo.getCategoryIds());
		Integer countryId = getFirstElement(seriesInfo.getCountryIds());
		
		AddSeriesParsedDataDbDto seriesParsedData = new AddSeriesParsedDataDbDto();
		seriesParsedData.setImageUrls(imageUrls);
		Date now = new Date();
		seriesParsedData.setCreatedAt(now);
		seriesParsedData.setUpdatedAt(now);
		seriesParsedData.setCategoryId(categoryId);
		seriesParsedData.setCountryId(countryId);
		seriesParsedData.setReleaseDay(seriesInfo.getReleaseDay());
		seriesParsedData.setReleaseMonth(seriesInfo.getReleaseMonth());
		seriesParsedData.setReleaseYear(seriesInfo.getReleaseYear());
		seriesParsedData.setQuantity(seriesInfo.getQuantity());
		seriesParsedData.setPerforated(seriesInfo.getPerforated());
		
		Set<String> michelNumbers = seriesInfo.getMichelNumbers();
		if (!michelNumbers.isEmpty()) {
			String shortenedNumbers = CatalogUtils.toShortForm(michelNumbers);
			Validate.validState(
				shortenedNumbers.length() <= SeriesImportParsedData.MICHEL_NUMBERS_LENGTH,
				"Michel numbers (%s) length exceeds max length of the field (%d)",
				shortenedNumbers,
				SeriesImportParsedData.MICHEL_NUMBERS_LENGTH
			);
			seriesParsedData.setMichelNumbers(shortenedNumbers);
		}
		
		SeriesSalesParsedDataDbDto seriesSalesParsedData = new SeriesSalesParsedDataDbDto();
		seriesSalesParsedData.setCreatedAt(now);
		seriesSalesParsedData.setUpdatedAt(now);
		seriesSalesParsedData.setSellerId(seriesInfo.getSellerId());
		seriesSalesParsedData.setSellerGroupId(seriesInfo.getSellerGroupId());
		seriesSalesParsedData.setSellerName(seriesInfo.getSellerName());
		seriesSalesParsedData.setSellerUrl(seriesInfo.getSellerUrl());
		
		BigDecimal price = seriesInfo.getPrice();
		if (price != null) {
			seriesSalesParsedData.setPrice(price);
			seriesSalesParsedData.setCurrency(seriesInfo.getCurrency());
		}
		
		BigDecimal altPrice = seriesInfo.getAltPrice();
		if (altPrice != null) {
			seriesSalesParsedData.setAltPrice(altPrice);
			seriesSalesParsedData.setAltCurrency(seriesInfo.getAltCurrency());
		}
		
		SeriesCondition condition = seriesInfo.getCondition();
		if (condition != null) {
			seriesSalesParsedData.setCondition(condition.toString());
		}
		
		// IMPORTANT: don't add code that modifies database above this line!
		// @todo #684 Series import: add integration test
		//  for the case when parsed value doesn't match database
		if (!seriesParsedData.hasAtLeastOneFieldFilled()) {
			eventPublisher.publishEvent(new ParsingFailed(this, requestId));
			return;
		}
		
		seriesImportDao.addParsedData(requestId, seriesParsedData);
		
		// @todo #695 Remove hasAtLeastOneFieldFilled() methods from DTOs
		if (seriesSalesParsedData.hasAtLeastOneFieldFilled()) {
			seriesSalesImportService.saveParsedData(requestId, seriesSalesParsedData);
		}
		
		log.info(
			"Request #{}: page were parsed ({}, {})",
			requestId,
			seriesParsedData,
			seriesSalesParsedData
		);
		
		changeStatus(
			requestId,
			SeriesImportRequestStatus.DOWNLOADING_SUCCEEDED,
			SeriesImportRequestStatus.PARSING_SUCCEEDED
		);
	}
	
	@Override
	@Transactional(readOnly = true)
	public SeriesParsedDataDto getParsedData(Integer requestId, String lang) {
		Validate.isTrue(requestId != null, "Request id must be non null");
		
		return seriesImportDao.findParsedDataByRequestId(requestId, lang);
	}
	
	@Override
	@Transactional(readOnly = true)
	@PreAuthorize(HasAuthority.IMPORT_SERIES)
	public ImportRequestInfo findRequestInfo(Integer seriesId) {
		Validate.isTrue(seriesId != null, "Series id must be non null");
		
		return seriesImportDao.findRequestInfo(seriesId);
	}
	
	@Override
	@Transactional(readOnly = true)
	@PreAuthorize(HasAuthority.IMPORT_SERIES)
	public List<ImportRequestFullInfo> findAll() {
		return seriesImportDao.findAll();
	}
	
	private static <T> T getFirstElement(List<T> list) {
		if (list.isEmpty()) {
			return null;
		}
		return list.get(0);
	}
	
}
