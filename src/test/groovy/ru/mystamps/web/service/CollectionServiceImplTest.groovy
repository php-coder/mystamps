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
import spock.lang.Unroll

import ru.mystamps.web.dao.CollectionDao
import ru.mystamps.web.dao.dto.AddCollectionDbDto
import ru.mystamps.web.dao.dto.CollectionInfoDto
import ru.mystamps.web.tests.DateUtils
import ru.mystamps.web.util.SlugUtils

@SuppressWarnings(['ClassJavadoc', 'MethodName', 'NoDef', 'NoTabCharacter', 'TrailingWhitespace'])
class CollectionServiceImplTest extends Specification {
	
	private final CollectionDao collectionDao = Mock()
	
	private CollectionService service
	
	def setup() {
		service = new CollectionServiceImpl(collectionDao)
	}
	
	//
	// Tests for createCollection()
	//
	
	@SuppressWarnings('FactoryMethodName')
	def "createCollection() should throw exception when owner id is null"() {
		when:
			service.createCollection(null, 'test-owner-login')
		then:
			thrown IllegalArgumentException
	}
	
	@SuppressWarnings('FactoryMethodName')
	def "createCollection() should throw exception when owner login is null"() {
		when:
			service.createCollection(123, null)
		then:
			thrown IllegalArgumentException
	}
	
	@SuppressWarnings('FactoryMethodName')
	def "createCollection() should throw exception when owner login can't be converted to slug"() {
		when:
			service.createCollection(123, '')
		then:
			thrown IllegalArgumentException
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'FactoryMethodName', 'UnnecessaryReturnKeyword'])
	def "createCollection() should pass owner id to dao"() {
		given:
			Integer expectedOwnerId = 123
		when:
			service.createCollection(expectedOwnerId, 'test')
		then:
			1 * collectionDao.add({ AddCollectionDbDto collection ->
				assert collection?.ownerId == expectedOwnerId
				return true
			}) >> 100
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'FactoryMethodName', 'UnnecessaryReturnKeyword'])
	def "createCollection() should pass slugified owner login to dao"() {
		given:
			String ownerLogin = 'Test User'
		and:
			String expectedSlug = SlugUtils.slugify(ownerLogin)
		when:
			service.createCollection(123, ownerLogin)
		then:
			1 * collectionDao.add({ AddCollectionDbDto collection ->
				assert collection?.slug == expectedSlug
				return true
			}) >> 200
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'FactoryMethodName', 'UnnecessaryReturnKeyword'])
	def "createCollection() should assign updated at to current date"() {
		when:
			service.createCollection(123, 'any-login')
		then:
			1 * collectionDao.add({ AddCollectionDbDto collection ->
				assert DateUtils.roughlyEqual(collection?.updatedAt, new Date())
				return true
			}) >> 300
	}
	
	//
	// Tests for addToCollection()
	//
	
	def "addToCollection() should throw exception when user id is null"() {
		when:
			service.addToCollection(null, 456)
		then:
			thrown IllegalArgumentException
	}
	
	def "addToCollection() should throw exception when series id is null"() {
		when:
			service.addToCollection(123, null)
		then:
			thrown IllegalArgumentException
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "addToCollection() should add series to collection and mark it as modified"() {
		given:
			Integer expectedUserId = 123
			Integer expectedSeriesId = 456
		when:
			service.addToCollection(expectedUserId, expectedSeriesId)
		then:
			1 * collectionDao.addSeriesToUserCollection({ Integer userId ->
				assert userId == expectedUserId
				return true
			}, { Integer seriesId ->
				assert seriesId == expectedSeriesId
				return true
			})
		and:
			1 * collectionDao.markAsModified({ Integer userId ->
				assert userId == expectedUserId
				return true
			}, { Date updatedAt ->
				assert DateUtils.roughlyEqual(updatedAt, new Date())
				return true
			})
	}
	
	//
	// Tests for removeFromCollection()
	//
	
	def "removeFromCollection() should throw exception when user id is null"() {
		when:
			service.removeFromCollection(null, 123)
		then:
			thrown IllegalArgumentException
	}
	
	def "removeFromCollection() should throw exception when series id is null"() {
		when:
			service.removeFromCollection(456, null)
		then:
			thrown IllegalArgumentException
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "removeFromCollection() should remove series from collection and mark it as modified"() {
		given:
			Integer expectedUserId = 123
			Integer expectedSeriesId = 456
		when:
			service.removeFromCollection(expectedUserId, expectedSeriesId)
		then:
			1 * collectionDao.removeSeriesFromUserCollection({ Integer userId ->
				assert userId == expectedUserId
				return true
			}, { Integer seriesId ->
				assert seriesId == expectedSeriesId
				return true
			})
		and:
			1 * collectionDao.markAsModified({ Integer userId ->
				assert userId == expectedUserId
				return true
			}, { Date updatedAt ->
				assert DateUtils.roughlyEqual(updatedAt, new Date())
				return true
			})
	}
	
	//
	// Tests for isSeriesInCollection()
	//
	
	def "isSeriesInCollection() should throw exception when series id is null"() {
		when:
			service.isSeriesInCollection(100, null)
		then:
			thrown IllegalArgumentException
	}
	
	def "isSeriesInCollection() should return false for anonymous"() {
		given:
			Integer anonymousUserId = null
		when:
			boolean serviceResult = service.isSeriesInCollection(anonymousUserId, 456)
		then:
			serviceResult == false
		and:
			0 * collectionDao.isSeriesInUserCollection(_ as Integer, _ as Integer)
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "isSeriesInCollection() should pass arguments to dao"() {
		given:
			Integer expectedUserId = 123
		and:
			Integer expectedSeriesId = 456
		and:
			boolean expectedResult = true
		when:
			boolean serviceResult = service.isSeriesInCollection(expectedUserId, expectedSeriesId)
		then:
			1 * collectionDao.isSeriesInUserCollection({ Integer userId ->
				assert userId == expectedUserId
				return true
			}, { Integer seriesId ->
				assert seriesId == expectedSeriesId
				return true
			}) >> expectedResult
		and:
			serviceResult == expectedResult
	}
	
	//
	// Tests for countCollectionsOfUsers()
	//
	
	def "countCollectionsOfUsers() should call dao and return result"() {
		given:
			long expectedResult = Long.MAX_VALUE
		when:
			long serviceResult = service.countCollectionsOfUsers()
		then:
			1 * collectionDao.countCollectionsOfUsers() >> expectedResult
		and:
			serviceResult == expectedResult
	}
	
	//
	// Tests for countUpdatedSince()
	//
	
	def "countUpdatedSince() should throw exception when date is null"() {
		when:
			service.countUpdatedSince(null)
		then:
			thrown IllegalArgumentException
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "countUpdatedSince() should invoke dao, pass argument and return result from dao"() {
		given:
			Date expectedDate   = new Date()
			long expectedResult = 47
		when:
			long result = service.countUpdatedSince(expectedDate)
		then:
			1 * collectionDao.countUpdatedSince({ Date date ->
				assert date == expectedDate
				return true
			}) >> expectedResult
		and:
			result == expectedResult
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
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
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
	
	//
	// Tests for findBySlug()
	//
	
	def "findBySlug() should throw exception when collection slug is null"() {
		when:
			service.findBySlug(null)
		then:
			thrown IllegalArgumentException
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "findBySlug() should invoke dao, pass argument and return result from dao"() {
		given:
			String expectedSlug = 'cuba'
		and:
			CollectionInfoDto expectedResult = TestObjects.createCollectionInfoDto()
		when:
			CollectionInfoDto result = service.findBySlug(expectedSlug)
		then:
			1 * collectionDao.findCollectionInfoBySlug({ String slug ->
				assert slug == expectedSlug
				return true
			}) >> expectedResult
		and:
			result == expectedResult
	}
	
}
