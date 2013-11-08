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
import ru.mystamps.web.service.exception.ImagePersistenceException

class DatabaseImagePersistenceStrategyTest extends Specification {
	
	private ImageDao imageDao = Mock()
	private ImageDataDao imageDataDao = Mock()
	private MultipartFile multipartFile = Mock()
	private Image image = TestObjects.createImage()
	
	private ImagePersistenceStrategy strategy = new DatabaseImagePersistenceStrategy(imageDao, imageDataDao)
	
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
			byte[] expected = "test".getBytes()
			multipartFile.getBytes() >> expected
		when:
			strategy.save(multipartFile, image)
		then:
			1 * imageDataDao.save({ ImageData imageData ->
				assert imageData?.content == expected
				return true
			})
	}
	
	def "save() should pass image to image data dao"() {
		given:
			Image expectedImage = TestObjects.createImage()
		when:
			strategy.save(multipartFile, expectedImage)
		then:
			1 * imageDataDao.save({ ImageData imageData ->
				assert imageData?.image == expectedImage
				return true
			})
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
