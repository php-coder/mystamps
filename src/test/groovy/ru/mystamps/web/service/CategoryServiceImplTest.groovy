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

import ru.mystamps.web.dao.JdbcCategoryDao
import ru.mystamps.web.dao.dto.AddCategoryDbDto
import ru.mystamps.web.entity.User
import ru.mystamps.web.model.AddCategoryForm
import ru.mystamps.web.service.dto.LinkEntityDto
import ru.mystamps.web.service.dto.SelectEntityDto
import ru.mystamps.web.service.dto.UrlEntityDto
import ru.mystamps.web.tests.DateUtils
import ru.mystamps.web.util.SlugUtils

class CategoryServiceImplTest extends Specification {
	
	private AddCategoryForm form
	private User user
	
	private JdbcCategoryDao categoryDao = Mock()
	private CategoryService service = new CategoryServiceImpl(categoryDao)
	
	def setup() {
		form = new AddCategoryForm()
		form.setName('Any category name')
		form.setNameRu('Любое название категории')
		
		user = TestObjects.createUser()
	}
	
	//
	// Tests for add()
	//
	
	def "add() should throw exception when dto is null"() {
		when:
			service.add(null, user)
		then:
			thrown IllegalArgumentException
	}
	
	def "add() should throw exception when English category name is null"() {
		given:
			form.setName(null)
		when:
			service.add(form, user)
		then:
			thrown IllegalArgumentException
	}
	
	def "add() should throw exception when Russian category name is null"() {
		given:
			form.setNameRu(null)
		when:
			service.add(form, user)
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
		and:
			UrlEntityDto expected = new UrlEntityDto(expectedId, expectedSlug)
		when:
			UrlEntityDto actual = service.add(form, user)
		then:
			actual == expected
	}
	
	def "add() should pass English category name to dao"() {
		given:
			String expectedCategoryName = 'Animals'
			form.setName(expectedCategoryName)
		when:
			service.add(form, user)
		then:
			1 * categoryDao.add({ AddCategoryDbDto category ->
				assert category?.name == expectedCategoryName
				return true
			}) >> 20
	}
	
	def "add() should pass Russian category name to dao"() {
		given:
			String expectedCategoryName = 'Животные'
			form.setNameRu(expectedCategoryName)
		when:
			service.add(form, user)
		then:
			1 * categoryDao.add({ AddCategoryDbDto category ->
				assert category?.nameRu == expectedCategoryName
				return true
			}) >> 30
	}
	
	def "add() should throw exception when name can't be converted to slug"() {
		given:
			form.setName(null)
		when:
			service.add(form, user)
		then:
			thrown IllegalArgumentException
	}
	
	def "add() should pass slug to dao"() {
		given:
			String name = "-foo123 test_"
		and:
			String slug = SlugUtils.slugify(name)
		and:
			form.setName(name)
		when:
			service.add(form, user)
		then:
			1 * categoryDao.add({ AddCategoryDbDto category ->
				assert category?.slug == slug
				return true
			}) >> 40
	}
	
	def "add() should assign created at to current date"() {
		when:
			service.add(form, user)
		then:
			1 * categoryDao.add({ AddCategoryDbDto category ->
				assert DateUtils.roughlyEqual(category?.createdAt, new Date())
				return true
			}) >> 50
	}
	
	def "add() should assign updated at to current date"() {
		when:
			service.add(form, user)
		then:
			1 * categoryDao.add({ AddCategoryDbDto category ->
				assert DateUtils.roughlyEqual(category?.updatedAt, new Date())
				return true
			}) >> 60
	}
	
	def "add() should assign created by to user"() {
		when:
			service.add(form, user)
		then:
			1 * categoryDao.add({ AddCategoryDbDto category ->
				assert category?.createdBy == user.id
				return true
			}) >> 70
	}
	
	def "add() should assign updated by to user"() {
		when:
			service.add(form, user)
		then:
			1 * categoryDao.add({ AddCategoryDbDto category ->
				assert category?.updatedBy == user.id
				return true
			}) >> 80
	}
	
	//
	// Tests for findAllAsSelectEntities(String)
	//
	
	def "findAllAsSelectEntities(String) should call dao"() {
		given:
			SelectEntityDto category1 = new SelectEntityDto(1, 'First Category')
		and:
			SelectEntityDto category2 = new SelectEntityDto(2, 'Second Category')
		and:
			List<SelectEntityDto> expectedCategories = [ category1, category2 ]
		and:
			categoryDao.findAllAsSelectEntities(_ as String) >> expectedCategories
		when:
			Iterable<SelectEntityDto> resultCategories = service.findAllAsSelectEntities('fr')
		then:
			resultCategories == expectedCategories
	}
	
	@Unroll
	def "findAllAsSelectEntities(String) should pass language '#expectedLanguage' to dao"(String expectedLanguage) {
		when:
			service.findAllAsSelectEntities(expectedLanguage)
		then:
			1 * categoryDao.findAllAsSelectEntities({ String language ->
				assert language == expectedLanguage
				return true
			})
		where:
			expectedLanguage | _
			'ru'             | _
			null             | _
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
			Iterable<LinkEntityDto> resultCategories = service.findAllAsLinkEntities('fr')
		then:
			resultCategories == expectedCategories
	}
	
	@Unroll
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
	
	def "countByName() should pass category name to dao"() {
		when:
			service.countByName('Sport')
		then:
			1 * categoryDao.countByName({ String name ->
				assert name == 'Sport'
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
	
	def "countByNameRu() should pass category name to dao"() {
		when:
			service.countByNameRu('Спорт')
		then:
			1 * categoryDao.countByNameRu({ String name ->
				assert name == 'Спорт'
				return true
			})
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
