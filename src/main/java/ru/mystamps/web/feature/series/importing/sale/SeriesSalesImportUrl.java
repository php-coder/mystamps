/*
 * Copyright (C) 2009-2020 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.feature.series.importing.sale;

import java.util.Map;

/**
 * URLs related to the import of the sales of the series.
 *
 * Should be used everywhere instead of hard-coded paths.
 *
 * @author Slava Semushin
 */
public final class SeriesSalesImportUrl {
	
	public static final String IMPORT_SERIES_SALES = "/series/sales/import";
	
	private SeriesSalesImportUrl() {
	}
	
	public static void exposeUrlsToView(Map<String, String> urls) {
		urls.put("IMPORT_SERIES_SALES", IMPORT_SERIES_SALES);
	}
	
}
