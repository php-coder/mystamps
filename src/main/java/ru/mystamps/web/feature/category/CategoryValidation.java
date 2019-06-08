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
package ru.mystamps.web.feature.category;

import ru.mystamps.web.feature.category.CategoryDb.Category;

// @todo #927 CategoryValidation: remove CATEGORY_ prefix from the constants
@SuppressWarnings("PMD.CommentDefaultAccessModifier")
public final class CategoryValidation {
	
	public static final int CATEGORY_NAME_MIN_LENGTH = 3;
	public static final int CATEGORY_NAME_MAX_LENGTH = Category.NAME_LENGTH;
	public static final String CATEGORY_NAME_EN_REGEXP = "[- a-zA-Z]+";
	public static final String CATEGORY_NAME_RU_REGEXP = "[- а-яёА-ЯЁ]+";
	static final String CATEGORY_NAME_NO_HYPHEN_REGEXP = "[ \\p{L}]([- \\p{L}]+[ \\p{L}])*";
	@SuppressWarnings("PMD.LongVariable")
	static final String CATEGORY_NAME_NO_REPEATING_HYPHENS_REGEXP = "(?!.+[-]{2,}).+";
	
	private CategoryValidation() {
	}
	
}
