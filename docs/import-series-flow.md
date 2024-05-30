# Import a series from another resource

Importing a series from another resource is a 4-stages process:
1. [Create import request](#create-import-request): user with sufficient
   privileges submits request for the import to the system
1. [Download page](#download-page): the system downloads requested page and
   saves its content to database
1. [Parse page](#parse-page): the system parses downloaded page, extracts
   information out of page content and also saves this information to database
1. [Import a series](#import-a-series): user views extracted information,
   provides missing fragments, and confirm import. The system creates a new
   series.

## Create import request

```mermaid
sequenceDiagram
	title Series import flow: stage 1 (create import request)

	participant Browser
	participant SeriesImportController
	participant SeriesImportService
	participant EventPublisher

	note over Browser,SeriesImportController: request import series form
	opt GET /series/import/request
	Browser->>SeriesImportController: 
	activate SeriesImportController
	SeriesImportController->>Browser: RequestSeriesImportForm
	deactivate SeriesImportController
	end

	note over Browser,SeriesImportController: submit request for importing a series
	opt POST /series/import/request

	Browser->>SeriesImportController: RequestSeriesImportForm
	activate SeriesImportController

	SeriesImportController->>SeriesImportService: RequestSeriesImportForm
	activate SeriesImportService
	SeriesImportService->>SeriesImportController: requestId
	deactivate SeriesImportService
	note right of SeriesImportService: Unprocessed
	SeriesImportController->>EventPublisher: ImportRequestCreated

	SeriesImportController->>Browser: redirect to /series/import/request/{id}
	deactivate SeriesImportController

	end
```

## Download page

```mermaid
sequenceDiagram
	title Series import flow: stage 2 (download page)

	participant ImportRequestCreatedEventListener
	participant DownloaderService
	participant SeriesImportService
	participant EventPublisher

	ImportRequestCreatedEventListener->>DownloaderService: url
	activate DownloaderService
	DownloaderService->>ImportRequestCreatedEventListener: 
	deactivate DownloaderService
	alt
	ImportRequestCreatedEventListener->>SeriesImportService: content of downloaded page
	activate SeriesImportService
	SeriesImportService->>SeriesImportService: 
	SeriesImportService->>ImportRequestCreatedEventListener: 
	note right of SeriesImportService: Unprocessed -> DownloadingSucceeded
	deactivate SeriesImportService
	ImportRequestCreatedEventListener->>EventPublisher: DownloadingSucceeded
	else
	ImportRequestCreatedEventListener->>SeriesImportService: 
	activate SeriesImportService
	SeriesImportService->>ImportRequestCreatedEventListener: 
	note right of SeriesImportService: Unprocessed -> DownloadingFailed
	deactivate SeriesImportService
	end
```

## Parse page

```mermaid
sequenceDiagram
	title Series import flow: stage 3 (parse page)

	participant DownloadingSucceededEventListener
	participant SiteParserService
	participant SeriesImportService
	participant SiteParser
	participant ExtractorService
	participant SeriesSalesImportService
	participant EventPublisher

	DownloadingSucceededEventListener->>SiteParserService: page URL
	activate SiteParserService
	SiteParserService->>DownloadingSucceededEventListener: site parser
	deactivate SiteParserService
	DownloadingSucceededEventListener->>SeriesImportService: requestId
	activate SeriesImportService
	SeriesImportService->>DownloadingSucceededEventListener: content of downloaded page
	deactivate SeriesImportService
	DownloadingSucceededEventListener->>SiteParser: content of downloaded page
	activate SiteParser
	SiteParser->>DownloadingSucceededEventListener: SeriesInfo
	deactivate SiteParser
	DownloadingSucceededEventListener->>ExtractorService: RawParsedDataDto
	activate ExtractorService
	ExtractorService->>DownloadingSucceededEventListener: SeriesExtractedInfo
	deactivate ExtractorService
	alt
	DownloadingSucceededEventListener->>SeriesImportService: SeriesExtractedInfo
	activate SeriesImportService
	alt
	SeriesImportService-->>SeriesSalesImportService: SeriesSalesParsedDataDbDto
	activate SeriesSalesImportService
	SeriesSalesImportService-->>SeriesImportService: 
	deactivate SeriesSalesImportService
	SeriesImportService->>SeriesImportService: 
	note right of SeriesImportService: DownloadingSucceeded -> ParsingSucceeded
	else
	SeriesImportService->>EventPublisher: ParsingFailed
	end
	SeriesImportService->>DownloadingSucceededEventListener: 
	deactivate SeriesImportService
	else
	DownloadingSucceededEventListener->>EventPublisher: ParsingFailed
	end
```

```mermaid
sequenceDiagram
	title Series import flow: stage 3a (handle error of parsing page)

	participant ParsingFailedEventListener
	participant SeriesImportService

	ParsingFailedEventListener->>SeriesImportService: 
	activate SeriesImportService
	SeriesImportService->>ParsingFailedEventListener: 
	deactivate SeriesImportService
	note right of SeriesImportService: DownloadingSucceeded -> ParsingFailed
```

## Import a series

```mermaid
sequenceDiagram
	title Series import flow: stage 4 (import a series)

	participant Browser
	participant SeriesImportController
	participant SeriesImportService
	participant SeriesSalesImportService
	participant SeriesService
	participant ParticipantService

	note over Browser,SeriesImportController: show info about import request
	opt GET /series/import/request/{id}
	Browser->>SeriesImportController: 
	activate SeriesImportController
	SeriesImportController->>SeriesImportService: requestId
	activate SeriesImportService
	SeriesImportService->>SeriesImportController: ImportRequestDto
	deactivate SeriesImportService
	SeriesImportController->>SeriesImportService: requestId
	activate SeriesImportService
	SeriesImportService->>SeriesImportController: SeriesParsedDataDto
	deactivate SeriesImportService
	SeriesImportController->>SeriesSalesImportService: requestId
	activate SeriesSalesImportService
	SeriesSalesImportService->>SeriesImportController: SeriesSaleParsedDataDto
	deactivate SeriesSalesImportService
	SeriesImportController->>Browser: ImportRequestDto<br/>ImportSeriesForm<br/>ImportSellerForm<br/>ImportSeriesSalesForm
	deactivate SeriesImportController
	end

	note over Browser,SeriesImportController: import a series
	opt POST /series/import/request/{id}
	Browser->>SeriesImportController: ImportSeriesForm
	activate SeriesImportController
	SeriesImportController->>SeriesImportService: AddSeriesDto<br/>AddParticipantDto<br/>AddSeriesSalesDto
	activate SeriesImportService
	opt Create a series
	SeriesImportService->>SeriesService: AddSeriesDto
	activate SeriesService
	SeriesService->>SeriesImportService: seriesId
	deactivate SeriesService
	end
	opt Create a seller (optional)
	SeriesImportService-->>ParticipantService: AddParticipantDto
	activate ParticipantService
	ParticipantService-->>SeriesImportService: sellerId
	deactivate ParticipantService
	end
	opt Create a series sale (optional)
	SeriesImportService-->>SeriesSalesImportService: AddSeriesSalesDto
	activate SeriesSalesImportService
	SeriesSalesImportService-->>SeriesImportService: 
	deactivate SeriesSalesImportService
	end
	SeriesImportService->>SeriesImportController: seriesId
	deactivate SeriesImportService
	note right of SeriesImportService: ParsingSucceeded -> ImportSucceeded
	SeriesImportController->>Browser: redirect to /series/{id}
	deactivate SeriesImportController
	end
```
