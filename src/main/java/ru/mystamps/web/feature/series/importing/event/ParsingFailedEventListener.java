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
package ru.mystamps.web.feature.series.importing.event;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import ru.mystamps.web.Db.SeriesImportRequestStatus;
import ru.mystamps.web.feature.series.importing.SeriesImportService;

/**
 * Listener of the {@link ParsingFailed} event.
 *
 * Changes request status to 'ParsingFailed'.
 */
@RequiredArgsConstructor
public class ParsingFailedEventListener
	implements ApplicationListener<ParsingFailed> {
	
	private static final Logger LOG = LoggerFactory.getLogger(ParsingFailedEventListener.class);
	
	private final SeriesImportService seriesImportService;
	
	@Override
	public void onApplicationEvent(ParsingFailed event) {
		Integer requestId = event.getRequestId();
		
		// FIXME: more info?
		LOG.info("Request #{}: parsing failed", requestId);
		
		seriesImportService.changeStatus(
			requestId,
			SeriesImportRequestStatus.DOWNLOADING_SUCCEEDED,
			SeriesImportRequestStatus.PARSING_FAILED
		);
	}
	
}
