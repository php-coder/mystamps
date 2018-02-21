/**
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
package ru.mystamps.web.service

import spock.lang.Specification
import spock.lang.Unroll

import org.slf4j.helpers.NOPLogger

import ru.mystamps.web.dao.UsersActivationDao
import ru.mystamps.web.dao.dto.AddUsersActivationDbDto
import ru.mystamps.web.dao.dto.UsersActivationDto
import ru.mystamps.web.dao.dto.UsersActivationFullDto
import ru.mystamps.web.controller.dto.RegisterAccountForm
import ru.mystamps.web.service.dto.SendUsersActivationDto
import ru.mystamps.web.test.DateUtils
import ru.mystamps.web.validation.ValidationRules

@SuppressWarnings(['ClassJavadoc', 'MethodName', 'NoDef', 'NoTabCharacter', 'TrailingWhitespace'])
class UsersActivationServiceImplTest extends Specification {
	
	private final UsersActivationDao usersActivationDao = Mock()
	private final MailService mailService = Mock()
	
	private UsersActivationService service
	private RegisterAccountForm registrationForm
	
	private static final Locale ANY_LOCALE = Locale.ENGLISH
	
	def setup() {
		registrationForm = new RegisterAccountForm()
		registrationForm.setEmail('john.dou@example.org')
		
		service = new UsersActivationServiceImpl(NOPLogger.NOP_LOGGER, usersActivationDao, mailService)
	}
	
	//
	// Tests for add()
	//
	
	def "add() should throw exception when dto is null"() {
		when:
			service.add(null, ANY_LOCALE)
		then:
			thrown IllegalArgumentException
	}
	
	def "add() should call dao"() {
		when:
			service.add(registrationForm, ANY_LOCALE)
		then:
			1 * usersActivationDao.add(_ as AddUsersActivationDbDto)
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "add() should generate activation key"() {
		when:
			service.add(registrationForm, ANY_LOCALE)
		then:
			1 * usersActivationDao.add({ AddUsersActivationDbDto activation ->
				assert activation?.activationKey?.length() == ValidationRules.ACT_KEY_LENGTH
				assert activation?.activationKey ==~ /^[\p{Lower}\p{Digit}]+$/
				return true
			})
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "add() should generate unique activation key"() {
		given:
			List<String> passedArguments = []
		when:
			service.add(registrationForm, ANY_LOCALE)
			service.add(registrationForm, ANY_LOCALE)
		then:
			2 * usersActivationDao.add({ AddUsersActivationDbDto activation ->
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
	
	def "add() should throw exception when email is null"() {
		given:
			registrationForm.setEmail(null)
		when:
			service.add(registrationForm, ANY_LOCALE)
		then:
			thrown IllegalArgumentException
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "add() should pass email to dao"() {
		given:
			String expectedEmail = 'somename@example.org'
			registrationForm.setEmail(expectedEmail)
		when:
			service.add(registrationForm, ANY_LOCALE)
		then:
			1 * usersActivationDao.add({ AddUsersActivationDbDto activation ->
				assert activation?.email == expectedEmail
				return true
			})
	}
	
	@Unroll
	@SuppressWarnings([
		'ClosureAsLastMethodParameter',
		'UnnecessaryReturnKeyword',
		/* false positive: */ 'UnnecessaryBooleanExpression',
	])
	def "add() should pass language '#expectedLang' to dao"(Locale lang, String expectedLang) {
		when:
			service.add(registrationForm, lang)
		then:
			1 * usersActivationDao.add({ AddUsersActivationDbDto activation ->
				assert activation?.lang == expectedLang
				return true
			})
		where:
			lang          || expectedLang
			null          || 'en'
			Locale.FRENCH || 'fr'
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "add() should assign created at to current date"() {
		when:
			service.add(registrationForm, ANY_LOCALE)
		then:
			1 * usersActivationDao.add({ AddUsersActivationDbDto activation ->
				assert DateUtils.roughlyEqual(activation?.createdAt, new Date())
				return true
			})
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "add() should pass user's activation request to mail service"() {
		when:
			service.add(registrationForm, Locale.FRANCE)
		then:
			1 * mailService.sendActivationKeyToUser({ SendUsersActivationDto activation ->
				assert activation != null
				assert activation.activationKey != null
				assert activation.email == registrationForm.email
				assert activation.locale == new Locale('fr')
				return true
			})
	}
	
	//
	// Tests for countByActivationKey()
	//
	
	def "countByActivationKey() should throw exception when key is null"() {
		when:
			service.countByActivationKey(null)
		then:
			thrown IllegalArgumentException
	}
	
	def "countByActivationKey() should call dao"() {
		given:
			usersActivationDao.countByActivationKey(_ as String) >> 2L
		when:
			long result = service.countByActivationKey('0123456789')
		then:
			result == 2L
	}
	
	def "countByActivationKey() should pass activation key to dao"() {
		when:
			service.countByActivationKey('0987654321')
		then:
			1 * usersActivationDao.countByActivationKey('0987654321')
	}
	
	//
	// Tests for countCreatedSince()
	//
	
	def "countCreatedSince() should throw exception when date is null"() {
		when:
			service.countCreatedSince(null)
		then:
			thrown IllegalArgumentException
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "countCreatedSince() should invoke dao, pass argument and return result from dao"() {
		given:
			Date expectedDate = new Date()
		and:
			long expectedResult = 31
		when:
			long result = service.countCreatedSince(expectedDate)
		then:
			1 * usersActivationDao.countCreatedSince({ Date date ->
				assert date == expectedDate
				return true
			}) >> expectedResult
		and:
			result == expectedResult
	}
	
	//
	// Tests for findByActivationKey()
	//

	def "findByActivationKey() should throw exception when argument is null"() {
		when:
			service.findByActivationKey(null)
		then:
			thrown IllegalArgumentException
	}

	def "findByActivationKey() should call dao, pass argument to it and return result"() {
		given:
			UsersActivationDto expectedResult = TestObjects.createUsersActivationDto()
		when:
			UsersActivationDto result = service.findByActivationKey('0987654321')
		then:
			1 * usersActivationDao.findByActivationKey('0987654321') >> expectedResult
		and:
			result == expectedResult
	}
	
	//
	// Tests for findOlderThan()
	//

	def "findOlderThan() should throw exception when days are less than zero"() {
		when:
			service.findOlderThan(-1)
		then:
			thrown IllegalArgumentException
	}

	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def "findOlderThan() should invoke dao, pass changed date and return the result"() {
		given:
			int days = 4
		and:
			Date expectedDate = new Date() - days
		and:
			List<UsersActivationFullDto> expectedResult = [ TestObjects.createUsersActivationFullDto() ]
		when:
			List<UsersActivationFullDto> result = service.findOlderThan(days)
		then:
			1 * usersActivationDao.findOlderThan({ Date date ->
				assert DateUtils.roughlyEqual(date, expectedDate)
				return true
			}) >> expectedResult
		and:
			result == expectedResult
	}
	
	//
	// Tests for remove()
	//
	
	def "remove() should throw exception when activation key is null"() {
		when:
			service.remove(null)
		then:
			thrown IllegalArgumentException
	}
	
	def "remove() should pass argument to DAO method"() {
		given:
			String activationKey = TestObjects.TEST_ACTIVATION_KEY
		when:
			service.remove(activationKey)
		then:
			1 * usersActivationDao.removeByActivationKey(activationKey)
	}
	
}
