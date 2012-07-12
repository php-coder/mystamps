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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

package ru.mystamps.web.controller;

import java.util.Map;

import javax.inject.Inject;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;

import ru.mystamps.web.Url;
import ru.mystamps.web.model.ActivateAccountForm;
import ru.mystamps.web.model.ActivateAccountForm.LoginChecks;
import ru.mystamps.web.model.ActivateAccountForm.NameChecks;
import ru.mystamps.web.model.ActivateAccountForm.PasswordChecks;
import ru.mystamps.web.model.ActivateAccountForm.PasswordConfirmationChecks;
import ru.mystamps.web.model.ActivateAccountForm.ActKeyChecks;
import ru.mystamps.web.model.ActivateAccountForm.FormChecks;
import ru.mystamps.web.service.UserService;

@Controller
public class ActivateAccountController {
	
	private final UserService userService;
	
	@Inject
	ActivateAccountController(final UserService userService) {
		this.userService = userService;
	}
	
	@InitBinder
	protected void initBinder(final WebDataBinder binder) {
		binder.registerCustomEditor(String.class, "name", new StringTrimmerEditor(true));
	}
	
	@RequestMapping(value = Url.ACTIVATE_ACCOUNT_PAGE, method = RequestMethod.GET)
	public ActivateAccountForm showForm() {
		return new ActivateAccountForm();
	}
	
	@RequestMapping(value = Url.ACTIVATE_ACCOUNT_PAGE_WITH_KEY, method = RequestMethod.GET)
	public String showForm(@PathVariable("key") final String activationKey, final Map model) {
		
		final ActivateAccountForm form = new ActivateAccountForm();
		form.setActivationKey(activationKey);
		model.put("activateAccountForm", form);
		
		return "account/activate";
	}
	
	@RequestMapping(value = Url.ACTIVATE_ACCOUNT_PAGE, method = RequestMethod.POST)
	public String processInput(
		@Validated({
			LoginChecks.class, NameChecks.class, PasswordChecks.class,
			PasswordConfirmationChecks.class, ActKeyChecks.class, FormChecks.class
		}) final ActivateAccountForm form, final BindingResult result) {
		
		if (result.hasErrors()) {
			return null;
		}
		
		userService.registerUser(
			form.getLogin(),
			form.getPassword(),
			form.getName(),
			form.getActivationKey()
		);
		
		return "redirect:" + Url.SUCCESSFUL_ACTIVATION_PAGE;
	}
	
}

