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

import javax.validation.Valid;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.Url;
import ru.mystamps.web.controller.dto.AddParticipantForm;
import ru.mystamps.web.service.TransactionParticipantService;

import static ru.mystamps.web.controller.ControllerUtils.redirectTo;

@Controller
@RequiredArgsConstructor
public class ParticipantController {
	
	private final TransactionParticipantService participantService;
	
	@InitBinder("addParticipantForm")
	protected void initBinder(WebDataBinder binder) {
		StringTrimmerEditor editor = new StringTrimmerEditor(true);
		binder.registerCustomEditor(String.class, "name", editor);
		binder.registerCustomEditor(String.class, "url", editor);
	}
	
	@GetMapping(Url.ADD_PARTICIPANT_PAGE)
	public AddParticipantForm showForm() {
		return new AddParticipantForm();
	}
	
	@PostMapping(Url.ADD_PARTICIPANT_PAGE)
	public String processInput(@Valid AddParticipantForm form, BindingResult result) {
		if (result.hasErrors()) {
			return null;
		}
		
		participantService.add(form);
		
		return redirectTo(Url.INDEX_PAGE);
	}
	
}
