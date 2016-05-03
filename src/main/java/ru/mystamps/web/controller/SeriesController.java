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

import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletResponse;
import javax.validation.groups.Default;

import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.Url;
import ru.mystamps.web.controller.converter.annotation.Category;
import ru.mystamps.web.controller.converter.annotation.Country;
import ru.mystamps.web.controller.converter.annotation.CurrentUser;
import ru.mystamps.web.dao.dto.LinkEntityDto;
import ru.mystamps.web.dao.dto.PurchaseAndSaleDto;
import ru.mystamps.web.dao.dto.SelectEntityDto;
import ru.mystamps.web.dao.dto.SeriesInfoDto;
import ru.mystamps.web.dao.dto.UrlEntityDto;
import ru.mystamps.web.model.AddImageForm;
import ru.mystamps.web.model.AddSeriesForm;
import ru.mystamps.web.service.CategoryService;
import ru.mystamps.web.service.CollectionService;
import ru.mystamps.web.service.CountryService;
import ru.mystamps.web.service.SeriesService;
import ru.mystamps.web.service.dto.SeriesDto;
import ru.mystamps.web.support.spring.security.Authority;
import ru.mystamps.web.support.spring.security.SecurityContextUtils;
import ru.mystamps.web.util.CatalogUtils;
import ru.mystamps.web.util.LocaleUtils;

import static ru.mystamps.web.validation.ValidationRules.MIN_RELEASE_YEAR;

@Controller
@RequiredArgsConstructor
@SuppressWarnings({ "PMD.AvoidDuplicateLiterals", "PMD.TooManyMethods"})
public class SeriesController {
	
	private static final Integer CURRENT_YEAR   = new GregorianCalendar().get(Calendar.YEAR);
	
	private static final Map<Integer, Integer> YEARS;
	
	private final CategoryService categoryService;
	private final CollectionService collectionService;
	private final CountryService countryService;
	private final SeriesService seriesService;
	
	static {
		YEARS = new LinkedHashMap<>();
		for (Integer i = CURRENT_YEAR; i >= MIN_RELEASE_YEAR; i--) {
			YEARS.put(i, i);
		}
	}
	
	@InitBinder("addSeriesForm")
	protected void initBinder(WebDataBinder binder) {
		StringTrimmerEditor editor = new StringTrimmerEditor(" ", true);
		binder.registerCustomEditor(String.class, "michelNumbers", editor);
		binder.registerCustomEditor(String.class, "scottNumbers", editor);
		binder.registerCustomEditor(String.class, "yvertNumbers", editor);
		binder.registerCustomEditor(String.class, "gibbonsNumbers", editor);
		binder.registerCustomEditor(String.class, "comment", new StringTrimmerEditor(true));
	}
	
	@ModelAttribute("years")
	public Map<Integer, Integer> getYears() {
		return YEARS;
	}
	
	@ModelAttribute("categories")
	public Iterable<SelectEntityDto> getCategories(Locale userLocale) {
		String lang = LocaleUtils.getLanguageOrNull(userLocale);
		return categoryService.findAllAsSelectEntities(lang);
	}
	
	@ModelAttribute("countries")
	public Iterable<LinkEntityDto> getCountries(Locale userLocale) {
		String lang = LocaleUtils.getLanguageOrNull(userLocale);
		return countryService.findAllAsLinkEntities(lang);
	}
	
	@RequestMapping(Url.ADD_SERIES_PAGE)
	public AddSeriesForm showForm() {
		
		AddSeriesForm addSeriesForm = new AddSeriesForm();
		addSeriesForm.setPerforated(true);
		
		return addSeriesForm;
	}
	
	@RequestMapping(Url.ADD_SERIES_WITH_CATEGORY_PAGE)
	public String showFormWithCategory(
		@Category @PathVariable("id") LinkEntityDto category,
		Model model) {
		
		AddSeriesForm form = new AddSeriesForm();
		form.setPerforated(true);
		form.setCategory(category);
		
		model.addAttribute("addSeriesForm", form);
		
		return "series/add";
	}
	
	@RequestMapping(Url.ADD_SERIES_WITH_COUNTRY_PAGE)
	public String showFormWithCountry(
		@Country @PathVariable("id") LinkEntityDto country,
		Model model) {
		
		AddSeriesForm form = new AddSeriesForm();
		form.setPerforated(true);
		form.setCountry(country);
		
		model.addAttribute("addSeriesForm", form);
		
		return "series/add";
	}
	
	@RequestMapping(value = Url.ADD_SERIES_PAGE, method = RequestMethod.POST)
	public String processInput(
		@Validated({ Default.class,
			AddSeriesForm.ReleaseDateChecks.class,
			AddSeriesForm.ImageChecks.class }) AddSeriesForm form,
		BindingResult result,
		@CurrentUser Integer currentUserId) {
		
		if (result.hasErrors()) {
			// don't try to re-display file upload field
			form.setImage(null);
			return null;
		}
		
		boolean userCanAddComments = SecurityContextUtils.hasAuthority(
			Authority.ADD_COMMENTS_TO_SERIES
		);
		Integer seriesId = seriesService.add(form, currentUserId, userCanAddComments);
		
		return redirectTo(Url.INFO_SERIES_PAGE, seriesId);
	}
	
	@RequestMapping(Url.INFO_SERIES_PAGE)
	public String showInfo(
		@PathVariable("id") Integer seriesId,
		Model model,
		@CurrentUser Integer currentUserId,
		Locale userLocale,
		HttpServletResponse response)
		throws IOException {
		
		if (seriesId == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		
		String lang = LocaleUtils.getLanguageOrNull(userLocale);
		SeriesDto series = seriesService.findFullInfoById(seriesId, lang);
		if (series == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		
		model.addAttribute("addImageForm", new AddImageForm());
		model.addAttribute("series", series);
		
		// CheckStyle: ignore LineLength for next 4 lines
		model.addAttribute("michelNumbers", CatalogUtils.toShortForm(series.getMichel().getNumbers()));
		model.addAttribute("scottNumbers", CatalogUtils.toShortForm(series.getScott().getNumbers()));
		model.addAttribute("yvertNumbers", CatalogUtils.toShortForm(series.getYvert().getNumbers()));
		model.addAttribute("gibbonsNumbers", CatalogUtils.toShortForm(series.getGibbons().getNumbers()));
		
		model.addAttribute(
			"isSeriesInCollection",
			currentUserId == null
			? false
			: collectionService.isSeriesInCollection(currentUserId, series.getId())
		);
		
		model.addAttribute(
			"allowAddingImages",
			isUserCanAddImagesToSeries(series)
		);
		
		model.addAttribute("maxQuantityOfImagesExceeded", false);
		
		// CheckStyle: ignore LineLength for next 1 line
		List<PurchaseAndSaleDto> purchasesAndSales = seriesService.findPurchasesAndSales(series.getId());
		model.addAttribute("purchasesAndSales", purchasesAndSales);
		
		return "series/info";
	}
	
	@RequestMapping(value = Url.ADD_IMAGE_SERIES_PAGE, method = RequestMethod.POST)
	public String processImage(
			@Validated({ Default.class, AddImageForm.ImageChecks.class }) AddImageForm form,
			BindingResult result,
			@PathVariable("id") Integer seriesId,
			Model model,
			@CurrentUser Integer currentUserId,
			Locale userLocale,
			HttpServletResponse response)
			throws IOException {
		
		if (seriesId == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		
		String lang = LocaleUtils.getLanguageOrNull(userLocale);
		SeriesDto series = seriesService.findFullInfoById(seriesId, lang);
		if (series == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		
		model.addAttribute("series", series);
		
		// CheckStyle: ignore LineLength for next 4 lines
		model.addAttribute("michelNumbers", CatalogUtils.toShortForm(series.getMichel().getNumbers()));
		model.addAttribute("scottNumbers", CatalogUtils.toShortForm(series.getScott().getNumbers()));
		model.addAttribute("yvertNumbers", CatalogUtils.toShortForm(series.getYvert().getNumbers()));
		model.addAttribute("gibbonsNumbers", CatalogUtils.toShortForm(series.getGibbons().getNumbers()));
		
		model.addAttribute(
			"isSeriesInCollection",
			collectionService.isSeriesInCollection(currentUserId, series.getId())
		);
		
		model.addAttribute(
			"allowAddingImages",
			isUserCanAddImagesToSeries(series)
		);
		
		boolean maxQuantityOfImagesExceeded = !isAdmin() && !isAllowedToAddingImages(series);
		model.addAttribute("maxQuantityOfImagesExceeded", maxQuantityOfImagesExceeded);
		
		if (result.hasErrors() || maxQuantityOfImagesExceeded) {
			// don't try to re-display file upload field
			form.setImage(null);
			return "series/info";
		}
		
		seriesService.addImageToSeries(form, series.getId(), currentUserId);
		
		return redirectTo(Url.INFO_SERIES_PAGE, series.getId());
	}
	
	@RequestMapping(
		value = Url.INFO_SERIES_PAGE,
		method = RequestMethod.POST,
		params = "action=ADD"
	)
	public String addToCollection(
		@PathVariable("id") Integer seriesId,
		@CurrentUser Integer currentUserId,
		RedirectAttributes redirectAttributes,
		HttpServletResponse response)
		throws IOException {
		
		if (seriesId == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		
		boolean seriesExists = seriesService.isSeriesExist(seriesId);
		if (!seriesExists) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		
		UrlEntityDto collection = collectionService.addToCollection(currentUserId, seriesId);
		
		redirectAttributes.addFlashAttribute("justAddedSeries", true);
		redirectAttributes.addFlashAttribute("justAddedSeriesId", seriesId);
		
		return redirectTo(Url.INFO_COLLECTION_PAGE, collection.getId(), collection.getSlug());
	}
	
	@RequestMapping(
		value = Url.INFO_SERIES_PAGE,
		method = RequestMethod.POST,
		params = "action=REMOVE"
	)
	public String removeFromCollection(
		@PathVariable("id") Integer seriesId,
		@CurrentUser Integer currentUserId,
		RedirectAttributes redirectAttributes,
		HttpServletResponse response)
		throws IOException {
		
		if (seriesId == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		
		boolean seriesExists = seriesService.isSeriesExist(seriesId);
		if (!seriesExists) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		
		UrlEntityDto collection = collectionService.removeFromCollection(currentUserId, seriesId);
		
		redirectAttributes.addFlashAttribute("justRemovedSeries", true);
		
		return redirectTo(Url.INFO_COLLECTION_PAGE, collection.getId(), collection.getSlug());
	}
	
	@RequestMapping(value = Url.SEARCH_SERIES_BY_CATALOG, method = RequestMethod.POST)
	public String searchSeriesByCatalog(
		@RequestParam("catalogNumber") String catalogNumber,
		@RequestParam("catalogName") String catalogName,
		Model model,
		Locale userLocale,
		RedirectAttributes redirectAttributes)
		throws IOException {
		
		if (StringUtils.isBlank(catalogNumber)) {
			redirectAttributes.addFlashAttribute("numberIsEmpty", true);
			return "redirect:" + Url.INDEX_PAGE;
		}
		
		String lang = LocaleUtils.getLanguageOrNull(userLocale);
		List<SeriesInfoDto> series;
		switch (catalogName) {
			case "michel":
				series = seriesService.findByMichelNumber(catalogNumber, lang);
				break;
			case "scott":
				series = seriesService.findByScottNumber(catalogNumber, lang);
				break;
			case "yvert":
				series = seriesService.findByYvertNumber(catalogNumber, lang);
				break;
			case "gibbons":
				series = seriesService.findByGibbonsNumber(catalogNumber, lang);
				break;
			default:
				series = Collections.emptyList();
				break;
		}
		model.addAttribute("searchResults", series);
		
		return "series/search_result";
	}
	
	private static boolean isAllowedToAddingImages(SeriesDto series) {
		return series.getImageIds().size() <= series.getQuantity();
	}
	
	private static String redirectTo(String url, Object... args) {
		String dstUrl = UriComponentsBuilder.fromUriString(url)
			.buildAndExpand(args)
			.toString();
		
		return "redirect:" + dstUrl;
	}
	
	private static boolean isUserCanAddImagesToSeries(SeriesDto series) {
		return isAdmin()
			|| isOwner(series) && isAllowedToAddingImages(series);
	}
	
	private static boolean isAdmin() {
		return SecurityContextUtils.hasAuthority(Authority.ADD_IMAGES_TO_SERIES);
	}
	
	@SuppressWarnings("PMD.UnusedNullCheckInEquals")
	private static boolean isOwner(SeriesDto series) {
		Integer userId = SecurityContextUtils.getUserId();
		return userId != null
			&& Objects.equals(
				series.getCreatedBy(),
				userId
			);
	}
	
}

