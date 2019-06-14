/*
 * Copyright (C) 2009-2019 Slava Semushin <slava.semushin@gmail.com>
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

import lombok.RequiredArgsConstructor;
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
import org.springframework.web.servlet.HandlerInterceptor;
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
import ru.mystamps.web.Url;
import ru.mystamps.web.feature.account.AccountConfig;
import ru.mystamps.web.feature.account.AccountUrl;
import ru.mystamps.web.feature.category.CategoryConfig;
import ru.mystamps.web.feature.category.CategoryLinkEntityDtoConverter;
import ru.mystamps.web.feature.category.CategoryService;
import ru.mystamps.web.feature.collection.CollectionConfig;
import ru.mystamps.web.feature.country.CountryConfig;
import ru.mystamps.web.feature.country.CountryLinkEntityDtoConverter;
import ru.mystamps.web.feature.country.CountryService;
import ru.mystamps.web.feature.image.ImageConfig;
import ru.mystamps.web.feature.participant.ParticipantConfig;
import ru.mystamps.web.feature.report.ReportConfig;
import ru.mystamps.web.feature.series.DownloadImageInterceptor;
import ru.mystamps.web.feature.series.SeriesConfig;
import ru.mystamps.web.feature.series.importing.SeriesImportConfig;
import ru.mystamps.web.feature.series.importing.event.EventsConfig;
import ru.mystamps.web.feature.series.importing.sale.SeriesSalesImportConfig;
import ru.mystamps.web.feature.site.SiteConfig;
import ru.mystamps.web.support.spring.mvc.RestExceptionHandler;
import ru.mystamps.web.support.spring.security.CurrentUserArgumentResolver;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableScheduling
@Import({
	AccountConfig.Controllers.class,
	CategoryConfig.Controllers.class,
	CollectionConfig.Controllers.class,
	CountryConfig.Controllers.class,
	ImageConfig.Controllers.class,
	ParticipantConfig.Controllers.class,
	ReportConfig.Controllers.class,
	SeriesConfig.Controllers.class,
	SeriesImportConfig.Controllers.class,
	SeriesSalesImportConfig.Controllers.class,
	SiteConfig.Controllers.class,
	EventsConfig.class,
})
@RequiredArgsConstructor
public class MvcConfig extends WebMvcConfigurerAdapter {
	
	private final ServicesConfig servicesConfig;
	private final CategoryService categoryService;
	private final CountryService countryService;
	
	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addConverter(new CategoryLinkEntityDtoConverter(categoryService));
		registry.addConverter(new CountryLinkEntityDtoConverter(countryService));
	}
	
	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}
	
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController(AccountUrl.AUTHENTICATION_PAGE);
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
		registry.addInterceptor(getLocaleChangeInterceptor());
		
		registry
			.addInterceptor(getDownloadImageInterceptor())
			.addPathPatterns(
				Url.ADD_SERIES_PAGE,
				Url.ADD_IMAGE_SERIES_PAGE,
				Url.REQUEST_IMPORT_PAGE.replace("{id}", "*")
			);
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
	
	@Bean
	public RestExceptionHandler restExceptionHandler() {
		return new RestExceptionHandler();
	}
	
	private static HandlerInterceptor getLocaleChangeInterceptor() {
		LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
		interceptor.setParamName("lang");
		return interceptor;
	}
	
	private HandlerInterceptor getDownloadImageInterceptor() {
		return new DownloadImageInterceptor(servicesConfig.getImageDownloaderService());
	}
	
}
