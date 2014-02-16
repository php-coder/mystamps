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

import javax.inject.Inject;

import java.util.Date;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import org.apache.commons.lang3.Validate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.mystamps.web.entity.Category;
import ru.mystamps.web.entity.User;
import ru.mystamps.web.dao.CategoryDao;
import ru.mystamps.web.service.dto.AddCategoryDto;
import ru.mystamps.web.service.dto.EntityInfoDto;

public class CategoryServiceImpl implements CategoryService {
	private static final Logger LOG = LoggerFactory.getLogger(CategoryServiceImpl.class);
	
	private final CategoryDao categoryDao;
	
	@Inject
	public CategoryServiceImpl(CategoryDao categoryDao) {
		this.categoryDao = categoryDao;
	}
	
	@Override
	@Transactional
	@PreAuthorize("hasAuthority('CREATE_CATEGORY')")
	public Category add(AddCategoryDto dto, User user) {
		Validate.isTrue(dto != null, "DTO should be non null");
		Validate.isTrue(dto.getName() != null, "Category name should be non null");
		Validate.isTrue(user != null, "Current user must be non null");
		
		Category category = new Category();
		category.setName(dto.getName());
		category.setNameRu(dto.getNameRu());
		
		Date now = new Date();
		category.getMetaInfo().setCreatedAt(now);
		category.getMetaInfo().setUpdatedAt(now);
		
		category.getMetaInfo().setCreatedBy(user);
		category.getMetaInfo().setUpdatedBy(user);

		Category entity = categoryDao.save(category);
		LOG.debug("Category has been created ({})", entity);
		
		return entity;
	}
	
	@Override
	@Transactional(readOnly = true)
	public Iterable<EntityInfoDto> findAll() {
		return categoryDao.findAllAsSelectEntries();
	}
	
	@Override
	@Transactional(readOnly = true)
	public long countAll() {
		return categoryDao.count();
	}
	
	@Override
	@Transactional(readOnly = true)
	public int countByName(String name) {
		Validate.isTrue(name != null, "Name should be non null");
		return categoryDao.countByName(name);
	}
	
	@Override
	@Transactional(readOnly = true)
	public int countByNameRu(String name) {
		Validate.isTrue(name != null, "Name on Russian should be non null");
		return categoryDao.countByNameRu(name);
	}
	
}
