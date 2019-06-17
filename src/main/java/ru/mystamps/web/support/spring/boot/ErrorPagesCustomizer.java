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
package ru.mystamps.web.support.spring.boot;

import org.springframework.boot.web.servlet.ErrorPage;
import org.springframework.boot.web.servlet.ErrorPageRegistrar;
import org.springframework.boot.web.servlet.ErrorPageRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.firewall.RequestRejectedException;
import ru.mystamps.web.feature.site.SiteUrl;

@Configuration
public class ErrorPagesCustomizer implements ErrorPageRegistrar {

	@Override
	public void registerErrorPages(ErrorPageRegistry registry) {
		registry.addErrorPages(
			new ErrorPage(HttpStatus.FORBIDDEN, SiteUrl.FORBIDDEN_PAGE),
			new ErrorPage(HttpStatus.NOT_FOUND, SiteUrl.NOT_FOUND_PAGE),
			new ErrorPage(RequestRejectedException.class, SiteUrl.NOT_FOUND_PAGE),
			new ErrorPage(Exception.class, SiteUrl.INTERNAL_ERROR_PAGE)
		);
	}
	
}
