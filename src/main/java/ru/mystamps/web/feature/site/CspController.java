/*
 * Copyright (C) 2009-2023 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.feature.site;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

@RestController
public class CspController {
	private static final Logger LOG = LoggerFactory.getLogger(CspController.class);
	
	private static final String UNKNOWN = "<unknown>";

	private static final Pattern ORIGINAL_POLICY_PATTERN = Pattern.compile(
		"\"original-policy\":\"[^\"]+\","
	);
	
	@PostMapping(SiteUrl.CSP_REPORTS_HANDLER)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void handleReport(
		@RequestBody String body,
		HttpServletRequest request,
		@RequestHeader(name = HttpHeaders.USER_AGENT, defaultValue = UNKNOWN) String userAgent) {
		
		if (LOG.isWarnEnabled()) {
			String ip = StringUtils.defaultString(request.getRemoteAddr(), UNKNOWN);
			LOG.warn("CSP report from IP: {}, user agent: {}", ip, userAgent);

			// Omit "original-policy" as it's quite long and it's useless most of the time
			String report = ORIGINAL_POLICY_PATTERN.matcher(body).replaceFirst(StringUtils.EMPTY);
			LOG.warn(report);
		}
	}

}
