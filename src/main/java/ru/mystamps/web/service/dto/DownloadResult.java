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
package ru.mystamps.web.service.dto;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class DownloadResult {
	private final Code code;
	private final byte[] data;
	private final String contentType;
	
	public static DownloadResult failed(Code code) {
		return new DownloadResult(code, ArrayUtils.EMPTY_BYTE_ARRAY, StringUtils.EMPTY);
	}
	
	public static DownloadResult succeeded(byte[] data, String contentType) {
		return new DownloadResult(Code.SUCCESS, data, contentType);
	}
	
	public boolean hasFailed() {
		return code != Code.SUCCESS;
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
	
}
