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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import ru.mystamps.web.Url;
import ru.mystamps.web.feature.account.AccountUrl;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Controller
public class RobotsTxtController {
	private static final Logger LOG = LoggerFactory.getLogger(RobotsTxtController.class);
	
	@GetMapping(Url.ROBOTS_TXT)
	@SuppressWarnings("PMD.AvoidDuplicateLiterals")
	public void getRobotsText(HttpServletResponse response) {
		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");
		
		try {
			PrintWriter writer = response.getWriter();
			
			writer.println("# robots.txt for " + Url.PUBLIC_URL);
			writer.println("User-Agent: *");
			writer.println("Disallow: " + AccountUrl.REGISTRATION_PAGE);
			writer.println("Disallow: " + AccountUrl.ACTIVATE_ACCOUNT_PAGE);
			writer.println("Disallow: " + AccountUrl.AUTHENTICATION_PAGE);
			writer.println("Disallow: " + AccountUrl.LOGIN_PAGE);
			writer.println("Disallow: " + Url.ADD_COUNTRY_PAGE);
			writer.println("Disallow: " + Url.ADD_SERIES_PAGE);
			writer.println("Disallow: " + Url.ADD_CATEGORY_PAGE);
			writer.println("Disallow: " + Url.FORBIDDEN_PAGE);
			writer.println("Disallow: " + Url.NOT_FOUND_PAGE);
			writer.println("Disallow: " + Url.INTERNAL_ERROR_PAGE);
			writer.println("Sitemap: " + Url.PUBLIC_URL + Url.SITEMAP_XML);
		} catch (IOException ex) {
			LOG.error("Can't return robots.txt: {}", ex.getMessage());
		}
	}
	
}

