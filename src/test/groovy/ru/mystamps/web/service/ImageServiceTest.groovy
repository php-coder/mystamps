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

import ru.mystamps.web.entity.Image
import ru.mystamps.web.service.dto.ImageDto

class ImageServiceTest extends Specification {
	
	private MultipartFile multipartFile = Mock()
	private ImagePersistenceStrategy imagePersistenceStrategy = Mock()
	
	private ImageService service = new ImageServiceImpl(imagePersistenceStrategy)
	
	//
	// Tests for save()
	//
	
	def "save() should return url with image"() {
		given:
			Integer expectedImageId = 17
			String expectedUrl = ImageService.GET_IMAGE_PAGE.replace("{id}", String.valueOf(expectedImageId))
		when:
			String url = service.save(multipartFile)
		then:
			1 * imagePersistenceStrategy.save({ MultipartFile passedFile ->
				assert passedFile == multipartFile
				return true
			}) >> expectedImageId
		and:
			url == expectedUrl
	}
	
	//
	// Tests for get()
	//
	
	def "get() should pass argument to dao and return result from it"() {
		given:
			Image expectedImage = TestObjects.createImage()
		and:
			String expectedType = expectedImage.type.toString()
		and:
			byte[] expectedData = expectedImage.data
		when:
			ImageDto image = service.get(7)
		then:
			1 * imagePersistenceStrategy.get({ Integer imageId ->
				assert imageId == 7
				return true
			}) >> expectedImage
		and:
			image.type == expectedType
			image.data == expectedData
	}
	
	def "get() should return null when dao returned null"() {
		given:
			imagePersistenceStrategy.get(_ as Integer) >> null
		when:
			ImageDto image = service.get(8)
		then:
			image == null
	}
	
}
