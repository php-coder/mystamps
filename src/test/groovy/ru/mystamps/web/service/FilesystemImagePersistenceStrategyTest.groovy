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

import org.slf4j.helpers.NOPLogger

import org.springframework.web.multipart.MultipartFile

import spock.lang.Specification

import ru.mystamps.web.dao.dto.ImageDto
import ru.mystamps.web.dao.dto.ImageInfoDto
import ru.mystamps.web.service.exception.ImagePersistenceException

import java.nio.file.Path

@SuppressWarnings(['ClassJavadoc', 'MethodName', 'NoDef', 'NoTabCharacter', 'TrailingWhitespace'])
class FilesystemImagePersistenceStrategyTest extends Specification {
	private static final STORAGE_DIR = File.separator + 'tmp'
	private static final PREVIEW_DIR = File.separator + 'tmp'
	
	private final MultipartFile multipartFile = Mock()
	private final ImageInfoDto imageInfoDto = TestObjects.createImageInfoDto()
	private final Path mockFile = Mock(Path)
	
	@SuppressWarnings('SpaceAfterComma') // false positive
	private final ImagePersistenceStrategy strategy = Spy(
		FilesystemImagePersistenceStrategy,
		constructorArgs:[NOPLogger.NOP_LOGGER, STORAGE_DIR, PREVIEW_DIR]
	)
	
	//
	// Tests for save()
	//
	
	def 'save() should save a file onto the file system'() {
		when:
			strategy.save(multipartFile, imageInfoDto)
		then:
			1 * strategy.writeToFile(_ as MultipartFile, _ as Path) >> { }
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def 'save() should save a file into a configured directory'() {
		given:
			String expectedDirectoryName = STORAGE_DIR
		when:
			strategy.save(multipartFile, imageInfoDto)
		then:
			1 * strategy.writeToFile(_ as MultipartFile, { Path path ->
				assert path?.parent?.toString() == expectedDirectoryName
				return true
			}) >> { }
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def 'save() should give a proper name to a file'() {
		given:
			String expectedExtension = imageInfoDto.type.toLowerCase()
			String expectedName = imageInfoDto.id
			String expectedFileName = expectedName + '.' + expectedExtension
		when:
			strategy.save(multipartFile, imageInfoDto)
		then:
			1 * strategy.writeToFile(_ as MultipartFile, { Path path ->
				assert path?.fileName?.toString() == expectedFileName
				return true
			}) >> { }
	}
	
	def 'save() should convert IOException to ImagePersistenceException'() {
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
	
	def 'get() should return null when file doesn\'t exist'() {
		given:
			strategy.exists(_ as Path) >> false
		and:
			strategy.generateFilePath(_ as File, _ as ImageInfoDto) >> mockFile
		when:
			ImageDto result = strategy.get(imageInfoDto)
		then:
			result == null
	}
	
	def 'get() should convert IOException to ImagePersistenceException'() {
		given:
			strategy.exists(_ as Path) >> true
		and:
			strategy.generateFilePath(_ as File, _ as ImageInfoDto) >> mockFile
		and:
			strategy.toByteArray(_ as Path) >> { throw new IOException() }
		when:
			strategy.get(imageInfoDto)
		then:
			ImagePersistenceException ex = thrown()
		and:
			ex.cause instanceof IOException
	}
	
	def 'get() should return result with correct type and content'() {
		given:
			String expectedType = imageInfoDto.type
		and:
			byte[] expectedData = 'any data'.bytes
		and:
			strategy.exists(_ as Path) >> true
		and:
			strategy.generateFilePath(_ as File, _ as ImageInfoDto) >> mockFile
		and:
			strategy.toByteArray(_ as Path) >> expectedData
		when:
			ImageDto result = strategy.get(imageInfoDto)
		then:
			result.type == expectedType
			result.data == expectedData
	}
	
}
