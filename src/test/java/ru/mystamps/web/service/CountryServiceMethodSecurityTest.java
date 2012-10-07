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

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import org.springframework.security.access.AccessDeniedException;

import ru.mystamps.web.config.ApplicationContext;
import ru.mystamps.web.config.TestContext;
import ru.mystamps.web.model.AddCountryForm;

@ActiveProfiles("test")
@ContextConfiguration(
	loader = AnnotationConfigContextLoader.class,
	classes = {
		ApplicationContext.class,
		TestContext.class
	}
)
@RunWith(SpringJUnit4ClassRunner.class)
public class CountryServiceMethodSecurityTest {
	
	@Inject
	private CountryService service;
	
	@Value("${valid_user_login}")
	private String validUserLogin;
	
	@Value("${valid_user_password}")
	private String validUserPassword;
	
	//
	// Tests for add()
	//
	
	@Test(expected = AccessDeniedException.class)
	public void addShouldDenyAccessToAnonymousUser() {
		AuthUtils.authenticateAsAnonymous();
		
		service.add(null);
	}
	
	@Test
	public void addShouldAllowAccessToAuthenticatedUser() {
		AuthUtils.authenticateAsUser(validUserLogin, validUserPassword);
		
		final AddCountryForm form = new AddCountryForm();
		form.setName("Any Country Name");
		service.add(form);
	}
	
}
