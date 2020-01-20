/*
 * Copyright (C) 2009-2020 Slava Semushin <slava.semushin@gmail.com>
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

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StreamUtils;
import ru.mystamps.web.feature.series.DownloadResult.Code;
import ru.mystamps.web.support.spring.security.HasAuthority;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

@RequiredArgsConstructor
public class HttpURLConnectionDownloaderService implements DownloaderService {
	
	private static final Logger LOG =
		LoggerFactory.getLogger(HttpURLConnectionDownloaderService.class);
	
	// We don't support redirects because they allow to bypass some of our validations.
	// FIXME: How exactly redirects can harm?
	@SuppressWarnings({"PMD.RedundantFieldInitializer", "PMD.ImmutableField"})
	private boolean followRedirects = false;
	
	// Only types listed here will be downloaded. For other types, INVALID_FILE_TYPE error
	// will be returned. An empty array (or null) means that all types are allowed.
	// FIXME: at this moment we do a case sensitive comparison. Should we change it?
	private final String[] allowedContentTypes;
	
	// Max time to wait during opening a connection to a resource (in milliseconds).
	// Also is used for setting a max time for reading data from it. A timeout
	// of zero is interpreted as an infinite timeout.
	private final int timeout;
	
	@Override
	@PreAuthorize(HasAuthority.DOWNLOAD_IMAGE)
	public DownloadResult download(String fileUrl) {
		// FIXME(security): fix possible log injection
		LOG.debug("Downloading '{}'", fileUrl);
		
		try {
			URL url = new URL(fileUrl);
			
			HttpURLConnection conn = openConnection(url);
			if (conn == null) {
				return DownloadResult.failed(Code.UNEXPECTED_ERROR);
			}
			
			configureUserAgent(conn);
			configureTimeouts(conn);
			configureRedirects(conn);
			
			Code connectionResult = connect(conn);
			if (connectionResult != Code.SUCCESS) {
				return DownloadResult.failed(connectionResult);
			}
			
			try (InputStream stream = new BufferedInputStream(conn.getInputStream())) {
				Code validationResult = validateConnection(conn);
				if (validationResult != Code.SUCCESS) {
					return DownloadResult.failed(validationResult);
				}
				
				// FIXME(java9): use InputStream.readAllBytes()
				byte[] data = StreamUtils.copyToByteArray(stream);
				String contentType = conn.getContentType();
				return DownloadResult.succeeded(data, contentType);
				
			} catch (FileNotFoundException ignored) {
				LOG.debug("Couldn't download file: not found on the server");
				return DownloadResult.failed(Code.FILE_NOT_FOUND);
			}
			
		} catch (MalformedURLException ex) {
			LOG.error("Couldn't download file: invalid URL: {}", ex.getMessage());
			return DownloadResult.failed(Code.INVALID_URL);

		} catch (IOException ex) {
			LOG.warn("Couldn't download file", ex);
			return DownloadResult.failed(Code.UNEXPECTED_ERROR);
		}
		
	}
	
	private static HttpURLConnection openConnection(URL url) throws IOException {
		URLConnection connection = url.openConnection();
		if (!(connection instanceof HttpURLConnection)) {
			LOG.warn(
				"Couldn't open connection: "
				+ "unknown type of connection class ({}). "
				+ "Downloading files from external servers won't work!",
				connection
			);
			return null;
		}

		return (HttpURLConnection)connection;
	}
	
	private static void configureUserAgent(URLConnection conn) {
		// FIXME: make it configurable
		conn.setRequestProperty(
			"User-Agent",
			"Mozilla/5.0 (X11; Fedora; Linux x86_64; rv:46.0) Gecko/20100101 Firefox/46.0"
		);
	}
	
	private void configureTimeouts(URLConnection conn) {
		conn.setConnectTimeout(timeout);
		conn.setReadTimeout(timeout);
	}
	
	private void configureRedirects(HttpURLConnection conn) {
		conn.setInstanceFollowRedirects(followRedirects);
	}
	
	private static Code connect(HttpURLConnection conn) {
		try {
			conn.connect();
			return Code.SUCCESS;
		
		} catch (ConnectException ignored) {
			LOG.debug("Couldn't download file: connect() has failed");
			return Code.UNEXPECTED_ERROR;
		
		} catch (IOException ex) {
			LOG.debug("Couldn't download file: connect() has failed", ex);
			return Code.UNEXPECTED_ERROR;
		}
	}
	
	private Code validateConnection(HttpURLConnection conn) throws IOException {
		int status = conn.getResponseCode();
		if (status == HttpURLConnection.HTTP_MOVED_TEMP
			|| status == HttpURLConnection.HTTP_MOVED_PERM) {
			if (!followRedirects) {
				LOG.debug("Couldn't download file: redirects are disallowed");
				return Code.INVALID_REDIRECT;
			}
			
		} else if (status != HttpURLConnection.HTTP_OK) {
			LOG.debug("Couldn't download file: unexpected response status {}", status);
			return Code.UNEXPECTED_ERROR;
		}
		
		String contentType = conn.getContentType();
		
		// We need only the first part from "text/html; charset=UTF-8"
		contentType = StringUtils.substringBefore(contentType, ";");
		
		if (!ArrayUtils.contains(allowedContentTypes, contentType)) {
			// FIXME(security): fix possible log injection
			LOG.debug("Couldn't download file: unsupported file type '{}'", contentType);
			return Code.INVALID_FILE_TYPE;
		}
		
		return Code.SUCCESS;
	}
	
}
