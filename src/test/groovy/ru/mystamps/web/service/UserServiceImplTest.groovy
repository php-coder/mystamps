/**
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
package ru.mystamps.web.service

import org.springframework.security.crypto.password.PasswordEncoder

import spock.lang.Specification

import ru.mystamps.web.dao.JdbcUserDao
import ru.mystamps.web.dao.UserDao
import ru.mystamps.web.entity.User
import ru.mystamps.web.entity.User.Role
import ru.mystamps.web.entity.UsersActivation
import ru.mystamps.web.model.ActivateAccountForm
import ru.mystamps.web.tests.DateUtils

class UserServiceImplTest extends Specification {
	
	private UserDao userDao = Mock()
	private JdbcUserDao jdbcUserDao = Mock()
	private UsersActivationService usersActivationService = Mock()
	private CollectionService collectionService = Mock()
	private PasswordEncoder encoder = Mock()
	
	private UserService service
	private ActivateAccountForm activationForm
	
	def setup() {
		User user = TestObjects.createUser()
		
		encoder.encode(_ as String) >> user.getHash()
		
		UsersActivation activation = TestObjects.createUsersActivation()
		usersActivationService.findByActivationKey(_ as String) >> activation
		
		activationForm = new ActivateAccountForm()
		activationForm.setLogin(user.getLogin())
		activationForm.setPassword(TestObjects.TEST_PASSWORD)
		activationForm.setName(user.getName())
		activationForm.setActivationKey(activation.getActivationKey())
		
		service = new UserServiceImpl(userDao, jdbcUserDao, usersActivationService, collectionService, encoder)
	}
	
	//
	// Tests for registerUser()
	//
	
	def "registerUser() should throw exception when dto is null"() {
		when:
			service.registerUser(null)
		then:
			thrown IllegalArgumentException
	}
	
	def "registerUser() should create user"() {
		when:
			service.registerUser(activationForm)
		then:
			1 * userDao.save(_ as User) >> TestObjects.createUser()
	}
	
	def "registerUser() should delete registration request"() {
		given:
			UsersActivation expectedActivation = TestObjects.createUsersActivation()
			String expectedActivationKey = expectedActivation.getActivationKey()
		when:
			service.registerUser(activationForm)
		then:
			usersActivationService.findByActivationKey(_ as String) >> expectedActivation
		and:
			1 * usersActivationService.remove({ String actualActivationKey ->
				assert actualActivationKey == expectedActivationKey
				return true
			})
		and:
			userDao.save(_ as User) >> TestObjects.createUser()
	}
	
	def "registerUser() should throw exception when activation key is null"() {
		given:
			activationForm.setActivationKey(null)
		when:
			service.registerUser(activationForm)
		then:
			thrown IllegalArgumentException
	}
	
	def "registerUser() should do nothing when registration request not found"() {
		when:
			service.registerUser(activationForm)
		then:
			usersActivationService.findByActivationKey(_ as String) >> null
		and:
			0 * userDao.save(_ as User)
		and:
			0 * usersActivationService.remove(_ as UsersActivation)
	}
	
	def "registerUser() should pass name to dao"() {
		given:
			String expectedUserName = activationForm.getName()
		when:
			service.registerUser(activationForm)
		then:
			1 * userDao.save({ User user ->
				assert user?.name == expectedUserName
				return true
			}) >> TestObjects.createUser()
	}
	
	def "registerUser() should pass login instead of name when name is null"() {
		given:
			String expectedUserLogin = activationForm.getLogin()
			activationForm.setName(null)
		when:
			service.registerUser(activationForm)
		then:
			1 * userDao.save({ User user ->
				assert user?.name == expectedUserLogin
				return true
			}) >> TestObjects.createUser()
	}
	
	def "registerUser() should pass login instead of name when name is empty"() {
		given:
			String expectedUserLogin = activationForm.getLogin()
		and:
			activationForm.setName('')
		when:
			service.registerUser(activationForm)
		then:
			1 * userDao.save({ User user ->
				assert user?.name == expectedUserLogin
				return true
			}) >> TestObjects.createUser()
	}
	
	def "registerUser() should fill role field"() {
		when:
			service.registerUser(activationForm)
		then:
			1 * userDao.save({ User user ->
				assert user?.role == Role.USER
				return true
			}) >> TestObjects.createUser()
	}
	
	def "registerUser() should use email from registration request"() {
		given:
			UsersActivation activation = TestObjects.createUsersActivation()
		and:
			usersActivationService.findByActivationKey(_ as String) >> activation
		when:
			service.registerUser(activationForm)
		then:
			1 * userDao.save({ User user ->
				assert user?.email == activation.email
				return true
			}) >> TestObjects.createUser()
	}
	
	def "registerUser() should use registration date from registration request"() {
		given:
			UsersActivation activation = TestObjects.createUsersActivation()
		when:
			service.registerUser(activationForm)
		then:
			usersActivationService.findByActivationKey(_ as String) >> activation
		and:
			1 * userDao.save({ User user ->
				assert user?.registeredAt == activation.createdAt
				return true
			}) >> TestObjects.createUser()
	}
	
	def "registerUser() should throw exception when password is null"() {
		given:
			activationForm.setPassword(null)
		when:
			service.registerUser(activationForm)
		then:
			thrown IllegalArgumentException
	}
	
	def "registerUser() should gets hash from encoder"() {
		given:
			String expectedHash = TestObjects.createUser().getHash()
		when:
			service.registerUser(activationForm)
		then:
			1 * userDao.save({ User user ->
				assert user?.hash == expectedHash
				return true
			}) >> TestObjects.createUser()
		and:
			1 * encoder.encode({ String password ->
				assert password == TestObjects.TEST_PASSWORD
				return true
			}) >> expectedHash
	}
	
	def "registerUser() should throw exception when encoder returns null"() {
		when:
			service.registerUser(activationForm)
		then:
			encoder.encode(_ as String) >> null
		and:
			thrown IllegalStateException
	}
	
	def "registerUser() should throw exception when login is null"() {
		given:
			activationForm.setLogin(null)
		when:
			service.registerUser(activationForm)
		then:
			thrown IllegalArgumentException
	}
	
	def "registerUser() should pass login to dao"() {
		given:
			String expectedUserLogin = activationForm.getLogin()
		when:
			service.registerUser(activationForm)
		then:
			1 * userDao.save({ User user ->
				assert user?.login == expectedUserLogin
				return true
			}) >> TestObjects.createUser()
	}
	
	def "registerUser() should assign activated at to current date"() {
		when:
			service.registerUser(activationForm)
		then:
			1 * userDao.save({ User user ->
				assert DateUtils.roughlyEqual(user?.activatedAt, new Date())
				return true
			}) >> TestObjects.createUser()
	}
	
	def "registerUser() should create collection for user"() {
		given:
			Integer expectedId = 909;
			String expectedLogin = "foobar"
		and:
			User user = TestObjects.createUser();
			user.setId(expectedId)
			user.setLogin(expectedLogin)
		and:
			userDao.save(_ as User) >> user
		when:
			service.registerUser(activationForm)
		then:
			1 * collectionService.createCollection({ Integer ownerId ->
				assert ownerId == expectedId
				return true
			}, { String ownerLogin ->
				assert ownerLogin == expectedLogin
				return true
			})
	}
	
	//
	// Tests for findByLogin()
	//
	
	def "findByLogin() should throw exception when login is null"() {
		when:
			service.findByLogin(null)
		then:
			thrown IllegalArgumentException
	}
	
	def "findByLogin() should call dao"() {
		given:
			User expectedUser = TestObjects.createUser()
			userDao.findByLogin(_ as String) >> expectedUser
		when:
			User user = service.findByLogin('any-login')
		then:
			user == expectedUser
	}
	
	def "findByLogin() should pass login to dao"() {
		when:
			service.findByLogin('john')
		then:
			1 * userDao.findByLogin('john')
	}
	
	//
	// Tests for countByLogin()
	//
	
	def "countByLogin() should throw exception when login is null"() {
		when:
			service.countByLogin(null)
		then:
			thrown IllegalArgumentException
	}
	
	def "countByLogin() should call dao"() {
		given:
			jdbcUserDao.countByLogin(_ as String) >> 2L
		when:
			long result = service.countByLogin('any-login')
		then:
			result == 2L
	}
	
	def "countByLogin() should pass login to dao"() {
		when:
			service.countByLogin('john')
		then:
			1 * jdbcUserDao.countByLogin('john')
	}
	
}
