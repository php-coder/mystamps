/*
 * Copyright (C) 2009-2025 Slava Semushin <slava.semushin@gmail.com>
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

import ru.mystamps.web.common.LinkEntityDto;
import ru.mystamps.web.feature.account.UsersActivationDto;
import ru.mystamps.web.feature.account.UsersActivationFullDto;
import ru.mystamps.web.feature.image.ImageDto;
import ru.mystamps.web.feature.image.ImageInfoDto;
import ru.mystamps.web.feature.site.SuspiciousActivityDto;
import ru.mystamps.web.tests.Random;

import java.nio.charset.StandardCharsets;
import java.util.Date;

public final class TestObjects {
	public static final String TEST_ACTIVITY_TYPE    = "EventType";
	public static final String TEST_ACTIVITY_PAGE    = "http://example.org/some/page";
	public static final String TEST_ACTIVITY_METHOD  = "GET";
	public static final String TEST_ACTIVITY_LOGIN   = "zebra";
	public static final String TEST_ACTIVITY_IP      = "127.0.0.1";
	public static final String TEST_ACTIVITY_REFERER = "http://example.org/referer";
	public static final String TEST_ACTIVITY_AGENT   = "Some browser";
	
	public static final String TEST_EMAIL           = "test@example.org";
	public static final String TEST_ACTIVATION_KEY  = "1234567890";
	
	private static final String TEST_NAME           = "Test Name";
	
	private static final String TEST_ENTITY_NAME    = TEST_NAME;
	private static final String TEST_ENTITY_SLUG    = "test-slug";

	private TestObjects() {
	}
	
	public static UsersActivationFullDto createUsersActivationFullDto() {
		UsersActivationFullDto activation = new UsersActivationFullDto(
			TEST_ACTIVATION_KEY,
			TEST_EMAIL,
			Random.date()
		);
		return activation;
	}
	
	public static UsersActivationDto createUsersActivationDto() {
		return new UsersActivationDto(TEST_EMAIL, new Date());
	}
	
	public static LinkEntityDto createLinkEntityDto() {
		return new LinkEntityDto(Random.id(), TEST_ENTITY_SLUG, TEST_ENTITY_NAME);
	}
	
	public static ImageInfoDto createImageInfoDto() {
		return new ImageInfoDto(Random.id(), "PNG");
	}
	
	public static ImageDto createImageDto() {
		return new ImageDto("PNG", "test".getBytes(StandardCharsets.UTF_8));
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
