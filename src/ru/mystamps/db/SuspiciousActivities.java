package ru.mystamps.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

public class SuspiciousActivities {
	private Logger log = null;
	private DataSource ds = null;
	
	private static final String logEventQuery =
			"INSERT INTO `suspicious_activities` " +
			"SELECT id, NOW(), ?, NULL, ?, ?, ? " +
			"FROM `suspicious_activities_types` " +
			"WHERE name = ?";
	
	public SuspiciousActivities() {
		log = Logger.getRootLogger();
		
		Context env = null;
		try {
			env = (Context)new InitialContext().lookup("java:comp/env");
			ds = (DataSource)env.lookup("jdbc/mystamps");
			
		} catch (NamingException ex) {
			log.error(ex);
		}
	}
	
	/**
	 * Add record about suspicious activity.
	 *
	 * @todo check length of arguments and warn() if its greater than field size
	 * @todo log user login
	 *
	 * @param String type
	 * @param String page
	 * @param String ip
	 * @param String refererPage
	 * @param String userAgent
	 **/
	public void logEvent(String type, String page, String ip, String refererPage, String userAgent) {
		
		try {
			Connection conn = ds.getConnection();
			
			try {
				PreparedStatement stat = conn.prepareStatement(logEventQuery);
				stat.setString(1, page);
				stat.setString(2, ip);
				stat.setString(3, refererPage);
				stat.setString(4, userAgent);
				stat.setString(5, type);
				stat.executeUpdate();
				
			} finally {
				conn.close();
			}
			
		} catch (SQLException ex) {
			log.error("logEvent(): " + ex);
		}
	}
	
}
