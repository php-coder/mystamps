/*
 * Copyright (C) 2009-2014 Slava Semushin <slava.semushin@gmail.com>
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

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.mystamps.web.Url;
import ru.mystamps.web.service.CountryService;
import ru.mystamps.web.service.SeriesService;

@Controller
public class SiteController {
	
	private final CountryService countryService;
	private final SeriesService seriesService;
	
	public SiteController(CountryService countryService, SeriesService seriesService) {
		this.countryService = countryService;
		this.seriesService = seriesService;
	}
	
	@RequestMapping(value = Url.INDEX_PAGE, method = RequestMethod.GET)
	public String showIndexPage(Model model) {
		model.addAttribute("countryCounter", countryService.countAll());
		model.addAttribute("seriesCounter", seriesService.countAll());
		model.addAttribute("stampsCounter", seriesService.countAllStamps());
		return "site/index";
	}
	
}
