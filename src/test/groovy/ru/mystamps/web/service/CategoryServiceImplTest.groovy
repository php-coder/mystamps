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

import ru.mystamps.web.dao.CategoryDao
import ru.mystamps.web.dao.JdbcCategoryDao
import ru.mystamps.web.entity.Category
import ru.mystamps.web.entity.Collection
import ru.mystamps.web.entity.User
import ru.mystamps.web.model.AddCategoryForm
import ru.mystamps.web.service.dto.SelectEntityDto
import ru.mystamps.web.tests.DateUtils

class CategoryServiceImplTest extends Specification {
	
	private AddCategoryForm form
	private User user
	
	private CategoryDao categoryDao = Mock()
	private JdbcCategoryDao jdbcCategoryDao = Mock()
	private CategoryService service = new CategoryServiceImpl(categoryDao, jdbcCategoryDao)
	
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
			Category expected = TestObjects.createCategory()
			categoryDao.save(_ as Category) >> expected
		when:
			Category actual = service.add(form, user)
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
			1 * categoryDao.save({ Category category ->
				assert category?.name == expectedCategoryName
				return true
			}) >> TestObjects.createCategory()
	}
	
	def "add() should pass Russian category name to dao"() {
		given:
			String expectedCategoryName = 'Животные'
			form.setNameRu(expectedCategoryName)
		when:
			service.add(form, user)
		then:
			1 * categoryDao.save({ Category category ->
				assert category?.nameRu == expectedCategoryName
				return true
			}) >> TestObjects.createCategory()
	}
	
	def "add() should assign created at to current date"() {
		when:
			service.add(form, user)
		then:
			1 * categoryDao.save({ Category category ->
				assert DateUtils.roughlyEqual(category?.metaInfo?.createdAt, new Date())
				return true
			}) >> TestObjects.createCategory()
	}
	
	def "add() should assign updated at to current date"() {
		when:
			service.add(form, user)
		then:
			1 * categoryDao.save({ Category category ->
				assert DateUtils.roughlyEqual(category?.metaInfo?.updatedAt, new Date())
				return true
			}) >> TestObjects.createCategory()
	}
	
	def "add() should assign created by to user"() {
		when:
			service.add(form, user)
		then:
			1 * categoryDao.save({ Category category ->
				assert category?.metaInfo?.createdBy == user
				return true
			}) >> TestObjects.createCategory()
	}
	
	def "add() should assign updated by to user"() {
		when:
			service.add(form, user)
		then:
			1 * categoryDao.save({ Category category ->
				assert category?.metaInfo?.updatedBy == user
				return true
			}) >> TestObjects.createCategory()
	}
	
	//
	// Tests for findAll(String)
	//
	
	def "findAll(String) should call dao"() {
		given:
			SelectEntityDto category1 = new SelectEntityDto(1, 'First Category')
		and:
			SelectEntityDto category2 = new SelectEntityDto(2, 'Second Category')
		and:
			List<SelectEntityDto> expectedCategories = [ category1, category2 ]
		and:
			categoryDao.findAllAsSelectEntries(_ as String) >> expectedCategories
		when:
			Iterable<SelectEntityDto> resultCategories = service.findAll('fr')
		then:
			resultCategories == expectedCategories
	}
	
	@Unroll
	def "findAll(String) should pass language '#expectedLanguage' to dao"(String expectedLanguage, Object _) {
		when:
			service.findAll(expectedLanguage)
		then:
			1 * categoryDao.findAllAsSelectEntries({ String language ->
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
			1 * categoryDao.count() >> expectedResult
		and:
			result == expectedResult
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
			categoryDao.countByName(_ as String) >> 2
		when:
			int result = service.countByName('Any name here')
		then:
			result == 2
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
			categoryDao.countByNameRu(_ as String) >> 2
		when:
			int result = service.countByNameRu('Any name here')
		then:
			result == 2
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

	def "getStatisticsOf() should throw exception when collection is null"() {
		when:
			service.getStatisticsOf(null, 'whatever')
		then:
			thrown IllegalArgumentException
	}

	def "getStatisticsOf() should throw exception when collection id is null"() {
		given:
			Collection collection = Mock()
			collection.getId() >> null
		when:
			service.getStatisticsOf(collection, 'whatever')
		then:
			thrown IllegalArgumentException
	}

	def "getStatisticsOf() should pass arguments to dao"() {
		given:
			Integer expectedCollectionId = 15
		and:
			String expectedLang = 'expected'
		and:
			Collection expectedCollection = Mock()
			expectedCollection.getId() >> expectedCollectionId
		when:
			service.getStatisticsOf(expectedCollection, expectedLang)
		then:
			1 * jdbcCategoryDao.getStatisticsOf(
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
