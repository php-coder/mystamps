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

import ru.mystamps.web.service.dto.ImageDto
import ru.mystamps.web.service.dto.ImageInfoDto
import ru.mystamps.web.service.exception.ImagePersistenceException

import java.nio.file.Path

class FilesystemImagePersistenceStrategyTest extends Specification {
	private static final STORAGE_DIR = File.separator + 'tmp'
	
	private MultipartFile multipartFile = Mock()
	private ImageInfoDto imageInfoDto = TestObjects.createImageInfoDto()
	private Path mockFile = Mock(Path)
	
	private ImagePersistenceStrategy strategy = Spy(FilesystemImagePersistenceStrategy, constructorArgs: [STORAGE_DIR])
	
	//
	// Tests for save()
	//
	
	def "save() should saves file onto the filesystem"() {
		when:
			strategy.save(multipartFile, imageInfoDto)
		then:
			1 * strategy.writeToFile(_ as MultipartFile, _ as Path) >> {}
	}
	
	def "save() should saves file onto the configured directory"() {
		given:
			String expectedDirectoryName = STORAGE_DIR
		when:
			strategy.save(multipartFile, imageInfoDto)
		then:
			1 * strategy.writeToFile(_ as MultipartFile, { Path path ->
				assert path.parent.toString() == expectedDirectoryName
				return true
			}) >> {}
	}
	
	def "save() should gives proper name to the file"() {
		given:
			String expectedExtension = imageInfoDto.type.toLowerCase()
			String expectedName = imageInfoDto.id
			String expectedFileName = expectedName + '.' + expectedExtension
		when:
			strategy.save(multipartFile, imageInfoDto)
		then:
			1 * strategy.writeToFile(_ as MultipartFile, { Path path ->
				assert path.fileName.toString() == expectedFileName
				return true
			}) >> {}
	}
	
	def "save() should converts IOException to ImagePersistenceException"() {
		given:
			strategy.writeToFile(_ as MultipartFile, _ as Path) >> { throw new IOException() }
		when:
			strategy.save(multipartFile, imageInfoDto)
		then:
			ImagePersistenceException ex = thrown()
		and:
			ex.cause instanceof IOException
	}
	
	//
	// Tests for get()
	//
	
	def "get() should returns null when file doesn't exist"() {
		given:
			strategy.exists(_ as Path) >> false
		and:
			strategy.createFile(_ as ImageInfoDto) >> mockFile
		when:
			ImageDto result = strategy.get(imageInfoDto)
		then:
			result == null
	}
	
	def "get() should converts IOException to ImagePersistenceException"() {
		given:
			strategy.exists(_ as Path) >> true
		and:
			strategy.createFile(_ as ImageInfoDto) >> mockFile
		and:
			strategy.toByteArray(_ as Path) >> { throw new IOException() }
		when:
			strategy.get(imageInfoDto)
		then:
			ImagePersistenceException ex = thrown()
		and:
			ex.cause instanceof IOException
	}
	
	def "get() should return result with correct type and content"() {
		given:
			String expectedType = imageInfoDto.type
		and:
			byte[] expectedData = 'any data'.bytes
		and:
			strategy.exists(_ as Path) >> true
		and:
			strategy.createFile(_ as ImageInfoDto) >> mockFile
		and:
			strategy.toByteArray(_ as Path) >> expectedData
		when:
			ImageDto result = strategy.get(imageInfoDto)
		then:
			result.type == expectedType
			result.data == expectedData
	}
	
}
