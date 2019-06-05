/*
 * Copyright (C) 2009-2019 Slava Semushin <slava.semushin@gmail.com>
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

import ru.mystamps.web.dao.dto.EntityWithParentDto;
import ru.mystamps.web.dao.dto.LinkEntityDto;
import ru.mystamps.web.feature.account.AddUserDbDto;
import ru.mystamps.web.feature.account.UserDetails;
import ru.mystamps.web.feature.account.UsersActivationDto;
import ru.mystamps.web.feature.account.UsersActivationFullDto;
import ru.mystamps.web.feature.collection.AddToCollectionDto;
import ru.mystamps.web.feature.collection.AddToCollectionForm;
import ru.mystamps.web.feature.collection.CollectionInfoDto;
import ru.mystamps.web.feature.collection.SeriesInCollectionWithPriceDto;
import ru.mystamps.web.feature.image.ImageDto;
import ru.mystamps.web.feature.image.ImageInfoDto;
import ru.mystamps.web.feature.participant.AddParticipantDto;
import ru.mystamps.web.feature.participant.AddParticipantForm;
import ru.mystamps.web.feature.participant.EntityWithIdDto;
import ru.mystamps.web.feature.series.AddSeriesDto;
import ru.mystamps.web.feature.series.AddSeriesForm;
import ru.mystamps.web.feature.series.PurchaseAndSaleDto;
import ru.mystamps.web.feature.series.SeriesFullInfoDto;
import ru.mystamps.web.feature.series.SeriesInfoDto;
import ru.mystamps.web.feature.series.SeriesLinkDto;
import ru.mystamps.web.feature.series.SitemapInfoDto;
import ru.mystamps.web.feature.series.importing.ImportRequestDto;
import ru.mystamps.web.feature.series.importing.ImportRequestFullInfo;
import ru.mystamps.web.feature.series.importing.ImportRequestInfo;
import ru.mystamps.web.feature.series.importing.ImportSeriesSalesForm;
import ru.mystamps.web.feature.series.importing.RawParsedDataDto;
import ru.mystamps.web.feature.series.importing.SeriesExtractedInfo;
import ru.mystamps.web.feature.series.importing.SeriesParsedDataDto;
import ru.mystamps.web.feature.series.importing.sale.SeriesSaleParsedDataDto;
import ru.mystamps.web.feature.series.importing.sale.SeriesSalesParsedDataDbDto;
import ru.mystamps.web.feature.series.sale.AddSeriesSalesDto;
import ru.mystamps.web.feature.site.SuspiciousActivityDto;
import ru.mystamps.web.tests.Random;
import ru.mystamps.web.util.SlugUtils;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;

import static io.qala.datagen.RandomShortApi.bool;
import static io.qala.datagen.RandomShortApi.nullOr;

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
	private static final String TEST_URL            = "test.example.org";
	
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
	
	public static AddUserDbDto createAddUserDbDto() {
		AddUserDbDto user = new AddUserDbDto();
		user.setLogin(Random.login());
		user.setRole(UserDetails.Role.USER);
		user.setName(TEST_NAME);
		user.setEmail(TEST_EMAIL);
		user.setRegisteredAt(Random.date());
		user.setActivatedAt(Random.date());
		user.setHash(TEST_HASH);

		return user;
	}
	
	public static UserDetails createUserDetails() {
		return new UserDetails(
			Random.userId(),
			Random.login(),
			TEST_NAME,
			TEST_HASH,
			UserDetails.Role.USER,
			Random.collectionSlug()
		);
	}
	
	public static ImageInfoDto createImageInfoDto() {
		return new ImageInfoDto(Random.id(), "PNG");
	}
	
	public static ImageDto createImageDto() {
		return new ImageDto("PNG", "test".getBytes(StandardCharsets.UTF_8));
	}
	
	public static SitemapInfoDto createSitemapInfoDto() {
		return new SitemapInfoDto(Random.id(), Random.date());
	}
	
	@SuppressWarnings("checkstyle:magicnumber")
	public static SeriesInfoDto createSeriesInfoDto() {
		String category = Random.categoryName();
		String country = Random.countryName();
		
		return new SeriesInfoDto(
			Random.id(),
			new LinkEntityDto(Random.id(), SlugUtils.slugify(category), category),
			new LinkEntityDto(Random.id(), SlugUtils.slugify(country), country),
			15, 10, Random.issueYear(),
			16,
			Random.perforated()
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
			nullOr(Random.price()),
			nullOr(Random.price()),
			nullOr(Random.price()),
			nullOr(Random.price()),
			nullOr(Random.price()),
			nullOr(Random.price())
		);
	}
	
	public static CollectionInfoDto createCollectionInfoDto() {
		return new CollectionInfoDto(Random.id(), Random.collectionSlug(), "Test User");
	}
	
	public static SeriesInCollectionWithPriceDto createSeriesInCollectionWithPriceDto() {
		return new SeriesInCollectionWithPriceDto(
			Random.id(),
			Random.issueYear(),
			Random.quantity(),
			Random.quantity(),
			Random.perforated(),
			Random.countryName(),
			Random.price(),
			Random.currency()
		);
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
			Random.date(),
			TEST_NAME,
			TEST_URL,
			TEST_NAME,
			TEST_URL,
			TEST_URL,
			Random.price(),
			Random.currency(),
			Random.price(),
			Random.currency()
		);
	}
	
	public static EntityWithIdDto createEntityWithIdDto() {
		return new EntityWithIdDto(Random.id(), TEST_ENTITY_NAME);
	}
	
	public static ImportRequestDto createImportRequestDto() {
		return new ImportRequestDto(Random.url(), Random.importRequestStatus(), Random.id());
	}
	
	public static SeriesParsedDataDto createSeriesParsedDataDto() {
		String categoryName = Random.categoryName();
		String categorySlug = SlugUtils.slugify(categoryName);
		
		String countryName = Random.countryName();
		String countrySlug = SlugUtils.slugify(countryName);
		
		return new SeriesParsedDataDto(
			new LinkEntityDto(Random.id(), categorySlug, categoryName),
			new LinkEntityDto(Random.id(), countrySlug, countryName),
			Random.url(),
			Random.issueYear(),
			Random.quantity(),
			Random.perforated(),
			String.join(",", Random.michelNumbers())
		);
	}
	
	public static RawParsedDataDto createRawParsedDataDto() {
		return new RawParsedDataDto(
			Random.categoryName(),
			Random.countryName(),
			Random.url(),
			Random.issueYear().toString(),
			Random.quantity().toString(),
			String.valueOf(Random.perforated()),
			Random.michelNumbers().toString(),
			Random.sellerName(),
			Random.url(),
			String.valueOf(Random.price()),
			Random.currency().toString()
		);
	}
	
	public static EntityWithParentDto createEntityWithParentDto() {
		String name = Random.categoryName();
		String parentName = Random.participantGroupName();
		return new EntityWithParentDto(Random.id().toString(), name, parentName);
	}
	
	public static AddSeriesDto createAddSeriesDto() {
		// @todo #734 TestObjects.createAddSeriesDto(): return randomized values
		return new AddSeriesForm();
	}
	
	public static AddSeriesSalesDto createAddSeriesSalesDto() {
		return createAddSeriesSalesDtoWithSellerId(Random.id());
	}
	
	public static AddSeriesSalesDto createAddSeriesSalesDtoWithSellerId(Integer sellerId) {
		ImportSeriesSalesForm form = new ImportSeriesSalesForm();
		form.setSellerId(sellerId);
		form.setPrice(Random.price());
		form.setCurrency(Random.currency());
		return form;
	}
	
	public static ImportRequestInfo createImportRequestInfo() {
		return new ImportRequestInfo(Random.id(), Random.url());
	}
	
	public static ImportRequestFullInfo createImportRequestFullInfo() {
		return new ImportRequestFullInfo(
			Random.id(),
			Random.url(),
			Random.importRequestStatus(),
			Random.date()
		);
	}
	
	public static SeriesExtractedInfo createSeriesExtractedInfo() {
		Integer sellerId = null;
		Integer sellerGroupId = null;
		String sellerName = null;
		String sellerUrl = null;
		
		boolean existingSeller = bool();
		if (existingSeller) {
			sellerId = Random.id();
		} else {
			sellerGroupId = Random.id();
			sellerName = Random.sellerName();
			sellerUrl = Random.url();
		}
		
		return new SeriesExtractedInfo(
			Random.listOfIntegers(),
			Random.listOfIntegers(),
			Random.issueYear(),
			Random.quantity(),
			Random.perforated(),
			Random.michelNumbers(),
			sellerId,
			sellerGroupId,
			sellerName,
			sellerUrl,
			Random.price(),
			Random.currency().toString()
		);
	}
	
	public static SeriesExtractedInfo createEmptySeriesExtractedInfo() {
		return new SeriesExtractedInfo(
			Collections.emptyList(),
			Collections.emptyList(),
			null,
			null,
			null,
			Collections.emptySet(),
			null,
			null,
			null,
			null,
			null,
			null
		);
	}
	
	public static SeriesSalesParsedDataDbDto createSeriesSalesParsedDataDbDto() {
		SeriesSalesParsedDataDbDto dto = new SeriesSalesParsedDataDbDto();
		
		boolean existingSeller = bool();
		if (existingSeller) {
			dto.setSellerId(Random.id());
		} else {
			dto.setSellerName(Random.sellerName());
			dto.setSellerUrl(Random.url());
		}
		
		dto.setPrice(Random.price());
		dto.setCurrency(Random.currency().toString());
		dto.setCreatedAt(Random.date());
		dto.setUpdatedAt(Random.date());
		return dto;
	}
	
	public static SeriesSaleParsedDataDto createSeriesSaleParsedDataDto() {
		Integer sellerId = null;
		Integer sellerGroupId = null;
		String sellerName = null;
		String sellerUrl = null;
		
		boolean existingSeller = bool();
		if (existingSeller) {
			sellerId = Random.id();
		} else {
			sellerGroupId = Random.id();
			sellerName = Random.sellerName();
			sellerUrl = Random.url();
		}
		
		return new SeriesSaleParsedDataDto(
			sellerId,
			sellerGroupId,
			sellerName,
			sellerUrl,
			Random.price(),
			Random.currency()
		);
	}
	
	public static AddParticipantDto createAddParticipantDto() {
		AddParticipantForm dto = new AddParticipantForm();
		dto.setName(Random.participantName());
		dto.setUrl(Random.url());
		dto.setGroupId(Random.id());
		dto.setBuyer(bool());
		dto.setSeller(bool());
		return dto;
	}
	
	public static AddToCollectionDto createAddToCollectionDto() {
		return createAddToCollectionForm();
	}
	
	public static AddToCollectionForm createAddToCollectionForm() {
		AddToCollectionForm dto = new AddToCollectionForm();
		dto.setNumberOfStamps(Random.quantity());
		dto.setPrice(Random.price());
		dto.setCurrency(Random.currency());
		dto.setSeriesId(Random.id());
		return dto;
	}
	
	public static SeriesLinkDto createSeriesLinkDto() {
		return new SeriesLinkDto(
			Random.id(),
			Random.issueYear(),
			Random.quantity(),
			Random.perforated(),
			Random.countryName()
		);
	}
	
}
