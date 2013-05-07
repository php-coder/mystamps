/*
 * Copyright (C) 2009-2013 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.service

import javax.inject.Inject

import java.util.Date

import org.apache.commons.lang3.{RandomStringUtils, StringUtils, Validate}

import org.slf4j.{Logger, LoggerFactory}

import org.springframework.security.authentication.encoding.PasswordEncoder

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import ru.mystamps.web.entity.{User, UsersActivation}
import ru.mystamps.web.dao.{UserDao, UsersActivationDao}
import ru.mystamps.web.service.dto.{ActivateAccountDto, RegisterAccountDto}

@Service
class UserServiceImpl extends UserService {
	private val LOG: Logger = LoggerFactory.getLogger(classOf[UserService])
	
	@Inject
	private var users: UserDao
	
	@Inject
	private var usersActivation: UsersActivationDao
	
	@Inject
	private var encoder: PasswordEncoder
	
	@Transactional
	override def addRegistrationRequest(dto: RegisterAccountDto): Unit = {
		Validate.isTrue(dto != null, "DTO should be non null")
		Validate.isTrue(dto.getEmail() != null, "Email should be non null")
		
		val activation: UsersActivation = new UsersActivation()
		
		activation.setActivationKey(generateActivationKey())
		activation.setEmail(dto.getEmail())
		activation.setCreatedAt(new Date())
		usersActivation.save(activation)
	}
	
	@Transactional(readOnly = true)
	override def findRegistrationRequestByActivationKey(activationKey: String): UsersActivation = {
		Validate.isTrue(activationKey != null, "Activation key should be non null")
		
		return usersActivation.findOne(activationKey)
	}
	
	@Transactional
	override def registerUser(dto: ActivateAccountDto): Unit = {
		Validate.isTrue(dto != null, "DTO should be non null")
		Validate.isTrue(dto.getLogin() != null, "Login should be non null")
		Validate.isTrue(dto.getPassword() != null, "Password should be non null")
		Validate.isTrue(dto.getActivationKey() != null, "Activation key should be non null")
		
		val login: String = dto.getLogin()
		
		// use login as name if name is not provided
		val finalName: String
		if (StringUtils.isEmpty(dto.getName())) {
			finalName = login
		} else {
			finalName = dto.getName()
		}
		
		val activationKey: String = dto.getActivationKey()
		val activation: UsersActivation = usersActivation.findOne(activationKey)
		if (activation == null) {
			LOG.warn("Cannot find registration request for activation key '{}'", activationKey)
			return
		}
		
		val email: String = activation.getEmail()
		val registrationDate: Date = activation.getCreatedAt()
		
		val salt: String = generateSalt()
		
		val hash: String = encoder.encodePassword(dto.getPassword(), salt)
		Validate.validState(hash != null, "Generated hash must be non null")
		
		val now: Date = new Date()
		
		val user: User = new User()
		user.setLogin(login)
		user.setName(finalName)
		user.setEmail(email)
		user.setRegisteredAt(registrationDate)
		user.setActivatedAt(now)
		user.setHash(hash)
		user.setSalt(salt)
		
		users.save(user)
		usersActivation.delete(activation)
		
		LOG.info(
			"Added user (login='{}', name='{}', activation key='{}')",
			login,
			finalName,
			activationKey
		)
	}
	
	@Transactional(readOnly = true)
	override def findByLogin(login: String): User = {
		Validate.isTrue(login != null, "Login should be non null")
		
		return users.findByLogin(login)
	}
	
	/**
	 * Generates activation key.
	 * @return string which contains numbers and letters in lower case
	 *         in 10 characters length
	 **/
	private def generateActivationKey(): String = {
		val actKeyLength: Int = UsersActivation.ACTIVATION_KEY_LENGTH
		return RandomStringUtils.randomAlphanumeric(actKeyLength).toLowerCase()
	}
	
	/**
	 * Generate password salt.
	 * @return string which contains letters and numbers in 10 characters length
	 **/
	private def generateSalt(): String = {
		return RandomStringUtils.randomAlphanumeric(User.SALT_LENGTH)
	}
	
}
