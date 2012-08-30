/*
 * Copyright (C) 2009-2012 Slava Semushin <slava.semushin@gmail.com>
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

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.fest.assertions.api.Assertions.assertThat;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

import ru.mystamps.web.dao.SuspiciousActivityDao;
import ru.mystamps.web.dao.SuspiciousActivityTypeDao;
import ru.mystamps.web.dao.UserDao;
import ru.mystamps.web.entity.User;
import ru.mystamps.web.entity.SuspiciousActivity;
import ru.mystamps.web.entity.SuspiciousActivityType;
import ru.mystamps.web.tests.fest.DateAssert;

@RunWith(MockitoJUnitRunner.class)
public class SiteServiceTest {
	private static final String TEST_PAGE         = "http://example.org/some/page";
	private static final String TEST_IP           = "127.0.0.1";
	private static final String TEST_REFERER_PAGE = "http://example.org/referer";
	private static final String TEST_USER_AGENT   = "Some browser";
	
	@Mock
	private SuspiciousActivityDao suspiciousActivityDao;
	
	@Mock
	private SuspiciousActivityTypeDao suspiciousActivityTypeDao;
	
	@Mock
	private UserDao userDao;
	
	@Captor
	private ArgumentCaptor<SuspiciousActivity> activityCaptor;
	
	@InjectMocks
	private SiteService service = new SiteService();
	
	//
	// Tests for logAboutAbsentPage()
	//
	
	@Test
	public void logAboutAbsentPageShouldCallDao() {
		when(suspiciousActivityTypeDao.findByName(anyString())).thenReturn(getPageNotFoundType());
		
		service.logAboutAbsentPage(TEST_PAGE, null, null, null, null);
		
		verify(suspiciousActivityDao).save(any(SuspiciousActivity.class));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void logAboutAbsentPageShouldThrowExceptionWhenActivityTypeNotFound() {
		when(suspiciousActivityTypeDao.findByName(anyString())).thenReturn(null);
		
		service.logAboutAbsentPage(null, null, null, null, null);
	}
	
	@Test
	public void logAboutAbsentPageShouldPassActivityTypeToDao() {
		final SuspiciousActivityType expectedType = getPageNotFoundType();
		when(suspiciousActivityTypeDao.findByName(anyString())).thenReturn(expectedType);
		
		service.logAboutAbsentPage(TEST_PAGE, null, null, null, null);
		
		verify(suspiciousActivityDao).save(activityCaptor.capture());
		
		assertThat(activityCaptor.getValue().getType()).isEqualTo(expectedType);
	}
	
	@Test
	public void logAboutAbsentPageShouldAssignOccuredAtToCurrentDate() {
		when(suspiciousActivityTypeDao.findByName(anyString())).thenReturn(getPageNotFoundType());
		
		service.logAboutAbsentPage(TEST_PAGE, null, null, null, null);
		
		verify(suspiciousActivityDao).save(activityCaptor.capture());
		
		DateAssert.assertThat(activityCaptor.getValue().getOccuredAt()).isCurrentDate();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void logAboutAbsentPageShouldThrowExceptionWhenPageIsNull() {
		when(suspiciousActivityTypeDao.findByName(anyString())).thenReturn(getPageNotFoundType());
		
		service.logAboutAbsentPage(null, null, null, null, null);
	}
	
	@Test
	public void logAboutAbsentPageShouldPassPageToDao() {
		when(suspiciousActivityTypeDao.findByName(anyString())).thenReturn(getPageNotFoundType());
		
		service.logAboutAbsentPage(TEST_PAGE, null, null, null, null);
		
		verify(suspiciousActivityDao).save(activityCaptor.capture());
		
		assertThat(activityCaptor.getValue().getPage()).isEqualTo(TEST_PAGE);
	}
	
	@Test
	public void logAboutAbsentPageShouldPassNullToDaoForUnknownUser() {
		when(suspiciousActivityTypeDao.findByName(anyString())).thenReturn(getPageNotFoundType());
		
		service.logAboutAbsentPage(TEST_PAGE, null, null, null, null);
		
		verify(suspiciousActivityDao).save(activityCaptor.capture());
		
		assertThat(activityCaptor.getValue().getUser()).isNull();
	}
	
	@Test
	public void logAboutAbsentPageShouldPassNullToDaoForNotExistingUser() {
		final Integer notExistingUserId = 1010;
		when(suspiciousActivityTypeDao.findByName(anyString())).thenReturn(getPageNotFoundType());
		when(userDao.findOne(any(Integer.class))).thenReturn(null);
		
		service.logAboutAbsentPage(TEST_PAGE, notExistingUserId, null, null, null);
		
		verify(userDao).findOne(notExistingUserId);
		verify(suspiciousActivityDao).save(activityCaptor.capture());
		
		assertThat(activityCaptor.getValue().getUser()).isNull();
	}
	
	@Test
	public void logAboutAbsentPageShouldPassUserToDao() {
		final User user = getUser();
		when(suspiciousActivityTypeDao.findByName(anyString())).thenReturn(getPageNotFoundType());
		when(userDao.findOne(any(Integer.class))).thenReturn(user);
		
		service.logAboutAbsentPage(TEST_PAGE, user.getId(), null, null, null);
		
		verify(userDao).findOne(user.getId());
		verify(suspiciousActivityDao).save(activityCaptor.capture());
		
		assertThat(activityCaptor.getValue().getUser()).isEqualTo(user);
	}
	
	@Test
	public void logAboutAbsentPageShouldPassIpToDao() {
		when(suspiciousActivityTypeDao.findByName(anyString())).thenReturn(getPageNotFoundType());
		
		service.logAboutAbsentPage(TEST_PAGE, null, TEST_IP, null, null);
		
		verify(suspiciousActivityDao).save(activityCaptor.capture());
		
		assertThat(activityCaptor.getValue().getIp()).isEqualTo(TEST_IP);
	}
	
	@Test
	public void logAboutAbsentPageShouldPassEmptyStringToDaoForUnknownIp() {
		when(suspiciousActivityTypeDao.findByName(anyString())).thenReturn(getPageNotFoundType());
		
		service.logAboutAbsentPage(TEST_PAGE, null, null, null, null);
		
		verify(suspiciousActivityDao).save(activityCaptor.capture());
		
		assertThat(activityCaptor.getValue().getIp()).isEmpty();
	}
	
	@Test
	public void logAboutAbsentPageShouldPassRefererToDao() {
		when(suspiciousActivityTypeDao.findByName(anyString())).thenReturn(getPageNotFoundType());
		
		service.logAboutAbsentPage(TEST_PAGE, null, null, TEST_REFERER_PAGE, null);
		
		verify(suspiciousActivityDao).save(activityCaptor.capture());
		
		assertThat(activityCaptor.getValue().getRefererPage()).isEqualTo(TEST_REFERER_PAGE);
	}
	
	@Test
	public void logAboutAbsentPageShouldPassEmptyStringToDaoForUnknownReferer() {
		when(suspiciousActivityTypeDao.findByName(anyString())).thenReturn(getPageNotFoundType());
		
		service.logAboutAbsentPage(TEST_PAGE, null, null, null, null);
		
		verify(suspiciousActivityDao).save(activityCaptor.capture());
		
		assertThat(activityCaptor.getValue().getRefererPage()).isEmpty();
	}
	
	@Test
	public void logAboutAbsentPageShouldPassUserAgentToDao() {
		when(suspiciousActivityTypeDao.findByName(anyString())).thenReturn(getPageNotFoundType());
		
		service.logAboutAbsentPage(TEST_PAGE, null, null, null, TEST_USER_AGENT);
		
		verify(suspiciousActivityDao).save(activityCaptor.capture());
		
		assertThat(activityCaptor.getValue().getUserAgent()).isEqualTo(TEST_USER_AGENT);
	}
	
	@Test
	public void logAboutAbsentPageShouldPassEmptyStringToDaoForUnknownUserAgent() {
		when(suspiciousActivityTypeDao.findByName(anyString())).thenReturn(getPageNotFoundType());
		
		service.logAboutAbsentPage(TEST_PAGE, null, null, null, null);
		
		verify(suspiciousActivityDao).save(activityCaptor.capture());
		
		assertThat(activityCaptor.getValue().getUserAgent()).isEmpty();
	}
	
	//
	// Tests for logAboutFailedAuthentication()
	//
	
	@Test
	public void logAboutFailedAuthenticationShouldCallDao() {
		when(suspiciousActivityTypeDao.findByName(anyString())).thenReturn(getAuthFailedType());
		
		service.logAboutFailedAuthentication(TEST_PAGE, null, null, null, null);
		
		verify(suspiciousActivityDao).save(any(SuspiciousActivity.class));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void logAboutFailedAuthenticationShouldThrowExceptionWhenActivityTypeNotFound() {
		when(suspiciousActivityTypeDao.findByName(anyString())).thenReturn(null);
		
		service.logAboutFailedAuthentication(null, null, null, null, null);
	}
	
	@Test
	public void logAboutFailedAuthenticationShouldPassActivityTypeToDao() {
		final SuspiciousActivityType expectedType = getAuthFailedType();
		when(suspiciousActivityTypeDao.findByName(anyString())).thenReturn(expectedType);
		
		service.logAboutFailedAuthentication(TEST_PAGE, null, null, null, null);
		
		verify(suspiciousActivityDao).save(activityCaptor.capture());
		
		assertThat(activityCaptor.getValue().getType()).isEqualTo(expectedType);
	}
	
	@Test
	public void logAboutFailedAuthenticationShouldAssignOccuredAtToCurrentDate() {
		when(suspiciousActivityTypeDao.findByName(anyString())).thenReturn(getAuthFailedType());
		
		service.logAboutFailedAuthentication(TEST_PAGE, null, null, null, null);
		
		verify(suspiciousActivityDao).save(activityCaptor.capture());
		
		DateAssert.assertThat(activityCaptor.getValue().getOccuredAt()).isCurrentDate();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void logAboutFailedAuthenticationShouldThrowExceptionWhenPageIsNull() {
		when(suspiciousActivityTypeDao.findByName(anyString())).thenReturn(getAuthFailedType());
		
		service.logAboutFailedAuthentication(null, null, null, null, null);
	}
	
	@Test
	public void logAboutFailedAuthenticationShouldPassPageToDao() {
		when(suspiciousActivityTypeDao.findByName(anyString())).thenReturn(getAuthFailedType());
		
		service.logAboutFailedAuthentication(TEST_PAGE, null, null, null, null);
		
		verify(suspiciousActivityDao).save(activityCaptor.capture());
		
		assertThat(activityCaptor.getValue().getPage()).isEqualTo(TEST_PAGE);
	}
	
	@Test
	public void logAboutFailedAuthenticationShouldPassNullToDaoForUnknownUser() {
		when(suspiciousActivityTypeDao.findByName(anyString())).thenReturn(getAuthFailedType());
		
		service.logAboutFailedAuthentication(TEST_PAGE, null, null, null, null);
		
		verify(suspiciousActivityDao).save(activityCaptor.capture());
		
		assertThat(activityCaptor.getValue().getUser()).isNull();
	}
	
	@Test
	public void logAboutFailedAuthenticationShouldPassNullToDaoForNotExistingUser() {
		final User user = getUser();
		final Integer userId = user.getId();
		when(suspiciousActivityTypeDao.findByName(anyString())).thenReturn(getAuthFailedType());
		when(userDao.findOne(any(Integer.class))).thenReturn(null);
		
		service.logAboutFailedAuthentication(TEST_PAGE, userId, null, null, null);
		
		verify(userDao).findOne(userId);
		verify(suspiciousActivityDao).save(activityCaptor.capture());
		
		assertThat(activityCaptor.getValue().getUser()).isNull();
	}
	
	@Test
	public void logAboutFailedAuthenticationShouldPassUserToDao() {
		final User user = getUser();
		final Integer userId = user.getId();
		when(suspiciousActivityTypeDao.findByName(anyString())).thenReturn(getAuthFailedType());
		when(userDao.findOne(any(Integer.class))).thenReturn(user);
		
		service.logAboutFailedAuthentication(TEST_PAGE, userId, null, null, null);
		
		verify(userDao).findOne(userId);
		verify(suspiciousActivityDao).save(activityCaptor.capture());
		
		assertThat(activityCaptor.getValue().getUser()).isEqualTo(user);
	}
	
	@Test
	public void logAboutFailedAuthenticationShouldPassIpToDao() {
		when(suspiciousActivityTypeDao.findByName(anyString())).thenReturn(getAuthFailedType());
		
		service.logAboutFailedAuthentication(TEST_PAGE, null, TEST_IP, null, null);
		
		verify(suspiciousActivityDao).save(activityCaptor.capture());
		
		assertThat(activityCaptor.getValue().getIp()).isEqualTo(TEST_IP);
	}
	
	@Test
	public void logAboutFailedAuthenticationShouldPassEmptyStringToDaoForUnknownIp() {
		when(suspiciousActivityTypeDao.findByName(anyString())).thenReturn(getAuthFailedType());
		
		service.logAboutFailedAuthentication(TEST_PAGE, null, null, null, null);
		
		verify(suspiciousActivityDao).save(activityCaptor.capture());
		
		assertThat(activityCaptor.getValue().getIp()).isEmpty();
	}
	
	@Test
	public void logAboutFailedAuthenticationShouldPassRefererToDao() {
		when(suspiciousActivityTypeDao.findByName(anyString())).thenReturn(getAuthFailedType());
		
		service.logAboutFailedAuthentication(TEST_PAGE, null, null, TEST_REFERER_PAGE, null);
		
		verify(suspiciousActivityDao).save(activityCaptor.capture());
		
		assertThat(activityCaptor.getValue().getRefererPage()).isEqualTo(TEST_REFERER_PAGE);
	}
	
	@Test
	public void logAboutFailedAuthenticationShouldPassEmptyStringToDaoForUnknownReferer() {
		when(suspiciousActivityTypeDao.findByName(anyString())).thenReturn(getAuthFailedType());
		
		service.logAboutFailedAuthentication(TEST_PAGE, null, null, null, null);
		
		verify(suspiciousActivityDao).save(activityCaptor.capture());
		
		assertThat(activityCaptor.getValue().getRefererPage()).isEmpty();
	}
	
	@Test
	public void logAboutFailedAuthenticationShouldPassUserAgentToDao() {
		when(suspiciousActivityTypeDao.findByName(anyString())).thenReturn(getAuthFailedType());
		
		service.logAboutFailedAuthentication(TEST_PAGE, null, null, null, TEST_USER_AGENT);
		
		verify(suspiciousActivityDao).save(activityCaptor.capture());
		
		assertThat(activityCaptor.getValue().getUserAgent()).isEqualTo(TEST_USER_AGENT);
	}
	
	@Test
	public void logAboutFailedAuthenticationShouldPassEmptyStringToDaoForUnknownUserAgent() {
		when(suspiciousActivityTypeDao.findByName(anyString())).thenReturn(getAuthFailedType());
		
		service.logAboutFailedAuthentication(TEST_PAGE, null, null, null, null);
		
		verify(suspiciousActivityDao).save(activityCaptor.capture());
		
		assertThat(activityCaptor.getValue().getUserAgent()).isEmpty();
	}
	
	private SuspiciousActivityType getPageNotFoundType() {
		final SuspiciousActivityType type = new SuspiciousActivityType();
		type.setId(1);
		type.setName("PageNotFound");
		return type;
	}
	
	private SuspiciousActivityType getAuthFailedType() {
		final SuspiciousActivityType type = new SuspiciousActivityType();
		type.setId(2);
		type.setName("AuthenticationFailed");
		return type;
	}
	
	private User getUser() {
		final Integer anyId = 777;
		final User user = new User();
		user.setId(anyId);
		user.setLogin("test");
		user.setName("Test Name");
		user.setEmail("test@example.org");
		user.setRegisteredAt(new Date());
		user.setActivatedAt(new Date());
		user.setHash("da00ec2e6ff9ed4d342b24a16e262c82f3c8b10b");
		user.setSalt("salt");
		
		return user;
	}
	
}
