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
