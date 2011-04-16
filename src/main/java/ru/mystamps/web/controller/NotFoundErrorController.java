package ru.mystamps.web.controller;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.mystamps.db.SuspiciousActivities;
import ru.mystamps.web.entity.User;

import static ru.mystamps.web.SiteMap.NOT_FOUND_PAGE_URL;

@Controller
@RequestMapping(NOT_FOUND_PAGE_URL)
public class NotFoundErrorController {
	private final Logger log = Logger.getRootLogger();
	
	@Autowired
	private SuspiciousActivities act;
	
	@RequestMapping(method = RequestMethod.GET)
	public String notFound(
			final HttpServletRequest request,
			final HttpSession session,
			@RequestHeader(value = "referer", required = false) final String referer,
			@RequestHeader(value = "user-agent", required = false) final String agent) {
		
		// TODO: sanitize all user's values (#60)
		final String page = (String)request.getAttribute("javax.servlet.error.request_uri");
		final String ip   = request.getRemoteAddr();
		
		Long uid = null;
		final User user = (User)session.getAttribute("user");
		if (user != null) {
			uid = user.getUid();
		}
		
		try {
			act.logEvent("PageNotFound", page, uid, ip, referer, agent);
		} catch (final SQLException ex) {
			// intentionally ignored:
			// database error should not break showing of 404 page
			log.warn("Cannot log 404 error", ex);
		}
		
		return "error/404";
	}
	
}

