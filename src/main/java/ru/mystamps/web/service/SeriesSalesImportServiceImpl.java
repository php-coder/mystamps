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
package ru.mystamps.web.service;

import org.apache.commons.lang3.Validate;

import org.slf4j.Logger;

import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.dao.SeriesSalesImportDao;
import ru.mystamps.web.dao.dto.SeriesSaleParsedDataDto;
import ru.mystamps.web.dao.dto.SeriesSalesParsedDataDbDto;

@RequiredArgsConstructor
public class SeriesSalesImportServiceImpl implements SeriesSalesImportService {
	
	private final Logger log;
	private final SeriesSalesImportDao seriesSalesImportDao;
	
	// @todo #834 SeriesSalesImportServiceImpl.saveParsedData(): introduce dto without dates
	@Override
	@Transactional
	public void saveParsedData(Integer requestId, SeriesSalesParsedDataDbDto data) {
		Validate.isTrue(requestId != null, "Request id must be non null");
		Validate.isTrue(data != null, "Parsed data must be non null");
		
		seriesSalesImportDao.addParsedData(requestId, data);
	}
	
	// @todo #695 SeriesSalesImportServiceImpl.getParsedData(): add unit tests
	@Override
	@Transactional(readOnly = true)
	public SeriesSaleParsedDataDto getParsedData(Integer requestId) {
		Validate.isTrue(requestId != null, "Request id must be non null");
		
		return seriesSalesImportDao.findParsedDataByRequestId(requestId);
	}
	
}
