/**
 * Copyright (C) 2009-2018 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.service

import static io.qala.datagen.RandomShortApi.english
import static io.qala.datagen.RandomShortApi.nullOr
import static io.qala.datagen.RandomShortApi.nullOrBlank
import static io.qala.datagen.RandomValue.between

import spock.lang.Specification

import org.slf4j.helpers.NOPLogger

import org.springframework.context.ApplicationEventPublisher

import ru.mystamps.web.controller.dto.RequestImportForm
import ru.mystamps.web.controller.event.ParsingFailed
import ru.mystamps.web.Db.SeriesImportRequestStatus
import ru.mystamps.web.dao.dto.ImportRequestDto
import ru.mystamps.web.dao.dto.ParsedDataDto
import ru.mystamps.web.dao.SeriesImportDao
import ru.mystamps.web.dao.dto.AddSeriesParsedDataDbDto
import ru.mystamps.web.dao.dto.ImportRequestInfo
import ru.mystamps.web.dao.dto.ImportSeriesDbDto
import ru.mystamps.web.dao.dto.ImportRequestFullInfo
import ru.mystamps.web.service.dto.AddSeriesDto
import ru.mystamps.web.service.dto.RawParsedDataDto
import ru.mystamps.web.tests.DateUtils
import ru.mystamps.web.tests.Random

@SuppressWarnings(['ClassJavadoc', 'MethodName', 'NoDef', 'NoTabCharacter', 'TrailingWhitespace'])
class SeriesImportServiceImplTest extends Specification {
	
	private final SeriesImportDao seriesImportDao = Mock()
	private final SeriesService seriesService = Mock()
	private final SeriesInfoExtractorService extractorService = Mock()
	private final ApplicationEventPublisher eventPublisher = Mock()
	
	private SeriesImportService service
	private RequestImportForm form
	
	def setup() {
		service = new SeriesImportServiceImpl(
			NOPLogger.NOP_LOGGER,
			seriesImportDao, seriesService,
			extractorService,
			eventPublisher
		)
		form = new RequestImportForm()
	}
	
	//
	// Tests for addRequest()
	//
	
	def 'addRequest() should throw exception if dto is null'() {
		when:
			service.addRequest(null, Random.userId())
		then:
			thrown IllegalArgumentException
	}
	
	def 'addRequest() should throw exception if url is null'() {
		given:
			form.setUrl(null)
		when:
			service.addRequest(form, Random.userId())
		then:
			thrown IllegalArgumentException
	}
	
	def 'addRequest() should throw exception if user id is null'() {
		when:
			service.addRequest(form, null)
		then:
			thrown IllegalArgumentException
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
			Integer seriesId = service.addSeries(expectedDto, expectedRequestId, expectedUserId)
		then:
			1 * seriesService.add(expectedDto, expectedUserId, false) >> expectedSeriesId
		and:
			seriesId == expectedSeriesId
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def 'addSeries() should update request and set status'() {
		given:
			Integer expectedRequestId = Random.id()
			Integer expectedSeriesId = Random.id()
		and:
			seriesService.add(_ as AddSeriesDto, _ as Integer, _ as Boolean) >> expectedSeriesId
		when:
			service.addSeries(TestObjects.createAddSeriesDto(), expectedRequestId, Random.userId())
		then:
			1 * seriesImportDao.setSeriesIdAndChangeStatus(
				expectedRequestId,
				expectedSeriesId,
				SeriesImportRequestStatus.PARSING_SUCCEEDED,
				SeriesImportRequestStatus.IMPORT_SUCCEEDED,
				{ Date updatedAt ->
					assert DateUtils.roughlyEqual(updatedAt, new Date())
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
			thrown IllegalArgumentException
	}
	
	def 'changeStatus() should throw exception when old status is blank'() {
		when:
			service.changeStatus(Random.id(), nullOrBlank(), Random.importRequestStatus())
		then:
			thrown IllegalArgumentException
	}
	
	def 'changeStatus() should throw exception when new status is blank'() {
		when:
			service.changeStatus(Random.id(), Random.importRequestStatus(), nullOrBlank())
		then:
			thrown IllegalArgumentException
	}
	
	def 'changeStatus() should throw exception when statuses are equal'() {
		given:
			String status = Random.importRequestStatus()
		when:
			service.changeStatus(Random.id(), status, status)
		then:
			thrown IllegalArgumentException
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
			1 * seriesImportDao.changeStatus({ Integer requestId ->
				assert requestId == expectedRequestId
				return true
			}, { Date date ->
				assert DateUtils.roughlyEqual(date, new Date())
				return true
			}, { String oldStatus ->
				assert oldStatus == expectedOldStatus
				return true
			}, { String newStatus ->
				assert newStatus == expectedNewStatus
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
			thrown IllegalArgumentException
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def 'findById() should invoke dao, pass argument and return result from dao'() {
		given:
			Integer expectedRequestId = Random.id()
		and:
			ImportRequestDto expectedResult = TestObjects.createImportRequestDto()
		when:
			ImportRequestDto result = service.findById(expectedRequestId)
		then:
			1 * seriesImportDao.findById({ Integer requestId ->
				assert requestId == expectedRequestId
				return true
			}) >> expectedResult
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
			thrown IllegalArgumentException
	}
	
	def 'saveDownloadedContent() should throw exception when content is blank'() {
		when:
			service.saveDownloadedContent(Random.id(), nullOrBlank())
		then:
			thrown IllegalArgumentException
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def 'saveDownloadedContent() should pass arguments to dao'() {
		given:
			Integer expectedRequestId = Random.id()
			String expectedContent = between(1, 10).english()
		when:
			service.saveDownloadedContent(expectedRequestId, expectedContent)
		then:
			1 * seriesImportDao.addRawContent({ Integer requestId ->
				assert requestId == expectedRequestId
				return true
			}, { Date createdAt ->
				assert DateUtils.roughlyEqual(createdAt, new Date())
				return true
			}, { Date updatedAt ->
				assert DateUtils.roughlyEqual(updatedAt, new Date())
				return true
			}, { String content ->
				assert content == expectedContent
				return true
			})
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def 'saveDownloadedContent() should change status'() {
		given:
			Integer expectedRequestId = Random.id()
		when:
			service.saveDownloadedContent(expectedRequestId, between(1, 10).english())
		then:
			1 * seriesImportDao.changeStatus({ Integer requestId ->
				assert requestId == expectedRequestId
				return true
			}, { Date date ->
				assert DateUtils.roughlyEqual(date, new Date())
				return true
			}, { String oldStatus ->
				assert oldStatus == SeriesImportRequestStatus.UNPROCESSED
				return true
			}, { String newStatus ->
				assert newStatus == SeriesImportRequestStatus.DOWNLOADING_SUCCEEDED
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
			thrown IllegalArgumentException
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
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
			1 * seriesImportDao.findRawContentByRequestId({ Integer requestId ->
				assert requestId == expectedRequestId
				return true
			}) >> expectedResult
		and:
			result == expectedResult
	}
	
	//
	// Tests for saveParsedData()
	//
	
	def 'saveParsedData() should throw exception when request id is null'() {
		when:
			service.saveParsedData(null, TestObjects.createRawParsedDataDto())
		then:
			thrown IllegalArgumentException
	}
	
	def 'saveParsedData() should throw exception when parsed data is null'() {
		when:
			service.saveParsedData(Random.id(), null)
		then:
			thrown IllegalArgumentException
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def 'saveParsedData() should publish ParsingFailed event when couldn\'t associate extracted data'() {
		given:
			Integer expectedRequestId = Random.id()
		and:
			RawParsedDataDto parsedData = new RawParsedDataDto(
				Random.categoryName(),
				Random.countryName(),
				null, /* imageUrl */
				Random.issueYear().toString(),
				Random.quantity().toString(),
				String.valueOf(Random.perforated())
			)
		and:
			extractorService.extractCategory(_ as String) >> Collections.emptyList()
			extractorService.extractCountry(_ as String) >> Collections.emptyList()
		when:
			service.saveParsedData(expectedRequestId, parsedData)
		then:
			1 * eventPublisher.publishEvent({ ParsingFailed event ->
				assert event?.requestId == expectedRequestId
				return true
			})
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def 'saveParsedData() should save parsed data'() {
		given:
			Integer expectedRequestId = Random.id()
			String expectedImageUrl = Random.url()
		and:
			RawParsedDataDto parsedData = new RawParsedDataDto(
				Random.categoryName(),
				Random.countryName(),
				expectedImageUrl,
				Random.issueYear().toString(),
				Random.quantity().toString(),
				String.valueOf(Random.perforated())
			)
		and:
			extractorService.extractCategory(_ as String) >> Collections.emptyList()
			extractorService.extractCountry(_ as String) >> Collections.emptyList()
		when:
			service.saveParsedData(expectedRequestId, parsedData)
		then:
			1 * seriesImportDao.addParsedContent(
				expectedRequestId,
				{ AddSeriesParsedDataDbDto saveParsedData ->
					assert saveParsedData?.imageUrl == expectedImageUrl
					assert DateUtils.roughlyEqual(saveParsedData?.createdAt, new Date())
					assert DateUtils.roughlyEqual(saveParsedData?.updatedAt, new Date())
					return true
				}
			)
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def 'saveParsedData() should pass data to extractor services and save its results'() {
		given:
			Integer expectedRequestId = Random.id()
			String expectedCategoryName = Random.categoryName()
			String expectedCountryName = Random.countryName()
			Integer expectedReleaseYear = Random.issueYear()
			List<Integer> expectedCategoryIds = Random.listOfIntegers()
			List<Integer> expectedCountryIds = Random.listOfIntegers()
			Integer expectedCategoryId = expectedCategoryIds.get(0)
			Integer expectedCountryId = expectedCountryIds.get(0)
			Integer expectedQuantity = Random.quantity()
			Boolean expectedPerforated = Random.perforated()
		and:
			RawParsedDataDto parsedData = new RawParsedDataDto(
				expectedCategoryName,
				expectedCountryName,
				Random.url(),
				expectedReleaseYear.toString(),
				expectedQuantity.toString(),
				expectedPerforated.toString()
			)
		when:
			service.saveParsedData(expectedRequestId, parsedData)
		then:
			1 * extractorService.extractCategory(expectedCategoryName) >> expectedCategoryIds
		and:
			1 * extractorService.extractCountry(expectedCountryName) >> expectedCountryIds
		and:
			1 * extractorService.extractReleaseYear(expectedReleaseYear.toString()) >> expectedReleaseYear
		and:
			1 * extractorService.extractQuantity(expectedQuantity.toString()) >> expectedQuantity
		and:
			1 * extractorService.extractPerforated(expectedPerforated.toString()) >> expectedPerforated
		and:
			1 * seriesImportDao.addParsedContent(
				expectedRequestId,
				{ AddSeriesParsedDataDbDto saveParsedData ->
					assert saveParsedData?.categoryId  == expectedCategoryId
					assert saveParsedData?.countryId   == expectedCountryId
					assert saveParsedData?.releaseYear == expectedReleaseYear
					assert saveParsedData?.quantity    == expectedQuantity
					assert saveParsedData?.perforated  == expectedPerforated
					return true
				}
			)
	}
	
	@SuppressWarnings('UnnecessaryReturnKeyword')
	def 'saveParsedData() should change request status'() {
		given:
			Integer expectedRequestId = Random.id()
		and:
			extractorService.extractCategory(_ as String) >> Collections.emptyList()
			extractorService.extractCountry(_ as String) >> Collections.emptyList()
		when:
			service.saveParsedData(expectedRequestId, TestObjects.createRawParsedDataDto())
		then:
			1 * seriesImportDao.changeStatus(
				expectedRequestId,
				{ Date date ->
					assert DateUtils.roughlyEqual(date, new Date())
					return true
				},
				SeriesImportRequestStatus.DOWNLOADING_SUCCEEDED,
				SeriesImportRequestStatus.PARSING_SUCCEEDED
			)
	}
	
	//
	// Tests for getParsedData()
	//
	
	def 'getParsedData() should throw exception when request id is null'() {
		when:
			service.getParsedData(null, Random.lang())
		then:
			thrown IllegalArgumentException
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def 'getParsedData() should invoke dao, pass argument and return result from dao'() {
		given:
			Integer expectedRequestId = Random.id()
			String expectedLang = nullOr(Random.lang())
		and:
			ParsedDataDto expectedResult = TestObjects.createParsedDataDto()
		when:
			ParsedDataDto result = service.getParsedData(expectedRequestId, expectedLang)
		then:
			1 * seriesImportDao.findParsedDataByRequestId({ Integer requestId ->
				assert requestId == expectedRequestId
				return true
			}, { String lang ->
				assert lang == expectedLang
				return true
			}) >> expectedResult
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
			thrown IllegalArgumentException
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
