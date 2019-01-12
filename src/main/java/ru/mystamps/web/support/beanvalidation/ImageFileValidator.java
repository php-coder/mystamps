/*
 * Copyright (C) 2009-2019 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.support.beanvalidation;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.multipart.MultipartFile;

public class ImageFileValidator implements ConstraintValidator<ImageFile, MultipartFile> {
	
	private static final Logger LOG = LoggerFactory.getLogger(ImageFileValidator.class);
	
	private static final String JPEG_CONTENT_TYPE = "image/jpeg";
	private static final String PNG_CONTENT_TYPE  = "image/png";
	
	// see https://en.wikipedia.org/wiki/JPEG#Syntax_and_structure
	// CheckStyle: ignore NoWhitespaceAfterCheck for next 3 lines
	private static final byte[][] JPEG_SIGNATURES = {
		{ (byte)0xFF, (byte)0xD8, (byte)0xFF, (byte)0xE0 },
		{ (byte)0xFF, (byte)0xD8, (byte)0xFF, (byte)0xE1 }
	};
	
	// see https://en.wikipedia.org/wiki/Portable_Network_Graphics#File_header
	private static final byte[] PNG_FIRST_PART_SIGNATURE = new byte[] {
		(byte)0x89, 0x50, 0x4E, 0x47
	};
	
	private static final byte[] PNG_SECOND_PART_SIGNATURE = new byte[] {
		0x0D, 0x0A, 0x1A, 0x0A
	};
	
	private static boolean isJpeg(byte[] bytes) {
		// FIXME: also check that last 2 bytes are FF D9 (use RandomAccessFile)
		
		for (byte[] signature: JPEG_SIGNATURES) {
			if (Arrays.equals(bytes, signature)) {
				return true;
			}
		}
		
		return false;
	}
	
	private static boolean doesItLookLikePng(byte[] bytes) {
		return Arrays.equals(bytes, PNG_FIRST_PART_SIGNATURE);
	}
	
	private static boolean isItReallyPng(byte[] bytes) {
		return Arrays.equals(bytes, PNG_SECOND_PART_SIGNATURE);
	}
	
	private static byte[] readFourBytes(InputStream is) {
		// CheckStyle: ignore MagicNumber for next 1 line
		byte[] bytes = new byte[4];
		try {
			int read = is.read(bytes, 0, bytes.length);
			if (read != bytes.length) {
				return null;
			}
			
			return bytes;
			
		} catch (IOException e) {
			LOG.warn("Error during reading from file: {}", e.getMessage());
			return null;
		}
	}
	
	private static String formatBytes(byte[] bytes) {
		// CheckStyle: ignore MagicNumber for next 1 line
		return String.format("%02x %02x %02x %02x", bytes[0], bytes[1], bytes[2], bytes[3]);
	}
	
	@Override
	public void initialize(ImageFile annotation) {
		// Intentionally empty: nothing to initialize
	}
	
	// The following warnings will gone after splitting this validator (see #593)
	@SuppressWarnings({ "PMD.ModifiedCyclomaticComplexity", "PMD.NPathComplexity" })
	@Override
	public boolean isValid(MultipartFile file, ConstraintValidatorContext ctx) {
		
		if (file == null) {
			return true;
		}
		
		if (StringUtils.isEmpty(file.getOriginalFilename())) {
			return true;
		}
		
		if (file.isEmpty()) {
			return false;
		}

		String contentType = file.getContentType();
		if (!StringUtils.equalsAny(contentType, PNG_CONTENT_TYPE, JPEG_CONTENT_TYPE)) {
			LOG.debug("Reject file with content type '{}'", contentType);
			return false;
		}
		
		try (InputStream stream = file.getInputStream()) {
			
			byte[] firstPart = readFourBytes(stream);
			if (firstPart == null) {
				LOG.warn("Failed to read 4 bytes from file");
				return false;
			}
			
			if (isJpeg(firstPart)) {
				return true;
			}
			
			if (doesItLookLikePng(firstPart)) {
				byte[] secondPart = readFourBytes(stream);
				if (isItReallyPng(secondPart)) {
					return true;
				}
				
				LOG.debug(
					"Looks like file isn't a PNG image. First bytes: {} {}",
					formatBytes(firstPart),
					formatBytes(secondPart)
				);
				return false;
			}
			
			LOG.debug("Looks like file isn't an image. First bytes: {}", formatBytes(firstPart));
			return false;
			
		} catch (IOException e) {
			LOG.warn("Error during file type validation: {}", e.getMessage());
			return false;
		}
	}
	
}
