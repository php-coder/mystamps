/*
 * Copyright (C) 2009-2025 Slava Semushin <slava.semushin@gmail.com>
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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
import ru.mystamps.web.feature.series.sale.SeriesSaleDto;
import ru.mystamps.web.feature.series.sale.SeriesSalesService;
import ru.mystamps.web.feature.site.SiteUrl;
import ru.mystamps.web.support.spring.security.Authority;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.SPACE;
import static ru.mystamps.web.common.ControllerUtils.redirectTo;

@Controller
@RequiredArgsConstructor
public class SeriesController {

	private static final Logger LOG = LoggerFactory.getLogger(SeriesController.class);
	
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
		StringTrimmerEditor editor = new StringTrimmerEditor(SPACE, true);
		binder.registerCustomEditor(String.class, "michelNumbers", editor);
		binder.registerCustomEditor(String.class, "scottNumbers", editor);
		binder.registerCustomEditor(String.class, "yvertNumbers", editor);
		binder.registerCustomEditor(String.class, "gibbonsNumbers", editor);
		binder.registerCustomEditor(String.class, "solovyovNumbers", editor);
		binder.registerCustomEditor(String.class, "zagorskiNumbers", editor);
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
		@AuthenticationPrincipal CustomUserDetails currentUser,
		Locale userLocale,
		Model model,
		HttpServletRequest request) {
		
		return processInput(form, result, currentUser, userLocale, model, request);
	}
	
	@PostMapping(path = SeriesUrl.ADD_SERIES_PAGE, params = "!imageUrl")
	public String processInput(
		@Validated({ Default.class,
			AddSeriesForm.RequireImageCheck.class,
			AddSeriesForm.ReleaseDateChecks.class,
			AddSeriesForm.ImageChecks.class }) AddSeriesForm form,
		BindingResult result,
		@AuthenticationPrincipal CustomUserDetails currentUser,
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
		
		Integer seriesId = seriesService.add(form, currentUser.getUserId());
		
		return redirectTo(SeriesUrl.INFO_SERIES_PAGE, seriesId);
	}
	
	@GetMapping(SeriesUrl.INFO_SERIES_PAGE)
	public String showInfo(
		@PathVariable("id") Integer seriesId,
		Model model,
		@AuthenticationPrincipal CustomUserDetails currentUser,
		@CurrentSecurityContext(expression = "authentication") Authentication authentication,
		Locale userLocale,
		HttpServletResponse response)
		throws IOException {
		
		if (seriesId == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		
		String lang = LocaleUtils.getLanguageOrNull(userLocale);
		boolean userCanSeeHiddenImages = SecurityContextUtils.hasAuthority(
			authentication,
			Authority.VIEW_HIDDEN_IMAGES
		);
		Integer currentUserId = currentUser == null ? null : currentUser.getUserId();
		SeriesDto series = seriesService.findFullInfoById(
			seriesId,
			currentUserId,
			lang,
			userCanSeeHiddenImages
		);
		if (series == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		
		prepareCommonAttrsForSeriesInfo(model, series, currentUserId, authentication, lang);
		addSeriesSalesFormToModel(authentication, model);
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
		List<SeriesInGalleryDto> series = seriesService.findByCountrySlug(slug, lang);
		
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
	
	@PostMapping(path = SeriesUrl.ADD_IMAGE_SERIES_PAGE, params = { "replaceImage", "imageUrl" })
	public String replaceImageWithImageUrl(
		@Validated({
			AddImageForm.ImageUrlChecks.class,
			AddImageForm.RequireImageIdCheck.class,
			AddImageForm.ImageChecks.class
		}) AddImageForm form,
		BindingResult result,
		@PathVariable("id") Integer seriesId,
		Model model,
		@AuthenticationPrincipal CustomUserDetails currentUser,
		@CurrentSecurityContext(expression = "authentication") Authentication authentication,
		Locale userLocale,
		HttpServletRequest request,
		HttpServletResponse response)
		throws IOException {
		
		return processImage(
			form,
			result,
			seriesId,
			model,
			currentUser,
			authentication,
			userLocale,
			request,
			response
		);
	}
	
	@PostMapping(path = SeriesUrl.ADD_IMAGE_SERIES_PAGE, params = "imageUrl")
	public String processImageWithImageUrl(
		@Validated({
			AddImageForm.ImageUrlChecks.class,
			AddImageForm.ImageChecks.class
		}) AddImageForm form,
		BindingResult result,
		@PathVariable("id") Integer seriesId,
		Model model,
		@AuthenticationPrincipal CustomUserDetails currentUser,
		@CurrentSecurityContext(expression = "authentication") Authentication authentication,
		Locale userLocale,
		HttpServletRequest request,
		HttpServletResponse response)
		throws IOException {
		
		return processImage(
			form,
			result,
			seriesId,
			model,
			currentUser,
			authentication,
			userLocale,
			request,
			response
		);
	}
	
	@PostMapping(
		path = SeriesUrl.ADD_IMAGE_SERIES_PAGE,
		params = "!imageUrl",
		consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
	)
	public String processImage(
		@Validated({
			AddImageForm.RequireImageCheck.class,
			AddImageForm.ImageChecks.class })
		AddImageForm form,
		BindingResult result,
		@PathVariable("id") Integer seriesId,
		Model model,
		@AuthenticationPrincipal CustomUserDetails currentUser,
		@CurrentSecurityContext(expression = "authentication") Authentication authentication,
		Locale userLocale,
		HttpServletRequest request,
		HttpServletResponse response)
		throws IOException {
		
		if (seriesId == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		
		String lang = LocaleUtils.getLanguageOrNull(userLocale);
		boolean userCanSeeHiddenImages = SecurityContextUtils.hasAuthority(
			authentication,
			Authority.VIEW_HIDDEN_IMAGES
		);
		Integer currentUserId = currentUser.getUserId();
		SeriesDto series = seriesService.findFullInfoById(
			seriesId,
			currentUserId,
			lang,
			userCanSeeHiddenImages
		);
		if (series == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		
		loadErrorsFromDownloadInterceptor(form, result, request);
		
		boolean maxQuantityOfImagesExceeded = !isAdmin(authentication)
			&& !isAllowedToAddingImages(series);
		model.addAttribute("maxQuantityOfImagesExceeded", maxQuantityOfImagesExceeded);
		
		if (result.hasErrors() || maxQuantityOfImagesExceeded) {
			prepareCommonAttrsForSeriesInfo(model, series, currentUserId, authentication, lang);
			addSeriesSalesFormToModel(authentication, model);
			addStampsToCollectionForm(model, series);
			
			// don't try to re-display file upload field
			form.setUploadedImage(null);
			
			return "series/info";
		}
		
		boolean replaceImage = request.getParameter("replaceImage") != null;
		if (replaceImage) {
			seriesService.replaceImage(form, series.getId(), currentUserId);
		} else {
			seriesService.addImageToSeries(form, series.getId(), currentUserId);
		}
		
		return redirectTo(SeriesUrl.INFO_SERIES_PAGE, series.getId());
	}
	
	@PostMapping(path = SeriesUrl.INFO_SERIES_PAGE, params = "action=ADD")
	public String addToCollection(
		@Valid AddToCollectionForm form,
		BindingResult result,
		@PathVariable("id") Integer seriesId,
		@AuthenticationPrincipal CustomUserDetails currentUserDetails,
		@CurrentSecurityContext(expression = "authentication") Authentication authentication,
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
		
		Integer currentUserId = currentUserDetails.getUserId();
		
		if (result.hasErrors()) {
			String lang = LocaleUtils.getLanguageOrNull(userLocale);
			boolean userCanSeeHiddenImages = SecurityContextUtils.hasAuthority(
				authentication,
				Authority.VIEW_HIDDEN_IMAGES
			);
			SeriesDto series = seriesService.findFullInfoById(
				seriesId,
				currentUserId,
				lang,
				userCanSeeHiddenImages
			);
			if (series == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return null;
			}
			
			prepareCommonAttrsForSeriesInfo(model, series, currentUserId, authentication, lang);
			addSeriesSalesFormToModel(authentication, model);
			addImageFormToModel(model);
			addStampsToCollectionForm(model, series);
			
			return "series/info";
		}
		
		collectionService.addToCollection(currentUserId, form);
		
		redirectAttributes.addFlashAttribute("justAddedSeries", true);
		redirectAttributes.addFlashAttribute("justAddedSeriesId", seriesId);
		redirectAttributes.addFlashAttribute("justAddedNumberOfStamps", form.getNumberOfStamps());

		String collectionSlug = currentUserDetails.getUserCollectionSlug();
		return redirectTo(CollectionUrl.INFO_COLLECTION_PAGE, collectionSlug);
	}
	
	@PostMapping(path = SeriesUrl.INFO_SERIES_PAGE, params = "action=REMOVE")
	public String removeFromCollection(
		@PathVariable("id") Integer seriesId,
		@RequestParam(name = "id", defaultValue = "0") Integer seriesInstanceId,
		@AuthenticationPrincipal CustomUserDetails currentUserDetails,
		RedirectAttributes redirectAttributes,
		HttpServletResponse response)
		throws IOException {
		
		if (seriesId == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		
		if (seriesInstanceId == 0) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return null;
		}
		
		boolean seriesExists = seriesService.isSeriesExist(seriesId);
		if (!seriesExists) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		
		Integer userId = currentUserDetails.getUserId();
		collectionService.removeFromCollection(userId, seriesId, seriesInstanceId);
		
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
		@AuthenticationPrincipal CustomUserDetails currentUser,
		@CurrentSecurityContext(expression = "authentication") Authentication authentication,
		Locale userLocale,
		HttpServletResponse response)
		throws IOException {
		
		if (seriesId == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		
		String lang = LocaleUtils.getLanguageOrNull(userLocale);
		boolean userCanSeeHiddenImages = SecurityContextUtils.hasAuthority(
			authentication,
			Authority.VIEW_HIDDEN_IMAGES
		);
		Integer currentUserId = currentUser.getUserId();
		SeriesDto series = seriesService.findFullInfoById(
			seriesId,
			currentUserId,
			lang,
			userCanSeeHiddenImages
		);
		if (series == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		
		boolean maxQuantityOfImagesExceeded = !isAdmin(authentication)
			&& !isAllowedToAddingImages(series);
		model.addAttribute("maxQuantityOfImagesExceeded", maxQuantityOfImagesExceeded);
		
		if (result.hasErrors() || maxQuantityOfImagesExceeded) {
			prepareCommonAttrsForSeriesInfo(model, series, currentUserId, authentication, lang);
			addSeriesSalesFormToModel(authentication, model);
			addImageFormToModel(model);
			addStampsToCollectionForm(model, series);
			
			return "series/info";
		}
		
		seriesSalesService.add(form, series.getId(), currentUserId);
		
		return redirectTo(SeriesUrl.INFO_SERIES_PAGE, series.getId());
	}
	
	@GetMapping(SeriesUrl.SEARCH_SERIES_BY_CATALOG)
	public String searchSeriesByCatalog(
		@RequestParam(name = "catalogNumber", defaultValue = EMPTY) String catalogNumber,
		@RequestParam(name = "catalogName", defaultValue = EMPTY) String catalogName,
		@RequestParam(name = "inCollection", defaultValue = "false") Boolean inCollection,
		@AuthenticationPrincipal CustomUserDetails currentUser,
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
		Integer currentUserId = currentUser == null ? null : currentUser.getUserId();
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
	
	// @todo #1280 Mark similar series: gracefully handle error when value mismatches to type
	@PostMapping(SeriesUrl.MARK_SIMILAR_SERIES)
	@ResponseBody
	public ResponseEntity<Void> markSimilarSeries(
		@RequestBody @Valid AddSimilarSeriesForm form) {
		
		try {
			seriesService.markAsSimilar(form);
			return ResponseEntity.noContent().build();

		} catch (RuntimeException ex) {
			LOG.error(
				"Couldn't mark series #{} similar to {}: {}",
				form.getSeriesId(),
				form.getSimilarSeriesIds(),
				ex.getMessage()
			);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
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
	
	private void prepareCommonAttrsForSeriesInfo(
		Model model,
		SeriesDto series,
		Integer currentUserId,
		Authentication authentication,
		String lang) {
		
		Integer seriesId = series.getId();
		
		model.addAttribute("series", series);
		
		List<SeriesLinkDto> similarSeries = seriesService.findSimilarSeries(seriesId, lang);
		model.addAttribute("similarSeries", similarSeries);
		
		String michelNumbers   = CatalogUtils.toShortForm(series.getMichel().getNumbers());
		String scottNumbers    = CatalogUtils.toShortForm(series.getScott().getNumbers());
		String yvertNumbers    = CatalogUtils.toShortForm(series.getYvert().getNumbers());
		String gibbonsNumbers  = CatalogUtils.toShortForm(series.getGibbons().getNumbers());
		String solovyovNumbers = CatalogUtils.toShortForm(series.getSolovyov().getNumbers());
		String zagorskiNumbers = CatalogUtils.toShortForm(series.getZagorski().getNumbers());
		model.addAttribute("michelNumbers", michelNumbers);
		model.addAttribute("scottNumbers", scottNumbers);
		model.addAttribute("yvertNumbers", yvertNumbers);
		model.addAttribute("gibbonsNumbers", gibbonsNumbers);
		model.addAttribute("solovyovNumbers", solovyovNumbers);
		model.addAttribute("zagorskiNumbers", zagorskiNumbers);
		
		boolean userCanAddImagesToSeries =
			isUserCanAddImagesToSeries(authentication, currentUserId, series);
		model.addAttribute("allowAddingImages", userCanAddImagesToSeries);
		
		// we require DOWNLOAD_IMAGE and ADD_IMAGES_TO_SERIES in order to reduce
		// a number of the possible cases to maintain
		boolean userCanReplaceImages =
			SecurityContextUtils.hasAuthority(authentication, Authority.REPLACE_IMAGE)
			&& SecurityContextUtils.hasAuthority(authentication, Authority.DOWNLOAD_IMAGE)
			&& SecurityContextUtils.hasAuthority(authentication, Authority.ADD_IMAGES_TO_SERIES);
		model.addAttribute("allowReplacingImages", userCanReplaceImages);
		
		if (SecurityContextUtils.hasAuthority(authentication, Authority.UPDATE_COLLECTION)) {
			Map<Integer, Integer> seriesInstances =
				collectionService.findSeriesInstances(currentUserId, seriesId);
			model.addAttribute("seriesInstances", seriesInstances);
		}
		
		if (SecurityContextUtils.hasAuthority(authentication, Authority.VIEW_SERIES_SALES)) {
			List<SeriesSaleDto> seriesSales = seriesSalesService.findSales(seriesId);
			model.addAttribute("seriesSales", seriesSales);
		}
		
		if (SecurityContextUtils.hasAuthority(authentication, Authority.IMPORT_SERIES)) {
			ImportRequestInfo importInfo = seriesImportService.findRequestInfo(seriesId);
			model.addAttribute("importInfo", importInfo);
		}
	}
	
	private void addSeriesSalesFormToModel(Authentication authentication, Model model) {
		if (!SecurityContextUtils.hasAuthority(authentication, Authority.ADD_SERIES_SALES)) {
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
	
	private static boolean isUserCanAddImagesToSeries(
		Authentication authentication,
		Integer userId,
		SeriesDto series
	) {
		return isAdmin(authentication)
			|| (isOwner(userId, series) && isAllowedToAddingImages(series));
	}
	
	private static boolean isAdmin(Authentication authentication) {
		return SecurityContextUtils.hasAuthority(authentication, Authority.ADD_IMAGES_TO_SERIES);
	}
	
	private static boolean isOwner(Integer userId, SeriesDto series) {
		return userId != null
			&& Objects.equals(series.getCreatedBy(), userId);
	}
	
}

