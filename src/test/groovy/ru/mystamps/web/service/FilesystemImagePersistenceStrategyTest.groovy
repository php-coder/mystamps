package ru.mystamps.web.service

import org.springframework.web.multipart.MultipartFile

import spock.lang.Specification

import ru.mystamps.web.entity.Image
import ru.mystamps.web.service.exception.ImagePersistenceException

class FilesystemImagePersistenceStrategyTest extends Specification {
	private static final STORAGE_DIR = "/tmp"
	
	private MultipartFile multipartFile = Mock()
	private Image image = TestObjects.createImage()
	
	private ImagePersistenceStrategy strategy = new FilesystemImagePersistenceStrategy(STORAGE_DIR)
	
	//
	// Tests for save()
	//
	
	def "save() should saves file onto the filesystem"() {
		when:
			strategy.save(multipartFile, image)
		then:
			1 * multipartFile.transferTo(_ as File)
	}
	
	def "save() should saves file onto the configured directory"() {
		given:
			String expectedDirectoryName = STORAGE_DIR
		when:
			strategy.save(multipartFile, image)
		then:
			1 * multipartFile.transferTo({File file ->
				assert file.parent == expectedDirectoryName
				return true
			})
	}
	
	def "save() should gives proper name to the file"() {
		given:
			String expectedExtension = image.type.toString().toLowerCase()
			String expectedName = image.id
			String expectedFileName = expectedName + '.' + expectedExtension
		when:
			strategy.save(multipartFile, image)
		then:
			1 * multipartFile.transferTo({File file ->
				assert file.name == expectedFileName
				return true
			})
	}
	
	def "save() should converts IOException to ImagePersistenceException"() {
		given:
			multipartFile.transferTo(_ as File) >> { throw new IOException() }
		when:
			strategy.save(multipartFile, image)
		then:
			ImagePersistenceException ex = thrown()
		and:
			ex.cause instanceof IOException
	}
	
	def "save() should converts IllegalStateException to ImagePersistenceException"() {
		given:
			multipartFile.transferTo(_ as File) >> { throw new IllegalStateException() }
		when:
			strategy.save(multipartFile, image)
		then:
			ImagePersistenceException ex = thrown()
		and:
			ex.cause instanceof IllegalStateException
	}
	
}
