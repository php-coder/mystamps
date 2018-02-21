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

import static io.qala.datagen.RandomShortApi.bool
import static io.qala.datagen.RandomShortApi.nullOr

import org.slf4j.helpers.NOPLogger

import spock.lang.Specification

import ru.mystamps.web.dao.TransactionParticipantDao
import ru.mystamps.web.dao.dto.AddParticipantDbDto
import ru.mystamps.web.dao.dto.EntityWithIdDto
import ru.mystamps.web.dao.dto.TransactionParticipantDto
import ru.mystamps.web.controller.dto.AddParticipantForm
import ru.mystamps.web.test.Random

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
		and:
			AddParticipantForm form = new AddParticipantForm()
			form.setName(expectedName)
			form.setUrl(expectedUrl)
			form.setGroupId(expectedGroupId)
			form.setBuyer(expectedBuyer)
			form.setSeller(expectedSeller)
		when:
			service.add(form)
		then:
			1 * transactionParticipantDao.add({ AddParticipantDbDto participant ->
				assert participant?.name == expectedName
				assert participant?.url  == expectedUrl
				assert participant?.groupId  == expectedGroupId
				assert participant?.buyer == expectedBuyer
				assert participant?.seller == expectedSeller
				return true
			})
	}
	
	//
	// Tests for findBuyersWithParents()
	//
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def 'findBuyersWithParents() should invoke dao and return its result'() {
		given:
			List<TransactionParticipantDto> expectedResult = Random.listOfTransactionParticipantDto()
		when:
			List<TransactionParticipantDto> result = service.findBuyersWithParents()
		then:
			1 * transactionParticipantDao.findBuyersWithParents() >> expectedResult
		and:
			result == expectedResult
	}
	
	//
	// Tests for findSellersWithParents()
	//
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def 'findSellersWithParents() should invoke dao and return its result'() {
		given:
			List<TransactionParticipantDto> expectedResult = Random.listOfTransactionParticipantDto()
		when:
			List<TransactionParticipantDto> result = service.findSellersWithParents()
		then:
			1 * transactionParticipantDao.findSellersWithParents() >> expectedResult
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
	
}
