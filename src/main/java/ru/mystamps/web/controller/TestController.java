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
package ru.mystamps.web.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ru.mystamps.web.Url;

@Controller
public class TestController {
	
	@GetMapping("/test/invalid/response-301")
	public void redirect(HttpServletResponse response) {
		response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
		response.setHeader("Location", Url.SITE);
	}
	
	@GetMapping("/test/invalid/response-400")
	public void badRequest(HttpServletResponse response) throws IOException {
		response.sendError(HttpServletResponse.SC_BAD_REQUEST);
	}
	
	@GetMapping("/test/invalid/response-404")
	public void notFound(HttpServletResponse response) throws IOException {
		response.sendError(HttpServletResponse.SC_NOT_FOUND);
	}
	
	@GetMapping("/test/invalid/empty-jpeg-file")
	public void emptyJpegFile(HttpServletResponse response) {
		response.setContentType("image/jpeg");
		response.setContentLength(0);
	}
	
	@GetMapping(path = "/test/invalid/not-image-file", produces = "application/json")
	@ResponseBody
	public String simpleJson() {
		return "test";
	}
	
	@GetMapping(path = "/test/invalid/simple-html")
	public void simpleHtml(HttpServletResponse response) throws IOException {
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().println(
			"<html><head><title>test</title></head><body>test</body></html>"
		);
	}
	
}
