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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import ru.mystamps.web.Db;
import ru.mystamps.web.feature.series.DownloaderService;
import ru.mystamps.web.feature.series.importing.SeriesImportService;
import ru.mystamps.web.service.dto.DownloadResult;

/**
 * Listener of the {@link ImportRequestCreated} event.
 *
 * Downloads a file, saves it to database and publish the {@link DownloadingSucceeded} event.
 * When downloading of a file fails, it changes request status to 'DownloadingFailed'.
 *
 * @see DownloadingSucceededEventListener
 */
@RequiredArgsConstructor
public class ImportRequestCreatedEventListener
	implements ApplicationListener<ImportRequestCreated> {
	
	private final Logger log;
	private final DownloaderService downloaderService;
	private final SeriesImportService seriesImportService;
	private final ApplicationEventPublisher eventPublisher;
	
	@Override
	public void onApplicationEvent(ImportRequestCreated event) {
		String url = event.getUrl();
		Integer requestId = event.getRequestId();
		
		log.info("Request #{}: start downloading '{}'", requestId, url);
		
		DownloadResult result = downloaderService.download(url);
		if (result.hasFailed()) {
			// CheckStyle: ignore LineLength for next 1 line
			log.info("Request #{}: downloading of '{}' failed: {}", requestId, url, result.getCode());

			seriesImportService.changeStatus(
				requestId,
				Db.SeriesImportRequestStatus.UNPROCESSED,
				Db.SeriesImportRequestStatus.DOWNLOADING_FAILED
			);
			return;
		}
		
		seriesImportService.saveDownloadedContent(requestId, result.getDataAsString());
		
		eventPublisher.publishEvent(new DownloadingSucceeded(this, requestId, url));
	}
	
}
