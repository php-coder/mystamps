/*
 * Copyright (C) 2011-2012 Slava Semushin <slava.semushin@gmail.com>
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

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.Validator;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.ViewResolver;

import ru.mystamps.web.support.spring.security.CustomUserDetailsArgumentResolver;
import ru.mystamps.web.Url;

@Configuration
@EnableWebMvc
@EnableScheduling
@ComponentScan(basePackages = {
	"ru.mystamps.web.controller"
})
public class MvcConfig extends WebMvcConfigurerAdapter {
	
	@Override
	public void configureDefaultServletHandling(final DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}
	
	@Override
	public void addViewControllers(final ViewControllerRegistry registry) {
		registry.addViewController(Url.AUTHENTICATION_PAGE);
		registry.addViewController(Url.INDEX_PAGE).setViewName("site/index");
		registry.addViewController(Url.RESTORE_PASSWORD_PAGE);
		registry.addViewController(Url.SUCCESSFUL_ACTIVATION_PAGE);
		registry.addViewController(Url.SUCCESSFUL_REGISTRATION_PAGE);
	}
	
	@Override
	public void addResourceHandlers(final ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/static/**").addResourceLocations("/WEB-INF/static/*");
	}
	
	@Override
	public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.add(new CustomUserDetailsArgumentResolver());
	}
	
	@Override
	public Validator getValidator() {
		final ReloadableResourceBundleMessageSource messageSource =
			new ReloadableResourceBundleMessageSource();
		messageSource.setBasename("classpath:ru/mystamps/i18n/ValidationMessages");
		
		final LocalValidatorFactoryBean factory = new LocalValidatorFactoryBean();
		factory.setValidationMessageSource(messageSource);
		
		return factory;
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
	
	@Bean(name = "multipartResolver")
	public MultipartResolver getMultipartResolver() {
		return new CommonsMultipartResolver();
	}

}
