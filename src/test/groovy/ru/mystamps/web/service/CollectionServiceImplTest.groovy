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
import spock.lang.Unroll

import ru.mystamps.web.dao.JdbcCollectionDao

class CollectionServiceImplTest extends Specification {
	private JdbcCollectionDao collectionDao = Mock()
	
	private CollectionService service
	
	def setup() {
		service = new CollectionServiceImpl(collectionDao)
	}
	
	//
	// Tests for findRecentlyCreated()
	//
	
	@Unroll
	def "findRecentlyCreated() should throw exception when quantity is #quantity"(Integer quantity) {
		when:
			service.findRecentlyCreated(quantity)
		then:
			thrown IllegalArgumentException
		where:
			quantity | _
			-1       | _
			0        | _
	}
	
	def "findRecentlyCreated() should pass arguments to dao"() {
		given:
			int expectedQuantity = 4
		when:
			service.findRecentlyCreated(expectedQuantity)
		then:
			1 * collectionDao.findLastCreated({ int quantity ->
				assert expectedQuantity == quantity
				return true
			}) >> []
	}
	
}
