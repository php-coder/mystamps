/*
 * Copyright (C) 2009-2022 Slava Semushin <slava.semushin@gmail.com>
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
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import ru.mystamps.web.feature.account.AccountUrl;
import ru.mystamps.web.feature.category.CategoryUrl;
import ru.mystamps.web.feature.country.CountryUrl;
import ru.mystamps.web.feature.series.SeriesUrl;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Controller
public class RobotsTxtController {
	private static final Logger LOG = LoggerFactory.getLogger(RobotsTxtController.class);
	
	@GetMapping(SiteUrl.ROBOTS_TXT)
	@SuppressWarnings("PMD.AvoidDuplicateLiterals")
	public void generateRobotsTxt(HttpServletResponse response) {
		response.setContentType(MediaType.TEXT_PLAIN_VALUE);
		response.setCharacterEncoding("UTF-8");
		
		try {
			PrintWriter writer = response.getWriter();
			
			writer.println("# robots.txt for " + SiteUrl.PUBLIC_URL);
			writer.println("User-Agent: *");
			writer.println("Disallow: " + AccountUrl.REGISTRATION_PAGE);
			writer.println("Disallow: " + AccountUrl.ACTIVATE_ACCOUNT_PAGE);
			writer.println("Disallow: " + AccountUrl.AUTHENTICATION_PAGE);
			writer.println("Disallow: " + AccountUrl.LOGIN_PAGE);
			writer.println("Disallow: " + CategoryUrl.ADD_CATEGORY_PAGE);
			writer.println("Disallow: " + CountryUrl.ADD_COUNTRY_PAGE);
			writer.println("Disallow: " + SeriesUrl.ADD_SERIES_PAGE);
			writer.println("Disallow: " + SiteUrl.FORBIDDEN_PAGE);
			writer.println("Disallow: " + SiteUrl.NOT_FOUND_PAGE);
			writer.println("Disallow: " + SiteUrl.INTERNAL_ERROR_PAGE);
			writer.println("Sitemap: " + SiteUrl.PUBLIC_URL + SiteUrl.SITEMAP_XML);
		} catch (IOException ex) {
			LOG.error("Can't return robots.txt: {}", ex.getMessage());
		}
	}
	
}

