/*
 * Copyright (C) 2009-2016 Slava Semushin <slava.semushin@gmail.com>
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

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import org.springframework.web.multipart.MultipartFile;
import ru.mystamps.web.controller.converter.annotation.Category;
import ru.mystamps.web.controller.converter.annotation.Country;
import ru.mystamps.web.service.dto.AddSeriesDto;
import ru.mystamps.web.service.dto.LinkEntityDto;
import ru.mystamps.web.validation.jsr303.CatalogName;
import ru.mystamps.web.validation.jsr303.CatalogNumbers;
import ru.mystamps.web.validation.jsr303.ImageFile;
import ru.mystamps.web.validation.jsr303.MaxFileSize;
import ru.mystamps.web.validation.jsr303.MaxFileSize.Unit;
import ru.mystamps.web.validation.jsr303.NotEmptyFile;
import ru.mystamps.web.validation.jsr303.NotEmptyFilename;
import ru.mystamps.web.validation.jsr303.NotNullIfFirstField;
import ru.mystamps.web.validation.jsr303.Price;
import ru.mystamps.web.validation.jsr303.ReleaseDateIsNotInFuture;

import javax.validation.GroupSequence;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

import static ru.mystamps.web.validation.ValidationRules.MAX_DAYS_IN_MONTH;
import static ru.mystamps.web.validation.ValidationRules.MAX_IMAGE_SIZE;
import static ru.mystamps.web.validation.ValidationRules.MAX_MONTHS_IN_YEAR;
import static ru.mystamps.web.validation.ValidationRules.MAX_SERIES_COMMENT_LENGTH;
import static ru.mystamps.web.validation.ValidationRules.MAX_STAMPS_IN_SERIES;
import static ru.mystamps.web.validation.ValidationRules.MIN_RELEASE_YEAR;
import static ru.mystamps.web.validation.ValidationRules.MIN_STAMPS_IN_SERIES;

/**
 * @author Sergey Chechenev
 */
@Getter
@Setter
public class SearchSeriesForm {
	
	@CatalogNumbers
	private String catalogNumber;
	
	@NotNull
	@CatalogName
	private String catalogName;
	
}
