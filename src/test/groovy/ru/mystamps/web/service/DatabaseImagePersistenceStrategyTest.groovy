package ru.mystamps.web.service

import spock.lang.Specification
import spock.lang.Unroll

import ru.mystamps.web.dao.ImageDao
import ru.mystamps.web.entity.Image

class DatabaseImagePersistenceStrategyTest extends Specification {
	
	private ImageDao imageDao = Mock()
	
	private DatabaseImagePersistenceStrategy strategy = new DatabaseImagePersistenceStrategy(imageDao)
	
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
