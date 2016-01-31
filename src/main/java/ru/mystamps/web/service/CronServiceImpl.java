/*
 * Copyright (C) 2009-2016 Slava Semushin <slava.semushin@gmail.com>
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

import org.apache.commons.lang3.Validate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.dao.dto.UsersActivationFullDto;

@RequiredArgsConstructor
public class CronServiceImpl implements CronService {
	private static final String EVERY_DAY_AT_00_30 = "0 30 0 * * *";
	
	private static final Logger LOG = LoggerFactory.getLogger(CronServiceImpl.class);
	
	private final UsersActivationService usersActivationService;
	
	@Override
	@Scheduled(cron = EVERY_DAY_AT_00_30)
	@Transactional
	public void purgeUsersActivations() {
		List<UsersActivationFullDto> expiredActivations =
			usersActivationService.findOlderThan(PURGE_AFTER_DAYS);
		
		Validate.validState(expiredActivations != null, "Expired activations should be non null");
		
		if (expiredActivations.isEmpty()) {
			LOG.info("Expired activations was not found.");
			return;
		}
		
		for (UsersActivationFullDto activation : expiredActivations) {
			LOG.info(
				"Delete expired activation (key: {}, email: {}, created: {})",
				activation.getActivationKey(),
				activation.getEmail(),
				activation.getCreatedAt()
			);
			
			usersActivationService.remove(activation.getActivationKey());
		}
	}
	
}
