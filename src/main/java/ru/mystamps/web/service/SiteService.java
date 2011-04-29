package ru.mystamps.web.service;

import java.util.Date;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.mystamps.web.dao.SuspiciousActivityDao;
import ru.mystamps.web.dao.SuspiciousActivityTypeDao;
import ru.mystamps.web.dao.UserDao;
import ru.mystamps.web.entity.User;
import ru.mystamps.web.entity.SuspiciousActivity;
import ru.mystamps.web.entity.SuspiciousActivityType;

@Service
public class SiteService {
	private final Logger log = Logger.getRootLogger();
	
	@Autowired
	private UserDao users;
	
	@Autowired
	private SuspiciousActivityDao suspiciousActivities;

	@Autowired
	private SuspiciousActivityTypeDao suspiciousActivityTypes;
	
	@Transactional
	public void logAboutAbsentPage(
			final String page,
			final User user,
			final String ip,
			final String referer,
			final String agent) {
		
		log(getAbsentPageType(), page, user, ip, referer, agent);
	}
	
	@Transactional
	public void logAboutFailedAuthentication(
			final String page,
			final User user,
			final String ip,
			final String referer,
			final String agent) {
		
		log(getFailedAuthenticationType(), page, user, ip, referer, agent);
	}
	
	private void log(
			final SuspiciousActivityType type,
			final String page,
			final User user,
			final String ip,
			final String referer,
			final String agent) {
		
		if (type == null) {
			throw new IllegalArgumentException("Type of suspicious activity was not set");
		}
		
		final SuspiciousActivity activity = new SuspiciousActivity();
		activity.setType(type);
		activity.setOccuredAt(new Date());
		activity.setPage(page);
		
		User currentUser = user;
		if (currentUser != null) {
			currentUser = users.findById(user.getId());
			if (currentUser == null) {
				log.warn("Cannot find user with id " + user.getId());
			}
		}
		activity.setUser(currentUser);
		
		activity.setIp(ip == null ? "" : ip);
		activity.setRefererPage(referer == null ? "" : referer);
		activity.setUserAgent(agent == null ? "" : agent);
		
		suspiciousActivities.add(activity);
	}
	
	private SuspiciousActivityType getAbsentPageType() {
		// see mystamps.sql
		return suspiciousActivityTypes.findByName("PageNotFound");
	}
	
	private SuspiciousActivityType getFailedAuthenticationType() {
		// see mystamps.sql
		return suspiciousActivityTypes.findByName("AuthenticationFailed");
	}
	
}
