/*
 * Copyright (C) 2009-2013 Slava Semushin <slava.semushin@gmail.com>
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

import ru.mystamps.web.dao.ImageDao
import ru.mystamps.web.entity.Image

class ImageServiceTest extends Specification {
	
	private ImageDao imageDao = Mock()
	private MultipartFile multipartFile = Mock()
	private ImagePersistenceStrategy imagePersistenceStrategy = Mock()
	
	private ImageService service = new ImageServiceImpl(imageDao, imagePersistenceStrategy)
	
	def setup() {
		multipartFile.getSize() >> 1024L
		multipartFile.getContentType() >> "image/png"
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
			multipartFile.getContentType() >> "image/tiff"
		and:
			thrown IllegalStateException
	}
	
	def "save() should pass image to image dao"() {
		when:
			service.save(multipartFile)
		then:
			1 * imageDao.save(_ as Image) >> new Image()
	}
	
	def "save() should convert IOException to RuntimeException"() {
		given:
			multipartFile.getBytes() >> { throw new IOException() }
		when:
			service.save(multipartFile)
		then:
			RuntimeException ex = thrown()
		and:
			ex.cause instanceof IOException
	}
	
	def "save() should pass file content to dao"() {
		given:
			byte[] expected = "test".getBytes()
			multipartFile.getBytes() >> expected
		when:
			service.save(multipartFile)
		then:
			1 * imageDao.save({ Image image ->
				assert image?.data == expected
				return true
			}) >> new Image()
	}
	
	def "save() should pass content type to dao"() {
		when:
			service.save(multipartFile)
		then:
			multipartFile.getContentType() >> "image/jpeg"
		and:
			1 * imageDao.save({ Image image ->
				assert image?.type == Image.Type.JPEG
				return true
			}) >> new Image()
	}
	
	def "save() should return url with image"() {
		given:
			Integer expectedImageId = 10
		and:
			Image image = Mock()
		when:
			String result = service.save(multipartFile)
		then:
			image.getId() >> expectedImageId
		and:
			imageDao.save(_ as Image) >> image
		and:
			String expectedUrl =
				ImageService.GET_IMAGE_PAGE.replace("{id}", String.valueOf(expectedImageId))
			result == expectedUrl
	}
	
	//
	// Tests for get()
	//
	
	def "get() should pass argument to dao and return result from it"() {
		given:
			Image expectedImage = TestObjects.createImage()
		when:
			Image image = service.get(7)
		then:
			1 * imagePersistenceStrategy.get({ Integer imageId ->
				assert imageId == 7
				return true
			}) >> expectedImage
		and:
			image == expectedImage
	}
	
}
