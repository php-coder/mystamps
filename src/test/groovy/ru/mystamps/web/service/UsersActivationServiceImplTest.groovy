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

import ru.mystamps.web.dao.JdbcUsersActivationDao
import ru.mystamps.web.entity.UsersActivation
import ru.mystamps.web.model.RegisterAccountForm
import ru.mystamps.web.tests.DateUtils
import spock.lang.Specification
import spock.lang.Unroll

class UsersActivationServiceImplTest extends Specification {

	private JdbcUsersActivationDao jdbcUsersActivationDao = Mock()
	private MailService mailService = Mock()

	private UsersActivationService service
	private RegisterAccountForm registrationForm

	private static final Locale ANY_LOCALE = Locale.ENGLISH;

	def setup() {
		registrationForm = new RegisterAccountForm()
		registrationForm.setEmail('john.dou@example.org')

		service = new UsersActivationServiceImpl(jdbcUsersActivationDao, mailService)
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
			1 * jdbcUsersActivationDao.add(_ as UsersActivation)
	}

	def "add() should generate activation key"() {
		when:
			service.add(registrationForm, ANY_LOCALE)
		then:
			1 * jdbcUsersActivationDao.add({ UsersActivation activation ->
				assert activation?.activationKey?.length() == UsersActivation.ACTIVATION_KEY_LENGTH
				assert activation?.activationKey ==~ /^[\p{Lower}\p{Digit}]+$/
				return true
			})
	}

	def "add() should generate unique activation key"() {
		given:
			List<String> passedArguments = []
		when:
			service.add(registrationForm, ANY_LOCALE)
			service.add(registrationForm, ANY_LOCALE)
		then:
			2 * jdbcUsersActivationDao.add({ UsersActivation activation ->
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

	def "add() should pass email to dao"() {
		given:
			String expectedEmail = 'somename@example.org'
			registrationForm.setEmail(expectedEmail)
		when:
			service.add(registrationForm, ANY_LOCALE)
		then:
			1 * jdbcUsersActivationDao.add({ UsersActivation activation ->
				assert activation?.email == expectedEmail
				return true
			})
	}

	@Unroll
	def "add() should pass language '#expectedLang' to dao"(Locale lang, String expectedLang) {
		when:
			service.add(registrationForm, lang)
		then:
			1 * jdbcUsersActivationDao.add({ UsersActivation activation ->
				assert activation?.lang == expectedLang
				return true
			})
		where:
			lang          || expectedLang
			null          || 'en'
			Locale.FRENCH || 'fr'
	}

	def "add() should assign created at to current date"() {
		when:
			service.add(registrationForm, ANY_LOCALE)
		then:
			1 * jdbcUsersActivationDao.add({ UsersActivation activation ->
				assert DateUtils.roughlyEqual(activation?.createdAt, new Date())
				return true
			})
	}

	def "add() should pass user's activation request to mail service"() {
		when:
			service.add(registrationForm, ANY_LOCALE)
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
			jdbcUsersActivationDao.countByActivationKey(_ as String) >> 2L
		when:
			long result = service.countByActivationKey('0123456789')
		then:
			result == 2L
	}

	def "countByActivationKey() should pass activation key to dao"() {
		when:
			service.countByActivationKey('0987654321')
		then:
			1 * jdbcUsersActivationDao.countByActivationKey('0987654321')
	}

    //
    // Tests for remove()
    //


    def "remove() should throw exception when key is null"() {
        when:
            service.remove(null)
        then:
            thrown IllegalArgumentException
    }

    def "remove() should throw exception when Activation Key is null"() {
        given:
            UsersActivation activation = TestObjects.createUsersActivation()
            activation.setActivationKey(null)
        when:
            service.remove(activation)
        then:
            thrown IllegalArgumentException
    }

    def "remove() should pass argument to DAO method"() {
        given:
            UsersActivation activation = TestObjects.createUsersActivation()
        when:
            service.remove(activation)
        then:
            1 * jdbcUsersActivationDao.removeByActivationKey(activation.getActivationKey())
    }
}
