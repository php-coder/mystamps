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

package ru.mystamps.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ru.mystamps.web.SiteMap.MAINTENANCE_PAGE_URL;

public class MaintenanceFilter implements Filter {
	
	private FilterConfig config;
	private final Logger log = LoggerFactory.getLogger(MaintenanceFilter.class);
	
	@Override
	public void init(final FilterConfig config) {
		this.config = config;
	}
	
	@Override
	public void doFilter(final ServletRequest request,
						final ServletResponse response,
						final FilterChain chain)
				throws IOException, ServletException {
		
		final String mode = config.getInitParameter("enableMaintainanceMode");
		if (mode == null || mode.equals("")) {
			throw new ServletException("enableMaintainanceMode parameter not set!");
		}
		
		final String allowedIP = config.getInitParameter("allowedIP");
		if (allowedIP == null || allowedIP.equals("")) {
			throw new ServletException("allowedIP parameter not set!");
		}
		
		if (mode.equalsIgnoreCase("yes")) {
			
			final String userIP = request.getRemoteHost();
			if (userIP == null || userIP.equals("")) {
				log.warn("Cannot get user IP address");
			
			} else if (!allowedIP.equals(userIP)) {
				final RequestDispatcher dispatch =
					request.getRequestDispatcher(MAINTENANCE_PAGE_URL);
				dispatch.forward(request, response);
				return;
			}
		}
		
		chain.doFilter(request, response);
	}
	
	@Override
	public void destroy() {
	}
}

