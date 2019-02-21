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
package ru.mystamps.web.feature.series.importing.sale;

import ru.mystamps.web.dao.dto.Currency;
import ru.mystamps.web.support.jdbc.JdbcUtils;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

final class RowMappers {
	
	private RowMappers() {
	}

	/* default */ static SeriesSaleParsedDataDto forSeriesSaleParsedDataDto(
		ResultSet rs,
		int unused) throws SQLException {

		Integer sellerId = JdbcUtils.getInteger(rs, "seller_id");
		Integer sellerGroupId = JdbcUtils.getInteger(rs, "seller_group_id");
		String sellerName = rs.getString("seller_name");
		String sellerUrl = rs.getString("seller_url");
		BigDecimal price = rs.getBigDecimal("price");
		Currency currency = JdbcUtils.getCurrency(rs, "currency");

		return new SeriesSaleParsedDataDto(
			sellerId,
			sellerGroupId,
			sellerName,
			sellerUrl,
			price,
			currency
		);
	}
	
}
