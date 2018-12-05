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
package ru.mystamps.web.feature.series.importing.event;

import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.feature.series.importing.RawParsedDataDto;
import ru.mystamps.web.feature.series.importing.SeriesImportService;
import ru.mystamps.web.feature.series.importing.extractor.JsoupSiteParser;
import ru.mystamps.web.feature.series.importing.extractor.SeriesInfo;
import ru.mystamps.web.feature.series.importing.extractor.SiteParser;
import ru.mystamps.web.feature.series.importing.extractor.SiteParserConfiguration;
import ru.mystamps.web.feature.series.importing.extractor.SiteParserService;

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
	
	private final Logger log;
	private final SeriesImportService seriesImportService;
	private final SiteParserService siteParserService;
	private final List<SiteParser> siteParsers;
	private final ApplicationEventPublisher eventPublisher;
	
	@PostConstruct
	public void init() {
		log.info("Registered site parsers: {}", siteParsers);
		
		// TODO: remove migration logic after finishing migration
		siteParsers.forEach(this::migrateParser);
	}
	
	@Override
	public void onApplicationEvent(DownloadingSucceeded event) {
		Integer requestId = event.getRequestId();
		
		log.info("Request #{}: downloading succeeded", requestId);
		
		String content = seriesImportService.getDownloadedContent(requestId);
		if (content == null) {
			// TODO: how to handle error? maybe publish UnexpectedErrorEvent?
			log.error("Request #{}: could not load a content from database", requestId);
			return;
		}
		
		// TODO: replace with siteParserService.findForUrl(url) and update diagrams
		String url = event.getUrl();
		SiteParser parser = null;
		for (SiteParser candidate : siteParsers) {
			if (candidate.canParse(url)) {
				parser = candidate;
				break;
			}
		}
		
		if (parser == null) {
			// TODO: how to handle error? maybe publish UnexpectedErrorEvent?
			log.error("Request #{}: could not find appropriate parser", requestId);
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
		seriesImportService.saveParsedData(requestId, data);
	}
	
	private void migrateParser(SiteParser parser) {
		if (!(parser instanceof JsoupSiteParser)) {
			log.warn("Could not migrate unknown (non-Jsoup based) parser: {}", parser);
			return;
		}
		
		SiteParserConfiguration cfg = ((JsoupSiteParser)parser).toConfiguration();
		String url = cfg.getMatchedUrl();
		String name = cfg.getName();
		if (siteParserService.findForUrl(url) != null) {
			log.warn(
				"Parser '{}': already exist in database and "
				+ "can be removed from application*.properties file",
				name
			);
			return;
		}
		
		log.info("Parser '{}': migrating to database", name);
		
		siteParserService.add(cfg);
		
		log.warn(
			"Parser '{}': successfully migrated and "
			+ "can be removed from application*.properties file",
			name
		);
	}
	
}
