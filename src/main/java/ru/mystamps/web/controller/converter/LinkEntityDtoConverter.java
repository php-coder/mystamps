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
package ru.mystamps.web.controller.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalConverter;
import org.springframework.core.convert.converter.Converter;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.service.CategoryService;
import ru.mystamps.web.service.dto.LinkEntityDto;
import ru.mystamps.web.controller.converter.annotation.Category;
import ru.mystamps.web.util.LocaleUtils;

@RequiredArgsConstructor
public class LinkEntityDtoConverter
	implements Converter<String, LinkEntityDto>, ConditionalConverter {
	
	private static final Logger LOG = LoggerFactory.getLogger(LinkEntityDtoConverter.class);
	
	private final CategoryService categoryService;
	
	@Override
	public LinkEntityDto convert(String value) {
		String lang = LocaleUtils.getCurrentLanguageOrNull();
		
		try {
			Integer id = Integer.valueOf(value);
			if (id <= 0) {
				LOG.warn("Attempt to convert non positive number ({})", id);
				return null;
			}
			
			return categoryService.findOneAsLinkEntity(id, lang);
			
		} catch (NumberFormatException ex) {
			LOG.warn("Can't convert value '{}' from string to integer: {}", value, ex.getMessage());
			return null;
		}
	}
	
	@Override
	public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (targetType == null) {
			LOG.warn("Can't determine whether type matches or not because target type is null");
			return false;
		}
		
		return targetType.hasAnnotation(Category.class);
	}
	
}
