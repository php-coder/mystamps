/*
 * Copyright (C) 2009-2018 Slava Semushin <slava.semushin@gmail.com>
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
import java.nio.file.StandardOpenOption;
import java.util.Locale;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;

import org.springframework.web.multipart.MultipartFile;

import ru.mystamps.web.dao.dto.ImageDto;
import ru.mystamps.web.dao.dto.ImageInfoDto;
import ru.mystamps.web.service.dto.FsImageDto;
import ru.mystamps.web.service.exception.ImagePersistenceException;

public class FilesystemImagePersistenceStrategy implements ImagePersistenceStrategy {
	
	private final Logger log;
	private final File storageDir;
	private final File previewDir;
	
	public FilesystemImagePersistenceStrategy(Logger logger, String storageDir, String previewDir) {
		this.log = logger;
		this.storageDir = new File(storageDir);
		this.previewDir = new File(previewDir);
	}
	
	@PostConstruct
	public void init() {
		log.info("Images will be saved into {} directory", storageDir);
		
		if (!storageDir.exists()) { // NOPMD: ConfusingTernary (it's ok for me)
			log.warn("Directory '{}' doesn't exist! Image uploading won't work.", storageDir);
		
		} else if (!storageDir.canWrite()) {
			log.warn(
				// TODO(java9): log also user: ProcessHandle.current().info().user()
				"Directory '{}' exists but isn't writable for the current user! "
				+ "Image uploading won't work.",
				storageDir
			);
		}
		
		log.info("Image previews will be saved into {} directory", previewDir);
		
		if (!previewDir.exists()) { // NOPMD: ConfusingTernary (it's ok for me)
			log.warn(
				"Directory '{}' doesn't exist! Image preview generation won't work",
				previewDir
			);
		
		} else if (!previewDir.canWrite()) {
			// TODO(java9): log also user: ProcessHandle.current().info().user()
			log.warn(
				"Directory '{}' exists but isn't writable for the current user! "
				+ "Image preview generation won't work",
				previewDir
			);
		}
	}
	
	@Override
	public void save(MultipartFile file, ImageInfoDto image) {
		try {
			Path dest = generateFilePath(storageDir, image);
			writeToFile(file, dest);
			
			log.info("Image data has been written into file {}", dest);
		
		} catch (IOException ex) {
			throw new ImagePersistenceException(ex);
		}
	}
	
	@Override
	public void savePreview(byte[] data, ImageInfoDto image) {
		try {
			Path dest = generateFilePath(previewDir, image);
			writeToFile(data, dest);
			
			log.info("Image preview data has been written into file {}", dest);
		
		} catch (IOException ex) {
			throw new ImagePersistenceException(ex);
		}
	}
	
	@Override
	public ImageDto get(ImageInfoDto image) {
		return get(storageDir, image, true);
	}
	
	@Override
	public ImageDto getPreview(ImageInfoDto image) {
		return get(previewDir, image, false);
	}
	
	@Override
	public void removeIfPossible(ImageInfoDto image) {
		Path dest = generateFilePath(storageDir, image);
		try {
			Files.deleteIfExists(dest);
		} catch (Exception ex) { // NOPMD: AvoidCatchingGenericException
			log.warn("Couldn't delete file {}: {}", dest, ex.getMessage());
		}
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
	protected void writeToFile(byte[] data, Path dest) throws IOException {
		// Default mode is: CREATE, WRITE, and TRUNCATE_EXISTING.
		// To prevent unexpected rewriting of existing file, we're overriding this behavior by
		// explicitly specifying options.
		Files.write(dest, data, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
	}
	
	// protected to allow spying
	protected boolean exists(Path path) {
		return Files.exists(path);
	}
	
	// protected to allow spying
	protected byte[] toByteArray(Path dest) throws IOException {
		return Files.readAllBytes(dest);
	}

	private ImageDto get(File dir, ImageInfoDto image, boolean logWarning) {
		Path dest = generateFilePath(dir, image);
		if (!exists(dest)) {
			if (logWarning) {
				log.warn(
					"Image #{}: content not found ({} doesn't exist)",
					image.getId(),
					dest
				);
			}
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
