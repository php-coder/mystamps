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
package ru.mystamps.web.validation;

import ru.mystamps.web.feature.series.SeriesDb.Series;
import ru.mystamps.web.feature.series.importing.SeriesImportDb.SeriesImportRequest;
import ru.mystamps.web.feature.series.sale.SeriesSalesDb.SeriesSales;

public final class ValidationRules {
	
	public static final int MIN_STAMPS_IN_SERIES = 1;
	public static final int MAX_STAMPS_IN_SERIES = 50;
	public static final int MIN_RELEASE_YEAR     = 1840;
	public static final int MAX_SERIES_COMMENT_LENGTH = Series.COMMENT_LENGTH;
	public static final String CATALOG_NUMBERS_REGEXP = "[1-9][0-9]{0,3}(,[1-9][0-9]{0,3})*";
	@SuppressWarnings({ "PMD.LongVariable", "checkstyle:linelength" })
	public static final String CATALOG_NUMBERS_AND_LETTERS_REGEXP = "[1-9][0-9]{0,3}[a-z]?(,[1-9][0-9]{0,3}[a-z]?)*";
	
	public static final int SERIES_SALES_URL_MAX_LENGTH = SeriesSales.TRANSACTION_URL_LENGTH;
	
	public static final int IMPORT_REQUEST_URL_MAX_LENGTH  = SeriesImportRequest.URL_LENGTH;
	
	/** Maximum uploading image size in kilobytes. */
	public static final long MAX_IMAGE_SIZE = 500;
	
	public static final int MAX_DAYS_IN_MONTH = 31;
	public static final int MAX_MONTHS_IN_YEAR = 12;
	
	private ValidationRules() {
	}
	
}

