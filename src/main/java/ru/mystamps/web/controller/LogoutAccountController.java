package ru.mystamps.web.controller;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static ru.mystamps.web.SiteMap.INDEX_PAGE_URL;
import static ru.mystamps.web.SiteMap.LOGOUT_PAGE_URL;

@Controller
@RequestMapping(LOGOUT_PAGE_URL)
public class LogoutAccountController {
	
	@RequestMapping(method = RequestMethod.GET)
	public String logout(final HttpSession session) {
		session.invalidate();
		return "redirect:" + INDEX_PAGE_URL;
	}
	
}

