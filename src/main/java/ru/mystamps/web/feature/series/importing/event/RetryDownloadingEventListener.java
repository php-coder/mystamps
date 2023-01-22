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
package ru.mystamps.web.feature.series.importing.event;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import ru.mystamps.web.feature.series.DownloadResult;
import ru.mystamps.web.feature.series.DownloaderService;
import ru.mystamps.web.feature.series.importing.ImportRequestDto;
import ru.mystamps.web.feature.series.importing.SeriesImportDb.SeriesImportRequestStatus;
import ru.mystamps.web.feature.series.importing.SeriesImportService;

/**
 * Listener of the {@link RetryDownloading} event.
 *
 * Downloads a file, saves it to database and publish the {@link DownloadingSucceeded} event.
 *
 * It is similar to {@link ImportRequestCreatedEventListener} with the following differences:
 * - it loads a request from database as we have only id
 * - it doesn't modify a request state when downloading fails
 * - it invokes {@code saveDownloadedContent()} with retry=true parameter
 *
 * @see ImportRequestCreatedEventListener
 * @see DownloadingSucceededEventListener
 */
@RequiredArgsConstructor
public class RetryDownloadingEventListener implements ApplicationListener<RetryDownloading> {
	
	private static final Logger LOG = LoggerFactory.getLogger(RetryDownloadingEventListener.class);
	
	private final DownloaderService downloaderService;
	private final SeriesImportService seriesImportService;
	private final ApplicationEventPublisher eventPublisher;
	
	@Override
	public void onApplicationEvent(RetryDownloading event) {
		Integer requestId = event.getRequestId();

		ImportRequestDto request = seriesImportService.findById(requestId);
		if (request == null) {
			// FIXME: how to handle error? maybe publish UnexpectedErrorEvent?
			LOG.error("Request #{}: couldn't retry is it doesn't exist", requestId);
			return;
		}

		String status = request.getStatus();
		if (!SeriesImportRequestStatus.DOWNLOADING_FAILED.equals(status)) {
			LOG.warn("Request #{}: unexpected status '{}'. Abort a retry process", request, status);
			return;
		}
		
		String url = request.getUrl();
		LOG.info("Request #{}: retry downloading '{}'", requestId, url);
		
		DownloadResult result = downloaderService.download(url);
		if (result.hasFailed()) {
			LOG.info(
				"Request #{}: downloading of '{}' failed again: {}",
				requestId,
				url,
				result.getCode()
			);
			
			// in case of failure we don't need to change request status as we assume that
			// it's already set to DownloadingFailed
			return;
		}
		
		// FIXME: do we need updated_by field?
		seriesImportService.saveDownloadedContent(requestId, result.getDataAsString(), true);
		
		eventPublisher.publishEvent(new DownloadingSucceeded(this, requestId, url));
	}
	
}
