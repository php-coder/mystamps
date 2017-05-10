/*
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
package ru.mystamps.web.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.multipart.MultipartFile;

import ru.mystamps.web.dao.dto.ImageDto;
import ru.mystamps.web.dao.dto.ImageInfoDto;
import ru.mystamps.web.service.dto.FsImageDto;
import ru.mystamps.web.service.exception.ImagePersistenceException;

public class FilesystemImagePersistenceStrategy implements ImagePersistenceStrategy {
	private static final Logger LOG =
		LoggerFactory.getLogger(FilesystemImagePersistenceStrategy.class);
	
	private final File storageDir;
	
	public FilesystemImagePersistenceStrategy(String storageDir) {
		this.storageDir = new File(storageDir);
	}
	
	@PostConstruct
	public void init() {
		LOG.info("Images will be saved into {} directory", storageDir);
		
		if (!storageDir.exists()) { // NOPMD: ConfusingTernary (it's ok for me)
			LOG.warn("Directory '{}' doesn't exist! Image uploading won't work.", storageDir);
		
		} else if (!storageDir.canWrite()) {
			LOG.warn(
				"Directory '{}' exists but isn't writable for the current user! "
				+ "Image uploading won't work.",
				storageDir
			);
		}
	}
	
	@Override
	public void save(MultipartFile file, ImageInfoDto image) {
		try {
			Path dest = generateFilePath(storageDir, image);
			writeToFile(file, dest);
			
			LOG.info("Image data has been written into file {}", dest);
		
		} catch (IOException ex) {
			throw new ImagePersistenceException(ex);
		}
	}
	
	@Override
	public ImageDto get(ImageInfoDto image) {
		return get(storageDir, image);
	}
	
	// protected to allow spying
	protected Path generateFilePath(File dir, ImageInfoDto image) {
		return new File(dir, generateFileName(image)).toPath();
	}
	
	// protected to allow spying
	protected void writeToFile(MultipartFile file, Path dest) throws IOException {
		// we can't use file.transferTo(dest) there because it creates file
		// relatively to directory from spring.http.multipart.location
		// in application.properties
		// See for details: https://jira.spring.io/browse/SPR-12650
		Files.copy(file.getInputStream(), dest);
	}

	// protected to allow spying
	protected boolean exists(Path path) {
		return Files.exists(path);
	}
	
	// protected to allow spying
	protected byte[] toByteArray(Path dest) throws IOException {
		return Files.readAllBytes(dest);
	}

	private ImageDto get(File dir, ImageInfoDto image) {
		Path dest = generateFilePath(dir, image);
		if (!exists(dest)) {
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
	
	private static String generateFileName(ImageInfoDto image) {
		// TODO(performance): specify initial capacity explicitly
		return new StringBuilder()
			.append(image.getId())
			.append('.')
			.append(image.getType().toLowerCase(Locale.ENGLISH))
			.toString();
	}
	
}
