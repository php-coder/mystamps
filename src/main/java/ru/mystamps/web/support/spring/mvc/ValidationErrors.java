/*
 * Copyright (C) 2009-2025 Slava Semushin <slava.semushin@gmail.com>
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

import javax.validation.ConstraintViolation;
import javax.validation.ElementKind;
import javax.validation.Path.Node;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
public class ValidationErrors {
	
	private final Map<String, List<String>> fieldErrors = new HashMap<>();
	
	public ValidationErrors(Errors errors) {
		errors.getFieldErrors()
			.forEach(this::extractFieldError);
	}
	
	public ValidationErrors(Set<ConstraintViolation<?>> violations) {
		violations.forEach(this::extractFieldError);
	}
	
	private void extractFieldError(FieldError error) {
		String name    = error.getField();
		String message = error.getDefaultMessage();
		
		getErrorsByFieldName(name).add(message);
	}
	
	// @todo #785 Improve error handling for requests with a list of objects
	private void extractFieldError(ConstraintViolation<?> violation) {
		// Extract a field name from a path: updateSeries.patches[0].value -> value
		// In this case, we loose index (see Node.getIndex() and Node.isInIterable()) but
		// as now we always have requests with a single object, that's not severe.
		// Ideally, for multiple objects we should return ValidationErrors[] instead of
		// ValidationErrors
		Node last = getLastOrNull(violation.getPropertyPath());
		String name;
		if (last != null && last.getKind() == ElementKind.PROPERTY) {
			name = last.getName();
		} else if (last != null && last.getKind() == ElementKind.PARAMETER) {
			// emulate validation of PatchRequest.value field
			name = "value";
		} else {
			// fallback to a field path for unsupported kinds
			name = violation.getPropertyPath().toString();
		}
		
		String message = violation.getMessage();
		
		getErrorsByFieldName(name).add(message);
	}
	
	private List<String> getErrorsByFieldName(String name) {
		return fieldErrors.computeIfAbsent(name, key -> new ArrayList<>());
	}
	
	private static <T> T getLastOrNull(Iterable<T> elements) {
		T last = null;
		for (T elem : elements) {
			last = elem;
		}
		return last;
	}
	
}
