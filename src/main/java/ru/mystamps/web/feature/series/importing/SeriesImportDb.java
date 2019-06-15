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
package ru.mystamps.web.feature.series.importing;

@SuppressWarnings("PMD.CommentDefaultAccessModifier")
public final class SeriesImportDb {
	
	static final class SeriesImportRequest {
		static final int URL_LENGTH = 767;
	}
	
	public static final class SeriesImportRequestStatus {
		// see initiate-series_import_request_statuses-table changeset
		// in src/main/resources/liquibase/version/0.4/2017-11-08--import_series.xml
		// @todo #687 replace set of strings by enum
		public static final String UNPROCESSED           = "Unprocessed";
		public static final String DOWNLOADING_SUCCEEDED = "DownloadingSucceeded";
		public static final String DOWNLOADING_FAILED    = "DownloadingFailed";
		public static final String PARSING_SUCCEEDED     = "ParsingSucceeded";
		public static final String PARSING_FAILED        = "ParsingFailed";
		public static final String IMPORT_SUCCEEDED      = "ImportSucceeded";
	}
	
	static final class SeriesImportParsedData {
		// see the following migration:
		// 2018-07-05--series_import_parsed_data_michel_numbers_field.xml
		static final int MICHEL_NUMBERS_LENGTH = 19;
	}
	
}
