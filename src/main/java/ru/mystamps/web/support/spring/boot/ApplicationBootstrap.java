package ru.mystamps.web.support.spring.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Import;

import ru.mystamps.web.config.ApplicationContext;
import ru.mystamps.web.config.DispatcherServletContext;

@EnableAutoConfiguration
@Import({
	ApplicationContext.class,
	DispatcherServletContext.class
})
public class ApplicationBootstrap {
	
	public static void main(String[] args) {
		SpringApplication.run(ApplicationBootstrap.class, args);
	}
	
}
