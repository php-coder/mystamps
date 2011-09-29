/*
 * Copyright (C) 2011 Slava Semushin <slava.semushin@gmail.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

package ru.mystamps.web.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableTransactionManagement
@PropertySource("classpath:spring/database.properties")
public class DbConfig {
	
	@Value("${jpa.showSql}")
	private String showSql;
	
	@Value("${jpa.dialectClassName}")
	private String dialectClassName;
	
	@Value("${hibernate.formatSql}")
	private String formatSql;
	
	@Value("${hibernate.hbm2ddl.auto}")
	private String hbm2ddl;
	
	@Autowired
	private DataSource dataSource;
	
	@Bean
	public JpaVendorAdapter getJpaVendorAdapter() {
		final HibernateJpaVendorAdapter jpaVendorAdapter =
			new HibernateJpaVendorAdapter();
		
		jpaVendorAdapter.setDatabasePlatform(dialectClassName);
		jpaVendorAdapter.setShowSql(Boolean.valueOf(showSql));
		
		return jpaVendorAdapter;
	}
	
	@Bean
	public LocalContainerEntityManagerFactoryBean getEntityManagerFactory() {
		final LocalContainerEntityManagerFactoryBean entityManagerFactory =
			new LocalContainerEntityManagerFactoryBean();
		
		entityManagerFactory.setJpaVendorAdapter(getJpaVendorAdapter());
		entityManagerFactory.setDataSource(dataSource);
		
		final Map<String, String> jpaProperties = new HashMap<String, String>();
		jpaProperties.put("hibernate.format_sql", formatSql);
		jpaProperties.put("hibernate.connection.charset", "UTF-8");
		jpaProperties.put("hibernate.hbm2ddl.auto", hbm2ddl);
		entityManagerFactory.setJpaPropertyMap(jpaProperties);
		
		return entityManagerFactory;
	}
	
	@Bean
	public PlatformTransactionManager getTransactionManager() {
		final JpaTransactionManager transactionManager =
			new JpaTransactionManager();
		
		transactionManager.setEntityManagerFactory(getEntityManagerFactory().getObject());
		
		return transactionManager;
	}
	
}
