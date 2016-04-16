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

import ru.mystamps.web.dao.JdbcImageDataDao
import ru.mystamps.web.dao.dto.AddImageDataDbDto
import ru.mystamps.web.entity.Image
import ru.mystamps.web.service.dto.ImageDto
import ru.mystamps.web.service.exception.ImagePersistenceException

class DatabaseImagePersistenceStrategyTest extends Specification {
	
	private JdbcImageDataDao imageDataDao = Mock()
	private MultipartFile multipartFile = Mock()
	private Image image = TestObjects.createImage()
	
	private ImagePersistenceStrategy strategy = new DatabaseImagePersistenceStrategy(imageDataDao)
	
	//
	// Tests for save()
	//
	
	def "save() should convert IOException to ImagePersistenceException"() {
		given:
			multipartFile.getBytes() >> { throw new IOException() }
		when:
			strategy.save(multipartFile, image)
		then:
			ImagePersistenceException ex = thrown()
		and:
			ex.cause instanceof IOException
	}
	
	def "save() should pass file content to image data dao"() {
		given:
			byte[] expected = 'test'.getBytes()
			multipartFile.getBytes() >> expected
		when:
			strategy.save(multipartFile, image)
		then:
			1 * imageDataDao.add({ AddImageDataDbDto imageData ->
				assert imageData?.content == expected
				return true
			})
	}
	
	def "save() should pass image to image data dao"() {
		given:
			Integer expectedImageId = image.getId()
		when:
			strategy.save(multipartFile, image)
		then:
			1 * imageDataDao.add({ AddImageDataDbDto imageData ->
				assert imageData?.imageId == expectedImageId
				return true
			})
	}
	
	//
	// Tests for get()
	//
	
	def "get() should pass image to image data dao"() {
		given:
			Integer expectedImageId = image.getId()
		and:
			Image image = TestObjects.createImage()
			image.setId(expectedImageId)
		when:
			strategy.get(image)
		then:
			1 * imageDataDao.findByImageId({ Integer imageId ->
				assert imageId == expectedImageId
				return true
			})
	}
	
	def "get() should return null when image data dao returned null"() {
		given:
			imageDataDao.findByImageId(_ as Integer) >> null
		when:
			ImageDto result = strategy.get(image)
		then:
			result == null
	}
	
	def "get() should return result from image data dao"() {
		given:
			ImageDto expectedImageDto = TestObjects.createDbImageDto()
		and:
			imageDataDao.findByImageId(_ as Integer) >> expectedImageDto
		when:
			ImageDto result = strategy.get(image)
		then:
			result == expectedImageDto
	}
	
}
