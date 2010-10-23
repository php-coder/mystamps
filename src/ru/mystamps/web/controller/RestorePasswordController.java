package ru.mystamps.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static ru.mystamps.web.SiteMap.RESTORE_PASSWORD_PAGE_URL;

@Controller
@RequestMapping(RESTORE_PASSWORD_PAGE_URL)
public class RestorePasswordController {
	
	@RequestMapping(method = RequestMethod.GET)
	public void showForm() {
	}
	
}

