/*
 * Copyright (C) 2009-2014 Slava Semushin <slava.semushin@gmail.com>
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

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.validation.GroupSequence;

import org.hibernate.validator.constraints.NotEmpty;

import ru.mystamps.web.service.dto.AddCategoryDto;
import ru.mystamps.web.validation.jsr303.UniqueCategoryName;
import ru.mystamps.web.validation.jsr303.UniqueCategoryName.Lang;

import lombok.Getter;
import lombok.Setter;

import static ru.mystamps.web.validation.ValidationRules.CATEGORY_NAME_NO_HYPHEN_REGEXP;
import static ru.mystamps.web.validation.ValidationRules.CATEGORY_NAME_EN_REGEXP;
import static ru.mystamps.web.validation.ValidationRules.CATEGORY_NAME_MAX_LENGTH;
import static ru.mystamps.web.validation.ValidationRules.CATEGORY_NAME_MIN_LENGTH;
import static ru.mystamps.web.validation.ValidationRules.CATEGORY_NAME_RU_REGEXP;

@Getter
@Setter
@GroupSequence({
	AddCategoryForm.class,
	AddCategoryForm.Level1Checks.class,
	AddCategoryForm.Level2Checks.class,
	AddCategoryForm.Level3Checks.class,
	AddCategoryForm.Level4Checks.class,
	AddCategoryForm.Level5Checks.class
})
public class AddCategoryForm implements AddCategoryDto {
	
	@NotEmpty(groups = Level1Checks.class)
	@Size.List({
		@Size(
			min = CATEGORY_NAME_MIN_LENGTH,
			message = "{value.too-short}",
			groups = Level2Checks.class
		),
		@Size(
			max = CATEGORY_NAME_MAX_LENGTH,
			message = "{value.too-long}",
			groups = Level2Checks.class
		)
	})
	@Pattern.List({
		@Pattern(
			regexp = CATEGORY_NAME_EN_REGEXP,
			message = "{category-name-en.invalid}",
			groups = Level3Checks.class
		),
		@Pattern(
			regexp = CATEGORY_NAME_NO_HYPHEN_REGEXP,
			message = "{category-name.hyphen}",
			groups = Level4Checks.class
		)
	})
	@UniqueCategoryName(lang = Lang.EN, groups = Level5Checks.class)
	private String name;
	
	@NotEmpty(groups = Level1Checks.class)
	@Size.List({
		@Size(
			min = CATEGORY_NAME_MIN_LENGTH,
			message = "{value.too-short}",
			groups = Level2Checks.class
		),
		@Size(
			max = CATEGORY_NAME_MAX_LENGTH,
			message = "{value.too-long}",
			groups = Level2Checks.class
		)
	})
	@Pattern.List({
		@Pattern(
			regexp = CATEGORY_NAME_RU_REGEXP,
			message = "{category-name-ru.invalid}",
			groups = Level3Checks.class
		),
		@Pattern(
			regexp = CATEGORY_NAME_NO_HYPHEN_REGEXP,
			message = "{category-name.hyphen}",
			groups = Level4Checks.class
		)
	})
	@UniqueCategoryName(lang = Lang.RU, groups = Level5Checks.class)
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
