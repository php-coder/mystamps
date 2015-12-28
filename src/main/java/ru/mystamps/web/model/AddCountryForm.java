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

import javax.validation.GroupSequence;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import lombok.Getter;
import lombok.Setter;

import ru.mystamps.web.service.dto.AddCountryDto;
import ru.mystamps.web.validation.jsr303.UniqueCountryName;
import ru.mystamps.web.validation.jsr303.UniqueCountryName.Lang;

import static ru.mystamps.web.validation.ValidationRules.COUNTRY_NAME_EN_REGEXP;
import static ru.mystamps.web.validation.ValidationRules.COUNTRY_NAME_MAX_LENGTH;
import static ru.mystamps.web.validation.ValidationRules.COUNTRY_NAME_MIN_LENGTH;
import static ru.mystamps.web.validation.ValidationRules.COUNTRY_NAME_NO_HYPHEN_REGEXP;
import static ru.mystamps.web.validation.ValidationRules.COUNTRY_NAME_RU_REGEXP;

@Getter
@Setter
@GroupSequence({
	AddCountryForm.class,
	AddCountryForm.Level1Checks.class,
	AddCountryForm.Level2Checks.class,
	AddCountryForm.Level3Checks.class,
	AddCountryForm.Level4Checks.class,
	AddCountryForm.Level5Checks.class
})
public class AddCountryForm implements AddCountryDto {
	
	@NotEmpty(groups = Level1Checks.class)
	@Size.List({
		@Size(
			min = COUNTRY_NAME_MIN_LENGTH,
			message = "{value.too-short}",
			groups = Level2Checks.class
		),
		@Size(
			max = COUNTRY_NAME_MAX_LENGTH,
			message = "{value.too-long}",
			groups = Level2Checks.class
		)
	})
	@Pattern.List({
		@Pattern(
			regexp = COUNTRY_NAME_EN_REGEXP,
			message = "{country-name-en.invalid}",
			groups = Level3Checks.class
		),
		@Pattern(
			regexp = COUNTRY_NAME_NO_HYPHEN_REGEXP,
			message = "{value.hyphen}",
			groups = Level4Checks.class
		)
	})
	@UniqueCountryName(lang = Lang.EN, groups = Level5Checks.class)
	private String name;
	
	@NotEmpty(groups = Level1Checks.class)
	@Size.List({
		@Size(
			min = COUNTRY_NAME_MIN_LENGTH,
			message = "{value.too-short}",
			groups = Level2Checks.class
		),
		@Size(
			max = COUNTRY_NAME_MAX_LENGTH,
			message = "{value.too-long}",
			groups = Level2Checks.class
		)
	})
	@Pattern.List({
		@Pattern(
			regexp = COUNTRY_NAME_RU_REGEXP,
			message = "{country-name-ru.invalid}",
			groups = Level3Checks.class
		),
		@Pattern(
			regexp = COUNTRY_NAME_NO_HYPHEN_REGEXP,
			message = "{value.hyphen}",
			groups = Level4Checks.class
		)
	})
	@UniqueCountryName(lang = Lang.RU, groups = Level5Checks.class)
	private String nameRu;
	
	public interface Level1Checks {
	}
	
	public interface Level2Checks {
	}
	
	public interface Level3Checks {
	}
	
	public interface Level4Checks {
	}
	
	public interface Level5Checks {
	}
	
}
