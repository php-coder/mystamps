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
package ru.mystamps.web.support.jdbc;

import ru.mystamps.web.common.Currency;
import ru.mystamps.web.common.EntityWithParentDto;
import ru.mystamps.web.common.JdbcUtils;
import ru.mystamps.web.common.LinkEntityDto;
import ru.mystamps.web.feature.participant.EntityWithIdDto;
import ru.mystamps.web.feature.series.PurchaseAndSaleDto;
import ru.mystamps.web.feature.series.SeriesFullInfoDto;
import ru.mystamps.web.feature.series.SeriesInfoDto;
import ru.mystamps.web.feature.series.SeriesLinkDto;
import ru.mystamps.web.feature.series.SitemapInfoDto;
import ru.mystamps.web.feature.site.SuspiciousActivityDto;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

@SuppressWarnings({ "PMD.AvoidDuplicateLiterals", "PMD.TooManyMethods" })
public final class RowMappers {
	
	private RowMappers() {
	}
	
	public static LinkEntityDto forLinkEntityDto(ResultSet rs, int unused) throws SQLException {
		return createLinkEntityDto(rs, "id", "slug", "name");
	}
	
	public static Object[] forNameAndCounter(ResultSet rs, int unused) throws SQLException {
		return new Object[]{
			rs.getString("name"),
			JdbcUtils.getInteger(rs, "counter")
		};
	}
	
	public static SitemapInfoDto forSitemapInfoDto(ResultSet rs, int unused) throws SQLException {
		return new SitemapInfoDto(
			rs.getInt("id"),
			rs.getTimestamp("updated_at")
		);
	}
	
	public static SeriesLinkDto forSeriesLinkDto(ResultSet rs, int unused) throws SQLException {
		Integer id         = rs.getInt("id");
		Integer year       = JdbcUtils.getInteger(rs, "release_year");
		Integer quantity   = rs.getInt("quantity");
		Boolean perforated = rs.getBoolean("perforated");
		String country     = rs.getString("country_name");
		
		return new SeriesLinkDto(id, year, quantity, perforated, country);
	}
	
	public static SeriesInfoDto forSeriesInfoDto(ResultSet rs, int unused) throws SQLException {
		Integer seriesId     = rs.getInt("id");
		Integer releaseDay   = JdbcUtils.getInteger(rs, "release_day");
		Integer releaseMonth = JdbcUtils.getInteger(rs, "release_month");
		Integer releaseYear  = JdbcUtils.getInteger(rs, "release_year");
		Integer quantity     = rs.getInt("quantity");
		Boolean perforated   = rs.getBoolean("perforated");
		
		LinkEntityDto category =
			createLinkEntityDto(rs, "category_id", "category_slug", "category_name");
		LinkEntityDto country =
			createLinkEntityDto(rs, "country_id", "country_slug", "country_name");
		
		return new SeriesInfoDto(
			seriesId,
			category,
			country,
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
	public static PurchaseAndSaleDto forPurchaseAndSaleDto(ResultSet rs, int unused)
		throws SQLException {
		
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
	
	public static SeriesFullInfoDto forSeriesFullInfoDto(ResultSet rs, int unused)
		throws SQLException {
		
		Integer seriesId     = rs.getInt("id");
		Integer releaseDay   = JdbcUtils.getInteger(rs, "release_day");
		Integer releaseMonth = JdbcUtils.getInteger(rs, "release_month");
		Integer releaseYear  = JdbcUtils.getInteger(rs, "release_year");
		Integer quantity     = rs.getInt("quantity");
		Boolean perforated   = rs.getBoolean("perforated");
		String comment       = rs.getString("comment");
		Integer createdBy    = rs.getInt("created_by");
		
		BigDecimal michelPrice   = rs.getBigDecimal("michel_price");
		BigDecimal scottPrice    = rs.getBigDecimal("scott_price");
		BigDecimal yvertPrice    = rs.getBigDecimal("yvert_price");
		BigDecimal gibbonsPrice  = rs.getBigDecimal("gibbons_price");
		BigDecimal solovyovPrice = rs.getBigDecimal("solovyov_price");
		BigDecimal zagorskiPrice = rs.getBigDecimal("zagorski_price");
		
		LinkEntityDto category =
			createLinkEntityDto(rs, "category_id", "category_slug", "category_name");
		
		LinkEntityDto country =
			createLinkEntityDto(rs, "country_id", "country_slug", "country_name");
		
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
			scottPrice,
			yvertPrice,
			gibbonsPrice,
			solovyovPrice,
			zagorskiPrice
		);
	}
	
	/**
	 * @author Sergey Chechenev
	 */
	public static SuspiciousActivityDto forSuspiciousActivityDto(ResultSet rs, int unused)
		throws SQLException {
		
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
	
	public static EntityWithIdDto forEntityWithIdDto(ResultSet rs, int unused) throws SQLException {
		return new EntityWithIdDto(
			rs.getInt("id"),
			rs.getString("name")
		);
	}
	
	public static EntityWithParentDto forEntityWithParentDto(ResultSet rs, int unused)
		throws SQLException {
		
		return new EntityWithParentDto(
			rs.getString("id"),
			rs.getString("name"),
			rs.getString("parent_name")
		);
	}
	
	public static Integer forInteger(ResultSet rs, int unused) throws SQLException {
		return rs.getInt("id");
	}
	
	public static LinkEntityDto createLinkEntityDto(
		ResultSet rs,
		String idColumn,
		String slugColumn,
		String nameColumn)
		throws SQLException {
		
		Integer id = JdbcUtils.getInteger(rs, idColumn);
		if (id == null) {
			return null;
		}
		
		return new LinkEntityDto(
			id,
			rs.getString(slugColumn),
			rs.getString(nameColumn)
		);
	}
	
}
