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
package ru.mystamps.web.config;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories("ru.mystamps.web.dao")
@PropertySource("classpath:${spring.profiles.active}/spring/database.properties")
public class DbConfig {
	
	@Inject
	private Environment env;
	
	@Inject
	private DataSource dataSource;
	
	@Inject
	private JpaVendorAdapter jpaVendorAdapter;
	
	// Explicitly specified bean names which will be looking by Spring Data
	
	@Bean(name = "entityManagerFactory")
	public LocalContainerEntityManagerFactoryBean getEntityManagerFactory() {
		LocalContainerEntityManagerFactoryBean entityManagerFactory =
			new LocalContainerEntityManagerFactoryBean();
		
		entityManagerFactory.setJpaVendorAdapter(jpaVendorAdapter);
		entityManagerFactory.setDataSource(dataSource);
		entityManagerFactory.setJpaPropertyMap(getJpaProperties());
		
		return entityManagerFactory;
	}
	
	private Map<String, String> getJpaProperties() {
		Map<String, String> jpaProperties = new HashMap<>();
		jpaProperties.put(
			"hibernate.format_sql",
			env.getRequiredProperty("hibernate.formatSql")
		);
		jpaProperties.put(
			"hibernate.connection.charset",
			"UTF-8"
		);
		jpaProperties.put(
			"hibernate.hbm2ddl.auto",
			env.getRequiredProperty("hibernate.hbm2ddl.auto")
		);
		
		return jpaProperties;
	}
	
	
}
