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
		
		ResourceUrl.exposeWebjarResourcesToView(map, production);
		
		map.put("PUBLIC_URL", production ? SiteUrl.PUBLIC_URL : SiteUrl.SITE);
		
		if (serveContentFromSingleHost) {
			ImageUrl.exposeResourcesToView(map, null);
			ResourceUrl.exposeResourcesToView(map, null);
		} else {
			ImageUrl.exposeResourcesToView(map, ResourceUrl.STATIC_RESOURCES_URL);
			ResourceUrl.exposeResourcesToView(map, ResourceUrl.STATIC_RESOURCES_URL);
		}
		
		return map;
	}
	
}
