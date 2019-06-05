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
package ru.mystamps.web.support.spring.security;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ru.mystamps.web.feature.site.SiteService;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@RequiredArgsConstructor
public class AuthenticationFailureListener
	implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {
	
	private static final Logger LOG = LoggerFactory.getLogger(AuthenticationFailureListener.class);
	
	private final SiteService siteService;
	
	@Override
	public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
		HttpServletRequest request = getRequest();
		if (request == null) {
			LOG.warn("Can't get http request object");
			return;
		}
		
		// FIXME: log more info (login for example) (#59)
		// FIXME: sanitize all user's values (#60)
		String method  = request.getMethod();
		String page    = request.getRequestURI();
		String ip      = request.getRemoteAddr();
		String referer = request.getHeader("referer");
		String agent   = request.getHeader("user-agent");
		Date date      = new Date(event.getTimestamp());
		
		siteService.logAboutFailedAuthentication(page, method, null, ip, referer, agent, date);
	}
	
	private static HttpServletRequest getRequest() {
		ServletRequestAttributes attrs =
			(ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
		if (attrs == null) {
			return null;
		}
		
		return attrs.getRequest();
	}
	
}
