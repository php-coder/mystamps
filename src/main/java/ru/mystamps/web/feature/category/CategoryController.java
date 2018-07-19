/*
 * Copyright (C) 2009-2018 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.feature.category;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.Url;
import ru.mystamps.web.controller.converter.annotation.Category;
import ru.mystamps.web.controller.converter.annotation.CurrentUser;
import ru.mystamps.web.controller.editor.ReplaceRepeatingSpacesEditor;
import ru.mystamps.web.dao.dto.LinkEntityDto;
import ru.mystamps.web.dao.dto.SeriesInfoDto;
import ru.mystamps.web.service.CategoryService;
import ru.mystamps.web.service.SeriesService;
import ru.mystamps.web.util.LocaleUtils;

import static ru.mystamps.web.util.ControllerUtils.redirectTo;

@Controller
@RequiredArgsConstructor
public class CategoryController {
	
	private final CategoryService categoryService;
	private final SeriesService seriesService;
	
	@InitBinder("addCategoryForm")
	protected void initBinder(WebDataBinder binder) {
		// We can't use StringTrimmerEditor here because "only one single registered custom
		// editor per property path is supported".
		ReplaceRepeatingSpacesEditor editor = new ReplaceRepeatingSpacesEditor(true);
		binder.registerCustomEditor(String.class, "name", editor);
		binder.registerCustomEditor(String.class, "nameRu", editor);
	}
	
	@GetMapping(Url.ADD_CATEGORY_PAGE)
	public AddCategoryForm showForm() {
		return new AddCategoryForm();
	}
	
	@PostMapping(Url.ADD_CATEGORY_PAGE)
	public String processInput(
		@Valid AddCategoryForm form,
		BindingResult result,
		@CurrentUser Integer currentUserId,
		RedirectAttributes redirectAttributes) {
		
		if (result.hasErrors()) {
			return null;
		}
		
		String slug = categoryService.add(form, currentUserId);
		
		redirectAttributes.addFlashAttribute("justAddedCategory", true);
		
		return redirectTo(Url.INFO_CATEGORY_PAGE, slug);
	}
	
	@GetMapping(Url.INFO_CATEGORY_PAGE)
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
	
	@GetMapping(Url.INFO_CATEGORY_BY_ID_PAGE)
	public View showInfoById(
		@Category @PathVariable("slug") LinkEntityDto country,
		HttpServletResponse response)
		throws IOException {
		
		if (country == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		
		RedirectView view = new RedirectView();
		view.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
		view.setUrl(Url.INFO_CATEGORY_PAGE);
		
		return view;
	}
	
	@GetMapping(Url.GET_CATEGORIES_PAGE)
	public String showCategories(Model model, Locale userLocale) {
		String lang = LocaleUtils.getLanguageOrNull(userLocale);
		List<LinkEntityDto> categories = categoryService.findAllAsLinkEntities(lang);
		
		model.addAttribute("categories", categories);
		
		return "category/list";
	}
	
	@GetMapping(Url.LIST_CATEGORIES_PAGE)
	public View list() {
		RedirectView view = new RedirectView();
		view.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
		view.setUrl(Url.GET_CATEGORIES_PAGE);
		return view;
	}
	
}

