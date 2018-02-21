/*
 * Copyright (C) 2009-2017 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.support.thymeleaf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.mystamps.web.controller.dto.SelectItem;
import ru.mystamps.web.dao.dto.CategoryDto;
import ru.mystamps.web.dao.dto.TransactionParticipantDto;

/**
 * Transforms flat list to hierarchical structure suitable for rendering a &lt;select&gt; tag
 * with &lt;optgroup&gt; in Thymeleaf.
 *
 * See also: <a href="https://gist.github.com/php-coder/d3020e4d8d00b8c5befe755c46f06f1b" target="_blank">gist with example</a>.
 */
public final class GroupByParent {
	
	// @todo #592 GroupByParent: add unit test
	private GroupByParent() {
	}
	
	// @todo #592 GroupByParent.transformParticipants(): introduce unified class for representing entity with parent
	// @todo #592 GroupByParent: merge transformCategories() and transformParticipants() methods
	@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
	public static List<SelectItem> transformParticipants(
		List<TransactionParticipantDto> participants) {
		
		if (participants.isEmpty()) {
			return Collections.emptyList();
		}
		
		List<SelectItem> items = new ArrayList<>();
		String lastParent = null;
		SelectItem lastItem = null;
		
		for (TransactionParticipantDto participant : participants) {
			String name   = participant.getName();
			String value  = participant.getId().toString();
			String parent = participant.getParentName();
			
			boolean participantWithoutParent = parent == null;
			boolean createNewItem = participantWithoutParent || !parent.equals(lastParent);
			
			if (createNewItem) {
				lastParent = parent;
				if (participantWithoutParent) {
					lastItem = new SelectItem(name, value);
				} else {
					lastItem = new SelectItem(parent);
					lastItem.addChild(name, value);
				}
				items.add(lastItem);
			} else {
				lastItem.addChild(name, value);
			}
		}
		
		return items;
	}
	
	// @todo #592 GroupByParent.transformCategories(): use unified class that represents entity with parent
	@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
	public static List<SelectItem> transformCategories(List<CategoryDto> categories) {
		if (categories.isEmpty()) {
			return Collections.emptyList();
		}
		
		List<SelectItem> items = new ArrayList<>();
		String lastParent = null;
		SelectItem lastItem = null;
		
		for (CategoryDto category : categories) {
			String name   = category.getName();
			String value  = category.getSlug();
			String parent = category.getParentName();
			
			boolean categoryWithoutParent = parent == null;
			boolean createNewItem = categoryWithoutParent || !parent.equals(lastParent);
			
			if (createNewItem) {
				lastParent = parent;
				if (categoryWithoutParent) {
					lastItem = new SelectItem(name, value);
				} else {
					lastItem = new SelectItem(parent);
					lastItem.addChild(name, value);
				}
				items.add(lastItem);
			} else {
				lastItem.addChild(name, value);
			}
		}
		
		return items;
	}
	
}
