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
package ru.mystamps.web.feature.site;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@Slf4j
public class CspController {
	private static final String UNKNOWN = "<unknown>";

	// @todo #1058 /site/csp/reports: add integration tests
	@PostMapping(SiteUrl.CSP_REPORTS_HANDLER)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void handleReport(
		@RequestBody String body,
		HttpServletRequest request,
		@RequestHeader(name = "user-agent", defaultValue = UNKNOWN) String userAgent) {
		
		String ip = StringUtils.defaultString(request.getRemoteAddr(), UNKNOWN);
		
		log.warn("CSP report from IP: {}, user agent: {}", ip, userAgent);
		log.warn(body);
	}

}
