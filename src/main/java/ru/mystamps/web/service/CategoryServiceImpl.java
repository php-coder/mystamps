/*
 * Copyright (C) 2009-2016 Slava Semushin <slava.semushin@gmail.com>
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
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.access.prepost.PreAuthorize;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.dao.CategoryDao;
import ru.mystamps.web.dao.dto.AddCategoryDbDto;
import ru.mystamps.web.dao.dto.LinkEntityDto;
import ru.mystamps.web.service.dto.AddCategoryDto;
import ru.mystamps.web.support.spring.security.HasAuthority;
import ru.mystamps.web.util.LocaleUtils;
import ru.mystamps.web.util.SlugUtils;

@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
	private static final Logger LOG = LoggerFactory.getLogger(CategoryServiceImpl.class);
	
	private final CategoryDao categoryDao;
	
	@Override
	@Transactional
	@PreAuthorize(HasAuthority.CREATE_CATEGORY)
	public String add(AddCategoryDto dto, Integer userId) {
		Validate.isTrue(dto != null, "DTO must be non null");
		Validate.isTrue(dto.getName() != null, "Category name in English must be non null");
		Validate.isTrue(dto.getNameRu() != null, "Category name in Russian must be non null");
		Validate.isTrue(userId != null, "User id must be non null");
		
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
		
		category.setCreatedBy(userId);
		category.setUpdatedBy(userId);

		Integer id = categoryDao.add(category);
		LOG.info("Category #{} has been created ({})", id, category);
		
		return slug;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<LinkEntityDto> findAllAsLinkEntities(String lang) {
		return categoryDao.findAllAsLinkEntities(lang);
	}
	
	@Override
	@Transactional(readOnly = true)
	public LinkEntityDto findOneAsLinkEntity(String slug, String lang) {
		Validate.isTrue(slug != null, "Category slug must be non null");
		Validate.isTrue(!slug.trim().isEmpty(), "Category slug must be non empty");
		
		return categoryDao.findOneAsLinkEntity(slug, lang);
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
		Validate.isTrue(name != null, "Name must be non null");
		
		// converting to lowercase to do a case-insensitive search
		String categoryName = name.toLowerCase(Locale.ENGLISH);
		
		return categoryDao.countByName(categoryName);
	}
	
	@Override
	@Transactional(readOnly = true)
	public long countByNameRu(String name) {
		Validate.isTrue(name != null, "Name in Russian must be non null");
		
		// converting to lowercase to do a case-insensitive search
		String categoryName = name.toLowerCase(LocaleUtils.RUSSIAN);
		
		return categoryDao.countByNameRu(categoryName);
	}
	
	@Override
	@Transactional(readOnly = true)
	public long countAddedSince(Date date) {
		Validate.isTrue(date != null, "Date must be non null");
		
		return categoryDao.countAddedSince(date);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Object[]> getStatisticsOf(Integer collectionId, String lang) {
		Validate.isTrue(collectionId != null, "Collection id must be non null");
		
		return categoryDao.getStatisticsOf(collectionId, lang);
	}
	
}
