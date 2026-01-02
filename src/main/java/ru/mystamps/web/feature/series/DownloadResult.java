/*
 * Copyright (C) 2009-2026 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.feature.series;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class DownloadResult {
	private static final Logger LOG = LoggerFactory.getLogger(DownloadResult.class);
	
	private final Code code;
	private final byte[] data;
	private final String contentType;
	private final Charset charset;
	
	public static DownloadResult failed(Code code) {
		return new DownloadResult(
			code,
			ArrayUtils.EMPTY_BYTE_ARRAY,
			StringUtils.EMPTY,
			StandardCharsets.UTF_8
		);
	}
	
	public static DownloadResult succeeded(byte[] data, String contentType) {
		Charset charset = extractCharset(contentType);
		if (charset == null) {
			charset = StandardCharsets.UTF_8;
		}
		return new DownloadResult(Code.SUCCESS, data, contentType, charset);
	}
	
	public boolean hasFailed() {
		return code != Code.SUCCESS;
	}
	
	public boolean hasSucceeded() {
		return code == Code.SUCCESS;
	}
	
	public String getDataAsString() {
		return new String(data, charset);
	}
	
	public enum Code {
		SUCCESS,
		INVALID_URL,
		INVALID_REDIRECT,
		INVALID_FILE_TYPE,
		FILE_NOT_FOUND,
		INSUFFICIENT_PERMISSIONS,
		UNEXPECTED_ERROR,
	}
	
	private static Charset extractCharset(String contentType) {
		try {
			MediaType mediaType = MediaType.parseMediaType(contentType);
			return mediaType.getCharset();
			
		} catch (IllegalArgumentException ex) {
			// MediaType.parseMediaType() might throw InvalidMediaTypeException.
			// MediaType.getCharset() might throw IllegalArgumentException,
			// IllegalCharsetNameException, or UnsupportedCharsetException.
			// All of them are inherited from IllegalArgumentException, so we catch only this class
			LOG.debug("Couldn't extract charset from '{}': {}", contentType, ex.getMessage());
			return null;
		}
	}
	
}
