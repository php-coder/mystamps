package ru.mystamps.web.controller;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.validation.BindingResult;

import ru.mystamps.web.model.AddCountryForm;
import ru.mystamps.web.validation.AddCountryValidator;

@Controller
@RequestMapping("/country/add.htm")
public class AddCountryController {
	
	@InitBinder
	protected void initBinder(final WebDataBinder binder) {
		binder.setValidator(new AddCountryValidator());
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public AddCountryForm showForm() {
		return new AddCountryForm();
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public void processInput(
			@Valid final AddCountryForm form,
			final BindingResult result) {
	}
	
}

