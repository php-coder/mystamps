/*
 * Copyright (C) 2009-2021 Slava Semushin <slava.semushin@gmail.com>
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
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Set;

// @todo #1280 AddSimilarSeriesForm: series and similar series must be different
@Getter
@Setter
public class AddSimilarSeriesForm implements AddSimilarSeriesDto {
	
	// @todo #1280 AddSimilarSeriesForm: seriesId must exist
	@NotNull
	private Integer seriesId;
	
	// @todo #1280 AddSimilarSeriesForm: add integration test for mandatory similarSeriesId
	// @todo #1280 AddSimilarSeriesForm: similarSeriesId must exist
	// @todo #1448 AddSimilarSeriesForm.similarSeriesId: remove deprecated member
	//
	// NOTE: similarSeriesId is deprecated and exist only for backward compatibility
	private Integer similarSeriesId;
	
	private Set<Integer> similarSeriesIds;
	
	@NotNull
	public Integer getSimilarSeriesId() {
		if (similarSeriesId != null) {
			return similarSeriesId;
		}
		
		if (similarSeriesIds != null && !similarSeriesIds.isEmpty()) {
			return similarSeriesIds.iterator().next();
		}
		
		return null;
	}
	
	// @todo #1448 AddSimilarSeriesForm: add integration test that similarSeriesIds isn't empty
	@NotEmpty
	public Set<Integer> getSimilarSeriesIds() {
		// respect the old interface to not break compatibility
		if (similarSeriesId != null) {
			return Collections.singleton(similarSeriesId);
		}
		
		return similarSeriesIds;
	}
	
}
