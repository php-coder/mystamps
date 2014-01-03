/*
 * Copyright (C) 2009-2014 Slava Semushin <slava.semushin@gmail.com>
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

import javax.inject.Inject;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.Validate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import ru.mystamps.web.dao.UsersActivationDao;
import ru.mystamps.web.entity.UsersActivation;

public class CronServiceImpl implements CronService {
	private static final long CHECK_PERIOD = 12 * DateUtils.MILLIS_PER_HOUR;
	
	private static final Logger LOG = LoggerFactory.getLogger(CronService.class);
	
	private final UsersActivationDao usersActivationDao;
	
	@Inject
	public CronServiceImpl(UsersActivationDao usersActivationDao) {
		this.usersActivationDao = usersActivationDao;
	}
	
	@Override
	@Scheduled(fixedDelay = CHECK_PERIOD)
	@Transactional
	public void purgeUsersActivations() {
		Date expiredSince = DateUtils.addDays(new Date(), -PURGE_AFTER_DAYS);
		
		List<UsersActivation> expiredActivations =
			usersActivationDao.findByCreatedAtLessThan(expiredSince);
		
		Validate.validState(expiredActivations != null, "Expired activations should be non null");
		
		if (expiredActivations.isEmpty()) {
			LOG.info("Expired activations was not found.");
			return;
		}
		
		for (UsersActivation activation : expiredActivations) {
			LOG.info(
				"Delete expired activation (key: {}, email: {}, created: {})",
				activation.getActivationKey(),
				activation.getEmail(),
				activation.getCreatedAt()
			);
		}
		
		usersActivationDao.delete(expiredActivations);
	}
	
}
