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
package ru.mystamps.web.feature.series.importing;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;
import ru.mystamps.web.support.beanvalidation.Group;

import javax.validation.GroupSequence;
import javax.validation.constraints.Size;

import static ru.mystamps.web.validation.ValidationRules.IMPORT_REQUEST_URL_MAX_LENGTH;

@Getter
@Setter
@GroupSequence({
	RequestImportForm.class,
	Group.Level1.class,
	Group.Level2.class,
	Group.Level3.class
})
public class RequestImportForm implements RequestImportDto {
	
	// @todo #995 Series sale import: use its own interface and form
	// @todo #995 /series/sales/import: validate that we have a parser for this url
	@NotEmpty(groups = Group.Level1.class)
	@Size(
		// For series sales a max length is SERIES_SALES_URL_MAX_LENGTH but since they are equal,
		// we use IMPORT_REQUEST_URL_MAX_LENGTH here.
		// Also, as the import saves nothing, this check actually isn't required. Perhaps,
		// we shouldn't validate on this stage and let it fail later, during a sale creation.
		max = IMPORT_REQUEST_URL_MAX_LENGTH,
		message = "{value.too-long}",
		groups = Group.Level2.class
	)
	@URL(groups = Group.Level3.class)
	private String url;
}
