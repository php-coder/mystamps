/**
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
package ru.mystamps.web.feature.series.importing

import org.slf4j.helpers.NOPLogger
import org.springframework.context.ApplicationEventPublisher
import ru.mystamps.web.feature.participant.AddParticipantDto
import ru.mystamps.web.feature.participant.ParticipantService
import ru.mystamps.web.feature.series.AddSeriesDto
import ru.mystamps.web.feature.series.SeriesService
import ru.mystamps.web.feature.series.importing.SeriesImportDb.SeriesImportRequestStatus
import ru.mystamps.web.feature.series.importing.event.ParsingFailed
import ru.mystamps.web.feature.series.importing.sale.SeriesSalesImportService
import ru.mystamps.web.feature.series.importing.sale.SeriesSalesParsedDataDbDto
import ru.mystamps.web.feature.series.sale.AddSeriesSalesDto
import ru.mystamps.web.feature.series.sale.SeriesSalesService
import ru.mystamps.web.service.TestObjects
import ru.mystamps.web.tests.DateUtils
import ru.mystamps.web.tests.Random
import spock.lang.Specification
import spock.lang.Unroll

import static io.qala.datagen.RandomShortApi.english
import static io.qala.datagen.RandomShortApi.nullOr
import static io.qala.datagen.RandomShortApi.nullOrBlank
import static io.qala.datagen.RandomValue.between

@SuppressWarnings([
	'ClassJavadoc',
	'MethodName',
	'MisorderedStaticImports',
	'NoDef',
	'NoTabCharacter',
	'TrailingWhitespace',
])
class SeriesImportServiceImplTest extends Specification {
	
	private final SeriesImportDao seriesImportDao = Mock()
	private final SeriesService seriesService = Mock()
	private final SeriesSalesService seriesSalesService = Mock()
	private final SeriesSalesImportService seriesSalesImportService = Mock()
	private final ParticipantService participantService = Mock()
	private final ApplicationEventPublisher eventPublisher = Mock()
	
	private SeriesImportService service
	private RequestSeriesImportForm form
	
	def setup() {
		service = new SeriesImportServiceImpl(
			NOPLogger.NOP_LOGGER,
			seriesImportDao,
			seriesService,
			seriesSalesService,
			seriesSalesImportService,
			participantService,
			eventPublisher
		)
		form = new RequestSeriesImportForm()
	}
	
	//
	// Tests for addRequest()
	//
	
	def 'addRequest() should throw exception if dto is null'() {
		when:
			service.addRequest(null, Random.userId())
		then:
			IllegalArgumentException ex = thrown()
			ex.message == 'DTO must be non null'
	}
	
	def 'addRequest() should throw exception if url is null'() {
		given:
			form.setUrl(null)
		when:
			service.addRequest(form, Random.userId())
		then:
			IllegalArgumentException ex = thrown()
			ex.message == 'URL must be non null'
	}
	
	def 'addRequest() should throw exception if user id is null'() {
		given:
			form.setUrl(Random.url())
		when:
			service.addRequest(form, null)
		then:
			IllegalArgumentException ex = thrown()
			ex.message == 'User id must be non null'
	}
	
	def 'addRequest() should throw exception if url is incorrect'() {
		given:
			form.setUrl('http://example.org/текст c пробелами')
		when:
			service.addRequest(form, Random.userId())
		then:
			RuntimeException ex = thrown()
		and:
			ex?.cause?.class == URISyntaxException
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def 'addRequest() should pass dto to dao and return its result'() {
		given:
			String expectedUrl = Random.url()
			form.setUrl(expectedUrl)
		and:
			Integer expectedUserId = Random.userId()
			Integer expectedResult = Random.id()
		when:
			Integer result = service.addRequest(form, expectedUserId)
		then:
			1 * seriesImportDao.add({ ImportSeriesDbDto request ->
				assert request?.url == expectedUrl
				assert request?.status == SeriesImportRequestStatus.UNPROCESSED
				assert DateUtils.roughlyEqual(request?.requestedAt, new Date())
				assert request?.requestedBy == expectedUserId
				assert DateUtils.roughlyEqual(request?.updatedAt, new Date())
				return true
			}) >> expectedResult
		and:
			result == expectedResult
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'LineLength', 'UnnecessaryReturnKeyword'])
	def 'addRequest() should save url in the encoded form'() {
		given:
			String url = 'http://example.org/текст_на_русском'
			String expectedUrl = 'http://example.org/%D1%82%D0%B5%D0%BA%D1%81%D1%82_%D0%BD%D0%B0_%D1%80%D1%83%D1%81%D1%81%D0%BA%D0%BE%D0%BC'
		and:
			form.setUrl(url)
		when:
			service.addRequest(form, Random.userId())
		then:
			1 * seriesImportDao.add({ ImportSeriesDbDto request ->
				assert request?.url == expectedUrl
				return true
			}) >> Random.id()
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'LineLength', 'UnnecessaryReturnKeyword'])
	def 'addRequest() should not encode url if it is already encoded'() {
		given:
			String expectedUrl = 'http://example.org/%D1%82%D0%B5%D0%BA%D1%81%D1%82_%D0%BD%D0%B0_%D1%80%D1%83%D1%81%D1%81%D0%BA%D0%BE%D0%BC'
		and:
			form.setUrl(expectedUrl)
		when:
			service.addRequest(form, Random.userId())
		then:
			1 * seriesImportDao.add({ ImportSeriesDbDto request ->
				assert request?.url == expectedUrl
				return true
			}) >> Random.id()
	}
	
	//
	// Tests for addSeries()
	//
	
	def 'addSeries() should create series and return its id'() {
		given:
			AddSeriesDto expectedDto = TestObjects.createAddSeriesDto()
			Integer expectedUserId = Random.userId()
			Integer expectedRequestId = Random.id()
			Integer expectedSeriesId = Random.id()
		when:
			Integer seriesId = service.addSeries(
				expectedDto,
				nullOr(TestObjects.createAddParticipantDto()),
				nullOr(TestObjects.createAddSeriesSalesDto()),
				expectedRequestId,
				expectedUserId
			)
		then:
			1 * seriesService.add(expectedDto, expectedUserId, false) >> expectedSeriesId
		and:
			seriesId == expectedSeriesId
	}
	
	def 'addSeries() should create series sale if provided'() {
		given:
			AddSeriesSalesDto expectedSaleDto = TestObjects.createAddSeriesSalesDto()
			Integer expectedSeriesId = Random.id()
			Integer expectedUserId = Random.userId()
		and:
			seriesService.add(_ as AddSeriesDto, _ as Integer, _ as Boolean) >> expectedSeriesId
		when:
			service.addSeries(
				TestObjects.createAddSeriesDto(),
				nullOr(TestObjects.createAddParticipantDto()),
				expectedSaleDto,
				Random.id(),
				expectedUserId
			)
		then:
			1 * seriesSalesService.add(expectedSaleDto, expectedSeriesId, expectedUserId)
	}

	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def 'addSeries() should create a new seller if needed and use it during series sale creation'() {
		given:
			Integer expectedSellerId = Random.id()
			AddParticipantDto expectedSellerDto = TestObjects.createAddParticipantDto()
		and:
			seriesService.add(_ as AddSeriesDto, _ as Integer, _ as Boolean) >> Random.id()
		when:
			service.addSeries(
				TestObjects.createAddSeriesDto(),
				expectedSellerDto,
				TestObjects.createAddSeriesSalesDtoWithSellerId(null),
				Random.id(),
				Random.userId()
			)
		then:
			1 * participantService.add(expectedSellerDto) >> expectedSellerId
		and:
			1 * seriesSalesService.add(
				{ AddSeriesSalesDto saleDto ->
					assert saleDto?.sellerId == expectedSellerId
					return true
				},
				_ as Integer,
				_ as Integer
			)
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def 'addSeries() should update request and set status'() {
		given:
			Integer expectedRequestId = Random.id()
			Integer expectedSeriesId = Random.id()
		and:
			seriesService.add(_ as AddSeriesDto, _ as Integer, _ as Boolean) >> expectedSeriesId
		when:
			service.addSeries(
				TestObjects.createAddSeriesDto(),
				nullOr(TestObjects.createAddParticipantDto()),
				nullOr(TestObjects.createAddSeriesSalesDto()),
				expectedRequestId,
				Random.userId()
			)
		then:
			1 * seriesImportDao.setSeriesIdAndChangeStatus(
				expectedSeriesId,
				{ UpdateImportRequestStatusDbDto status ->
					assert status?.requestId == expectedRequestId
					assert DateUtils.roughlyEqual(status?.date, new Date())
					assert status?.oldStatus == SeriesImportRequestStatus.PARSING_SUCCEEDED 
					assert status?.newStatus == SeriesImportRequestStatus.IMPORT_SUCCEEDED
					return true
				}
			)
	}
	
	//
	// Tests for changeStatus()
	//
	
	def 'changeStatus() should throw exception when request id is null'() {
		given:
			List<String> possibleStatuses = Random.importRequestStatuses(2)
			String oldStatus = possibleStatuses.get(0)
			String newStatus = possibleStatuses.get(1)
		when:
			service.changeStatus(null, oldStatus, newStatus)
		then:
			IllegalArgumentException ex = thrown()
			ex.message == 'Request id must be non null'
	}
	
	def 'changeStatus() should throw exception when old status is blank'() {
		when:
			service.changeStatus(Random.id(), nullOrBlank(), Random.importRequestStatus())
		then:
			IllegalArgumentException ex = thrown()
			ex.message == 'Old status must be non-blank'
	}
	
	def 'changeStatus() should throw exception when new status is blank'() {
		when:
			service.changeStatus(Random.id(), Random.importRequestStatus(), nullOrBlank())
		then:
			IllegalArgumentException ex = thrown()
			ex.message == 'New status must be non-blank'
	}
	
	def 'changeStatus() should throw exception when statuses are equal'() {
		given:
			String status = Random.importRequestStatus()
		when:
			service.changeStatus(Random.id(), status, status)
		then:
			IllegalArgumentException ex = thrown()
			ex.message == 'Statuses must be different'
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def 'changeStatus() should pass arguments to dao'() {
		given:
			Integer expectedRequestId = Random.userId()
		and:
			List<String> expectedStatuses = Random.importRequestStatuses(2)
			String expectedOldStatus = expectedStatuses.get(0)
			String expectedNewStatus = expectedStatuses.get(1)
		when:
			service.changeStatus(expectedRequestId, expectedOldStatus, expectedNewStatus)
		then:
			1 * seriesImportDao.changeStatus({ UpdateImportRequestStatusDbDto status ->
				assert status?.requestId == expectedRequestId
				assert DateUtils.roughlyEqual(status.date, new Date())
				assert status?.oldStatus == expectedOldStatus
				assert status?.newStatus == expectedNewStatus
				return true
			})
	
	}
	
	//
	// Tests for findById()
	//
	
	def 'findById() should throw exception when request id is null'() {
		when:
			service.findById(null)
		then:
			IllegalArgumentException ex = thrown()
			ex.message == 'Request id must be non null'
	}
	
	def 'findById() should invoke dao, pass argument and return result from dao'() {
		given:
			Integer expectedRequestId = Random.id()
		and:
			ImportRequestDto expectedResult = TestObjects.createImportRequestDto()
		when:
			ImportRequestDto result = service.findById(expectedRequestId)
		then:
			1 * seriesImportDao.findById(expectedRequestId) >> expectedResult
		and:
			result == expectedResult
	}
	
	//
	// Tests for saveDownloadedContent()
	//
	
	def 'saveDownloadedContent() should throw exception when request id is null'() {
		given:
			String content = between(1, 10).english()
		when:
			service.saveDownloadedContent(null, content)
		then:
			IllegalArgumentException ex = thrown()
			ex.message == 'Request id must be non null'
	}

	@Unroll
	def "saveDownloadedContent() should throw exception when content is '#content'"(String content) {
		when:
			service.saveDownloadedContent(Random.id(), content)
		then:
			IllegalArgumentException ex = thrown()
			ex.message == 'Content must be non-blank'
		where:
			content | _
			''      | _
			'  '    | _
	}
	
	@SuppressWarnings('UnnecessaryReturnKeyword')
	def 'saveDownloadedContent() should pass arguments to dao'() {
		given:
			Integer expectedRequestId = Random.id()
			String expectedContent = between(1, 10).english()
		when:
			service.saveDownloadedContent(expectedRequestId, expectedContent)
		then:
			1 * seriesImportDao.addRawContent(
				expectedRequestId,
				{ Date createdAt ->
					assert DateUtils.roughlyEqual(createdAt, new Date())
					return true
				},
				{ Date updatedAt ->
					assert DateUtils.roughlyEqual(updatedAt, new Date())
					return true
				},
				expectedContent
			)
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def 'saveDownloadedContent() should change status'() {
		given:
			Integer expectedRequestId = Random.id()
		when:
			service.saveDownloadedContent(expectedRequestId, between(1, 10).english())
		then:
			1 * seriesImportDao.changeStatus({ UpdateImportRequestStatusDbDto status ->
					assert status?.requestId == expectedRequestId
					assert DateUtils.roughlyEqual(status.date, new Date())
					assert status?.oldStatus == SeriesImportRequestStatus.UNPROCESSED
					assert status?.newStatus == SeriesImportRequestStatus.DOWNLOADING_SUCCEEDED
					return true
			})
	}
	
	//
	// Tests for getDownloadedContent()
	//
	
	def 'getDownloadedContent() should throw exception when request id is null'() {
		when:
			service.getDownloadedContent(null)
		then:
			IllegalArgumentException ex = thrown()
			ex.message == 'Request id must be non null'
	}
	
	def 'getDownloadedContent() should invoke dao, pass argument and return result from dao'() {
		given:
			Integer expectedRequestId = Random.id()
		and:
			// exact length of the string doesn't matter, so we limit it to 10 to ensure that
			// it won't produce a huge string that will eat a lot of memory
			String expectedResult = english(5, 10)
		when:
			String result = service.getDownloadedContent(expectedRequestId)
		then:
			1 * seriesImportDao.findRawContentByRequestId(expectedRequestId) >> expectedResult
		and:
			result == expectedResult
	}
	
	//
	// Tests for saveParsedData()
	//
	
	def 'saveParsedData() should throw exception when request id is null'() {
		when:
			service.saveParsedData(null, TestObjects.createEmptySeriesExtractedInfo(), Random.url())
		then:
			IllegalArgumentException ex = thrown()
			ex.message == 'Request id must be non null'
	}
	
	def 'saveParsedData() should throw exception when series info is null'() {
		when:
			service.saveParsedData(Random.id(), null, Random.url())
		then:
			IllegalArgumentException ex = thrown()
			ex.message == 'Series info must be non null'
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def 'saveParsedData() should publish ParsingFailed event when all series fields are empty'() {
		given:
			Integer expectedRequestId = Random.id()
		when:
			service.saveParsedData(expectedRequestId, TestObjects.createEmptySeriesExtractedInfo(), null)
		then:
			1 * eventPublisher.publishEvent({ ParsingFailed event ->
				assert event?.requestId == expectedRequestId
				return true
			})
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def 'saveParsedData() should save series parsed data'() {
		given:
			Integer expectedRequestId = Random.id()
			String expectedImageUrl = Random.url()
		when:
			service.saveParsedData(expectedRequestId, TestObjects.createSeriesExtractedInfo(), expectedImageUrl)
		then:
			1 * seriesImportDao.addParsedData(
				expectedRequestId,
				{ AddSeriesParsedDataDbDto parsedData ->
					// other members are tested in another test
					assert parsedData?.imageUrl == expectedImageUrl
					assert DateUtils.roughlyEqual(parsedData?.createdAt, new Date())
					assert DateUtils.roughlyEqual(parsedData?.updatedAt, new Date())
					return true
				}
			)
	}
	
	@SuppressWarnings([ 'ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword' ])
	def 'saveParsedData() should save series sale parsed data if provided'() {
		given:
			Integer expectedRequestId = Random.id()
		when:
			service.saveParsedData(expectedRequestId, TestObjects.createSeriesExtractedInfo(), Random.url())
		then:
			1 * seriesSalesImportService.saveParsedData(
				expectedRequestId,
				{ SeriesSalesParsedDataDbDto parsedData ->
					// other members are tested in another test
					assert DateUtils.roughlyEqual(parsedData?.createdAt, new Date())
					assert DateUtils.roughlyEqual(parsedData?.updatedAt, new Date())
					return true
				}
			)
	}
	
	@SuppressWarnings([ 'ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword', 'UnnecessaryGetter' ])
	def 'saveParsedData() should save series info'() {
		given:
			SeriesExtractedInfo expectedSeriesInfo = TestObjects.createSeriesExtractedInfo()
		and:
			Integer expectedCategoryId  = expectedSeriesInfo.getCategoryIds().get(0)
			Integer expectedCountryId   = expectedSeriesInfo.getCountryIds().get(0)
			Integer expectedReleaseYear = expectedSeriesInfo.getReleaseYear()
			Integer expectedQuantity    = expectedSeriesInfo.getQuantity()
			Boolean expectedPerforated  = expectedSeriesInfo.getPerforated()
			Integer expectedSellerId    = expectedSeriesInfo.getSellerId()
			String expectedSellerName   = expectedSeriesInfo.getSellerName()
			String expectedSellerUrl    = expectedSeriesInfo.getSellerUrl()
			BigDecimal expectedPrice    = expectedSeriesInfo.getPrice()
			String expectedCurrency     = expectedSeriesInfo.getCurrency()
		when:
			service.saveParsedData(Random.id(), expectedSeriesInfo, Random.url())
		then:
			1 * seriesImportDao.addParsedData(
				_ as Integer,
				{ AddSeriesParsedDataDbDto parsedData ->
					assert parsedData?.categoryId  == expectedCategoryId
					assert parsedData?.countryId   == expectedCountryId
					assert parsedData?.releaseYear == expectedReleaseYear
					assert parsedData?.quantity    == expectedQuantity
					assert parsedData?.perforated  == expectedPerforated
					return true
				}
			)
		and:
			1 * seriesSalesImportService.saveParsedData(
				_ as Integer,
				{ SeriesSalesParsedDataDbDto parsedData ->
					assert parsedData != null
					assert parsedData.sellerId   == expectedSellerId
					assert parsedData.sellerName == expectedSellerName
					assert parsedData.sellerUrl  == expectedSellerUrl
					assert parsedData.price      == expectedPrice
					assert parsedData.currency   == expectedCurrency
					return true
				}
			)
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def 'saveParsedData() should change request status'() {
		when:
			service.saveParsedData(Random.id(), TestObjects.createEmptySeriesExtractedInfo(), Random.url())
		then:
			1 * seriesImportDao.changeStatus({ UpdateImportRequestStatusDbDto status ->
					assert DateUtils.roughlyEqual(status.date, new Date())
					assert status?.oldStatus == SeriesImportRequestStatus.DOWNLOADING_SUCCEEDED
					assert status?.newStatus == SeriesImportRequestStatus.PARSING_SUCCEEDED
					return true
			})
	}
	
	//
	// Tests for getParsedData()
	//
	
	def 'getParsedData() should throw exception when request id is null'() {
		when:
			service.getParsedData(null, Random.lang())
		then:
			IllegalArgumentException ex = thrown()
			ex.message == 'Request id must be non null'
	}
	
	def 'getParsedData() should invoke dao, pass argument and return result from dao'() {
		given:
			Integer expectedRequestId = Random.id()
			String expectedLang = nullOr(Random.lang())
		and:
			SeriesParsedDataDto expectedResult = TestObjects.createSeriesParsedDataDto()
		when:
			SeriesParsedDataDto result = service.getParsedData(expectedRequestId, expectedLang)
		then:
			1 * seriesImportDao.findParsedDataByRequestId(expectedRequestId, expectedLang) >> expectedResult
		and:
			result == expectedResult
	}
	
	//
	// Tests for findRequestInfo()
	//
	
	def 'findRequestInfo() should throw exception when series id is null'() {
		when:
			service.findRequestInfo(null)
		then:
			IllegalArgumentException ex = thrown()
			ex.message == 'Series id must be non null'
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def 'findRequestInfo() should invoke dao and return its result'() {
		given:
			Integer expectedSeriesId = Random.id()
		and:
			ImportRequestInfo expectedResult = TestObjects.createImportRequestInfo()
		when:
			ImportRequestInfo result = service.findRequestInfo(expectedSeriesId)
		then:
			1 * seriesImportDao.findRequestInfo(expectedSeriesId) >> expectedResult
		and:
			result == expectedResult
	}
	
	//
	// Tests for findAll()
	//
	
	def 'findAll() should invoke dao and return its result'() {
		given:
			List<ImportRequestFullInfo> expectedResult = Random.listOfImportRequestFullInfo()
		when:
			List<ImportRequestFullInfo> result = service.findAll()
		then:
			1 * seriesImportDao.findAll() >> expectedResult
		and:
			result == expectedResult
	}
	
}
