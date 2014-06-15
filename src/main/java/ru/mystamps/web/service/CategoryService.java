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
package ru.mystamps.web.service;

import ru.mystamps.web.entity.Category;
import ru.mystamps.web.entity.User;
import ru.mystamps.web.service.dto.AddCategoryDto;
import ru.mystamps.web.service.dto.EntityInfoDto;

public interface CategoryService {
	Category add(AddCategoryDto dto, User user);
	Iterable<EntityInfoDto> findAll(String lang);
	long countAll();
	int countByName(String name);
	int countByNameRu(String name);
}
