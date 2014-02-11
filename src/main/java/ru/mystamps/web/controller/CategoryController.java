/*
 * Copyright (C) 2009-2014 Slava Semushin <slava.semushin@gmail.com>
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

import javax.inject.Inject;
import javax.validation.Valid;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.validation.BindingResult;

import ru.mystamps.web.Url;
import ru.mystamps.web.entity.Category;
import ru.mystamps.web.entity.User;
import ru.mystamps.web.model.AddCategoryForm;
import ru.mystamps.web.service.CategoryService;

@Controller
public class CategoryController {
	
	private final CategoryService categoryService;
	
	@Inject
	public CategoryController(CategoryService categoryService) {
		this.categoryService = categoryService;
	}
	
	@InitBinder("addCategoryForm")
	protected void initBinder(WebDataBinder binder) {
		StringTrimmerEditor editor = new StringTrimmerEditor(false);
		binder.registerCustomEditor(String.class, "name", editor);
		binder.registerCustomEditor(String.class, "nameRu", editor);
	}
	
	@RequestMapping(value = Url.ADD_CATEGORY_PAGE, method = RequestMethod.GET)
	public AddCategoryForm showForm() {
		return new AddCategoryForm();
	}
	
	@RequestMapping(value = Url.ADD_CATEGORY_PAGE, method = RequestMethod.POST)
	public String processInput(
		@Valid AddCategoryForm form,
		BindingResult result,
		User currentUser) {
		
		if (result.hasErrors()) {
			return null;
		}
		
		Category category = categoryService.add(form, currentUser);
		
		String dstUrl = UriComponentsBuilder.fromUriString(Url.INFO_CATEGORY_PAGE)
			.buildAndExpand(category.getId())
			.toString();
		
		return "redirect:" + dstUrl;
	}
	
	@RequestMapping(value = Url.INFO_CATEGORY_PAGE, method = RequestMethod.GET)
	public String showInfo(@PathVariable("id") Category category, Model model) {
		
		if (category == null) {
			throw new NotFoundException();
		}
		
		model.addAttribute("category", category);
		return "category/info";
	}
	
}

