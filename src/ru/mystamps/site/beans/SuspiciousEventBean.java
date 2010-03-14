package ru.mystamps.site.beans;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

public class SuspiciousEventBean {
	
	private String type = null;
	private String page = null;
	private String ip = null;
	private String refererPage = null;
	private String userAgent = null;
	
	private Logger log = null;
	private DataSource ds = null;
	
	public SuspiciousEventBean() {
		log = Logger.getRootLogger();
		
		Context env = null;
		try {
			env = (Context)new InitialContext().lookup("java:comp/env");
			ds = (DataSource)env.lookup("jdbc/mystamps");
			
		} catch (NamingException ex) {
			log.error(ex);
		}
	}
	
	public String getLogResult() {
		logEvent();
		return "";
	}
	
	/**
	 * Log event about suspicious activity to database.
	 **/
	private void logEvent() {
		
		/// @todo use model to log event
		/// @todo check length of arguments and warn() if its greater than field size
		/// @todo log user login
		final String logQuery =
			"INSERT INTO `suspicious_activities` " +
			"SELECT id, NOW(), ?, NULL, ?, ?, ? " +
			"FROM `suspicious_activities_types` " +
			"WHERE name = ?";
		
		try {
			Connection conn = ds.getConnection();
			
			try {
				PreparedStatement stat = conn.prepareStatement(logQuery);
				stat.setString(1, getPage());
				stat.setString(2, getIp());
				stat.setString(3, getRefererPage());
				stat.setString(4, getUserAgent());
				stat.setString(5, getType());
				stat.executeUpdate();
				
			} finally {
				conn.close();
			}
			
		} catch (SQLException ex) {
			log.error("logEvent(): " + ex);
		}
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

