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
package ru.mystamps.web;

import ru.mystamps.web.feature.account.AccountUrl;
import ru.mystamps.web.feature.category.CategoryUrl;
import ru.mystamps.web.feature.collection.CollectionUrl;
import ru.mystamps.web.feature.country.CountryUrl;
import ru.mystamps.web.feature.image.ImageUrl;
import ru.mystamps.web.feature.participant.ParticipantUrl;
import ru.mystamps.web.feature.report.ReportUrl;
import ru.mystamps.web.feature.series.SeriesUrl;
import ru.mystamps.web.feature.series.importing.SeriesImportUrl;
import ru.mystamps.web.feature.series.importing.sale.SeriesSalesImportUrl;
import ru.mystamps.web.feature.site.ResourceUrl;
import ru.mystamps.web.feature.site.SiteUrl;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds path to site and all URLs.
 *
 * Should be used anywhere instead of hard-coded paths.
 *
 * @author Slava Semushin
 */
public final class Url {
	
	// see also src/main/resources/application-test.properties
	public static final String H2_CONSOLE_PAGE = "/console";
	
	// CheckStyle: ignore LineLength for next 3 lines
	private static final String BOOTSTRAP_CSS      = "/public/bootstrap/3.4.1/css/bootstrap.min.css";
	private static final String BOOTSTRAP_JS       = "/public/bootstrap/3.4.1/js/bootstrap.min.js";
	private static final String JQUERY_JS          = "/public/jquery/1.9.1/jquery.min.js";
	
	// CheckStyle: ignore LineLength for next 3 lines
	// FIXME: use minimal version of CSS file when it will be available (https://github.com/webjars/selectize.js/issues/3)
	private static final String SELECTIZE_CSS      = "/public/selectize/0.12.5/css/selectize.bootstrap3.css";
	private static final String SELECTIZE_JS       = "/public/selectize/0.12.5/js/standalone/selectize.min.js";
	
	// see also pom.xml and ru.mystamps.web.config.MvcConfig#addResourceHandlers()
	// CheckStyle: ignore LineLength for next 5 lines
	private static final String BOOTSTRAP_CSS_CDN  = "https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css";
	private static final String BOOTSTRAP_JS_CDN   = "https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js";
	private static final String JQUERY_JS_CDN      = "https://yandex.st/jquery/1.9.1/jquery.min.js";
	private static final String SELECTIZE_CSS_CDN  = "https://cdnjs.cloudflare.com/ajax/libs/selectize.js/0.12.5/css/selectize.bootstrap3.min.css";
	private static final String SELECTIZE_JS_CDN   = "https://cdnjs.cloudflare.com/ajax/libs/selectize.js/0.12.5/js/standalone/selectize.min.js";
	
	private Url() {
	}
	
	public static Map<String, String> asMap(boolean production) {
		boolean serveContentFromSingleHost = !production;
		
		// Not all URLs are listed here but only those that are being used on views
		Map<String, String> map = new HashMap<>();
		AccountUrl.exposeUrlsToView(map);
		CategoryUrl.exposeUrlsToView(map);
		CountryUrl.exposeUrlsToView(map);
		CollectionUrl.exposeUrlsToView(map);
		ParticipantUrl.exposeUrlsToView(map);
		ReportUrl.exposeUrlsToView(map);
		ResourceUrl.exposeUrlsToView(map);
		SeriesUrl.exposeUrlsToView(map);
		SeriesImportUrl.exposeUrlsToView(map);
		SeriesSalesImportUrl.exposeUrlsToView(map);
		SiteUrl.exposeUrlsToView(map);
		
		map.put("PUBLIC_URL", production ? SiteUrl.PUBLIC_URL : SiteUrl.SITE);
		
		if (serveContentFromSingleHost) {
			ImageUrl.exposeResourcesToView(map, null);
			ResourceUrl.exposeResourcesToView(map, null);
			
			map.put("BOOTSTRAP_CSS", BOOTSTRAP_CSS);
			map.put("BOOTSTRAP_JS", BOOTSTRAP_JS);
			map.put("JQUERY_JS", JQUERY_JS);
			map.put("SELECTIZE_CSS", SELECTIZE_CSS);
			map.put("SELECTIZE_JS", SELECTIZE_JS);
		} else {
			ImageUrl.exposeResourcesToView(map, ResourceUrl.STATIC_RESOURCES_URL);
			ResourceUrl.exposeResourcesToView(map, ResourceUrl.STATIC_RESOURCES_URL);
			
			// Use CDN for the external resources like libraries
			map.put("BOOTSTRAP_CSS", BOOTSTRAP_CSS_CDN);
			map.put("BOOTSTRAP_JS", BOOTSTRAP_JS_CDN);
			map.put("JQUERY_JS", JQUERY_JS_CDN);
			map.put("SELECTIZE_CSS", SELECTIZE_CSS_CDN);
			map.put("SELECTIZE_JS", SELECTIZE_JS_CDN);
		}
		
		return map;
	}
	
}
