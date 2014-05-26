/*
 * Copyright (C) 2009-2014 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.multipart.MultipartFile;

import ru.mystamps.web.entity.Image;
import ru.mystamps.web.service.dto.FsImageDto;
import ru.mystamps.web.service.dto.ImageDto;
import ru.mystamps.web.service.exception.ImagePersistenceException;

public class FilesystemImagePersistenceStrategy implements ImagePersistenceStrategy {
	private static final Logger LOG =
		LoggerFactory.getLogger(FilesystemImagePersistenceStrategy.class);
	
	private final File storageDir;
	
	public FilesystemImagePersistenceStrategy(String directory) {
		this.storageDir = new File(directory);
	}
	
	@PostConstruct
	public void init() {
		LOG.info("Images will be saved into {} directory", storageDir);
		
		if (!storageDir.exists()) {
			LOG.warn("Directory '{}' doesn't exist! Image uploading won't work.", storageDir);
		}
		
		if (!storageDir.canWrite()) {
			LOG.warn(
				"Directory '{}' exists but doesn't writable for current user! "
				+ "Image uploading won't work.",
				storageDir
			);
		}
	}
	
	@Override
	public void save(MultipartFile file, Image image) {
		try {
			File dest = createFile(image);
			writeToFile(file, dest);
			
			LOG.debug("Image's data was written into file {}", dest);
		
		} catch (IOException ex) {
			throw new ImagePersistenceException(ex);
		}
	}
	
	@Override
	public ImageDto get(Image image) {
		File dest = createFile(image);
		if (!dest.exists()) {
			LOG.warn("Found image without content: #{} ({} doesn't exist)", image.getId(), dest);
			return null;
		}
		
		try {
			byte[] content = toByteArray(dest);
			return new FsImageDto(image, content);
		
		} catch (IOException ex) {
			throw new ImagePersistenceException(ex);
		}
	}
	
	// protected to allow spying
	protected File createFile(Image image) {
		return new File(storageDir, generateFileName(image));
	}
	
	// protected to allow spying
	protected void writeToFile(MultipartFile file, File dest) throws IOException {
		// we can't use file.transferTo(dest) there because it creates file
		// relatively to directory from multipart-config/location in web.xml
		Files.copy(file.getInputStream(), dest.toPath());
	}
	
	// protected to allow spying
	protected byte[] toByteArray(File dest) throws IOException {
		// TODO(guava): use Files.toByteArray()
		return FileUtils.readFileToByteArray(dest);
	}

	private static String generateFileName(Image image) {
		// TODO(performance): specify initial capacity explicitly
		return new StringBuilder()
			.append(image.getId())
			.append('.')
			.append(image.getType().toString().toLowerCase())
			.toString();
	}
	
}
