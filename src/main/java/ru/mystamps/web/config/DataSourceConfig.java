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
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

@Configuration
public interface DataSourceConfig {
	
	DataSource getDataSource();
	
	@Profile("prod")
	@PropertySource("classpath:prod/spring/database.properties")
	class ProdDataSourceConfig implements DataSourceConfig {
		
		@Inject
		private Environment env;
		
		@Bean(destroyMethod = "close")
		@Override
		public DataSource getDataSource() {
			BasicDataSource dataSource = new BasicDataSource();
			
			dataSource.setDriverClassName(env.getRequiredProperty("db.driverClassName"));
			dataSource.setUrl(env.getRequiredProperty("db.url"));
			dataSource.setUsername(env.getRequiredProperty("db.username"));
			dataSource.setPassword(env.getRequiredProperty("db.password"));
			dataSource.setValidationQuery("SELECT 1");
			dataSource.setTestOnBorrow(true);
			
			return dataSource;
		}
		
	}
	
	@Profile("test")
	class TestDataSourceConfig implements DataSourceConfig {
		
		@Bean(destroyMethod = "shutdown")
		@Override
		public DataSource getDataSource() {
			return new EmbeddedDatabaseBuilder()
				.setType(EmbeddedDatabaseType.H2)
				.setName("mystamps")
				.build();
		}
		
	}
	
}

