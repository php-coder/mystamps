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
package ru.mystamps.web.feature.country;

import lombok.Getter;
import lombok.Setter;
import ru.mystamps.web.feature.country.UniqueCountryName.Lang;
import ru.mystamps.web.support.beanvalidation.DenyValues;
import ru.mystamps.web.support.beanvalidation.Group;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static ru.mystamps.web.feature.country.CountryValidation.NAME_EN_REGEXP;
import static ru.mystamps.web.feature.country.CountryValidation.NAME_MAX_LENGTH;
import static ru.mystamps.web.feature.country.CountryValidation.NAME_MIN_LENGTH;
import static ru.mystamps.web.feature.country.CountryValidation.NAME_NO_HYPHEN_REGEXP;
import static ru.mystamps.web.feature.country.CountryValidation.NAME_NO_REPEATING_HYPHENS_REGEXP;
import static ru.mystamps.web.feature.country.CountryValidation.NAME_RU_REGEXP;

@Getter
@Setter
@GroupSequence({
	AddCountryForm.class,
	Group.Level1.class,
	Group.Level2.class,
	Group.Level3.class,
	Group.Level4.class,
	Group.Level5.class,
	Group.Level6.class,
	Group.Level7.class,
	Group.Level8.class
})
public class AddCountryForm implements AddCountryDto {
	
	@NotEmpty(groups = Group.Level1.class)
	@Size(min = NAME_MIN_LENGTH, message = "{value.too-short}", groups = Group.Level2.class)
	@Size(max = NAME_MAX_LENGTH, message = "{value.too-long}", groups = Group.Level2.class)
	@Pattern(
		regexp = NAME_EN_REGEXP,
		message = "{value.invalid-en-chars}",
		groups = Group.Level3.class
	)
	@Pattern(
		regexp = NAME_NO_REPEATING_HYPHENS_REGEXP,
		message = "{value.repeating-hyphen}",
		groups = Group.Level4.class
	)
	@Pattern(
		regexp = NAME_NO_HYPHEN_REGEXP,
		message = "{value.hyphen}",
		groups = Group.Level5.class
	)
	@DenyValues(value = {"add", "list"}, groups = Group.Level6.class)
	@UniqueCountryName(lang = Lang.EN, groups = Group.Level7.class)
	@UniqueCountrySlug(groups = Group.Level8.class)
	private String name;
	
	@Size(min = NAME_MIN_LENGTH, message = "{value.too-short}", groups = Group.Level2.class)
	@Size(max = NAME_MAX_LENGTH, message = "{value.too-long}", groups = Group.Level2.class)
	@Pattern(
		regexp = NAME_RU_REGEXP,
		message = "{value.invalid-ru-chars}",
		groups = Group.Level3.class
	)
	@Pattern(
		regexp = NAME_NO_REPEATING_HYPHENS_REGEXP,
		message = "{value.repeating-hyphen}",
		groups = Group.Level4.class
	)
	@Pattern(
		regexp = NAME_NO_HYPHEN_REGEXP,
		message = "{value.hyphen}",
		groups = Group.Level5.class
	)
	@UniqueCountryName(lang = Lang.RU, groups = Group.Level7.class)
	private String nameRu;
	
}
