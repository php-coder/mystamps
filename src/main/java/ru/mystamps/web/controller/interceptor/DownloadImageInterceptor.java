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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import ru.mystamps.web.service.DownloaderService;
import ru.mystamps.web.service.dto.DownloadResult;
import ru.mystamps.web.support.spring.security.Authority;
import ru.mystamps.web.support.spring.security.SecurityContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Converts image URL to an image by downloading it from a server and binding to a form field.
 *
 * It handles only POST requests.
 */
@RequiredArgsConstructor
public class DownloadImageInterceptor extends HandlerInterceptorAdapter {
	
	/**
	 * Field name that contains image URL.
	 */
	public static final String IMAGE_URL_FIELD_NAME = "imageUrl";
	
	/**
	 * Field name to which a downloaded image will be bound.
	 */
	public static final String DOWNLOADED_IMAGE_FIELD_NAME = "downloadedImage";
	
	/**
	 * Field name of the image that is uploaded by a user.
	 *
	 * When it's present, we won't download an image from URL because a user should choose only one
	 * image source.
	 */
	public static final String UPLOADED_IMAGE_FIELD_NAME = "uploadedImage";
	
	/**
	 * Name of request attribute, that will be used for storing an error code.
	 *
	 * To check whether an error has occurred, you can retrieve this attribute within a controller.
	 * When it's not {@code null}, it has the code in the format of fully-qualified name
	 * of the member of the {@link DownloadResult} enum.
	 */
	public static final String ERROR_CODE_ATTR_NAME = "DownloadedImage.ErrorCode";
	
	private static final Logger LOG = LoggerFactory.getLogger(DownloadImageInterceptor.class);
	
	private final DownloaderService downloaderService;
	
	@Override
	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	public boolean preHandle(
		HttpServletRequest request,
		HttpServletResponse response,
		Object handler) throws Exception {
		
		if (!HttpMethod.POST.matches(request.getMethod())) {
			return true;
		}
		
		// If the field doesn't have a value, then nothing to do here.
		String imageUrl = StringUtils.trimToEmpty(request.getParameter(IMAGE_URL_FIELD_NAME));
		if (StringUtils.isEmpty(imageUrl)) {
			return true;
		}
		
		if (!(request instanceof StandardMultipartHttpServletRequest)) {
			// It could mean that <form> tag doesn't have enctype="multipart/form-data" attribute.
			LOG.warn(
				"Unknown type of request ({}). "
				+ "Downloading images from external servers won't work!",
				request
			);
			return true;
		}
		
		StandardMultipartHttpServletRequest multipartRequest =
			(StandardMultipartHttpServletRequest)request;
		
		// Minor optimization: we don't try to download a file if we know that user also uploads its
		// own file. This case also will be validated by a controller and user will see an error.
		MultipartFile image = multipartRequest.getFile(UPLOADED_IMAGE_FIELD_NAME);
		if (image != null && StringUtils.isNotEmpty(image.getOriginalFilename())) {
			return true;
		}
		
		if (!SecurityContextUtils.hasAuthority(Authority.DOWNLOAD_IMAGE)) {
			// FIXME(security): fix possible log injection
			LOG.warn(
				"User #{} without permissions has tried to download a file from '{}'",
				SecurityContextUtils.getUserId(),
				imageUrl
			);
			request.setAttribute(
				ERROR_CODE_ATTR_NAME,
				DownloadResult.Code.INSUFFICIENT_PERMISSIONS
			);
			return true;
		}
		
		// A user has specified an image URL: we should download a file and represent it as a field.
		// Later we'll validate this file in a controller.
		DownloadResult result = downloaderService.download(imageUrl);
		if (result.hasFailed()) {
			request.setAttribute(ERROR_CODE_ATTR_NAME, result.getCode());
			return true;
		}
		
		MultipartFile downloadedImage =
			new ByteArrayMultipartFile(result.getData(), result.getContentType(), imageUrl);
		
		multipartRequest.getMultiFileMap().set(DOWNLOADED_IMAGE_FIELD_NAME, downloadedImage);
		
		return true;
	}
	
}
