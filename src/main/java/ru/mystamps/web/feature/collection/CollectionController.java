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
package ru.mystamps.web.feature.collection;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.mystamps.web.common.LocaleUtils;
import ru.mystamps.web.feature.category.CategoryService;
import ru.mystamps.web.feature.country.CountryService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class CollectionController {
	
	private final CategoryService categoryService;
	private final CollectionService collectionService;
	private final CountryService countryService;
	private final MessageSource messageSource;
	
	@GetMapping(CollectionUrl.INFO_COLLECTION_PAGE)
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
		
		if (!seriesOfCollection.isEmpty()) {
			long categoryCounter = categoryService.countCategoriesOf(collectionId);
			long countryCounter  = countryService.countCountriesOf(collectionId);
			long seriesCounter   = collectionService.countSeriesOf(collectionId);
			long stampsCounter   = collectionService.countStampsOf(collectionId);
			
			Map<String, Integer> categoriesStat =
				categoryService.getStatisticsOf(collectionId, lang);
			
			Map<String, Integer> countriesStat = getCountriesStatistics(collectionId, lang);
			
			model.addAttribute("categoryCounter", categoryCounter);
			model.addAttribute("countryCounter", countryCounter);
			model.addAttribute("seriesCounter", seriesCounter);
			model.addAttribute("stampsCounter", stampsCounter);
			
			model.addAttribute("statOfCollectionByCategories", categoriesStat);
			model.addAttribute("statOfCollectionByCountries", countriesStat);
		}
		
		return "collection/info";
	}
	
	@GetMapping(CollectionUrl.ESTIMATION_COLLECTION_PAGE)
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
	
	private Map<String, Integer> getCountriesStatistics(Integer collectionId, String lang) {
		Map<String, Integer> countriesStat = countryService.getStatisticsOf(collectionId, lang);
		
		// manually localize "Unknown" country's name
		Integer unknownCounter = countriesStat.get("Unknown");
		if (unknownCounter != null) {
			String localizedValue =
				messageSource.getMessage("t_unspecified", null, new Locale(lang));
			countriesStat.put(localizedValue, unknownCounter);
			countriesStat.remove("Unknown");
		}
		
		return countriesStat;
	}

}
