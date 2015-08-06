/*
 * Copyright (C) 2009-2015 Slava Semushin <slava.semushin@gmail.com>
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.groups.Default;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.Url;
import ru.mystamps.web.entity.Collection;
import ru.mystamps.web.entity.User;
import ru.mystamps.web.model.AddImageForm;
import ru.mystamps.web.model.AddSeriesForm;
import ru.mystamps.web.model.AddSeriesForm.ScottCatalogChecks;
import ru.mystamps.web.model.AddSeriesForm.GibbonsCatalogChecks;
import ru.mystamps.web.model.AddSeriesForm.MichelCatalogChecks;
import ru.mystamps.web.model.AddSeriesForm.YvertCatalogChecks;
import ru.mystamps.web.entity.Series;
import ru.mystamps.web.service.CategoryService;
import ru.mystamps.web.service.CollectionService;
import ru.mystamps.web.service.CountryService;
import ru.mystamps.web.service.SeriesService;
import ru.mystamps.web.service.dto.SelectEntityDto;
import ru.mystamps.web.support.spring.security.SecurityContextUtils;
import ru.mystamps.web.util.CatalogUtils;
import ru.mystamps.web.util.LocaleUtils;

@Controller
@RequiredArgsConstructor
@SuppressWarnings({ "PMD.AvoidDuplicateLiterals", "PMD.TooManyMethods"})
public class SeriesController {
	
	private static final Integer SINCE_YEAR     = 1840;
	private static final Integer CURRENT_YEAR   = new GregorianCalendar().get(Calendar.YEAR);
	
	private static final Map<Integer, Integer> YEARS;
	
	private final CategoryService categoryService;
	private final CollectionService collectionService;
	private final CountryService countryService;
	private final SeriesService seriesService;
	
	static {
		YEARS = new LinkedHashMap<>();
		for (Integer i = CURRENT_YEAR; i >= SINCE_YEAR; i--) {
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
		return categoryService.findAll(lang);
	}
	
	@ModelAttribute("countries")
	public Iterable<SelectEntityDto> getCountries(Locale userLocale) {
		String lang = LocaleUtils.getLanguageOrNull(userLocale);
		return countryService.findAll(lang);
	}
	
	@RequestMapping(Url.ADD_SERIES_PAGE)
	public AddSeriesForm showForm() {
		
		AddSeriesForm addSeriesForm = new AddSeriesForm();
		addSeriesForm.setPerforated(true);
		
		return addSeriesForm;
	}
	
	@RequestMapping(value = Url.ADD_SERIES_PAGE, method = RequestMethod.POST)
	public String processInput(
		@Validated({
			Default.class, MichelCatalogChecks.class, ScottCatalogChecks.class,
			YvertCatalogChecks.class, GibbonsCatalogChecks.class, AddSeriesForm.ImageChecks.class
		}) AddSeriesForm form,
		BindingResult result,
		HttpServletRequest request,
		User currentUser) {
		
		if (result.hasErrors()) {
			// don't try to re-display file upload field
			form.setImage(null);
			return null;
		}
		
		boolean userCanAddComments =
			SecurityContextUtils.hasAuthority(request, "ADD_COMMENTS_TO_SERIES");
		Integer seriesId = seriesService.add(form, currentUser, userCanAddComments);
		
		return redirectTo(Url.INFO_SERIES_PAGE, seriesId);
	}
	
	@RequestMapping(Url.INFO_SERIES_PAGE)
	public String showInfo(@PathVariable("id") Series series, Model model, User currentUser) {
		
		if (series == null) {
			throw new NotFoundException();
		}
		
		model.addAttribute("addImageForm", new AddImageForm());
		model.addAttribute("series", series);
		model.addAttribute("michelNumbers", CatalogUtils.toShortForm(series.getMichel()));
		model.addAttribute("scottNumbers", CatalogUtils.toShortForm(series.getScott()));
		model.addAttribute("yvertNumbers", CatalogUtils.toShortForm(series.getYvert()));
		model.addAttribute("gibbonsNumbers", CatalogUtils.toShortForm(series.getGibbons()));
		
		model.addAttribute(
			"isSeriesInCollection",
			collectionService.isSeriesInCollection(currentUser, series)
		);
		
		return "series/info";
	}
	
	@RequestMapping(value = Url.ADD_IMAGE_SERIES_PAGE, method = RequestMethod.POST)
	public String processImage(
			@Validated({ Default.class, AddImageForm.ImageChecks.class }) AddImageForm form,
			BindingResult result,
			@PathVariable("id") Series series,
			Model model,
			User currentUser) {
		
		model.addAttribute("series", series);
		model.addAttribute("michelNumbers", CatalogUtils.toShortForm(series.getMichel()));
		model.addAttribute("scottNumbers", CatalogUtils.toShortForm(series.getScott()));
		model.addAttribute("yvertNumbers", CatalogUtils.toShortForm(series.getYvert()));
		model.addAttribute("gibbonsNumbers", CatalogUtils.toShortForm(series.getGibbons()));
		
		model.addAttribute(
			"isSeriesInCollection",
			collectionService.isSeriesInCollection(currentUser, series)
		);
		
		if (result.hasErrors()) {
			// don't try to re-display file upload field
			form.setImage(null);
			return "series/info";
		}
		
		if (series == null) {
			throw new NotFoundException();
		}
		
		seriesService.addImageToSeries(form, series, currentUser);
		
		return redirectTo(Url.INFO_SERIES_PAGE, series.getId());
	}
	
	@RequestMapping(
		value = Url.INFO_SERIES_PAGE,
		method = RequestMethod.POST,
		params = "action=ADD"
	)
	public String addToCollection(
		@PathVariable("id") Series series,
		User currentUser,
		RedirectAttributes redirectAttributes) {
		
		if (series == null) {
			throw new NotFoundException();
		}
		
		Collection collection = collectionService.addToCollection(currentUser, series);
		
		redirectAttributes.addFlashAttribute("justAddedSeries", true);
		redirectAttributes.addFlashAttribute("justAddedSeriesId", series.getId());
		
		return redirectTo(Url.INFO_COLLECTION_PAGE, collection.getId(), collection.getSlug());
	}
	
	@RequestMapping(
		value = Url.INFO_SERIES_PAGE,
		method = RequestMethod.POST,
		params = "action=REMOVE"
	)
	public String removeFromCollection(
		@PathVariable("id") Series series,
		User currentUser,
		RedirectAttributes redirectAttributes) {
		
		if (series == null) {
			throw new NotFoundException();
		}
		
		Collection collection = collectionService.removeFromCollection(currentUser, series);
		
		redirectAttributes.addFlashAttribute("justRemovedSeries", true);
		
		return redirectTo(Url.INFO_COLLECTION_PAGE, collection.getId(), collection.getSlug());
	}
	
	@RequestMapping(Url.FIND_SERIES_BY_MICHEL)
	public String findSeriesByMichel(@PathVariable("num") String michelNumberCode) {
		if (michelNumberCode == null) {
			throw new NotFoundException();
		}
		
		Optional<Integer> seriesId = seriesService.findSeriesIdByMichelNumber(michelNumberCode);
		if (!seriesId.isPresent()) {
			throw new NotFoundException();
		}
		
		return redirectTo(Url.INFO_SERIES_PAGE, seriesId.get());
	}
	
	@RequestMapping(Url.FIND_SERIES_BY_SCOTT)
	public String findSeriesByScott(@PathVariable("num") String scottNumberCode) {
		if (scottNumberCode == null) {
			throw new NotFoundException();
		}
		
		Optional<Integer> seriesId = seriesService.findSeriesIdByScottNumber(scottNumberCode);
		if (!seriesId.isPresent()) {
			throw new NotFoundException();
		}
		
		return redirectTo(Url.INFO_SERIES_PAGE, seriesId.get());
	}
	
	@RequestMapping(Url.FIND_SERIES_BY_YVERT)
	public String findSeriesByYvert(@PathVariable("num") String yvertNumberCode) {
		if (yvertNumberCode == null) {
			throw new NotFoundException();
		}
		
		Optional<Integer> seriesId = seriesService.findSeriesIdByYvertNumber(yvertNumberCode);
		if (!seriesId.isPresent()) {
			throw new NotFoundException();
		}
		
		return redirectTo(Url.INFO_SERIES_PAGE, seriesId.get());
	}
	
	@RequestMapping(Url.FIND_SERIES_BY_GIBBONS)
	public String findSeriesByGibbons(@PathVariable("num") String gibbonsNumberCode) {
		if (gibbonsNumberCode == null) {
			throw new NotFoundException();
		}
		
		Optional<Integer> seriesId = seriesService.findSeriesIdByGibbonsNumber(gibbonsNumberCode);
		if (!seriesId.isPresent()) {
			throw new NotFoundException();
		}
		
		return redirectTo(Url.INFO_SERIES_PAGE, seriesId.get());
	}
	
	private static String redirectTo(String url, Object... args) {
		String dstUrl = UriComponentsBuilder.fromUriString(url)
			.buildAndExpand(args)
			.toString();
		
		return "redirect:" + dstUrl;
	}
	
}

