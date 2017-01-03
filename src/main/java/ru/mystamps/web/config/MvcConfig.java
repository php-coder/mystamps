/*
 * Copyright (C) 2009-2017 Slava Semushin <slava.semushin@gmail.com>
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
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.CacheControl;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.resource.VersionResourceResolver;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.Url;
import ru.mystamps.web.controller.converter.LinkEntityDtoGenericConverter;
import ru.mystamps.web.support.spring.security.CurrentUserArgumentResolver;

@Configuration
@EnableScheduling
@Import(ControllersConfig.class)
@RequiredArgsConstructor
public class MvcConfig extends WebMvcConfigurerAdapter {
	
	private final ServicesConfig servicesConfig;
	
	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addConverter(
			new LinkEntityDtoGenericConverter(
				servicesConfig.getCategoryService(),
				servicesConfig.getCountryService()
			)
		);
	}
	
	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}
	
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController(Url.AUTHENTICATION_PAGE);
		registry.addViewController(Url.FORBIDDEN_PAGE);
	}
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		VersionResourceResolver resourceResolver = new VersionResourceResolver()
			.addFixedVersionStrategy(Url.RESOURCES_VERSION, "/**");
		
		@SuppressWarnings("checkstyle:magicnumber")
		CacheControl cacheControl = CacheControl.maxAge(7, TimeUnit.DAYS);
		
		registry.addResourceHandler("/static/**")
			.addResourceLocations("/WEB-INF/static/")
			.setCacheControl(cacheControl)
			.resourceChain(true)
			.addResolver(resourceResolver);
		registry.addResourceHandler("/public/js/**")
			.addResourceLocations("classpath:/js/")
			.resourceChain(true)
			.addResolver(resourceResolver);
		
		// For WebJars:
		registry.addResourceHandler("/public/bootstrap/**")
			.addResourceLocations("classpath:/META-INF/resources/webjars/bootstrap/");
		registry.addResourceHandler("/public/jquery/**")
			.addResourceLocations("classpath:/META-INF/resources/webjars/jquery/");
		registry.addResourceHandler("/public/selectize/**")
			.addResourceLocations("classpath:/META-INF/resources/webjars/selectize.js/");
	}
	
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.add(new CurrentUserArgumentResolver());
	}
	
	@Override
	public Validator getValidator() {
		ReloadableResourceBundleMessageSource messageSource =
			new ReloadableResourceBundleMessageSource();
		messageSource.setBasename("classpath:ru/mystamps/i18n/ValidationMessages");
		messageSource.setFallbackToSystemLocale(false);
		
		LocalValidatorFactoryBean factory = new LocalValidatorFactoryBean();
		factory.setValidationMessageSource(messageSource);
		
		return factory;
	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
		interceptor.setParamName("lang");
		
		registry.addInterceptor(interceptor);
	}
	
	@Override
	public void configurePathMatch(PathMatchConfigurer configurer) {
		// If enabled a method mapped to "/users" also matches to "/users/"
		configurer.setUseTrailingSlashMatch(false);
		// If enabled a method mapped to "/users" also matches to "/users.*"
		configurer.setUseSuffixPatternMatch(false);
	}
	
	@Bean(name = "localeResolver")
	public LocaleResolver getLocaleResolver() {
		SessionLocaleResolver resolver = new SessionLocaleResolver();
		resolver.setDefaultLocale(Locale.ENGLISH);
		return resolver;
	}
	
}
