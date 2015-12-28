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

import java.util.Locale;

import javax.validation.Valid;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.Url;
import ru.mystamps.web.model.ActivateAccountForm;
import ru.mystamps.web.model.ActivateAccountForm.ActKeyChecks;
import ru.mystamps.web.model.ActivateAccountForm.FormChecks;
import ru.mystamps.web.model.ActivateAccountForm.LoginChecks;
import ru.mystamps.web.model.ActivateAccountForm.NameChecks;
import ru.mystamps.web.model.ActivateAccountForm.PasswordChecks;
import ru.mystamps.web.model.ActivateAccountForm.PasswordConfirmationChecks;
import ru.mystamps.web.model.RegisterAccountForm;
import ru.mystamps.web.service.UserService;
import ru.mystamps.web.service.UsersActivationService;

@Controller
@RequiredArgsConstructor
public class AccountController {
	
	private final UserService userService;
	private final UsersActivationService usersActivationService;
	
	@InitBinder("registerAccountForm")
	protected void registrationInitBinder(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, "email", new StringTrimmerEditor(false));
	}
	
	@InitBinder("activateAccountForm")
	protected void activationInitBinder(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, "name", new StringTrimmerEditor(true));
	}
	
	@RequestMapping(Url.REGISTRATION_PAGE)
	public RegisterAccountForm showRegistrationForm() {
		return new RegisterAccountForm();
	}
	
	@RequestMapping(value = Url.REGISTRATION_PAGE, method = RequestMethod.POST)
	public String processRegistrationForm(
		@Valid RegisterAccountForm form,
		BindingResult result,
		RedirectAttributes redirectAttributes,
		Locale userLocale) {
		
		if (result.hasErrors()) {
			return null;
		}
		
		usersActivationService.add(form, userLocale);
		
		redirectAttributes.addFlashAttribute("justRegisteredUser", true);
		
		return "redirect:" + Url.ACTIVATE_ACCOUNT_PAGE;
	}
	
	@RequestMapping(Url.ACTIVATE_ACCOUNT_PAGE)
	public ActivateAccountForm showActivationForm() {
		return new ActivateAccountForm();
	}
	
	@RequestMapping(Url.ACTIVATE_ACCOUNT_PAGE_WITH_KEY)
	public String showActivationFormWithKey(
		@PathVariable("key") String activationKey,
		Model model) {
		
		ActivateAccountForm form = new ActivateAccountForm();
		form.setActivationKey(activationKey);
		model.addAttribute("activateAccountForm", form);
		
		return "account/activate";
	}
	
	@RequestMapping(value = Url.ACTIVATE_ACCOUNT_PAGE, method = RequestMethod.POST)
	public String processActivationForm(
		@Validated({
			LoginChecks.class, NameChecks.class, PasswordChecks.class,
			PasswordConfirmationChecks.class, ActKeyChecks.class, FormChecks.class
		}) ActivateAccountForm form, BindingResult result, RedirectAttributes redirectAttributes) {
		
		if (result.hasErrors()) {
			return null;
		}
		
		userService.registerUser(form);
		
		redirectAttributes.addFlashAttribute("justActivatedUser", true);
		
		return "redirect:" + Url.AUTHENTICATION_PAGE;
	}
	
}

