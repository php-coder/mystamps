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

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.Db;
import ru.mystamps.web.dao.SuspiciousActivityDao;
import ru.mystamps.web.dao.dto.AddSuspiciousActivityDbDto;
import ru.mystamps.web.support.spring.security.SecurityContextUtils;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
public class SiteServiceImpl implements SiteService {
	
	private static final Logger LOG = LoggerFactory.getLogger(SiteServiceImpl.class);
	
	// see initiate-suspicious_activities_types-table changeset
	// in src/main/resources/liquibase/initial-state.xml
	private static final String PAGE_NOT_FOUND = "PageNotFound";
	private static final String AUTHENTICATION_FAILED = "AuthenticationFailed";
	private static final String MISSING_CSRF_TOKEN = "MissingCsrfToken";
	
	// see add-types-for-csrf-tokens-to-suspicious_activities_types-table changeset
	// in src/main/resources/liquibase/version/0.4/2016-02-19--csrf_events.xml
	private static final String INVALID_CSRF_TOKEN = "InvalidCsrfToken";
	
	private final SuspiciousActivityDao suspiciousActivities;
	
	@Override
	@SuppressWarnings("PMD.UseObjectForClearerAPI")
	@Async
	@Transactional
	public void logAboutAbsentPage(
			String page,
			String method,
			Integer userId,
			String ip,
			String referer,
			String agent) {
		
		logEvent(PAGE_NOT_FOUND, page, method, userId, ip, referer, agent, new Date());
	}
	
	@Override
	@SuppressWarnings("PMD.UseObjectForClearerAPI")
	@Transactional
	public void logAboutFailedAuthentication(
			String page,
			String method,
			Integer userId,
			String ip,
			String referer,
			String agent,
			Date date) {
		
		logEvent(AUTHENTICATION_FAILED, page, method, userId, ip, referer, agent, date);
	}
	
	@Override
	@Transactional
	public void logAboutMissingCsrfToken(HttpServletRequest request) {
		
		logEvent(
			MISSING_CSRF_TOKEN,
			request.getRequestURI(),
			request.getMethod(),
			SecurityContextUtils.getUserId(),
			request.getRemoteAddr(),
			request.getHeader("referer"),
			request.getHeader("user-agent"),
			new Date()
		);
		
	}
	
	@Override
	@Transactional
	public void logAboutInvalidCsrfToken(HttpServletRequest request) {
		
		logEvent(
			INVALID_CSRF_TOKEN,
			request.getRequestURI(),
			request.getMethod(),
			SecurityContextUtils.getUserId(),
			request.getRemoteAddr(),
			request.getHeader("referer"),
			request.getHeader("user-agent"),
			new Date()
		);
		
	}
	
	@SuppressWarnings({"PMD.UseObjectForClearerAPI", "checkstyle:parameternumber"})
	private void logEvent(
			String type,
			String page,
			String method,
			Integer userId,
			String ip,
			String referer,
			String agent,
			Date date) {
		
		Validate.isTrue(type != null, "Type of suspicious activity was not set");
		Validate.isTrue(page != null, "Page should be non null");
		
		AddSuspiciousActivityDbDto activity = new AddSuspiciousActivityDbDto();
		
		activity.setType(type);
		activity.setOccurredAt(date == null ? new Date() : date);
		activity.setPage(abbreviatePage(page));
		activity.setMethod(method);
		activity.setUserId(userId);
		activity.setIp(StringUtils.defaultString(ip));
		activity.setRefererPage(StringUtils.stripToNull(abbreviateRefererPage(referer)));
		activity.setUserAgent(StringUtils.stripToNull(abbreviateUserAgent(agent)));
		
		suspiciousActivities.add(activity);
	}
	
	private static String abbreviatePage(String page) {
		return abbreviateIfLengthGreaterThan(page, Db.SuspiciousActivity.PAGE_URL_LENGTH, "page");
	}
	
	private static String abbreviateRefererPage(String referer) {
		return abbreviateIfLengthGreaterThan(
			referer,
			Db.SuspiciousActivity.REFERER_PAGE_LENGTH,
			"referer_page"
		);
	}
	
	private static String abbreviateUserAgent(String agent) {
		return abbreviateIfLengthGreaterThan(
			agent,
			Db.SuspiciousActivity.USER_AGENT_LENGTH,
			"user_agent"
		);
	}
	
	// CheckStyle: ignore LineLength for next 1 lines
	private static String abbreviateIfLengthGreaterThan(String text, int maxLength, String fieldName) {
		if (text == null || text.length() <= maxLength) {
			return text;
		}
		
		// TODO(security): fix possible log injection
		LOG.warn(
				"Length of value for '{}' field ({}) exceeds max field size ({}): '{}'",
				fieldName,
				text.length(),
				maxLength,
				text
		);
		
		return StringUtils.abbreviate(text, maxLength);
	}
	
}
