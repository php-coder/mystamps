/*
 * Copyright (C) 2009-2011 Slava Semushin <slava.semushin@gmail.com>
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

package ru.mystamps.web.tests.cases;

import static org.fest.assertions.Assertions.assertThat;

import java.net.HttpURLConnection;

import org.apache.commons.lang.RandomStringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;

import ru.mystamps.web.dao.SuspiciousActivityDao;
import ru.mystamps.web.entity.SuspiciousActivity;
import ru.mystamps.web.tests.page.NotFoundErrorPage;

import static ru.mystamps.web.tests.TranslationUtils.tr;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/DispatcherServletContext.xml"})
@TransactionConfiguration(defaultRollback = false)
public class WhenUserOpenNotExistingPage extends WhenUserAtAnyPage<NotFoundErrorPage> {
	
	private final String currentUrl;
	
	@Autowired
	private SuspiciousActivityDao suspiciousActivities;
	
	private String generateRandomUrl() {
		return String.format(
			"/tests/page-does-not-exists-%s.htm",
			RandomStringUtils.randomNumeric(5)
		);
	}
	
	public WhenUserOpenNotExistingPage() {
		super(NotFoundErrorPage.class);
		hasTitleWithoutStandardPrefix(tr("t_404_title"));
		hasResponseServerCode(HttpURLConnection.HTTP_NOT_FOUND);
		
		currentUrl = generateRandomUrl();
		page.open(currentUrl);
	}
	
	@Test
	public void shouldHaveStandardStructure() {
		checkStandardStructure();
	}
	
	@Test
	public void shouldExistsErrorMessage() {
		assertThat(page.getErrorMessage()).isEqualTo(tr("t_404_description", "\n"));
	}
	
	@Test
	public void shouldExistsErrorCode() {
		assertThat(page.getErrorCode()).isEqualTo("404");
	}
	
	@Test
	@Transactional(readOnly = true)
	public void incidentShouldBeLoggedToDatabase() {
		SuspiciousActivity activity =
			suspiciousActivities.findByPage(currentUrl);
		
		System.out.println("current = " + currentUrl);
		assertThat(activity).isNotNull();
		System.out.println("page = " + activity.getPage());
		assertThat(activity.getType().getName()).isEqualTo("PageNotFound");
	}
	
}
