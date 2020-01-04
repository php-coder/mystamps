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
package ru.mystamps.web.feature.account;

import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.mystamps.web.feature.collection.CollectionService;
import ru.mystamps.web.feature.site.MailService;

/**
 * Spring configuration that is required for having user accounts in an application.
 *
 * The beans are grouped into two classes to make possible to register a controller
 * and the services in the separated application contexts.
 */
@Configuration
public class AccountConfig {
	
	@RequiredArgsConstructor
	public static class Controllers {
		
		private final UserService userService;
		private final UsersActivationService usersActivationService;
		
		@Bean
		public AccountController accountController() {
			return new AccountController(userService, usersActivationService);
		}
		
	}
	
	@RequiredArgsConstructor
	public static class Services {
		
		private final NamedParameterJdbcTemplate jdbcTemplate;
		
		@Bean
		public UserService userService(
			UserDao userDao,
			UsersActivationService usersActivationService,
			CollectionService collectionService,
			PasswordEncoder encoder) {
			
			return new UserServiceImpl(
				LoggerFactory.getLogger(UserServiceImpl.class),
				userDao,
				usersActivationService,
				collectionService,
				encoder
			);
		}
		
		@Bean
		public UsersActivationService usersActivationService(
			UsersActivationDao usersActivationDao,
			@Lazy MailService mailService) {
			
			return new UsersActivationServiceImpl(
				LoggerFactory.getLogger(UsersActivationServiceImpl.class),
				usersActivationDao,
				mailService
			);
		}
		
		@Bean
		public UserDao userDao() {
			return new JdbcUserDao(jdbcTemplate);
		}
		
		@Bean
		public UsersActivationDao usersActivationDao() {
			return new JdbcUsersActivationDao(jdbcTemplate);
		}
		
	}
	
}
