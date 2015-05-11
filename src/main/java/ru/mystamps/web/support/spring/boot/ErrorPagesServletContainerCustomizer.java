/*
 * Copyright (C) 2009-2015 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.support.spring.boot;

import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.ErrorPage;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import ru.mystamps.web.Url;

@Configuration
public class ErrorPagesServletContainerCustomizer implements EmbeddedServletContainerCustomizer {
	
	@Override
	public void customize(ConfigurableEmbeddedServletContainer container) {
		container.addErrorPages(new ErrorPage(HttpStatus.UNAUTHORIZED, Url.UNAUTHORIZED_PAGE));
		container.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, Url.NOT_FOUND_PAGE));
		container.addErrorPages(new ErrorPage(Exception.class, Url.INTERNAL_ERROR_PAGE));
	}
	
}
