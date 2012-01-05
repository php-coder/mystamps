/*
 * Copyright (C) 2009-2012 Slava Semushin <slava.semushin@gmail.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

package ru.mystamps.web.controller;

import java.util.Map;

import javax.inject.Inject;
import javax.validation.Valid;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.validation.BindingResult;

import ru.mystamps.web.model.ActivateAccountForm;
import ru.mystamps.web.service.UserService;
import ru.mystamps.web.validation.ActivateAccountValidator;

import static ru.mystamps.web.SiteMap.ACTIVATE_ACCOUNT_PAGE_URL;
import static ru.mystamps.web.SiteMap.ACTIVATE_ACCOUNT_PAGE_WITH_KEY_URL;
import static ru.mystamps.web.SiteMap.SUCCESSFUL_ACTIVATION_PAGE_URL;

@Controller
public class ActivateAccountController {
	
	private final UserService userService;
	private final ActivateAccountValidator activateAccountValidator;
	
	@Inject
	ActivateAccountController(
		final UserService userService,
		final ActivateAccountValidator activateAccountValidator) {
		
		this.userService = userService;
		this.activateAccountValidator = activateAccountValidator;
	}
	
	@InitBinder
	protected void initBinder(final WebDataBinder binder) {
		binder.setValidator(activateAccountValidator);
		binder.registerCustomEditor(String.class, "name", new StringTrimmerEditor(false));
	}
	
	@RequestMapping(value = ACTIVATE_ACCOUNT_PAGE_URL, method = RequestMethod.GET)
	public ActivateAccountForm showForm() {
		return new ActivateAccountForm();
	}
	
	@RequestMapping(value = ACTIVATE_ACCOUNT_PAGE_WITH_KEY_URL, method = RequestMethod.GET)
	public String showForm(@PathVariable("key") final String activationKey, final Map model) {
		
		final ActivateAccountForm form = new ActivateAccountForm();
		form.setActivationKey(activationKey);
		model.put("activateAccountForm", form);
		
		return "account/activate";
	}
	
	@RequestMapping(value = ACTIVATE_ACCOUNT_PAGE_URL, method = RequestMethod.POST)
	public String processInput(
			@Valid final ActivateAccountForm form,
			final BindingResult result) {
		
		if (result.hasErrors()) {
			return null;
		}
		
		userService.registerUser(
			form.getLogin(),
			form.getPassword(),
			form.getName(),
			form.getActivationKey()
		);
		
		return "redirect:" + SUCCESSFUL_ACTIVATION_PAGE_URL;
	}
	
}

