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

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mystamps.web.Url;
import ru.mystamps.web.support.spring.security.CurrentUser;

@RestController
@RequiredArgsConstructor
class SuggestionController {

	private final CategoryService categoryService;
	
	@GetMapping(Url.SUGGEST_SERIES_CATEGORY)
	public String suggestCategoryForUser(@CurrentUser Integer currentUserId) {
		return StringUtils.defaultString(
			categoryService.suggestCategoryForUser(currentUserId),
			StringUtils.EMPTY
		);
	}
	
}

