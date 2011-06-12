/*
 * Copyright (C) 2009-2011 Slava Semushin <slava.semushin@gmail.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

package ru.mystamps.web.service;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.mystamps.web.dao.SuspiciousActivityDao;
import ru.mystamps.web.dao.SuspiciousActivityTypeDao;
import ru.mystamps.web.dao.UserDao;
import ru.mystamps.web.entity.User;
import ru.mystamps.web.entity.SuspiciousActivity;
import ru.mystamps.web.entity.SuspiciousActivityType;

@Service
public class SiteService {
	private final Logger log = LoggerFactory.getLogger(SiteService.class);
	
	@Autowired
	private UserDao users;
	
	@Autowired
	private SuspiciousActivityDao suspiciousActivities;

	@Autowired
	private SuspiciousActivityTypeDao suspiciousActivityTypes;
	
	@Transactional
	public void logAboutAbsentPage(
			final String page,
			final User user,
			final String ip,
			final String referer,
			final String agent) {
		
		log(getAbsentPageType(), page, user, ip, referer, agent);
	}
	
	@Transactional
	public void logAboutFailedAuthentication(
			final String page,
			final User user,
			final String ip,
			final String referer,
			final String agent) {
		
		log(getFailedAuthenticationType(), page, user, ip, referer, agent);
	}
	
	private void log(
			final SuspiciousActivityType type,
			final String page,
			final User user,
			final String ip,
			final String referer,
			final String agent) {
		
		if (type == null) {
			throw new IllegalArgumentException("Type of suspicious activity was not set");
		}
		
		final SuspiciousActivity activity = new SuspiciousActivity();
		activity.setType(type);
		activity.setOccuredAt(new Date());
		activity.setPage(page);
		
		User currentUser = user;
		if (currentUser != null) {
			currentUser = users.findById(user.getId());
			if (currentUser == null) {
				log.warn("Cannot find user with id {}", user.getId());
			}
		}
		activity.setUser(currentUser);
		
		activity.setIp(StringUtils.defaultString(ip));
		activity.setRefererPage(StringUtils.defaultString(referer));
		activity.setUserAgent(StringUtils.defaultString(agent));
		
		suspiciousActivities.add(activity);
	}
	
	private SuspiciousActivityType getAbsentPageType() {
		// see mystamps.sql
		return suspiciousActivityTypes.findByName("PageNotFound");
	}
	
	private SuspiciousActivityType getFailedAuthenticationType() {
		// see mystamps.sql
		return suspiciousActivityTypes.findByName("AuthenticationFailed");
	}
	
}
