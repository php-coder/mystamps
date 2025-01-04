/*
 * Copyright (C) 2009-2025 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.feature.series.importing;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Filter that trims an empty object into null by removing corresponding fields.
 *
 * In order to prevent validation of an empty {@code ImportSeriesSalesForm},
 * this filter turns it into {@code null}.
 *
 * @see ImportSeriesForm
 * @see ImportSeriesSalesForm
 * @see SeriesImportController#processImportSeriesForm
 */
public class ImportSeriesFormTrimmerFilter extends OncePerRequestFilter {
	
	private static final Logger LOG = LoggerFactory.getLogger(ImportSeriesFormTrimmerFilter.class);

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain chain)
		throws ServletException, IOException {

		HttpServletRequest wrappedRequest = request;
		try {
			if (HttpMethod.POST.matches(request.getMethod())) {
				LOG.debug("Handling request {} {}", request.getMethod(), request.getRequestURI());
				
				String sellerId = request.getParameter("seriesSale.sellerId");
				String price    = request.getParameter("seriesSale.price");
				String currency = request.getParameter("seriesSale.currency");
				
				LOG.debug(
					"seriesSale: sellerId = '{}', price = '{}', currency = '{}'",
					sellerId,
					price,
					currency
				);
				
				boolean atLeastOneIsSet = sellerId != null
					|| price != null
					|| currency != null;
				
				boolean allEmpty = StringUtils.isEmpty(sellerId)
					&& StringUtils.isEmpty(price)
					&& StringUtils.isEmpty(currency);
				
				if (atLeastOneIsSet && allEmpty) {
					LOG.debug("Remove empty seriesSale.* parameters from request");
					wrappedRequest = new ErasePrefixedParametersWrapper(request, "seriesSale.");
				}
			}
			
		} finally {
			chain.doFilter(wrappedRequest, response);
		}
		
	}
	
	public static class ErasePrefixedParametersWrapper extends HttpServletRequestWrapper {
		private final String prefix;
		
		/* default */ ErasePrefixedParametersWrapper(HttpServletRequest request, String prefix) {
			super(request);
			this.prefix = prefix;
		}
		
		@Override
		public String getParameter(String name) {
			return name.startsWith(prefix) ? null : super.getParameter(name);
		}
		
		@Override
		public Map<String, String[]> getParameterMap() {
			return super.getParameterMap()
				.entrySet()
				.stream()
				.filter(entry -> !entry.getKey().startsWith(prefix))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		}

		@Override
		public Enumeration<String> getParameterNames() {
			List<String> result = Collections.list(super.getParameterNames());
			
			result.removeIf(name -> name.startsWith(prefix));
			
			return Collections.enumeration(result);
		}

		@Override
		public String[] getParameterValues(String name) {
			return name.startsWith(prefix) ? null : super.getParameterValues(name);
		}
	}
	
}
