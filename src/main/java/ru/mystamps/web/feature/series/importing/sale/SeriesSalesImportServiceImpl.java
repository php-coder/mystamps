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
package ru.mystamps.web.feature.series.importing.sale;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.Validate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import ru.mystamps.web.feature.series.DownloadResult;
import ru.mystamps.web.feature.series.DownloaderService;
import ru.mystamps.web.feature.series.importing.RawParsedDataDto;
import ru.mystamps.web.feature.series.importing.SeriesExtractedInfo;
import ru.mystamps.web.feature.series.importing.SeriesInfoExtractorService;
import ru.mystamps.web.feature.series.importing.extractor.SeriesInfo;
import ru.mystamps.web.feature.series.importing.extractor.SiteParser;
import ru.mystamps.web.feature.series.importing.extractor.SiteParserService;
import ru.mystamps.web.support.spring.security.HasAuthority;

@RequiredArgsConstructor
public class SeriesSalesImportServiceImpl implements SeriesSalesImportService {
	
	private final SeriesSalesImportDao seriesSalesImportDao;
	private final DownloaderService downloaderService;
	private final SiteParserService siteParserService;
	private final SeriesInfoExtractorService extractorService;
	
	// @todo #995 SeriesSalesImportServiceImpl.downloadAndParse(): add unit tests
	@Override
	@Transactional(readOnly = true)
	@PreAuthorize(HasAuthority.IMPORT_SERIES)
	@SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
	public SeriesSaleExtractedInfo downloadAndParse(String url) {
		SiteParser parser = siteParserService.findForUrl(url);
		if (parser == null) {
			throw new RuntimeException("could not find an appropriate parser");
		}
		
		DownloadResult result = downloaderService.download(url);
		if (result.hasFailed()) {
			String message = "could not download: " + result.getCode();
			throw new RuntimeException(message);
		}
		
		String content = result.getDataAsString();
		
		// @todo #995 SiteParser: introduce a method for parsing only sales-related info
		SeriesInfo info = parser.parse(content);
		if (info.isEmpty()) {
			throw new RuntimeException("could not parse the page");
		}
		
		RawParsedDataDto data = new RawParsedDataDto(
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			info.getSellerName(),
			info.getSellerUrl(),
			info.getPrice(),
			info.getCurrency()
		);
		
		// CheckStyle: ignore LineLength for next 1 line
		// @todo #995 SeriesInfoExtractorService: introduce a method for parsing only sales-related info
		SeriesExtractedInfo seriesInfo = extractorService.extract(url, data);
		
		return new SeriesSaleExtractedInfo(
			seriesInfo.getSellerId(),
			seriesInfo.getPrice(),
			seriesInfo.getCurrency()
		);
	}
	
	// @todo #834 SeriesSalesImportServiceImpl.saveParsedData(): introduce dto without dates
	@Override
	@Transactional
	public void saveParsedData(Integer requestId, SeriesSalesParsedDataDbDto data) {
		Validate.isTrue(requestId != null, "Request id must be non null");
		Validate.isTrue(data != null, "Parsed data must be non null");
		
		seriesSalesImportDao.addParsedData(requestId, data);
	}
	
	@Override
	@Transactional(readOnly = true)
	public SeriesSaleParsedDataDto getParsedData(Integer requestId) {
		Validate.isTrue(requestId != null, "Request id must be non null");
		
		return seriesSalesImportDao.findParsedDataByRequestId(requestId);
	}
	
}
