/**
 * Copyright (C) 2009-2019 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.feature.account

import org.slf4j.helpers.NOPLogger
import org.springframework.security.crypto.password.PasswordEncoder
import ru.mystamps.web.feature.collection.CollectionService
import ru.mystamps.web.service.TestObjects
import ru.mystamps.web.tests.DateUtils
import ru.mystamps.web.tests.Random
import spock.lang.Specification

@SuppressWarnings(['ClassJavadoc', 'MethodName', 'NoDef', 'NoTabCharacter', 'TrailingWhitespace'])
class UserServiceImplTest extends Specification {
	
	private final UserDao userDao = Mock()
	private final UsersActivationService usersActivationService = Mock()
	private final CollectionService collectionService = Mock()
	private final PasswordEncoder encoder = Mock()
	
	private UserService service
	private ActivateAccountForm activationForm
	
	def setup() {
		AddUserDbDto user = TestObjects.createAddUserDbDto()
		
		encoder.encode(_ as String) >> user.hash
		
		UsersActivationDto activation = TestObjects.createUsersActivationDto()
		usersActivationService.findByActivationKey(_ as String) >> activation
		
		activationForm = new ActivateAccountForm()
		activationForm.setLogin(user.login)
		activationForm.setPassword(TestObjects.TEST_PASSWORD)
		activationForm.setName(user.name)
		activationForm.setActivationKey(TestObjects.TEST_ACTIVATION_KEY)
		
		service = new UserServiceImpl(
			NOPLogger.NOP_LOGGER,
			userDao,
			usersActivationService,
			collectionService,
			encoder
		)
	}
	
	//
	// Tests for registerUser()
	//
	
	def "registerUser() should throw exception when dto is null"() {
		when:
			service.registerUser(null)
		then:
			IllegalArgumentException ex = thrown()
			ex.message == 'DTO must be non null'
	}
	
	def "registerUser() should create user"() {
		when:
			service.registerUser(activationForm)
		then:
			1 * userDao.add(_ as AddUserDbDto) >> Random.userId()
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "registerUser() should delete registration request"() {
		given:
			String expectedActivationKey = activationForm.activationKey
		when:
			service.registerUser(activationForm)
		then:
			1 * usersActivationService.remove({ String actualActivationKey ->
				assert actualActivationKey == expectedActivationKey
				return true
			})
		and:
			userDao.add(_ as AddUserDbDto) >> Random.userId()
	}
	
	def "registerUser() should throw exception when activation key is null"() {
		given:
			activationForm.setActivationKey(null)
		when:
			service.registerUser(activationForm)
		then:
			IllegalArgumentException ex = thrown()
			ex.message == 'Activation key must be non null'
	}
	
	def "registerUser() should do nothing when registration request not found"() {
		when:
			service.registerUser(activationForm)
		then:
			usersActivationService.findByActivationKey(_ as String) >> null
		and:
			0 * userDao.add(_ as AddUserDbDto)
		and:
			0 * usersActivationService.remove(_ as String)
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "registerUser() should pass name to dao"() {
		given:
			String expectedUserName = activationForm.name
		when:
			service.registerUser(activationForm)
		then:
			1 * userDao.add({ AddUserDbDto user ->
				assert user?.name == expectedUserName
				return true
			}) >> Random.userId()
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "registerUser() should pass login instead of name when name is null"() {
		given:
			String expectedUserLogin = activationForm.login
			activationForm.setName(null)
		when:
			service.registerUser(activationForm)
		then:
			1 * userDao.add({ AddUserDbDto user ->
				assert user?.name == expectedUserLogin
				return true
			}) >> Random.userId()
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "registerUser() should pass login instead of name when name is empty"() {
		given:
			String expectedUserLogin = activationForm.login
		and:
			activationForm.setName('')
		when:
			service.registerUser(activationForm)
		then:
			1 * userDao.add({ AddUserDbDto user ->
				assert user?.name == expectedUserLogin
				return true
			}) >> Random.userId()
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "registerUser() should fill role field"() {
		when:
			service.registerUser(activationForm)
		then:
			1 * userDao.add({ AddUserDbDto user ->
				assert user?.role == UserDetails.Role.USER
				return true
			}) >> Random.userId()
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "registerUser() should use email from registration request"() {
		given:
			UsersActivationDto activation = new UsersActivationDto('test@example.org', new Date())
		and:
			usersActivationService.findByActivationKey(_ as String) >> activation
		when:
			service.registerUser(activationForm)
		then:
			1 * userDao.add({ AddUserDbDto user ->
				assert user?.email == activation.email
				return true
			}) >> Random.userId()
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "registerUser() should use registration date from registration request"() {
		given:
			UsersActivationDto activation = new UsersActivationDto(TestObjects.TEST_EMAIL, new Date(86, 8, 12))
		when:
			service.registerUser(activationForm)
		then:
			usersActivationService.findByActivationKey(_ as String) >> activation
		and:
			1 * userDao.add({ AddUserDbDto user ->
				assert user?.registeredAt == activation.createdAt
				return true
			}) >> Random.userId()
	}
	
	def "registerUser() should throw exception when password is null"() {
		given:
			activationForm.setPassword(null)
		when:
			service.registerUser(activationForm)
		then:
			IllegalArgumentException ex = thrown()
			ex.message == 'Password must be non null'
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "registerUser() should gets hash from encoder"() {
		given:
			String expectedHash = TestObjects.createAddUserDbDto().hash
		when:
			service.registerUser(activationForm)
		then:
			1 * userDao.add({ AddUserDbDto user ->
				assert user?.hash == expectedHash
				return true
			}) >> Random.userId()
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
			IllegalStateException ex = thrown()
			ex.message == 'Generated hash must be non null'
	}
	
	def "registerUser() should throw exception when login is null"() {
		given:
			activationForm.setLogin(null)
		when:
			service.registerUser(activationForm)
		then:
			IllegalArgumentException ex = thrown()
			ex.message == 'Login must be non null'
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "registerUser() should pass login to dao"() {
		given:
			String expectedUserLogin = activationForm.login
		when:
			service.registerUser(activationForm)
		then:
			1 * userDao.add({ AddUserDbDto user ->
				assert user?.login == expectedUserLogin
				return true
			}) >> Random.userId()
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "registerUser() should assign activated at to current date"() {
		when:
			service.registerUser(activationForm)
		then:
			1 * userDao.add({ AddUserDbDto user ->
				assert DateUtils.roughlyEqual(user?.activatedAt, new Date())
				return true
			}) >> Random.userId()
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "registerUser() should create collection for user"() {
		given:
			Integer expectedId = 909
			String expectedLogin = activationForm.login
		and:
			userDao.add(_ as AddUserDbDto) >> expectedId
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
	// Tests for findUserDetailsByLogin()
	//
	
	def "findUserDetailsByLogin() should throw exception when login is null"() {
		when:
			service.findUserDetailsByLogin(null)
		then:
			IllegalArgumentException ex = thrown()
			ex.message == 'Login must be non null'
	}
	
	def "findUserDetailsByLogin() should call dao"() {
		given:
			UserDetails expectedUserDetails = TestObjects.createUserDetails()
			userDao.findUserDetailsByLogin(_ as String) >> expectedUserDetails
		when:
			UserDetails userDetails = service.findUserDetailsByLogin('any-login')
		then:
			userDetails == expectedUserDetails
	}
	
	def "findUserDetailsByLogin() should pass login to dao"() {
		when:
			service.findUserDetailsByLogin('john')
		then:
			1 * userDao.findUserDetailsByLogin('john')
	}
	
	//
	// Tests for countByLogin()
	//
	
	def "countByLogin() should throw exception when login is null"() {
		when:
			service.countByLogin(null)
		then:
			IllegalArgumentException ex = thrown()
			ex.message == 'Login must be non null'
	}
	
	def "countByLogin() should call dao"() {
		given:
			userDao.countByLogin(_ as String) >> 2L
		when:
			long result = service.countByLogin('any-login')
		then:
			result == 2L
	}
	
	def "countByLogin() should pass login to dao in lowercase"() {
		when:
			service.countByLogin('John')
		then:
			1 * userDao.countByLogin('john')
	}
	
	//
	// Tests for countRegisteredSince()
	//
	
	def "countRegisteredSince() should throw exception when date is null"() {
		when:
			service.countRegisteredSince(null)
		then:
			IllegalArgumentException ex = thrown()
			ex.message == 'Date must be non null'
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "countRegisteredSince() should invoke dao, pass argument and return result from dao"() {
		given:
			Date expectedDate = new Date()
		and:
			long expectedResult = 32
		when:
			long result = service.countRegisteredSince(expectedDate)
		then:
			1 * userDao.countActivatedSince({ Date date ->
				assert date == expectedDate
				return true
			}) >> expectedResult
		and:
			result == expectedResult
	}
	
}
