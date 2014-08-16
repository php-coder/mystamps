/*
 * Copyright (C) 2009-2014 Slava Semushin <slava.semushin@gmail.com>
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

import javax.inject.Inject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
@PropertySource("classpath:${spring.profiles.active}/spring/mail.properties")
public class MailConfig {
	
	@Inject
	private Environment env;
	
	@Bean
	public JavaMailSender getMailSender() {
		JavaMailSenderImpl mailer = new JavaMailSenderImpl();
		mailer.setHost(env.getRequiredProperty("mail.smtp.host"));
		mailer.setPort(env.getRequiredProperty("mail.smtp.port", Integer.class));
		mailer.setUsername(env.getRequiredProperty("mail.smtp.login"));
		mailer.setPassword(env.getRequiredProperty("mail.smtp.password"));
		
		return mailer;
	}
	
}
