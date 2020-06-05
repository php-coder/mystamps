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
package ru.mystamps.web.feature.series;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.mystamps.web.support.spring.mvc.PatchRequest;
import ru.mystamps.web.support.spring.mvc.PatchRequest.Operation;
import ru.mystamps.web.support.spring.security.CurrentUser;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.List;

import static ru.mystamps.web.feature.series.StampsCatalog.GIBBONS;
import static ru.mystamps.web.feature.series.StampsCatalog.MICHEL;
import static ru.mystamps.web.feature.series.StampsCatalog.SCOTT;
import static ru.mystamps.web.feature.series.StampsCatalog.SOLOVYOV;
import static ru.mystamps.web.feature.series.StampsCatalog.YVERT;
import static ru.mystamps.web.feature.series.StampsCatalog.ZAGORSKI;

@Validated
@RestController
@RequiredArgsConstructor
class RestSeriesController {
	
	private final SeriesService seriesService;
	
	// @todo #785 Update series: add integration test
	// @todo #785 Update series: add validation for a comment
	// @todo #1340 Update series: add validation for a price
	// @todo #1343 Update series: add validation for a release year
	@PatchMapping(SeriesUrl.INFO_SERIES_PAGE)
	public ResponseEntity<Void> updateSeries(
		@PathVariable("id") Integer seriesId,
		@RequestBody @Valid @NotEmpty List<@Valid PatchRequest> patches,
		@CurrentUser Integer currentUserId,
		HttpServletResponse response) throws IOException {
		
		if (seriesId == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		
		if (!seriesService.isSeriesExist(seriesId)) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		
		for (PatchRequest patch : patches) {
			if (patch.getOp() != Operation.add) {
				// @todo #785 Update series: properly fail on non-supported operations
				continue;
			}
			
			// CheckStyle: ignore LineLength for next 24 lines
			switch (patch.getPath()) {
				case "/comment":
					seriesService.addComment(seriesId, patch.getValue());
					break;
				case "/release_year":
					seriesService.addReleaseYear(seriesId, patch.integerValue(), currentUserId);
					break;
				case "/michel_price":
					seriesService.addPrice(MICHEL, seriesId, patch.bigDecimalValue(), currentUserId);
					break;
				case "/scott_price":
					seriesService.addPrice(SCOTT, seriesId, patch.bigDecimalValue(), currentUserId);
					break;
				case "/yvert_price":
					seriesService.addPrice(YVERT, seriesId, patch.bigDecimalValue(), currentUserId);
					break;
				case "/gibbons_price":
					seriesService.addPrice(GIBBONS, seriesId, patch.bigDecimalValue(), currentUserId);
					break;
				case "/solovyov_price":
					seriesService.addPrice(SOLOVYOV, seriesId, patch.bigDecimalValue(), currentUserId);
					break;
				case "/zagorski_price":
					seriesService.addPrice(ZAGORSKI, seriesId, patch.bigDecimalValue(), currentUserId);
					break;
				default:
					// @todo #785 Update series: properly fail on invalid path
					break;
			}
		}
		
		return ResponseEntity.noContent().build();
	}
	
}

