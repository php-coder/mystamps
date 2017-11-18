/*
 * Copyright (C) 2009-2017 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.service.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.ToString;

import ru.mystamps.web.dao.dto.EntityWithSlugDto;

@Getter
@ToString
public class FirstLevelCategoryDto {
	private final String name;
	private final String slug;
	private final List<EntityWithSlugDto> children = new ArrayList<>();
	
	public FirstLevelCategoryDto(String name, String slug) {
		this.name = name;
		this.slug = slug;
	}
	
	public FirstLevelCategoryDto(String name) {
		this(name, null);
	}
	
	public void addChild(String slug, String name) {
		children.add(new EntityWithSlugDto(name, slug));
	}
	
}
