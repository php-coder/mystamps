package ru.mystamps.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static ru.mystamps.web.SiteMap.MAINTENANCE_PAGE_URL;

@Controller
@RequestMapping(MAINTENANCE_PAGE_URL)
public class MaintenanceSiteController {
	
	@RequestMapping(method = RequestMethod.GET)
	public void maintenance() {
	}
	
}

