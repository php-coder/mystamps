/*
 * Copyright (C) 2009-2025 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.support.togglz;

import com.github.heneke.thymeleaf.togglz.TogglzDialect;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.togglz.console.TogglzConsoleServlet;
import org.togglz.core.logging.LoggingStateRepository;
import org.togglz.core.manager.FeatureManager;
import org.togglz.core.manager.FeatureManagerBuilder;
import org.togglz.core.repository.cache.CachingStateRepository;
import org.togglz.core.repository.jdbc.JDBCStateRepository;
import org.togglz.spring.security.SpringSecurityUserProvider;
import ru.mystamps.web.support.spring.security.StringAuthority;

import javax.sql.DataSource;
import java.util.Collections;

@Configuration
@RequiredArgsConstructor
public class TogglzConfig {
	
	private static final String TOGGLZ_CONSOLE_PAGE = "/togglz";
	
	private final DataSource dataSource;
	
	@Bean
	public FeatureManager getFeatureManager() {
		return new FeatureManagerBuilder()
			.stateRepository(
				new LoggingStateRepository(
					new CachingStateRepository(
						new JDBCStateRepository(dataSource)
					)
				)
			)
			.featureEnum(Features.class)
			.userProvider(new SpringSecurityUserProvider(StringAuthority.MANAGE_TOGGLZ))
			.build();
	}
	
	/* Web console for managing Togglz.
	 *
	 * Access it via http://127.0.0.1:8080/togglz after authentication as "admin" user.
	 *
	 * @see https://www.togglz.org/documentation/admin-console.html
	 */
	@Bean
	public ServletRegistrationBean<TogglzConsoleServlet> getTogglzConsole() {
		ServletRegistrationBean<TogglzConsoleServlet> servlet = new ServletRegistrationBean<>();
		servlet.setName("TogglzConsole");
		servlet.setServlet(new TogglzConsoleServlet());
		// See also src/main/java/ru/mystamps/web/support/spring/security/SecurityConfig.java
		servlet.setUrlMappings(Collections.singletonList(TOGGLZ_CONSOLE_PAGE + "/*"));
		return servlet;
	}
	
	@Bean
	public TogglzDialect getTogglzDialect() {
		return new TogglzDialect();
	}
	
}
