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

package ru.mystamps.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.mystamps.web.entity.User;
import ru.mystamps.web.service.SiteService;

import static ru.mystamps.web.SiteMap.NOT_FOUND_PAGE_URL;

@Controller
@RequestMapping(NOT_FOUND_PAGE_URL)
public class NotFoundErrorController {
	private final Logger log = LoggerFactory.getLogger(NotFoundErrorController.class);
	
	private final SiteService siteService;
	
	@Autowired
	NotFoundErrorController(final SiteService siteService) {
		this.siteService = siteService;
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public void notFound(
			final HttpServletRequest request,
			final HttpSession session,
			@RequestHeader(value = "referer", required = false) final String referer,
			@RequestHeader(value = "user-agent", required = false) final String agent) {
		
		// TODO: sanitize all user's values (#60)
		final String page = (String)request.getAttribute("javax.servlet.error.request_uri");
		final String ip   = request.getRemoteAddr();
		final User user   = (User)session.getAttribute("user");
		
		try {
			siteService.logAboutAbsentPage(page, user, ip, referer, agent);
		} catch (final Exception ex) {
			// intentionally ignored:
			// database error should not break showing of 404 page
			log.warn("Cannot log 404 error", ex);
		}
	}
	
}

