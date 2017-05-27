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

import spock.lang.Specification

import ru.mystamps.web.dao.TransactionParticipantDao
import ru.mystamps.web.dao.dto.AddParticipantDbDto
import ru.mystamps.web.dao.dto.EntityWithIdDto
import ru.mystamps.web.model.AddParticipantForm

@SuppressWarnings(['ClassJavadoc', 'MethodName', 'NoDef', 'NoTabCharacter', 'TrailingWhitespace'])
class TransactionParticipantServiceImplTest extends Specification {
	
	private final TransactionParticipantDao transactionParticipantDao = Mock()
	private final TransactionParticipantService service =
		new TransactionParticipantServiceImpl(transactionParticipantDao)
	
	//
	// Tests for add()
	//
	
	def "add() should throw exception if dto is null"() {
		when:
			service.add(null)
		then:
			thrown IllegalArgumentException
	}
	
	def "add() should throw exception if name is null"() {
		given:
			AddParticipantForm form = new AddParticipantForm()
			form.setName(null)
		when:
			service.add(form)
		then:
			thrown IllegalArgumentException
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "add() should create participant"() {
		given:
			String expectedName = 'test'
			String expectedUrl  = 'http://example.org'
		and:
			AddParticipantForm form = new AddParticipantForm()
			form.setName(expectedName)
			form.setUrl(expectedUrl)
		when:
			service.add(form)
		then:
			1 * transactionParticipantDao.add({ AddParticipantDbDto participant ->
				assert participant?.name == expectedName
				assert participant?.url  == expectedUrl
				return true
			})
	}
	
	//
	// Tests for findAllBuyers()
	//
	
	def "findAllBuyers() should call dao and return result"() {
		given:
			List<EntityWithIdDto> expectedResult = [ TestObjects.createEntityWithIdDto() ]
		when:
			List<EntityWithIdDto> result = service.findAllBuyers()
		then:
			1 * transactionParticipantDao.findAllAsEntityWithIdDto() >> expectedResult
		and:
			result == expectedResult
	}
	
	//
	// Tests for findAllSellers()
	//
	
	def "findAllSellers() should call dao and return result"() {
		given:
			List<EntityWithIdDto> expectedResult = [ TestObjects.createEntityWithIdDto() ]
		when:
			List<EntityWithIdDto> result = service.findAllSellers()
		then:
			1 * transactionParticipantDao.findAllAsEntityWithIdDto() >> expectedResult
		and:
			result == expectedResult
	}
	
}
