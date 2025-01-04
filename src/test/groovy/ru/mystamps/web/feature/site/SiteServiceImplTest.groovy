/**
 * Copyright (C) 2009-2025 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.feature.site

import org.slf4j.helpers.NOPLogger
import ru.mystamps.web.feature.site.SiteDb.SuspiciousActivity
import ru.mystamps.web.service.TestObjects
import spock.lang.Specification

class SiteServiceImplTest extends Specification {
	private static final String TEST_TYPE         = TestObjects.TEST_ACTIVITY_TYPE
	private static final String TEST_PAGE         = TestObjects.TEST_ACTIVITY_PAGE
	private static final String TEST_METHOD       = TestObjects.TEST_ACTIVITY_METHOD
	private static final String TEST_USER_AGENT   = TestObjects.TEST_ACTIVITY_AGENT
	
	private final SuspiciousActivityDao suspiciousActivityDao = Mock()
	
	private SiteServiceImpl serviceImpl
	
	def setup() {
		serviceImpl = Spy(
			SiteServiceImpl,
			constructorArgs:[NOPLogger.NOP_LOGGER, suspiciousActivityDao]
		)
	}
	
	//
	// Tests for logEvent()
	//
	
	def "logEvent() should pass abbreviated page when it's too long"() {
		given:
			String longPageUrl = '/long/url/' + ('x' * SuspiciousActivity.PAGE_URL_LENGTH)
		and:
			String expectedPageUrl = longPageUrl.take(SuspiciousActivity.PAGE_URL_LENGTH - 3) + '...'
		when:
			serviceImpl.logEvent(TEST_TYPE, longPageUrl, TEST_METHOD, null, null, null, null, null)
		then:
			1 * suspiciousActivityDao.add({ AddSuspiciousActivityDbDto activity ->
				assert activity?.page == expectedPageUrl
				return true
			})
	}

	def "logEvent() should pass abbreviated method when it's too long"() {
		given:
			String method = 'PROPFIND'
		and:
			String exceptedMethod = method.take(SuspiciousActivity.METHOD_LENGTH - 3) + '...'
		when:
			serviceImpl.logEvent(TEST_TYPE, TEST_PAGE, method, null, null, null, TEST_USER_AGENT, null)
		then:
			1 * suspiciousActivityDao.add({ AddSuspiciousActivityDbDto activity ->
				assert activity?.method == exceptedMethod
				return true
			})
	}
	
	def "logEvent() should pass abbreviated referer when it's too long"() {
		given:
			String longRefererUrl = '/long/url/' + ('x' * SuspiciousActivity.REFERER_PAGE_LENGTH)
		and:
			String expectedRefererUrl = longRefererUrl.take(SuspiciousActivity.REFERER_PAGE_LENGTH - 3) + '...'
		when:
			serviceImpl.logEvent(TEST_TYPE, TEST_PAGE, TEST_METHOD, null, null, longRefererUrl, null, null)
		then:
			1 * suspiciousActivityDao.add({ AddSuspiciousActivityDbDto activity ->
				assert activity?.refererPage == expectedRefererUrl
				return true
			})
	}
	
	def "logEvent() should pass abbreviated user agent when it's too long"() {
		given:
			String longUserAgent = 'Mozilla/5.0 (Windows NT 6.1) AppleWebKit/' + ('x' * SuspiciousActivity.USER_AGENT_LENGTH)
		and:
			String expectedUserAgent = longUserAgent.take(SuspiciousActivity.USER_AGENT_LENGTH - 3) + '...'
		when:
			serviceImpl.logEvent(TEST_TYPE, TEST_PAGE, TEST_METHOD, null, null, null, longUserAgent, null)
		then:
			1 * suspiciousActivityDao.add({ AddSuspiciousActivityDbDto activity ->
				assert activity?.userAgent == expectedUserAgent
				return true
			})
	}
	
}
