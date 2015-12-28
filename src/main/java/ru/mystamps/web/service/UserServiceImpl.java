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
package ru.mystamps.web.service;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.dao.JdbcUserDao;
import ru.mystamps.web.dao.UserDao;
import ru.mystamps.web.dao.dto.UsersActivationDto;
import ru.mystamps.web.entity.User;
import ru.mystamps.web.service.dto.ActivateAccountDto;

import static ru.mystamps.web.entity.User.Role.USER;

@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	
	private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);
	
	private final UserDao userDao;
	private final JdbcUserDao jdbcUserDao;
	private final UsersActivationService usersActivationService;
	private final CollectionService collectionService;
	private final PasswordEncoder encoder;
	
	@Override
	@Transactional
	public void registerUser(ActivateAccountDto dto) {
		Validate.isTrue(dto != null, "DTO should be non null");
		Validate.isTrue(dto.getLogin() != null, "Login should be non null");
		Validate.isTrue(dto.getPassword() != null, "Password should be non null");
		Validate.isTrue(dto.getActivationKey() != null, "Activation key should be non null");
		
		String login = dto.getLogin();
		
		// use login as name if name is not provided
		String finalName;
		if (StringUtils.isEmpty(dto.getName())) {
			finalName = login;
		} else {
			finalName = dto.getName();
		}
		
		String activationKey = dto.getActivationKey();
		UsersActivationDto activation = usersActivationService.findByActivationKey(activationKey);
		if (activation == null) {
			LOG.warn("Cannot find registration request for activation key '{}'", activationKey);
			return;
		}
		
		String email = activation.getEmail();
		Date registrationDate = activation.getCreatedAt();
		
		String hash = encoder.encode(dto.getPassword());
		Validate.validState(hash != null, "Generated hash must be non null");
		
		Date now = new Date();
		
		User user = new User();
		user.setLogin(login);
		user.setRole(USER);
		user.setName(finalName);
		user.setEmail(email);
		user.setRegisteredAt(registrationDate);
		user.setActivatedAt(now);
		user.setHash(hash);
		
		user = userDao.save(user);
		usersActivationService.remove(activationKey);
		
		LOG.info("User has been created ({})", user);
		
		collectionService.createCollection(user.getId(), user.getLogin());
	}
	
	@Override
	@Transactional(readOnly = true)
	public User findByLogin(String login) {
		Validate.isTrue(login != null, "Login should be non null");
		
		return userDao.findByLogin(login);
	}
	
	@Override
	@Transactional(readOnly = true)
	public long countByLogin(String login) {
		Validate.isTrue(login != null, "Login should be non null");
		
		return jdbcUserDao.countByLogin(login);
	}
	
}
