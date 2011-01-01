package ru.mystamps.web.controller;

import java.sql.SQLException;

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

import org.apache.log4j.Logger;

import ru.mystamps.db.UsersActivation;
import ru.mystamps.db.Users;
import ru.mystamps.web.model.ActivateAccountForm;
import ru.mystamps.web.validation.ActivateAccountValidator;

import static ru.mystamps.web.SiteMap.ACTIVATE_ACCOUNT_PAGE_URL;

@Controller
@RequestMapping(ACTIVATE_ACCOUNT_PAGE_URL)
public class ActivateAccountController {
	
	private final Logger log = Logger.getRootLogger();
	
	@Autowired
	private Users users;
	
	@Autowired
	private UsersActivation activationRequests;
	
	@Autowired
	private ActivateAccountValidator activateAccountValidator;
	
	@InitBinder
	protected void initBinder(final WebDataBinder binder) {
		binder.setValidator(activateAccountValidator);
		binder.registerCustomEditor(String.class, "name", new StringTrimmerEditor(false));
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public ActivateAccountForm showForm(
			@RequestParam(value = "key", required = false, defaultValue = "") final String activationKey) {
		final ActivateAccountForm form = new ActivateAccountForm();
		form.setActivationKey(activationKey);
		return form;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String processInput(
			@Valid final ActivateAccountForm form,
			final BindingResult result)
		throws SQLException {
		
		if (result.hasErrors()) {
			return "account/activate";
		}
		
		final String login = form.getLogin();
		final String password = form.getPassword();
		final String activationKey = form.getActivationKey();
		
		// use login as name if name is not provided
		final String name = ("".equals(form.getName()) ? login : form.getName());
		
		log.debug("Activate user '" + login + "' (" + name + ") with password '" + password +
				"' (key = " + activationKey + ")");
		
		// TODO: use transaction
		users.add(login, password, name, activationKey);
		activationRequests.del(activationKey);
		
		return "account/activation_successful";
	}
	
}

