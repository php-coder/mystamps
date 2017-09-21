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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.web.header.HeaderWriter;

import ru.mystamps.web.Url;

/**
 * Implementation of {@link HeaderWriter} that is adding CSP header depending on the current URL.
 */
class ContentSecurityPolicyHeaderWriter implements HeaderWriter {

	private static final String COLLECTION_INFO_PAGE_PATTERN =
		Url.INFO_COLLECTION_PAGE.replace("{slug}", "");
	
	private static final String ADD_IMAGE_PAGE_PATTERN = "/series/(add|\\d+|\\d/(ask|image))";
	
	private static final String TOGGLZ_PAGES_PATTERN = Url.TOGGLZ_CONSOLE_PAGE + '/';
	
	// default policy prevents loading resources from any source
	private static final String DEFAULT_SRC = "default-src 'none'";
	
	// - 'self' is required for uploaded images and its previews
	// - 'https://cdn.rawgit.com' is required by languages.png (TODO: GH #246)
	// - 'https://raw.githubusercontent.com' is required by languages.png
	// CheckStyle: ignore LineLength for next 1 line
	private static final String IMG_SRC = "img-src 'self' https://cdn.rawgit.com https://raw.githubusercontent.com";
	
	// - 'self' is required by glyphicons-halflings-regular.woff2 from bootstrap
	private static final String FONT_SRC = "font-src 'self'";
	
	// CheckStyle: ignore LineLength for next 1 line
	private static final String REPORT_URI = "report-uri https://mystamps.report-uri.io/r/default/csp/reportOnly";
	
	// - 'self' is required for our own CSS files
	// - 'https://cdn.rawgit.com' is required by languages.min.css (TODO: GH #246)
	private static final String STYLE_SRC = "style-src 'self' https://cdn.rawgit.com";
	
	// - 'sha256-Dpm...' is required for 'box-shadow: none; border: 0px;' inline CSS
	// that are using on /series/add and /series/{id} pages.
	private static final String STYLE_SERIES_ADD_IMAGE =
		" 'sha256-DpmxvnMJIlwkpmmAANZYNzmyfnX2PQCBDO4CB2BFjzU='";
	
	// - 'https://www.gstatic.com' is required by Google Charts
	// - 'sha256-/kX...' is required for 'overflow: hidden;' inline CSS for Google Charts.
	private static final String STYLE_COLLECTION_INFO =
		" https://www.gstatic.com 'sha256-/kXZODfqoc2myS1eI6wr0HH8lUt+vRhW8H/oL+YJcMg='";
	
	// - 'sha256-biL...' is required for 'display: none;' inline CSS for Togglz
	// - 'sha256-zQD...' is required for 'width: 100%; text-align: center;' inline CSS for Togglz
	private static final String STYLE_TOGGLZ =
		" 'sha256-biLFinpqYMtWHmXfkA1BPeCY0/fNt46SAZ+BBk5YUog='"
		+ " 'sha256-zQDRfdePzsm4666fPPtpna61v74bryIt2Xu5qx2rn4A='";
	
	// - 'self' is required for our own JS files
	// - 'unsafe-inline' is required by jquery.min.js (that is using code inside of
	// event handlers. We can't use hashing algorithms because they aren't supported
	// for handlers. In future, we should get rid of jQuery or use
	// 'unsafe-hashed-attributes' from CSP3. Details:
	// https://github.com/jquery/jquery/blob/d71f6a53927ad02d/jquery.js#L1441-L1447
	// and https://w3c.github.io/webappsec-csp/#unsafe-hashed-attributes-usage)
	private static final String SCRIPT_SRC = "script-src 'self' 'unsafe-inline'";
	
	// - 'unsafe-eval' is required by loader.js from Google Charts
	// - 'https://www.gstatic.com' is required by Google Charts
	private static final String SCRIPT_COLLECTION_INFO = " 'unsafe-eval' https://www.gstatic.com";
	
	// - 'self' is required for AJAX requests from our scripts (country suggestions on /series/add)
	private static final String CONNECT_SRC = "connect-src 'self'";
	
	private static final char SEPARATOR = ';';
	
	private static final int MIN_HEADER_LENGTH =
		DEFAULT_SRC.length()
		+ IMG_SRC.length()
		+ FONT_SRC.length()
		+ REPORT_URI.length()
		+ STYLE_SRC.length()
		+ SCRIPT_SRC.length()
		// number of separators between directives
		+ 5;
	
	@Override
	public void writeHeaders(HttpServletRequest request, HttpServletResponse response) {
		String uri = request.getRequestURI();
		response.setHeader("Content-Security-Policy-Report-Only", constructDirectives(uri));
	}

	private static String constructDirectives(String uri) {
		boolean onCollectionInfoPage = uri.startsWith(COLLECTION_INFO_PAGE_PATTERN);
		
		StringBuilder sb = new StringBuilder(MIN_HEADER_LENGTH);
		
		sb.append(DEFAULT_SRC).append(SEPARATOR)
		  .append(IMG_SRC).append(SEPARATOR)
		  .append(FONT_SRC).append(SEPARATOR)
		  .append(REPORT_URI).append(SEPARATOR)
		  .append(STYLE_SRC);
		
		if (onCollectionInfoPage) {
			sb.append(STYLE_COLLECTION_INFO);
		
		} else if (uri.equals(Url.ADD_SERIES_PAGE) || uri.matches(ADD_IMAGE_PAGE_PATTERN)) {
			sb.append(STYLE_SERIES_ADD_IMAGE);
		
		} else if (uri.startsWith(TOGGLZ_PAGES_PATTERN)) {
			sb.append(STYLE_TOGGLZ);
		}
		
		sb.append(SEPARATOR)
		  .append(SCRIPT_SRC);
		
		if (onCollectionInfoPage) {
			sb.append(SCRIPT_COLLECTION_INFO);
		}
		
		if (uri.equals(Url.ADD_SERIES_PAGE)) {
			sb.append(SEPARATOR)
			  .append(CONNECT_SRC);
		}
		
		return sb.toString();
	}
	
}
