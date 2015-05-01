package ru.mystamps.web.support.spring.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

@EnableAutoConfiguration
public class ApplicationBootstrap {
	
	public static void main(String[] args) {
		SpringApplication.run(ApplicationBootstrap.class, args);
	}
	
}
