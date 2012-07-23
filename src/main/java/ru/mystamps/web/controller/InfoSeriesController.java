/*
 * Copyright (C) 2012 Slava Semushin <slava.semushin@gmail.com>
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

package ru.mystamps.web.controller;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.mystamps.web.entity.Series;
import ru.mystamps.web.service.SeriesService;
import ru.mystamps.web.Url;
import ru.mystamps.web.util.CatalogUtils;

@Controller
@RequestMapping(Url.INFO_SERIES_PAGE)
public class InfoSeriesController {
	
	private final SeriesService seriesService;
	
	@Inject
	InfoSeriesController(final SeriesService seriesService) {
		this.seriesService = seriesService;
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public String showInfo(
		@PathVariable("id") final Integer id,
		final Model model,
		final HttpServletResponse response) throws IOException {
		
		final Series series = seriesService.findById(id);
		if (series == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		
		model.addAttribute("series", series);
		model.addAttribute("michelNumbers", CatalogUtils.toShortForm(series.getMichel()));
		model.addAttribute("scottNumbers", CatalogUtils.toShortForm(series.getScott()));
		model.addAttribute("yvertNumbers", CatalogUtils.toShortForm(series.getYvert()));
		model.addAttribute("gibbonsNumbers", CatalogUtils.toShortForm(series.getGibbons()));
		
		return "series/info";
	}
	
}

