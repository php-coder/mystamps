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

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.Url;
import ru.mystamps.web.controller.converter.annotation.CurrentUser;
import ru.mystamps.web.service.SiteService;

@Controller
@RequiredArgsConstructor
public class ErrorController {
	private static final Logger LOG = LoggerFactory.getLogger(ErrorController.class);
	
	private final SiteService siteService;
	
	@RequestMapping(Url.NOT_FOUND_PAGE)
	public void notFound(
			HttpServletRequest request,
			@CurrentUser Integer currentUserId,
			// CheckStyle: ignore LineLength for next 1 line
			@RequestAttribute(name = RequestDispatcher.ERROR_REQUEST_URI, required = false) String page,
			@RequestHeader(name = "referer", required = false) String referer,
			@RequestHeader(name = "user-agent", required = false) String agent) {
		
		// TODO: sanitize all user's values (#60)
		String ip     = request.getRemoteAddr();
		String method = request.getMethod();
		
		siteService.logAboutAbsentPage(page, method, currentUserId, ip, referer, agent);
	}
	
	@RequestMapping(Url.INTERNAL_ERROR_PAGE)
	public void internalError(
		// CheckStyle: ignore LineLength for next 3 lines
		@RequestAttribute(name = RequestDispatcher.ERROR_EXCEPTION_TYPE, required = false) Class<?> exceptionType,
		@RequestAttribute(name = RequestDispatcher.ERROR_EXCEPTION, required = false) Exception exception,
		@RequestAttribute(name = RequestDispatcher.ERROR_REQUEST_URI, required = false) String page) {
		
		// TODO: log to database (with *.status_code, *.message, *.servlet_name and user details)
		
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

