/**
 * Copyright (C) 2009-2016 Slava Semushin <slava.semushin@gmail.com>
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

import spock.lang.Unroll
import spock.lang.Specification

import ru.mystamps.web.Db
import ru.mystamps.web.dao.SuspiciousActivityDao
import ru.mystamps.web.dao.dto.AddSuspiciousActivityDbDto
import ru.mystamps.web.tests.DateUtils

@SuppressWarnings(['ClassJavadoc', 'MethodName', 'NoDef', 'NoTabCharacter', 'TrailingWhitespace'])
class SiteServiceImplTest extends Specification {
	private static final String TEST_TYPE         = TestObjects.TEST_ACTIVITY_TYPE
	private static final String TEST_PAGE         = TestObjects.TEST_ACTIVITY_PAGE
	private static final String TEST_IP           = TestObjects.TEST_ACTIVITY_IP
	private static final String TEST_METHOD       = TestObjects.TEST_ACTIVITY_METHOD
	private static final String TEST_REFERER_PAGE = TestObjects.TEST_ACTIVITY_REFERER
	private static final String TEST_USER_AGENT   = TestObjects.TEST_ACTIVITY_AGENT
	
	private SuspiciousActivityDao suspiciousActivityDao = Mock()
	private SiteServiceImpl serviceImpl
	
	def setup() {
		serviceImpl = Spy(SiteServiceImpl, constructorArgs:[suspiciousActivityDao])
	}
	
	//
	// Tests for logAboutAbsentPage()
	//
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "logAboutAbsentPage() should pass arguments to logEvent()"() {
		given:
			Integer expectedUserId = 17
		when:
			serviceImpl.logAboutAbsentPage(
				TEST_PAGE,
				TEST_METHOD,
				expectedUserId,
				TEST_IP,
				TEST_REFERER_PAGE,
				TEST_USER_AGENT
			)
		then:
			1 * serviceImpl.logEvent(
				SiteServiceImpl.PAGE_NOT_FOUND,
				TEST_PAGE,
				TEST_METHOD,
				expectedUserId,
				TEST_IP,
				TEST_REFERER_PAGE,
				TEST_USER_AGENT,
				{ Date date ->
					assert DateUtils.roughlyEqual(date, new Date())
					return true
				}
			)
	}
	
	//
	// Tests for logAboutFailedAuthentication()
	//
	
	def "logAboutFailedAuthentication() should pass arguments to logEvent()"() {
		given:
			Integer expectedUserId = 18
			Date expectedDate      = new Date()
		when:
			serviceImpl.logAboutFailedAuthentication(
				TEST_PAGE,
				TEST_METHOD,
				expectedUserId,
				TEST_IP,
				TEST_REFERER_PAGE,
				TEST_USER_AGENT,
				expectedDate
			)
		then:
			1 * serviceImpl.logEvent(
				SiteServiceImpl.AUTHENTICATION_FAILED,
				TEST_PAGE,
				TEST_METHOD,
				expectedUserId,
				TEST_IP,
				TEST_REFERER_PAGE,
				TEST_USER_AGENT,
				expectedDate
			)
	}
	
	//
	// Tests for logEvent()
	//
	
	def "logEvent() should throw exception when type is null"() {
		when:
			serviceImpl.logEvent(null, TEST_PAGE, TEST_METHOD, null, null, null, null, null)
		then:
			thrown IllegalArgumentException
	}
	
	def "logEvent() should throw exception when page is null"() {
		when:
			serviceImpl.logEvent(TEST_TYPE, null, TEST_METHOD, null, null, null, null, null)
		then:
			thrown IllegalArgumentException
	}
	
	def "logEvent() should call dao"() {
		when:
			serviceImpl.logEvent(TEST_TYPE, TEST_PAGE, TEST_METHOD, null, null, null, null, null)
		then:
			1 * suspiciousActivityDao.add(_ as AddSuspiciousActivityDbDto)
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "logEvent() should pass activity type to dao"() {
		given:
			String expectedType = 'expectedType'
		when:
			serviceImpl.logEvent(expectedType, TEST_PAGE, TEST_METHOD, null, null, null, null, null)
		then:
			1 * suspiciousActivityDao.add({ AddSuspiciousActivityDbDto activity ->
				assert activity?.type == expectedType
				return true
			})
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "logEvent() should assign occurred at to specified date when date was provided"() {
		given:
			Date expectedDate = new Date() - 100
		when:
			serviceImpl.logEvent(TEST_TYPE, TEST_PAGE, TEST_METHOD, null, null, null, null, expectedDate)
		then:
			1 * suspiciousActivityDao.add({ AddSuspiciousActivityDbDto activity ->
				assert DateUtils.roughlyEqual(activity?.occurredAt, expectedDate)
				return true
			})
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "logEvent() should assign occurred at to current date when date wasn't provided"() {
		when:
			serviceImpl.logEvent(TEST_TYPE, TEST_PAGE, TEST_METHOD, null, null, null, null, null)
		then:
			1 * suspiciousActivityDao.add({ AddSuspiciousActivityDbDto activity ->
				assert DateUtils.roughlyEqual(activity?.occurredAt, new Date())
				return true
			})
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "logEvent() should pass page to dao"() {
		when:
			serviceImpl.logEvent(TEST_TYPE, TEST_PAGE, TEST_METHOD, null, null, null, null, null)
		then:
			1 * suspiciousActivityDao.add({ AddSuspiciousActivityDbDto activity ->
				assert activity?.page == TEST_PAGE
				return true
			})
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "logAboutAbsentPage() should pass abbreviated page when it's too long"() {
		given:
			String longPageUrl = '/long/url/' + ('x' * Db.SuspiciousActivity.PAGE_URL_LENGTH)
		and:
			String expectedPageUrl = longPageUrl.take(Db.SuspiciousActivity.PAGE_URL_LENGTH - 3) + '...'
		when:
			serviceImpl.logEvent(TEST_TYPE, longPageUrl, TEST_METHOD, null, null, null, null, null)
		then:
			1 * suspiciousActivityDao.add({ AddSuspiciousActivityDbDto activity ->
				assert activity?.page == expectedPageUrl
				return true
			})
	}
	
	@Unroll
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "logEvent() should pass method to dao"(String expectedMethod) {
		when:
			serviceImpl.logEvent(TEST_TYPE, TEST_PAGE, expectedMethod, null, null, null, null, null)
		then:
			1 * suspiciousActivityDao.add({ AddSuspiciousActivityDbDto activity ->
				assert activity?.method == expectedMethod
				return true
			})
		where: expectedMethod | _
			'OPTIONS'         | _
			null              | _
	}

	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "logEvent() should pass abbreviated method when it's too long"() {
		given:
			String method = 'PROPFIND'
		and:
			String exceptedMethod = method.take(Db.SuspiciousActivity.METHOD_LENGTH - 3) + '...'
		when:
			serviceImpl.logEvent(TEST_TYPE, TEST_PAGE, method, null, null, null, TEST_USER_AGENT, null)
		then:
			1 * suspiciousActivityDao.add({ AddSuspiciousActivityDbDto activity ->
				assert activity?.method == exceptedMethod
				return true
			})
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "logEvent() should pass null to dao for unknown user id"() {
		when:
			serviceImpl.logEvent(TEST_TYPE, TEST_PAGE, TEST_METHOD, null, null, null, null, null)
		then:
			1 * suspiciousActivityDao.add({ AddSuspiciousActivityDbDto activity ->
				assert activity?.userId == null
				return true
			})
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "logEvent() should pass user id to dao"() {
		given:
			Integer expectedUserId = 20
		when:
			serviceImpl.logEvent(TEST_TYPE, TEST_PAGE, TEST_METHOD, expectedUserId, null, null, null, null)
		then:
			1 * suspiciousActivityDao.add({ AddSuspiciousActivityDbDto activity ->
				assert activity?.userId == expectedUserId
				return true
			})
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "logEvent() should pass ip to dao"() {
		when:
			serviceImpl.logEvent(TEST_TYPE, TEST_PAGE, TEST_METHOD, null, TEST_IP, null, null, null)
		then:
			1 * suspiciousActivityDao.add({ AddSuspiciousActivityDbDto activity ->
				assert activity?.ip == TEST_IP
				return true
			})
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "logEvent() should pass empty string to dao for unknown ip"() {
		when:
			serviceImpl.logEvent(TEST_TYPE, TEST_PAGE, TEST_METHOD, null, null, null, null, null)
		then:
			1 * suspiciousActivityDao.add({ AddSuspiciousActivityDbDto activity ->
				assert activity?.ip?.empty
				return true
			})
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "logEvent() should pass referer to dao"() {
		when:
			serviceImpl.logEvent(TEST_TYPE, TEST_PAGE, TEST_METHOD, null, null, TEST_REFERER_PAGE, null, null)
		then:
			1 * suspiciousActivityDao.add({ AddSuspiciousActivityDbDto activity ->
				assert activity?.refererPage == TEST_REFERER_PAGE
				return true
			})
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "logEvent() should pass abbreviated referer when it's too long"() {
		given:
			String longRefererUrl = '/long/url/' + ('x' * Db.SuspiciousActivity.REFERER_PAGE_LENGTH)
		and:
			String expectedRefererUrl = longRefererUrl.take(Db.SuspiciousActivity.REFERER_PAGE_LENGTH - 3) + '...'
		when:
			serviceImpl.logEvent(TEST_TYPE, TEST_PAGE, TEST_METHOD, null, null, longRefererUrl, null, null)
		then:
			1 * suspiciousActivityDao.add({ AddSuspiciousActivityDbDto activity ->
				assert activity?.refererPage == expectedRefererUrl
				return true
			})
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "logEvent() should pass null to dao for unknown referer"(String refererPage) {
		when:
			serviceImpl.logEvent(TEST_TYPE, TEST_PAGE, TEST_METHOD, null, null, refererPage, null, null)
		then:
			1 * suspiciousActivityDao.add({ AddSuspiciousActivityDbDto activity ->
				assert activity?.refererPage == null
				return true
			})
		where: refererPage | _
			'  '           | _
			''             | _
			null           | _
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "logEvent() should pass user agent to dao"() {
		when:
			serviceImpl.logEvent(TEST_TYPE, TEST_PAGE, TEST_METHOD, null, null, null, TEST_USER_AGENT, null)
		then:
			1 * suspiciousActivityDao.add({ AddSuspiciousActivityDbDto activity ->
				assert activity?.userAgent == TEST_USER_AGENT
				return true
			})
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "logEvent() should pass abbreviated user agent when it's too long"() {
		given:
			String longUserAgent = 'Mozilla/5.0 (Windows NT 6.1) AppleWebKit/' + ('x' * Db.SuspiciousActivity.USER_AGENT_LENGTH)
		and:
			String expectedUserAgent = longUserAgent.take(Db.SuspiciousActivity.USER_AGENT_LENGTH - 3) + '...'
		when:
			serviceImpl.logEvent(TEST_TYPE, TEST_PAGE, TEST_METHOD, null, null, null, longUserAgent, null)
		then:
			1 * suspiciousActivityDao.add({ AddSuspiciousActivityDbDto activity ->
				assert activity?.userAgent == expectedUserAgent
				return true
			})
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "logEvent() should pass null to dao for unknown user agent"(String userAgent) {
		when:
			serviceImpl.logEvent(TEST_TYPE, TEST_PAGE, TEST_METHOD, null, null, null, userAgent, null)
		then:
			1 * suspiciousActivityDao.add({ AddSuspiciousActivityDbDto activity ->
				assert activity?.userAgent == null
				return true
			})
		where: userAgent | _
			'  '     | _
			''       | _
			null     | _
	}
	
}
