/*
 * Copyright (C) 2009-2021 Slava Semushin <slava.semushin@gmail.com>
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

import liquibase.exception.LiquibaseException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;
import org.togglz.core.context.StaticFeatureManagerProvider;
import org.togglz.core.manager.FeatureManager;
import ru.mystamps.web.config.ApplicationContext;
import ru.mystamps.web.config.DispatcherServletContext;
import ru.mystamps.web.support.liquibase.LiquibaseSupport;

// PMD: "All methods are static" here because it's a program entry point.
// CheckStyle: I cannot declare the constructor as private because app won't start.
@SuppressWarnings({ "PMD.UseUtilityClass", "checkstyle:hideutilityclassconstructor" })
public class ApplicationBootstrap {
	
	public static void main(String... args) throws LiquibaseException {
		// When the application is started as
		//
		//     java -jar target/mystamps.war liquibase validate
		// or
		//    ./mvnw spring-boot:run -Dspring-boot.run.arguments='liquibase,validate'
		//
		// we don't run a full application but loads only Liquibase-related classes
		boolean executeOnlyLiquibase = args.length == 2
			&& "liquibase".equals(args[0])
			&& "validate".equals(args[1]);
		if (executeOnlyLiquibase) {
			ConfigurableApplicationContext context = LiquibaseSupport
				.createSpringApplication()
				.run(args);
			
			LiquibaseSupport.validate(context);
			return;
		}
		
		ConfigurableApplicationContext context =
			SpringApplication.run(DefaultStartup.class, args);
		
		FeatureManager featureManager = context.getBean(FeatureManager.class);
		StaticFeatureManagerProvider.setFeatureManager(featureManager);
	}

	@EnableAutoConfiguration
	@Import({
		ApplicationContext.class,
		DispatcherServletContext.class,
		ThymeleafViewResolverInitializingBean.class,
		JettyWebServerFactoryCustomizer.class,
		ErrorPagesCustomizer.class
	})
	public static class DefaultStartup {
	}
	
}
