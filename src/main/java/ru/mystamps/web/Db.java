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

public final class Db {
	
	public static final class Category {
		public static final int NAME_LENGTH = 50;
	}
	
	public static final class Country {
		public static final int NAME_LENGTH = 50;
	}
	
	public static final class Images {
		public static final int FILENAME_LENGTH = 255;
	}
	
	public static final class TransactionParticipant {
		public static final int NAME_LENGTH = 50;
		public static final int URL_LENGTH  = 255;
	}
	
	public static final class Series {
		public static final int COMMENT_LENGTH = 255;
	}
	
	public static final class SeriesImportRequest {
		public static final int URL_LENGTH = 767;
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
	
	public static final class SeriesImportParsedData {
		// see the following migration:
		// 2018-07-05--series_import_parsed_data_michel_numbers_field.xml
		public static final int MICHEL_NUMBERS_LENGTH = 19;
	}
	
	public static final class SeriesSales {
		public static final int TRANSACTION_URL_LENGTH = 255;
	}
	
	public static final class SuspiciousActivity {
		public static final int PAGE_URL_LENGTH     = 100;
		public static final int METHOD_LENGTH       = 7;
		public static final int REFERER_PAGE_LENGTH = 255;
		public static final int USER_AGENT_LENGTH   = 255;
	}
	
	public static final class UsersActivation {
		public static final int ACTIVATION_KEY_LENGTH = 10;
		public static final int EMAIL_LENGTH          = 255;
	}
	
	public static final class User {
		public static final int LOGIN_LENGTH = 15;
		public static final int NAME_LENGTH  = 100;
	}
	
}
