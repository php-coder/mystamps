/*
 * Copyright (C) 2009-2019 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.feature.collection;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.Url;
import ru.mystamps.web.feature.category.CategoryService;
import ru.mystamps.web.feature.country.CountryService;
import ru.mystamps.web.util.LocaleUtils;

@Controller
@RequiredArgsConstructor
public class CollectionController {
	
	private final CategoryService categoryService;
	private final CollectionService collectionService;
	private final CountryService countryService;
	private final MessageSource messageSource;
	
	// @todo #884 /collection/{slug}: add a link to collection estimation page
	@GetMapping(Url.INFO_COLLECTION_PAGE)
	public String showInfoBySlug(
		@PathVariable("slug") String slug,
		Model model,
		Locale userLocale,
		HttpServletResponse response)
		throws IOException {
		
		if (slug == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		
		CollectionInfoDto collection = collectionService.findBySlug(slug);
		if (collection == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		
		String owner = collection.getOwnerName();
		model.addAttribute("ownerName", owner);
		
		Integer collectionId = collection.getId();
		String lang = LocaleUtils.getLanguageOrNull(userLocale);
		List<SeriesInCollectionDto> seriesOfCollection =
			collectionService.findSeriesInCollection(collectionId, lang);
		model.addAttribute("seriesOfCollection", seriesOfCollection);
		
		if (seriesOfCollection.iterator().hasNext()) {
			long categoryCounter = categoryService.countCategoriesOf(collectionId);
			long countryCounter  = countryService.countCountriesOf(collectionId);
			long seriesCounter   = collectionService.countSeriesOf(collectionId);
			long stampsCounter   = collectionService.countStampsOf(collectionId);
			
			List<Object[]> categoriesStat = categoryService.getStatisticsOf(collectionId, lang);
			List<Object[]> countriesStat  = getCountriesStatistics(collectionId, lang);
			
			model.addAttribute("categoryCounter", categoryCounter);
			model.addAttribute("countryCounter", countryCounter);
			model.addAttribute("seriesCounter", seriesCounter);
			model.addAttribute("stampsCounter", stampsCounter);
			
			model.addAttribute("statOfCollectionByCategories", categoriesStat);
			model.addAttribute("statOfCollectionByCountries", countriesStat);
		}
		
		return "collection/info";
	}
	
	// @todo #884 Add integration tests for collection estimation page
	@GetMapping(Url.ESTIMATION_COLLECTION_PAGE)
	public String showPrices(
		@PathVariable("slug") String slug,
		Model model,
		Locale userLocale,
		HttpServletResponse response)
		throws IOException {
		
		if (slug == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		
		// FIXME: we need only ownerName, without id and slug
		CollectionInfoDto collection = collectionService.findBySlug(slug);
		if (collection == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		
		String owner = collection.getOwnerName();
		model.addAttribute("ownerName", owner);
		
		String lang = LocaleUtils.getLanguageOrNull(userLocale);
		List<SeriesInCollectionWithPriceDto> seriesOfCollection =
			collectionService.findSeriesWithPricesBySlug(slug, lang);
		model.addAttribute("seriesOfCollection", seriesOfCollection);
		
		return "collection/estimation";
	}

	@GetMapping(Url.INFO_COLLECTION_BY_ID_PAGE)
	public View showInfoById(
		@PathVariable("slug") String slug,
		HttpServletResponse response)
		throws IOException {
		
		if (slug == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		
		CollectionInfoDto collection = collectionService.findBySlug(slug);
		if (collection == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		
		RedirectView view = new RedirectView();
		view.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
		view.setUrl(Url.INFO_COLLECTION_PAGE);
		
		return view;
	}
	
	@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
	private List<Object[]> getCountriesStatistics(Integer collectionId, String lang) {
		List<Object[]> countriesStat = countryService.getStatisticsOf(collectionId, lang);
		
		for (Object[] countryStat : countriesStat) {
			// manually localize "Unknown" country's name
			Object name = countryStat[0];
			if ("Unknown".equals(name)) {
				countryStat[0] = messageSource.getMessage("t_unspecified", null, new Locale(lang));
			}
		}
		
		return countriesStat;
	}

}
