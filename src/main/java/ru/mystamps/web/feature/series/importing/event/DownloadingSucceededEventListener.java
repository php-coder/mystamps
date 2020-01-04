/*
 * Copyright (C) 2009-2020 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.feature.series.importing.event;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import ru.mystamps.web.feature.series.importing.RawParsedDataDto;
import ru.mystamps.web.feature.series.importing.SeriesExtractedInfo;
import ru.mystamps.web.feature.series.importing.SeriesImportService;
import ru.mystamps.web.feature.series.importing.SeriesInfoExtractorService;
import ru.mystamps.web.feature.series.importing.extractor.SeriesInfo;
import ru.mystamps.web.feature.series.importing.extractor.SiteParser;
import ru.mystamps.web.feature.series.importing.extractor.SiteParserService;

import javax.annotation.PostConstruct;

/**
 * Listener of the {@link DownloadingSucceeded} event.
 *
 * Gets the content of a downloaded file from database, finds appropriate site parser and passes
 * the content to that parser. When it couldn't extract meaningful data from page content,
 * the listener publishes {@link ParsingFailed} event. Otherwise the extracted information is saved
 * to database and the listeners changes request status to 'ParsingSucceeded'.
 *
 * @see ParsingFailedEventListener
 */
@RequiredArgsConstructor
public class DownloadingSucceededEventListener
	implements ApplicationListener<DownloadingSucceeded> {
	
	// CheckStyle: ignore LineLength for next 1 line
	private static final Logger LOG = LoggerFactory.getLogger(DownloadingSucceededEventListener.class);
	
	private final SeriesImportService seriesImportService;
	private final SiteParserService siteParserService;
	private final SeriesInfoExtractorService extractorService;
	private final ApplicationEventPublisher eventPublisher;
	
	@PostConstruct
	public void init() {
		LOG.info("Registered site parsers: {}", siteParserService.findParserNames());
	}
	
	@Override
	public void onApplicationEvent(DownloadingSucceeded event) {
		Integer requestId = event.getRequestId();
		
		LOG.info("Request #{}: downloading succeeded", requestId);
		
		SiteParser parser = siteParserService.findForUrl(event.getUrl());
		if (parser == null) {
			// FIXME: how to handle error? maybe publish UnexpectedErrorEvent?
			LOG.error("Request #{}: could not find appropriate parser", requestId);
			return;
		}
		
		String content = seriesImportService.getDownloadedContent(requestId);
		if (content == null) {
			// FIXME: how to handle error? maybe publish UnexpectedErrorEvent?
			LOG.error("Request #{}: could not load a content from database", requestId);
			return;
		}
		
		SeriesInfo info = parser.parse(content);
		if (info.isEmpty()) {
			eventPublisher.publishEvent(new ParsingFailed(this, requestId));
			return;
		}
		
		RawParsedDataDto data = new RawParsedDataDto(
			info.getCategoryName(),
			info.getCountryName(),
			info.getImageUrl(),
			info.getIssueDate(),
			info.getQuantity(),
			info.getPerforated(),
			info.getMichelNumbers(),
			info.getSellerName(),
			info.getSellerUrl(),
			info.getPrice(),
			info.getCurrency()
		);
		
		SeriesExtractedInfo seriesInfo = extractorService.extract(event.getUrl(), data);
		
		seriesImportService.saveParsedData(requestId, seriesInfo, data.getImageUrl());
	}
	
}
