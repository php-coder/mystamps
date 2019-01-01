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
package ru.mystamps.web.feature.image;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.Url;

@Controller
@RequiredArgsConstructor
public class ImageController {
	
	private final ImageService imageService;
	
	@GetMapping(Url.GET_IMAGE_PAGE)
	public void getImage(@PathVariable("id") Integer imageId, HttpServletResponse response)
		throws IOException {
		
		if (imageId == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		ImageDto image = imageService.get(imageId);
		if (image == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		// TODO: set content disposition
		response.setContentType("image/" + image.getType().toLowerCase(Locale.ENGLISH));
		response.setContentLength(image.getData().length);
		
		response.getOutputStream().write(image.getData());
	}
	
	@GetMapping(Url.GET_IMAGE_PREVIEW_PAGE)
	public void getImagePreview(@PathVariable("id") Integer imageId, HttpServletResponse response)
		throws IOException {
		
		if (imageId == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		ImageDto image = imageService.getOrCreatePreview(imageId);
		if (image == null) {
			// return original image when error has occurred
			getImage(imageId, response);
			return;
		}
		
		response.setContentType("image/" + image.getType().toLowerCase(Locale.ENGLISH));
		response.setContentLength(image.getData().length);
		
		response.getOutputStream().write(image.getData());
	}
	
}

