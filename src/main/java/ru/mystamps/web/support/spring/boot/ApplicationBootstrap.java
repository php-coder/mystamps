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

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ErrorMvcAutoConfiguration;
import org.springframework.boot.context.embedded.FilterRegistrationBean;

import org.togglz.core.context.StaticFeatureManagerProvider;
import org.togglz.core.manager.FeatureManager;

import ru.mystamps.web.config.ApplicationContext;
import ru.mystamps.web.config.DispatcherServletContext;

@EnableAutoConfiguration(exclude = ErrorMvcAutoConfiguration.class)
@Import({
	ApplicationContext.class,
	DispatcherServletContext.class,
	ThymeleafViewResolverInitializingBean.class,
	JettyServletContainerCustomizer.class,
	ErrorPagesServletContainerCustomizer.class
})
public class ApplicationBootstrap {
	
	public static void main(String... args) {
		// @see http://www.slf4j.org/codes.html#loggerNameMismatch
		System.setProperty("slf4j.detectLoggerNameMismatch", "true");
		
		ConfigurableApplicationContext context =
			SpringApplication.run(ApplicationBootstrap.class, args);
		
		FeatureManager featureManager = context.getBean(FeatureManager.class);
		StaticFeatureManagerProvider.setFeatureManager(featureManager);
	}
	
	// TODO: remove @Qualifier and inject by type
	// See for details: https://github.com/spring-projects/spring-boot/issues/2774
	@Bean
	public FilterRegistrationBean getSpringSecurityFilterChainBindedToError(
			@Qualifier("springSecurityFilterChain") Filter springSecurityFilterChain) {
		
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(springSecurityFilterChain);
		registration.setDispatcherTypes(EnumSet.allOf(DispatcherType.class));
		return registration;
	}
	
}
