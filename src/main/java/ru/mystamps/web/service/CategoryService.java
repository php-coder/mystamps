/*
 * Copyright (C) 2009-2018 Slava Semushin <slava.semushin@gmail.com>
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

import java.util.Date;
import java.util.List;
import java.util.Set;

import ru.mystamps.web.dao.dto.EntityWithParentDto;
import ru.mystamps.web.dao.dto.LinkEntityDto;
import ru.mystamps.web.service.dto.AddCategoryDto;

@SuppressWarnings("PMD.TooManyMethods")
public interface CategoryService {
	String add(AddCategoryDto dto, Integer userId);
	List<Integer> findIdsByNames(Set<String> names);
	List<Integer> findIdsWhenNameStartsWith(String name);
	List<LinkEntityDto> findAllAsLinkEntities(String lang);
	List<EntityWithParentDto> findCategoriesWithParents(String lang);
	LinkEntityDto findOneAsLinkEntity(String slug, String lang);
	long countAll();
	long countCategoriesOf(Integer collectionId);
	long countBySlug(String slug);
	long countByName(String name);
	long countByNameRu(String name);
	long countAddedSince(Date date);
	long countUntranslatedNamesSince(Date date);
	List<Object[]> getStatisticsOf(Integer collectionId, String lang);
}
