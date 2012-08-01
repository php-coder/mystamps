/*
 * Copyright (C) 2009-2012 Slava Semushin <slava.semushin@gmail.com>
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

import javax.inject.Inject;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang3.StringUtils;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.google.common.base.Preconditions.checkArgument;

import ru.mystamps.web.dao.SuspiciousActivityDao;
import ru.mystamps.web.dao.SuspiciousActivityTypeDao;
import ru.mystamps.web.dao.UserDao;
import ru.mystamps.web.entity.User;
import ru.mystamps.web.entity.SuspiciousActivity;
import ru.mystamps.web.entity.SuspiciousActivityType;

@Service
public class SiteService {
	
	private static final Logger LOG = LoggerFactory.getLogger(SiteService.class);
	
	@Inject
	private UserDao users;
	
	@Inject
	private SuspiciousActivityDao suspiciousActivities;

	@Inject
	private SuspiciousActivityTypeDao suspiciousActivityTypes;
	
	@Transactional
	public void logAboutAbsentPage(
			final String page,
			final Integer userId,
			final String ip,
			final String referer,
			final String agent) {
		
		log(getAbsentPageType(), page, userId, ip, referer, agent);
	}
	
	@Transactional
	public void logAboutFailedAuthentication(
			final String page,
			final Integer userId,
			final String ip,
			final String referer,
			final String agent) {
		
		log(getFailedAuthenticationType(), page, userId, ip, referer, agent);
	}
	
	private void log(
			final SuspiciousActivityType type,
			final String page,
			final Integer userId,
			final String ip,
			final String referer,
			final String agent) {
		
		checkArgument(type != null, "Type of suspicious activity was not set");
		checkArgument(page != null, "Page should be non null");
		
		final SuspiciousActivity activity = new SuspiciousActivity();
		activity.setType(type);
		activity.setOccuredAt(new Date());
		activity.setPage(page);
		
		User currentUser = null;
		if (userId != null) {
			currentUser = users.findOne(userId);
			if (currentUser == null) {
				LOG.warn("Cannot find user with id {}", userId);
			}
		}
		activity.setUser(currentUser);
		
		activity.setIp(StringUtils.defaultString(ip));
		activity.setRefererPage(StringUtils.defaultString(referer));
		activity.setUserAgent(StringUtils.defaultString(agent));
		
		suspiciousActivities.save(activity);
	}
	
	private SuspiciousActivityType getAbsentPageType() {
		// see src/env/{dev,test}/WEB-INF/classes/init-data.sql
		return suspiciousActivityTypes.findByName("PageNotFound");
	}
	
	private SuspiciousActivityType getFailedAuthenticationType() {
		// see src/env/{dev,test}/WEB-INF/classes/init-data.sql
		return suspiciousActivityTypes.findByName("AuthenticationFailed");
	}
	
}
