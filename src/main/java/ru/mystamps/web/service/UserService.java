/*
 * Copyright (C) 2009-2011 Slava Semushin <slava.semushin@gmail.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

package ru.mystamps.web.service;

import javax.inject.Inject;

import java.util.Date;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.google.common.base.Preconditions.checkArgument;

import ru.mystamps.web.entity.User;
import ru.mystamps.web.entity.UsersActivation;
import ru.mystamps.web.dao.UserDao;
import ru.mystamps.web.dao.UsersActivationDao;

@Service
public class UserService {
	
	private final Logger log = LoggerFactory.getLogger(UserService.class);
	
	@Inject
	private UserDao users;
	
	@Inject
	private UsersActivationDao usersActivation;
	
	@Transactional
	public void addRegistrationRequest(final String email) {
		checkArgument(email != null, "Email should be non null");
		
		final UsersActivation activation = new UsersActivation();
		
		activation.setActivationKey(generateActivationKey());
		activation.setEmail(email);
		activation.setCreatedAt(new Date());
		usersActivation.add(activation);
	}
	
	@Transactional(readOnly = true)
	public UsersActivation findRegistrationRequestByActivationKey(
			final String activationKey) {
		
		checkArgument(activationKey != null, "Activation key should be non null");
		
		return usersActivation.findByActivationKey(activationKey);
	}
	
	@Transactional
	public void registerUser(final String login, final String password,
			final String name, final String activationKey) {
		
		checkArgument(login != null, "Login should be non null");
		checkArgument(password != null, "Password should be non null");
		checkArgument(name != null, "Name should be non null");
		checkArgument(activationKey != null, "Activation key should be non null");
		
		// use login as name if name is not provided
		final String finalName;
		if ("".equals(name)) {
			finalName = login;
		} else {
			finalName = name;
		}
		
		final UsersActivation activation =
			usersActivation.findByActivationKey(activationKey);
		if (activation == null) {
			log.warn("Cannot find registration request for activation key '{}'", activationKey);
			return;
		}
		
		final String email = activation.getEmail();
		final Date registrationDate = activation.getCreatedAt();
		
		final String salt = generateSalt();
		final String hash = computeSha1Sum(salt + password);
		final Date currentDate = new Date();
		
		final User user = new User();
		user.setLogin(login);
		user.setName(finalName);
		user.setEmail(email);
		user.setRegisteredAt(registrationDate);
		user.setActivatedAt(currentDate);
		user.setHash(hash);
		user.setSalt(salt);
		
		users.add(user);
		usersActivation.delete(activation);
		
		log.info(
			"Added user (login='{}', name='{}', activation key='{}')",
			new Object[]{login, finalName, activationKey}
		);
	}
	
	@Transactional(readOnly = true)
	public User findByLogin(final String login) {
		checkArgument(login != null, "Login should be non null");
		
		return users.findByLogin(login);
	}
	
	@Transactional(readOnly = true)
	public User findByLoginAndPassword(final String login, final String password) {
		checkArgument(login != null, "Login should be non null");
		checkArgument(password != null, "Password should be non null");
		
		final User user = users.findByLogin(login);
		if (user == null) {
			log.info("Wrong login '{}'", login);
			return null;
		}
		
		final String hash = user.getHash();
		if (hash == null) {
			log.warn("User with login '{}' and id={} has null hash!", login, user.getId());
			return null;
		}
		
		final String salt = user.getSalt();
		if (salt == null) {
			log.warn("User with login '{}' and id={} has null salt!", login, user.getId());
			return null;
		}
		
		if (!hash.equals(computeSha1Sum(salt + password))) {
			log.info("Wrong password for login '{}'", login);
			return null;
		}
		
		log.info("Valid credentials for login '{}'. User has id = {}", login, user.getId());
		
		return user;
	}
	
	/**
	 * Generates activation key.
	 * @return string which contains numbers and letters in lower case
	 *         in 10 characters length
	 **/
	private static String generateActivationKey() {
		final int actKeyLength = UsersActivation.ACTIVATION_KEY_LENGTH;
		return RandomStringUtils.randomAlphanumeric(actKeyLength).toLowerCase();
	}
	
	/**
	 * Generate password salt.
	 * @return string which contains letters and numbers in 10 characters length
	 **/
	private String generateSalt() {
		return RandomStringUtils.randomAlphanumeric(User.SALT_LENGTH);
	}
	
	private String computeSha1Sum(final String string) {
		return Hex.encodeHexString(DigestUtils.sha(string));
	}
	
}
