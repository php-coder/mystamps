package ru.mystamps.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/site/maintenance.htm")
public class MaintenanceSiteController {
	
	@RequestMapping(method = RequestMethod.GET)
	public void maintenance() {
	}
	
}

