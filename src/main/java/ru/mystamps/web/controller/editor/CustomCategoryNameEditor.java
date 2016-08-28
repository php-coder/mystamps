/*
 * Copyright (C) 2009-2016 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.controller.editor;

import java.beans.PropertyEditorSupport;

public class CustomCategoryNameEditor extends PropertyEditorSupport {

	public static final String CATEGORY_REPEAT_HYPHEN_REGEXP = "[-]{2,}";
	@Override
	public void setAsText(String name) throws IllegalArgumentException {
		if (name.contains("--")) {
			setValue(name.replaceAll(CATEGORY_REPEAT_HYPHEN_REGEXP, "-").trim());
		} else {
			setValue(name.trim());
		}
	}
	
}
