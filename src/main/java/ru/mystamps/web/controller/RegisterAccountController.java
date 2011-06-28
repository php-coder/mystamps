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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.validation.BindingResult;

import ru.mystamps.web.model.RegisterAccountForm;
import ru.mystamps.web.service.UserService;
import ru.mystamps.web.validation.RegisterAccountValidator;

import static ru.mystamps.web.SiteMap.REGISTRATION_PAGE_URL;

@Controller
@RequestMapping(REGISTRATION_PAGE_URL)
public class RegisterAccountController {
	
	private final UserService userService;
	private final RegisterAccountValidator registerAccountValidator;
	
	@Autowired
	RegisterAccountController(
		final UserService userService,
		final RegisterAccountValidator registerAccountValidator) {
		
		this.userService = userService;
		this.registerAccountValidator = registerAccountValidator;
	}
	
	@InitBinder
	protected void initBinder(final WebDataBinder binder) {
		binder.setValidator(registerAccountValidator);
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public RegisterAccountForm showForm() {
		return new RegisterAccountForm();
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String processInput(
			@Valid final RegisterAccountForm form,
			final BindingResult result) {
		
		if (result.hasErrors()) {
			return "account/register";
		}
		
		userService.addRegistrationRequest(form.getEmail());
		
		// TODO: do redirect to protect from double submission (#74)
		return "account/activation_sent";
	}
	
}

