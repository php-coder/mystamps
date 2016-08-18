/*
 * Copyright (C) 2009-2016 Slava Semushin <slava.semushin@gmail.com>
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

import ru.mystamps.web.dao.dto.AddUserDbDto;
import ru.mystamps.web.dao.dto.CollectionInfoDto;
import ru.mystamps.web.dao.dto.DbImageDto;
import ru.mystamps.web.dao.dto.ImageInfoDto;
import ru.mystamps.web.dao.dto.LinkEntityDto;
import ru.mystamps.web.dao.dto.SeriesInfoDto;
import ru.mystamps.web.dao.dto.SitemapInfoDto;
import ru.mystamps.web.dao.dto.SuspiciousActivityDto;
import ru.mystamps.web.dao.dto.UrlEntityDto;
import ru.mystamps.web.dao.dto.UserDetails;
import ru.mystamps.web.dao.dto.UsersActivationDto;
import ru.mystamps.web.dao.dto.UsersActivationFullDto;

final class TestObjects {
	public static final String TEST_ACTIVITY_TYPE    = "EventType";
	public static final String TEST_ACTIVITY_PAGE    = "http://example.org/some/page";
	public static final String TEST_ACTIVITY_METHOD  = "GET";
	public static final String TEST_ACTIVITY_LOGIN   = "zebra";
	public static final String TEST_ACTIVITY_IP      = "127.0.0.1";
	public static final String TEST_ACTIVITY_REFERER = "http://example.org/referer";
	public static final String TEST_ACTIVITY_AGENT   = "Some browser";
	
	public static final Integer TEST_USER_ID        = 777;
	public static final String TEST_EMAIL           = "test@example.org";
	public static final String TEST_ACTIVATION_KEY  = "1234567890";
	
	protected static final String TEST_PASSWORD     = "secret";
	
	private static final String TEST_NAME           = "Test Name";
	private static final String TEST_LOGIN          = "test";
	
	private static final Integer TEST_ENTITY_ID     = 456;
	private static final String TEST_ENTITY_NAME    = TEST_NAME;
	private static final String TEST_ENTITY_SLUG    = "test-slug";
	
	// CheckStyle: ignore LineLength for next 1 line
	private static final String TEST_HASH           = "$2a$10$Oo8A/oaKQYwt4Zi1RWGir.HHziCG267CJaqaNaNUtE/8ceysZn0za";

	private TestObjects() {
	}
	
	public static UsersActivationFullDto createUsersActivationFullDto() {
		UsersActivationFullDto activation = new UsersActivationFullDto(
			TEST_ACTIVATION_KEY,
			TEST_EMAIL,
			new Date()
		);
		return activation;
	}
	
	public static UsersActivationDto createUsersActivationDto() {
		return new UsersActivationDto(TEST_EMAIL, new Date());
	}
	
	public static UrlEntityDto createUrlEntityDto() {
		return new UrlEntityDto(TEST_ENTITY_ID, TEST_ENTITY_SLUG);
	}
	
	public static LinkEntityDto createLinkEntityDto() {
		return new LinkEntityDto(TEST_ENTITY_ID, TEST_ENTITY_SLUG, TEST_ENTITY_NAME);
	}
	
	public static AddUserDbDto createAddUserDbDto() {
		AddUserDbDto user = new AddUserDbDto();
		user.setLogin(TEST_LOGIN);
		user.setRole(UserDetails.Role.USER);
		user.setName(TEST_NAME);
		user.setEmail(TEST_EMAIL);
		user.setRegisteredAt(new Date());
		user.setActivatedAt(new Date());
		user.setHash(TEST_HASH);

		return user;
	}
	
	public static UserDetails createUserDetails() {
		final Integer anyId = 777;
		String collectionSlug = TEST_LOGIN;
		
		return new UserDetails(
			anyId,
			TEST_LOGIN,
			TEST_NAME,
			TEST_HASH,
			UserDetails.Role.USER,
			collectionSlug
		);
	}
	
	public static ImageInfoDto createImageInfoDto() {
		return new ImageInfoDto(1, "PNG");
	}
	
	public static DbImageDto createDbImageDto() {
		return new DbImageDto("PNG", "test".getBytes());
	}
	
	public static SitemapInfoDto createSitemapInfoDto() {
		return new SitemapInfoDto(1, new Date());
	}
	
	@SuppressWarnings("checkstyle:magicnumber")
	public static SeriesInfoDto createSeriesInfoDto() {
		return new SeriesInfoDto(
			12,
			13, "test-category", "Test Category",
			14, "test-country", "Test Country",
			15, 10, 2000,
			16,
			true
		);
	}
	
	@SuppressWarnings("checkstyle:magicnumber")
	public static CollectionInfoDto createCollectionInfoDto() {
		return new CollectionInfoDto(101, "test-user", "Test User");
	}
	
	public static SuspiciousActivityDto createSuspiciousActivityDto() {
		return new SuspiciousActivityDto(
			TEST_ACTIVITY_TYPE,
			new Date(),
			TEST_ACTIVITY_PAGE,
			TEST_ACTIVITY_METHOD,
			TEST_ACTIVITY_LOGIN,
			TEST_ACTIVITY_IP,
			TEST_ACTIVITY_REFERER,
			TEST_ACTIVITY_AGENT
		);
	}
	
}
