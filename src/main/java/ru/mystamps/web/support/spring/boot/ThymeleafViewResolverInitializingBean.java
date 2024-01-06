/*
 * Copyright (C) 2009-2024 Slava Semushin <slava.semushin@gmail.com>
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

import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import ru.mystamps.web.feature.account.AccountUrl;
import ru.mystamps.web.feature.category.CategoryUrl;
import ru.mystamps.web.feature.collection.CollectionUrl;
import ru.mystamps.web.feature.country.CountryUrl;
import ru.mystamps.web.feature.image.ImageUrl;
import ru.mystamps.web.feature.participant.ParticipantUrl;
import ru.mystamps.web.feature.report.ReportUrl;
import ru.mystamps.web.feature.series.SeriesUrl;
import ru.mystamps.web.feature.series.importing.SeriesImportUrl;
import ru.mystamps.web.feature.series.importing.sale.SeriesSalesImportUrl;
import ru.mystamps.web.feature.site.ResourceUrl;
import ru.mystamps.web.feature.site.SiteUrl;

import java.util.HashMap;
import java.util.Map;

/**
 * Adjusts {@link ThymeleafViewResolver} instance by setting static variables.
 *
 * @see <a href="https://github.com/spring-projects/spring-boot/issues/3037">spring-boot#3037</a>
 **/
@Configuration
@Setter
public class ThymeleafViewResolverInitializingBean
	implements InitializingBean, ApplicationContextAware, EnvironmentAware {
	
	private ApplicationContext applicationContext;
	private Environment environment;
	
	@Override
	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	public void afterPropertiesSet() throws Exception {
		ThymeleafViewResolver viewResolver =
			applicationContext.getBean(ThymeleafViewResolver.class);
		
		Profiles prod = Profiles.of("prod");
		boolean productionEnv = environment.acceptsProfiles(prod);
		boolean useCdn = environment.getProperty("app.use-cdn", Boolean.class, Boolean.TRUE);
		viewResolver.setStaticVariables(resourcesAsMap(productionEnv, useCdn));
	}

	// Not all URLs are exported here but only those that are being used on views
	private Map<String, ?> resourcesAsMap(boolean production, boolean useCdn) {
		Map<String, String> map = new HashMap<>();
		
		map.put("PUBLIC_URL", production ? SiteUrl.PUBLIC_URL : SiteUrl.SITE);
		
		AccountUrl.exposeUrlsToView(map);
		CategoryUrl.exposeUrlsToView(map);
		CountryUrl.exposeUrlsToView(map);
		CollectionUrl.exposeUrlsToView(map);
		ParticipantUrl.exposeUrlsToView(map);
		ReportUrl.exposeUrlsToView(map);
		ResourceUrl.exposeUrlsToView(map);
		SeriesUrl.exposeUrlsToView(map);
		SeriesImportUrl.exposeUrlsToView(map);
		SeriesSalesImportUrl.exposeUrlsToView(map);
		SiteUrl.exposeUrlsToView(map);
		
		String resourcesHost = production ? ResourceUrl.STATIC_RESOURCES_URL : null;
		ImageUrl.exposeResourcesToView(map, resourcesHost);
		ResourceUrl.exposeResourcesToView(map, resourcesHost);
		
		ResourceUrl.exposeWebjarResourcesToView(map, useCdn);

		return map;
	}
	
}
