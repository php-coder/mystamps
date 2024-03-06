/**
 * Copyright (C) 2009-2024 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.feature.image

import org.slf4j.helpers.NOPLogger
import org.springframework.web.multipart.MultipartFile
import ru.mystamps.web.feature.image.ImageDb.Images
import ru.mystamps.web.tests.Random
import spock.lang.Specification
import spock.lang.Unroll

class ImageServiceImplTest extends Specification {

	private final ImageDao imageDao = Mock()
	private final MultipartFile multipartFile = Mock()
	private final ImagePreviewStrategy imagePreviewStrategy = Mock()
	private final ImagePersistenceStrategy imagePersistenceStrategy = Mock()
	
	private final ImageService service = new ImageServiceImpl(
		NOPLogger.NOP_LOGGER,
		imagePersistenceStrategy,
		imagePreviewStrategy,
		imageDao
	)
	
	def setup() {
		multipartFile.size >> 1024L
		multipartFile.contentType >> 'image/png'
		multipartFile.originalFilename >> 'super-image.png'
	}
	
	//
	// Tests for save()
	//
	
	@Unroll
	def "save() should pass content type '#contentType' to image dao"(String contentType, String expectedType) {
		when:
			service.save(multipartFile)
		then:
			multipartFile.contentType >> contentType
		and:
			1 * imageDao.add({ String type ->
				assert type == expectedType
				return true
			}, _ as String) >> 19
		where:
			contentType                 || expectedType
			'image/jpeg'                || 'JPEG'
			'image/jpeg; charset=UTF-8' || 'JPEG'
			'image/png'                 || 'PNG'
			'image/png; charset=UTF8'   || 'PNG'
	}
	
	def 'save() should pass abbreviated filename when it is too long'() {
		given:
			String longFilename = '/long/url/' + ('x' * Images.FILENAME_LENGTH)
			String expectedFilename = longFilename.take(Images.FILENAME_LENGTH - 3) + '...'
		when:
			service.save(multipartFile)
		then:
			multipartFile.originalFilename >> longFilename
		and:
			imageDao.add(
				_ as String,
				{ String actualFilename ->
					assert actualFilename == expectedFilename
					return true
				}
			) >> Random.id()
	}
	
}
