/*
 * Copyright (C) 2009-2012 Slava Semushin <slava.semushin@gmail.com>
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

package ru.mystamps.web.model;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.web.multipart.MultipartFile;

import ru.mystamps.web.validation.jsr303.CatalogNumbers;
import ru.mystamps.web.validation.jsr303.NotEmptyFilename;

import static ru.mystamps.web.validation.ValidationRules.MAX_SERIES_COMMENT_LENGTH;
import static ru.mystamps.web.validation.ValidationRules.MAX_STAMPS_IN_SERIES;
import static ru.mystamps.web.validation.ValidationRules.MIN_STAMPS_IN_SERIES;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddSeriesForm {
	private Integer country;
	private Integer year;
	
	@NotNull
	@Min(MIN_STAMPS_IN_SERIES)
	@Max(MAX_STAMPS_IN_SERIES)
	private Integer quantity;
	
	@NotNull
	private Boolean perforated;
	
	@CatalogNumbers
	private String michelNumbers;
	
	@CatalogNumbers
	private String scottNumbers;
	
	@CatalogNumbers
	private String yvertNumbers;
	
	@CatalogNumbers
	private String gibbonsNumbers;
	
	@Size(max = MAX_SERIES_COMMENT_LENGTH, message = "{value.too-long}")
	private String comment;
	
	@NotNull
	@NotEmptyFilename
	private MultipartFile image;
	
}
