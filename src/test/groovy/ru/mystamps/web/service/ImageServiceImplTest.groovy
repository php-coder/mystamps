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

import org.springframework.web.multipart.MultipartFile

import spock.lang.Specification
import spock.lang.Unroll

import ru.mystamps.web.dao.JdbcImageDao
import ru.mystamps.web.service.dto.ImageDto
import ru.mystamps.web.service.dto.ImageInfoDto
import ru.mystamps.web.service.exception.ImagePersistenceException

class ImageServiceImplTest extends Specification {

	private JdbcImageDao jdbcImageDao = Mock()
	private MultipartFile multipartFile = Mock()
	private ImagePersistenceStrategy imagePersistenceStrategy = Mock()
	
	private ImageService service = new ImageServiceImpl(
		imagePersistenceStrategy,
		jdbcImageDao
	)
	
	def setup() {
		multipartFile.getSize() >> 1024L
		multipartFile.getContentType() >> 'image/png'
		jdbcImageDao.add(_ as String) >> 17
	}
	
	//
	// Tests for save()
	//
	
	def "save() should throw exception if file is null"() {
		when:
			service.save(null)
		then:
			thrown IllegalArgumentException
	}
	
	def "save() should throw exception if file has zero size"() {
		when:
			service.save(multipartFile)
		then:
			multipartFile.getSize() >> 0L
		and:
			thrown IllegalArgumentException
	}
	
	def "save() should throw exception if content type is null"() {
		when:
			service.save(multipartFile)
		then:
			multipartFile.getContentType() >> null
		and:
			thrown IllegalArgumentException
	}
	
	def "save() should throw exception for unsupported content type"() {
		when:
			service.save(multipartFile)
		then:
			multipartFile.getContentType() >> 'image/tiff'
		and:
			thrown IllegalStateException
	}
	
	def "save() should pass image to image dao"() {
		when:
			service.save(multipartFile)
		then:
			1 * jdbcImageDao.add(_ as String) >> 18
	}
	
	@Unroll
	def "save() should pass content type '#contentType' to image dao"(String contentType, String expectedType) {
		when:
			service.save(multipartFile)
		then:
			multipartFile.getContentType() >> contentType
		and:
			1 * jdbcImageDao.add({ String type ->
				assert type == expectedType
				return true
			}) >> 19
		where:
			contentType                 || expectedType
			'image/jpeg'                || 'JPEG'
			'image/jpeg; charset=UTF-8' || 'JPEG'
			'image/png'                 || 'PNG'
			'image/png; charset=UTF8'   || 'PNG'
	}
	
	def "save() should throw exception when image dao returned null"() {
		when:
			service.save(multipartFile)
		then:
			jdbcImageDao.add(_ as String) >> null
		and:
			0 * imagePersistenceStrategy.save(_ as MultipartFile, _ as ImageInfoDto)
		and:
			thrown ImagePersistenceException
	}
	
	def "save() should call strategy"() {
		given:
			ImageInfoDto image = TestObjects.createImageInfoDto()
		when:
			String url = service.save(multipartFile)
		then:
			jdbcImageDao.add(_ as String) >> image.id
		and:
			1 * imagePersistenceStrategy.save({ MultipartFile passedFile ->
				assert passedFile == multipartFile
				return true
			}, { ImageInfoDto passedImage ->
				assert passedImage?.id == image.id
				assert passedImage?.type == image.type.toString()
				return true
			})
	}
	
	def "save() should return saved image"() {
		given:
			Integer expectedImageId = 20
		when:
			Integer actualImageId = service.save(multipartFile)
		then:
			jdbcImageDao.add(_ as String) >> expectedImageId
		and:
			actualImageId == expectedImageId
	}
	
	//
	// Tests for get()
	//
	
	@Unroll
	def "get() should throw exception if image id is #imageId"(Integer imageId) {
		when:
			service.get(imageId)
		then:
			thrown IllegalArgumentException
		where:
			imageId | _
			null    | _
			-1      | _
			0       | _
	}
	
	def "get() should pass argument to image dao"() {
		when:
			service.get(7)
		then:
			1 * jdbcImageDao.findById({ Integer imageId ->
				assert imageId == 7
				return true
			})
	}
	
	def "get() should not call strategy when image dao returned null"() {
		when:
			ImageDto image = service.get(9)
		then:
			jdbcImageDao.findById(_ as Integer) >> null
		and:
			0 * imagePersistenceStrategy.get(_ as ImageInfoDto)
		and:
			image == null
	}
	
	def "get() should pass argument to strategy and return result from it"() {
		given:
			ImageInfoDto expectedImage = TestObjects.createImageInfoDto()
		and:
			jdbcImageDao.findById(_ as Integer) >> expectedImage
		and:
			ImageDto expectedImageDto = TestObjects.createDbImageDto()
		when:
			ImageDto actualImageDto = service.get(7)
		then:
			1 * imagePersistenceStrategy.get({ ImageInfoDto passedImage ->
				assert passedImage?.id == expectedImage.id
				assert passedImage?.type == expectedImage.type.toString()
				return true
			}) >> expectedImageDto
		and:
			actualImageDto == expectedImageDto
	}
	
	def "get() should return null when strategy returned null"() {
		given:
			jdbcImageDao.findById(_ as Integer) >> TestObjects.createImageInfoDto()
		and:
			imagePersistenceStrategy.get(_ as ImageInfoDto) >> null
		when:
			ImageDto image = service.get(8)
		then:
			image == null
	}
	
}
