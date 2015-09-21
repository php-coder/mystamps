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

import ru.mystamps.web.dao.UsersActivationDao
import ru.mystamps.web.entity.UsersActivation
import ru.mystamps.web.tests.DateUtils

class CronServiceImplTest extends Specification {
	
	private UsersActivationDao usersActivationDao = Mock()
	private UsersActivationService usersActivationService = Mock()
	
	private CronService service = new CronServiceImpl(usersActivationDao, usersActivationService)
	
	//
	// Tests for purgeUsersActivations()
	//
	
	def "purgeUsersActivations() should get expired activations from dao"() {
		when:
			service.purgeUsersActivations()
		then:
			1 * usersActivationDao.findByCreatedAtLessThan(_ as Date) >> []
	}
	
	def "purgeUsersActivations() should pass expired date to dao"() {
		given:
			Date expectedDate = new Date() - CronService.PURGE_AFTER_DAYS
		when:
			service.purgeUsersActivations()
		then:
			1 * usersActivationDao.findByCreatedAtLessThan({ Date passedDate ->
				assert DateUtils.roughlyEqual(passedDate, expectedDate)
				return true
			}) >> []
	}
	
	def "purgeUsersActivations() should throw exception when null activations was returned"() {
		given:
			usersActivationDao.findByCreatedAtLessThan(_ as Date) >> null
		when:
			service.purgeUsersActivations()
		then:
			thrown IllegalStateException
	}
	
	def "purgeUsersActivations() should delete expired activations"() {
		given:
			List<UsersActivation> expectedActivations = [ TestObjects.createUsersActivation() ]
			usersActivationDao.findByCreatedAtLessThan(_ as Date) >> expectedActivations
		when:
			service.purgeUsersActivations()
		then:
			1 * usersActivationService.remove(_ as String)
	}
	
	def "purgeUsersActivations() should do nothing if no activations"() {
		given:
			usersActivationDao.findByCreatedAtLessThan(_ as Date) >> []
		when:		
			service.purgeUsersActivations()
		then:
			0 * usersActivationService.remove(_ as String)
	}
	
}

