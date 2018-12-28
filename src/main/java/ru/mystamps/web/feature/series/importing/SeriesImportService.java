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
package ru.mystamps.web.feature.series.importing;

import java.util.List;

import ru.mystamps.web.feature.participant.AddParticipantDto;
import ru.mystamps.web.feature.series.AddSeriesDto;
import ru.mystamps.web.feature.series.sale.AddSeriesSalesDto;

public interface SeriesImportService {
	Integer addRequest(RequestImportDto dto, Integer userId);
	// @todo #695 SeriesImportService.addSeries(): introduce DTO object
	Integer addSeries(
		AddSeriesDto dto,
		AddParticipantDto sellerDto,
		AddSeriesSalesDto sale,
		Integer requestId,
		Integer userId
	);
	void changeStatus(Integer requestId, String oldStatus, String newStatus);
	ImportRequestDto findById(Integer requestId);
	void saveDownloadedContent(Integer requestId, String content);
	String getDownloadedContent(Integer requestId);
	void saveParsedData(Integer requestId, SeriesExtractedInfo seriesInfo, String imageUrl);
	SeriesParsedDataDto getParsedData(Integer requestId, String lang);
	ImportRequestInfo findRequestInfo(Integer seriesId);
	List<ImportRequestFullInfo> findAll();
}
