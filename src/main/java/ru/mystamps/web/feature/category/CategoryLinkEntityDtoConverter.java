/*
 * Copyright (C) 2009-2020 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.feature.category;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import ru.mystamps.web.common.LinkEntityDto;
import ru.mystamps.web.common.LocaleUtils;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
public class CategoryLinkEntityDtoConverter implements ConditionalGenericConverter {
	
	private static final Logger LOG = LoggerFactory.getLogger(CategoryLinkEntityDtoConverter.class);
	
	private final CategoryService categoryService;
	
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
			
			if (hasCategoryAnnotation(targetType)) {
				String lang = LocaleUtils.getCurrentLanguageOrNull();
				return categoryService.findOneAsLinkEntity(slug, lang);
			}
			
			LOG.warn(
				"Can't convert type '{}' because it doesn't contain @Category annotation",
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
		
		// String -> @Category LinkEntityDto
		return isString(sourceType) && isDto(targetType) && hasCategoryAnnotation(targetType);
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
	
}
