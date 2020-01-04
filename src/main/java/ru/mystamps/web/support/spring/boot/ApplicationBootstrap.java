/*
 * Copyright (C) 2009-2020 Slava Semushin <slava.semushin@gmail.com>
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

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;
import org.togglz.core.context.StaticFeatureManagerProvider;
import org.togglz.core.manager.FeatureManager;
import ru.mystamps.web.config.ApplicationContext;
import ru.mystamps.web.config.DispatcherServletContext;

// PMD: "All methods are static" here because it's a program entry point.
// CheckStyle: I cannot declare the constructor as private because app won't start.
@SuppressWarnings({ "PMD.UseUtilityClass", "checkstyle:hideutilityclassconstructor" })
@EnableAutoConfiguration
@Import({
	ApplicationContext.class,
	DispatcherServletContext.class,
	ThymeleafViewResolverInitializingBean.class,
	JettyWebServerFactoryCustomizer.class,
	ErrorPagesCustomizer.class
})
public class ApplicationBootstrap {
	
	public static void main(String... args) {
		System.setProperty("java.awt.headless", "true");
		
		ConfigurableApplicationContext context =
			SpringApplication.run(ApplicationBootstrap.class, args);
		
		FeatureManager featureManager = context.getBean(FeatureManager.class);
		StaticFeatureManagerProvider.setFeatureManager(featureManager);
	}
	
}
