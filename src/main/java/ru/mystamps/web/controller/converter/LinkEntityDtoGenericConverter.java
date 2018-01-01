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
package ru.mystamps.web.controller.converter;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.controller.converter.annotation.Category;
import ru.mystamps.web.controller.converter.annotation.Country;
import ru.mystamps.web.dao.dto.LinkEntityDto;
import ru.mystamps.web.service.CategoryService;
import ru.mystamps.web.service.CountryService;
import ru.mystamps.web.util.LocaleUtils;

@RequiredArgsConstructor
public class LinkEntityDtoGenericConverter implements ConditionalGenericConverter {
	
	private static final Logger LOG = LoggerFactory.getLogger(LinkEntityDtoGenericConverter.class);
	
	private final CategoryService categoryService;
	private final CountryService countryService;
	
	@Override
	public Set<ConvertiblePair> getConvertibleTypes() {
		Set<ConvertiblePair> pairs = new HashSet<>();
		pairs.add(new ConvertiblePair(String.class, LinkEntityDto.class));
		pairs.add(new ConvertiblePair(LinkEntityDto.class, String.class));
		return pairs;
	}
	
	@Override
	public Object convert(Object value, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (value == null) {
			return null;
		}
		
		if (isDto(sourceType) && isString(targetType)) {
			LinkEntityDto dto = (LinkEntityDto)value;
			return String.valueOf(dto.getId());
		}
		
		if (isString(sourceType) && isDto(targetType)) {
			String slug = value.toString();
			if (slug.isEmpty()) {
				return null;
			}
			
			String lang = LocaleUtils.getCurrentLanguageOrNull();
			if (hasCountryAnnotation(targetType)) {
				return countryService.findOneAsLinkEntity(slug, lang);
			}
			
			if (hasCategoryAnnotation(targetType)) {
				return categoryService.findOneAsLinkEntity(slug, lang);
			}
			
			LOG.warn(
				"Can't convert type '{}' because it doesn't contain supported annotations",
				targetType
			);
			
			return null;
		}
		
		LOG.warn("Attempt to convert unsupported types: from {} to {}", sourceType, targetType);
		return null;
	}
	
	@Override
	public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (sourceType == null || targetType == null) {
			return false;
		}
		
		// LinkEntityDto -> String
		if (isDto(sourceType) && isString(targetType)) {
			return true;
		}
		
		// String -> @Category/@Country LinkEntityDto
		return isString(sourceType)
			&& isDto(targetType)
			&& (hasCategoryAnnotation(targetType) || hasCountryAnnotation(targetType));
	}
	
	private static boolean isString(TypeDescriptor type) {
		return String.class.equals(type.getType());
	}
	
	private static boolean isDto(TypeDescriptor type) {
		return LinkEntityDto.class.equals(type.getType());
	}
	
	private static boolean hasCategoryAnnotation(TypeDescriptor type) {
		return type.hasAnnotation(Category.class);
	}
	
	private static boolean hasCountryAnnotation(TypeDescriptor type) {
		return type.hasAnnotation(Country.class);
	}
	
}
