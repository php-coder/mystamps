/**
 * Copyright (C) 2009-2020 Slava Semushin <slava.semushin@gmail.com>
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
import ru.mystamps.web.service.TestObjects
import ru.mystamps.web.tests.Random
import spock.lang.Specification
import spock.lang.Unroll

@SuppressWarnings(['ClassJavadoc', 'MethodName', 'NoDef', 'NoTabCharacter', 'TrailingWhitespace'])
class LegacyImageServiceImplTest extends Specification {

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
	
	@SuppressWarnings('UnnecessaryGetter')
	def setup() {
		multipartFile.getSize() >> 1024L
		multipartFile.getContentType() >> 'image/png'
		multipartFile.getOriginalFilename() >> 'super-image.png'
		imageDao.add(_ as String, _ as String) >> 17
	}
	
	//
	// Tests for save()
	//
	
	def "save() should throw exception if file is null"() {
		when:
			service.save(null)
		then:
			IllegalArgumentException ex = thrown()
			ex.message == 'File must be non null'
	}
	
	def "save() should throw exception if file has zero size"() {
		when:
			service.save(multipartFile)
		then:
			multipartFile.size >> 0L
		and:
			IllegalArgumentException ex = thrown()
			ex.message == 'Image size must be greater than zero'
	}
	
	def "save() should throw exception if content type is null"() {
		when:
			service.save(multipartFile)
		then:
			multipartFile.contentType >> null
		and:
			IllegalArgumentException ex = thrown()
			ex.message == 'File type must be non null'
	}
	
	def "save() should throw exception for unsupported content type"() {
		when:
			service.save(multipartFile)
		then:
			multipartFile.contentType >> 'image/tiff'
		and:
			IllegalStateException ex = thrown()
			ex.message == "File type must be PNG or JPEG image, but 'image/tiff' (tiff) were passed"
	}
	
	@Unroll
	@SuppressWarnings([
		'ClosureAsLastMethodParameter',
		'UnnecessaryReturnKeyword',
		/* false positive: */ 'UnnecessaryBooleanExpression',
	])
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
	
	@Unroll
	@SuppressWarnings([
		'ClosureAsLastMethodParameter',
		'UnnecessaryReturnKeyword',
		/* false positive: */ 'UnnecessaryBooleanExpression',
	])
	def 'save() should pass filename "#filename" to image dao'(String filename, String expectedFilename) {
		when:
			service.save(multipartFile)
		then:
			multipartFile.originalFilename >> filename
		and:
			1 * imageDao.add(
				_ as String,
				{ String actualFilename ->
					assert actualFilename == expectedFilename
					return true
				}
			) >> Random.id()
		where:
			filename                  || expectedFilename
			null                      || null
			''                        || null
			'  '                      || null
			'test.png'                || 'test.png'
			' test.png '              || 'test.png'
			'http://example/pic.jpeg' || 'http://example/pic.jpeg'
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
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
	
	def "save() should throw exception when image dao returned null"() {
		when:
			service.save(multipartFile)
		then:
			imageDao.add(_ as String, _ as String) >> null
		and:
			0 * imagePersistenceStrategy.save(_ as MultipartFile, _ as ImageInfoDto)
		and:
			ImagePersistenceException ex = thrown()
			ex.message == "Can't save image"
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "save() should call strategy"() {
		given:
			ImageInfoDto image = TestObjects.createImageInfoDto()
		when:
			service.save(multipartFile)
		then:
			imageDao.add(_ as String, _ as String) >> image.id
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
			Integer expectedImageId = 17
		and:
			ImageInfoDto expectedImageInfo = new ImageInfoDto(expectedImageId, 'PNG')
		when:
			ImageInfoDto actualImageInfo = service.save(multipartFile)
		then:
			imageDao.add(_ as String, _ as String) >> expectedImageId
		and:
			actualImageInfo == expectedImageInfo
	}
	
	//
	// Tests for get()
	//
	
	def 'get() should throw exception if image id is null'() {
		when:
			service.get(null)
		then:
			IllegalArgumentException ex = thrown()
			ex.message == 'Image id must be non null'
	}
	
	@Unroll
	def "get() should throw exception if image id is #imageId"(Integer imageId) {
		when:
			service.get(imageId)
		then:
			IllegalArgumentException ex = thrown()
			ex.message == 'Image id must be greater than zero'
		where:
			imageId | _
			-1      | _
			0       | _
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "get() should pass argument to image dao"() {
		when:
			service.get(7)
		then:
			1 * imageDao.findById({ Integer imageId ->
				assert imageId == 7
				return true
			})
	}
	
	def "get() should not call strategy when image dao returned null"() {
		when:
			ImageDto image = service.get(9)
		then:
			imageDao.findById(_ as Integer) >> null
		and:
			0 * imagePersistenceStrategy.get(_ as ImageInfoDto)
		and:
			image == null
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "get() should pass argument to strategy and return result from it"() {
		given:
			ImageInfoDto expectedImage = TestObjects.createImageInfoDto()
		and:
			imageDao.findById(_ as Integer) >> expectedImage
		and:
			ImageDto expectedImageDto = TestObjects.createImageDto()
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
			imageDao.findById(_ as Integer) >> TestObjects.createImageInfoDto()
		and:
			imagePersistenceStrategy.get(_ as ImageInfoDto) >> null
		when:
			ImageDto image = service.get(8)
		then:
			image == null
	}
	
	//
	// Tests for getOrCreatePreview()
	//
	
	def 'getOrCreatePreview() should throw exception if image id is null'() {
		when:
			service.getOrCreatePreview(null)
		then:
			IllegalArgumentException ex = thrown()
			ex.message == 'Image id must be non null'
	}
	
	@Unroll
	def "getOrCreatePreview() should throw exception if image id is #imageId"(Integer imageId) {
		when:
			service.getOrCreatePreview(imageId)
		then:
			IllegalArgumentException ex = thrown()
			ex.message == 'Image id must be greater than zero'
		where:
			imageId | _
			-1      | _
			0       | _
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "getOrCreatePreview() should pass argument to strategy and return result from it"() {
		given:
			Integer expectedImageId = 7
			String expectedImageType = 'jpeg'
		and:
			ImageDto expectedImageDto = TestObjects.createImageDto()
		when:
			ImageDto actualImageDto = service.getOrCreatePreview(expectedImageId)
		then:
			1 * imagePersistenceStrategy.getPreview({ ImageInfoDto passedImage ->
				assert passedImage?.id == expectedImageId
				assert passedImage?.type == expectedImageType
				return true
			}) >> expectedImageDto
		and:
			actualImageDto == expectedImageDto
	}
	
	//
	// Tests for addToSeries()
	//
	
	def "addToSeries() should throw exception when series id is null"() {
		when:
			service.addToSeries(null, 1)
		then:
			IllegalArgumentException ex = thrown()
			ex.message == 'Series id must be non null'
	}
	
	def "addToSeries() should throw exception when image id is null"() {
		when:
			service.addToSeries(1, null)
		then:
			IllegalArgumentException ex = thrown()
			ex.message == 'Image id must be non null'
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "addToSeries() should invoke dao, pass argument and return result from dao"() {
		given:
			Integer expectedSeriesId = 14
			Integer expectedImageId  = 15
		when:
			service.addToSeries(expectedSeriesId, expectedImageId)
		then:
			1 * imageDao.addToSeries({ Integer seriesId ->
				assert seriesId == expectedSeriesId
				return true
			}, { Integer imageId ->
				assert imageId == expectedImageId
				return true
			})
	}
	
	//
	// Tests for findBySeriesId()
	//
	
	def "findBySeriesId() should throw exception when series id is null"() {
		when:
			service.findBySeriesId(null)
		then:
			IllegalArgumentException ex = thrown()
			ex.message == 'Series id must be non null'
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "findBySeriesId() should invoke dao, pass argument and return result from dao"() {
		given:
			Integer expectedSeriesId = 14
		and:
			List<Integer> expectedResult = [ 1, 2 ]
		when:
			List<Integer> result = service.findBySeriesId(expectedSeriesId)
		then:
			1 * imageDao.findBySeriesId({ Integer seriesId ->
				assert seriesId == expectedSeriesId
				return true
			}) >> expectedResult
		and:
			result == expectedResult
	}
	
	//
	// Tests for removeIfPossible()
	//
	
	def "removeIfPossible() should throw exception when image info is null"() {
		when:
			service.removeIfPossible(null)
		then:
			IllegalArgumentException ex = thrown()
			ex.message == 'Image info must be non null'
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "removeIfPossible() should pass argument to strategy"() {
		given:
			ImageInfoDto expectedImageInfo = TestObjects.createImageInfoDto()
		when:
			service.removeIfPossible(expectedImageInfo)
		then:
			1 * imagePersistenceStrategy.removeIfPossible({ ImageInfoDto imageInfo ->
				assert imageInfo == expectedImageInfo
				return true
			})
	}
	
}
