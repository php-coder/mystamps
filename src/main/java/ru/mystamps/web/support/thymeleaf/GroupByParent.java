/*
 * Copyright (C) 2009-2019 Slava Semushin <slava.semushin@gmail.com>
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

import ru.mystamps.web.common.EntityWithParentDto;
import ru.mystamps.web.feature.series.SelectItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Transforms flat list to hierarchical structure suitable for rendering a &lt;select&gt; tag
 * with &lt;optgroup&gt; in Thymeleaf.
 *
 * See also: <a href="https://gist.github.com/php-coder/d3020e4d8d00b8c5befe755c46f06f1b" target="_blank">gist with example</a>.
 */
public final class GroupByParent {
	
	// @todo #592 GroupByParent: add unit tests
	private GroupByParent() {
	}
	
	@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
	public static List<SelectItem> transformEntities(List<EntityWithParentDto> entities) {
		if (entities.isEmpty()) {
			return Collections.emptyList();
		}
		
		List<SelectItem> items = new ArrayList<>();
		String lastParent = null;
		SelectItem lastItem = null;
		
		for (EntityWithParentDto entity : entities) {
			String name   = entity.getName();
			String value  = entity.getId();
			String parent = entity.getParentName();
			
			boolean entityWithoutParent = parent == null;
			boolean createNewItem = entityWithoutParent || !parent.equals(lastParent);
			
			if (createNewItem) {
				lastParent = parent;
				if (entityWithoutParent) {
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
