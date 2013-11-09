package ru.mystamps.web.service

import org.springframework.web.multipart.MultipartFile

import spock.lang.Specification

import ru.mystamps.web.dao.ImageDataDao
import ru.mystamps.web.entity.Image
import ru.mystamps.web.entity.ImageData
import ru.mystamps.web.service.dto.DbImageDto
import ru.mystamps.web.service.dto.ImageDto
import ru.mystamps.web.service.exception.ImagePersistenceException

class DatabaseImagePersistenceStrategyTest extends Specification {
	
	private ImageDataDao imageDataDao = Mock()
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
	
	def "get() should pass image to image data dao"() {
		given:
			Image expectedImage = TestObjects.createImage()
		when:
			strategy.get(expectedImage)
		then:
			1 * imageDataDao.findByImage({ Image image ->
				assert image == expectedImage
				return true
			})
	}
	
	def "get() should return null when image data dao returned null"() {
		given:
			imageDataDao.findByImage(_ as Image) >> null
		when:
			ImageDto result = strategy.get(image)
		then:
			result == null
	}
	
	def "get() should return result from image data dao"() {
		given:
			ImageData expectedImageData = TestObjects.createImageData()
		and:
			ImageDto expectedImage = new DbImageDto(expectedImageData)
		and:
			imageDataDao.findByImage(_ as Image) >> expectedImageData
		when:
			ImageDto result = strategy.get(image)
		then:
			result == expectedImage
	}
	
}
