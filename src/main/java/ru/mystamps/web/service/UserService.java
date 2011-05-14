package ru.mystamps.web.service;

import java.util.Date;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.mystamps.web.entity.User;
import ru.mystamps.web.entity.UsersActivation;
import ru.mystamps.web.dao.UserDao;
import ru.mystamps.web.dao.UsersActivationDao;

@Service
public class UserService {
	
	private final Logger log = Logger.getRootLogger();
	
	@Autowired
	private UserDao users;
	
	@Autowired
	private UsersActivationDao usersActivation;
	
	@Transactional
	public void addRegistrationRequest(final String email) {
		final UsersActivation activation = new UsersActivation();
		
		/// @todo: get rid of hardcoded act key (#98)
		activation.setActivationKey(
			email.equals("coder@rock.home")
			? "7777744444"
			: generateActivationKey()
		);
		
		activation.setEmail(email);
		activation.setCreatedAt(new Date());
		usersActivation.add(activation);
	}
	
	@Transactional(readOnly = true)
	public UsersActivation findRegistrationRequestByActivationKey(
			final String activationKey) {
		return usersActivation.findByActivationKey(activationKey);
	}
	
	@Transactional
	public void registerUser(final String login, final String password,
			final String name, final String activationKey) {
		
		// use login as name if name is not provided
		final String finalName = ("".equals(name) ? login : name);
		
		final UsersActivation activation =
			usersActivation.findByActivationKey(activationKey);
		
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
		
		log.debug(
			"Added user '" + login + "' (" + finalName + ") " +
			"with password '" + password + "' " +
			"(key = " + activationKey + ")"
		);
	}
	
	@Transactional(readOnly = true)
	public User findByLogin(final String login) {
		return users.findByLogin(login);
	}
	
	@Transactional(readOnly = true)
	public User findByLoginAndPassword(final String login, final String password) {
		final User user = users.findByLogin(login);
		if (user == null) {
			log.debug("findByLoginAndPassword(" + login + ", " + password + "): wrong login");
			return null;
		}
		
		if (!user.getHash().equals(computeSha1Sum(user.getSalt() + password))) {
			log.debug("findByLoginAndPassword(" + login + ", " + password + "): wrong password");
			return null;
		}
		
		log.debug("findByLoginAndPassword(" + login + ", " + password + ") = " + user.getId());
		
		return user;
	}
	
	/**
	 * Generates activation key.
	 * @return string which contains numbers and letters in lower case
	 *         in 10 characters length
	 **/
	private static String generateActivationKey() {
		// Length of users_activation.act_key field in database equals
		// to 10 characters (see mystamps.sql)
		return RandomStringUtils.randomAlphanumeric(10).toLowerCase();
	}
	
	/**
	 * Generate password salt.
	 * @return string which contains letters and numbers in 10 characters length
	 **/
	private String generateSalt() {
		// Length of users.salt field in database equals to 10
		// characters (see mystamps.sql)
		return RandomStringUtils.randomAlphanumeric(10);
	}
	
	private String computeSha1Sum(final String string) {
		return Hex.encodeHexString(DigestUtils.sha(string));
	}
	
}
