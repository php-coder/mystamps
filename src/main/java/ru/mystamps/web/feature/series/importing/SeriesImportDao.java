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

import java.util.Date;
import java.util.List;

public interface SeriesImportDao {
	Integer add(ImportSeriesDbDto importRequest);
	void setSeriesIdAndChangeStatus(Integer seriesId, UpdateImportRequestStatusDbDto requestStatus);
	void changeStatus(UpdateImportRequestStatusDbDto requestStatus);
	ImportRequestDto findById(Integer id);
	void addRawContent(Integer requestId, Date createdAt, Date updatedAt, String content);
	String findRawContentByRequestId(Integer requestId);
	void addParsedData(Integer requestId, AddSeriesParsedDataDbDto data);
	SeriesParsedDataDto findParsedDataByRequestId(Integer requestId, String lang);
	ImportRequestInfo findRequestInfo(Integer seriesId);
	List<ImportRequestFullInfo> findAll();
}
