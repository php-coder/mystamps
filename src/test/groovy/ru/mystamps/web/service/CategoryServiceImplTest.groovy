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

import org.slf4j.helpers.NOPLogger

import ru.mystamps.web.dao.CategoryDao
import ru.mystamps.web.dao.dto.AddCategoryDbDto
import ru.mystamps.web.controller.dto.AddCategoryForm
import ru.mystamps.web.dao.dto.LinkEntityDto
import ru.mystamps.web.tests.DateUtils
import ru.mystamps.web.tests.Random
import ru.mystamps.web.util.SlugUtils

@SuppressWarnings(['ClassJavadoc', 'MethodName', 'NoDef', 'NoTabCharacter', 'TrailingWhitespace'])
class CategoryServiceImplTest extends Specification {
	
	private final CategoryDao categoryDao = Mock()
	private final CategoryService service = new CategoryServiceImpl(NOPLogger.NOP_LOGGER, categoryDao)
	
	private AddCategoryForm form
	
	def setup() {
		form = new AddCategoryForm()
		form.setName('Any category name')
		form.setNameRu('Любое название категории')
	}
	
	//
	// Tests for add()
	//
	
	def "add() should throw exception when dto is null"() {
		when:
			service.add(null, Random.userId())
		then:
			thrown IllegalArgumentException
	}
	
	def "add() should throw exception when English category name is null"() {
		given:
			form.setName(null)
		when:
			service.add(form, Random.userId())
		then:
			thrown IllegalArgumentException
	}
	
	def "add() should throw exception when user is null"() {
		when:
			service.add(form, null)
		then:
			thrown IllegalArgumentException
	}
	
	def "add() should call dao"() {
		given:
			Integer expectedId = 10
		and:
			form.setName('Example Category')
		and:
			String expectedSlug = 'example-category'
		and:
			categoryDao.add(_ as AddCategoryDbDto) >> expectedId
		when:
			String actualSlug = service.add(form, Random.userId())
		then:
			actualSlug == expectedSlug
	}
	
	def "add() should throw exception when name can't be converted to slug"() {
		given:
			form.setName(null)
		when:
			service.add(form, Random.userId())
		then:
			thrown IllegalArgumentException
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "add() should pass slug to dao"() {
		given:
			String name = '-foo123 test_'
		and:
			String slug = SlugUtils.slugify(name)
		and:
			form.setName(name)
		when:
			service.add(form, Random.userId())
		then:
			1 * categoryDao.add({ AddCategoryDbDto category ->
				assert category?.slug == slug
				return true
			}) >> 40
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "add() should pass values to dao"() {
		given:
			Integer expectedUserId = 10
			String expectedEnglishName = 'Animals'
			String expectedRussianName = 'Животные'
		and:
			form.setName(expectedEnglishName)
			form.setNameRu(expectedRussianName)
		when:
			service.add(form, expectedUserId)
		then:
			1 * categoryDao.add({ AddCategoryDbDto category ->
				assert category?.name == expectedEnglishName
				assert category?.nameRu == expectedRussianName
				assert category?.createdBy == expectedUserId
				assert category?.updatedBy == expectedUserId
				assert DateUtils.roughlyEqual(category?.createdAt, new Date())
				assert DateUtils.roughlyEqual(category?.updatedAt, new Date())
				return true
			}) >> 70
	}
	
	//
	// Tests for findAllAsLinkEntities(String)
	//
	
	def "findAllAsLinkEntities(String) should call dao"() {
		given:
			LinkEntityDto category1 = new LinkEntityDto(1, 'first-category', 'First Category')
		and:
			LinkEntityDto category2 = new LinkEntityDto(2, 'second-category', 'Second Category')
		and:
			List<LinkEntityDto> expectedCategories = [ category1, category2 ]
		and:
			categoryDao.findAllAsLinkEntities(_ as String) >> expectedCategories
		when:
			List<LinkEntityDto> resultCategories = service.findAllAsLinkEntities('fr')
		then:
			resultCategories == expectedCategories
	}
	
	@Unroll
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "findAllAsLinkEntities(String) should pass language '#expectedLanguage' to dao"(String expectedLanguage) {
		when:
			service.findAllAsLinkEntities(expectedLanguage)
		then:
			1 * categoryDao.findAllAsLinkEntities({ String language ->
				assert language == expectedLanguage
				return true
			})
		where:
			expectedLanguage | _
			'ru'             | _
			null             | _
	}
	
	//
	// Tests for findOneAsLinkEntity()
	//
	
	@Unroll
	def "findOneAsLinkEntity() should throw exception when category slug is '#slug'"(String slug) {
		when:
			service.findOneAsLinkEntity(slug, 'ru')
		then:
			thrown IllegalArgumentException
		where:
			slug | _
			' '  | _
			''   | _
			null | _
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "findOneAsLinkEntity() should pass arguments to dao"() {
		given:
			String expectedSlug = 'people'
		and:
			String expectedLang = 'fr'
		and:
			LinkEntityDto expectedDto = TestObjects.createLinkEntityDto()
		when:
			LinkEntityDto actualDto = service.findOneAsLinkEntity(expectedSlug, expectedLang)
		then:
			1 * categoryDao.findOneAsLinkEntity(
				{ String slug ->
					assert expectedSlug == slug
					return true
				},
				{ String lang ->
					assert expectedLang == lang
					return true
				}
			) >> expectedDto
		and:
			actualDto == expectedDto
	}
	
	//
	// Tests for countAll()
	//
	
	def "countAll() should call dao and returns result"() {
		given:
			long expectedResult = 10
		when:
			long result = service.countAll()
		then:
			1 * categoryDao.countAll() >> expectedResult
		and:
			result == expectedResult
	}
	
	//
	// Tests for countCategoriesOf()
	//
	
	def "countCategoriesOf() should throw exception when collection id is null"() {
		when:
			service.countCategoriesOf(null)
		then:
			thrown IllegalArgumentException
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "countCategoriesOf() should pass arguments to dao"() {
		given:
			Integer expectedCollectionId = 10
		when:
			service.countCategoriesOf(expectedCollectionId)
		then:
			1 * categoryDao.countCategoriesOfCollection({ Integer collectionId ->
				assert expectedCollectionId == collectionId
				return true
			}) >> 0L
	}
	
	//
	// Tests for countBySlug()
	//
	
	def "countBySlug() should throw exception when slug is null"() {
		when:
			service.countBySlug(null)
		then:
			thrown IllegalArgumentException
	}
	
	def "countBySlug() should call dao"() {
		given:
			categoryDao.countBySlug(_ as String) >> 3L
		when:
			long result = service.countBySlug('any-slug')
		then:
			result == 3L
	}
	
	//
	// Tests for countByName()
	//
	
	def "countByName() should throw exception when name is null"() {
		when:
			service.countByName(null)
		then:
			thrown IllegalArgumentException
	}
	
	def "countByName() should call dao"() {
		given:
			categoryDao.countByName(_ as String) >> 2L
		when:
			long result = service.countByName('Any name here')
		then:
			result == 2L
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "countByName() should pass category name to dao in lowercase"() {
		when:
			service.countByName('Sport')
		then:
			1 * categoryDao.countByName({ String name ->
				assert name == 'sport'
				return true
			})
	}
	
	//
	// Tests for countByNameRu()
	//
	
	def "countByNameRu() should throw exception when name is null"() {
		when:
			service.countByNameRu(null)
		then:
			thrown IllegalArgumentException
	}
	
	def "countByNameRu() should call dao"() {
		given:
			categoryDao.countByNameRu(_ as String) >> 2L
		when:
			long result = service.countByNameRu('Any name here')
		then:
			result == 2L
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "countByNameRu() should pass category name to dao in lowercase"() {
		when:
			service.countByNameRu('Спорт')
		then:
			1 * categoryDao.countByNameRu({ String name ->
				assert name == 'спорт'
				return true
			})
	}
	
	//
	// Tests for countAddedSince()
	//
	
	def "countAddedSince() should throw exception when date is null"() {
		when:
			service.countAddedSince(null)
		then:
			thrown IllegalArgumentException
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "countAddedSince() should invoke dao, pass argument and return result from dao"() {
		given:
			Date expectedDate = new Date()
		and:
			long expectedResult = 33
		when:
			long result = service.countAddedSince(expectedDate)
		then:
			1 * categoryDao.countAddedSince({ Date date ->
				assert date == expectedDate
				return true
			}) >> expectedResult
		and:
			result == expectedResult
	}
	
	//
	// Tests for countUntranslatedNamesSince()
	//
	
	def "countUntranslatedNamesSince() should throw exception when date is null"() {
		when:
			service.countUntranslatedNamesSince(null)
		then:
			thrown IllegalArgumentException
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "countUntranslatedNamesSince() should invoke dao, pass argument and return result from dao"() {
		given:
			Date expectedDate = new Date()
		and:
			long expectedResult = 17
		when:
			long result = service.countUntranslatedNamesSince(expectedDate)
		then:
			1 * categoryDao.countUntranslatedNamesSince({ Date date ->
				assert date == expectedDate
				return true
			}) >> expectedResult
		and:
			result == expectedResult
	}
	
	//
	// Tests for getStatisticsOf()
	//

	def "getStatisticsOf() should throw exception when collection id is null"() {
		when:
			service.getStatisticsOf(null, 'whatever')
		then:
			thrown IllegalArgumentException
	}

	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "getStatisticsOf() should pass arguments to dao"() {
		given:
			Integer expectedCollectionId = 15
		and:
			String expectedLang = 'expected'
		when:
			service.getStatisticsOf(expectedCollectionId, expectedLang)
		then:
			1 * categoryDao.getStatisticsOf(
				{ Integer collectionId ->
					assert expectedCollectionId == collectionId
					return true
				},
				{ String lang ->
					assert expectedLang == lang
					return true
				}
			) >> null
	}

}
