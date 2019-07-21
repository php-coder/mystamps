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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class SeriesSaleImportController {
	
	private static final Logger LOG = LoggerFactory.getLogger(SeriesSaleImportController.class);
	
	private final SeriesSalesImportService seriesSalesImportService;
	
	@PostMapping(SeriesSalesImportUrl.IMPORT_SERIES_SALES)
	public ResponseEntity<SeriesSaleExtractedInfo> downloadAndParse(
		@RequestBody @Valid RequestSeriesSaleImportForm form) {
		
		String url = form.getUrl();
		
		try {
			SeriesSaleExtractedInfo result = seriesSalesImportService.downloadAndParse(url);
			return ResponseEntity.ok(result);
			
		} catch (RuntimeException ex) { // NOPMD: AvoidCatchingGenericException; try to catch-all
			LOG.error("Failed to process '{}': {}", url, ex.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
}
