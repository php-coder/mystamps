package ru.mystamps.web.controller;

import java.sql.SQLException;

import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.validation.BindingResult;

import ru.mystamps.db.Users;
import ru.mystamps.db.SuspiciousActivities;
import ru.mystamps.web.entity.User;
import ru.mystamps.web.model.AuthAccountForm;
import ru.mystamps.web.validation.AuthAccountValidator;

import static ru.mystamps.web.SiteMap.INDEX_PAGE_URL;
import static ru.mystamps.web.SiteMap.AUTHENTICATION_PAGE_URL;

@Controller
@RequestMapping(AUTHENTICATION_PAGE_URL)
public class AuthAccountController {
	
	@Autowired
	private Users users;
	
	@Autowired
	private SuspiciousActivities act;
	
	@Autowired
	private AuthAccountValidator authAccountValidator;
	
	@InitBinder
	protected void initAuthBinder(final WebDataBinder binder) {
		binder.setValidator(authAccountValidator);
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public AuthAccountForm showForm() {
		return new AuthAccountForm();
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String processInput(
			final HttpServletRequest request,
			final HttpSession session,
			@RequestHeader("referer") final String referer,
			@RequestHeader("user-agent") final String agent,
			@Valid final AuthAccountForm form,
			final BindingResult result)
		throws SQLException {
		
		if (result.hasErrors()) {
			
			// When user provides wrong login/password pair than
			// validation mechanism add error to form. Check this to
			// handle situation with wrong credentials.
			// @see AuthAccountValidator::validateLoginPasswordPair()
			if (result.hasGlobalErrors() &&
				result.getGlobalError() != null &&
				result.getGlobalError().getCode().equals("login.password.invalid")) {
				
				// TODO: log more info (login/password pair for example) (#59)
				// TODO: sanitize all user's values (#60)
				final String page = request.getRequestURI();
				final String ip   = request.getRemoteAddr();
				
				Long uid = null;
				final User user = (User)session.getAttribute("user");
				if (user != null) {
					uid = user.getUid();
				}
				
				act.logEvent("AuthenticationFailed", page, uid, ip, referer, agent);
			}
			
			return "account/auth";
		}
		
		final User user = users.getUserByLogin(form.getLogin());
		session.setAttribute("user", user);
		
		return "redirect:" + INDEX_PAGE_URL;
	}
	
}

