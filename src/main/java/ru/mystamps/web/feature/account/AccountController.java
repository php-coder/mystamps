/*
 * Copyright (C) 2009-2024 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.feature.account;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.mystamps.web.feature.account.ActivateAccountForm.ActKeyChecks;
import ru.mystamps.web.feature.account.ActivateAccountForm.FormChecks;
import ru.mystamps.web.feature.account.ActivateAccountForm.LoginChecks;
import ru.mystamps.web.feature.account.ActivateAccountForm.NameChecks;
import ru.mystamps.web.feature.account.ActivateAccountForm.PasswordChecks;
import ru.mystamps.web.feature.account.ActivateAccountForm.PasswordConfirmationChecks;

import javax.validation.Valid;
import java.util.Locale;

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
		binder.registerCustomEditor(String.class, "login", new StringTrimmerEditor(true));
		binder.registerCustomEditor(String.class, "name", new StringTrimmerEditor(true));
	}
	
	@GetMapping(AccountUrl.REGISTRATION_PAGE)
	public RegisterAccountForm showRegistrationForm() {
		return new RegisterAccountForm();
	}
	
	@PostMapping(AccountUrl.REGISTRATION_PAGE)
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
		
		return "redirect:" + AccountUrl.ACTIVATE_ACCOUNT_PAGE;
	}
	
	@GetMapping(AccountUrl.ACTIVATE_ACCOUNT_PAGE)
	public ActivateAccountForm showActivationForm(
		@RequestParam(name = "key", required = false) String activationKey) {
		
		ActivateAccountForm form = new ActivateAccountForm();
		if (StringUtils.isNotEmpty(activationKey)) {
			form.setActivationKey(activationKey);
		}
		
		return form;
	}
	
	@PostMapping(AccountUrl.ACTIVATE_ACCOUNT_PAGE)
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
		
		return "redirect:" + AccountUrl.AUTHENTICATION_PAGE;
	}
	
}

