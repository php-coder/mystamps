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
package ru.mystamps.web.dao.impl;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

// CheckStyle: ignore AvoidStarImportCheck for next 1 line
import ru.mystamps.web.dao.dto.*; // NOPMD: UnusedImports

@SuppressWarnings({ "PMD.AvoidDuplicateLiterals", "PMD.TooManyMethods" })
final class RowMappers {
	
	private RowMappers() {
	}
	
	public static LinkEntityDto forLinkEntityDto(ResultSet rs, int i) throws SQLException {
		return new LinkEntityDto(
			rs.getInt("id"),
			rs.getString("slug"),
			rs.getString("name")
		);
	}
	
	public static Object[] forNameAndCounter(ResultSet rs, int i) throws SQLException {
		return new Object[]{
			rs.getString("name"),
			JdbcUtils.getInteger(rs, "counter")
		};
	}
	
	public static SitemapInfoDto forSitemapInfoDto(ResultSet rs, int i) throws SQLException {
		return new SitemapInfoDto(
			rs.getInt("id"),
			rs.getTimestamp("updated_at")
		);
	}
	
	public static SeriesInfoDto forSeriesInfoDto(ResultSet rs, int i) throws SQLException {
		Integer seriesId     = rs.getInt("id");
		Integer releaseDay   = JdbcUtils.getInteger(rs, "release_day");
		Integer releaseMonth = JdbcUtils.getInteger(rs, "release_month");
		Integer releaseYear  = JdbcUtils.getInteger(rs, "release_year");
		Integer quantity     = rs.getInt("quantity");
		Boolean perforated   = rs.getBoolean("perforated");
		Integer categoryId   = rs.getInt("category_id");
		String categorySlug  = rs.getString("category_slug");
		String categoryName  = rs.getString("category_name");
		Integer countryId    = JdbcUtils.getInteger(rs, "country_id");
		String countrySlug   = rs.getString("country_slug");
		String countryName   = rs.getString("country_name");
		
		return new SeriesInfoDto(
			seriesId,
			categoryId,
			categorySlug,
			categoryName,
			countryId,
			countrySlug,
			countryName,
			releaseDay,
			releaseMonth,
			releaseYear,
			quantity,
			perforated
		);
	}
	
	/**
	 * @author Sergey Chechenev
	 */
	// CheckStyle: ignore LineLength for next 1 line
	public static PurchaseAndSaleDto forPurchaseAndSaleDto(ResultSet rs, int i) throws SQLException {
		Date date               = rs.getDate("date");
		String sellerName       = rs.getString("seller_name");
		String sellerUrl        = rs.getString("seller_url");
		String buyerName        = rs.getString("buyer_name");
		String buyerUrl         = rs.getString("buyer_url");
		String transactionUrl   = rs.getString("transaction_url");
		BigDecimal firstPrice   = rs.getBigDecimal("first_price");
		Currency firstCurrency  = JdbcUtils.getCurrency(rs, "first_currency");
		BigDecimal secondPrice  = rs.getBigDecimal("second_price");
		Currency secondCurrency = JdbcUtils.getCurrency(rs, "second_currency");
		
		return new PurchaseAndSaleDto(
			date,
			sellerName,
			sellerUrl,
			buyerName,
			buyerUrl,
			transactionUrl,
			firstPrice,
			firstCurrency,
			secondPrice,
			secondCurrency
		);
	}
	
	public static SeriesFullInfoDto forSeriesFullInfoDto(ResultSet rs, int i) throws SQLException {
		Integer seriesId     = rs.getInt("id");
		Integer releaseDay   = JdbcUtils.getInteger(rs, "release_day");
		Integer releaseMonth = JdbcUtils.getInteger(rs, "release_month");
		Integer releaseYear  = JdbcUtils.getInteger(rs, "release_year");
		Integer quantity     = rs.getInt("quantity");
		Boolean perforated   = rs.getBoolean("perforated");
		String comment       = rs.getString("comment");
		Integer createdBy    = rs.getInt("created_by");
		
		BigDecimal michelPrice = rs.getBigDecimal("michel_price");
		String michelCurrency  = rs.getString("michel_currency");
		
		BigDecimal scottPrice = rs.getBigDecimal("scott_price");
		String scottCurrency  = rs.getString("scott_currency");
		
		BigDecimal yvertPrice = rs.getBigDecimal("yvert_price");
		String yvertCurrency  = rs.getString("yvert_currency");
		
		BigDecimal gibbonsPrice = rs.getBigDecimal("gibbons_price");
		String gibbonsCurrency  = rs.getString("gibbons_currency");
		
		Integer categoryId     = rs.getInt("category_id");
		String categorySlug    = rs.getString("category_slug");
		String categoryName    = rs.getString("category_name");
		LinkEntityDto category = new LinkEntityDto(categoryId, categorySlug, categoryName);
		
		Integer countryId     = JdbcUtils.getInteger(rs, "country_id");
		LinkEntityDto country = null;
		if (countryId != null) {
			String countrySlug = rs.getString("country_slug");
			String countryName = rs.getString("country_name");
			country = new LinkEntityDto(countryId, countrySlug, countryName);
		}
		
		return new SeriesFullInfoDto(
			seriesId,
			category,
			country,
			releaseDay,
			releaseMonth,
			releaseYear,
			quantity,
			perforated,
			comment,
			createdBy,
			michelPrice,
			michelCurrency,
			scottPrice,
			scottCurrency,
			yvertPrice,
			yvertCurrency,
			gibbonsPrice,
			gibbonsCurrency
		);
	}
	
	/**
	 * @author Sergey Chechenev
	 */
	// CheckStyle: ignore LineLength for next 1 line
	public static SuspiciousActivityDto forSuspiciousActivityDto(ResultSet rs, int i) throws SQLException {
		String type        = rs.getString("activity_name");
		Date occurredAt    = rs.getTimestamp("occurred_at");
		String page        = rs.getString("page");
		String method      = rs.getString("method");
		String userLogin   = rs.getString("user_login");
		String ip          = rs.getString("ip");
		String refererPage = rs.getString("referer_page");
		String userAgent   = rs.getString("user_agent");
		
		return new SuspiciousActivityDto(
			type,
			occurredAt,
			page,
			method,
			userLogin,
			ip,
			refererPage,
			userAgent
		);
	}
		
		// CheckStyle: ignore LineLength for next 1 line
	public static UsersActivationDto forUsersActivationDto(ResultSet rs, int i) throws SQLException {
		return new UsersActivationDto(
			rs.getString("email"),
			rs.getTimestamp("created_at")
		);
	}
	
	// CheckStyle: ignore LineLength for next 1 line
	public static UsersActivationFullDto forUsersActivationFullDto(ResultSet rs, int i) throws SQLException {
		return new UsersActivationFullDto(
			rs.getString("activation_key"),
			rs.getString("email"),
			rs.getTimestamp("created_at")
		);
	}
	
	public static CollectionInfoDto forCollectionInfoDto(ResultSet rs, int i) throws SQLException {
		return new CollectionInfoDto(
			rs.getInt("id"),
			rs.getString("slug"),
			rs.getString("name")
		);
	}
	
	public static UserDetails forUserDetails(ResultSet rs, int i) throws SQLException {
		return new UserDetails(
			rs.getInt("id"),
			rs.getString("login"),
			rs.getString("name"),
			rs.getString("hash"),
			UserDetails.Role.valueOf(rs.getString("role")),
			rs.getString("collection_slug")
		);
	}
	
	public static DbImageDto forDbImageDto(ResultSet rs, int i) throws SQLException {
		return new DbImageDto(
			rs.getString("type"),
			rs.getBytes("data")
		);
	}
	
	public static ImageInfoDto forImageInfoDto(ResultSet rs, int i) throws SQLException {
		return new ImageInfoDto(
			rs.getInt("id"),
			rs.getString("type")
		);
	}
	
	public static EntityWithIdDto forEntityWithIdDto(ResultSet rs, int i) throws SQLException {
		return new EntityWithIdDto(
			rs.getInt("id"),
			rs.getString("name")
		);
	}
	
	public static CategoryDto forCategoryDto(ResultSet rs, int i) throws SQLException {
		return new CategoryDto(
			rs.getString("name"),
			rs.getString("slug"),
			rs.getString("parent_name")
		);
	}
	
	public static TransactionParticipantDto forTransactionParticipantDto(ResultSet rs, int i)
		throws SQLException {
		
		return new TransactionParticipantDto(
			rs.getInt("id"),
			rs.getString("name"),
			rs.getString("parent_name")
		);
	}
	
	public static ImportRequestDto forImportRequestDto(ResultSet rs, int i) throws SQLException {
		return new ImportRequestDto(
			rs.getString("url"),
			rs.getString("status")
		);
	}
	
	public static Integer forInteger(ResultSet rs, int i) throws SQLException {
		return rs.getInt("id");
	}
	
	public static ParsedDataDto forParsedDataDto(ResultSet rs, int i) throws SQLException {
		EntityWithSlugDto category = null;
		String categorySlug = rs.getString("category_slug");
		if (categorySlug != null) {
			String categoryName = rs.getString("category_name");
			category = new EntityWithSlugDto(categoryName, categorySlug);
		}
		
		EntityWithSlugDto country = null;
		String countrySlug = rs.getString("country_slug");
		if (countrySlug != null) {
			String countryName = rs.getString("country_name");
			country = new EntityWithSlugDto(countryName, countrySlug);
		}
		
		String imageUrl = rs.getString("image_url");
		Integer releaseYear = JdbcUtils.getInteger(rs, "release_year");
		
		return new ParsedDataDto(category, country, imageUrl, releaseYear);
	}
	
}
