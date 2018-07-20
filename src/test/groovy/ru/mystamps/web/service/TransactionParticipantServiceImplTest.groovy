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

import static io.qala.datagen.RandomShortApi.bool
import static io.qala.datagen.RandomShortApi.nullOr
import static io.qala.datagen.RandomShortApi.nullOrBlank

import org.slf4j.helpers.NOPLogger

import spock.lang.Specification

import ru.mystamps.web.dao.TransactionParticipantDao
import ru.mystamps.web.dao.dto.AddParticipantDbDto
import ru.mystamps.web.dao.dto.EntityWithIdDto
import ru.mystamps.web.dao.dto.EntityWithParentDto
import ru.mystamps.web.feature.participant.AddParticipantForm
import ru.mystamps.web.tests.Random

@SuppressWarnings(['ClassJavadoc', 'MethodName', 'NoDef', 'NoTabCharacter', 'TrailingWhitespace'])
class TransactionParticipantServiceImplTest extends Specification {
	
	private final TransactionParticipantDao transactionParticipantDao = Mock()
	private final TransactionParticipantService service = new TransactionParticipantServiceImpl(
		NOPLogger.NOP_LOGGER,
		transactionParticipantDao
	)
	
	//
	// Tests for add()
	//
	
	def 'add() should throw exception if dto is null'() {
		when:
			service.add(null)
		then:
			thrown IllegalArgumentException
	}
	
	def 'add() should throw exception if name is null'() {
		given:
			AddParticipantForm form = new AddParticipantForm()
			form.setName(null)
			form.setBuyer(bool())
			form.setSeller(bool())
		when:
			service.add(form)
		then:
			thrown IllegalArgumentException
	}
	
	def 'add() should throw exception when buyer flag is null'() {
		given:
			AddParticipantForm form = new AddParticipantForm()
			form.setName(Random.participantName())
			form.setBuyer(null)
			form.setSeller(bool())
		when:
			service.add(form)
		then:
			thrown IllegalArgumentException
	}
	
	def 'add() should throw exception when seller flag is null'() {
		given:
			AddParticipantForm form = new AddParticipantForm()
			form.setName(Random.participantName())
			form.setBuyer(bool())
			form.setSeller(null)
		when:
			service.add(form)
		then:
			thrown IllegalArgumentException
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def 'add() should create participant'() {
		given:
			String expectedName     = Random.participantName()
			String expectedUrl      = Random.url()
			Integer expectedGroupId = nullOr(Random.id())
			Boolean expectedBuyer   = bool()
			Boolean expectedSeller  = bool()
			Integer expectedResult  = Random.id()
		and:
			AddParticipantForm form = new AddParticipantForm()
			form.setName(expectedName)
			form.setUrl(expectedUrl)
			form.setGroupId(expectedGroupId)
			form.setBuyer(expectedBuyer)
			form.setSeller(expectedSeller)
		when:
			Integer result = service.add(form)
		then:
			1 * transactionParticipantDao.add({ AddParticipantDbDto participant ->
				assert participant?.name == expectedName
				assert participant?.url  == expectedUrl
				assert participant?.groupId  == expectedGroupId
				assert participant?.buyer == expectedBuyer
				assert participant?.seller == expectedSeller
				return true
			}) >> expectedResult
		and:
			result == expectedResult
	}
	
	//
	// Tests for findBuyersWithParents()
	//
	
	def 'findBuyersWithParents() should invoke dao and return its result'() {
		given:
			List<EntityWithParentDto> expectedResult = Random.listOfEntityWithParentDto()
		when:
			List<EntityWithParentDto> result = service.findBuyersWithParents()
		then:
			1 * transactionParticipantDao.findBuyersWithParents() >> expectedResult
		and:
			result == expectedResult
	}
	
	//
	// Tests for findSellersWithParents()
	//
	
	def 'findSellersWithParents() should invoke dao and return its result'() {
		given:
			List<EntityWithParentDto> expectedResult = Random.listOfEntityWithParentDto()
		when:
			List<EntityWithParentDto> result = service.findSellersWithParents()
		then:
			1 * transactionParticipantDao.findSellersWithParents() >> expectedResult
		and:
			result == expectedResult
	}
	
	//
	// Tests for findSellerId()
	//
	
	def 'findSellerId() should throw exception when name is null, empty or blank'() {
		when:
			service.findSellerId(nullOrBlank(), Random.url())
		then:
			IllegalArgumentException ex = thrown()
			ex.message == 'Seller name must be non-blank'
	}
	
	def 'findSellerId() should throw exception when url is null, empty or blank'() {
		when:
			service.findSellerId(Random.sellerName(), nullOrBlank())
		then:
			IllegalArgumentException ex = thrown()
			ex.message == 'Seller url must be non-blank'
	}
	
	def 'findSellerId() should invoke dao and return its result'() {
		given:
			String expectedName = Random.sellerName()
			String expectedUrl = Random.url()
			Integer expectedResult = Random.id()
		when:
			Integer result = service.findSellerId(expectedName, expectedUrl)
		then:
			1 * transactionParticipantDao.findSellerId(expectedName, expectedUrl) >> expectedResult
		and:
			result == expectedResult
	}
	
	//
	// Tests for findAllGroups()
	//
	
	def 'findAllGroups() should invoke dao and return its result'() {
		given:
			List<EntityWithIdDto> expectedResult = Random.listOfEntityWithIdDto()
		when:
			List<EntityWithIdDto> result = service.findAllGroups()
		then:
			1 * transactionParticipantDao.findAllGroups() >> expectedResult
		and:
			result == expectedResult
	}
	
	//
	// Tests for findGroupIdByName()
	//
	
	def 'findGroupIdByName() should throw exception when name is null, empty or blank'() {
		when:
			service.findGroupIdByName(nullOrBlank())
		then:
			IllegalArgumentException ex = thrown()
			ex.message == 'Group name must be non-blank'
	}
	
	def 'findGroupIdByName() should invoke dao and return its result'() {
		given:
			String expectedName = Random.participantGroupName()
			Integer expectedResult = Random.id()
		when:
			Integer result = service.findGroupIdByName(expectedName)
		then:
			1 * transactionParticipantDao.findGroupIdByName(expectedName) >> expectedResult
		and:
			result == expectedResult
	}
	
}
