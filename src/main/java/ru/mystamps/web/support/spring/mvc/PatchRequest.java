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

package ru.mystamps.web.support.spring.mvc;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

// See for details: http://jsonpatch.com
@Getter
@Setter
@ToString
public class PatchRequest {

	public enum Operation {
		add, copy, move, remove, replace, test;
	}

	// @todo #785 Update series: add integration test for required "op" field
	@NotNull
	private Operation op;

	// @todo #785 Update series: add integration test for non-empty "path" field
	@NotEmpty
	private String path;

	// @todo #785 Update series: add integration test for non-empty "value" field
	@NotEmpty
	private String value;

}
