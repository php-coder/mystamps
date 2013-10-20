package ru.mystamps.web.service

import org.springframework.web.multipart.MultipartFile

import spock.lang.Specification
import spock.lang.Unroll

import ru.mystamps.web.dao.ImageDao
import ru.mystamps.web.entity.Image

class DatabaseImagePersistenceStrategyTest extends Specification {
	
	private ImageDao imageDao = Mock()
	private MultipartFile multipartFile = Mock()
	
	private DatabaseImagePersistenceStrategy strategy = new DatabaseImagePersistenceStrategy(imageDao)
	
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
	
	def "save() should pass file content to dao"() {
		given:
			byte[] expected = "test".getBytes()
			multipartFile.getBytes() >> expected
		when:
			strategy.save(multipartFile)
		then:
			1 * imageDao.save({ Image image ->
				assert image?.data == expected
				return true
			}) >> new Image()
	}
	
	def "save() should pass content type to dao"() {
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
	
	def "save() should return value from dao"() {
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
	
	def "get() should pass argument to dao and return result from it"() {
		given:
			Image expectedImage = TestObjects.createImage()
		when:
			Image image = strategy.get(7)
		then:
			1 * imageDao.findOne({ Integer imageId ->
				assert imageId == 7
				return true
			}) >> expectedImage
		and:
			image == expectedImage
	}
	
}
