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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceConfigurer;
import org.springframework.web.servlet.config.annotation.ViewControllerConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.ViewResolver;

import static ru.mystamps.web.SiteMap.INDEX_PAGE_URL;
import static ru.mystamps.web.SiteMap.MAINTENANCE_PAGE_URL;
import static ru.mystamps.web.SiteMap.RESTORE_PASSWORD_PAGE_URL;
import static ru.mystamps.web.SiteMap.SUCCESSFUL_ACTIVATION_PAGE_URL;
import static ru.mystamps.web.SiteMap.SUCCESSFUL_REGISTRATION_PAGE_URL;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {
	"ru.mystamps.web.controller",
	"ru.mystamps.web.dao",
	"ru.mystamps.web.service",
	"ru.mystamps.web.validation"
})
public class MvcConfig extends WebMvcConfigurerAdapter {
	
	@Override
	public void configureDefaultServletHandling(final DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}
	
	@Override
	public void configureViewControllers(final ViewControllerConfigurer configurer) {
		configurer.mapViewName(INDEX_PAGE_URL, "site/index");
		configurer.mapViewName(MAINTENANCE_PAGE_URL, "site/maintenance");
		configurer.mapViewName(RESTORE_PASSWORD_PAGE_URL, "password/restore");
		configurer.mapViewName(SUCCESSFUL_ACTIVATION_PAGE_URL, "account/activation_successful");
		configurer.mapViewName(SUCCESSFUL_REGISTRATION_PAGE_URL, "account/activation_sent");
	}
	
	@Override
	public void configureResourceHandling(final ResourceConfigurer configurer) {
		configurer.addPathMapping("/static/**").addResourceLocation("/WEB-INF/static/*");
	}
	
	@Bean
	public ViewResolver getViewResolver() {
		final InternalResourceViewResolver viewResolver =
			new InternalResourceViewResolver();
		
		viewResolver.setPrefix("/WEB-INF/pages/");
		viewResolver.setSuffix(".jsp");
		
		return viewResolver;
	}
	
	@Bean(name = "messageSource")
	MessageSource getMessageSource() {
		final ReloadableResourceBundleMessageSource messageSource =
			new ReloadableResourceBundleMessageSource();
		
		messageSource.setBasename("classpath:ru/mystamps/i18n/Messages");
		messageSource.setDefaultEncoding("UTF-8");
		
		return messageSource;
	}
	
}
