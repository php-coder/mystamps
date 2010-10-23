package ru.mystamps.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static ru.mystamps.web.SiteMap.INDEX_PAGE_URL;

@Controller
@RequestMapping(INDEX_PAGE_URL)
public class IndexSiteController {
	
	@RequestMapping(method = RequestMethod.GET)
	public void index() {
	}
	
}

