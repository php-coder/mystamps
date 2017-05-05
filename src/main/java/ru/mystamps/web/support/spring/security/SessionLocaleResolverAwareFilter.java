/*
 * Copyright (C) 2009-2017 Slava Semushin <slava.semushin@gmail.com>
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

import java.io.IOException;
import java.util.Locale;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.WebUtils;

/**
 * Filter sets locale by looking up in the session or uses English as a fallback.
 *
 * Unfortunately RequestContextFilter doesn't respect locale that may be set by
 * SessionLocaleResolver. This leads to improperly localized error messages from Spring Security.
 * This filter fixes such behavior: it looks up locale in the session first or uses default
 * locale (English). To be able to overwrite locale that was set by RequestContextFilter,
 * filter should be invoked after it.
 *
 * @author Slava Semushin
 */
class SessionLocaleResolverAwareFilter implements Filter {
	private static final Logger LOG =
		LoggerFactory.getLogger(SessionLocaleResolverAwareFilter.class);
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// Intentionally empty: nothing to initialize
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
		throws IOException, ServletException {
		
		try {
			HttpServletRequest req = (HttpServletRequest)request;
			LOG.debug("Handling request {} {}", req.getMethod(), req.getRequestURI());
			
			// NB: This won't work if the name of session attribute is
			// modified with SessionLocaleResolver.setLocaleAttributeName() method.
			Locale locale = (Locale)WebUtils.getSessionAttribute(
				req,
				SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME
			);
			
			if (locale == null) {
				locale = Locale.ENGLISH;
				LOG.debug("Locale reset to 'en' (default)");
			} else {
				LOG.debug("Locale reset to '{}' (from session)", locale);
			}
			
			LocaleContextHolder.setLocale(locale);
			
		} catch (RuntimeException ex) { // NOPMD: AvoidCatchingGenericException
			LOG.warn("Couldn't handle request: {}", ex);
		
		} finally {
			chain.doFilter(request, response);
		}
	}
	
	@Override
	public void destroy() {
		// Intentionally empty: nothing to do
	}
	
}
