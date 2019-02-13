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
package ru.mystamps.web.controller.interceptor;

import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

@RequiredArgsConstructor
class ByteArrayMultipartFile implements MultipartFile {
	private final byte[] content;
	private final String contentType;
	private final String link;

	@Override
	public String getName() {
		throw new IllegalStateException("Not implemented");
	}

	@Override
	public String getOriginalFilename() {
		return link;
	}

	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public boolean isEmpty() {
		return getSize() == 0;
	}

	@Override
	public long getSize() {
		if (content == null) {
			return 0;
		}
		return content.length;
	}

	@Override
	public byte[] getBytes() throws IOException {
		return content; // NOPMD: MethodReturnsInternalArray
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return new ByteArrayInputStream(content);
	}

	@Override
	public void transferTo(File dest) throws IOException, IllegalStateException {
		// Default mode is: CREATE, WRITE, and TRUNCATE_EXISTING.
		// To prevent unexpected rewriting of existing file, we're overriding this behavior by
		// explicitly specifying options.
		Files.write(
			dest.toPath(),
			content,
			StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE
		);
	}
}
