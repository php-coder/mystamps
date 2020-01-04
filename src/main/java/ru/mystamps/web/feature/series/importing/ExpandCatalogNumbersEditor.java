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
package ru.mystamps.web.feature.series.importing;

import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import ru.mystamps.web.feature.series.CatalogUtils;

import java.beans.PropertyEditorSupport;
import java.util.Set;

/**
 * Expands range of catalog numbers (1-3) into a comma-separated list (1,2,3).
 *
 * @author Slava Semushin
 */
// @todo #694 ExpandCatalogNumbersEditor: add unit tests
@RequiredArgsConstructor
public class ExpandCatalogNumbersEditor extends PropertyEditorSupport {
	
	// We can't use StringUtils.EMPTY constant from commons-lang because
	// we are using StringUtils from Spring.
	private static final String EMPTY = "";
	
	@Override
	public void setAsText(String text) {
		String result = null;
		
		if (text != null) {
			// Ideally, an input value should be pre-processed by StringTrimmerEditor(" ", true)
			// to remove all spaces and trim string into null if needed. But we can't use
			// StringTrimmerEditor because "only one single registered custom editor per property
			// path is supported". That's why we have to duplicate logic of the StringTrimmerEditor
			// here.
			//
			// @todo #694 ExpandCatalogNumbersEditor: find a better way of editors composition
			String value = text.trim();
			value = StringUtils.deleteAny(value, " ");
			
			if (!value.equals(EMPTY)) {
				try {
					// @todo #694 /series/import/request/{id}:
					//  add integration test for trimming of michel numbers
					// @todo #694 CatalogUtils: consider introducing toLongForm(String) method
					Set<String> catalogNumbers = CatalogUtils.parseCatalogNumbers(value);
					result = String.join(",", catalogNumbers);
					
				} catch (IllegalArgumentException ignored) { // NOPMD: EmptyCatchBlock
					// Intentionally empty: invalid values should be retain as-is.
					// They will be rejected later during validation.
				}
			}
		}
		
		setValue(result);
	}
	
}
