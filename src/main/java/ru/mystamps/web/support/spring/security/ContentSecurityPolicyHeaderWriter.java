/*
 * Copyright (C) 2009-2019 Slava Semushin <slava.semushin@gmail.com>
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
import org.springframework.security.web.header.HeaderWriter;
import ru.mystamps.web.feature.collection.CollectionUrl;
import ru.mystamps.web.feature.series.SeriesUrl;
import ru.mystamps.web.feature.site.SiteUrl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.regex.Pattern;

/**
 * Implementation of {@link HeaderWriter} that is adding CSP header depending on the current URL.
 */
@RequiredArgsConstructor
class ContentSecurityPolicyHeaderWriter implements HeaderWriter {

	private static final String COLLECTION_INFO_PAGE_PATTERN =
		CollectionUrl.INFO_COLLECTION_PAGE.replace("{slug}", "");
	
	private static final Pattern SERIES_INFO_PAGE_PATTERN =
		Pattern.compile(SeriesUrl.SERIES_INFO_PAGE_REGEXP);
	
	private static final String ADD_IMAGE_PAGE_PATTERN = "/series/(add|\\d+|\\d+/(ask|image))";
	
	// see also spring.h2.console.path in application-test.properties and SecurityConfig
	private static final String H2_CONSOLE_PATTERN = "/console/";
	
	// default policy prevents loading resources from any source
	private static final String DEFAULT_SRC = "default-src 'none'";
	
	// - 'https://cdn.jsdelivr.net' is required by languages.png (FIXME: GH #246)
	private static final String IMG_SRC = "img-src https://cdn.jsdelivr.net";
	
	// - 'self' is required for uploaded images and its previews
	private static final String IMG_SRC_SELF = " 'self'";
	
	// - 'https://stamps.filezz.ru' is required for uploaded images and its previews
	private static final String IMG_SRC_ALT = " https://stamps.filezz.ru";
	
	private static final String FONT_SRC = "font-src ";
	
	// - 'self' is required by glyphicons-halflings-regular.woff2 from bootstrap
	private static final String FONT_SRC_SELF = "'self'";
	
	// - 'https://maxcdn.bootstrapcdn.com' is required by glyphicons-halflings-regular.woff2
	private static final String FONT_SRC_CDN = "https://maxcdn.bootstrapcdn.com";
	
	private static final String REPORT_URI = "report-uri ";
	
	// - 'https://cdn.jsdelivr.net' is required by languages.min.css (FIXME: GH #246)
	private static final String STYLE_SRC = "style-src https://cdn.jsdelivr.net ";
	
	// - 'self' is required for our own CSS files
	private static final String STYLES_SELF = "'self'";
	
	// - 'https://stamps.filezz.ru' is required for our own CSS files
	private static final String STYLES_ALT = "https://stamps.filezz.ru";
	
	// - 'https://maxcdn.bootstrapcdn.com' is required for bootstrap.min.js
	private static final String STYLES_CDN = "https://maxcdn.bootstrapcdn.com";
	
	// - 'sha256-Dpm...' is required for 'box-shadow: none; border: 0px;' inline CSS
	// that are using on /series/add and /series/{id} pages.
	private static final String STYLE_SERIES_ADD_IMAGE =
		" 'sha256-DpmxvnMJIlwkpmmAANZYNzmyfnX2PQCBDO4CB2BFjzU='";
	
	// - 'https://cdnjs.cloudflare.com' is required by selectize.min.js
	private static final String STYLE_SERIES_ADD_PAGE = " https://cdnjs.cloudflare.com";
	
	// - 'https://www.gstatic.com' is required by Google Charts
	// - 'sha256-/kX...' is required for 'overflow: hidden;' inline CSS for Google Charts.
	private static final String STYLE_COLLECTION_INFO =
		" https://www.gstatic.com 'sha256-/kXZODfqoc2myS1eI6wr0HH8lUt+vRhW8H/oL+YJcMg='";
	
	// - 'sha256-biL...' is required for 'display: none;' inline CSS
	// - 'sha256-ZdH...' is required for 'display: none' inline CSS
	// - 'sha256-aqN...' is required for 'display:none' inline CSS
	// - 'sha256-tIs...' is required for 'text-decoration: none;' inline CSS
	// - 'sha256-VPM...' is required for 'vertical-align: middle;' inline CSS
	// - 'sha256-CDs...' is required for 'padding:0px' inline CSS
	// - 'sha256-65m...' is required for 'white-space:nowrap' inline CSS
	// - 'sha256-xSK...' is required for 'margin: 0px; padding: 0px;' inline CSS
	// - 'sha256-Jnn...' is required for 'padding:0;width:10px;height:10px;' inline CSS
	// - 'sha256-yBh...' is required for 'margin: 20px' inline CSS
	// - 'sha256-ec+...' is required for 'width:300px' inline CSS
	// - 'sha256-rqk...' is required for 'width:300px;' inline CSS
	// - 'sha256-PGJ...' is required for 'width:200px;' inline CSS
	private static final String STYLE_H2_CONSOLE =
		" 'sha256-biLFinpqYMtWHmXfkA1BPeCY0/fNt46SAZ+BBk5YUog='"
		+ " 'sha256-ZdHxw9eWtnxUb3mk6tBS+gIiVUPE3pGM470keHPDFlE='"
		+ " 'sha256-aqNNdDLnnrDOnTNdkJpYlAxKVJtLt9CtFLklmInuUAE='"
		+ " 'sha256-tIs8OfjWm8MHgPJrHv7mM4wvA/FDFcra3Pd5icRMX+k='"
		+ " 'sha256-VPm872V2JvE+vhivDg7UeH+N9a9YzzqGGow5mzY48hc='"
		+ " 'sha256-CDs+xFw5uMoNgtE5XIrz5GXgs3O+/NFkYK2IK/vKSBE='"
		+ " 'sha256-65mkwZPt4V1miqNM9CcVYkrpnlQigG9H6Vi9OM/JCgY='"
		+ " 'sha256-xSKCQeN6yeCb4HCkijkjoBFHWdJFwmwDiFa3XlZZ6Bs='"
		+ " 'sha256-JnnwE+8wsBgf/bh1qyvAsUVHBgiTioeZ1NSUKff7mOM='"
		+ " 'sha256-yBhVF062O1IGu3ZngyEhh9l561VFLsJpdSxVtbwisRY='"
		+ " 'sha256-eC+jXvbVSsG0J4zQfR5fWxxUCqpaa5DZLbINjWNCu48='"
		+ " 'sha256-rqkMEwsWwrInJqctxmIaWOCFPV+Qmym3tMHH3wtq3Y0='"
		+ " 'sha256-PGJ8tjuz2DXGgB1Sie9pW8BrxBGK6EQndbLEkXd44T8='";
	
	// - 'unsafe-inline' is required by jquery.min.js (that is using code inside of
	// event handlers. We can't use hashing algorithms because they aren't supported
	// for handlers. In future, we should get rid of jQuery or use
	// 'unsafe-hashed-attributes' from CSP3. Details:
	// https://github.com/jquery/jquery/blob/d71f6a53927ad02d/jquery.js#L1441-L1447
	// and https://w3c.github.io/webappsec-csp/#unsafe-hashed-attributes-usage)
	private static final String SCRIPT_SRC = "script-src 'unsafe-inline' ";
	
	// - 'self' is required for our own JS files
	private static final String SCRIPTS_SELF = "'self'";

	// - 'https://stamps.filezz.ru' is required for our own JS files
	private static final String SCRIPTS_ALT = "https://stamps.filezz.ru";

	// - 'https://maxcdn.bootstrapcdn.com' is required for bootstrap.min.js
	// - 'https://yandex.st' is required for jquery.min.js
	private static final String SCRIPTS_CDN = "https://maxcdn.bootstrapcdn.com https://yandex.st";
	
	// - 'https://cdnjs.cloudflare.com' is required by selectize.bootstrap3.min.css
	private static final String SCRIPTS_SERIES_ADD_PAGE = " https://cdnjs.cloudflare.com";
	
	// - 'https://unpkg.com' is required by react/react-dom
	private static final String SCRIPTS_SERIES_INFO_PAGE = " https://unpkg.com";
	
	// - 'unsafe-eval' is required by loader.js from Google Charts
	// - 'https://www.gstatic.com' is required by Google Charts
	private static final String SCRIPT_COLLECTION_INFO = " 'unsafe-eval' https://www.gstatic.com";
	
	// - 'self' is required for AJAX requests from our scripts
	// (country suggestions on /series/add and series sale import on /series/{id})
	private static final String CONNECT_SRC = "connect-src 'self'";
	
	// - 'self' is required for frames on H2 webconsole
	private static final String CHILD_SRC = "child-src 'self'";
	
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
	
	private final boolean useCdn;
	private final boolean hasH2Console;
	private final String host;
	
	@Override
	public void writeHeaders(HttpServletRequest request, HttpServletResponse response) {
		String uri = request.getRequestURI();
		response.setHeader("Content-Security-Policy-Report-Only", constructDirectives(uri));
	}

	@SuppressWarnings({ "PMD.NPathComplexity", "PMD.ModifiedCyclomaticComplexity" })
	protected String constructDirectives(String uri) {
		boolean onCollectionInfoPage = uri.startsWith(COLLECTION_INFO_PAGE_PATTERN);
		boolean onAddSeriesPage = uri.equals(SeriesUrl.ADD_SERIES_PAGE);
		boolean onH2ConsolePage = hasH2Console && uri.startsWith(H2_CONSOLE_PATTERN);
		
		StringBuilder sb = new StringBuilder(MIN_HEADER_LENGTH);
		
		sb.append(DEFAULT_SRC).append(SEPARATOR)
		  .append(IMG_SRC).append(useCdn ? IMG_SRC_ALT : IMG_SRC_SELF).append(SEPARATOR)
		  .append(FONT_SRC).append(useCdn ?  FONT_SRC_CDN : FONT_SRC_SELF).append(SEPARATOR)
		  .append(REPORT_URI).append(host).append(SiteUrl.CSP_REPORTS_HANDLER).append(SEPARATOR)
		  .append(STYLE_SRC);
		
		if (useCdn) {
			sb.append(STYLES_ALT).append(' ').append(STYLES_CDN);
		} else {
			sb.append(STYLES_SELF);
		}
		
		if (onCollectionInfoPage) {
			sb.append(STYLE_COLLECTION_INFO);
		
		} else if (uri.matches(ADD_IMAGE_PAGE_PATTERN)) {
			sb.append(STYLE_SERIES_ADD_IMAGE);
			
			if (onAddSeriesPage) {
				sb.append(STYLE_SERIES_ADD_PAGE);
			}
		
		} else if (onH2ConsolePage) {
			sb.append(STYLE_H2_CONSOLE);
		}
		
		sb.append(SEPARATOR)
		  .append(SCRIPT_SRC);
		
		if (useCdn) {
			sb.append(SCRIPTS_ALT).append(' ').append(SCRIPTS_CDN);
		} else {
			sb.append(SCRIPTS_SELF);
		}
		
		if (onCollectionInfoPage) {
			sb.append(SCRIPT_COLLECTION_INFO);
		
		} else if (onAddSeriesPage) {
			sb.append(SCRIPTS_SERIES_ADD_PAGE)
			  .append(SEPARATOR)
			  .append(CONNECT_SRC);
		
		} else if (onH2ConsolePage) {
			sb.append(SEPARATOR)
			  .append(CHILD_SRC);
		} else if (SERIES_INFO_PAGE_PATTERN.matcher(uri).matches()) {
			// anonymous and users without a required authority actually don't need these directives
			sb.append(SCRIPTS_SERIES_INFO_PAGE)
			  .append(SEPARATOR)
			  .append(CONNECT_SRC);
		}
		
		return sb.toString();
	}
	
}
