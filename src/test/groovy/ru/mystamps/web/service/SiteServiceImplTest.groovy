/**
 * Copyright (C) 2009-2015 Slava Semushin <slava.semushin@gmail.com>
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

import spock.lang.Specification

import ru.mystamps.web.dao.SuspiciousActivityDao
import ru.mystamps.web.entity.User
import ru.mystamps.web.entity.SuspiciousActivity
import ru.mystamps.web.entity.SuspiciousActivityType
import ru.mystamps.web.tests.DateUtils

class SiteServiceImplTest extends Specification {
	private static final String TEST_PAGE         = 'http://example.org/some/page'
	private static final String TEST_IP           = '127.0.0.1'
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
			service.logAboutAbsentPage(TEST_PAGE, null, null, null, null)
		then:
			1 * suspiciousActivityDao.add(_ as SuspiciousActivity)
	}
	
	def "logAboutAbsentPage() should pass activity type to dao"() {
		given:
			SuspiciousActivityType expectedType = TestObjects.createPageNotFoundActivityType()
		when:
			service.logAboutAbsentPage(TEST_PAGE, null, null, null, null)
		then:
			1 * suspiciousActivityDao.add({ SuspiciousActivity activity ->
				assert activity?.type?.name == expectedType.name
				return true
			})
	}
	
	def "logAboutAbsentPage() should assign occurred at to current date"() {
		when:
			service.logAboutAbsentPage(TEST_PAGE, null, null, null, null)
		then:
			1 * suspiciousActivityDao.add({ SuspiciousActivity activity ->
				assert DateUtils.roughlyEqual(activity?.occurredAt, new Date())
				return true
			})
	}
	
	def "logAboutAbsentPage() should throw exception when page is null"() {
		when:
			service.logAboutAbsentPage(null, null, null, null, null)
		then:
			thrown IllegalArgumentException
	}
	
	def "logAboutAbsentPage() should pass page to dao"() {
		when:
			service.logAboutAbsentPage(TEST_PAGE, null, null, null, null)
		then:
			1 * suspiciousActivityDao.add({ SuspiciousActivity activity ->
				assert activity?.page == TEST_PAGE
				return true
			})
	}
	
	def "logAboutAbsentPage() should pass null to dao for unknown user"() {
		when:
			service.logAboutAbsentPage(TEST_PAGE, null, null, null, null)
		then:
			1 * suspiciousActivityDao.add({ SuspiciousActivity activity ->
				assert activity?.user == null
				return true
			})
	}
	
	def "logAboutAbsentPage() should pass user to dao"() {
		given:
			User user = TestObjects.createUser()
		when:
			service.logAboutAbsentPage(TEST_PAGE, user, null, null, null)
		then:
			1 * suspiciousActivityDao.add({ SuspiciousActivity activity ->
				assert activity?.user == user
				return true
			})
	}
	
	def "logAboutAbsentPage() should pass ip to dao"() {
		when:
			service.logAboutAbsentPage(TEST_PAGE, null, TEST_IP, null, null)
		then:
			1 * suspiciousActivityDao.add({ SuspiciousActivity activity ->
				assert activity?.ip == TEST_IP
				return true
			})
	}
	
	def "logAboutAbsentPage() should pass empty string to dao for unknown ip"() {
		when:
			service.logAboutAbsentPage(TEST_PAGE, null, null, null, null)
		then:
			1 * suspiciousActivityDao.add({ SuspiciousActivity activity ->
				assert activity?.ip?.empty
				return true
			})
	}
	
	def "logAboutAbsentPage() should pass referer to dao"() {
		when:
			service.logAboutAbsentPage(TEST_PAGE, null, null, TEST_REFERER_PAGE, null)
		then:
			1 * suspiciousActivityDao.add({ SuspiciousActivity activity ->
				assert activity?.refererPage == TEST_REFERER_PAGE
				return true
			})
	}
	
	def "logAboutAbsentPage() should pass empty string to dao for unknown referer"() {
		when:
			service.logAboutAbsentPage(TEST_PAGE, null, null, null, null)
		then:
			1 * suspiciousActivityDao.add({ SuspiciousActivity activity ->
				assert activity?.refererPage?.empty
				return true
			})
	}
	
	def "logAboutAbsentPage() should pass user agent to dao"() {
		when:
			service.logAboutAbsentPage(TEST_PAGE, null, null, null, TEST_USER_AGENT)
		then:
			1 * suspiciousActivityDao.add({ SuspiciousActivity activity ->
				assert activity?.userAgent == TEST_USER_AGENT
				return true
			})
	}
	
	def "logAboutAbsentPage() should pass empty string to dao for unknown user agent"() {
		when:
			service.logAboutAbsentPage(TEST_PAGE, null, null, null, null)
		then:
			1 * suspiciousActivityDao.add({ SuspiciousActivity activity ->
				assert activity?.userAgent?.empty
				return true
			})
	}
	
	//
	// Tests for logAboutFailedAuthentication()
	//
	
	def "logAboutFailedAuthentication() should call dao"() {
		when:
			service.logAboutFailedAuthentication(TEST_PAGE, null, null, null, null)
		then:
			1 * suspiciousActivityDao.add(_ as SuspiciousActivity)
	}
	
	def "logAboutFailedAuthentication() should pass activity type to dao"() {
		given:
			SuspiciousActivityType expectedType = TestObjects.createAuthFailedActivityType()
		when:
			service.logAboutFailedAuthentication(TEST_PAGE, null, null, null, null)
		then:
			1 * suspiciousActivityDao.add({ SuspiciousActivity activity ->
				assert activity?.type?.name == expectedType.name
				return true
			})
	}
	
	def "logAboutFailedAuthentication() should assign occurred at to current date"() {
		when:
			service.logAboutFailedAuthentication(TEST_PAGE, null, null, null, null)
		then:
			1 * suspiciousActivityDao.add({ SuspiciousActivity activity ->
				assert DateUtils.roughlyEqual(activity?.occurredAt, new Date())
				return true
			})
	}
	
	def "logAboutFailedAuthentication() should throw exception when page is null"() {
		when:
			service.logAboutFailedAuthentication(null, null, null, null, null)
		then:
			thrown IllegalArgumentException
	}
	
	def "logAboutFailedAuthentication() should pass page to dao"() {
		when:
			service.logAboutFailedAuthentication(TEST_PAGE, null, null, null, null)
		then:
			1 * suspiciousActivityDao.add({ SuspiciousActivity activity ->
				assert activity?.page == TEST_PAGE
				return true
			})
	}
	
	def "logAboutFailedAuthentication() should pass null to dao for unknown user"() {
		when:
			service.logAboutFailedAuthentication(TEST_PAGE, null, null, null, null)
		then:
			1 * suspiciousActivityDao.add({ SuspiciousActivity activity ->
				assert activity?.user == null
				return true
			})
	}
	
	def "logAboutFailedAuthentication() should pass user to dao"() {
		given:
			User user = TestObjects.createUser()
		when:
			service.logAboutFailedAuthentication(TEST_PAGE, user, null, null, null)
		then:
			1 * suspiciousActivityDao.add({ SuspiciousActivity activity ->
				assert activity?.user == user
				return true
			})
	}
	
	def "logAboutFailedAuthentication() should pass ip to dao"() {
		when:
			service.logAboutFailedAuthentication(TEST_PAGE, null, TEST_IP, null, null)
		then:
			1 * suspiciousActivityDao.add({ SuspiciousActivity activity ->
				assert activity?.ip == TEST_IP
				return true
			})
	}
	
	def "logAboutFailedAuthentication() should pass empty string to dao for unknown ip"() {
		when:
			service.logAboutFailedAuthentication(TEST_PAGE, null, null, null, null)
		then:
			1 * suspiciousActivityDao.add({ SuspiciousActivity activity ->
				assert activity?.ip?.empty
				return true
			})
	}
	
	def "logAboutFailedAuthentication() should pass referer to dao"() {
		when:
			service.logAboutFailedAuthentication(TEST_PAGE, null, null, TEST_REFERER_PAGE, null)
		then:
			1 * suspiciousActivityDao.add({ SuspiciousActivity activity ->
				assert activity?.refererPage == TEST_REFERER_PAGE
				return true
			})
	}
	
	def "logAboutFailedAuthentication() should pass empty string to dao for unknown referer"() {
		when:
			service.logAboutFailedAuthentication(TEST_PAGE, null, null, null, null)
		then:
			1 * suspiciousActivityDao.add({ SuspiciousActivity activity ->
				assert activity?.refererPage?.empty
				return true
			})
	}
	
	def "logAboutFailedAuthentication() should pass user agent to dao"() {
		when:
			service.logAboutFailedAuthentication(TEST_PAGE, null, null, null, TEST_USER_AGENT)
		then:
			1 * suspiciousActivityDao.add({ SuspiciousActivity activity ->
				assert activity?.userAgent == TEST_USER_AGENT
				return true
			})
	}
	
	def "logAboutFailedAuthentication() should pass empty string to dao for unknown user agent"() {
		when:
			service.logAboutFailedAuthentication(TEST_PAGE, null, null, null, null)
		then:
			1 * suspiciousActivityDao.add({ SuspiciousActivity activity ->
				assert activity?.userAgent?.empty
				return true
			})
	}
	
}
