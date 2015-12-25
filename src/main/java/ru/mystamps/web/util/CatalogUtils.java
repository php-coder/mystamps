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
package ru.mystamps.web.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import ru.mystamps.web.entity.StampsCatalog;

/**
 * Helpers for dealing with stamps catalog numbers.
 **/
public final class CatalogUtils {
	
	private static final int ONE_ELEMENT_SIZE = 1;
	private static final int TWO_ELEMENTS_SIZE = 2;
	
	private static final Comparator<String> STR_AFTER_INT =
		new Comparator<String>() {
			@Override
			public int compare(String lhs, String rhs) {
				try {
					Integer left = Integer.valueOf(lhs);
					Integer right = Integer.valueOf(rhs);
					return left.compareTo(right);
				
				} catch (NumberFormatException ex) {
					return 1;
				}
				
			}
		};
	
	private CatalogUtils() {
	}
	
	/**
	 * Converts set of catalog numbers to comma-delimited string with range of numbers.
	 **/
	public static String toShortForm(Set<? extends StampsCatalog> catalogNumbers) {
		Validate.isTrue(catalogNumbers != null, "Catalog numbers must be non null");
		
		if (catalogNumbers.isEmpty()) {
			return "";
		}
		
		Set<String> numbers = new TreeSet<>(STR_AFTER_INT);
		for (StampsCatalog catalog : catalogNumbers) {
			numbers.add(catalog.getCode());
		}
		
		List<String> groups = new ArrayList<>();
		List<String> currentBuffer = new ArrayList<>();
		for (String currentString : numbers) {
			
			// for first element
			if (currentBuffer.isEmpty()) {
				currentBuffer.add(currentString);
				continue;
			}
			
			// in range
			Integer current = Integer.valueOf(currentString);
			Integer previous = Integer.valueOf(currentBuffer.get(currentBuffer.size() - 1));
			if (previous + 1 == current) {
				currentBuffer.add(currentString);
				continue;
			}
			
			addBufferToGroups(currentBuffer, groups);
			currentBuffer.clear();
			
			// start new group
			currentBuffer.add(currentString);
		}
		
		addBufferToGroups(currentBuffer, groups);
		
		return StringUtils.join(groups, ", ");
	}
	
	/**
	 * Parses comma-delimited string and converts catalog numbers to set of strings.
	 **/
	public static Set<String> parseCatalogNumbers(String catalogNumbers) {
		
		if (StringUtils.isEmpty(catalogNumbers)) {
			return Collections.emptySet();
		}
		
		Set<String> result = new LinkedHashSet<>();
		for (String number : catalogNumbers.split(",")) {
			Validate.validState(!number.trim().isEmpty(), "Catalog number must be non empty");
			
			// TODO: parse range of numbers
			
			result.add(number);
		}
		
		return result;
	}
	
	private static void addBufferToGroups(List<String> buffer, List<String> groups) {
		Validate.isTrue(!buffer.isEmpty(), "Buffer must be non-empty");
		
		String firstElement = buffer.get(0);
		String lastElement = buffer.get(buffer.size() - 1);
		
		if (buffer.size() == ONE_ELEMENT_SIZE) {
			groups.add(firstElement);
			
		} else if (buffer.size() == TWO_ELEMENTS_SIZE) {
			groups.add(firstElement);
			groups.add(lastElement);
			
		// save sequence as range
		} else {
			groups.add(firstElement + "-" + lastElement);
		}
	}
	
}
