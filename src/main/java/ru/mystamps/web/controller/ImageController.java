/*
 * Copyright (C) 2009-2012 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.controller;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.mystamps.web.entity.Image;
import ru.mystamps.web.service.ImageService;
import ru.mystamps.web.Url;

@Controller
public class ImageController {
	
	private final ImageService imageService;
	
	@Inject
	public ImageController(ImageService imageService) {
		this.imageService = imageService;
	}
	
	@RequestMapping(value = Url.GET_IMAGE_PAGE, method = RequestMethod.GET)
	public void getImage(@PathVariable("id") Integer id, HttpServletResponse response)
		throws IOException {
		
		Image image = imageService.findById(id);
		if (image == null) {
			throw new NotFoundException();
		}

		// TODO: set content disposition
		response.setContentType("image/" + image.getType().toString().toLowerCase());
		response.setContentLength(image.getData().length);
		
		response.getOutputStream().write(image.getData());
	}
	
}

