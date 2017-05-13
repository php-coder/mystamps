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

import org.springframework.web.multipart.MultipartFile

import spock.lang.Specification

import ru.mystamps.web.dao.ImageDataDao
import ru.mystamps.web.dao.dto.AddImageDataDbDto
import ru.mystamps.web.dao.dto.ImageDto
import ru.mystamps.web.dao.dto.ImageInfoDto
import ru.mystamps.web.service.exception.ImagePersistenceException

@SuppressWarnings(['ClassJavadoc', 'MethodName', 'NoDef', 'NoTabCharacter', 'TrailingWhitespace'])
class DatabaseImagePersistenceStrategyTest extends Specification {
	
	private final ImageDataDao imageDataDao = Mock()
	private final MultipartFile multipartFile = Mock()
	private final ImageInfoDto imageInfoDto = TestObjects.createImageInfoDto()
	
	private final ImagePersistenceStrategy strategy = new DatabaseImagePersistenceStrategy(imageDataDao)
	
	//
	// Tests for save()
	//
	
	def "save() should convert IOException to ImagePersistenceException"() {
		given:
			multipartFile.bytes >> { throw new IOException() }
		when:
			strategy.save(multipartFile, imageInfoDto)
		then:
			ImagePersistenceException ex = thrown()
		and:
			ex.cause instanceof IOException
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "save() should pass dto to image data dao"() {
		given:
			Integer expectedImageId = imageInfoDto.id
		and:
			byte[] expectedContent = 'test'.bytes
			multipartFile.bytes >> expectedContent
		when:
			strategy.save(multipartFile, imageInfoDto)
		then:
			1 * imageDataDao.add({ AddImageDataDbDto imageData ->
				assert imageData?.imageId == expectedImageId
				assert imageData?.content == expectedContent
				assert imageData?.preview == false
				return true
			})
	}
	
	//
	// Tests for savePreview()
	//
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "savePreview() should pass dto to image data dao"() {
		given:
			Integer expectedImageId = imageInfoDto.id
		and:
			byte[] expectedContent = 'test'.bytes
		when:
			strategy.savePreview(expectedContent, imageInfoDto)
		then:
			1 * imageDataDao.add({ AddImageDataDbDto imageData ->
				assert imageData?.imageId == expectedImageId
				assert imageData?.content == expectedContent
				assert imageData?.preview == true
				return true
			})
	}
	
	//
	// Tests for get()
	//
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "get() should pass image to image data dao"() {
		given:
			Integer expectedImageId = imageInfoDto.id
		when:
			strategy.get(imageInfoDto)
		then:
			1 * imageDataDao.findByImageId({ Integer imageId ->
				assert imageId == expectedImageId
				return true
			}, { Boolean preview ->
				assert preview == false
				return true
			})
	}
	
	def "get() should return null when image data dao returned null"() {
		given:
			imageDataDao.findByImageId(_ as Integer, _ as Boolean) >> null
		when:
			ImageDto result = strategy.get(imageInfoDto)
		then:
			result == null
	}
	
	def "get() should return result from image data dao"() {
		given:
			ImageDto expectedImageDto = TestObjects.createDbImageDto()
		and:
			imageDataDao.findByImageId(_ as Integer, _ as Boolean) >> expectedImageDto
		when:
			ImageDto result = strategy.get(imageInfoDto)
		then:
			result == expectedImageDto
	}
	
}
