/**
 * Copyright (C) 2009-2017 Slava Semushin <slava.semushin@gmail.com>
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

import ru.mystamps.web.controller.dto.RequestImportForm
import ru.mystamps.web.Db.SeriesImportRequestStatus
import ru.mystamps.web.dao.dto.ImportRequestDto
import ru.mystamps.web.dao.dto.ParsedDataDto
import ru.mystamps.web.dao.SeriesImportDao
import ru.mystamps.web.dao.dto.ImportSeriesDbDto
import ru.mystamps.web.tests.DateUtils
import ru.mystamps.web.tests.Random

@SuppressWarnings(['ClassJavadoc', 'MethodName', 'NoDef', 'NoTabCharacter', 'TrailingWhitespace'])
class SeriesImportServiceImplTest extends Specification {
	
	private final SeriesImportDao seriesImportDao = Mock()
	private final SeriesService seriesService = Mock()
	private final SeriesInfoExtractorService extractorService = Mock()
	
	private SeriesImportService service
	private RequestImportForm form
	
	def setup() {
		service = new SeriesImportServiceImpl(
			NOPLogger.NOP_LOGGER,
			seriesImportDao, seriesService,
			extractorService
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
	
	def 'addRequest() should throw exception if user id is null'() {
		when:
			service.addRequest(form, null)
		then:
			thrown IllegalArgumentException
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
				assert request.url == expectedUrl
				assert request.status == SeriesImportRequestStatus.UNPROCESSED
				assert DateUtils.roughlyEqual(request?.requestedAt, new Date())
				assert request.requestedBy == expectedUserId
				assert DateUtils.roughlyEqual(request?.updatedAt, new Date())
				return true
			}) >> expectedResult
		and:
			result == expectedResult
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
	
}
