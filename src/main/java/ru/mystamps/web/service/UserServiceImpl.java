/*
 * Copyright (C) 2009-2017 Slava Semushin <slava.semushin@gmail.com>
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
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.dao.UserDao;
import ru.mystamps.web.dao.dto.AddUserDbDto;
import ru.mystamps.web.dao.dto.UserDetails;
import ru.mystamps.web.dao.dto.UsersActivationDto;
import ru.mystamps.web.service.dto.ActivateAccountDto;

import static ru.mystamps.web.dao.dto.UserDetails.Role.USER;

@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	
	private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);
	
	private final UserDao userDao;
	private final UsersActivationService usersActivationService;
	private final CollectionService collectionService;
	private final PasswordEncoder encoder;
	
	@Override
	@Transactional
	public void registerUser(ActivateAccountDto dto) {
		Validate.isTrue(dto != null, "DTO must be non null");
		Validate.isTrue(dto.getLogin() != null, "Login must be non null");
		Validate.isTrue(dto.getPassword() != null, "Password must be non null");
		Validate.isTrue(dto.getActivationKey() != null, "Activation key must be non null");
		
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
		
		AddUserDbDto user = new AddUserDbDto();
		user.setLogin(login);
		user.setRole(USER);
		user.setName(finalName);
		user.setEmail(email);
		user.setRegisteredAt(registrationDate);
		user.setActivatedAt(now);
		user.setHash(hash);
		
		Integer id = userDao.add(user);
		usersActivationService.remove(activationKey);
		
		LOG.info("User #{} has been created ({})", id, user);
		
		collectionService.createCollection(id, user.getLogin());
	}
	
	@Override
	@Transactional(readOnly = true)
	public UserDetails findUserDetailsByLogin(String login) {
		Validate.isTrue(login != null, "Login must be non null");
		
		return userDao.findUserDetailsByLogin(login);
	}
	
	@Override
	@Transactional(readOnly = true)
	public long countByLogin(String login) {
		Validate.isTrue(login != null, "Login must be non null");
		
		// converting to lowercase to do a case-insensitive search
		String userLogin = login.toLowerCase(Locale.ENGLISH);
		
		return userDao.countByLogin(userLogin);
	}
	
	@Override
	@Transactional(readOnly = true)
	public long countRegisteredSince(Date date) {
		Validate.isTrue(date != null, "Date must be non null");
		
		return userDao.countActivatedSince(date);
	}
	
}
