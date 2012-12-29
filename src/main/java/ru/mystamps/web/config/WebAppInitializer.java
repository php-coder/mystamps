/*
 * Copyright (C) 2009-2013 Slava Semushin <slava.semushin@gmail.com>
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

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

public class WebAppInitializer implements WebApplicationInitializer {
	
	@Override
	public void onStartup(ServletContext container) throws ServletException {
		setupContextLoaderListener(container);
		setupDispatcherServlet(container);
		
		setupRequestContextListener(container);
		
		setupCharsetFilter(container);
		setupSpringSecurityFilter(container);
	}

	/**
	 * Sets up application context.
	 *
	 * Equivalent for the following XML-configuration:
	 *
	 * <pre>
	 * {@code
	 *     <context-param>
	 *         <param-name>contextClass</param-name>
	 *         <param-value>org.springframework.web.context.support.AnnotationConfigWebApplicationContext</param-value>
	 *     </context-param>
	 *
	 *     <context-param>
	 *         <param-name>contextConfigLocation</param-name>
	 *         <param-value>ru.mystamps.web.config.ApplicationContext</param-value>
	 *      </context-param>
	 *
	 *     <listener>
	 *         <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
	 *     </listener>
	 * }
	 * </pre>
	 **/
	private static void setupContextLoaderListener(ServletContext servletContext) {
		AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
		ctx.register(ApplicationContext.class);
		
		servletContext.addListener(new ContextLoaderListener(ctx));
	}

	/**
	 * Sets up dispatcher servlet.
	 *
	 * Equivalent for the following XML-configuration:
	 *
	 * <pre>
	 * {@code
	 *     <servlet>
	 *         <servlet-name>spring</servlet-name>
	 *         <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
	 *         <init-param>
	 *             <param-name>contextClass</param-name>
	 *             <param-value>org.springframework.web.context.support.AnnotationConfigWebApplicationContext</param-value>
	 *         </init-param>
	 *         <init-param>
	 *             <param-name>contextConfigLocation</param-name>
	 *             <param-value>ru.mystamps.web.config.DispatcherServletContext</param-value>
	 *         </init-param>
	 *         <load-on-startup>1</load-on-startup>
	 *     </servlet>
	 *
	 *     <servlet-mapping>
	 *         <servlet-name>spring</servlet-name>
	 *         <url-pattern>/</url-pattern>
	 *     </servlet-mapping>
	 * }
	 * </pre>
	 **/
	private static void setupDispatcherServlet(ServletContext servletContext) {
		AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
		ctx.register(DispatcherServletContext.class);
		
		ServletRegistration.Dynamic dispatcherServlet =
			servletContext.addServlet("spring", new DispatcherServlet(ctx));
		dispatcherServlet.setLoadOnStartup(1);
		dispatcherServlet.addMapping("/");
	}

	/**
	 * Sets up request context listener.
	 *
	 * Equivalent for the following XML-configuration:
	 *
	 * <pre>
	 * {@code
	 *     <listener>
	 *         <listener-class>org.springframework.web.context.request.RequestContextListener</listener-class>
	 *     </listener>
	 * }
	 * </pre>
	 **/
	private static void setupRequestContextListener(ServletContext servletContext) {
		// To expose user's request to AuthenticationFailureListener where we need it for logging
		servletContext.addListener(new RequestContextListener());
	}

	/**
	 * Sets up charset filter.
	 *
	 * Equivalent for the following XML-configuration:
	 *
	 * <pre>
	 * {@code
	 *     <filter>
	 *         <filter-name>charsetFilter</filter-name>
	 *         <filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
	 *         <init-param>
	 *             <param-name>encoding</param-name>
	 *             <param-value>UTF-8</param-value>
	 *         </init-param>
	 *     </filter>
	 *
	 *     <filter-mapping>
	 *         <filter-name>charsetFilter</filter-name>
	 *         <url-pattern>/*</url-pattern>
	 *     </filter-mapping>
	 * }
	 * </pre>
	 **/
	private void setupCharsetFilter(ServletContext servletContext) {
		FilterRegistration.Dynamic charsetFilter =
			servletContext.addFilter("charsetFilter", new CharacterEncodingFilter());
		charsetFilter.setInitParameter("encoding", "UTF-8");
		charsetFilter.addMappingForUrlPatterns(null, true, "/*");
	}

	/**
	 * Sets up Spring Security filter.
	 *
	 * Equivalent for the following XML-configuration:
	 *
	 * <pre>
	 * {@code
	 *     <filter>
	 *         <filter-name>springSecurityFilterChain</filter-name>
	 *         <filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
	 *     </filter>
	 *
	 *     <filter-mapping>
	 *         <filter-name>springSecurityFilterChain</filter-name>
	 *         <url-pattern>/*</url-pattern>
	 *         <dispatcher>REQUEST</dispatcher>
	 *         <dispatcher>ERROR</dispatcher>
	 *     </filter-mapping>
	 * }
	 * </pre>
	 **/
	private void setupSpringSecurityFilter(ServletContext servletContext) {
		FilterRegistration.Dynamic securityFilter =
			servletContext.addFilter("springSecurityFilterChain", new DelegatingFilterProxy());

		securityFilter.addMappingForUrlPatterns(
		   EnumSet.of(DispatcherType.REQUEST, DispatcherType.ERROR),
		   true,
		   "/*"
		);
	}

}
