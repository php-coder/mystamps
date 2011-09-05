/*
 * Copyright (C) 2011 Slava Semushin <slava.semushin@gmail.com>
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

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.fest.assertions.Assertions.assertThat;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

import ru.mystamps.web.dao.UserDao;
import ru.mystamps.web.dao.UsersActivationDao;
import ru.mystamps.web.entity.User;
import ru.mystamps.web.entity.UsersActivation;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {
	
	private static final String TEST_NAME           = "Test Name";
	private static final String TEST_LOGIN          = "test";
	private static final String TEST_PASSWORD       = "secret";
	private static final String TEST_SALT           = "salt";
	
	// sha1(TEST_SALT + TEST_PASSWORD)
	private static final String TEST_HASH           = "da00ec2e6ff9ed4d342b24a16e262c82f3c8b10b";
	
	private static final String TEST_EMAIL          = "test@example.org";
	private static final String TEST_ACTIVATION_KEY = "1234567890";
	
	@Mock
	private UserDao userDao;
	
	@Mock
	private UsersActivationDao usersActivationDao;
	
	@Captor
	private ArgumentCaptor<UsersActivation> activationCaptor;
	
	@Captor
	private ArgumentCaptor<User> userCaptor;
	
	@InjectMocks
	private UserService service = new UserService();
	
	//
	// Tests for addRegistrationRequest()
	//
	
	@Test
	public void addRegistrationRequestShouldCallDao() {
		service.addRegistrationRequest(TEST_EMAIL);
		verify(usersActivationDao).add(any(UsersActivation.class));
	}
	
	@Test
	public void addRegistrationRequestShouldGenerateActivationKey() {
		service.addRegistrationRequest(TEST_EMAIL);
		
		verify(usersActivationDao).add(activationCaptor.capture());
		
		final String activationKey = activationCaptor.getValue().getActivationKey();
		assertThat(activationKey.length()).as("activation key length").isEqualTo(UsersActivation.ACTIVATION_KEY_LENGTH);
		assertThat(activationKey).matches("^[\\p{Lower}\\p{Digit}]+$");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void addRegistrationRequestShouldThrowExceptionWhenEmailIsNull() {
		service.addRegistrationRequest(null);
	}
	
	@Test
	public void addRegistrationRequestShouldPassEmailToDao() {
		service.addRegistrationRequest(TEST_EMAIL);
		
		verify(usersActivationDao).add(activationCaptor.capture());
		
		assertThat(activationCaptor.getValue().getEmail()).isEqualTo(TEST_EMAIL);
	}
	
	@Test
	public void addRegistrationRequestShouldAssignCurrentDate() {
		service.addRegistrationRequest(TEST_EMAIL);
		
		verify(usersActivationDao).add(activationCaptor.capture());
		
		assertThat(activationCaptor.getValue().getCreatedAt()).isNotNull();
	}
	
	//
	// Tests for findRegistrationRequestByActivationKey()
	//
	
	@Test(expected = IllegalArgumentException.class)
	public void findRegistrationRequestByActivationKeyShouldThrowExceptionWhenActivationKeyIsNull() {
		service.findRegistrationRequestByActivationKey(null);
	}
	
	@Test
	public void findRegistrationRequestByActivationKeyShouldCallDao() {
		final UsersActivation expectedActivation = getUsersActivation();
		when(usersActivationDao.findByActivationKey(anyString())).thenReturn(expectedActivation);
		
		final UsersActivation activation =
			service.findRegistrationRequestByActivationKey(TEST_ACTIVATION_KEY);
		
		assertThat(activation).isEqualTo(expectedActivation);
	}
	
	
	@Test
	public void findRegistrationRequestByActivationKeyShouldPassActivationKeyToDao() {
		service.findRegistrationRequestByActivationKey(TEST_ACTIVATION_KEY);
		verify(usersActivationDao).findByActivationKey(TEST_ACTIVATION_KEY);
	}
	
	//
	// Tests for registerUser()
	//
	
	@Test
	public void registerUserShouldCreateUser() {
		when(usersActivationDao.findByActivationKey(anyString())).thenReturn(getUsersActivation());
		
		service.registerUser(TEST_LOGIN, TEST_PASSWORD, TEST_NAME, TEST_ACTIVATION_KEY);
		
		verify(userDao).add(any(User.class));
	}
	
	@Test
	public void registerUserShouldDeleteRegistrationRequest() {
		final UsersActivation activation = getUsersActivation();
		when(usersActivationDao.findByActivationKey(anyString())).thenReturn(activation);
		
		service.registerUser(TEST_LOGIN, TEST_PASSWORD, TEST_NAME, TEST_ACTIVATION_KEY);
		
		verify(usersActivationDao).delete(activation);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void registerUserShouldThrowExceptionWhenActivationKeyIsNull() {
		service.registerUser(TEST_LOGIN, TEST_PASSWORD, TEST_NAME, null);
	}
	
	@Test
	public void registerUserShouldDoNothingWhenRegistrationRequestNotFound() {
		when(usersActivationDao.findByActivationKey(anyString())).thenReturn(null);
		
		service.registerUser(TEST_LOGIN, TEST_PASSWORD, TEST_NAME, TEST_ACTIVATION_KEY);
		
		verify(userDao, never()).add(any(User.class));
		verify(usersActivationDao, never()).delete(any(UsersActivation.class));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void registerUserShouldThrowExceptionWhenNameIsNull() {
		service.registerUser(TEST_LOGIN, TEST_PASSWORD, null, TEST_ACTIVATION_KEY);
	}
	
	@Test
	public void registerUserShouldPassNameToDao() {
		when(usersActivationDao.findByActivationKey(anyString())).thenReturn(getUsersActivation());
		
		service.registerUser(TEST_LOGIN, TEST_PASSWORD, TEST_NAME, TEST_ACTIVATION_KEY);
		
		verify(userDao).add(userCaptor.capture());
		
		assertThat(userCaptor.getValue().getName()).isEqualTo(TEST_NAME);
	}
	
	@Test
	public void registerUserShouldPassLoginInsteadOfNameWhenNameIsEmpty() {
		when(usersActivationDao.findByActivationKey(anyString())).thenReturn(getUsersActivation());
		
		service.registerUser(TEST_LOGIN, TEST_PASSWORD, "", TEST_ACTIVATION_KEY);
		
		verify(userDao).add(userCaptor.capture());
		
		assertThat(userCaptor.getValue().getName()).isEqualTo(TEST_LOGIN);
	}
	
	@Test
	public void registerUserShouldUseEmailFromRegistrationRequest() {
		final UsersActivation activation = getUsersActivation();
		when(usersActivationDao.findByActivationKey(anyString())).thenReturn(activation);
		
		service.registerUser(TEST_LOGIN, TEST_PASSWORD, TEST_NAME, TEST_ACTIVATION_KEY);
		
		verify(userDao).add(userCaptor.capture());
		
		assertThat(userCaptor.getValue().getEmail()).isEqualTo(activation.getEmail());
	}
	
	@Test
	public void registerUserShouldUseRegistrationDateFromRegistrationRequest() {
		final UsersActivation activation = getUsersActivation();
		when(usersActivationDao.findByActivationKey(anyString())).thenReturn(activation);
		
		service.registerUser(TEST_LOGIN, TEST_PASSWORD, TEST_NAME, TEST_ACTIVATION_KEY);
		
		verify(userDao).add(userCaptor.capture());
		
		assertThat(userCaptor.getValue().getRegisteredAt()).isEqualTo(activation.getCreatedAt());
	}
	
	@Test
	public void registerUserShouldGenerateSalt() {
		when(usersActivationDao.findByActivationKey(anyString())).thenReturn(getUsersActivation());
		
		service.registerUser(TEST_LOGIN, TEST_PASSWORD, TEST_NAME, TEST_ACTIVATION_KEY);
		
		verify(userDao).add(userCaptor.capture());
		
		final String salt = userCaptor.getValue().getSalt();
		assertThat(salt.length()).as("salt length").isEqualTo(User.SALT_LENGTH);
		assertThat(salt).matches("^[\\p{Alnum}]+$");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void registerUserShouldThrowExceptionWhenPasswordIsNull() {
		service.registerUser(TEST_LOGIN, null, TEST_NAME, TEST_ACTIVATION_KEY);
	}
	
	@Test
	public void registerUserShouldGenerateHash() {
		when(usersActivationDao.findByActivationKey(anyString())).thenReturn(getUsersActivation());
		
		service.registerUser(TEST_LOGIN, TEST_PASSWORD, TEST_NAME, TEST_ACTIVATION_KEY);
		
		verify(userDao).add(userCaptor.capture());
		
		final int SHA1_SUM_LENGTH = 40;
		final String hash = userCaptor.getValue().getHash();
		assertThat(hash.length()).as("hash length").isEqualTo(SHA1_SUM_LENGTH);
		assertThat(hash).matches("^[\\p{Lower}\\p{Digit}]+$");
		
		// TODO: check that hash based on password
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void registerUserShouldThrowExceptionWhenLoginIsNull() {
		service.registerUser(null, TEST_PASSWORD, TEST_NAME, TEST_ACTIVATION_KEY);
	}
	
	@Test
	public void registerUserShouldPassLoginToDao() {
		when(usersActivationDao.findByActivationKey(anyString())).thenReturn(getUsersActivation());
		
		service.registerUser(TEST_LOGIN, TEST_PASSWORD, TEST_NAME, TEST_ACTIVATION_KEY);
		
		verify(userDao).add(userCaptor.capture());
		
		assertThat(userCaptor.getValue().getLogin()).isEqualTo(TEST_LOGIN);
	}
	
	@Test
	public void registerUserShouldAssignActivationDate() {
		when(usersActivationDao.findByActivationKey(anyString())).thenReturn(getUsersActivation());
		
		service.registerUser(TEST_LOGIN, TEST_PASSWORD, TEST_NAME, TEST_ACTIVATION_KEY);
		
		verify(userDao).add(userCaptor.capture());
		
		assertThat(userCaptor.getValue().getActivatedAt()).isNotNull();
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
		final User expectedUser = getValidUser();
		when(userDao.findByLogin(anyString())).thenReturn(expectedUser);
		
		final User user = service.findByLogin(TEST_LOGIN);
		
		assertThat(user).isEqualTo(expectedUser);
	}
	
	@Test
	public void findByLoginShouldPassLoginToDao() {
		service.findByLogin(TEST_LOGIN);
		verify(userDao).findByLogin(TEST_LOGIN);
	}
	
	//
	// Tests for findByLoginAndPassword()
	//
	
	@Test(expected = IllegalArgumentException.class)
	public void findByLoginAndPasswordShouldThrowExceptionWhenLoginIsNull() {
		service.findByLoginAndPassword(null, TEST_PASSWORD);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void findByLoginAndPasswordShouldThrowExceptionWhenPasswordIsNull() {
		service.findByLoginAndPassword(TEST_LOGIN, null);
	}
	
	@Test
	public void findByLoginAndPasswordShouldCallDao() {
		final User expectedUser = getValidUser();
		when(userDao.findByLogin(anyString())).thenReturn(expectedUser);
		
		final User user = service.findByLoginAndPassword(TEST_LOGIN, TEST_PASSWORD);
		
		assertThat(user).isEqualTo(expectedUser);
	}
	
	@Test
	public void findByLoginAndPasswordShouldReturnNullWhenUserNotFound() {
		when(userDao.findByLogin(anyString())).thenReturn(null);
		
		final User user = service.findByLoginAndPassword(TEST_LOGIN, TEST_PASSWORD);
		assertThat(user).isNull();
	}
	
	@Test
	public void findByLoginAndPasswordShouldReturnNullWhenInvalidPasswordProvided() {
		final User resultUser = getValidUser();
		resultUser.setHash("anyHash");
		when(userDao.findByLogin(anyString())).thenReturn(resultUser);
		
		final User user = service.findByLoginAndPassword(TEST_LOGIN, TEST_PASSWORD);
		assertThat(user).isNull();
	}
	
	@Test
	public void findByLoginAndPasswordShouldReturnUserForValidCredentials() {
		when(userDao.findByLogin(anyString())).thenReturn(getValidUser());
		
		final User user = service.findByLoginAndPassword(TEST_LOGIN, TEST_PASSWORD);
		assertThat(user).isNotNull();
		
		// TODO
		//assertThat(user).isEqualTo(resultUser);
	}
	
	@Test
	public void findByLoginAndPasswordShouldReturnNullWhenUserHasNullSalt() {
		final User resultUser = getValidUser();
		resultUser.setSalt(null);
		when(userDao.findByLogin(anyString())).thenReturn(resultUser);
		
		final User user = service.findByLoginAndPassword(TEST_LOGIN, TEST_PASSWORD);
		assertThat(user).isNull();
	}
	
	@Test
	public void findByLoginAndPasswordShouldReturnNullWhenUserHasNullHash() {
		final User resultUser = getValidUser();
		resultUser.setHash(null);
		when(userDao.findByLogin(anyString())).thenReturn(resultUser);
		
		final User user = service.findByLoginAndPassword(TEST_LOGIN, TEST_PASSWORD);
		assertThat(user).isNull();
	}
	
	private User getValidUser() {
		final User user = new User();
		user.setId(777);
		user.setLogin(TEST_LOGIN);
		user.setName(TEST_NAME);
		user.setEmail(TEST_EMAIL);
		user.setRegisteredAt(new Date());
		user.setActivatedAt(new Date());
		user.setHash(TEST_HASH);
		user.setSalt(TEST_SALT);
		
		return user;
	}
	
	private UsersActivation getUsersActivation() {
		final UsersActivation activation = new UsersActivation();
		activation.setActivationKey(TEST_ACTIVATION_KEY);
		activation.setEmail(TEST_EMAIL);
		activation.setCreatedAt(new Date());
		return activation;
	}
	
}
