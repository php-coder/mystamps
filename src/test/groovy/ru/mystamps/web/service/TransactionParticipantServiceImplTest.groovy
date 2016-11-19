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

import ru.mystamps.web.dao.TransactionParticipantDao
import ru.mystamps.web.dao.dto.EntityWithIdDto

@SuppressWarnings(['ClassJavadoc', 'MethodName', 'NoDef', 'NoTabCharacter', 'TrailingWhitespace'])
class TransactionParticipantServiceImplTest extends Specification {
	
	private final TransactionParticipantDao transactionParticipantDao = Mock()
	private final TransactionParticipantService service =
		new TransactionParticipantServiceImpl(transactionParticipantDao)
	
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
