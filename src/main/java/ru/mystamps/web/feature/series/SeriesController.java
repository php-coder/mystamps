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
package ru.mystamps.web.feature.series;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.mystamps.web.common.EntityWithParentDto;
import ru.mystamps.web.common.LinkEntityDto;
import ru.mystamps.web.common.LocaleUtils;
import ru.mystamps.web.feature.category.Category;
import ru.mystamps.web.feature.category.CategoryService;
import ru.mystamps.web.feature.collection.AddToCollectionForm;
import ru.mystamps.web.feature.collection.CollectionService;
import ru.mystamps.web.feature.collection.CollectionUrl;
import ru.mystamps.web.feature.country.Country;
import ru.mystamps.web.feature.country.CountryService;
import ru.mystamps.web.feature.participant.ParticipantService;
import ru.mystamps.web.feature.series.importing.ImportRequestInfo;
import ru.mystamps.web.feature.series.importing.SeriesImportService;
import ru.mystamps.web.feature.series.sale.AddSeriesSalesForm;
import ru.mystamps.web.feature.series.sale.SeriesSalesService;
import ru.mystamps.web.feature.site.SiteUrl;
import ru.mystamps.web.support.spring.security.Authority;
import ru.mystamps.web.support.spring.security.CurrentUser;
import ru.mystamps.web.support.spring.security.CustomUserDetails;
import ru.mystamps.web.support.spring.security.SecurityContextUtils;
import ru.mystamps.web.support.thymeleaf.GroupByParent;
import ru.mystamps.web.support.togglz.Features;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.groups.Default;
import java.io.IOException;
import java.time.Year;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.mystamps.web.common.ControllerUtils.redirectTo;

@Controller
@RequiredArgsConstructor
@SuppressWarnings({ "PMD.AvoidDuplicateLiterals", "PMD.TooManyMethods", "PMD.GodClass" })
public class SeriesController {

	private static final Integer CURRENT_YEAR;
	private static final Map<Integer, Integer> YEARS;
	
	private final CategoryService categoryService;
	private final CollectionService collectionService;
	private final CountryService countryService;
	private final SeriesService seriesService;
	private final SeriesImportService seriesImportService;
	private final SeriesSalesService seriesSalesService;
	private final ParticipantService participantService;
	
	static {
		CURRENT_YEAR = Integer.valueOf(Year.now(ZoneOffset.UTC).getValue());
		YEARS = new LinkedHashMap<>();
		for (Integer i = CURRENT_YEAR; i >= SeriesValidation.MIN_RELEASE_YEAR; i--) {
			YEARS.put(i, i);
		}
	}
	
	@InitBinder("addSeriesForm")
	protected void initSeriesFormBinder(WebDataBinder binder) {
		StringTrimmerEditor editor = new StringTrimmerEditor(" ", true);
		binder.registerCustomEditor(String.class, "michelNumbers", editor);
		binder.registerCustomEditor(String.class, "scottNumbers", editor);
		binder.registerCustomEditor(String.class, "yvertNumbers", editor);
		binder.registerCustomEditor(String.class, "gibbonsNumbers", editor);
		binder.registerCustomEditor(String.class, "solovyovNumbers", editor);
		binder.registerCustomEditor(String.class, "zagorskiNumbers", editor);
		binder.registerCustomEditor(String.class, "comment", new StringTrimmerEditor(true));
	}
	
	@InitBinder("addSeriesSalesForm")
	protected void initSeriesSalesFormBinder(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, "url", new StringTrimmerEditor(true));
	}
	
	@GetMapping(SeriesUrl.ADD_SERIES_PAGE)
	public void showForm(
		@Category @RequestParam(name = "category", required = false) LinkEntityDto category,
		@Country @RequestParam(name = "country", required = false) LinkEntityDto country,
		Model model,
		Locale userLocale) {
		
		String lang = LocaleUtils.getLanguageOrNull(userLocale);
		
		addCategoriesToModel(model, lang);
		addCountriesToModel(model, lang);
		addYearToModel(model);
		
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
	
	@PostMapping(path = SeriesUrl.ADD_SERIES_PAGE, params = "imageUrl")
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
	
	@PostMapping(path = SeriesUrl.ADD_SERIES_PAGE, params = "!imageUrl")
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
			
			addCategoriesToModel(model, lang);
			addCountriesToModel(model, lang);
			addYearToModel(model);
			
			// don't try to re-display file upload field
			form.setUploadedImage(null);
			form.setDownloadedImage(null);
			return null;
		}
		
		boolean userCanAddComments = SecurityContextUtils.hasAuthority(
			Authority.ADD_COMMENTS_TO_SERIES
		);
		Integer seriesId = seriesService.add(form, currentUserId, userCanAddComments);
		
		return redirectTo(SeriesUrl.INFO_SERIES_PAGE, seriesId);
	}
	
	@GetMapping(SeriesUrl.INFO_SERIES_PAGE)
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
		
		Map<String, ?> commonAttrs = prepareCommonAttrsForSeriesInfo(series, currentUserId, lang);
		model.addAllAttributes(commonAttrs);
		
		addSeriesSalesFormToModel(model);
		addImageFormToModel(model);
		addStampsToCollectionForm(model, series);
		
		model.addAttribute("maxQuantityOfImagesExceeded", false);
		
		return "series/info";
	}
	
	@GetMapping(SeriesUrl.INFO_COUNTRY_PAGE)
	public String showInfoByCountrySlug(
		@Country @PathVariable("slug") LinkEntityDto country,
		Model model,
		Locale userLocale,
		HttpServletResponse response)
		throws IOException {
		
		if (country == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		
		String slug = country.getSlug();
		String name = country.getName();
		
		String lang = LocaleUtils.getLanguageOrNull(userLocale);
		List<SeriesInfoDto> series = seriesService.findByCountrySlug(slug, lang);
		
		model.addAttribute("countrySlug", slug);
		model.addAttribute("countryName", name);
		model.addAttribute("seriesOfCountry", series);
		
		return "country/info";
	}
	
	@GetMapping(SeriesUrl.INFO_CATEGORY_PAGE)
	public String showInfoBySlug(
		@Category @PathVariable("slug") LinkEntityDto category,
		Model model,
		Locale userLocale,
		HttpServletResponse response)
		throws IOException {
		
		if (category == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		
		String slug = category.getSlug();
		String name = category.getName();
		
		String lang = LocaleUtils.getLanguageOrNull(userLocale);
		List<SeriesInfoDto> series = seriesService.findByCategorySlug(slug, lang);
		
		model.addAttribute("categorySlug", slug);
		model.addAttribute("categoryName", name);
		model.addAttribute("seriesOfCategory", series);
		
		return "category/info";
	}
	
	@SuppressWarnings("checkstyle:parameternumber")
	@PostMapping(path = SeriesUrl.ADD_IMAGE_SERIES_PAGE, params = "imageUrl")
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
	@PostMapping(path = SeriesUrl.ADD_IMAGE_SERIES_PAGE, params = "!imageUrl")
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
			Map<String, ?> commonAttrs =
				prepareCommonAttrsForSeriesInfo(series, currentUserId, lang);
			model.addAllAttributes(commonAttrs);
			
			addSeriesSalesFormToModel(model);
			addStampsToCollectionForm(model, series);
			
			// don't try to re-display file upload field
			form.setUploadedImage(null);
			
			return "series/info";
		}
		
		seriesService.addImageToSeries(form, series.getId(), currentUserId);
		
		return redirectTo(SeriesUrl.INFO_SERIES_PAGE, series.getId());
	}
	
	// many method parameters are OK here
	@SuppressWarnings("checkstyle:parameternumber")
	@PostMapping(path = SeriesUrl.INFO_SERIES_PAGE, params = "action=ADD")
	public String addToCollection(
		@Valid AddToCollectionForm form,
		BindingResult result,
		@PathVariable("id") Integer seriesId,
		@AuthenticationPrincipal CustomUserDetails currentUserDetails,
		Locale userLocale,
		RedirectAttributes redirectAttributes,
		HttpServletResponse response,
		Model model)
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
		
		if (form.getSeriesId() == null || !form.getSeriesId().equals(seriesId)) {
			// series id in the URL doesn't match id from a hidden field:
			// looks like user has faked a hidden field to bypass the validation
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return null;
		}
		
		Integer userId = currentUserDetails.getUserId();
		
		if (result.hasErrors()) {
			String lang = LocaleUtils.getLanguageOrNull(userLocale);
			SeriesDto series = seriesService.findFullInfoById(seriesId, lang);
			if (series == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return null;
			}
			
			Map<String, ?> commonAttrs = prepareCommonAttrsForSeriesInfo(series, userId, lang);
			model.addAllAttributes(commonAttrs);
			
			addSeriesSalesFormToModel(model);
			addImageFormToModel(model);
			addStampsToCollectionForm(model, series);
			
			return "series/info";
		}
		
		collectionService.addToCollection(userId, form);
		
		redirectAttributes.addFlashAttribute("justAddedSeries", true);
		redirectAttributes.addFlashAttribute("justAddedSeriesId", seriesId);

		String collectionSlug = currentUserDetails.getUserCollectionSlug();
		return redirectTo(CollectionUrl.INFO_COLLECTION_PAGE, collectionSlug);
	}
	
	@PostMapping(path = SeriesUrl.INFO_SERIES_PAGE, params = "action=REMOVE")
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
		return redirectTo(CollectionUrl.INFO_COLLECTION_PAGE, collectionSlug);
	}
	
	@PostMapping(SeriesUrl.ADD_SERIES_ASK_PAGE)
	public String processAskForm(
		@Validated({ Default.class, AddSeriesSalesForm.UrlChecks.class }) AddSeriesSalesForm form,
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
			Map<String, ?> commonAttrs =
				prepareCommonAttrsForSeriesInfo(series, currentUserId, lang);
			model.addAllAttributes(commonAttrs);
			
			addSeriesSalesFormToModel(model);
			addImageFormToModel(model);
			addStampsToCollectionForm(model, series);
			
			return "series/info";
		}
		
		seriesSalesService.add(form, series.getId(), currentUserId);
		
		return redirectTo(SeriesUrl.INFO_SERIES_PAGE, series.getId());
	}
	
	@GetMapping(SeriesUrl.SEARCH_SERIES_BY_CATALOG)
	public String searchSeriesByCatalog(
		@RequestParam(name = "catalogNumber", defaultValue = "") String catalogNumber,
		@RequestParam(name = "catalogName", defaultValue = "") String catalogName,
		@RequestParam(name = "inCollection", defaultValue = "false") Boolean inCollection,
		@CurrentUser Integer currentUserId,
		Model model,
		Locale userLocale,
		RedirectAttributes redirectAttributes) {
		
		if (StringUtils.isBlank(catalogNumber)) {
			redirectAttributes.addFlashAttribute("numberIsEmpty", true);
			return "redirect:" + SiteUrl.INDEX_PAGE;
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
			case "solovyov":
				series = seriesService.findBySolovyovNumber(catalogNumber, lang);
				break;
			case "zagorski":
				series = seriesService.findByZagorskiNumber(catalogNumber, lang);
				break;
			default:
				series = Collections.emptyList();
				break;
		}
		
		// @todo #1098 Optimize a search within user's collection
		if (Features.SEARCH_IN_COLLECTION.isActive()
			&& inCollection
			&& currentUserId != null) {
			series = series
					.stream()
					.filter(
						e -> collectionService.isSeriesInCollection(currentUserId, e.getId())
					)
					.collect(Collectors.toList());
		}
		
		model.addAttribute("searchResults", series);
		
		return "series/search_result";
	}
	
	// "public" in order to be accessible from SeriesImportController
	public void addCategoriesToModel(Model model, String lang) {
		List<EntityWithParentDto> categories = categoryService.findCategoriesWithParents(lang);
		
		List<SelectItem> groupedCategories = GroupByParent.transformEntities(categories);
		
		model.addAttribute("categories", groupedCategories);
	}
	
	// "public" in order to be accessible from SeriesImportController
	public void addCountriesToModel(Model model, String lang) {
		List<LinkEntityDto> countries = countryService.findAllAsLinkEntities(lang);
		model.addAttribute("countries", countries);
	}
	
	// "public" in order to be accessible from SeriesImportController
	public void addYearToModel(Model model) {
		model.addAttribute("years", YEARS);
	}
	
	// "public" in order to be accessible from SeriesImportController
	public void addSellersToModel(Model model) {
		List<EntityWithParentDto> sellers = participantService.findSellersWithParents();
		List<SelectItem> groupedSellers = GroupByParent.transformEntities(sellers);
		model.addAttribute("sellers", groupedSellers);
	}
	
	// "public" in order to be accessible from SeriesImportController
	public static void loadErrorsFromDownloadInterceptor(
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
						DownloadImageInterceptor.UPLOADED_IMAGE_FIELD_NAME,
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
	
	private Map<String, ?> prepareCommonAttrsForSeriesInfo(
		SeriesDto series,
		Integer currentUserId,
		String lang) {
		
		Map<String, Object> model = new HashMap<>();
		Integer seriesId = series.getId();
		
		model.put("series", series);
		
		List<SeriesLinkDto> similarSeries = seriesService.findSimilarSeries(seriesId, lang);
		model.put("similarSeries", similarSeries);
		
		String michelNumbers   = CatalogUtils.toShortForm(series.getMichel().getNumbers());
		String scottNumbers    = CatalogUtils.toShortForm(series.getScott().getNumbers());
		String yvertNumbers    = CatalogUtils.toShortForm(series.getYvert().getNumbers());
		String gibbonsNumbers  = CatalogUtils.toShortForm(series.getGibbons().getNumbers());
		String solovyovNumbers = CatalogUtils.toShortForm(series.getSolovyov().getNumbers());
		String zagorskiNumbers = CatalogUtils.toShortForm(series.getZagorski().getNumbers());
		model.put("michelNumbers", michelNumbers);
		model.put("scottNumbers", scottNumbers);
		model.put("yvertNumbers", yvertNumbers);
		model.put("gibbonsNumbers", gibbonsNumbers);
		model.put("solovyovNumbers", solovyovNumbers);
		model.put("zagorskiNumbers", zagorskiNumbers);
		
		boolean isSeriesInCollection =
			collectionService.isSeriesInCollection(currentUserId, seriesId);
		
		boolean userCanAddImagesToSeries =
			isUserCanAddImagesToSeries(series);

		model.put("isSeriesInCollection", isSeriesInCollection);
		model.put("allowAddingImages", userCanAddImagesToSeries);
		
		if (SecurityContextUtils.hasAuthority(Authority.VIEW_SERIES_SALES)) {
			
			List<PurchaseAndSaleDto> purchasesAndSales =
				seriesService.findPurchasesAndSales(seriesId);
			model.put("purchasesAndSales", purchasesAndSales);
		}
		
		if (SecurityContextUtils.hasAuthority(Authority.IMPORT_SERIES)) {
			ImportRequestInfo importInfo = seriesImportService.findRequestInfo(seriesId);
			model.put("importInfo", importInfo);
		}
		
		return model;
	}
	
	private void addSeriesSalesFormToModel(Model model) {
		if (!SecurityContextUtils.hasAuthority(Authority.ADD_SERIES_SALES)) {
			return;
		}
		
		if (!model.containsAttribute("addSeriesSalesForm")) {
			AddSeriesSalesForm form = new AddSeriesSalesForm();
			model.addAttribute("addSeriesSalesForm", form);
		}
		
		addSellersToModel(model);
		
		List<EntityWithParentDto> buyers = participantService.findBuyersWithParents();
		List<SelectItem> groupedBuyers = GroupByParent.transformEntities(buyers);
		model.addAttribute("buyers", groupedBuyers);
	}
	
	private static void addImageFormToModel(Model model) {
		AddImageForm form = new AddImageForm();
		model.addAttribute("addImageForm", form);
	}
	
	private static void addStampsToCollectionForm(Model model, SeriesDto series) {
		// FIXME: only add when isSeriesInCollection
		if (model.containsAttribute("addToCollectionForm")) {
			return;
		}
		
		AddToCollectionForm form = new AddToCollectionForm();
		form.setNumberOfStamps(series.getQuantity());
		model.addAttribute("addToCollectionForm", form);
	}
	
	private static boolean isAllowedToAddingImages(SeriesDto series) {
		return series.getImageIds().size() <= series.getQuantity();
	}
	
	// I like these parentheses and also ErrorProne suggests to have an explicit order
	@SuppressWarnings("PMD.UselessParentheses")
	private static boolean isUserCanAddImagesToSeries(SeriesDto series) {
		return isAdmin()
			|| (isOwner(series) && isAllowedToAddingImages(series));
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

