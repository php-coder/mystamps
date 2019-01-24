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
package ru.mystamps.web.service;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import org.slf4j.Logger;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.access.prepost.PreAuthorize;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.dao.StampsCatalogDao;
import ru.mystamps.web.support.spring.security.HasAuthority;

@RequiredArgsConstructor
public class StampsCatalogServiceImpl implements StampsCatalogService {
	
	private final Logger log;
	private final String catalogName;
	private final StampsCatalogDao stampsCatalogDao;
	
	@Override
	@Transactional
	@PreAuthorize(HasAuthority.CREATE_SERIES)
	public void add(Set<String> catalogNumbers) {
		Validate.isTrue(catalogNumbers != null, "%s numbers must be non null", catalogName);
		Validate.isTrue(!catalogNumbers.isEmpty(), "%s numbers must be non empty", catalogName);
		
		List<String> insertedNumbers = stampsCatalogDao.add(catalogNumbers);
		
		if (!insertedNumbers.isEmpty()) {
			log.info("{} numbers {} were created", catalogName, insertedNumbers);
		}
	}
	
	@Override
	@Transactional
	@PreAuthorize(HasAuthority.CREATE_SERIES)
	public void addToSeries(Integer seriesId, Set<String> catalogNumbers) {
		Validate.isTrue(seriesId != null, "Series id must be non null");
		Validate.isTrue(catalogNumbers != null, "%s numbers must be non null", catalogName);
		Validate.isTrue(!catalogNumbers.isEmpty(), "%s numbers must be non empty", catalogName);
		
		stampsCatalogDao.addToSeries(seriesId, catalogNumbers);
		
		log.info("Series #{}: {} numbers {} were added", seriesId, catalogName, catalogNumbers);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<String> findBySeriesId(Integer seriesId) {
		Validate.isTrue(seriesId != null, "Series id must be non null");
		
		return stampsCatalogDao.findBySeriesId(seriesId);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Integer> findSeriesIdsByNumber(String catalogNumber) {
		Validate.isTrue(catalogNumber != null, "%s number must be non null", catalogName);
		Validate.isTrue(
			StringUtils.isNotBlank(catalogNumber),
			"%s number must be non-blank",
			catalogName
		);
		
		return stampsCatalogDao.findSeriesIdsByNumber(catalogNumber);
	}
	
}
