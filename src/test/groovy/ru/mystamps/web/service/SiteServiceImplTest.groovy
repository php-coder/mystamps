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

class SiteServiceImplTest extends Specification {
	private static final String TEST_PAGE         = 'http://example.org/some/page'
	private static final String TEST_IP           = '127.0.0.1'
	private static final String TEST_METHOD       = 'GET'
	private static final String TEST_REFERER_PAGE = 'http://example.org/referer'
	private static final String TEST_USER_AGENT   = 'Some browser'
	
	private SuspiciousActivityDao suspiciousActivityDao = Mock()
	private SiteService service
	
	def setup() {
		service = new SiteServiceImpl(suspiciousActivityDao)
	}
	
	//
	// Tests for logAboutAbsentPage()
	//
	
	def "logAboutAbsentPage() should call dao"() {
		when:
			service.logAboutAbsentPage(TEST_PAGE, TEST_METHOD, null, null, null, null)
		then:
			1 * suspiciousActivityDao.add(_ as AddSuspiciousActivityDbDto)
	}
	
	def "logAboutAbsentPage() should pass activity type to dao"() {
		when:
			service.logAboutAbsentPage(TEST_PAGE, TEST_METHOD, null, null, null, null)
		then:
			1 * suspiciousActivityDao.add({ AddSuspiciousActivityDbDto activity ->
				assert activity?.type == 'PageNotFound'
				return true
			})
	}
	
	def "logAboutAbsentPage() should assign occurred at to current date"() {
		when:
			service.logAboutAbsentPage(TEST_PAGE, TEST_METHOD, null, null, null, null)
		then:
			1 * suspiciousActivityDao.add({ AddSuspiciousActivityDbDto activity ->
				assert DateUtils.roughlyEqual(activity?.occurredAt, new Date())
				return true
			})
	}
	
	def "logAboutAbsentPage() should throw exception when page is null"() {
		when:
			service.logAboutAbsentPage(null, null, null, null, null, null)
		then:
			thrown IllegalArgumentException
	}
	
	def "logAboutAbsentPage() should pass page to dao"() {
		when:
			service.logAboutAbsentPage(TEST_PAGE, TEST_METHOD, null, null, null, null)
		then:
			1 * suspiciousActivityDao.add({ AddSuspiciousActivityDbDto activity ->
				assert activity?.page == TEST_PAGE
				return true
			})
	}
	
	def "logAboutAbsentPage() should pass abbreviated page when it's too long"() {
		given:
			String longPageUrl = '/long/url/' + ('x' * Db.SuspiciousActivity.PAGE_URL_LENGTH)
		and:
			String expectedPageUrl = longPageUrl.take(Db.SuspiciousActivity.PAGE_URL_LENGTH - 3) + '...'
		when:
			service.logAboutAbsentPage(longPageUrl, TEST_METHOD, null, null, null, null)
		then:
			1 * suspiciousActivityDao.add({ AddSuspiciousActivityDbDto activity ->
				assert activity?.page == expectedPageUrl
				return true
			})
	}
	
	@Unroll
	def "logAboutAbsentPage() should pass method to dao"(String expectedMethod) {
		when:
			service.logAboutAbsentPage(TEST_PAGE, expectedMethod, null, null, null, null)
		then:
			1 * suspiciousActivityDao.add({ AddSuspiciousActivityDbDto activity ->
				assert activity?.method == expectedMethod
				return true
			})
		where: expectedMethod | _
			'OPTIONS'         | _
			null              | _
	}
	
	def "logAboutAbsentPage() should pass null to dao for unknown user"() {
		when:
			service.logAboutAbsentPage(TEST_PAGE, TEST_METHOD, null, null, null, null)
		then:
			1 * suspiciousActivityDao.add({ AddSuspiciousActivityDbDto activity ->
				assert activity?.userId == null
				return true
			})
	}
	
	def "logAboutAbsentPage() should pass user to dao"() {
		given:
			Integer expectedUserId = 20
		when:
			service.logAboutAbsentPage(TEST_PAGE, TEST_METHOD, expectedUserId, null, null, null)
		then:
			1 * suspiciousActivityDao.add({ AddSuspiciousActivityDbDto activity ->
				assert activity?.userId == expectedUserId
				return true
			})
	}
	
	def "logAboutAbsentPage() should pass ip to dao"() {
		when:
			service.logAboutAbsentPage(TEST_PAGE, TEST_METHOD, null, TEST_IP, null, null)
		then:
			1 * suspiciousActivityDao.add({ AddSuspiciousActivityDbDto activity ->
				assert activity?.ip == TEST_IP
				return true
			})
	}
	
	def "logAboutAbsentPage() should pass empty string to dao for unknown ip"() {
		when:
			service.logAboutAbsentPage(TEST_PAGE, TEST_METHOD, null, null, null, null)
		then:
			1 * suspiciousActivityDao.add({ AddSuspiciousActivityDbDto activity ->
				assert activity?.ip?.empty
				return true
			})
	}
	
	def "logAboutAbsentPage() should pass referer to dao"() {
		when:
			service.logAboutAbsentPage(TEST_PAGE, TEST_METHOD, null, null, TEST_REFERER_PAGE, null)
		then:
			1 * suspiciousActivityDao.add({ AddSuspiciousActivityDbDto activity ->
				assert activity?.refererPage == TEST_REFERER_PAGE
				return true
			})
	}
	def "logAboutAbsentPage() should pass abbreviated referer when it's too long"() {
		given:
			String longRefererUrl = '/long/url/' + ('x' * Db.SuspiciousActivity.REFERER_PAGE_LENGTH)
		and:
			String expectedRefererUrl = longRefererUrl.take(Db.SuspiciousActivity.REFERER_PAGE_LENGTH - 3) + '...'
		when:
			service.logAboutAbsentPage(TEST_PAGE, TEST_METHOD, null, null, longRefererUrl, null)
		then:
			1 * suspiciousActivityDao.add({ AddSuspiciousActivityDbDto activity ->
				assert activity?.refererPage == expectedRefererUrl
				return true
			})
	}
	
	def "logAboutAbsentPage() should pass null to dao for unknown referer"(String refererPage) {
		when:
			service.logAboutAbsentPage(TEST_PAGE, TEST_METHOD, null, null, refererPage, null)
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
	
	def "logAboutAbsentPage() should pass user agent to dao"() {
		when:
			service.logAboutAbsentPage(TEST_PAGE, TEST_METHOD, null, null, null, TEST_USER_AGENT)
		then:
			1 * suspiciousActivityDao.add({ AddSuspiciousActivityDbDto activity ->
				assert activity?.userAgent == TEST_USER_AGENT
				return true
			})
	}
	def "logAboutAbsentPage() should pass abbreviated user agent when it's too long"() {
		given:
			String longUserAgent = 'Mozilla/5.0 (Windows NT 6.1) AppleWebKit/' + ('x' * Db.SuspiciousActivity.USER_AGENT_LENGTH)
		and:
			String expectedUserAgent = longUserAgent.take(Db.SuspiciousActivity.USER_AGENT_LENGTH - 3) + '...'
		when:
			service.logAboutAbsentPage(TEST_PAGE, TEST_METHOD, null, null, null, longUserAgent)
		then:
			1 * suspiciousActivityDao.add({ AddSuspiciousActivityDbDto activity ->
				assert activity?.userAgent == expectedUserAgent
				return true
			})
	}
	
	def "logAboutAbsentPage() should pass null to dao for unknown user agent"(String userAgent) {
		when:
			service.logAboutAbsentPage(TEST_PAGE, TEST_METHOD, null, null, null, userAgent)
		then:
			1 * suspiciousActivityDao.add({ AddSuspiciousActivityDbDto activity ->
				assert activity?.userAgent == null
				return true
			})
		where: userAgent | _
		'  '             | _
		''               | _
		null             | _
	}
	
	//
	// Tests for logAboutFailedAuthentication()
	//
	
	def "logAboutFailedAuthentication() should call dao"() {
		when:
			service.logAboutFailedAuthentication(TEST_PAGE, TEST_METHOD, null, null, null, null, null)
		then:
			1 * suspiciousActivityDao.add(_ as AddSuspiciousActivityDbDto)
	}
	
	def "logAboutFailedAuthentication() should pass activity type to dao"() {
		when:
			service.logAboutFailedAuthentication(TEST_PAGE, TEST_METHOD, null, null, null, null, null)
		then:
			1 * suspiciousActivityDao.add({ AddSuspiciousActivityDbDto activity ->
				assert activity?.type == 'AuthenticationFailed'
				return true
			})
	}
	
	def "logAboutFailedAuthentication() should assign occurred at to current date when date was provided"() {
		given:
			Date expectedDate = new Date() - 100;
		when:
			service.logAboutFailedAuthentication(TEST_PAGE, TEST_METHOD, null, null, null, null, expectedDate)
		then:
			1 * suspiciousActivityDao.add({ AddSuspiciousActivityDbDto activity ->
				assert DateUtils.roughlyEqual(activity?.occurredAt, expectedDate)
				return true
			})
	}
	
	def "logAboutFailedAuthentication() should assign occurred at to current date when date wasn't provided"() {
		when:
			service.logAboutFailedAuthentication(TEST_PAGE, TEST_METHOD, null, null, null, null, null)
		then:
			1 * suspiciousActivityDao.add({ AddSuspiciousActivityDbDto activity ->
				assert DateUtils.roughlyEqual(activity?.occurredAt, new Date())
				return true
			})
	}
	
	def "logAboutFailedAuthentication() should throw exception when page is null"() {
		when:
			service.logAboutFailedAuthentication(null, null, null, null, null, null, null)
		then:
			thrown IllegalArgumentException
	}
	
	def "logAboutFailedAuthentication() should pass page to dao"() {
		when:
			service.logAboutFailedAuthentication(TEST_PAGE, TEST_METHOD, null, null, null, null, null)
		then:
			1 * suspiciousActivityDao.add({ AddSuspiciousActivityDbDto activity ->
				assert activity?.page == TEST_PAGE
				return true
			})
	}
	def "logAboutFailedAuthentication() should pass abbreviated page when it's too long"() {
		given:
			String longPageUrl = '/long/url/' + ('x' * Db.SuspiciousActivity.PAGE_URL_LENGTH)
		and:
			String expectedPageUrl = longPageUrl.take(Db.SuspiciousActivity.PAGE_URL_LENGTH - 3) + '...'
		when:
			service.logAboutFailedAuthentication(longPageUrl, TEST_METHOD, null, null, null, null, null)
		then:
			1 * suspiciousActivityDao.add({ AddSuspiciousActivityDbDto activity ->
				assert activity?.page == expectedPageUrl
				return true
			})
	}
	
	def "logAboutFailedAuthentication() should pass null to dao for unknown user"() {
		when:
			service.logAboutFailedAuthentication(TEST_PAGE, TEST_METHOD, null, null, null, null, null)
		then:
			1 * suspiciousActivityDao.add({ AddSuspiciousActivityDbDto activity ->
				assert activity?.userId == null
				return true
			})
	}
	
	def "logAboutFailedAuthentication() should pass user to dao"() {
		given:
			Integer expectedUserId = 30
		when:
			service.logAboutFailedAuthentication(TEST_PAGE, TEST_METHOD, expectedUserId, null, null, null, null)
		then:
			1 * suspiciousActivityDao.add({ AddSuspiciousActivityDbDto activity ->
				assert activity?.userId == expectedUserId
				return true
			})
	}
	
	def "logAboutFailedAuthentication() should pass ip to dao"() {
		when:
			service.logAboutFailedAuthentication(TEST_PAGE, TEST_METHOD, null, TEST_IP, null, null, null)
		then:
			1 * suspiciousActivityDao.add({ AddSuspiciousActivityDbDto activity ->
				assert activity?.ip == TEST_IP
				return true
			})
	}
	
	def "logAboutFailedAuthentication() should pass empty string to dao for unknown ip"() {
		when:
			service.logAboutFailedAuthentication(TEST_PAGE, TEST_METHOD, null, null, null, null, null)
		then:
			1 * suspiciousActivityDao.add({ AddSuspiciousActivityDbDto activity ->
				assert activity?.ip?.empty
				return true
			})
	}
	
	def "logAboutFailedAuthentication() should pass referer to dao"() {
		when:
			service.logAboutFailedAuthentication(TEST_PAGE, TEST_METHOD, null, null, TEST_REFERER_PAGE, null, null)
		then:
			1 * suspiciousActivityDao.add({ AddSuspiciousActivityDbDto activity ->
				assert activity?.refererPage == TEST_REFERER_PAGE
				return true
			})
	}
	
	def "logAboutFailedAuthentication() should pass null to dao for unknown referer"(String refererPage) {
		when:
			service.logAboutFailedAuthentication(TEST_PAGE, TEST_METHOD, null, null, refererPage, null, null)
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
	
	def "logAboutFailedAuthentication() should pass abbreviated referer when it's too long"() {
		given:
			String longRefererUrl = '/long/url/' + ('x' * Db.SuspiciousActivity.REFERER_PAGE_LENGTH)
		and:
			String expectedRefererUrl = longRefererUrl.take(Db.SuspiciousActivity.REFERER_PAGE_LENGTH - 3) + '...'
		when:
			service.logAboutFailedAuthentication(TEST_PAGE, TEST_METHOD, null, null, longRefererUrl, null, null)
		then:
			1 * suspiciousActivityDao.add({ AddSuspiciousActivityDbDto activity ->
				assert activity?.refererPage == expectedRefererUrl
				return true
			})
	}
	
	def "logAboutFailedAuthentication() should pass user agent to dao"() {
		when:
			service.logAboutFailedAuthentication(TEST_PAGE, TEST_METHOD, null, null, null, TEST_USER_AGENT, null)
		then:
			1 * suspiciousActivityDao.add({ AddSuspiciousActivityDbDto activity ->
				assert activity?.userAgent == TEST_USER_AGENT
				return true
			})
	}
	
	def "logAboutFailedAuthentication() should pass null to dao for unknown user agent"(String userAgent) {
		when:
			service.logAboutFailedAuthentication(TEST_PAGE, TEST_METHOD, null, null, null, userAgent, null)
		then:
			1 * suspiciousActivityDao.add({ AddSuspiciousActivityDbDto activity ->
				assert activity?.userAgent == null
				return true
			})
		where: userAgent | _
			'  '         | _
			''           | _
			null         | _
	}
	
	def "logAboutFailedAuthentication() should pass abbreviated user agent when it's too long"() {
		given:
			String longUserAgent = 'Mozilla/5.0 (Windows NT 6.1) AppleWebKit/' + ('x' * Db.SuspiciousActivity.USER_AGENT_LENGTH)
		and:
			String expectedUserAgent = longUserAgent.take(Db.SuspiciousActivity.USER_AGENT_LENGTH - 3) + '...'
		when:
			service.logAboutFailedAuthentication(TEST_PAGE, TEST_METHOD, null, null, null, longUserAgent, null)
		then:
			1 * suspiciousActivityDao.add({ AddSuspiciousActivityDbDto activity ->
				assert activity?.userAgent == expectedUserAgent
				return true
			})
	}
	
}
