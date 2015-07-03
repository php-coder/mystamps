/*
 * Copyright (C) 2009-2015 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import ru.mystamps.web.service.dto.SitemapInfoDto;
import ru.mystamps.web.service.SeriesService;
import ru.mystamps.web.Url;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class SitemapController {
	
	// TODO: convert dates to UTC and omit server-specific timezone part
	// According to http://www.w3.org/TR/NOTE-datetime
	private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mmXXX";
	
	private final SeriesService seriesService;
	
	@RequestMapping(Url.SITEMAP_XML)
	public void getSitemapXml(HttpServletResponse response) {
		response.setContentType("application/xml");
		response.setCharacterEncoding("UTF-8");
		
		DateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
		
		try {
			PrintWriter writer = response.getWriter();
			
			writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			writer.println("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">");
			
			writer.print("\t<url>\t\t<loc>");
			writer.print(Url.PUBLIC_URL);
			writer.print(Url.INDEX_PAGE);
			writer.println("</loc>\t</url>");
			
			for (SitemapInfoDto item : seriesService.findAllForSitemap()) {
				writer.println("\t<url>");
				
				writer.print("\t\t<loc>");
				writer.print(createLocEntry(item));
				writer.println("</loc>");
				
				writer.print("\t\t<lastmod>");
				writer.print(createLastModEntry(dateFormatter, item));
				writer.println("</lastmod>");
				
				writer.println("\t</url>");
			}
			
			writer.println("</urlset>");
		} catch (IOException ex) {
			LOG.error("Can't return sitemap.xml: {}", ex.getMessage());
		}
	}
	
	private static String createLocEntry(SitemapInfoDto item) {
		return Url.PUBLIC_URL + Url.INFO_SERIES_PAGE.replace("{id}", String.valueOf(item.getId()));
	}
	
	private static String createLastModEntry(DateFormat dateFormatter, SitemapInfoDto item) {
		return dateFormatter.format(item.getUpdatedAt());
	}
	
}

