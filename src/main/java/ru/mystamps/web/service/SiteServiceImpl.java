/*
 * Copyright (C) 2009-2015 Slava Semushin <slava.semushin@gmail.com>
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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.dao.SuspiciousActivityDao;
import ru.mystamps.web.entity.User;
import ru.mystamps.web.entity.SuspiciousActivity;
import ru.mystamps.web.entity.SuspiciousActivityType;

@RequiredArgsConstructor
public class SiteServiceImpl implements SiteService {
	
	// see initiate-suspicious_activities_types-table changeset
	// in src/main/resources/liquibase/initial-state.xml
	private static final String PAGE_NOT_FOUND = "PageNotFound";
	private static final String AUTHENTICATION_FAILED = "AuthenticationFailed";
	
	private final SuspiciousActivityDao suspiciousActivities;
	
	@Override
	@SuppressWarnings("PMD.UseObjectForClearerAPI")
	@Async
	@Transactional
	public void logAboutAbsentPage(
			String page,
			User user,
			String ip,
			String referer,
			String agent) {
		
		logEvent(PAGE_NOT_FOUND, page, user, ip, referer, agent, new Date());
	}
	
	@Override
	@SuppressWarnings("PMD.UseObjectForClearerAPI")
	@Transactional
	public void logAboutFailedAuthentication(
			String page,
			User user,
			String ip,
			String referer,
			String agent,
			Date date) {
		
		logEvent(AUTHENTICATION_FAILED, page, user, ip, referer, agent, date);
	}
	
	@SuppressWarnings("PMD.UseObjectForClearerAPI")
	private void logEvent(
			String type,
			String page,
			User user,
			String ip,
			String referer,
			String agent,
			Date date) {
		
		Validate.isTrue(type != null, "Type of suspicious activity was not set");
		Validate.isTrue(page != null, "Page should be non null");
		
		SuspiciousActivity activity = new SuspiciousActivity();
		
		// TODO: replace entity with DTO and replace SuspiciousActivityType by String
		SuspiciousActivityType activityType = new SuspiciousActivityType();
		activityType.setName(type);
		activity.setType(activityType);
		
		activity.setOccurredAt(date == null ? new Date() : date);
		activity.setPage(page);
		
		activity.setUser(user);
		
		activity.setIp(StringUtils.defaultString(ip));
		activity.setRefererPage(StringUtils.defaultString(referer));
		activity.setUserAgent(StringUtils.defaultString(agent));
		
		suspiciousActivities.add(activity);
	}
	
}
