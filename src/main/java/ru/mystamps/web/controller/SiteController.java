/*
 * Copyright (C) 2009-2016 Slava Semushin <slava.semushin@gmail.com>
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

import java.util.Locale;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.Url;
import ru.mystamps.web.service.CategoryService;
import ru.mystamps.web.service.CollectionService;
import ru.mystamps.web.service.CountryService;
import ru.mystamps.web.service.SeriesService;
import ru.mystamps.web.service.SuspiciousActivityService;
import ru.mystamps.web.util.LocaleUtils;

@Controller
@RequiredArgsConstructor
public class SiteController {
	
	private static final int AMOUNT_OF_RECENTLY_ADDED_SERIES = 10; // NOPMD: LongVariable
	private static final int AMOUNT_OF_RECENTLY_CREATED_COLLECTIONS = 10; // NOPMD: LongVariable
	
	private final CategoryService categoryService;
	private final CollectionService collectionService;
	private final CountryService countryService;
	private final SeriesService seriesService;
	private final SuspiciousActivityService suspiciousActivityService;
	
	@RequestMapping(Url.INDEX_PAGE)
	public String showIndexPage(Model model, Locale userLocale) {
		model.addAttribute("categoryCounter", categoryService.countAll());
		model.addAttribute("countryCounter", countryService.countAll());
		model.addAttribute("seriesCounter", seriesService.countAll());
		model.addAttribute("stampsCounter", seriesService.countAllStamps());
		model.addAttribute("collectionsCounter", collectionService.countCollectionsOfUsers());
		
		String lang = LocaleUtils.getLanguageOrNull(userLocale);
		model.addAttribute(
			"recentlyAddedSeries",
			seriesService.findRecentlyAdded(AMOUNT_OF_RECENTLY_ADDED_SERIES, lang)
		);
		
		model.addAttribute(
			"recentlyAddedCollections",
			collectionService.findRecentlyCreated(AMOUNT_OF_RECENTLY_CREATED_COLLECTIONS)
		);
		
		return "site/index";
	}
	
	/**
	 * @author Sergey Chechenev
	 */
	@RequestMapping(Url.SITE_EVENTS_PAGE)
	public Model browseSiteEvents(Model model) {
		model.addAttribute(
			"activities",
			suspiciousActivityService.findSuspiciousActivities()
		);
		
		return model;
	}
	
}
