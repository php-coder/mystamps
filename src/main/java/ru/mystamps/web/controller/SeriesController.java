/*
 * Copyright (C) 2009-2017 Slava Semushin <slava.semushin@gmail.com>
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
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.groups.Default;

import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.Url;
import ru.mystamps.web.controller.converter.annotation.Category;
import ru.mystamps.web.controller.converter.annotation.Country;
import ru.mystamps.web.controller.converter.annotation.CurrentUser;
import ru.mystamps.web.controller.dto.AddImageForm;
import ru.mystamps.web.controller.dto.AddSeriesForm;
import ru.mystamps.web.controller.dto.AddSeriesSalesForm;
import ru.mystamps.web.controller.dto.NullableImageUrl;
import ru.mystamps.web.controller.interceptor.DownloadImageInterceptor;
import ru.mystamps.web.dao.dto.EntityWithIdDto;
import ru.mystamps.web.dao.dto.LinkEntityDto;
import ru.mystamps.web.dao.dto.PurchaseAndSaleDto;
import ru.mystamps.web.dao.dto.SeriesInfoDto;
import ru.mystamps.web.service.CategoryService;
import ru.mystamps.web.service.CollectionService;
import ru.mystamps.web.service.CountryService;
import ru.mystamps.web.service.SeriesSalesService;
import ru.mystamps.web.service.SeriesService;
import ru.mystamps.web.service.TransactionParticipantService;
import ru.mystamps.web.service.dto.DownloadResult;
import ru.mystamps.web.service.dto.FirstLevelCategoryDto;
import ru.mystamps.web.service.dto.SeriesDto;
import ru.mystamps.web.support.spring.security.Authority;
import ru.mystamps.web.support.spring.security.CustomUserDetails;
import ru.mystamps.web.support.spring.security.SecurityContextUtils;
import ru.mystamps.web.support.togglz.Features;
import ru.mystamps.web.util.CatalogUtils;
import ru.mystamps.web.util.LocaleUtils;

import static ru.mystamps.web.controller.ControllerUtils.redirectTo;
import static ru.mystamps.web.validation.ValidationRules.MIN_RELEASE_YEAR;

@Controller
@RequiredArgsConstructor
@SuppressWarnings({ "PMD.AvoidDuplicateLiterals", "PMD.TooManyMethods", "PMD.GodClass" })
public class SeriesController {
	
	private static final Integer CURRENT_YEAR   = new GregorianCalendar().get(Calendar.YEAR);
	
	private static final Map<Integer, Integer> YEARS;
	
	private final CategoryService categoryService;
	private final CollectionService collectionService;
	private final CountryService countryService;
	private final SeriesService seriesService;
	private final SeriesSalesService seriesSalesService;
	private final TransactionParticipantService transactionParticipantService;
	
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
	
	@GetMapping(Url.ADD_SERIES_PAGE)
	public void showForm(
		@Category @RequestParam(name = "category", required = false) LinkEntityDto category,
		@Country @RequestParam(name = "country", required = false) LinkEntityDto country,
		Model model,
		Locale userLocale) {
		
		String lang = LocaleUtils.getLanguageOrNull(userLocale);
		
		List<FirstLevelCategoryDto> categories = categoryService.findFirstLevelCategories(lang);
		model.addAttribute("categories", categories);
		
		List<LinkEntityDto> countries = countryService.findAllAsLinkEntities(lang);
		model.addAttribute("countries", countries);
		
		model.addAttribute("years", YEARS);
		
		AddSeriesForm addSeriesForm = new AddSeriesForm();
		addSeriesForm.setPerforated(true);
		
		if (category != null) {
			addSeriesForm.setCategory(category);
		}
		
		if (country != null) {
			addSeriesForm.setCountry(country);
		}
		
		model.addAttribute("addSeriesForm", addSeriesForm);
	}
	
	@GetMapping(Url.ADD_SERIES_WITH_CATEGORY_PAGE)
	public View showFormWithCategory(
		@PathVariable("slug") String category,
		RedirectAttributes redirectAttributes) {
		
		redirectAttributes.addAttribute("category", category);
		
		RedirectView view = new RedirectView();
		view.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
		view.setUrl(Url.ADD_SERIES_PAGE);
		
		return view;
	}
	
	@GetMapping(Url.ADD_SERIES_WITH_COUNTRY_PAGE)
	public View showFormWithCountry(
		@PathVariable("slug") String country,
		RedirectAttributes redirectAttributes) {
		
		redirectAttributes.addAttribute("country", country);
		
		RedirectView view = new RedirectView();
		view.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
		view.setUrl(Url.ADD_SERIES_PAGE);
		
		return view;
	}
	
	@PostMapping(path = Url.ADD_SERIES_PAGE, params = "imageUrl")
	public String processInputWithImageUrl(
		@Validated({ Default.class,
			AddSeriesForm.ImageUrlChecks.class,
			AddSeriesForm.ReleaseDateChecks.class,
			AddSeriesForm.ImageChecks.class }) AddSeriesForm form,
		BindingResult result,
		@CurrentUser Integer currentUserId,
		Locale userLocale,
		Model model,
		HttpServletRequest request) {
		
		return processInput(form, result, currentUserId, userLocale, model, request);
	}
	
	@PostMapping(path = Url.ADD_SERIES_PAGE, params = "!imageUrl")
	public String processInput(
		@Validated({ Default.class,
			AddSeriesForm.RequireImageCheck.class,
			AddSeriesForm.ReleaseDateChecks.class,
			AddSeriesForm.ImageChecks.class }) AddSeriesForm form,
		BindingResult result,
		@CurrentUser Integer currentUserId,
		Locale userLocale,
		Model model,
		HttpServletRequest request) {

		loadErrorsFromDownloadInterceptor(form, result, request);
		
		if (result.hasErrors()) {
			String lang = LocaleUtils.getLanguageOrNull(userLocale);
			
			List<FirstLevelCategoryDto> categories = categoryService.findFirstLevelCategories(lang);
			model.addAttribute("categories", categories);
			
			List<LinkEntityDto> countries = countryService.findAllAsLinkEntities(lang);
			model.addAttribute("countries", countries);
			
			model.addAttribute("years", YEARS);
			
			// don't try to re-display file upload field
			form.setImage(null);
			form.setDownloadedImage(null);
			return null;
		}
		
		boolean userCanAddComments = SecurityContextUtils.hasAuthority(
			Authority.ADD_COMMENTS_TO_SERIES
		);
		Integer seriesId = seriesService.add(form, currentUserId, userCanAddComments);
		
		return redirectTo(Url.INFO_SERIES_PAGE, seriesId);
	}
	
	@GetMapping(Url.INFO_SERIES_PAGE)
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
		
		Map<String, ?> commonAttrs = prepareCommonAttrsForSeriesInfo(series, currentUserId);
		model.addAllAttributes(commonAttrs);
		
		addSeriesSalesFormToModel(model);
		addImageFormToModel(model);
		
		model.addAttribute("maxQuantityOfImagesExceeded", false);
		
		return "series/info";
	}
	
	@SuppressWarnings("checkstyle:parameternumber")
	@PostMapping(path = Url.ADD_IMAGE_SERIES_PAGE, params = "imageUrl")
	public String processImageWithImageUrl(
		@Validated({
			AddImageForm.ImageUrlChecks.class,
			AddImageForm.ImageChecks.class
		}) AddImageForm form,
		BindingResult result,
		@PathVariable("id") Integer seriesId,
		Model model,
		@CurrentUser Integer currentUserId,
		Locale userLocale,
		HttpServletRequest request,
		HttpServletResponse response)
		throws IOException {
		
		return processImage(
			form,
			result,
			seriesId,
			model,
			currentUserId,
			userLocale,
			request,
			response
		);
	}
	
	@SuppressWarnings("checkstyle:parameternumber")
	@PostMapping(path = Url.ADD_IMAGE_SERIES_PAGE, params = "!imageUrl")
	public String processImage(
		@Validated({
			AddImageForm.RequireImageCheck.class,
			AddImageForm.ImageChecks.class })
		AddImageForm form,
		BindingResult result,
		@PathVariable("id") Integer seriesId,
		Model model,
		@CurrentUser Integer currentUserId,
		Locale userLocale,
		HttpServletRequest request,
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
		
		loadErrorsFromDownloadInterceptor(form, result, request);
		
		boolean maxQuantityOfImagesExceeded = !isAdmin() && !isAllowedToAddingImages(series);
		model.addAttribute("maxQuantityOfImagesExceeded", maxQuantityOfImagesExceeded);
		
		if (result.hasErrors() || maxQuantityOfImagesExceeded) {
			Map<String, ?> commonAttrs = prepareCommonAttrsForSeriesInfo(series, currentUserId);
			model.addAllAttributes(commonAttrs);
			
			addSeriesSalesFormToModel(model);
			
			// don't try to re-display file upload field
			form.setImage(null);
			
			return "series/info";
		}
		
		seriesService.addImageToSeries(form, series.getId(), currentUserId);
		
		return redirectTo(Url.INFO_SERIES_PAGE, series.getId());
	}
	
	@PostMapping(path = Url.INFO_SERIES_PAGE, params = "action=ADD")
	public String addToCollection(
		@PathVariable("id") Integer seriesId,
		@AuthenticationPrincipal CustomUserDetails currentUserDetails,
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
		
		Integer userId = currentUserDetails.getUserId();
		collectionService.addToCollection(userId, seriesId);
		
		redirectAttributes.addFlashAttribute("justAddedSeries", true);
		redirectAttributes.addFlashAttribute("justAddedSeriesId", seriesId);

		String collectionSlug = currentUserDetails.getUserCollectionSlug();
		return redirectTo(Url.INFO_COLLECTION_PAGE, collectionSlug);
	}
	
	@PostMapping(path = Url.INFO_SERIES_PAGE, params = "action=REMOVE")
	public String removeFromCollection(
		@PathVariable("id") Integer seriesId,
		@AuthenticationPrincipal CustomUserDetails currentUserDetails,
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
		
		Integer userId = currentUserDetails.getUserId();
		collectionService.removeFromCollection(userId, seriesId);
		
		redirectAttributes.addFlashAttribute("justRemovedSeries", true);
		
		String collectionSlug = currentUserDetails.getUserCollectionSlug();
		return redirectTo(Url.INFO_COLLECTION_PAGE, collectionSlug);
	}
	
	@PostMapping(Url.ADD_SERIES_ASK_PAGE)
	public String processAskForm(
		@Valid AddSeriesSalesForm form,
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
		
		boolean maxQuantityOfImagesExceeded = !isAdmin() && !isAllowedToAddingImages(series);
		model.addAttribute("maxQuantityOfImagesExceeded", maxQuantityOfImagesExceeded);
		
		if (result.hasErrors() || maxQuantityOfImagesExceeded) {
			Map<String, ?> commonAttrs = prepareCommonAttrsForSeriesInfo(series, currentUserId);
			model.addAllAttributes(commonAttrs);
			
			addSeriesSalesFormToModel(model);
			addImageFormToModel(model);
			
			return "series/info";
		}
		
		seriesSalesService.add(form, series.getId(), currentUserId);
		
		return redirectTo(Url.INFO_SERIES_PAGE, series.getId());
	}
	
	@GetMapping(Url.SEARCH_SERIES_BY_CATALOG)
	public String searchSeriesByCatalog(
		@RequestParam(name = "catalogNumber", defaultValue = "") String catalogNumber,
		@RequestParam(name = "catalogName", defaultValue = "") String catalogName,
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
	
	// CheckStyle: ignore LineLength for next 1 line
	private Map<String, ?> prepareCommonAttrsForSeriesInfo(SeriesDto series, Integer currentUserId) {
		Map<String, Object> model = new HashMap<>();
		
		model.put("series", series);
		
		String michelNumbers  = CatalogUtils.toShortForm(series.getMichel().getNumbers());
		String scottNumbers   = CatalogUtils.toShortForm(series.getScott().getNumbers());
		String yvertNumbers   = CatalogUtils.toShortForm(series.getYvert().getNumbers());
		String gibbonsNumbers = CatalogUtils.toShortForm(series.getGibbons().getNumbers());
		model.put("michelNumbers", michelNumbers);
		model.put("scottNumbers", scottNumbers);
		model.put("yvertNumbers", yvertNumbers);
		model.put("gibbonsNumbers", gibbonsNumbers);
		
		boolean isSeriesInCollection =
			collectionService.isSeriesInCollection(currentUserId, series.getId());
		
		boolean userCanAddImagesToSeries =
			isUserCanAddImagesToSeries(series);

		model.put("isSeriesInCollection", isSeriesInCollection);
		model.put("allowAddingImages", userCanAddImagesToSeries);
		
		if (Features.SHOW_PURCHASES_AND_SALES.isActive()
			&& SecurityContextUtils.hasAuthority(Authority.VIEW_SERIES_SALES)) {
			
			List<PurchaseAndSaleDto> purchasesAndSales =
				seriesService.findPurchasesAndSales(series.getId());
			model.put("purchasesAndSales", purchasesAndSales);
		}
		
		return model;
	}
	
	private void addSeriesSalesFormToModel(Model model) {
		if (!(Features.ADD_PURCHASES_AND_SALES.isActive()
			&& SecurityContextUtils.hasAuthority(Authority.ADD_SERIES_SALES))) {
			return;
		}
		
		if (!model.containsAttribute("addSeriesSalesForm")) {
			AddSeriesSalesForm addSeriesSalesForm = new AddSeriesSalesForm();
			addSeriesSalesForm.setDate(new Date());
			model.addAttribute("addSeriesSalesForm", addSeriesSalesForm);
		}
		
		List<EntityWithIdDto> sellers = transactionParticipantService.findAllSellers();
		model.addAttribute("sellers", sellers);
		
		List<EntityWithIdDto> buyers = transactionParticipantService.findAllBuyers();
		model.addAttribute("buyers", buyers);
	}
	
	// false positive on Travis CI
	@SuppressWarnings("PMD.UnusedPrivateMethod")
	private static void loadErrorsFromDownloadInterceptor(
		NullableImageUrl form,
		BindingResult result,
		HttpServletRequest request) {
		
		Object downloadResultErrorCode =
			request.getAttribute(DownloadImageInterceptor.ERROR_CODE_ATTR_NAME);
		
		if (downloadResultErrorCode == null) {
			return;
		}
		
		if (downloadResultErrorCode instanceof DownloadResult.Code) {
			DownloadResult.Code code = (DownloadResult.Code)downloadResultErrorCode;
			switch (code) {
				case INVALID_URL:
					// Url is being validated by @URL, to avoid showing an error message
					// twice we're skipping error from an interceptor.
					break;
				case INSUFFICIENT_PERMISSIONS:
					// A user without permissions has tried to download a file. It means that he
					// didn't specify a file but somehow provide a URL to an image. In this case,
					// let's show an error message that file is required.
					result.rejectValue(
						"image",
						"ru.mystamps.web.support.beanvalidation.NotEmptyFilename.message"
					);
					form.nullifyImageUrl();
					break;
				default:
					result.rejectValue(
						DownloadImageInterceptor.DOWNLOADED_IMAGE_FIELD_NAME,
						DownloadResult.class.getName() + "." + code.toString(),
						"Could not download image"
					);
					break;
			}
		}
		
		request.removeAttribute(DownloadImageInterceptor.ERROR_CODE_ATTR_NAME);
	}
	
	private static void addImageFormToModel(Model model) {
		AddImageForm form = new AddImageForm();
		model.addAttribute("addImageForm", form);
	}
	
	private static boolean isAllowedToAddingImages(SeriesDto series) {
		return series.getImageIds().size() <= series.getQuantity();
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

