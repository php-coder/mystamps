/*
 * Copyright (C) 2009-2017 Slava Semushin <slava.semushin@gmail.com>
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

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Date;

// CheckStyle: ignore AvoidStarImportCheck for next 2 lines
import ru.mystamps.web.controller.dto.AddSeriesForm;
import ru.mystamps.web.dao.dto.*;
import ru.mystamps.web.service.dto.AddSeriesDto;
import ru.mystamps.web.tests.Random;
import ru.mystamps.web.util.SlugUtils;

import static io.qala.datagen.RandomShortApi.bool;

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
	
	protected static final String TEST_PASSWORD     = "secret";
	
	private static final String TEST_NAME           = "Test Name";
	private static final String TEST_LOGIN          = "test";
	
	private static final String TEST_URL            = "test.example.org";
	private static final BigDecimal TEST_PRICE      = new BigDecimal("100.99");
	
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
	
	public static LinkEntityDto createLinkEntityDto() {
		return new LinkEntityDto(Random.id(), TEST_ENTITY_SLUG, TEST_ENTITY_NAME);
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
		String collectionSlug = TEST_LOGIN;
		
		return new UserDetails(
			Random.userId(),
			TEST_LOGIN,
			TEST_NAME,
			TEST_HASH,
			UserDetails.Role.USER,
			collectionSlug
		);
	}
	
	public static ImageInfoDto createImageInfoDto() {
		return new ImageInfoDto(Random.id(), "PNG");
	}
	
	public static DbImageDto createDbImageDto() {
		return new DbImageDto("PNG", "test".getBytes(StandardCharsets.UTF_8));
	}
	
	public static SitemapInfoDto createSitemapInfoDto() {
		return new SitemapInfoDto(Random.id(), new Date());
	}
	
	@SuppressWarnings("checkstyle:magicnumber")
	public static SeriesInfoDto createSeriesInfoDto() {
		return new SeriesInfoDto(
			Random.id(),
			new LinkEntityDto(Random.id(), "test-category", "Test Category"),
			new LinkEntityDto(Random.id(), "test-country", "Test Country"),
			15, 10, Random.issueYear(),
			16,
			bool()
		);
	}
	
	public static SeriesFullInfoDto createSeriesFullInfoDto() {
		SeriesInfoDto info = createSeriesInfoDto();
		return new SeriesFullInfoDto(
			info.getId(),
			info.getCategory(),
			info.getCountry(),
			info.getReleaseDay(), info.getReleaseMonth(), info.getReleaseYear(),
			info.getQuantity(),
			info.getPerforated(),
			"this is a full info",
			Random.userId(),
			TEST_PRICE, Currency.EUR.toString(),
			TEST_PRICE, Currency.USD.toString(),
			TEST_PRICE, Currency.EUR.toString(),
			TEST_PRICE, Currency.GBP.toString()
		);
	}
	
	@SuppressWarnings("checkstyle:magicnumber")
	public static CollectionInfoDto createCollectionInfoDto() {
		return new CollectionInfoDto(Random.id(), "test-user", "Test User");
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
	
	/**
	 * @author Sergey Chechenev
	 */
	public static PurchaseAndSaleDto createPurchaseAndSaleDto() {
		return new PurchaseAndSaleDto(
			new Date(),
			TEST_NAME,
			TEST_URL,
			TEST_NAME,
			TEST_URL,
			TEST_URL,
			TEST_PRICE,
			Currency.RUB,
			TEST_PRICE,
			Currency.USD
		);
	}
	
	public static EntityWithIdDto createEntityWithIdDto() {
		return new EntityWithIdDto(Random.id(), TEST_ENTITY_NAME);
	}
	
	public static ImportRequestDto createImportRequestDto() {
		return new ImportRequestDto(Random.url(), Random.importRequestStatus(), null);
	}
	
	public static ParsedDataDto createParsedDataDto() {
		String categoryName = Random.categoryName();
		String categorySlug = SlugUtils.slugify(categoryName);
		
		String countryName = Random.countryName();
		String countrySlug = SlugUtils.slugify(countryName);
		
		return new ParsedDataDto(
			new LinkEntityDto(Random.id(), categorySlug, categoryName),
			new LinkEntityDto(Random.id(), countrySlug, countryName),
			Random.url(),
			Random.issueYear()
		);
	}
	
	public static CategoryDto createCategoryDto() {
		String name = Random.categoryName();
		String slug = SlugUtils.slugify(name);
		// @todo #548 Introduce name generator for top categories
		String parentName = Random.categoryName();
		return new CategoryDto(name, slug, parentName);
	}
	
	public static TransactionParticipantDto createTransactionParticipantDto() {
		String name = Random.categoryName();
		// @todo #702 TestObjects: introduce name generator for transaction participant groups
		String parentName = Random.categoryName();
		return new TransactionParticipantDto(Random.id(), name, parentName);
	}
	
	public static AddSeriesDto createAddSeriesDto() {
		// @todo #734 TestObjects.createAddSeriesDto(): return randomized values
		return new AddSeriesForm();
	}
	
}
