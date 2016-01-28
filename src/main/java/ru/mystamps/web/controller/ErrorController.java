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
package ru.mystamps.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.Url;
import ru.mystamps.web.service.SiteService;
import ru.mystamps.web.support.spring.security.CustomUserDetails;

@Controller
@RequiredArgsConstructor
public class ErrorController {
	private static final Logger LOG = LoggerFactory.getLogger(ErrorController.class);
	
	private final SiteService siteService;
	
	@RequestMapping(Url.NOT_FOUND_PAGE)
	public void notFound(
			HttpServletRequest request,
			CustomUserDetails userDetails,
			@RequestHeader(value = "referer", required = false) String referer,
			@RequestHeader(value = "user-agent", required = false) String agent) {
		
		// TODO: sanitize all user's values (#60)
		String page   = (String)request.getAttribute("javax.servlet.error.request_uri");
		String ip     = request.getRemoteAddr();
		String method = request.getMethod();
		
		Integer userId = null;
		if (userDetails != null) {
			userId = userDetails.getUser().getId();
		}
		
		siteService.logAboutAbsentPage(page, method, userId, ip, referer, agent);
	}
	
	@RequestMapping(Url.INTERNAL_ERROR_PAGE)
	public void internalError(HttpServletRequest request) {
		// TODO: log to database (with *.status_code, *.message, *.servlet_name and user details)
		
		// CheckStyle: ignore LineLength for next 1 line
		Class<?> exceptionType = (Class<?>)request.getAttribute("javax.servlet.error.exception_type");
		Exception exception    = (Exception)request.getAttribute("javax.servlet.error.exception");
		String page            = (String)request.getAttribute("javax.servlet.error.request_uri");
		
		if (page != null && !Url.INTERNAL_ERROR_PAGE.equals(page)) {
			String msg = String.format(
				"Exception '%s' occurred at page %s",
				getNameOrAsIs(exceptionType),
				page
			);
			LOG.error(msg, exception);
		}
	}
	
	private static Object getNameOrAsIs(Class<?> clazz) {
		if (clazz == null) {
			return null;
		}
		
		return clazz.getName();
	}
	
}

