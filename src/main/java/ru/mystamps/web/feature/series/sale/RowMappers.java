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
package ru.mystamps.web.feature.series.sale;

import ru.mystamps.web.common.Currency;
import ru.mystamps.web.common.JdbcUtils;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

final class RowMappers {
	
	private RowMappers() {
	}
	
	/**
	 * @author Sergey Chechenev
	 */
	/* default */ static SeriesSaleDto forSeriesSaleDto(ResultSet rs, int unused)
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
		
		// LATER: consider extracting this into a helper method
		String conditionField = rs.getString("cond");
		SeriesCondition condition = rs.wasNull() ? null : SeriesCondition.valueOf(conditionField);
		
		return new SeriesSaleDto(
			date,
			sellerName,
			sellerUrl,
			buyerName,
			buyerUrl,
			transactionUrl,
			firstPrice,
			firstCurrency,
			secondPrice,
			secondCurrency,
			condition
		);
	}
	
}
