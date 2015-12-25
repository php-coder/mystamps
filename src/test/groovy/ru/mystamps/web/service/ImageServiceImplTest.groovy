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

import org.springframework.web.multipart.MultipartFile

import spock.lang.Specification
import spock.lang.Unroll

import ru.mystamps.web.dao.ImageDao
import ru.mystamps.web.dao.JdbcImageDao
import ru.mystamps.web.entity.Image
import ru.mystamps.web.service.dto.ImageDto
import ru.mystamps.web.service.exception.ImagePersistenceException

class ImageServiceImplTest extends Specification {

	private ImageDao imageDao = Mock()
	private JdbcImageDao jdbcImageDao = Mock()
	private MultipartFile multipartFile = Mock()
	private ImagePersistenceStrategy imagePersistenceStrategy = Mock()
	
	private ImageService service = new ImageServiceImpl(
		imagePersistenceStrategy,
		imageDao,
		jdbcImageDao
	)
	
	def setup() {
		multipartFile.getSize() >> 1024L
		multipartFile.getContentType() >> 'image/png'
		imageDao.save(_ as Image) >> new Image()
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
			1 * imageDao.save(_ as Image) >> new Image()
	}
	
	@Unroll
	def "save() should pass content type '#contentType' to image dao"(String contentType, Image.Type expectedType) {
		when:
			service.save(multipartFile)
		then:
			multipartFile.getContentType() >> contentType
		and:
			1 * imageDao.save({ Image image ->
				assert image?.type == expectedType
				return true
			}) >> new Image()
		where:
			contentType                 || expectedType
			'image/jpeg'                || Image.Type.JPEG
			'image/jpeg; charset=UTF-8' || Image.Type.JPEG
			'image/png'                 || Image.Type.PNG
			'image/png; charset=UTF8'   || Image.Type.PNG
	}
	
	def "save() should throw exception when image dao returned null"() {
		when:
			service.save(multipartFile)
		then:
			imageDao.save(_ as Image) >> null
		and:
			0 * imagePersistenceStrategy.save(_ as MultipartFile, _ as Image)
		and:
			thrown ImagePersistenceException
	}
	
	def "save() should call strategy"() {
		given:
			Image expectedImage = TestObjects.createImage()
		when:
			String url = service.save(multipartFile)
		then:
			imageDao.save(_ as Image) >> expectedImage
		and:
			1 * imagePersistenceStrategy.save({ MultipartFile passedFile ->
				assert passedFile == multipartFile
				return true
			}, { Image passedImage ->
				assert passedImage == expectedImage
				return true
			})
	}
	
	def "save() should return saved image"() {
		given:
			Image expectedImage = TestObjects.createImage()
		when:
			Image actualImage = service.save(multipartFile)
		then:
			imageDao.save(_ as Image) >> expectedImage
		and:
			actualImage == expectedImage
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
			1 * imageDao.findOne({ Integer imageId ->
				assert imageId == 7
				return true
			})
	}
	
	def "get() should not call strategy when image dao returned null"() {
		when:
			ImageDto image = service.get(9)
		then:
			imageDao.findOne(_ as Integer) >> null
		and:
			0 * imagePersistenceStrategy.get(_ as Integer)
		and:
			image == null
	}
	
	def "get() should pass argument to strategy and return result from it"() {
		given:
			Image expectedImage = TestObjects.createImage()
		and:
			imageDao.findOne(_ as Integer) >> expectedImage
		and:
			ImageDto expectedImageDto = TestObjects.createDbImageDto()
		when:
			ImageDto actualImageDto = service.get(7)
		then:
			1 * imagePersistenceStrategy.get({ Image passedImage ->
				assert passedImage == expectedImage
				return true
			}) >> expectedImageDto
		and:
			actualImageDto == expectedImageDto
	}
	
	def "get() should return null when strategy returned null"() {
		given:
			imageDao.findOne(_ as Integer) >> TestObjects.createImage()
		and:
			imagePersistenceStrategy.get(_ as Integer) >> null
		when:
			ImageDto image = service.get(8)
		then:
			image == null
	}
	
}
