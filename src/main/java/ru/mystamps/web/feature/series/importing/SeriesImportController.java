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
package ru.mystamps.web.feature.series.importing;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.mystamps.web.common.LocaleUtils;
import ru.mystamps.web.feature.participant.EntityWithIdDto;
import ru.mystamps.web.feature.participant.ParticipantService;
import ru.mystamps.web.feature.series.CatalogUtils;
import ru.mystamps.web.feature.series.SeriesController;
import ru.mystamps.web.feature.series.SeriesUrl;
import ru.mystamps.web.feature.series.importing.event.ImportRequestCreated;
import ru.mystamps.web.feature.series.importing.event.RetryDownloading;
import ru.mystamps.web.feature.series.importing.sale.SeriesSaleParsedDataDto;
import ru.mystamps.web.feature.series.importing.sale.SeriesSalesImportService;
import ru.mystamps.web.support.spring.security.CustomUserDetails;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static ru.mystamps.web.common.ControllerUtils.redirectTo;

@Controller
@RequiredArgsConstructor
public class SeriesImportController {
	
	private final SeriesImportService seriesImportService;
	private final SeriesSalesImportService seriesSalesImportService;
	private final SeriesController seriesController;
	private final ParticipantService participantService;
	private final ApplicationEventPublisher eventPublisher;
	
	@InitBinder("requestImportForm")
	protected void initRequestImportForm(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, "url", new StringTrimmerEditor(true));
	}
	
	@InitBinder("importSeriesForm")
	protected void initImportSeriesForm(WebDataBinder binder) {
		// CheckStyle: ignore LineLength for next 1 line
		binder.registerCustomEditor(String.class, "michelNumbers", new ExpandCatalogNumbersEditor());
		binder.registerCustomEditor(String.class, "seller.name", new StringTrimmerEditor(true));
		binder.registerCustomEditor(String.class, "seller.url", new StringTrimmerEditor(true));
	}
	
	@GetMapping(SeriesImportUrl.REQUEST_IMPORT_SERIES_PAGE)
	public void showRequestImportForm(Model model) {
		RequestSeriesImportForm requestImportForm = new RequestSeriesImportForm();
		model.addAttribute("requestImportForm", requestImportForm);
	}
	
	@PostMapping(SeriesImportUrl.REQUEST_IMPORT_SERIES_PAGE)
	public String processRequestImportForm(
		@Valid @ModelAttribute("requestImportForm") RequestSeriesImportForm form,
		BindingResult result,
		@AuthenticationPrincipal CustomUserDetails currentUser) {
		
		if (result.hasErrors()) {
			return null;
		}
		
		Integer requestId = seriesImportService.addRequest(form, currentUser.getUserId());
		
		// @todo #927 Extract logic to a separate method or add to SeriesImportService.addRequest()
		ImportRequestCreated requestCreated =
			new ImportRequestCreated(this, requestId, form.getUrl());
		eventPublisher.publishEvent(requestCreated);
		
		return redirectTo(SeriesImportUrl.REQUEST_IMPORT_PAGE, requestId);
	}

	// @todo #1254 Update workflow to mention RetryDownloading event
	@PostMapping(path = SeriesImportUrl.REQUEST_IMPORT_SERIES_PAGE, params = "requestId")
	public String rerunImport(
		@RequestParam("requestId") Integer requestId,
		HttpServletResponse response)
		throws IOException {
		
		ImportRequestDto request = seriesImportService.findById(requestId);
		if (request == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		
		RetryDownloading retryDownloading = new RetryDownloading(this, requestId);
		eventPublisher.publishEvent(retryDownloading);
		
		return redirectTo(SeriesImportUrl.REQUEST_IMPORT_PAGE, requestId);
	}
	
	@SuppressWarnings({ "PMD.ModifiedCyclomaticComplexity", "PMD.NPathComplexity" })
	@GetMapping(SeriesImportUrl.REQUEST_IMPORT_PAGE)
	public String showRequestAndImportSeriesForm(
		@PathVariable("id") Integer requestId,
		Model model,
		Locale userLocale,
		HttpServletResponse response)
		throws IOException {
		
		if (requestId == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		
		ImportRequestDto request = seriesImportService.findById(requestId);
		if (request == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		
		model.addAttribute("request", request);

		String lang = LocaleUtils.getLanguageOrNull(userLocale);
		SeriesParsedDataDto series = seriesImportService.getParsedData(requestId, lang);
		
		ImportSeriesForm form = new ImportSeriesForm();
		form.setPerforated(Boolean.TRUE);
		
		boolean hasParsedData = series != null;
		if (hasParsedData) {
			form.setCategory(series.getCategory());
			form.setCountry(series.getCountry());
			form.setImageUrl(series.getImageUrl());
			form.setDay(series.getIssueDay());
			form.setMonth(series.getIssueMonth());
			form.setYear(series.getIssueYear());
			form.setQuantity(series.getQuantity());
			if (series.getPerforated() != null) {
				form.setPerforated(series.getPerforated());
			}
			form.setMichelNumbers(CatalogUtils.toShortForm(series.getMichelNumbers()));
		}
		
		SeriesSaleParsedDataDto seriesSale = seriesSalesImportService.getParsedData(requestId);
		if (seriesSale != null) {
			ImportSeriesSalesForm seriesSaleForm = new ImportSeriesSalesForm();
			seriesSaleForm.setSellerId(seriesSale.getSellerId());
			seriesSaleForm.setPrice(seriesSale.getPrice());
			seriesSaleForm.setCurrency(seriesSale.getCurrency());
			seriesSaleForm.setAltPrice(seriesSale.getAltPrice());
			seriesSaleForm.setAltCurrency(seriesSale.getAltCurrency());
			seriesSaleForm.setCondition(seriesSale.getCondition());
			
			ImportSellerForm sellerForm = new ImportSellerForm();
			sellerForm.setName(seriesSale.getSellerName());
			sellerForm.setUrl(seriesSale.getSellerUrl());
			sellerForm.setGroupId(seriesSale.getSellerGroupId());
			
			form.setSeller(sellerForm);
			form.setSeriesSale(seriesSaleForm);
			
			if (seriesSale.getSellerId() == null) {
				// required for displaying seller group
				List<EntityWithIdDto> groups = participantService.findAllGroups();
				model.addAttribute("groups", groups);
			} else {
				seriesController.addSellersToModel(model);
			}
		}
		
		model.addAttribute("importSeriesForm", form);
		model.addAttribute("showForm", hasParsedData);
		
		seriesController.addCategoriesToModel(model, lang);
		seriesController.addCountriesToModel(model, lang);
		seriesController.addYearToModel(model);
		
		return "series/import/info";
	}
	
	@SuppressWarnings("checkstyle:parameternumber")
	@PostMapping(SeriesImportUrl.REQUEST_IMPORT_PAGE)
	public String processImportSeriesForm(
		@PathVariable("id") Integer requestId,
		Model model,
		@Valid ImportSeriesForm form,
		BindingResult result,
		@AuthenticationPrincipal CustomUserDetails currentUser,
		Locale userLocale,
		HttpServletRequest httpRequest,
		HttpServletResponse httpResponse)
		throws IOException {
		
		if (requestId == null) {
			httpResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		
		ImportRequestDto request = seriesImportService.findById(requestId);
		if (request == null) {
			httpResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		
		SeriesController.loadErrorsFromDownloadInterceptor(form, result, httpRequest);
		
		model.addAttribute("request", request);
		
		String lang = LocaleUtils.getLanguageOrNull(userLocale);
		
		SeriesParsedDataDto series = seriesImportService.getParsedData(requestId, lang);
		boolean hasParsedData = series != null;
		model.addAttribute("showForm", hasParsedData);
		
		seriesController.addCategoriesToModel(model, lang);
		seriesController.addCountriesToModel(model, lang);
		seriesController.addYearToModel(model);
		
		ImportSeriesSalesForm seriesSaleForm = form.getSeriesSale();
		if (seriesSaleForm != null) {
			seriesController.addSellersToModel(model);
			
			// required for displaying seller group
			List<EntityWithIdDto> groups = participantService.findAllGroups();
			model.addAttribute("groups", groups);
		}
		
		if (result.hasErrors()) {
			return "series/import/info";
		}
		
		if (seriesSaleForm != null) {
			// fill required values prior passing the form into the service.
			seriesSaleForm.setDate(new Date());
			seriesSaleForm.setUrl(request.getUrl());
		}
		
		Integer seriesId = seriesImportService.addSeries(
			form,
			form.getSeller(),
			seriesSaleForm,
			requestId,
			currentUser.getUserId()
		);
		
		return redirectTo(SeriesUrl.INFO_SERIES_PAGE, seriesId);
	}
	
	@GetMapping(SeriesImportUrl.LIST_IMPORT_REQUESTS_PAGE)
	public String showListOfImportRequests(Model model) {
		model.addAttribute("requests", seriesImportService.findAll());
		
		return "series/import/list";
	}
	
}

