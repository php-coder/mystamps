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
package ru.mystamps.web.feature.series.importing.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Event occurs when parsing of a file from import request has been failed.
 *
 * It could occurs by 2 reasons:
 * - when we couldn't extract anything meaningful
 *   (elements specified by locators don't contain info)
 * - when we extracted information but were unable to match it with database
 *   (for example, we extracted country named "Italy" but it doesn't exist in database)
 */
@Getter
public class ParsingFailed extends ApplicationEvent {
	private final Integer requestId;
	
	public ParsingFailed(Object source, Integer requestId) {
		super(source);
		this.requestId = requestId;
	}
	
}
