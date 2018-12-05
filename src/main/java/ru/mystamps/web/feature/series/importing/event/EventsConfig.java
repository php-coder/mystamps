/*
 * Copyright (C) 2009-2018 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.feature.series.importing.event;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.config.ServicesConfig;
import ru.mystamps.web.feature.series.importing.SeriesImportService;
import ru.mystamps.web.feature.series.importing.extractor.JdbcSiteParserDao;
import ru.mystamps.web.feature.series.importing.extractor.JsoupSiteParser;
import ru.mystamps.web.feature.series.importing.extractor.SiteParser;
import ru.mystamps.web.feature.series.importing.extractor.SiteParserDao;
import ru.mystamps.web.feature.series.importing.extractor.SiteParserService;
import ru.mystamps.web.feature.series.importing.extractor.SiteParserServiceImpl;
import ru.mystamps.web.feature.series.importing.extractor.TimedSiteParser;

@Configuration
@RequiredArgsConstructor
public class EventsConfig {
	
	private static final Logger LOG = LoggerFactory.getLogger(EventsConfig.class);
	
	private final SeriesImportService seriesImportService;
	private final ServicesConfig servicesConfig;
	private final ApplicationEventPublisher eventPublisher;
	private final ConfigurableBeanFactory beanFactory;
	private final ConfigurableEnvironment env;
	private final NamedParameterJdbcTemplate jdbcTemplate;
	
	@PostConstruct
	public void init() {
		Map<Integer, SiteParser> parsers = createSiteParsers();
		for (Map.Entry<Integer, SiteParser> entry : parsers.entrySet()) {
			Integer num = entry.getKey();
			SiteParser parser = entry.getValue();
			if (!parser.isFullyInitialized()) {
				LOG.warn("Ignored non-fully initialized site parser (app.site-parser[{}])", num);
				continue;
			}
			LOG.trace("Registering site parser for '{}'", parser);
			beanFactory.registerSingleton("siteParser" + num, parser);
		}
	}
	
	@Bean
	public SiteParserService siteParserService(SiteParserDao siteParserDao) {
		return new SiteParserServiceImpl(
			LoggerFactory.getLogger(SiteParserServiceImpl.class),
			siteParserDao
		);
	}
	
	@Bean
	public SiteParserDao siteParserDao() {
		return new JdbcSiteParserDao(jdbcTemplate);
	}
	
	@Bean
	public ApplicationListener<ImportRequestCreated> getImportRequestCreatedEventListener() {
		return new ImportRequestCreatedEventListener(
			LoggerFactory.getLogger(ImportRequestCreatedEventListener.class),
			servicesConfig.getSeriesDownloaderService(),
			seriesImportService,
			eventPublisher
		);
	}
	
	// This bean has logic that modifies database. To ensure that all migrations have been applied
	// we need this dependency. This annotation shouldn't be needed in Spring Boot 2.
	// TODO: remove this annotation when migration will be completed
	@DependsOn("liquibase")
	@Bean
	public ApplicationListener<DownloadingSucceeded> getDownloadingSucceededEventListener(
		Optional<List<SiteParser>> siteParsers,
		SiteParserService siteParserService
		) {
		
		return new DownloadingSucceededEventListener(
			LoggerFactory.getLogger(DownloadingSucceededEventListener.class),
			seriesImportService,
			siteParserService,
			siteParsers.orElse(Collections.emptyList()),
			eventPublisher
		);
	}
	
	@Bean
	public ApplicationListener<ParsingFailed> getParsingFailedEventListener() {
		return new ParsingFailedEventListener(
			LoggerFactory.getLogger(ParsingFailedEventListener.class),
			seriesImportService
		);
	}
	
	@SuppressWarnings({
		"PMD.AvoidInstantiatingObjectsInLoops",
		"PMD.ModifiedCyclomaticComplexity" // TODO: deal with it someday
	})
	private Map<Integer, SiteParser> createSiteParsers() {
		boolean foundSiteParserProps = false;
		Map<Integer, SiteParser> parsers = new HashMap<>();
		
		for (PropertySource<?> source : env.getPropertySources()) {
			// while we expect that properties will be in PropertiesPropertySource, we use
			// EnumerablePropertySource to also handle systemProperties and systemEnvironment
			// that are MapPropertySource and SystemEnvironmentPropertySource respectively
			if (!(source instanceof EnumerablePropertySource<?>)) {
				LOG.trace("Ignored property source: {} ({})", source.getName(), source.getClass());
				continue;
			}
			
			LOG.trace("Inspecting property source: {} ({})", source.getName(), source.getClass());
			
			for (String name : ((EnumerablePropertySource<?>)source).getPropertyNames()) {
				if (!name.startsWith("app.site-parser")) {
					continue;
				}
				
				String propertyValue = (String)source.getProperty(name);
				LOG.trace("Detected property '{}' with value {}", name, propertyValue);
				
				// extract parser number (app.site-parser[2].name -> 2)
				String strNum = StringUtils.substringBetween(name, "[", "]");
				if (StringUtils.isBlank(strNum)) {
					LOG.warn("Ignored property '{}': could not extract index", name);
					continue;
				}
				
				Integer num = Integer.valueOf(strNum);
				if (!parsers.containsKey(num)) {
					// @todo #801 EventsConfig.createSiteParsers(): split the logic for properties
					//  parsing and the object instantiation
					SiteParser parser =
						new TimedSiteParser(
							LoggerFactory.getLogger(TimedSiteParser.class),
							new JsoupSiteParser()
						);
					parsers.put(num, parser);
				}
				
				// extract parser property (app.site-parser[2].name -> name)
				String fieldName = StringUtils.substringAfterLast(name, ".");
				if (StringUtils.isBlank(fieldName)) {
					LOG.warn("Ignored property '{}': could not extract property name", name);
					continue;
				}
				
				SiteParser parser = parsers.get(num);
				boolean validProperty = parser.setField(fieldName, propertyValue);
				if (!validProperty) {
					LOG.warn("Ignored property '{}': unknown or unsupported", name);
					continue;
				}
				foundSiteParserProps = true;
			}
			// we shouldn't process others to be able to override a property
			if (foundSiteParserProps) {
				break;
			}
		}
		
		return parsers;
	}
	
}
