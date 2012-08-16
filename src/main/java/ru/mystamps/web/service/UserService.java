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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Strings.isNullOrEmpty;

import ru.mystamps.web.entity.User;
import ru.mystamps.web.entity.UsersActivation;
import ru.mystamps.web.dao.UserDao;
import ru.mystamps.web.dao.UsersActivationDao;
import ru.mystamps.web.support.spring.security.CustomUserDetails;

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
	public void addRegistrationRequest(final String email) {
		checkArgument(email != null, "Email should be non null");
		
		final UsersActivation activation = new UsersActivation();
		
		activation.setActivationKey(generateActivationKey());
		activation.setEmail(email);
		activation.setCreatedAt(new Date());
		usersActivation.save(activation);
	}
	
	@Transactional(readOnly = true)
	public UsersActivation findRegistrationRequestByActivationKey(
			final String activationKey) {
		
		checkArgument(activationKey != null, "Activation key should be non null");
		
		return usersActivation.findByActivationKey(activationKey);
	}
	
	@Transactional
	@SuppressWarnings("PMD.UseObjectForClearerAPI")
	public void registerUser(final String login, final String password,
			final String name, final String activationKey) {
		
		checkArgument(login != null, "Login should be non null");
		checkArgument(password != null, "Password should be non null");
		checkArgument(activationKey != null, "Activation key should be non null");
		
		// use login as name if name is not provided
		final String finalName; // NOPMD: SF #3557789
		if (isNullOrEmpty(name)) {
			finalName = login;
		} else {
			finalName = name;
		}
		
		final UsersActivation activation =
			usersActivation.findByActivationKey(activationKey);
		if (activation == null) {
			LOG.warn("Cannot find registration request for activation key '{}'", activationKey);
			return;
		}
		
		final String email = activation.getEmail();
		final Date registrationDate = activation.getCreatedAt();
		
		final String salt = generateSalt();
		
		final String hash = encoder.encodePassword(password, salt);
		checkState(hash != null, "Generated hash must be non null");
		
		final Date now = new Date();
		
		final User user = new User();
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
			new Object[]{login, finalName, activationKey}
		);
	}
	
	@Transactional(readOnly = true)
	public User findByLogin(final String login) {
		checkArgument(login != null, "Login should be non null");
		
		return users.findByLogin(login);
	}
	
	protected User getCurrentUser() {
		final SecurityContext ctx = SecurityContextHolder.getContext();
		checkState(ctx != null, "Security context must be non null");
		
		final Authentication auth = ctx.getAuthentication();
		if (auth == null) {
			return null;
		}
		
		final Object principal = auth.getPrincipal();
		if (principal == null) {
			return null;
		}
		
		checkState(
			principal instanceof CustomUserDetails,
			"Principal must be CustomUserDetails type"
		);
		
		final CustomUserDetails userDetails = (CustomUserDetails)principal;
		
		return userDetails.getUser();
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
	
}
