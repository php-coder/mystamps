package ru.mystamps.web.service

import org.springframework.web.multipart.MultipartFile

import spock.lang.Specification
import spock.lang.Unroll

import ru.mystamps.web.dao.ImageDao
import ru.mystamps.web.dao.ImageDataDao
import ru.mystamps.web.entity.Image
import ru.mystamps.web.entity.ImageData
import ru.mystamps.web.service.dto.DbImageDto
import ru.mystamps.web.service.dto.ImageDto

class DatabaseImagePersistenceStrategyTest extends Specification {
	
	private ImageDao imageDao = Mock()
	private ImageDataDao imageDataDao = Mock()
	private MultipartFile multipartFile = Mock()
	
	private DatabaseImagePersistenceStrategy strategy = new DatabaseImagePersistenceStrategy(imageDao, imageDataDao)
	
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
			strategy.save(null)
		then:
			thrown IllegalArgumentException
	}
	
	def "save() should throw exception if file has zero size"() {
		when:
			strategy.save(multipartFile)
		then:
			multipartFile.getSize() >> 0L
		and:
			thrown IllegalArgumentException
	}
	
	def "save() should throw exception if content type is null"() {
		when:
			strategy.save(multipartFile)
		then:
			multipartFile.getContentType() >> null
		and:
			thrown IllegalArgumentException
	}
	
	def "save() should throw exception for unsupported content type"() {
		when:
			strategy.save(multipartFile)
		then:
			multipartFile.getContentType() >> "image/tiff"
		and:
			thrown IllegalStateException
	}
	
	def "save() should pass image to image dao"() {
		when:
			strategy.save(multipartFile)
		then:
			1 * imageDao.save(_ as Image) >> new Image()
	}
	
	def "save() should convert IOException to RuntimeException"() {
		given:
			multipartFile.getBytes() >> { throw new IOException() }
		when:
			strategy.save(multipartFile)
		then:
			RuntimeException ex = thrown()
		and:
			ex.cause instanceof IOException
	}
	
	def "save() should pass content type to image dao"() {
		when:
			strategy.save(multipartFile)
		then:
			multipartFile.getContentType() >> "image/jpeg"
		and:
			1 * imageDao.save({ Image image ->
				assert image?.type == Image.Type.JPEG
				return true
			}) >> new Image()
	}
	
	def "save() should throw exception when image dao returned null"() {
		when:
			strategy.save(multipartFile)
		then:
			imageDao.save(_ as Image) >> null
		and:
			0 * imageDataDao.save(_ as ImageData)
		and:
			thrown RuntimeException
	}
	
	def "save() should pass file content to image data dao"() {
		given:
			byte[] expected = "test".getBytes()
			multipartFile.getBytes() >> expected
		when:
			strategy.save(multipartFile)
		then:
			1 * imageDataDao.save({ ImageData imageData ->
				assert imageData?.content == expected
				return true
			})
	}
	
	def "save() should pass image from image dao to image data dao"() {
		given:
			Image expectedImage = TestObjects.createImage()
		when:
			strategy.save(multipartFile)
		then:
			imageDao.save(_ as Image) >> expectedImage
		and:
			1 * imageDataDao.save({ ImageData imageData ->
				assert imageData?.image == expectedImage
				return true
			})
	}
	
	def "save() should return value from image dao"() {
		given:
			Integer expectedImageId = 12
		and:
			Image expectedImage = TestObjects.createImage()
			expectedImage.setId(expectedImageId)
		when:
			Integer imageId = strategy.save(multipartFile)
		then:
			imageDao.save(_ as Image) >> expectedImage
		and:
			imageId == expectedImageId
	}
	
	//
	// Tests for get()
	//
	
	@Unroll
	def "get() should throw exception if image id is #imageId"(Integer imageId, Object _) {
		when:
			strategy.get(imageId)
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
			strategy.get(7)
		then:
			1 * imageDao.findOne({ Integer imageId ->
				assert imageId == 7
				return true
			}) >> TestObjects.createImage()
	}
	
	def "get() should not call image data dao when image dao returned null"() {
		given:
			imageDao.findOne(_ as Integer) >> null
		when:
			ImageDto image = strategy.get(9)
		then:
			0 * imageDataDao.findByImage(_ as Image)
		and:
			image == null
	}
	
	def "get() should pass result from image dao to image data dao"() {
		given:
			Image expectedImage = TestObjects.createImage()
		and:
			imageDao.findOne(_ as Integer) >> expectedImage
		when:
			strategy.get(6)
		then:
			1 * imageDataDao.findByImage({ Image image ->
				assert image == expectedImage
				return true
			})
	}
	
	def "get() should return null when image data dao returned null"() {
		given:
			imageDao.findOne(_ as Integer) >> TestObjects.createImage()
		and:
			imageDataDao.findByImage(_ as Image) >> null
		when:
			ImageDto image = strategy.get(8)
		then:
			image == null
	}
	
	def "get() should return result from image data dao"() {
		given:
			imageDao.findOne(_ as Integer) >> TestObjects.createImage()
		and:
			ImageData expectedImageData = TestObjects.createImageData()
		and:
			ImageDto expectedImage = new DbImageDto(expectedImageData)
		and:
			imageDataDao.findByImage(_ as Image) >> expectedImageData
		when:
			ImageDto image = strategy.get(10)
		then:
			image == expectedImage
	}
	
}
