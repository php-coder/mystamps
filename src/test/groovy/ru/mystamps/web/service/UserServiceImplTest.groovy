/**
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
package ru.mystamps.web.service

import org.springframework.security.authentication.encoding.PasswordEncoder

import spock.lang.Specification
import spock.lang.Unroll

import ru.mystamps.web.dao.UserDao
import ru.mystamps.web.dao.UsersActivationDao
import ru.mystamps.web.entity.User
import ru.mystamps.web.entity.User.Role
import ru.mystamps.web.entity.UsersActivation
import ru.mystamps.web.model.ActivateAccountForm
import ru.mystamps.web.model.RegisterAccountForm
import ru.mystamps.web.tests.DateUtils

class UserServiceImplTest extends Specification {
	
	private UserDao userDao = Mock()
	private UsersActivationDao usersActivationDao = Mock()
	private MailService mailService = Mock()
	private PasswordEncoder encoder = Mock()
	
	private UserService service
	private ActivateAccountForm activationForm
	private RegisterAccountForm registrationForm
	
	private static final Locale ANY_LOCALE = Locale.ENGLISH;
	
	def setup() {
		User user = TestObjects.createUser()
		
		encoder.encodePassword(_ as String, _ as String) >> user.getHash()
		
		UsersActivation activation = TestObjects.createUsersActivation()
		usersActivationDao.findOne(_ as String) >> activation
		
		registrationForm = new RegisterAccountForm()
		registrationForm.setEmail("john.dou@example.org")
		
		activationForm = new ActivateAccountForm()
		activationForm.setLogin(user.getLogin())
		activationForm.setPassword(TestObjects.TEST_PASSWORD)
		activationForm.setName(user.getName())
		activationForm.setActivationKey(activation.getActivationKey())
		
		service = new UserServiceImpl(userDao, usersActivationDao, mailService, encoder)
	}
	
	//
	// Tests for addRegistrationRequest()
	//
	
	def "addRegistrationRequest() should throw exception when dto is null"() {
		when:
			service.addRegistrationRequest(null, ANY_LOCALE)
		then:
			thrown IllegalArgumentException
	}
	
	def "addRegistrationRequest() should call dao"() {
		when:
			service.addRegistrationRequest(registrationForm, ANY_LOCALE)
		then:
			1 * usersActivationDao.save(_ as UsersActivation)
	}
	
	def "addRegistrationRequest() should generate activation key"() {
		when:
			service.addRegistrationRequest(registrationForm, ANY_LOCALE)
		then:
			1 * usersActivationDao.save({ UsersActivation activation ->
				assert activation?.activationKey?.length() == UsersActivation.ACTIVATION_KEY_LENGTH
				assert activation?.activationKey ==~ /^[\p{Lower}\p{Digit}]+$/
				return true
			})
	}
	
	def "addRegistrationRequest() should generate unique activation key"() {
		given:
			List<String> passedArguments = []
		when:
			service.addRegistrationRequest(registrationForm, ANY_LOCALE)
			service.addRegistrationRequest(registrationForm, ANY_LOCALE)
		then:
			2 * usersActivationDao.save({ UsersActivation activation ->
				passedArguments.add(activation?.activationKey)
				return true
			})
		and:
			passedArguments.size() == 2
		and:
			String firstActivationKey = passedArguments.get(0)
			firstActivationKey != null
		and:
			String secondActivationKey = passedArguments.get(1)
			secondActivationKey != null
		and:
			firstActivationKey != secondActivationKey
	}
	
	def "addRegistrationRequest() should throw exception when email is null"() {
		given:
			registrationForm.setEmail(null)
		when:
			service.addRegistrationRequest(registrationForm, ANY_LOCALE)
		then:
			thrown IllegalArgumentException
	}
	
	def "addRegistrationRequest() should pass email to dao"() {
		given:
			String expectedEmail = "somename@example.org"
			registrationForm.setEmail(expectedEmail)
		when:
			service.addRegistrationRequest(registrationForm, ANY_LOCALE)
		then:
			1 * usersActivationDao.save({ UsersActivation activation ->
				assert activation?.email == expectedEmail
				return true
			})
	}
	
	@Unroll
	def "addRegistrationRequest() should pass language '#expectedLang' to dao"(Locale lang, String expectedLang) {
		when:
			service.addRegistrationRequest(registrationForm, lang)
		then:
			1 * usersActivationDao.save({ UsersActivation activation ->
				assert activation?.lang == expectedLang
				return true
			})
		where:
			lang          || expectedLang
			null          || "en"
			Locale.FRENCH || "fr"
	}
	
	def "addRegistrationRequest() should assign created at to current date"() {
		when:
			service.addRegistrationRequest(registrationForm, ANY_LOCALE)
		then:
			1 * usersActivationDao.save({ UsersActivation activation ->
				assert DateUtils.roughlyEqual(activation?.createdAt, new Date())
				return true
			})
	}
	
	def "addRegistrationRequest() should pass user's activation request to mail service"() {
		when:
			service.addRegistrationRequest(registrationForm, ANY_LOCALE)
		then:
			1 * mailService.sendActivationKeyToUser({ UsersActivation activation ->
				assert activation != null
				assert activation.activationKey != null
				assert activation.email == registrationForm.email
				assert DateUtils.roughlyEqual(activation.createdAt, new Date())
				return  true;
			})
	}
	
	//
	// Tests for countRegistrationRequestByActivationKey()
	//
	
	def "countRegistrationRequestByActivationKey() should throw exception when key is null"() {
		when:
			service.countRegistrationRequestByActivationKey(null)
		then:
			thrown IllegalArgumentException
	}
	
	def "countRegistrationRequestByActivationKey() should call dao"() {
		given:
			usersActivationDao.countByActivationKey(_ as String) >> 2
		when:
			int result = service.countRegistrationRequestByActivationKey("0123456789")
		then:
			result == 2
	}
	
	def "countRegistrationRequestByActivationKey() should pass activation key to dao"() {
		when:
			service.countRegistrationRequestByActivationKey("0987654321")
		then:
			1 * usersActivationDao.countByActivationKey("0987654321")
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
			1 * userDao.save(_ as User)
	}
	
	def "registerUser() should delete registration request"() {
		given:
			UsersActivation expectedActivation = TestObjects.createUsersActivation()
		when:
			service.registerUser(activationForm)
		then:
			usersActivationDao.findOne(_ as String) >> expectedActivation
		and:
			1 * usersActivationDao.delete({ UsersActivation actualActivation ->
				assert actualActivation == expectedActivation
				return true
			})
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
			usersActivationDao.findOne(_ as String) >> null
		and:
			0 * userDao.save(_ as User)
		and:
			0 * usersActivationDao.delete(_ as UsersActivation)
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
			})
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
			})
	}
	
	def "registerUser() should pass login instead of name when name is empty"() {
		given:
			String expectedUserLogin = activationForm.getLogin()
		and:
			activationForm.setName("")
		when:
			service.registerUser(activationForm)
		then:
			1 * userDao.save({ User user ->
				assert user?.name == expectedUserLogin
				return true
			})
	}
	
	def "registerUser() should fill role field"() {
		when:
			service.registerUser(activationForm)
		then:
			1 * userDao.save({ User user ->
				assert user?.role == Role.USER
				return true
			})
	}
	
	def "registerUser() should use email from registration request"() {
		given:
			UsersActivation activation = TestObjects.createUsersActivation()
		and:
			usersActivationDao.findOne(_ as String) >> activation
		when:
			service.registerUser(activationForm)
		then:
			1 * userDao.save({ User user ->
				assert user?.email == activation.email
				return true
			})
	}
	
	def "registerUser() should use registration date from registration request"() {
		given:
			UsersActivation activation = TestObjects.createUsersActivation()
		when:
			service.registerUser(activationForm)
		then:
			usersActivationDao.findOne(_ as String) >> activation
		and:
			1 * userDao.save({ User user ->
				assert user?.registeredAt == activation.createdAt
				return true
			})
	}
	
	def "registerUser() should generate salt"() {
		when:
			service.registerUser(activationForm)
		then:
			1 * userDao.save({ User user ->
				assert user?.salt?.length() == User.SALT_LENGTH
				assert user?.salt ==~ /^[\p{Alnum}]+$/
				return true
			})
	}
	
	def "registerUser() should generate unique salt"() {
		given:
			List<String> passedArguments = []
		when:
			service.registerUser(activationForm)
			service.registerUser(activationForm)
		then:
			2 * userDao.save({ User user ->
				passedArguments.add(user?.salt)
				return true
			})
		and:
			passedArguments.size() == 2
		and:
			String firstSalt = passedArguments.get(0)
			firstSalt != null
		and:
			String secondSalt = passedArguments.get(1)
			secondSalt != null
		and:
			firstSalt != secondSalt
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
			})
		and:
			1 * encoder.encodePassword(
				{ String password ->
					assert password == TestObjects.TEST_PASSWORD
					return true
				},
				_ as String
			) >> expectedHash
	}
	
	def "registerUser() should throw exception when encoder returns null"() {
		when:
			service.registerUser(activationForm)
		then:
			encoder.encodePassword(_ as String, _ as String) >> null
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
			})
	}
	
	def "registerUser() should assign activated at to current date"() {
		when:
			service.registerUser(activationForm)
		then:
			1 * userDao.save({ User user ->
				assert DateUtils.roughlyEqual(user?.activatedAt, new Date())
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
			User user = service.findByLogin("any-login")
		then:
			user == expectedUser
	}
	
	def "findByLogin() should pass login to dao"() {
		when:
			service.findByLogin("john")
		then:
			1 * userDao.findByLogin("john")
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
			userDao.countByLogin(_ as String) >> 2
		when:
			int result = service.countByLogin("any-login")
		then:
			result == 2
	}
	
	def "countByLogin() should pass login to dao"() {
		when:
			service.countByLogin("john")
		then:
			1 * userDao.countByLogin("john")
	}
	
}
