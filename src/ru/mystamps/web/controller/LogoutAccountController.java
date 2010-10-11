package ru.mystamps.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/account/logout.htm")
public class LogoutAccountController {
	
	@RequestMapping(method = RequestMethod.GET)
	public String logout(final HttpServletRequest request) {
		
		final HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
		
		return "redirect:/site/index.htm";
	}
	
}

