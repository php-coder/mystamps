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

import spock.lang.Specification

import ru.mystamps.web.dao.dto.UsersActivationFullDto
import ru.mystamps.web.service.dto.AdminDailyReport

class CronServiceImplTest extends Specification {
	
	private CategoryService categoryService = Mock()
	private CountryService countryService = Mock()
	private SeriesService seriesService = Mock()
	private MailService mailService = Mock()
	private UserService userService = Mock()
	private UsersActivationService usersActivationService = Mock()
	
	private CronService service = new CronServiceImpl(
		categoryService,
		countryService,
		seriesService,
		userService,
		usersActivationService,
		mailService
	)
	
	private static def assertMidnight(Date date) {
		assert date[Calendar.HOUR_OF_DAY] == 0
		assert date[Calendar.MINUTE]      == 0
		assert date[Calendar.SECOND]      == 0
		assert date[Calendar.MILLISECOND] == 0
	}
	
	private static def assertDatesEqual(Date first, Date second) {
		assert first[Calendar.YEAR]         == second[Calendar.YEAR]
		assert first[Calendar.MONTH]        == second[Calendar.MONTH]
		assert first[Calendar.DAY_OF_MONTH] == second[Calendar.DAY_OF_MONTH]
	}
	
	private static def assertMidnightOfYesterday(Date date) {
		assert date != null
		assertDatesEqual(date, new Date().previous())
		assertMidnight(date)
	}
	
	private static def assertMidnightOfToday(Date date) {
		assert date != null
		assertDatesEqual(date, new Date())
		assertMidnight(date)
	}
	
	//
	// Tests for sendDailyStatistics()
	//
	
	def "sendDailyStatistics() should invoke services and pass start date to them"() {
		when:
			service.sendDailyStatistics()
		then:
			1 * categoryService.countAddedSince({ Date date ->
				assertMidnightOfYesterday(date)
				return true
			})
			1 * countryService.countAddedSince( { Date date ->
				assertMidnightOfYesterday(date)
				return true
			})
			1 * seriesService.countAddedSince({ Date date ->
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
	}
	
	def "sendDailyStatistics() should prepare report and pass it to mail service"() {
		given:
			categoryService.countAddedSince(_ as Date) >> 1
			countryService.countAddedSince(_ as Date) >> 2
			seriesService.countAddedSince(_ as Date) >> 3
			usersActivationService.countCreatedSince(_ as Date) >> 4
			userService.countRegisteredSince(_ as Date) >> 5
		when:
			service.sendDailyStatistics()
		then:
			1 * mailService.sendDailyStatisticsToAdmin({ AdminDailyReport report ->
				assert report != null
				assertMidnightOfYesterday(report.startDate)
				assertMidnightOfToday(report.endDate)
				assert report.addedCategoriesCounter == 1
				assert report.addedCountriesCounter == 2
				assert report.addedSeriesCounter == 3
				assert report.registrationRequestsCounter == 4
				assert report.registeredUsersCounter == 5
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
			thrown IllegalStateException
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

