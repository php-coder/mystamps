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
package ru.mystamps.web.support.h2;

import java.util.Collections;

import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import org.h2.server.web.WebServlet;

@Configuration
@Profile("test")
public class H2Config {
	
	/* Web console for managing H2 database.
	 *
	 * Access it via http://127.0.0.1:8080/console and use "org.h2.Driver" as the driver,
	 * "jdbc:h2:mem:mystamps" as the URL, "sa" as the username and a blank password.
	 */
	@Bean
	public ServletRegistrationBean getH2Console() {
		ServletRegistrationBean servlet = new ServletRegistrationBean();
		servlet.setName("H2Console");
		servlet.setServlet(new WebServlet());
		servlet.setLoadOnStartup(2);
		// See also src/main/java/ru/mystamps/web/support/spring/security/SecurityConfig.java
		servlet.setUrlMappings(Collections.singletonList("/console/*"));
		return servlet;
	}
	
}
