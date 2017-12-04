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

import org.slf4j.Logger;

import org.springframework.context.ApplicationListener;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.Db;
import ru.mystamps.web.service.SeriesImportService;

/**
 * Listener of the @{link DownloadingFailed} event.
 *
 * Changes request status to 'DownloadingFailed'.
 */
@RequiredArgsConstructor
public class DownloadingFailedEventListener
	implements ApplicationListener<DownloadingFailed> {
	
	private final Logger log;
	private final SeriesImportService importService;
	
	@Override
	public void onApplicationEvent(DownloadingFailed event) {
		Integer requestId = event.getRequestId();
		
		log.info(
			"Request #{}: downloading of '{}' failed: {}",
			requestId,
			event.getUrl(),
			event.getCode()
		);
		
		importService.changeStatus(
			requestId,
			Db.SeriesImportRequestStatus.UNPROCESSED,
			Db.SeriesImportRequestStatus.DOWNLOADING_FAILED
		);
	}
	
}
