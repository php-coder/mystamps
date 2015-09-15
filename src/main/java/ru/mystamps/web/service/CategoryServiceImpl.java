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
package ru.mystamps.web.service;

import java.util.Date;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.dao.JdbcCategoryDao;
import ru.mystamps.web.dao.dto.AddCategoryDbDto;
import ru.mystamps.web.entity.User;
import ru.mystamps.web.service.dto.AddCategoryDto;
import ru.mystamps.web.service.dto.LinkEntityDto;
import ru.mystamps.web.service.dto.SelectEntityDto;
import ru.mystamps.web.service.dto.UrlEntityDto;
import ru.mystamps.web.util.SlugUtils;

@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
	private static final Logger LOG = LoggerFactory.getLogger(CategoryServiceImpl.class);
	
	private final JdbcCategoryDao categoryDao;
	
	@Override
	@Transactional
	@PreAuthorize("hasAuthority('CREATE_CATEGORY')")
	public UrlEntityDto add(AddCategoryDto dto, User user) {
		Validate.isTrue(dto != null, "DTO should be non null");
		Validate.isTrue(dto.getName() != null, "English category name should be non null");
		Validate.isTrue(dto.getNameRu() != null, "Russian category name should be non null");
		Validate.isTrue(user != null, "Current user must be non null");
		
		AddCategoryDbDto category = new AddCategoryDbDto();
		category.setName(dto.getName());
		category.setNameRu(dto.getNameRu());
		
		String slug = SlugUtils.slugify(dto.getName());
		Validate.isTrue(
			StringUtils.isNotEmpty(slug),
			"Slug for string '%s' must be non empty", dto.getName()
		);
		category.setSlug(slug);
		
		Date now = new Date();
		category.setCreatedAt(now);
		category.setUpdatedAt(now);
		
		category.setCreatedBy(user.getId());
		category.setUpdatedBy(user.getId());

		Integer id = categoryDao.add(category);
		LOG.info("Category #{} has been created ({})", id, category);
		
		return new UrlEntityDto(id, slug);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Iterable<SelectEntityDto> findAllAsSelectEntities(String lang) {
		return categoryDao.findAllAsSelectEntities(lang);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Iterable<LinkEntityDto> findAllAsLinkEntities(String lang) {
		return categoryDao.findAllAsLinkEntities(lang);
	}
	
	@Override
	@Transactional(readOnly = true)
	public long countAll() {
		return categoryDao.countAll();
	}
	
	@Override
	@Transactional(readOnly = true)
	public long countCategoriesOf(Integer collectionId) {
		Validate.isTrue(collectionId != null, "Collection id must be non null");
		
		return categoryDao.countCategoriesOfCollection(collectionId);
	}
	
	@Override
	@Transactional(readOnly = true)
	public long countByName(String name) {
		Validate.isTrue(name != null, "Name should be non null");
		return categoryDao.countByName(name);
	}
	
	@Override
	@Transactional(readOnly = true)
	public long countByNameRu(String name) {
		Validate.isTrue(name != null, "Name on Russian should be non null");
		return categoryDao.countByNameRu(name);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Map<String, Integer> getStatisticsOf(Integer collectionId, String lang) {
		Validate.isTrue(collectionId != null, "Collection id must be non null");
		
		return categoryDao.getStatisticsOf(collectionId, lang);
	}
	
}
