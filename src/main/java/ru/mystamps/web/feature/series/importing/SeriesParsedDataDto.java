/*
 * Copyright (C) 2009-2024 Slava Semushin <slava.semushin@gmail.com>
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

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.mystamps.web.common.LinkEntityDto;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class SeriesParsedDataDto {
	private final LinkEntityDto category;
	private final LinkEntityDto country;
	private final Integer issueDay;
	private final Integer issueMonth;
	private final Integer issueYear;
	private final Integer quantity;
	private final Boolean perforated;
	private final String michelNumbers;
	
	@Setter
	private List<String> imageUrls;
	
	// for backward compatibility
	public String getImageUrl() {
		return imageUrls == null || imageUrls.isEmpty() ? null : imageUrls.get(0);
	}
	
}
