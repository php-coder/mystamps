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
package ru.mystamps.web.feature.category;

import lombok.RequiredArgsConstructor;
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
import ru.mystamps.web.common.LinkEntityDto;
import ru.mystamps.web.common.LocaleUtils;
import ru.mystamps.web.feature.series.SeriesUrl;
import ru.mystamps.web.support.spring.mvc.ReplaceRepeatingSpacesEditor;
import ru.mystamps.web.support.spring.security.CurrentUser;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static ru.mystamps.web.common.ControllerUtils.redirectTo;

@Controller
@RequiredArgsConstructor
public class CategoryController {
	
	private final CategoryService categoryService;
	
	@InitBinder("addCategoryForm")
	protected void initBinder(WebDataBinder binder) {
		// We can't use StringTrimmerEditor here because "only one single registered custom
		// editor per property path is supported".
		ReplaceRepeatingSpacesEditor editor = new ReplaceRepeatingSpacesEditor(true);
		binder.registerCustomEditor(String.class, "name", editor);
		binder.registerCustomEditor(String.class, "nameRu", editor);
	}
	
	@GetMapping(CategoryUrl.ADD_CATEGORY_PAGE)
	public AddCategoryForm showForm() {
		return new AddCategoryForm();
	}
	
	@PostMapping(CategoryUrl.ADD_CATEGORY_PAGE)
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
		
		return redirectTo(SeriesUrl.INFO_CATEGORY_PAGE, slug);
	}
	
	@GetMapping(CategoryUrl.INFO_CATEGORY_BY_ID_PAGE)
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
		view.setUrl(SeriesUrl.INFO_CATEGORY_PAGE);
		
		return view;
	}
	
	@GetMapping(CategoryUrl.GET_CATEGORIES_PAGE)
	public String showCategories(Model model, Locale userLocale) {
		String lang = LocaleUtils.getLanguageOrNull(userLocale);
		List<LinkEntityDto> categories = categoryService.findAllAsLinkEntities(lang);
		
		model.addAttribute("categories", categories);
		
		return "category/list";
	}
	
	@GetMapping(CategoryUrl.LIST_CATEGORIES_PAGE)
	public View list() {
		RedirectView view = new RedirectView();
		view.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
		view.setUrl(CategoryUrl.GET_CATEGORIES_PAGE);
		return view;
	}
	
}

