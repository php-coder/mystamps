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
	
	@Autowired
	private UserService userService;
	
	@InitBinder
	protected void initBinder(final WebDataBinder binder) {
		binder.setValidator(new RegisterAccountValidator());
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

