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

import javax.inject.Inject;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import org.springframework.transaction.annotation.Transactional;

import ru.mystamps.web.dao.SuspiciousActivityDao;
import ru.mystamps.web.dao.SuspiciousActivityTypeDao;
import ru.mystamps.web.entity.User;
import ru.mystamps.web.entity.SuspiciousActivity;
import ru.mystamps.web.entity.SuspiciousActivityType;

public class SiteServiceImpl implements SiteService {
	
	private final SuspiciousActivityDao suspiciousActivities;
	private final SuspiciousActivityTypeDao suspiciousActivityTypes;
	
	@Inject
	public SiteServiceImpl(
		SuspiciousActivityDao suspiciousActivities,
		SuspiciousActivityTypeDao suspiciousActivityTypes) {
		
		this.suspiciousActivities = suspiciousActivities;
		this.suspiciousActivityTypes = suspiciousActivityTypes;
	}
	
	@Override
	@SuppressWarnings("PMD.UseObjectForClearerAPI")
	@Transactional
	public void logAboutAbsentPage(
			String page,
			User user,
			String ip,
			String referer,
			String agent) {
		
		logEvent(getAbsentPageType(), page, user, ip, referer, agent);
	}
	
	@Override
	@SuppressWarnings("PMD.UseObjectForClearerAPI")
	@Transactional
	public void logAboutFailedAuthentication(
			String page,
			User user,
			String ip,
			String referer,
			String agent) {
		
		logEvent(getFailedAuthenticationType(), page, user, ip, referer, agent);
	}
	
	@SuppressWarnings("PMD.UseObjectForClearerAPI")
	private void logEvent(
			SuspiciousActivityType type,
			String page,
			User user,
			String ip,
			String referer,
			String agent) {
		
		Validate.isTrue(type != null, "Type of suspicious activity was not set");
		Validate.isTrue(page != null, "Page should be non null");
		
		SuspiciousActivity activity = new SuspiciousActivity();
		activity.setType(type);
		activity.setOccurredAt(new Date());
		activity.setPage(page);
		
		activity.setUser(user);
		
		activity.setIp(StringUtils.defaultString(ip));
		activity.setRefererPage(StringUtils.defaultString(referer));
		activity.setUserAgent(StringUtils.defaultString(agent));
		
		suspiciousActivities.save(activity);
	}
	
	private SuspiciousActivityType getAbsentPageType() {
		// see initiate-suspicious_activities_types-table changeset
		// in src/main/resources/liquibase/initial-state.xml
		return suspiciousActivityTypes.findByName("PageNotFound");
	}
	
	private SuspiciousActivityType getFailedAuthenticationType() {
		// see initiate-suspicious_activities_types-table changeset
		// in src/main/resources/liquibase/initial-state.xml
		return suspiciousActivityTypes.findByName("AuthenticationFailed");
	}
	
}
