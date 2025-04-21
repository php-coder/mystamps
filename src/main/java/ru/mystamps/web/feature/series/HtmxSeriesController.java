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

package ru.mystamps.web.feature.series;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.mystamps.web.support.spring.security.CustomUserDetails;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;

@Controller
@RequiredArgsConstructor
public class HtmxSeriesController {
	
	private final SeriesService seriesService;
	
	@InitBinder("addCommentForm")
	protected void initBinderForComments(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, "comment", new StringTrimmerEditor(true));
	}
	
	@InitBinder("addCatalogNumbersForm")
	protected void initBinderForCatalogNumbers(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, "catalogNumbers", new StringTrimmerEditor(true));
	}
	
	@PatchMapping(
		path = SeriesUrl.INFO_SERIES_PAGE,
		headers = "HX-Trigger=add-comment-form"
	)
	public String updateSeriesComment(
		@PathVariable("id") Integer seriesId,
		@Valid AddCommentForm form,
		BindingResult result,
		@AuthenticationPrincipal CustomUserDetails currentUser,
		Model model,
		HttpServletResponse response
	) throws IOException {
		
		if (seriesId == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		
		if (!seriesService.isSeriesExist(seriesId)) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		
		if (result.hasErrors()) {
			model.addAttribute("isHtmx", true);
			model.addAttribute("seriesId", seriesId);
			response.sendError(HttpStatus.UNPROCESSABLE_ENTITY.value());
			return "series/info :: AddCommentForm";
		}
		
		String comment = form.getComment();
		Integer currentUserId = currentUser.getUserId();
		seriesService.addComment(seriesId, currentUserId, comment);
		
		model.addAttribute("comment", comment);
		return "series/partial/comment";
	}

	@PatchMapping(
		path = SeriesUrl.INFO_SERIES_PAGE,
		headers = "HX-Trigger=add-catalog-numbers-form"
	)
	public String addCatalogNumbers(
		@PathVariable("id") Integer seriesId,
		@Valid AddCatalogNumbersForm form,
		BindingResult result,
		@AuthenticationPrincipal CustomUserDetails currentUser,
		Model model,
		HttpServletResponse response
	) throws IOException {
		
		if (seriesId == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		
		if (!seriesService.isSeriesExist(seriesId)) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		
		if (result.hasErrors()) {
			response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());
			model.addAttribute("isHtmx", true);
			model.addAttribute("seriesId", seriesId);
			return "series/info :: AddCatalogNumbersForm";
		}
		
		Integer currentUserId = currentUser.getUserId();
		seriesService.addCatalogNumbers(
			form.getCatalogName(),
			seriesId,
			form.getCatalogNumbers(),
			currentUserId
		);
		
		// @todo #1671 AddCatalogNumbersForm: update a page without full reload
		response.addHeader("HX-Refresh", "true");
		
		return null;
	}

	@PatchMapping(
		path = SeriesUrl.INFO_SERIES_PAGE,
		headers = "HX-Trigger=add-catalog-price-form"
	)
	public String addCatalogPrice(
		@PathVariable("id") Integer seriesId,
		@Valid AddCatalogPriceForm form,
		BindingResult result,
		@AuthenticationPrincipal CustomUserDetails currentUser,
		HttpServletResponse response
	) throws IOException {
		
		if (seriesId == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		
		if (!seriesService.isSeriesExist(seriesId)) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		
		if (result.hasErrors()) {
			response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());
			// XXX: implement
			return null;
		}
		
		Integer currentUserId = currentUser.getUserId();
		seriesService.addCatalogPrice(
			form.getCatalogName(),
			seriesId,
			form.getPrice(),
			currentUserId
		);
		
		// @todo #1671 AddCatalogPriceForm: update a page without full reload
		response.addHeader("HX-Refresh", "true");
		
		return null;
	}
	
}
