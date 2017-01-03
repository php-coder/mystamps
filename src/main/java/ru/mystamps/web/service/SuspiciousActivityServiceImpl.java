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
package ru.mystamps.web.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.access.prepost.PreAuthorize;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.dao.SuspiciousActivityDao;
import ru.mystamps.web.dao.dto.SuspiciousActivityDto;
import ru.mystamps.web.support.spring.security.HasAuthority;

/**
 * @author Sergey Chechenev
 * @author Slava Semushin
 */
@RequiredArgsConstructor
public class SuspiciousActivityServiceImpl implements SuspiciousActivityService {
	private final SuspiciousActivityDao suspiciousActivityDao;
	
	@Override
	@Transactional(readOnly = true)
	@PreAuthorize(HasAuthority.VIEW_SITE_EVENTS)
	public long countAll() {
		return suspiciousActivityDao.countAll();
	}
	
	@Override
	@Transactional(readOnly = true)
	public long countByTypeSince(String type, Date date) {
		Validate.isTrue(StringUtils.isNotBlank(type), "Type must be non-blank");
		Validate.isTrue(date != null, "Date must be non null");
		
		return suspiciousActivityDao.countByTypeSince(type, date);
	}
	
	@Override
	@Transactional(readOnly = true)
	@PreAuthorize(HasAuthority.VIEW_SITE_EVENTS)
	public List<SuspiciousActivityDto> findSuspiciousActivities(int page, int recordsPerPage) {
		Validate.isTrue(page > 0, "Page must be greater than zero");
		Validate.isTrue(recordsPerPage > 0, "RecordsPerPage must be greater than zero");
		
		return suspiciousActivityDao.findAll(page, recordsPerPage);
	}
	
}
