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
package ru.mystamps.web.feature.site;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.mystamps.web.common.LinkEntityDto;
import ru.mystamps.web.common.LocaleUtils;
import ru.mystamps.web.common.Pager;
import ru.mystamps.web.feature.category.CategoryService;
import ru.mystamps.web.feature.collection.CollectionService;
import ru.mystamps.web.feature.country.CountryService;
import ru.mystamps.web.feature.series.SeriesLinkDto;
import ru.mystamps.web.feature.series.SeriesService;

import java.util.List;
import java.util.Locale;

@Controller
@RequiredArgsConstructor
public class SiteController {
	
	@SuppressWarnings("PMD.LongVariable")
	private static final int AMOUNT_OF_RECENTLY_ADDED_SERIES = 10;
	
	@SuppressWarnings("PMD.LongVariable")
	private static final int AMOUNT_OF_RECENTLY_CREATED_COLLECTIONS = 10;
	
	private static final int RECORDS_PER_PAGE = 50;
	
	private final CategoryService categoryService;
	private final CollectionService collectionService;
	private final CountryService countryService;
	private final SeriesService seriesService;
	private final SuspiciousActivityService suspiciousActivityService;
	
	@GetMapping(SiteUrl.INDEX_PAGE)
	public String showIndexPage(Model model, Locale userLocale) {
		long categoryCounter    = categoryService.countAll();
		long countryCounter     = countryService.countAll();
		long seriesCounter      = seriesService.countAll();
		long stampsCounter      = seriesService.countAllStamps();
		long collectionsCounter = collectionService.countCollectionsOfUsers();
		
		String lang = LocaleUtils.getLanguageOrNull(userLocale);
		List<SeriesLinkDto> recentlyAdded =
			seriesService.findRecentlyAdded(AMOUNT_OF_RECENTLY_ADDED_SERIES, lang);
		
		List<LinkEntityDto> recentlyCreated =
			collectionService.findRecentlyCreated(AMOUNT_OF_RECENTLY_CREATED_COLLECTIONS);
		
		model.addAttribute("categoryCounter", categoryCounter);
		model.addAttribute("countryCounter", countryCounter);
		model.addAttribute("seriesCounter", seriesCounter);
		model.addAttribute("stampsCounter", stampsCounter);
		model.addAttribute("collectionsCounter", collectionsCounter);
		model.addAttribute("recentlyAddedSeries", recentlyAdded);
		model.addAttribute("recentlyAddedCollections", recentlyCreated);
		
		return "site/index";
	}
	
	/**
	 * @author Sergey Chechenev
	 * @author Slava Semushin
	 */
	@GetMapping(SiteUrl.SITE_EVENTS_PAGE)
	public void viewSiteEvents(
		@RequestParam(name = "page", defaultValue = "1") int pageNum,
		Model model) {
		
		int page = Math.max(1, pageNum);
		long activitiesRecords = suspiciousActivityService.countAll();
		List<SuspiciousActivityDto> activities =
			suspiciousActivityService.findSuspiciousActivities(page, RECORDS_PER_PAGE);
		Pager pager = new Pager(activitiesRecords, RECORDS_PER_PAGE, page);
		
		model.addAttribute("pager", pager);
		model.addAttribute("activities", activities);
	}
	
}
