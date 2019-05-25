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
package ru.mystamps.web.feature.series.sale;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;
import org.springframework.format.annotation.DateTimeFormat;
import ru.mystamps.web.dao.dto.Currency;
import ru.mystamps.web.support.beanvalidation.BothOrNoneRequired;
import ru.mystamps.web.support.beanvalidation.FieldsMismatch;
import ru.mystamps.web.support.beanvalidation.Group;
import ru.mystamps.web.validation.ValidationRules;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Date;

// @todo #678 Add integration tests for price and alternative price: both or none are required
@Getter
@Setter
@FieldsMismatch.List({
	@FieldsMismatch(
		first = "sellerId", second = "buyerId", message = "{seller.buyer.match}"
	),
	@FieldsMismatch(
		first = "currency", second = "altCurrency", message = "{currencies.prices.match}"
	)
})
@BothOrNoneRequired(
	first = "altPrice",
	second = "altCurrency",
	message = "{altprice.altcurrency.both-required}"
)
public class AddSeriesSalesForm implements AddSeriesSalesDto {
	
	@DateTimeFormat(pattern = "dd.MM.yyyy")
	private Date date;
	
	@NotNull
	private Integer sellerId;
	
	@Size(
		max = ValidationRules.SERIES_SALES_URL_MAX_LENGTH,
		message = "{value.too-long}",
		groups = Group.Level1.class
	)
	@URL(groups = Group.Level2.class)
	private String url;
	
	@NotNull
	private BigDecimal price;
	
	@NotNull
	private Currency currency;
	
	private BigDecimal altPrice;
	
	private Currency altCurrency;
	
	private Integer buyerId;
	
	@GroupSequence({
		Group.Level1.class,
		Group.Level2.class,
	})
	public interface UrlChecks {
	}

}
