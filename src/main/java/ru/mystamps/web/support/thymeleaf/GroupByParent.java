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

import ru.mystamps.web.dao.dto.CategoryDto;
import ru.mystamps.web.dao.dto.TransactionParticipantDto;
import ru.mystamps.web.service.dto.FirstLevelCategoryDto;
import ru.mystamps.web.service.dto.GroupedTransactionParticipantDto;

/**
 * Transforms flat list to hierarchical structure suitable for rendering a &lt;select&gt; tag
 * with &lt;optgroup&gt; in Thymeleaf.
 *
 * See also: <a href="https://gist.github.com/php-coder/d3020e4d8d00b8c5befe755c46f06f1b" target="_blank">gist with example</a>.
 */
public final class GroupByParent {
	
	private GroupByParent() {
	}
	
	@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
	public static List<GroupedTransactionParticipantDto> transformParticipants(
		List<TransactionParticipantDto> participants) {
		
		if (participants.isEmpty()) {
			return Collections.emptyList();
		}
		
		List<GroupedTransactionParticipantDto> items = new ArrayList<>();
		String lastParent = null;
		GroupedTransactionParticipantDto lastItem = null;
		
		for (TransactionParticipantDto participant : participants) {
			String name   = participant.getName();
			Integer id    = participant.getId();
			String parent = participant.getParentName();
			
			boolean participantWithoutParent = parent == null;
			boolean createNewItem = participantWithoutParent || !parent.equals(lastParent);
			
			if (createNewItem) {
				lastParent = parent;
				if (participantWithoutParent) {
					lastItem = new GroupedTransactionParticipantDto(id, name);
				} else {
					lastItem = new GroupedTransactionParticipantDto(parent);
					lastItem.addChild(id, name);
				}
				items.add(lastItem);
			} else {
				lastItem.addChild(id, name);
			}
		}
		
		return items;
	}
	
	@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
	public static List<FirstLevelCategoryDto> transformCategories(List<CategoryDto> categories) {
		if (categories.isEmpty()) {
			return Collections.emptyList();
		}
		
		List<FirstLevelCategoryDto> items = new ArrayList<>();
		String lastParent = null;
		FirstLevelCategoryDto lastItem = null;
		
		for (CategoryDto category : categories) {
			String name   = category.getName();
			String slug   = category.getSlug();
			String parent = category.getParentName();
			
			boolean categoryWithoutParent = parent == null;
			boolean createNewItem = categoryWithoutParent || !parent.equals(lastParent);
			
			if (createNewItem) {
				lastParent = parent;
				if (categoryWithoutParent) {
					lastItem = new FirstLevelCategoryDto(slug, name);
				} else {
					lastItem = new FirstLevelCategoryDto(parent);
					lastItem.addChild(slug, name);
				}
				items.add(lastItem);
			} else {
				lastItem.addChild(slug, name);
			}
		}
		
		return items;
	}
	
}
