/*
 * Copyright (C) 2009-2023 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.feature.site;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import ru.mystamps.web.feature.site.SiteDb.SuspiciousActivity;
import ru.mystamps.web.feature.site.SiteDb.SuspiciousActivityType;
import ru.mystamps.web.support.spring.security.SecurityContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@RequiredArgsConstructor
public class SiteServiceImpl implements SiteService {
	
	private final Logger log;
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
		
		logEvent(
			SuspiciousActivityType.PAGE_NOT_FOUND,
			page,
			method,
			userId,
			ip,
			referer,
			agent,
			new Date()
		);
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
		
		logEvent(
			SuspiciousActivityType.AUTHENTICATION_FAILED,
			page,
			method,
			userId,
			ip,
			referer,
			agent,
			date
		);
	}
	
	/**
	 * @author Sergey Chechenev
	 */
	@Override
	@Transactional
	public void logAboutMissingCsrfToken(HttpServletRequest request) {
		
		logEvent(
			SuspiciousActivityType.MISSING_CSRF_TOKEN,
			request.getRequestURI(),
			request.getMethod(),
			SecurityContextUtils.getUserId(),
			request.getRemoteAddr(),
			request.getHeader(HttpHeaders.REFERER),
			request.getHeader(HttpHeaders.USER_AGENT),
			new Date()
		);
		
	}
	
	/**
	 * @author Sergey Chechenev
	 */
	@Override
	@Transactional
	public void logAboutInvalidCsrfToken(HttpServletRequest request) {
		
		logEvent(
			SuspiciousActivityType.INVALID_CSRF_TOKEN,
			request.getRequestURI(),
			request.getMethod(),
			SecurityContextUtils.getUserId(),
			request.getRemoteAddr(),
			request.getHeader(HttpHeaders.REFERER),
			request.getHeader(HttpHeaders.USER_AGENT),
			new Date()
		);
		
	}
	
	// protected for using in unit tests
	@SuppressWarnings({ "PMD.UseObjectForClearerAPI", "checkstyle:parameternumber" })
	protected void logEvent(
			String type,
			String page,
			String method,
			Integer userId,
			String ip,
			String referer,
			String agent,
			Date date) {
		
		Validate.isTrue(type != null, "Type of suspicious activity must be non null");
		Validate.isTrue(page != null, "Page must be non null");
		
		AddSuspiciousActivityDbDto activity = new AddSuspiciousActivityDbDto();
		
		activity.setType(type);
		activity.setOccurredAt(date == null ? new Date() : date);
		activity.setPage(abbreviatePage(page));
		activity.setMethod(abbreviateMethod(method));
		activity.setUserId(userId);
		activity.setIp(StringUtils.defaultString(ip));
		activity.setRefererPage(StringUtils.stripToNull(abbreviateRefererPage(referer)));
		activity.setUserAgent(StringUtils.stripToNull(abbreviateUserAgent(agent)));
		
		suspiciousActivities.add(activity);
	}
	
	/**
	 * Abbreviate name of HTTP method.
	 * @param method name of HTTP method
	 * @return name of the method as-is or its abbreviation with three points at the end
	 * @author Aleksandr Zorin
	 */
	private String abbreviateMethod(String method) {
		return abbreviateIfLengthGreaterThan(
			method,
			SuspiciousActivity.METHOD_LENGTH,
			"method"
		);
	}
	
	private String abbreviatePage(String page) {
		return abbreviateIfLengthGreaterThan(
			page,
			SuspiciousActivity.PAGE_URL_LENGTH,
			"page"
		);
	}
	
	private String abbreviateRefererPage(String referer) {
		return abbreviateIfLengthGreaterThan(
			referer,
			SuspiciousActivity.REFERER_PAGE_LENGTH,
			"referer_page"
		);
	}
	
	private String abbreviateUserAgent(String agent) {
		return abbreviateIfLengthGreaterThan(
			agent,
			SuspiciousActivity.USER_AGENT_LENGTH,
			"user_agent"
		);
	}
	
	// CheckStyle: ignore LineLength for next 1 lines
	private String abbreviateIfLengthGreaterThan(String text, int maxLength, String fieldName) {
		if (text == null || text.length() <= maxLength) {
			return text;
		}
		
		// FIXME(security): fix possible log injection
		log.warn(
				"Length of '{}' exceeds max length for '{}' field: {} > {}",
				text,
				fieldName,
				text.length(),
				maxLength
		);
		
		return StringUtils.abbreviate(text, maxLength);
	}
	
}
