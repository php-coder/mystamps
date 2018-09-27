/*
 * Copyright (C) 2009-2018 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.feature.series;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Data for representation of a link to a series.
 *
 * Example of a link:
 * <code>
 * &lt;a href="/series/{id}"&gt;{country}, {year}, {quantity} stamps (without perforation)&lt;/a&gt;
 * </code>
 */
@Getter
@ToString
@RequiredArgsConstructor
public class SeriesLinkDto {
	private final Integer id;
	private final Integer year;
	private final Integer quantity;
	private final Boolean perforated;
	private final String country;
}
