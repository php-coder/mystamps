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

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.csrf.InvalidCsrfTokenException;
import org.springframework.security.web.csrf.MissingCsrfTokenException;

import ru.mystamps.web.entity.User;
import ru.mystamps.web.service.SiteService;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * @author Sergey Chechenev
 */
@RequiredArgsConstructor
public class LogCsrfEventAndShow401PageForAccessDenied implements AccessDeniedHandler {
	private final SiteService siteService;
	private final String errorPage;
	
	@Override
	public void handle(
		HttpServletRequest request,
		HttpServletResponse response,
		AccessDeniedException exception)
		throws IOException, ServletException {
		
		if (exception instanceof MissingCsrfTokenException) {
			logAboutMissingCsrfToken(request);
		}
		
		if (exception instanceof InvalidCsrfTokenException) {
			logAboutInvalidCsrfToken(request);
		}
		
		if (!response.isCommitted()) {
			if (errorPage != null) {
				request.setAttribute(
					WebAttributes.ACCESS_DENIED_403,
					exception
				);
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				RequestDispatcher dispatcher = request.getRequestDispatcher(errorPage);
				dispatcher.forward(request, response);
			}
		}
	}
	
	private void logAboutMissingCsrfToken(HttpServletRequest request) {
		siteService.logAboutMissingCsrfToken(
			request.getRequestURI(),
			request.getMethod(),
			getUserId(),
			request.getRemoteAddr(),
			request.getHeader("referer"),
			request.getHeader("user-agent")
		);
	}
	
	private void logAboutInvalidCsrfToken(HttpServletRequest request) {
		siteService.logAboutInvalidCsrfToken(
			request.getRequestURI(),
			request.getMethod(),
			getUserId(),
			request.getRemoteAddr(),
			request.getHeader("referer"),
			request.getHeader("user-agent")
		);
	}
	
	private Integer getUserId() {
		Optional<Authentication> authentication = Optional
			.ofNullable(SecurityContextHolder.getContext().getAuthentication());
		
		return authentication
			.map(Authentication::getDetails)
			.filter(CustomUserDetails.class::isInstance)
			.map(CustomUserDetails.class::cast)
			.map(CustomUserDetails::getUser)
			.map(User::getId)
			.orElse(null);
	}
	
}
