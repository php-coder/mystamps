/*
 * Copyright (C) 2012 Slava Semushin <slava.semushin@gmail.com>
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

package ru.mystamps.web.util;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang3.reflect.ConstructorUtils;

import com.google.common.base.Throwables;

import ru.mystamps.web.entity.StampsCatalog;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * Helpers for dealing with stamps catalog numbers.
 **/
public final class CatalogUtils {
	
	private CatalogUtils() {
	}
	
	/**
	 * Converts set of catalog numbers from objects representation to comma-delimited string.
	 **/
	public static String toShortForm(final Set<? extends StampsCatalog> catalogNumbers) {
		checkArgument(catalogNumbers != null, "Catalog numbers must be non null");
		
		if (catalogNumbers.isEmpty()) {
			return "";
		}
		
		final StringBuilder sb = new StringBuilder();
		
		// TODO: more sophisticated impl
		boolean firstElement = true;
		for (final StampsCatalog catalog : catalogNumbers) {
			if (firstElement) {
				firstElement = false;
			} else {
				sb.append(", ");
			}
			
			sb.append(catalog.getCode());
		}
		
		return sb.toString();
	}
	
	/**
	 * Parses comma-delimited string and converts catalog numbers to set of objects.
	 **/
	public static <T extends StampsCatalog> Set<T> fromString(
		final String catalogNumbers,
		final Class<T> elementClass) {
		
		if (isNullOrEmpty(catalogNumbers)) {
			return Collections.emptySet();
		}
		
		checkArgument(elementClass != null, "Class of element must be non null");
		
		final Set<T> result = new LinkedHashSet<T>();
		for (final String number : catalogNumbers.split(",")) {
			checkState(!number.trim().isEmpty(), "Catalog number must be non empty");
			
			// TODO: parse range of numbers
			
			try {
				result.add(
					ConstructorUtils.invokeConstructor(elementClass, number)
				);
				
			} catch (final Exception ex) {
				throw Throwables.propagate(ex);
			}
		}
		
		return result;
	}
	
}
