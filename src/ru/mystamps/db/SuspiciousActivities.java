package ru.mystamps.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class SuspiciousActivities {
	private final DataSource ds;
	
	private static final String logEventQuery =
			"INSERT INTO `suspicious_activities` " +
			"SELECT id, NOW(), ?, NULL, ?, ?, ? " +
			"FROM `suspicious_activities_types` " +
			"WHERE name = ?";
	
	/**
	 * @throws NamingException
	 **/
	public SuspiciousActivities()
		throws NamingException {
		
		final Context env =
			(Context)new InitialContext().lookup("java:comp/env");
		ds = (DataSource)env.lookup("jdbc/mystamps");
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
	 * @throws SQLException
	 **/
	public void logEvent(final String type,
			final String page,
			final String ip,
			final String refererPage,
			final String userAgent)
		throws SQLException {
		
		final Connection conn = ds.getConnection();
		
		try {
			final PreparedStatement stat =
				conn.prepareStatement(logEventQuery);
			stat.setString(1, page);
			stat.setString(2, ip);
			stat.setString(3, refererPage);
			stat.setString(4, userAgent);
			stat.setString(5, type);
			stat.executeUpdate();
			
		} finally {
			conn.close();
		}
	}
	
}
