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

import java.io.IOException;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.validation.BindingResult;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.Url;
import ru.mystamps.web.entity.Category;
import ru.mystamps.web.entity.User;
import ru.mystamps.web.model.AddCategoryForm;
import ru.mystamps.web.service.CategoryService;
import ru.mystamps.web.service.SeriesService;
import ru.mystamps.web.service.dto.UrlEntityDto;
import ru.mystamps.web.util.LocaleUtils;

@Controller
@RequiredArgsConstructor
public class CategoryController {
	
	private final CategoryService categoryService;
	private final SeriesService seriesService;
	
	@InitBinder("addCategoryForm")
	protected void initBinder(WebDataBinder binder) {
		StringTrimmerEditor editor = new StringTrimmerEditor(false);
		binder.registerCustomEditor(String.class, "name", editor);
		binder.registerCustomEditor(String.class, "nameRu", editor);
	}
	
	@RequestMapping(Url.ADD_CATEGORY_PAGE)
	public AddCategoryForm showForm() {
		return new AddCategoryForm();
	}
	
	@RequestMapping(value = Url.ADD_CATEGORY_PAGE, method = RequestMethod.POST)
	public String processInput(
		@Valid AddCategoryForm form,
		BindingResult result,
		User currentUser,
		RedirectAttributes redirectAttributes) {
		
		if (result.hasErrors()) {
			return null;
		}
		
		UrlEntityDto categoryUrl = categoryService.add(form, currentUser.getId());
		
		String dstUrl = UriComponentsBuilder.fromUriString(Url.INFO_CATEGORY_PAGE)
			.buildAndExpand(categoryUrl.getId(), categoryUrl.getSlug())
			.toString();
		
		redirectAttributes.addFlashAttribute("justAddedCategory", true);
		
		return "redirect:" + dstUrl;
	}
	
	@RequestMapping(Url.INFO_CATEGORY_PAGE)
	public String showInfo(
		@PathVariable("id") Category category,
		Model model,
		Locale userLocale,
		HttpServletResponse response)
		throws IOException {
		
		if (category == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		
		model.addAttribute("category", category);
		
		String lang = LocaleUtils.getLanguageOrNull(userLocale);
		Integer categoryId = category.getId();
		model.addAttribute("seriesOfCategory", seriesService.findByCategoryId(categoryId, lang));
		
		return "category/info";
	}
	
	@RequestMapping(Url.LIST_CATEGORIES_PAGE)
	public void list(Model model, Locale userLocale) {
		String lang = LocaleUtils.getLanguageOrNull(userLocale);
		model.addAttribute("categories", categoryService.findAllAsLinkEntities(lang));
	}
	
}

