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

import java.util.Locale;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.Url;
import ru.mystamps.web.entity.Collection;
import ru.mystamps.web.service.SeriesService;
import ru.mystamps.web.util.LocaleUtils;

@Controller
@RequiredArgsConstructor
public class CollectionController {
	
	private final SeriesService seriesService;
	
	@RequestMapping(value = Url.INFO_COLLECTION_PAGE, method = RequestMethod.GET)
	public String showInfo(
		@PathVariable("id") Collection collection,
		Model model,
		Locale userLocale) {
		
		if (collection == null) {
			throw new NotFoundException();
		}
		
		model.addAttribute("ownerName", collection.getOwner().getName());
		
		String lang = LocaleUtils.getLanguageOrNull(userLocale);
		model.addAttribute("seriesOfCollection", seriesService.findBy(collection, lang));
		
		return "collection/info";
	}
	
}
