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
package ru.mystamps.web.service;

import java.util.Date;

import org.springframework.security.authentication.encoding.PasswordEncoder;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.fest.assertions.api.Assertions.assertThat;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

import ru.mystamps.web.dao.UserDao;
import ru.mystamps.web.dao.UsersActivationDao;
import ru.mystamps.web.entity.User;
import ru.mystamps.web.entity.UsersActivation;
import ru.mystamps.web.model.ActivateAccountForm;
import ru.mystamps.web.model.RegisterAccountForm;
import ru.mystamps.web.tests.fest.DateAssert;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {
	
	private static final String TEST_NAME           = "Test Name";
	private static final String TEST_LOGIN          = "test";
	private static final String TEST_PASSWORD       = "secret";
	private static final String TEST_SALT           = "salt";
	
	// sha1(TEST_SALT + "{" + TEST_PASSWORD + "}")
	private static final String TEST_HASH           = "b0dd94c84e784ddb1e9a83c8a2e8f403846647b9";
	
	private static final String TEST_EMAIL          = "test@example.org";
	private static final String TEST_ACTIVATION_KEY = "1234567890";
	
	@Mock
	private UserDao userDao;
	
	@Mock
	private UsersActivationDao usersActivationDao;
	
	@Mock
	private PasswordEncoder encoder;
	
	@Captor
	private ArgumentCaptor<UsersActivation> activationCaptor;
	
	@Captor
	private ArgumentCaptor<User> userCaptor;
	
	private UserService service;
	private ActivateAccountForm activationForm;
	private RegisterAccountForm registrationForm;
	
	@Before
	public void setUp() {
		when(encoder.encodePassword(anyString(), anyString())).thenReturn(TEST_HASH);
		when(usersActivationDao.findOne(anyString())).thenReturn(getUsersActivation());
		
		registrationForm = new RegisterAccountForm();
		registrationForm.setEmail(TEST_EMAIL);
		
		activationForm = new ActivateAccountForm();
		activationForm.setLogin(TEST_LOGIN);
		activationForm.setPassword(TEST_PASSWORD);
		activationForm.setName(TEST_NAME);
		activationForm.setActivationKey(TEST_ACTIVATION_KEY);
		
		service = new UserServiceImpl(userDao, usersActivationDao, encoder);
	}
	
	//
	// Tests for addRegistrationRequest()
	//
	
	@Test(expected = IllegalArgumentException.class)
	public void addRegistrationRequestShouldThrowExceptionWhenDtoIsNull() {
		service.addRegistrationRequest(null);
	}
	
	@Test
	public void addRegistrationRequestShouldCallDao() {
		service.addRegistrationRequest(registrationForm);
		verify(usersActivationDao).save(any(UsersActivation.class));
	}
	
	@Test
	public void addRegistrationRequestShouldGenerateActivationKey() {
		service.addRegistrationRequest(registrationForm);
		
		verify(usersActivationDao).save(activationCaptor.capture());
		
		String activationKey = activationCaptor.getValue().getActivationKey();
		assertThat(activationKey.length()).as("activation key length")
			.isEqualTo(UsersActivation.ACTIVATION_KEY_LENGTH);
		
		assertThat(activationKey).matches("^[\\p{Lower}\\p{Digit}]+$");
	}
	
	@Test
	public void addRegistrationRequestShouldGenerateUniqueActivationKey() {
		service.addRegistrationRequest(registrationForm);
		verify(usersActivationDao).save(activationCaptor.capture());
		String firstActivationKey = activationCaptor.getValue().getActivationKey();
		
		service.addRegistrationRequest(registrationForm);
		verify(usersActivationDao, atLeastOnce()).save(activationCaptor.capture());
		String secondActivationKey = activationCaptor.getValue().getActivationKey();
		
		assertThat(firstActivationKey).isNotEqualTo(secondActivationKey);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void addRegistrationRequestShouldThrowExceptionWhenEmailIsNull() {
		registrationForm.setEmail(null);
		
		service.addRegistrationRequest(registrationForm);
	}
	
	@Test
	public void addRegistrationRequestShouldPassEmailToDao() {
		String expectedEmail = "somename@example.org";
		registrationForm.setEmail(expectedEmail);
		
		service.addRegistrationRequest(registrationForm);
		
		verify(usersActivationDao).save(activationCaptor.capture());
		
		assertThat(activationCaptor.getValue().getEmail()).isEqualTo(expectedEmail);
	}
	
	@Test
	public void addRegistrationRequestShouldAssignCreatedAtToCurrentDate() {
		service.addRegistrationRequest(registrationForm);
		
		verify(usersActivationDao).save(activationCaptor.capture());
		
		DateAssert.assertThat(activationCaptor.getValue().getCreatedAt()).isCurrentDate();
	}
	
	//
	// Tests for findRegistrationRequestByActivationKey()
	//
	
	@Test(expected = IllegalArgumentException.class)
	public void findRegistrationRequestByActivationKeyShouldThrowExceptionWhenKeyIsNull() {
		service.findRegistrationRequestByActivationKey(null);
	}
	
	@Test
	public void findRegistrationRequestByActivationKeyShouldCallDao() {
		UsersActivation expectedActivation = getUsersActivation();
		when(usersActivationDao.findOne(anyString())).thenReturn(expectedActivation);
		
		UsersActivation activation =
			service.findRegistrationRequestByActivationKey(TEST_ACTIVATION_KEY);
		
		assertThat(activation).isEqualTo(expectedActivation);
	}
	
	
	@Test
	public void findRegistrationRequestByActivationKeyShouldPassActivationKeyToDao() {
		service.findRegistrationRequestByActivationKey(TEST_ACTIVATION_KEY);
		verify(usersActivationDao).findOne(TEST_ACTIVATION_KEY);
	}
	
	//
	// Tests for registerUser()
	//
	
	@Test(expected = IllegalArgumentException.class)
	public void registerUserShouldThrowExceptionWhenDtoIsNull() {
		service.registerUser(null);
	}
	
	@Test
	public void registerUserShouldCreateUser() {
		service.registerUser(activationForm);
		
		verify(userDao).save(any(User.class));
	}
	
	@Test
	public void registerUserShouldDeleteRegistrationRequest() {
		UsersActivation activation = getUsersActivation();
		when(usersActivationDao.findOne(anyString())).thenReturn(activation);
		
		service.registerUser(activationForm);
		
		verify(usersActivationDao).delete(activation);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void registerUserShouldThrowExceptionWhenActivationKeyIsNull() {
		activationForm.setActivationKey(null);
		
		service.registerUser(activationForm);
	}
	
	@Test
	public void registerUserShouldDoNothingWhenRegistrationRequestNotFound() {
		when(usersActivationDao.findOne(anyString())).thenReturn(null);
		
		service.registerUser(activationForm);
		
		verify(userDao, never()).save(any(User.class));
		verify(usersActivationDao, never()).delete(any(UsersActivation.class));
	}
	
	@Test
	public void registerUserShouldPassNameToDao() {
		service.registerUser(activationForm);
		
		verify(userDao).save(userCaptor.capture());
		
		assertThat(userCaptor.getValue().getName()).isEqualTo(TEST_NAME);
	}
	
	@Test
	public void registerUserShouldPassLoginInsteadOfNameWhenNameIsNull() {
		activationForm.setName(null);
		
		service.registerUser(activationForm);
		
		verify(userDao).save(userCaptor.capture());
		
		assertThat(userCaptor.getValue().getName()).isEqualTo(TEST_LOGIN);
	}
	
	@Test
	public void registerUserShouldPassLoginInsteadOfNameWhenNameIsEmpty() {
		activationForm.setName("");
		
		service.registerUser(activationForm);
		
		verify(userDao).save(userCaptor.capture());
		
		assertThat(userCaptor.getValue().getName()).isEqualTo(TEST_LOGIN);
	}
	
	@Test
	public void registerUserShouldUseEmailFromRegistrationRequest() {
		UsersActivation activation = getUsersActivation();
		when(usersActivationDao.findOne(anyString())).thenReturn(activation);
		
		service.registerUser(activationForm);
		
		verify(userDao).save(userCaptor.capture());
		
		assertThat(userCaptor.getValue().getEmail()).isEqualTo(activation.getEmail());
	}
	
	@Test
	public void registerUserShouldUseRegistrationDateFromRegistrationRequest() {
		UsersActivation activation = getUsersActivation();
		when(usersActivationDao.findOne(anyString())).thenReturn(activation);
		
		service.registerUser(activationForm);
		
		verify(userDao).save(userCaptor.capture());
		
		assertThat(userCaptor.getValue().getRegisteredAt()).isEqualTo(activation.getCreatedAt());
	}
	
	@Test
	public void registerUserShouldGenerateSalt() {
		service.registerUser(activationForm);
		
		verify(userDao).save(userCaptor.capture());
		
		String salt = userCaptor.getValue().getSalt();
		assertThat(salt.length()).as("salt length").isEqualTo(User.SALT_LENGTH);
		assertThat(salt).matches("^[\\p{Alnum}]+$");
	}
	
	@Test
	public void registerUserShouldGenerateUniqueSalt() {
		service.registerUser(activationForm);
		verify(userDao).save(userCaptor.capture());
		String firstSalt = userCaptor.getValue().getSalt();
		
		service.registerUser(activationForm);
		verify(userDao, atLeastOnce()).save(userCaptor.capture());
		String secondSalt = userCaptor.getValue().getSalt();
		
		assertThat(firstSalt).isNotEqualTo(secondSalt);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void registerUserShouldThrowExceptionWhenPasswordIsNull() {
		activationForm.setPassword(null);
		
		service.registerUser(activationForm);
	}
	
	@Test
	public void registerUserShouldGetsHashFromEncoder() {
		String expectedHash = TEST_HASH;
		
		when(encoder.encodePassword(anyString(), anyString())).thenReturn(expectedHash);
		
		service.registerUser(activationForm);
		
		verify(userDao).save(userCaptor.capture());
		verify(encoder).encodePassword(eq(TEST_PASSWORD), anyString());
		
		String actualHash = userCaptor.getValue().getHash();
		assertThat(actualHash).isEqualTo(expectedHash);
	}
	
	@Test(expected = IllegalStateException.class)
	public void registerUserShouldThrowExceptionWhenEncoderReturnsNull() {
		when(encoder.encodePassword(anyString(), anyString())).thenReturn(null);
		
		service.registerUser(activationForm);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void registerUserShouldThrowExceptionWhenLoginIsNull() {
		activationForm.setLogin(null);
		
		service.registerUser(activationForm);
	}
	
	@Test
	public void registerUserShouldPassLoginToDao() {
		service.registerUser(activationForm);
		
		verify(userDao).save(userCaptor.capture());
		
		assertThat(userCaptor.getValue().getLogin()).isEqualTo(TEST_LOGIN);
	}
	
	@Test
	public void registerUserShouldAssignActivatedAtToCurrentDate() {
		service.registerUser(activationForm);
		
		verify(userDao).save(userCaptor.capture());
		
		DateAssert.assertThat(userCaptor.getValue().getActivatedAt()).isCurrentDate();
	}
	
	//
	// Tests for findByLogin()
	//
	
	@Test(expected = IllegalArgumentException.class)
	public void findByLoginShouldThrowExceptionWhenLoginIsNull() {
		service.findByLogin(null);
	}
	
	@Test
	public void findByLoginShouldCallDao() {
		User expectedUser = getValidUser();
		when(userDao.findByLogin(anyString())).thenReturn(expectedUser);
		
		User user = service.findByLogin(TEST_LOGIN);
		
		assertThat(user).isEqualTo(expectedUser);
	}
	
	@Test
	public void findByLoginShouldPassLoginToDao() {
		service.findByLogin(TEST_LOGIN);
		verify(userDao).findByLogin(TEST_LOGIN);
	}
	
	static User getValidUser() {
		final Integer anyId = 777;
		User user = new User();
		user.setId(anyId);
		user.setLogin(TEST_LOGIN);
		user.setName(TEST_NAME);
		user.setEmail(TEST_EMAIL);
		user.setRegisteredAt(new Date());
		user.setActivatedAt(new Date());
		user.setHash(TEST_HASH);
		user.setSalt(TEST_SALT);
		
		return user;
	}
	
	static UsersActivation getUsersActivation() {
		UsersActivation activation = new UsersActivation();
		activation.setActivationKey(TEST_ACTIVATION_KEY);
		activation.setEmail(TEST_EMAIL);
		activation.setCreatedAt(new Date());
		return activation;
	}
	
}
