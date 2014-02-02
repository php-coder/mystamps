/*
 * Copyright (C) 2009-2014 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.config;

import javax.inject.Inject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import liquibase.integration.spring.SpringLiquibase;

@Configuration
public class LiquibaseConfig {
	
	@Inject
	private Environment env;
	
	@Inject
	private DataSourceConfig dataSourceConfig;
	
	@Inject
	@Bean(name = "liquibase")
	public SpringLiquibase getSpringLiquibase() {
		SpringLiquibase liquibase = new SpringLiquibase();
		liquibase.setDataSource(dataSourceConfig.getDataSource());
		liquibase.setChangeLog("classpath:/liquibase/changelog.xml");
		liquibase.setContexts(getActiveContexts(env));
		return liquibase;
	}
	
	private static String getActiveContexts(Environment env) {
		if (env.acceptsProfiles("test")) {
			return "scheme, init-data, test-data";
		} else {
			// see also duplicate definition at pom.xml
			return "scheme, init-data";
		}
	}
	
}
