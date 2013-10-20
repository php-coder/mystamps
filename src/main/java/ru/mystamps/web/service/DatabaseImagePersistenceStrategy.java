/*
 * Copyright (C) 2009-2013 Slava Semushin <slava.semushin@gmail.com>
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

import org.apache.commons.lang3.Validate;

import ru.mystamps.web.dao.ImageDao;
import ru.mystamps.web.entity.Image;

public class DatabaseImagePersistenceStrategy implements ImagePersistenceStrategy {
	
	private final ImageDao imageDao;
	
	@Inject
	public DatabaseImagePersistenceStrategy(ImageDao imageDao) {
		this.imageDao = imageDao;
	}
	
	@Override
	public Image get(Integer id) {
		Validate.isTrue(id != null, "Image id must be non null");
		Validate.isTrue(id > 0, "Image id must be greater than zero");
		
		return imageDao.findOne(id);
	}
	
}
