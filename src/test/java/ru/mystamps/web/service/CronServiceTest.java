/*
 * Copyright (C) 2012 Slava Semushin <slava.semushin@gmail.com>
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

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.fest.assertions.api.Assertions.assertThat;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ru.mystamps.web.dao.UsersActivationDao;
import ru.mystamps.web.entity.UsersActivation;

public class CronServiceTest {
	
	@Mock
	private UsersActivationDao usersActivationDao;
	
	@Captor
	private ArgumentCaptor<Date> dateCaptor;
	
	@InjectMocks
	private CronService service = new CronService();
	
	@BeforeMethod
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}
	
	//
	// Tests for purgeUsersActivations()
	//
	
	@Test
	public void purgeUsersActivationsShouldGetExpiredActivationsFromDao() {
		when(usersActivationDao.findByCreatedAtLessThan(any(Date.class)))
			.thenReturn(Collections.<UsersActivation>emptyList());
		
		service.purgeUsersActivations();
		
		verify(usersActivationDao).findByCreatedAtLessThan(any(Date.class));
	}
	
	@Test
	public void purgeUsersActivationsShouldPassExpiredDateToDao() {
		when(usersActivationDao.findByCreatedAtLessThan(any(Date.class)))
			.thenReturn(Collections.<UsersActivation>emptyList());
		
		service.purgeUsersActivations();
		
		verify(usersActivationDao).findByCreatedAtLessThan(dateCaptor.capture());
		
		final Calendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());
		calendar.add(
			Calendar.DAY_OF_MONTH,
			-(CronService.PURGE_UNACTIVATED_REQUEST_AFTER_DAYS)
		);
		
		// Truncate seconds in dates to prevent test fail due to different milliseconds in dates
		final Date expectedDate = DateUtils.truncate(calendar.getTime(), Calendar.SECOND);
		final Date passedDate   = DateUtils.truncate(dateCaptor.getValue(), Calendar.SECOND);
		
		assertThat(passedDate.equals(expectedDate)).isTrue();
	}
	
	@Test(expectedExceptions = IllegalStateException.class)
	public void purgeUsersActivationsShouldThrowExceptionWhenNullActivationsWasReturned() {
		when(usersActivationDao.findByCreatedAtLessThan(any(Date.class))).thenReturn(null);
		
		service.purgeUsersActivations();
	}
	
	@Test
	public void purgeUsersActivationsShouldDeleteExpiredActivations() {
		final List<UsersActivation> expectedActivations =
			Collections.singletonList(UserServiceTest.getUsersActivation());
		when(usersActivationDao.findByCreatedAtLessThan(any(Date.class)))
			.thenReturn(expectedActivations);
		
		service.purgeUsersActivations();
		
		verify(usersActivationDao).delete(eq(expectedActivations));
	}
	
	@Test
	public void purgeUsersActivationsShouldDoNothingIfNoActivations() {
		when(usersActivationDao.findByCreatedAtLessThan(any(Date.class)))
			.thenReturn(Collections.<UsersActivation>emptyList());
		
		service.purgeUsersActivations();
		
		verify(usersActivationDao, never()).delete(any(Iterable.class));
	}
	
}

