/*
 * Copyright (C) 2009-2011 Slava Semushin <slava.semushin@gmail.com>
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

import javax.validation.Valid;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.validation.BindingResult;

import ru.mystamps.web.model.ActivateAccountForm;
import ru.mystamps.web.service.UserService;
import ru.mystamps.web.validation.ActivateAccountValidator;

import static ru.mystamps.web.SiteMap.ACTIVATE_ACCOUNT_PAGE_URL;

@Controller
@RequestMapping(ACTIVATE_ACCOUNT_PAGE_URL)
public class ActivateAccountController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ActivateAccountValidator activateAccountValidator;
	
	@InitBinder
	protected void initBinder(final WebDataBinder binder) {
		binder.setValidator(activateAccountValidator);
		binder.registerCustomEditor(String.class, "name", new StringTrimmerEditor(false));
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public ActivateAccountForm showForm(
			@RequestParam(value = "key", required = false, defaultValue = "")
			final String activationKey) {
		
		final ActivateAccountForm form = new ActivateAccountForm();
		form.setActivationKey(activationKey);
		return form;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String processInput(
			@Valid final ActivateAccountForm form,
			final BindingResult result) {
		
		if (result.hasErrors()) {
			return "account/activate";
		}
		
		userService.registerUser(
			form.getLogin(),
			form.getPassword(),
			form.getName(),
			form.getActivationKey()
		);
		
		return "account/activation_successful";
	}
	
}

