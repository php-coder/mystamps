/*
 * Copyright (C) 2009-2012 Slava Semushin <slava.semushin@gmail.com>
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

import javax.inject.Inject;

import java.util.Date;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.authentication.encoding.PasswordEncoder;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.mystamps.web.entity.User;
import ru.mystamps.web.entity.UsersActivation;
import ru.mystamps.web.dao.UserDao;
import ru.mystamps.web.dao.UsersActivationDao;
import ru.mystamps.web.service.dto.ActivateAccountDto;
import ru.mystamps.web.service.dto.RegisterAccountDto;

@Service
public class UserService {
	
	private static final Logger LOG = LoggerFactory.getLogger(UserService.class);
	
	@Inject
	private UserDao users;
	
	@Inject
	private UsersActivationDao usersActivation;
	
	@Inject
	private PasswordEncoder encoder;
	
	@Transactional
	public void addRegistrationRequest(RegisterAccountDto dto) {
		Validate.isTrue(dto != null, "DTO should be non null");
		Validate.isTrue(dto.getEmail() != null, "Email should be non null");
		
		UsersActivation activation = new UsersActivation();
		
		activation.setActivationKey(generateActivationKey());
		activation.setEmail(dto.getEmail());
		activation.setCreatedAt(new Date());
		usersActivation.save(activation);
	}
	
	@Transactional(readOnly = true)
	public UsersActivation findRegistrationRequestByActivationKey(String activationKey) {
		Validate.isTrue(activationKey != null, "Activation key should be non null");
		
		return usersActivation.findByActivationKey(activationKey);
	}
	
	@Transactional
	public void registerUser(ActivateAccountDto dto) {
		Validate.isTrue(dto != null, "DTO should be non null");
		Validate.isTrue(dto.getLogin() != null, "Login should be non null");
		Validate.isTrue(dto.getPassword() != null, "Password should be non null");
		Validate.isTrue(dto.getActivationKey() != null, "Activation key should be non null");
		
		String login = dto.getLogin();
		
		// use login as name if name is not provided
		String finalName; // NOPMD: SF #3557789
		if (StringUtils.isEmpty(dto.getName())) {
			finalName = login;
		} else {
			finalName = dto.getName();
		}
		
		String activationKey = dto.getActivationKey();
		UsersActivation activation = usersActivation.findByActivationKey(activationKey);
		if (activation == null) {
			LOG.warn("Cannot find registration request for activation key '{}'", activationKey);
			return;
		}
		
		String email = activation.getEmail();
		Date registrationDate = activation.getCreatedAt();
		
		String salt = generateSalt();
		
		String hash = encoder.encodePassword(dto.getPassword(), salt);
		Validate.validState(hash != null, "Generated hash must be non null");
		
		Date now = new Date();
		
		User user = new User();
		user.setLogin(login);
		user.setName(finalName);
		user.setEmail(email);
		user.setRegisteredAt(registrationDate);
		user.setActivatedAt(now);
		user.setHash(hash);
		user.setSalt(salt);
		
		users.save(user);
		usersActivation.delete(activation);
		
		LOG.info(
			"Added user (login='{}', name='{}', activation key='{}')",
			login,
			finalName,
			activationKey
		);
	}
	
	@Transactional(readOnly = true)
	public User findByLogin(String login) {
		Validate.isTrue(login != null, "Login should be non null");
		
		return users.findByLogin(login);
	}
	
	/**
	 * Generates activation key.
	 * @return string which contains numbers and letters in lower case
	 *         in 10 characters length
	 **/
	private static String generateActivationKey() {
		int actKeyLength = UsersActivation.ACTIVATION_KEY_LENGTH;
		return RandomStringUtils.randomAlphanumeric(actKeyLength).toLowerCase();
	}
	
	/**
	 * Generate password salt.
	 * @return string which contains letters and numbers in 10 characters length
	 **/
	private static String generateSalt() {
		return RandomStringUtils.randomAlphanumeric(User.SALT_LENGTH);
	}
	
}
