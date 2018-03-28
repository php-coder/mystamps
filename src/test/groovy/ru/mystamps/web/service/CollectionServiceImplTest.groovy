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
import static io.qala.datagen.RandomShortApi.positiveLong

import spock.lang.Specification
import spock.lang.Unroll

import org.slf4j.helpers.NOPLogger

import ru.mystamps.web.dao.CollectionDao
import ru.mystamps.web.dao.dto.AddCollectionDbDto
import ru.mystamps.web.dao.dto.CollectionInfoDto
import ru.mystamps.web.tests.DateUtils
import ru.mystamps.web.tests.Random
import ru.mystamps.web.util.SlugUtils

@SuppressWarnings(['ClassJavadoc', 'MethodName', 'NoDef', 'NoTabCharacter', 'TrailingWhitespace'])
class CollectionServiceImplTest extends Specification {
	
	private final CollectionDao collectionDao = Mock()
	
	private CollectionService service
	
	def setup() {
		service = new CollectionServiceImpl(NOPLogger.NOP_LOGGER, collectionDao)
	}
	
	//
	// Tests for createCollection()
	//
	
	@SuppressWarnings('FactoryMethodName')
	def 'createCollection() should throw exception when owner id is null'() {
		when:
			service.createCollection(null, 'test-owner-login')
		then:
			thrown IllegalArgumentException
	}
	
	@SuppressWarnings('FactoryMethodName')
	def 'createCollection() should throw exception when owner login is null'() {
		when:
			service.createCollection(Random.userId(), null)
		then:
			thrown IllegalArgumentException
	}
	
	@SuppressWarnings('FactoryMethodName')
	def 'createCollection() should throw exception when owner login can\'t be converted to slug'() {
		when:
			service.createCollection(Random.userId(), '')
		then:
			thrown IllegalArgumentException
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'FactoryMethodName', 'UnnecessaryReturnKeyword'])
	def 'createCollection() should pass owner id to dao'() {
		given:
			Integer expectedOwnerId = Random.userId()
		when:
			service.createCollection(expectedOwnerId, 'test')
		then:
			1 * collectionDao.add({ AddCollectionDbDto collection ->
				assert collection?.ownerId == expectedOwnerId
				return true
			}) >> Random.id()
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'FactoryMethodName', 'UnnecessaryReturnKeyword'])
	def 'createCollection() should pass slugified owner login to dao'() {
		given:
			String ownerLogin = 'Test User'
		and:
			String expectedSlug = SlugUtils.slugify(ownerLogin)
		when:
			service.createCollection(Random.userId(), ownerLogin)
		then:
			1 * collectionDao.add({ AddCollectionDbDto collection ->
				assert collection?.slug == expectedSlug
				return true
			}) >> Random.id()
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'FactoryMethodName', 'UnnecessaryReturnKeyword'])
	def 'createCollection() should assign updated at to current date'() {
		when:
			service.createCollection(Random.userId(), 'any-login')
		then:
			1 * collectionDao.add({ AddCollectionDbDto collection ->
				assert DateUtils.roughlyEqual(collection?.updatedAt, new Date())
				return true
			}) >> Random.id()
	}
	
	//
	// Tests for addToCollection()
	//
	
	def 'addToCollection() should throw exception when user id is null'() {
		when:
			service.addToCollection(null, Random.id())
		then:
			thrown IllegalArgumentException
	}
	
	def 'addToCollection() should throw exception when series id is null'() {
		when:
			service.addToCollection(Random.userId(), null)
		then:
			thrown IllegalArgumentException
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def 'addToCollection() should add series to collection and mark it as modified'() {
		given:
			Integer expectedUserId = Random.userId()
			Integer expectedSeriesId = Random.id()
		when:
			service.addToCollection(expectedUserId, expectedSeriesId)
		then:
			1 * collectionDao.addSeriesToUserCollection(expectedUserId, expectedSeriesId)
		and:
			1 * collectionDao.markAsModified(
				expectedUserId,
				{ Date updatedAt ->
					assert DateUtils.roughlyEqual(updatedAt, new Date())
					return true
				}
			)
	}
	
	//
	// Tests for removeFromCollection()
	//
	
	def 'removeFromCollection() should throw exception when user id is null'() {
		when:
			service.removeFromCollection(null, Random.id())
		then:
			thrown IllegalArgumentException
	}
	
	def 'removeFromCollection() should throw exception when series id is null'() {
		when:
			service.removeFromCollection(Random.userId(), null)
		then:
			thrown IllegalArgumentException
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def 'removeFromCollection() should remove series from collection and mark it as modified'() {
		given:
			Integer expectedUserId = Random.userId()
			Integer expectedSeriesId = Random.id()
		when:
			service.removeFromCollection(expectedUserId, expectedSeriesId)
		then:
			1 * collectionDao.removeSeriesFromUserCollection(expectedUserId, expectedSeriesId)
		and:
			1 * collectionDao.markAsModified(
				expectedUserId,
				{ Date updatedAt ->
					assert DateUtils.roughlyEqual(updatedAt, new Date())
					return true
				}
			)
	}
	
	//
	// Tests for isSeriesInCollection()
	//
	
	def 'isSeriesInCollection() should throw exception when series id is null'() {
		when:
			service.isSeriesInCollection(Random.userId(), null)
		then:
			thrown IllegalArgumentException
	}
	
	def 'isSeriesInCollection() should return false for anonymous'() {
		given:
			Integer anonymousUserId = null
		when:
			boolean serviceResult = service.isSeriesInCollection(anonymousUserId, Random.id())
		then:
			serviceResult == false
		and:
			0 * collectionDao.isSeriesInUserCollection(_ as Integer, _ as Integer)
	}
	
	def 'isSeriesInCollection() should pass arguments to dao'() {
		given:
			Integer expectedUserId = Random.userId()
		and:
			Integer expectedSeriesId = Random.id()
		and:
			boolean expectedResult = bool()
		when:
			boolean serviceResult = service.isSeriesInCollection(expectedUserId, expectedSeriesId)
		then:
			1 * collectionDao.isSeriesInUserCollection(expectedUserId, expectedSeriesId) >> expectedResult
		and:
			serviceResult == expectedResult
	}
	
	//
	// Tests for countCollectionsOfUsers()
	//
	
	def 'countCollectionsOfUsers() should call dao and return result'() {
		given:
			long expectedResult = positiveLong()
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
	
	def 'countUpdatedSince() should throw exception when date is null'() {
		when:
			service.countUpdatedSince(null)
		then:
			thrown IllegalArgumentException
	}
	
	def 'countUpdatedSince() should invoke dao, pass argument and return result from dao'() {
		given:
			Date expectedDate   = Random.date()
			long expectedResult = positiveLong()
		when:
			long result = service.countUpdatedSince(expectedDate)
		then:
			1 * collectionDao.countUpdatedSince(expectedDate) >> expectedResult
		and:
			result == expectedResult
	}
	
	//
	// Tests for findRecentlyCreated()
	//
	
	@Unroll
	def 'findRecentlyCreated() should throw exception when quantity is #quantity'(Integer quantity) {
		when:
			service.findRecentlyCreated(quantity)
		then:
			thrown IllegalArgumentException
		where:
			quantity | _
			-1       | _
			0        | _
	}
	
	def 'findRecentlyCreated() should pass arguments to dao'() {
		given:
			int expectedQuantity = Random.quantity()
		when:
			service.findRecentlyCreated(expectedQuantity)
		then:
			1 * collectionDao.findLastCreated(expectedQuantity) >> []
	}
	
	//
	// Tests for findBySlug()
	//
	
	def 'findBySlug() should throw exception when collection slug is null'() {
		when:
			service.findBySlug(null)
		then:
			thrown IllegalArgumentException
	}
	
	def 'findBySlug() should invoke dao, pass argument and return result from dao'() {
		given:
			String expectedSlug = 'cuba'
		and:
			CollectionInfoDto expectedResult = TestObjects.createCollectionInfoDto()
		when:
			CollectionInfoDto result = service.findBySlug(expectedSlug)
		then:
			1 * collectionDao.findCollectionInfoBySlug(expectedSlug) >> expectedResult
		and:
			result == expectedResult
	}
	
}
