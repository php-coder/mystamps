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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
public class CustomAccessDeniedHandler extends AccessDeniedHandlerImpl {
	@Autowired
	private SiteService siteService;
	
	@Override
	public void handle(
		HttpServletRequest request,
		HttpServletResponse response,
		AccessDeniedException accessDeniedException)
		throws IOException,	ServletException {
		
		if (accessDeniedException instanceof MissingCsrfTokenException
			|| accessDeniedException instanceof InvalidCsrfTokenException) {
			logAboutWrongCsrfToken(request);
		}
		super.handle(request, response, accessDeniedException);
	}
	
	private void logAboutWrongCsrfToken(HttpServletRequest request) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Integer userId = null;
		if (authentication != null) {
			CustomUserDetails userDetails = (CustomUserDetails)authentication.getDetails();
			userId = userDetails.getUser().getId();
		}
				
		siteService.logAboutWrongCsrfToken(
			request.getRequestURI(),
			request.getMethod(),
			userId,
			request.getRemoteAddr(),
			request.getHeader("referer"),
			request.getHeader("user-agent")
		);
	}
	
}
