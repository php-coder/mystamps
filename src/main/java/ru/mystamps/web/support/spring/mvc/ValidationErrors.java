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
package ru.mystamps.web.support.spring.mvc;

import lombok.Getter;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class ValidationErrors {
	
	private final Map<String, List<String>> fieldErrors = new HashMap<>();
	
	public ValidationErrors(Errors errors) {
		errors.getFieldErrors()
			.forEach(this::extractFieldError);
	}
	
	private void extractFieldError(FieldError error) {
		String name    = error.getField();
		String message = error.getDefaultMessage();
		
		getErrorsByFieldName(name).add(message);
	}
	
	private List<String> getErrorsByFieldName(String name) {
		return fieldErrors.computeIfAbsent(name, key -> new ArrayList<>());
	}
	
}
