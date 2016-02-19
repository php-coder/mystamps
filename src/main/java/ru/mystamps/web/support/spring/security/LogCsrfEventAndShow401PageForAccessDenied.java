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
package ru.mystamps.web.support.spring.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.csrf.InvalidCsrfTokenException;
import org.springframework.security.web.csrf.MissingCsrfTokenException;

import ru.mystamps.web.service.SiteService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Sergey Chechenev
 */
public class LogCsrfEventAndShow401PageForAccessDenied extends AccessDeniedHandlerImpl {
	private final SiteService siteService;
	
	public LogCsrfEventAndShow401PageForAccessDenied(SiteService siteService, String errorPage) {
		super();
		super.setErrorPage(errorPage);
		this.siteService = siteService;
	}
	
	@Override
	public void handle(
		HttpServletRequest request,
		HttpServletResponse response,
		AccessDeniedException exception)
		throws IOException, ServletException {
		
		if (exception instanceof MissingCsrfTokenException) {
			siteService.logAboutMissingCsrfToken(request);
		} else if (exception instanceof InvalidCsrfTokenException) {
			siteService.logAboutInvalidCsrfToken(request);
		}

		super.handle(request, response, exception);
	}
	
}
