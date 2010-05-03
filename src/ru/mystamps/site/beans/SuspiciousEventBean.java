package ru.mystamps.site.beans;

import java.sql.SQLException;

import javax.naming.NamingException;

import org.apache.log4j.Logger;

import ru.mystamps.db.SuspiciousActivities;

public class SuspiciousEventBean {
	
	private String type = null;
	private String page = null;
	private String ip = null;
	private String refererPage = null;
	private String userAgent = null;
	
	private Logger log = null;
	private SuspiciousActivities act = null;
	
	/**
	 * @throws NamingException
	 **/
	public SuspiciousEventBean()
		throws NamingException {
		log = Logger.getRootLogger();
		act = new SuspiciousActivities();
	}
	
	/**
	 * Log event about suspicious activity to database.
	 * @throws SQLException
	 **/
	public String getLogResult()
		throws SQLException {
		act.logEvent(getType(), getPage(), getIp(), getRefererPage(), getUserAgent());
		return "";
	}
	
	public void setType(String type) {
		log.debug("type: " + type);
		this.type = type;
	}
	
	public String getType() {
		return this.type;
	}
	
	public void setPage(String page) {
		log.debug("page: " + page);
		this.page = page;
	}
	
	public String getPage() {
		return this.page;
	}
	
	public void setIp(String ip) {
		log.debug("ip: " + ip);
		this.ip = ip;
	}
	
	public String getIp() {
		return this.ip;
	}
	
	public void setRefererPage(String refererPage) {
		log.debug("referer: " + refererPage);
		this.refererPage = refererPage;
	}
	
	public String getRefererPage() {
		return this.refererPage;
	}
	
	public void setUserAgent(String userAgent) {
		log.debug("user-agent: " + userAgent);
		this.userAgent = userAgent;
	}
	
	public String getUserAgent() {
		return this.userAgent;
	}
	
}

