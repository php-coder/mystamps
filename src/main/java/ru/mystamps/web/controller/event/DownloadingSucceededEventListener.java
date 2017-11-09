/*
 * Copyright (C) 2009-2017 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.controller.event;

import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.service.SeriesImportService;
import ru.mystamps.web.service.dto.RawParsedDataDto;
import ru.mystamps.web.util.extractor.SeriesInfo;
import ru.mystamps.web.util.extractor.SiteParser;

/**
 * Listener of the @{link DownloadingSucceeded} event.
 *
 * TODO: javadoc
 */
@RequiredArgsConstructor
public class DownloadingSucceededEventListener
	implements ApplicationListener<DownloadingSucceeded> {
	
	private final Logger log;
	private final SeriesImportService importService;
	private final List<SiteParser> siteParsers;
	private final ApplicationEventPublisher eventPublisher;
	
	@PostConstruct
	public void init() {
		log.info("Registered site parsers: {}", siteParsers);
	}
	
	@Override
	public void onApplicationEvent(DownloadingSucceeded event) {
		Integer requestId = event.getRequestId();
		
		log.info("Request #{}: downloading succeeded", requestId);
		
		String content = importService.getDownloadedContent(requestId);
		if (content == null) {
			// TODO: how to handle error? maybe publish UnexpectedErrorEvent?
			log.error("Request #{}: could not load a content from database", requestId);
			return;
		}
		
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
			info.getIssueDate()
		);
		importService.saveParsedData(requestId, data);
	}
	
}
