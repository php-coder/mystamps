package ru.mystamps.web.support.spring.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;

import org.togglz.core.context.StaticFeatureManagerProvider;
import org.togglz.core.manager.FeatureManager;

import ru.mystamps.web.config.ApplicationContext;
import ru.mystamps.web.config.DispatcherServletContext;

@EnableAutoConfiguration
@Import({
	ApplicationContext.class,
	DispatcherServletContext.class
})
public class ApplicationBootstrap {
	
	public static void main(String[] args) {
		ConfigurableApplicationContext context =
			SpringApplication.run(ApplicationBootstrap.class, args);
		
		FeatureManager featureManager = context.getBean(FeatureManager.class);
		StaticFeatureManagerProvider.setFeatureManager(featureManager);
	}
	
}
