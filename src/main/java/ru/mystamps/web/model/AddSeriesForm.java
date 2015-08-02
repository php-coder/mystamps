/*
 * Copyright (C) 2009-2015 Slava Semushin <slava.semushin@gmail.com>
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

import java.math.BigDecimal;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.GroupSequence;

import org.hibernate.validator.constraints.Range;

import org.springframework.web.multipart.MultipartFile;

import ru.mystamps.web.entity.Category;
import ru.mystamps.web.entity.Country;
import ru.mystamps.web.service.dto.AddSeriesDto;
import ru.mystamps.web.validation.jsr303.CatalogNumbers;
import ru.mystamps.web.validation.jsr303.ImageFile;
import ru.mystamps.web.validation.jsr303.NotEmptyFile;
import ru.mystamps.web.validation.jsr303.NotEmptyFilename;
import ru.mystamps.web.validation.jsr303.NotNullIfFirstField;
import ru.mystamps.web.validation.jsr303.Price;
import ru.mystamps.web.validation.jsr303.UniqueGibbonsNumbers;
import ru.mystamps.web.validation.jsr303.UniqueMichelNumbers;
import ru.mystamps.web.validation.jsr303.UniqueScottNumbers;
import ru.mystamps.web.validation.jsr303.UniqueYvertNumbers;

import static ru.mystamps.web.validation.ValidationRules.MAX_SERIES_COMMENT_LENGTH;
import static ru.mystamps.web.validation.ValidationRules.MAX_STAMPS_IN_SERIES;
import static ru.mystamps.web.validation.ValidationRules.MIN_STAMPS_IN_SERIES;
import static ru.mystamps.web.validation.ValidationRules.MAX_DAYS_IN_MONTH;
import static ru.mystamps.web.validation.ValidationRules.MAX_MONTHS_IN_YEAR;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// TODO: combine price with currency to separate class
@SuppressWarnings({"PMD.TooManyFields", "PMD.AvoidDuplicateLiterals"})
@NotNullIfFirstField.List({
	@NotNullIfFirstField(
		first = "month", second = "year", message = "{month.requires.year}"
	),
	@NotNullIfFirstField(
		first = "day", second = "month", message = "{day.requires.month}"
	)
})
public class AddSeriesForm implements AddSeriesDto {
	
	@NotNull
	private Category category;
	
	private Country country;
	
	@Range(min = 1, max = MAX_DAYS_IN_MONTH, message = "{day.invalid}")
	private Integer day;
	
	@Range(min = 1, max = MAX_MONTHS_IN_YEAR, message = "{month.invalid}")
	private Integer month;
	
	private Integer year;
	
	@NotNull
	@Min(MIN_STAMPS_IN_SERIES)
	@Max(MAX_STAMPS_IN_SERIES)
	private Integer quantity;
	
	@NotNull
	private Boolean perforated;
	
	@CatalogNumbers(groups = MichelCatalog1Checks.class)
	@UniqueMichelNumbers(groups = MichelCatalog2Checks.class)
	private String michelNumbers;
	
	@Price
	private BigDecimal michelPrice;
	
	@CatalogNumbers(groups = ScottCatalog1Checks.class)
	@UniqueScottNumbers(groups = ScottCatalog2Checks.class)
	private String scottNumbers;
	
	@Price
	private BigDecimal scottPrice;
	
	@CatalogNumbers(groups = YvertCatalog1Checks.class)
	@UniqueYvertNumbers(groups = YvertCatalog2Checks.class)
	private String yvertNumbers;
	
	@Price
	private BigDecimal yvertPrice;
	
	@CatalogNumbers(groups = GibbonsCatalog1Checks.class)
	@UniqueGibbonsNumbers(groups = GibbonsCatalog2Checks.class)
	private String gibbonsNumbers;
	
	@Price
	private BigDecimal gibbonsPrice;
	
	@Size(max = MAX_SERIES_COMMENT_LENGTH, message = "{value.too-long}")
	private String comment;
	
	@NotNull
	@NotEmptyFilename(groups = Image1Checks.class)
	@NotEmptyFile(groups = Image2Checks.class)
	@ImageFile(groups = Image3Checks.class)
	private MultipartFile image;
	
	
	@GroupSequence({
			MichelCatalog1Checks.class,
			MichelCatalog2Checks.class
	})
	public interface MichelCatalogChecks {
	}
	
	public interface MichelCatalog1Checks {
	}
	
	public interface MichelCatalog2Checks {
	}
	
	@GroupSequence({
			ScottCatalog1Checks.class,
			ScottCatalog2Checks.class
	})
	public interface ScottCatalogChecks {
	}
	
	public interface ScottCatalog1Checks {
	}
	
	public interface ScottCatalog2Checks {
	}
	
	@GroupSequence({
			YvertCatalog1Checks.class,
			YvertCatalog2Checks.class
	})
	public interface YvertCatalogChecks {
	}
	
	public interface YvertCatalog1Checks {
	}
	
	public interface YvertCatalog2Checks {
	}
	
	@GroupSequence({
			GibbonsCatalog1Checks.class,
			GibbonsCatalog2Checks.class
	})
	public interface GibbonsCatalogChecks {
	}
	
	public interface GibbonsCatalog1Checks {
	}
	
	public interface GibbonsCatalog2Checks {
	}
	
	@GroupSequence({
		Image1Checks.class,
		Image2Checks.class,
		Image3Checks.class
	})
	public interface ImageChecks {
	}
	
	public interface Image1Checks {
	}
	
	public interface Image2Checks {
	}
	
	public interface Image3Checks {
	}
	
}
