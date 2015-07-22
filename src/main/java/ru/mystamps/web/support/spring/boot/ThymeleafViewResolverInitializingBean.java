/*
 * Copyright (C) 2009-2015 Slava Semushin <slava.semushin@gmail.com>
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import org.thymeleaf.spring4.view.ThymeleafViewResolver;

import lombok.Setter;

import ru.mystamps.web.Url;

/**
 * Adjusts {@link ThymeleafViewResolver} instance by setting static variables.
 *
 * @see <a href="https://github.com/spring-projects/spring-boot/issues/3037">spring-boot#3037</a>
 **/
@Configuration
@Setter
public class ThymeleafViewResolverInitializingBean
	implements InitializingBean, ApplicationContextAware, EnvironmentAware {
	
	private static final Logger LOG =
		LoggerFactory.getLogger(ThymeleafViewResolverInitializingBean.class);
	
	private ApplicationContext applicationContext;
	private Environment environment;
	
	@Override
	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	public void afterPropertiesSet() throws Exception {
		ThymeleafViewResolver viewResolver =
			applicationContext.getBean(ThymeleafViewResolver.class);
		if (viewResolver == null) {
			LOG.warn(// NOPMD: GuardLogStatement
				"Cannot adjust ThymeleafViewResolver: "
				+ "bean of this type wasn't found in application context"
			);
			return;
		}
		
		boolean useSingleHost = !environment.acceptsProfiles("prod");
		viewResolver.setStaticVariables(Url.asMap(useSingleHost));
	}
	
}
