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
package ru.mystamps.web.tests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openqa.selenium.WebElement;

public final class WebElementUtils {
	
	private WebElementUtils() {
	}
	
	public static List<String> convertToListWithText(List<WebElement> elements) {
		if (elements.isEmpty()) {
			return Collections.emptyList();
		}
		
		List<String> result = new ArrayList<>(elements.size());
		for (WebElement el : elements) {
			String text = null;
			if ("input".equals(el.getTagName())) {
				text = el.getAttribute("value");
			} else {
				text = el.getText();
			}
			if (text != null) {
				result.add(text);
			}
		}
		
		return result;
	}
	
}
