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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

package ru.mystamps.web.model;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.validation.GroupSequence;

import org.hibernate.validator.constraints.NotEmpty;

import ru.mystamps.web.validation.jsr303.UniqueCountryName;

import lombok.Getter;
import lombok.Setter;

import static ru.mystamps.web.validation.ValidationRules.COUNTRY_NAME_MAX_LENGTH;
import static ru.mystamps.web.validation.ValidationRules.COUNTRY_NAME_MIN_LENGTH;
import static ru.mystamps.web.validation.ValidationRules.COUNTRY_NAME_REGEXP1;
import static ru.mystamps.web.validation.ValidationRules.COUNTRY_NAME_REGEXP2;

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
public class AddCountryForm {
	
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
			regexp = COUNTRY_NAME_REGEXP1,
			message = "{country-name.invalid}",
			groups = Level3Checks.class
		),
		@Pattern(
			regexp = COUNTRY_NAME_REGEXP2,
			message = "{country-name.hyphen}",
			groups = Level4Checks.class
		)
	})
	@UniqueCountryName(groups = Level5Checks.class)
	private String country;
	
	
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
