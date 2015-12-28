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

import java.util.Collections;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.controller.converter.annotation.Category;
import ru.mystamps.web.controller.converter.annotation.Country;
import ru.mystamps.web.service.CategoryService;
import ru.mystamps.web.service.CountryService;
import ru.mystamps.web.service.dto.LinkEntityDto;
import ru.mystamps.web.util.LocaleUtils;

@RequiredArgsConstructor
public class LinkEntityDtoGenericConverter implements ConditionalGenericConverter {
	
	private static final Logger LOG = LoggerFactory.getLogger(LinkEntityDtoGenericConverter.class);
	
	private final CategoryService categoryService;
	private final CountryService countryService;
	
	@Override
	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(String.class, LinkEntityDto.class));
	}
	
	@Override
	public Object convert(Object value, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (value == null) {
			LOG.warn("Attempt to convert null");
			return null;
		}
		
		try {
			Integer id = Integer.valueOf(value.toString());
			if (id <= 0) {
				LOG.warn("Attempt to convert non positive number ({})", id);
				return null;
			}
			
			String lang = LocaleUtils.getCurrentLanguageOrNull();
			
			if (targetType.hasAnnotation(Category.class)) {
				return categoryService.findOneAsLinkEntity(id, lang);
				
			} else if (targetType.hasAnnotation(Country.class)) {
				return countryService.findOneAsLinkEntity(id, lang);
			}
			
			LOG.warn(
				"Can't convert type '{}' because it doesn't contain supported annotations",
				targetType
			);
			return null;
			
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
		
		return targetType.hasAnnotation(Category.class) || targetType.hasAnnotation(Country.class);
	}
	
}
