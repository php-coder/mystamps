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
package ru.mystamps.web.support.spring.security;

import java.util.Collections;
import java.util.Date;

import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;

import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;

import static org.fest.assertions.api.Assertions.assertThat;

import static org.mockito.Mockito.when;

import ru.mystamps.web.entity.User;
import ru.mystamps.web.service.AuthService;

@PrepareForTest(SecurityContextHolder.class)
@RunWith(MockitoJUnitRunner.class)
public class SpringSecurityAuthServiceTest {
	
	private static final String TEST_NAME           = "Test Name";
	private static final String TEST_LOGIN          = "test";
	private static final String TEST_PASSWORD       = "secret";
	private static final String TEST_SALT           = "salt";
	
	// sha1(TEST_SALT + "{" + TEST_PASSWORD + "}")
	private static final String TEST_HASH           = "b0dd94c84e784ddb1e9a83c8a2e8f403846647b9";
	
	private static final String TEST_EMAIL          = "test@example.org";
	
	@Rule
	public PowerMockRule powerMockRule = new PowerMockRule(); // NOCHECKSTYLE
	
	@Mock
	private PasswordEncoder encoder;
	
	@Mock
	private Authentication authentication;
	
	@Mock
	private SecurityContext securityContext;
	
	@InjectMocks
	private AuthService service = new SpringSecurityAuthService();
	
	//
	// Tests for getCurrentUser()
	//
	
	@Test(expected = IllegalStateException.class)
	public void getCurrentUserShouldThrowExceptionIfSecurityContextIsNull() {
		PowerMockito.mockStatic(SecurityContextHolder.class);
		when(SecurityContextHolder.getContext()).thenReturn(null);
		
		service.getCurrentUser();
	}
	
	@Test
	public void getCurrentUserShouldReturnNullWhenAuthenticationIsNull() {
		PowerMockito.mockStatic(SecurityContextHolder.class);
		when(securityContext.getAuthentication()).thenReturn(null);
		when(SecurityContextHolder.getContext()).thenReturn(securityContext);
		
		assertThat(service.getCurrentUser()).isNull();
	}
	
	@Test
	public void getCurrentUserShouldReturnNullWhenPrincipalIsNull() {
		PowerMockito.mockStatic(SecurityContextHolder.class);
		when(authentication.getPrincipal()).thenReturn(null);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		when(SecurityContextHolder.getContext()).thenReturn(securityContext);
		
		assertThat(service.getCurrentUser()).isNull();
	}
	
	@Test(expected = IllegalStateException.class)
	public void getCurrentUserShouldThrowExceptionWhenPrincipalHasUnknownType() {
		PowerMockito.mockStatic(SecurityContextHolder.class);
		when(authentication.getPrincipal()).thenReturn(new Object());
		when(securityContext.getAuthentication()).thenReturn(authentication);
		when(SecurityContextHolder.getContext()).thenReturn(securityContext);
		
		assertThat(service.getCurrentUser()).isNull();
	}
	
	@Test
	public void getCurrentUserShouldReturnUser() {
		PowerMockito.mockStatic(SecurityContextHolder.class);
		User expectedUser = getValidUser();
		CustomUserDetails userDetails = new CustomUserDetails(
			expectedUser,
			Collections.<GrantedAuthority>emptyList()
		);
		when(authentication.getPrincipal()).thenReturn(userDetails);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		when(SecurityContextHolder.getContext()).thenReturn(securityContext);
		
		assertThat(service.getCurrentUser()).isEqualTo(expectedUser);
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
	
}
