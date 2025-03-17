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
package ru.mystamps.web.feature.site;

import java.util.Map;

/**
 * Static resources URLs.
 *
 * Should be used everywhere instead of hard-coded paths.
 *
 * @author Slava Semushin
 */
public final class ResourceUrl {
	
	public static final String STATIC_RESOURCES_URL = "https://stamps.filezz.ru";
	
	// MUST be updated when any of our resources were modified
	public static final String RESOURCES_VERSION = "v0.4.7.0";

	private static final String CATALOG_UTILS_JS      = "/public/js/" + RESOURCES_VERSION + "/utils/CatalogUtils.min.js";
	private static final String COLLECTION_INFO_JS     = "/public/js/" + RESOURCES_VERSION + "/collection/info.min.js";
	private static final String DATE_UTILS_JS          = "/public/js/" + RESOURCES_VERSION + "/utils/DateUtils.min.js";
	private static final String MAIN_CSS               = "/static/"    + RESOURCES_VERSION + "/styles/main.min.css";
	private static final String PARTICIPANT_ADD_JS     = "/public/js/" + RESOURCES_VERSION + "/participant/add.min.js";
	private static final String SERIES_ADD_JS          = "/public/js/" + RESOURCES_VERSION + "/series/add.min.js";
	private static final String SERIES_INFO_JS         = "/public/js/" + RESOURCES_VERSION + "/series/info.min.js";
	private static final String SALE_IMPORT_FORM_JS     = "/public/js/" + RESOURCES_VERSION + "/components/SeriesSaleImportForm.min.js";
	private static final String SIMILAR_SERIES_FORM_JS  = "/public/js/" + RESOURCES_VERSION + "/components/SimilarSeriesForm.min.js";
	private static final String CATALOG_PRICE_FORM_JS   = "/public/js/" + RESOURCES_VERSION + "/components/AddCatalogPriceForm.min.js";
	private static final String RELEASE_YEAR_FORM_JS    = "/public/js/" + RESOURCES_VERSION + "/components/AddReleaseYearForm.min.js";
	private static final String HIDE_IMAGE_FORM_JS      = "/public/js/" + RESOURCES_VERSION + "/components/HideImageForm.min.js";
	private static final String SERIES_SALES_LIST_JS    = "/public/js/" + RESOURCES_VERSION + "/components/SeriesSalesList.min.js";

	private static final String BOOTSTRAP_LANGUAGE     = "https://cdn.jsdelivr.net/gh/usrz/bootstrap-languages@3ac2a3d2b27ac43a471cd99e79d378a03b2c6b5f/languages.min.css";
	private static final String FAVICON_ICO            = "/favicon.ico";

	// see also pom.xml and MvcConfig.addResourceHandlers()
	private static final String AXIOS_JS           = "0.19.2/dist/axios.min.js";
	private static final String BOOTSTRAP_CSS      = "/bootstrap/3.4.1/css/bootstrap.min.css";
	private static final String BOOTSTRAP_JS       = "/bootstrap/3.4.1/js/bootstrap.min.js";
	private static final String HTMX_JS            = "2.0.4/dist/htmx.min.js";
	private static final String JQUERY_JS          = "/jquery/1.9.1/jquery.min.js";
	private static final String REACT_JS           = "16.8.6/umd/react.production.min.js";
	private static final String REACT_DOM_JS       = "16.8.6/umd/react-dom.production.min.js";
	private static final String SELECTIZE_JS       = "/0.13.3/js/standalone/selectize.min.js";
	
	private static final String SELECTIZE_CSS      = "/public/selectize/0.13.3/css/selectize.bootstrap3.css";
	private static final String SELECTIZE_CSS_CDN  = "https://cdnjs.cloudflare.com/ajax/libs/selectize.js/0.13.3/css/selectize.bootstrap3.min.css";
	
	private ResourceUrl() {
	}
	
	public static void exposeUrlsToView(Map<String, String> urls) {
		urls.put("BOOTSTRAP_LANGUAGE", BOOTSTRAP_LANGUAGE);
	}
	
	public static void exposeResourcesToView(Map<String, String> resources, String host) {
		put(resources, host, "CATALOG_UTILS_JS", CATALOG_UTILS_JS);
		put(resources, host, "COLLECTION_INFO_JS", COLLECTION_INFO_JS);
		put(resources, host, "DATE_UTILS_JS", DATE_UTILS_JS);
		put(resources, host, "FAVICON_ICO", FAVICON_ICO);
		put(resources, host, "MAIN_CSS", MAIN_CSS);
		put(resources, host, "PARTICIPANT_ADD_JS", PARTICIPANT_ADD_JS);
		put(resources, host, "SERIES_ADD_JS", SERIES_ADD_JS);
		put(resources, host, "SERIES_INFO_JS", SERIES_INFO_JS);
		put(resources, host, "SALE_IMPORT_FORM_JS", SALE_IMPORT_FORM_JS);
		put(resources, host, "SIMILAR_SERIES_FORM_JS", SIMILAR_SERIES_FORM_JS);
		put(resources, host, "CATALOG_PRICE_FORM_JS", CATALOG_PRICE_FORM_JS);
		put(resources, host, "RELEASE_YEAR_FORM_JS", RELEASE_YEAR_FORM_JS);
		put(resources, host, "HIDE_IMAGE_FORM_JS", HIDE_IMAGE_FORM_JS);
		put(resources, host, "SERIES_SALES_LIST_JS", SERIES_SALES_LIST_JS);

	}
	
	// see also MvcConfig.addResourceHandlers()
	public static void exposeWebjarResourcesToView(Map<String, String> resources, boolean useCdn) {
		if (useCdn) {
			put(resources, "https://unpkg.com/axios@", "AXIOS_JS", AXIOS_JS);
			put(resources, "https://unpkg.com/htmx.org@", "HTMX_JS", HTMX_JS);
			put(resources, "https://maxcdn.bootstrapcdn.com", "BOOTSTRAP_CSS", BOOTSTRAP_CSS);
			put(resources, "https://maxcdn.bootstrapcdn.com", "BOOTSTRAP_JS", BOOTSTRAP_JS);
			put(resources, "https://yandex.st", "JQUERY_JS", JQUERY_JS);
			put(resources, "https://unpkg.com/react@", "REACT_JS", REACT_JS);
			put(resources, "https://unpkg.com/react-dom@", "REACT_DOM_JS", REACT_DOM_JS);
			put(resources, "https://cdnjs.cloudflare.com/ajax/libs/selectize.js", "SELECTIZE_JS", SELECTIZE_JS);
			put(resources, null, "SELECTIZE_CSS", SELECTIZE_CSS_CDN);
			return;
		}
		
		put(resources, "/public/axios/", "AXIOS_JS", AXIOS_JS);
		put(resources, "/public/htmx/", "HTMX_JS", HTMX_JS);
		put(resources, "/public", "BOOTSTRAP_CSS", BOOTSTRAP_CSS);
		put(resources, "/public", "BOOTSTRAP_JS", BOOTSTRAP_JS);
		put(resources, "/public", "JQUERY_JS", JQUERY_JS);
		put(resources, "/public/react/", "REACT_JS", REACT_JS);
		put(resources, "/public/react-dom/", "REACT_DOM_JS", REACT_DOM_JS);
		put(resources, "/public/selectize", "SELECTIZE_JS", SELECTIZE_JS);
		put(resources, null, "SELECTIZE_CSS", SELECTIZE_CSS);
	}
	
	private static void put(Map<String, String> map, String valuePrefix, String key, String value) {
		if (valuePrefix == null) {
			map.put(key, value);
			return;
		}

		map.put(key, valuePrefix + value);
	}
	
}
