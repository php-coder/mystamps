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
package ru.mystamps.web.service

import org.slf4j.helpers.NOPLogger

import spock.lang.Specification

import ru.mystamps.web.feature.account.UserService
import ru.mystamps.web.feature.account.UsersActivationFullDto
import ru.mystamps.web.feature.account.UsersActivationService
import ru.mystamps.web.feature.category.CategoryService
import ru.mystamps.web.feature.collection.CollectionService
import ru.mystamps.web.feature.country.CountryService
import ru.mystamps.web.feature.series.SeriesService
import ru.mystamps.web.service.dto.AdminDailyReport

@SuppressWarnings(['ClassJavadoc', 'MethodName', 'NoDef', 'NoTabCharacter', 'TrailingWhitespace'])
class CronServiceImplTest extends Specification {
	
	private final CategoryService categoryService = Mock()
	private final CountryService countryService = Mock()
	private final CollectionService collectionService = Mock()
	private final SeriesService seriesService = Mock()
	private final SuspiciousActivityService suspiciousActivityService = Mock()
	private final MailService mailService = Mock()
	private final UserService userService = Mock()
	private final UsersActivationService usersActivationService = Mock()
	
	private final CronService service = new CronServiceImpl(
		NOPLogger.NOP_LOGGER,
		categoryService,
		countryService,
		collectionService,
		seriesService,
		suspiciousActivityService,
		userService,
		usersActivationService,
		mailService
	)
	
	private static void assertMidnight(Date date) {
		assert date[Calendar.HOUR_OF_DAY] == 0
		assert date[Calendar.MINUTE]      == 0
		assert date[Calendar.SECOND]      == 0
		assert date[Calendar.MILLISECOND] == 0
	}
	
	private static void assertDatesEqual(Date first, Date second) {
		assert first[Calendar.YEAR]         == second[Calendar.YEAR]
		assert first[Calendar.MONTH]        == second[Calendar.MONTH]
		assert first[Calendar.DAY_OF_MONTH] == second[Calendar.DAY_OF_MONTH]
	}
	
	private static void assertMidnightOfYesterday(Date date) {
		assert date != null
		assertDatesEqual(date, new Date().previous())
		assertMidnight(date)
	}
	
	private static void assertMidnightOfToday(Date date) {
		assert date != null
		assertDatesEqual(date, new Date())
		assertMidnight(date)
	}
	
	//
	// Tests for sendDailyStatistics()
	//
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "sendDailyStatistics() should invoke services and pass start date to them"() {
		when:
			service.sendDailyStatistics()
		then:
			1 * categoryService.countAddedSince({ Date date ->
				assertMidnightOfYesterday(date)
				return true
			})
			1 * categoryService.countUntranslatedNamesSince({ Date date ->
				assertMidnightOfYesterday(date)
				return true
			})
			1 * countryService.countAddedSince( { Date date ->
				assertMidnightOfYesterday(date)
				return true
			})
			1 * countryService.countUntranslatedNamesSince( { Date date ->
				assertMidnightOfYesterday(date)
				return true
			})
			1 * seriesService.countAddedSince({ Date date ->
				assertMidnightOfYesterday(date)
				return true
			})
			1 * seriesService.countUpdatedSince({ Date date ->
				assertMidnightOfYesterday(date)
				return true
			})
			1 * collectionService.countUpdatedSince({ Date date ->
				assertMidnightOfYesterday(date)
				return true
			})
			1 * usersActivationService.countCreatedSince({ Date date ->
				assertMidnightOfYesterday(date)
				return true
			})
			1 * userService.countRegisteredSince({ Date date ->
				assertMidnightOfYesterday(date)
				return true
			})
			1 * suspiciousActivityService.countByTypeSince('PageNotFound', { Date date ->
				assertMidnightOfYesterday(date)
				return true
			})
			1 * suspiciousActivityService.countByTypeSince('AuthenticationFailed', { Date date ->
				assertMidnightOfYesterday(date)
				return true
			})
			1 * suspiciousActivityService.countByTypeSince('MissingCsrfToken', { Date date ->
				assertMidnightOfYesterday(date)
				return true
			})
			1 * suspiciousActivityService.countByTypeSince('InvalidCsrfToken', { Date date ->
				assertMidnightOfYesterday(date)
				return true
			})
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "sendDailyStatistics() should prepare report and pass it to mail service"() {
		given:
			categoryService.countAddedSince(_ as Date) >> 1
			categoryService.countUntranslatedNamesSince(_ as Date) >> 11
			countryService.countAddedSince(_ as Date) >> 2
			countryService.countUntranslatedNamesSince(_ as Date) >> 12
			seriesService.countAddedSince(_ as Date) >> 3
			seriesService.countUpdatedSince(_ as Date) >> 4
			collectionService.countUpdatedSince(_ as Date) >> 13
			usersActivationService.countCreatedSince(_ as Date) >> 5
			userService.countRegisteredSince(_ as Date) >> 6
		and:
			long expectedEvents = 7 + 8 + 9 + 10
			suspiciousActivityService.countByTypeSince('PageNotFound', _ as Date) >> 7
			suspiciousActivityService.countByTypeSince('AuthenticationFailed', _ as Date) >> 8
			suspiciousActivityService.countByTypeSince('MissingCsrfToken', _ as Date) >> 9
			suspiciousActivityService.countByTypeSince('InvalidCsrfToken', _ as Date) >> 10
		when:
			service.sendDailyStatistics()
		then:
			1 * mailService.sendDailyStatisticsToAdmin({ AdminDailyReport report ->
				assert report != null
				assertMidnightOfYesterday(report.startDate)
				assertMidnightOfToday(report.endDate)
				assert report.addedCategoriesCounter == 1
				assert report.untranslatedCategoriesCounter == 11
				assert report.addedCountriesCounter == 2
				assert report.untranslatedCountriesCounter == 12
				assert report.addedSeriesCounter == 3
				assert report.updatedSeriesCounter == 4
				assert report.updatedCollectionsCounter == 13
				assert report.registrationRequestsCounter == 5
				assert report.registeredUsersCounter == 6
				assert report.notFoundCounter == 7
				assert report.failedAuthCounter == 8
				assert report.missingCsrfCounter == 9
				assert report.invalidCsrfCounter == 10
				assert report.countEvents() == expectedEvents
				return true
			})
	}
	
	//
	// Tests for purgeUsersActivations()
	//
	
	def "purgeUsersActivations() should get expired activations from service"() {
		when:
			service.purgeUsersActivations()
		then:
			1 * usersActivationService.findOlderThan(_ as Integer) >> []
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "purgeUsersActivations() should pass days to service"() {
		given:
			int expectedDays = CronService.PURGE_AFTER_DAYS
		when:
			service.purgeUsersActivations()
		then:
			1 * usersActivationService.findOlderThan({ int days ->
				assert days == expectedDays
				return true
			}) >> []
	}
	
	def "purgeUsersActivations() should throw exception when null activations were returned"() {
		given:
			usersActivationService.findOlderThan(_ as Integer) >> null
		when:
			service.purgeUsersActivations()
		then:
			IllegalStateException ex = thrown()
			ex.message == 'Expired activations must be non null'
	}
	
	def "purgeUsersActivations() should delete expired activations"() {
		given:
			List<UsersActivationFullDto> expectedActivations = [ TestObjects.createUsersActivationFullDto() ]
			usersActivationService.findOlderThan(_ as Integer) >> expectedActivations
		when:
			service.purgeUsersActivations()
		then:
			1 * usersActivationService.remove(_ as String)
	}
	
	def "purgeUsersActivations() should do nothing if no activations"() {
		given:
			usersActivationService.findOlderThan(_ as Integer) >> []
		when:		
			service.purgeUsersActivations()
		then:
			0 * usersActivationService.remove(_ as String)
	}
	
}

